# Etherscan Analytics Project

The **Etherscan Analytics Project** is a real-time analytics platform designed to fetch, process, and analyze Ethereum blockchain transaction data. It leverages **Apache Flink** for streaming data processing, **Apache Kafka** for messaging, and **PostgreSQL** for persistent data storage.

## Features

- **Etherscan Integration**: Fetches Ethereum blockchain transactions in real-time.
- **Real-Time Analytics**: Processes transactions to identify the top active wallets.
    - Top addresses by the number of transactions.
    - Top addresses by total value received.
- **Data Storage**: Stores analyzed data in PostgreSQL for further exploration.
- **Scalable Design**: Built with Flink and Kafka for distributed processing.
- **Dockerized Setup**: Simplified deployment using Docker Compose.

---

## Project Structure

### Key Components
- **Main.java**: Entry point for the application.
- **config/**: Configuration files for Kafka and PostgreSQL.
- **processor/**: Logic for processing and filtering Ethereum transactions.
- **analytics/**: Classes for implementing analytics such as top addresses.
- **dao/**: Data Access Objects for database interaction.
- **model/**: Data models representing transactions and analytics results.
- **service/**: Service layer for data manipulation.

---

## Prerequisites

- **Java 11 or higher**
- **Maven** for dependency management
- **Apache Flink** installation 
- **Etherscan API Key**

---

## Getting Started

### 1. Clone the Repository
    ```bash
    git clone https://github.com/denizparlak1/etherscan-analytics.git
    cd etherscan-analytics

### 2. Set Up application.properties
    ```bash
    # Kafka Broker
    bootstrap.servers=localhost:9092

    # Kafka Topics
    kafka.input.topic=incoming-transactions
    kafka.output.topic=parsed-transactions

    # Kafka Consumer Config
    enable.auto.commit=false
    auto.offset.reset=earliest
    group.id=etherscan-analytics-group


    # PostgreSQL Configuration
    postgres.url=jdbc:postgresql://localhost:5432/analytics
    postgres.user=postgres
    postgres.password=postgres

    # Logging Level
    logging.level.root=INFO
    logging.level.com.example=DEBUG

### 3. Build and Run
    mvn clean package
    java -jar target/etherscan-analytics.jar

Core Workflows
1. Fetching Transactions
   Transactions are fetched from Etherscan API and pushed to Kafka's input topic.
2. Processing Logic

   Filter and Map:Raw transaction data is filtered and transformed.
   Analytics: Flink jobs compute:
   - Top 3 addresses by transaction count.
   - Top 3 addresses by total value received.
   - Results are stored in PostgreSQL.
3. Real-Time Data Flow

   Kafka acts as the intermediary for processing and message transfer.
   Flink streams data, performs analytics, and updates the database.

### Running Analytics
- Top Addresses by Transaction Count:
    - Processed using FinalTopNProcessFunction.
    - Data stored in the top_addresses table.
- Top Addresses by Total Value:
   - Processed using TopReceivingAddressesFunction.
   - Data stored in the top_receiving_addresses table.

## Acknowledgments

This project is the result of my dedication and hard work. Special thanks to the open-source community for the tools and resources that made this project possible. I am grateful for the opportunity to learn and grow through this development process.

---

## Good Luck and Happy Coding!
Feel free to reach out with any questions or suggestions. Enjoy exploring real-time blockchain analytics!
