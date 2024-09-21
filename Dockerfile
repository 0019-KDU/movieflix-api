FROM openjdk:17
WORKDIR /app
COPY ./target/movieApi.jar /app
EXPOSE 8080
CMD ["java","-jar","movieApi.jar"]