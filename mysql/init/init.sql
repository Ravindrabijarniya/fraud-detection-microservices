-- Create databases
CREATE DATABASE IF NOT EXISTS auth_db;
CREATE DATABASE IF NOT EXISTS transaction_db;
CREATE DATABASE IF NOT EXISTS fraud_db;
CREATE DATABASE IF NOT EXISTS notification_db;

-- Create users
CREATE USER IF NOT EXISTS 'auth_user'@'%' IDENTIFIED BY 'auth123';
CREATE USER IF NOT EXISTS 'transaction_user'@'%' IDENTIFIED BY 'transaction123';
CREATE USER IF NOT EXISTS 'fraud_engine'@'%' IDENTIFIED BY 'fraud_engine_pwd';
CREATE USER IF NOT EXISTS 'notification_user'@'%' IDENTIFIED BY 'notification123';

-- Grant permissions
GRANT ALL PRIVILEGES ON auth_db.* TO 'auth_user'@'%';
GRANT ALL PRIVILEGES ON transaction_db.* TO 'transaction_user'@'%';
GRANT ALL PRIVILEGES ON fraud_db.* TO 'fraud_engine'@'%';
GRANT ALL PRIVILEGES ON notification_db.* TO 'notification_user'@'%';

FLUSH PRIVILEGES;
