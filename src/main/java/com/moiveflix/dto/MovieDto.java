package com.moiveflix.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieDto {


    private Integer movieId;

    @NotBlank(message = "Please provide movie's title!")
    private String title;

    @NotBlank(message = "Please provide movie's director!")
    private String director;


    private Set<String> moiveCast;

    private Integer releaseYear;

    @NotBlank(message = "Please provide movie's poster!")
    private String poster;

    @NotBlank(message = "Please provide poster's url")
    private String posterUrl;
}
