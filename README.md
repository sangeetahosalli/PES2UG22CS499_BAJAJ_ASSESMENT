🧩 Bajaj Finserv Health — Webhook Solver (Qualifier 1, Java)
📘 Overview

This Spring Boot project implements the Bajaj Finserv Health Java Qualifier 1 problem.
It automates the entire flow — from webhook generation to final SQL query submission — without any manual trigger.

⚙️ Features

Executes automatically on application startup

Sends a POST request to generate a webhook

Receives accessToken (JWT) and webhook URL

Submits the final SQL query (Question 1) using the token

No controller or REST endpoint needed for triggering

Uses Spring Boot + RestTemplate

🧮 SQL Query (Question 1)
SELECT p.AMOUNT AS SALARY,
       CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME,
       TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE,
       d.DEPARTMENT_NAME
FROM PAYMENTS p
JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID
JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID
WHERE DAY(p.PAYMENT_TIME) <> 1
  AND p.AMOUNT = (
    SELECT MAX(AMOUNT)
    FROM PAYMENTS
    WHERE DAY(PAYMENT_TIME) <> 1
  );

🧱 Tech Stack

Java 17+

Spring Boot 3.1.6

Maven

RestTemplate (HTTP client)

🚀 How It Works

On startup, the app sends:

POST https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA


Body:

{
  "name": "John Doe",
  "regNo": "REG12347",
  "email": "john@example.com"
}


Receives:

webhook (submission URL)

accessToken (JWT)

Submits SQL using:

POST https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA


Headers:

Authorization: <accessToken>
Content-Type: application/json


Body:

{
  "finalQuery": "YOUR_SQL_QUERY_HERE"
}


Logs success or error to the console and exits.

🖥️ Running the Application
Prerequisites

Java 17+

Maven 3.8 or higher

Steps
# Go to the project folder
cd bfhl-webhook-solver

# Build the project
mvn clean package

# Run the JAR file
java -jar target/bfhl-webhook-solver-1.0.0.jar


The program will automatically perform all actions and exit once complete.

🧰 Project Structure
bfhl-webhook-solver/
│
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/example/bfhlwebhook/
│   │   │   └── Application.java
│   │   └── resources/application.properties
└── target/
    └── bfhl-webhook-solver-1.0.0.jar

⚙️ Configuration

Default port: 8080
To use a different port, edit:

src/main/resources/application.properties


and add:

server.port=8081
