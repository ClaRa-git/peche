-- ============================================================
-- Peche3000 - Schéma de base de données
-- ============================================================

\c cfadb

-- Extension pour les UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ------------------------------------------------------------
-- Catégories de produits
-- ------------------------------------------------------------
CREATE TABLE category (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version     BIGINT NOT NULL DEFAULT 0,
    name        VARCHAR(100) NOT NULL,
    slug        VARCHAR(100) NOT NULL UNIQUE
);

-- ------------------------------------------------------------
-- Utilisateurs (clients + admins)
-- ------------------------------------------------------------
CREATE TABLE app_user (
      id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
      version         BIGINT NOT NULL DEFAULT 0,
      email           VARCHAR(255) NOT NULL UNIQUE,
      password_hash   VARCHAR(255) NOT NULL,
      first_name      VARCHAR(100) NOT NULL,
      last_name       VARCHAR(100) NOT NULL,
      phone           VARCHAR(20),
      address         TEXT,
      enabled         BOOLEAN NOT NULL DEFAULT TRUE,
      account_expired  BOOLEAN NOT NULL DEFAULT FALSE,
      account_locked   BOOLEAN NOT NULL DEFAULT FALSE,
      password_expired BOOLEAN NOT NULL DEFAULT FALSE,
      role            VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER'
          CHECK (role IN ('ROLE_USER', 'ROLE_ADMIN')),
      created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ------------------------------------------------------------
-- Produits
-- ------------------------------------------------------------
CREATE TABLE product (
     id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
     version         BIGINT NOT NULL DEFAULT 0,
     category_id     UUID NOT NULL REFERENCES category(id),
     name            VARCHAR(255) NOT NULL,
     description     TEXT,
     price           DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
     stock_quantity  INTEGER NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
     image_url       VARCHAR(500),
     is_active       BOOLEAN NOT NULL DEFAULT TRUE,
     created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ------------------------------------------------------------
-- Commandes
-- ------------------------------------------------------------
CREATE TABLE order_table (
     id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
     version             BIGINT NOT NULL DEFAULT 0,
     user_id             UUID NOT NULL REFERENCES app_user(id),
     status              VARCHAR(20) NOT NULL DEFAULT 'pending'
         CHECK (status IN ('pending', 'paid', 'shipped', 'delivered', 'cancelled')),
     total_amount        DECIMAL(10, 2) NOT NULL CHECK (total_amount >= 0),
     stripe_payment_id   VARCHAR(255),
     shipping_address    TEXT NOT NULL,
     ordered_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ------------------------------------------------------------
-- Lignes de commande
-- ------------------------------------------------------------
CREATE TABLE order_item (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version         BIGINT NOT NULL DEFAULT 0,
    order_id        UUID NOT NULL REFERENCES order_table(id) ON DELETE CASCADE,
    product_id      UUID NOT NULL REFERENCES product(id),
    quantity        INTEGER NOT NULL CHECK (quantity > 0),
    unit_price      DECIMAL(10, 2) NOT NULL CHECK (unit_price >= 0)
);

-- ------------------------------------------------------------
-- Demandes de permis de pêche
-- ------------------------------------------------------------
CREATE TABLE fishing_permit (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version BIGINT      NOT NULL DEFAULT 0,
    user_id             UUID NOT NULL REFERENCES app_user(id),
    permit_type         VARCHAR(50) NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'pending'
        CHECK (status IN ('pending', 'approved', 'rejected')),
    requested_date      DATE NOT NULL DEFAULT CURRENT_DATE,
    valid_from          DATE,
    valid_until         DATE,
    rejection_reason    TEXT
);

-- ------------------------------------------------------------
-- Concours de pêche
-- ------------------------------------------------------------
CREATE TABLE contest (
     id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
     version             BIGINT NOT NULL DEFAULT 0,
     name                VARCHAR(255) NOT NULL,
     description         TEXT,
     location            VARCHAR(255) NOT NULL,
     contest_date        DATE NOT NULL,
     max_participants    INTEGER CHECK (max_participants > 0),
     is_open             BOOLEAN NOT NULL DEFAULT TRUE
);

-- ------------------------------------------------------------
-- Inscriptions aux concours
-- ------------------------------------------------------------
CREATE TABLE contest_registration (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version         BIGINT NOT NULL DEFAULT 0,
    user_id         UUID NOT NULL REFERENCES app_user(id),
    contest_id      UUID NOT NULL REFERENCES contest(id),
    registered_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    status          VARCHAR(20) NOT NULL DEFAULT 'confirmed'
      CHECK (status IN ('confirmed', 'cancelled')),
    UNIQUE (user_id, contest_id)
);

-- ------------------------------------------------------------
-- Rôles (pour gestion des permissions)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS role (
    id        UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version   BIGINT NOT NULL DEFAULT 0,
    authority VARCHAR(255) NOT NULL UNIQUE
);

-- ------------------------------------------------------------
-- Table de liaison entre utilisateurs et rôles (many-to-many)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS app_user_role (
    app_user_id UUID NOT NULL REFERENCES app_user(id),
    role_id     UUID NOT NULL REFERENCES role(id),
    PRIMARY KEY (app_user_id, role_id)
);


-- ------------------------------------------------------------
-- Index utiles pour les performances
-- ------------------------------------------------------------
CREATE INDEX idx_product_category    ON product(category_id);
CREATE INDEX idx_product_is_active   ON product(is_active);
CREATE INDEX idx_order_user          ON order_table(user_id);
CREATE INDEX idx_order_status        ON order_table(status);
CREATE INDEX idx_order_item_order    ON order_item(order_id);
CREATE INDEX idx_permit_user         ON fishing_permit(user_id);
CREATE INDEX idx_permit_status       ON fishing_permit(status);
CREATE INDEX idx_contest_reg_user    ON contest_registration(user_id);
CREATE INDEX idx_contest_reg_contest ON contest_registration(contest_id);