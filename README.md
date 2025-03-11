# Batch Processing with Spring Batch and MongoDB

This project demonstrates a batch processing application using Spring Batch and MongoDB. The application reads items, processes them, and writes the results.

## Technologies Used

- Java
- Spring Boot
- Spring Batch
- MongoDB
- Maven
- Docker

## Prerequisites

- Java 11 or higher
- Maven
- Docker

## Setup

1. **Clone the repository:**
   ```sh
   git clone <repository-url>
   cd <repository-directory>
    ```
2. **Build the application:**   
   ```sh
    mvn clean install
    ```
3. **Run the application:**   
   ```sh
    java -jar target/spring-batch-mongodb-0.0.1-SNAPSHOT.jar
    ```
4. **Access the application:**
    - The application run in background;
    - Database is populated with sample data local access;
    - Access the database using MongoDB Compass or any other MongoDB client.
    - The application will read the data from the database, process it, and write the results back to the database.
    - The application will print the results to the console. 


   
    
