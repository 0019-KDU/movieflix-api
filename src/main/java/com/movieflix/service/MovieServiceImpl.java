package com.movieflix.service;

import com.movieflix.dto.MovieDto;
import com.movieflix.entities.Movie;
import com.movieflix.exceptions.FileExistsException;
import com.movieflix.exceptions.MovieNotFoundException;
import com.movieflix.repositories.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {
    private static final Logger logger = LoggerFactory.getLogger(MovieServiceImpl.class);
    private final MovieRepository movieRepository;

    private final FileService fileService;

    @Value("$project.poster")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    public MovieServiceImpl(MovieRepository movieRepository, FileService fileService)throws IOException {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
    }

    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {
        //1.upload the file
        if (Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
            throw new FileExistsException("File already exists! Please enter another file name!");
        }
        String uploadedFileName=fileService.uploadFile(path,file);

        //2.set the value of field 'poster' as filename
        movieDto.setPoster(uploadedFileName);

        //3.map dto to Movie object
        Movie movie=new Movie(
                null,
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMoiveCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        );

        //4.save the movie object -> saved moive object
        Movie savedMovie=movieRepository.save(movie);

        //5.generate the postUrl
        String posterUrl=baseUrl+"/file/"+uploadedFileName;

        //6.map movie object to DTO object and return it
        MovieDto response=new MovieDto(
                savedMovie.getMovieId(),
                savedMovie.getTitle(),
                savedMovie.getDirector(),
                savedMovie.getStudio(),
                savedMovie.getMoiveCast(),
                savedMovie.getReleaseYear(),
                savedMovie.getPoster(),
                posterUrl
        );
        return response;
    }

    @Override
    public MovieDto getMovie(Integer movieId) {
        //1.check the data in DB and if exists,fetch the data of given ID
        Movie movie=movieRepository.findById(movieId).orElseThrow(()->new MovieNotFoundException("Movie not found with id:"+movieId));

        //2.generate posterUrl
        String posterUrl=baseUrl+"/file/"+movie.getPoster();

        //3.map to MovieDto object and return it
        MovieDto response=new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMoiveCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );
        return response;
    }

    @Override
    public List<MovieDto> getAllMovies() {
        //1.fetch all data from DB
        List<Movie> movies=movieRepository.findAll();

        List<MovieDto> movieDtos=new ArrayList<>();

        //2.iterate through the list,generate posterUrl for each movie obj and map to MovieDto obj
        for (Movie movie : movies) {
            String posterUrl=baseUrl+"/file/"+movie.getPoster();
            MovieDto movieDto=new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMoiveCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(movieDto);
        }
        return movieDtos;
    }

    @Override
    public MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException {
        //1.check if movie object exists with given movieId
        Movie mv=movieRepository.findById(movieId).orElseThrow(()->new MovieNotFoundException("Movie not found with id: "+movieId));

        //2.if file is null ,do nothing
        //if file is not null ,then delete existing associated with the record
        //and upload the new file
        String fileName=mv.getPoster();
        if (file != null) {
            Files.deleteIfExists(Paths.get(path+ File.separator + fileName));
            fileName=fileService.uploadFile(path,file);
        }

        //3.set movieDto's poster value,according to step 2
        movieDto.setPoster(fileName);
        //4.map it to Movie object
        Movie movie=new Movie(
                mv.getMovieId(),
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMoiveCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        );
        //5.save the movie object ->return saved movie object
        Movie updatedMovie=movieRepository.save(movie);

        //6.generate posterUrl for it
        String posterUrl=baseUrl+"/file/"+fileName;
        //7.map to MovieDto and return it
        MovieDto response=new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMoiveCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );
        return response;
    }

    @Transactional
    @Override
    public String deleteMovie(Integer movieId) throws IOException {
        // 1. Check if the movie exists in the DB
        Movie mv = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + movieId));

        // 2. Try to delete the file associated with this movie
        try {
            Files.deleteIfExists(Paths.get(path, mv.getPoster()));
            logger.info("Deleted poster file for movie: " + movieId);
        } catch (IOException e) {
            logger.error("Error deleting poster file for movie with id: " + movieId, e);
        }

        // 3. Delete the movie object from the DB
        movieRepository.delete(mv);
        logger.info("Deleted movie from DB with id: " + movieId);

        return "Movie deleted with id: " + movieId;
    }
}







