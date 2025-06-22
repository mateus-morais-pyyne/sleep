-- V1__create_tables.sql

-- Enable UUID extension if not already enabled (PostgreSQL specific)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create tb_users table
CREATE TABLE tb_users (
                          id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          name VARCHAR(255) NOT NULL,
                          age INT NOT NULL,
                          email VARCHAR(255) NOT NULL UNIQUE
);

-- Create sleep_logs table
CREATE TABLE tb_sleep_logs (
                               id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

                               date_of_sleep DATE NOT NULL,
                               bed_time TIMESTAMP NOT NULL,
                               wake_up_time TIMESTAMP NOT NULL,
                               total_time_in_bed INT NOT NULL,

                               morning_feeling VARCHAR(10) NOT NULL CHECK (morning_feeling IN ('BAD', 'OK', 'GOOD')),

                               user_id UUID NOT NULL,
                               CONSTRAINT fk_user_sleep_log FOREIGN KEY (user_id) REFERENCES tb_users(id) ON DELETE CASCADE
);
