-- creation de l'utilisateur pour l'application
CREATE ROLE cfa WITH LOGIN PASSWORD 'cfa2025';

-- creation de la base
CREATE DATABASE cfadb OWNER cfa;


-- droit sur la database
GRANT ALL PRIVILEGES ON DATABASE cfadb TO cfa;

-- creation du schema
CREATE SCHEMA IF NOT EXISTS public;

-- droit sur le schema
GRANT ALL PRIVILEGES ON SCHEMA public TO cfa;