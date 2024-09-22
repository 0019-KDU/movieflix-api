FROM openjdk:17
ARG DB_USERNAME=root
ARG DB_PASSWORD=rnTlb2#t
ARG DB_URL=jdbc:mysql://movieapi.cjwgyc0ayecm.eu-north-1.rds.amazonaws.com:3306/movies_db

ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV DB_URL=${DB_URL}

COPY target/movieApi.jar movieApi.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/movieApi.jar"]
