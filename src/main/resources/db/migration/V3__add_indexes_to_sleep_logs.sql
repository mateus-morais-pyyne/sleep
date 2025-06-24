-- V3__add_indexes_to_sleep_logs.sql

-- Add an index on user_id to speed up lookups by user
CREATE INDEX IF NOT EXISTS idx_sleep_logs_user_id ON tb_sleep_logs(user_id);

-- Add an index on date_of_sleep to optimize date range queries
CREATE INDEX IF NOT EXISTS idx_sleep_logs_date_of_sleep ON tb_sleep_logs(date_of_sleep);

-- Add a combined index for user_id and date_of_sleep
CREATE INDEX IF NOT EXISTS idx_sleep_logs_user_id_date_of_sleep
  ON tb_sleep_logs(user_id, date_of_sleep);