FROM openjdk:17
WORKDIR /app
COPY ./target/moiveApi.jar /app
EXPOSE 8080
CMD ["java","-jar","movieApi.jar"]