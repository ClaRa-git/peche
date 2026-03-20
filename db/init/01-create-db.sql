-- création de l'utilisateur pour l'application
CREATE ROLE cfa WITH LOGIN PASSWORD 'cfa2025';

-- création de la base
CREATE DATABASE cfadb OWNER cfa;

-- se connecter à la base cfadb avant la suite
\c cfadb

-- création du schéma (optionnel car déjà présent par défaut)
CREATE SCHEMA IF NOT EXISTS public;

-- droits sur la base
GRANT ALL PRIVILEGES ON DATABASE cfadb TO cfa;

-- droits sur le schéma
GRANT ALL PRIVILEGES ON SCHEMA public TO cfa;

-- droits sur les tables existantes
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO cfa;

-- droits sur les séquences (important pour les SERIAL / IDENTITY)
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO cfa;

-- droits par défaut pour les futures tables
ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT ALL ON TABLES TO cfa;

-- droits par défaut pour les futures séquences
ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT ALL ON SEQUENCES TO cfa;