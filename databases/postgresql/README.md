# Postgres Course Setup

## Docker Postgres Setup

Create Docker container with Postgres database:

    docker create --name postgres-subscriptions -e POSTGRES_PASSWORD=Welcome -p 5432:5432 postgres:11.5-alpine

Start container:

    docker start postgres-subscriptions

Stop container:

    docker stop postgres-subscriptions

Connection Info:

    JDBC URL: `jdbc:postgresql://localhost:5432/subscriptions`

    Username: `postgres`

    Password: `Welcome`

Note: This stores the data inside the container - when you delete the container, the data is deleted as well.

Connect to PSQL prompt from docker:
   docker exec -it postgres-subscriptions psql -U postgres

## Application Database Setup

Create the Database:

    psql> create database subscriptions;

Setup the Tables:

    psql -d subscriptions -f create_tables.sql

Install the Data:

    psql -d subscriptions -f insert_data.sql
