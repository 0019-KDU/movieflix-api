# Use the official OpenJDK 17 image from Docker Hub
FROM openjdk:17
ARG DB_USERNAME=root
ARG DB_PASSWORD=rnTlb2#t
ARG DB_URL=jdbc:mysql://movieapi.cjwgyc0ayecm.eu-north-1.rds.amazonaws.com:3306/movies_db

ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV DB_URL=${DB_URL}
# Set working directory inside the container
WORKDIR /app
# Copy the compiled Java application JAR file into the container
COPY ./target/course-service.jar /app
# Expose the port the Spring Boot application will run on
EXPOSE 8080
# Command to run the application
CMD ["java", "-jar", "course-service.jar"]
