-- ============================================================
-- Peche3000 - Données de test (seed)
-- ============================================================

\c cfadb

-- ------------------------------------------------------------
-- Catégories
-- ------------------------------------------------------------
INSERT INTO category (id, version, name, slug) VALUES
    ('11111111-0000-0000-0000-000000000001', 0, 'Cannes à pêche',    'cannes-a-peche'),
    ('11111111-0000-0000-0000-000000000002', 0, 'Moulinets',         'moulinets'),
    ('11111111-0000-0000-0000-000000000003', 0, 'Appâts & leurres',  'appats-leurres'),
    ('11111111-0000-0000-0000-000000000004', 0, 'Vêtements',         'vetements'),
    ('11111111-0000-0000-0000-000000000005', 0, 'Accessoires',       'accessoires');

-- ------------------------------------------------------------
-- Utilisateurs
-- Mot de passe pour tous : "password123"
-- Hash BCrypt généré avec strength=10
-- ------------------------------------------------------------
INSERT INTO app_user (id, version, email, password_hash, first_name, last_name, phone, address, enabled, account_expired, account_locked, password_expired ,role) VALUES
     (
         '22222222-0000-0000-0000-000000000001',
         '0',
         'admin@peche3000.fr',
         '$2a$10$YDLgoWqSqAwp0s49oimqtOmfHxpll/egis2tk4FavKcLQcF36mGHO',
         'Admin',
         'Système',
         '0600000001',
         '1 rue de la Pêche, 75001 Paris',
         'true',
         'false',
         'false',
         'false',
         'ROLE_ADMIN'
     ),
     (
         '22222222-0000-0000-0000-000000000002',
         '0',
         'jean.dupont@email.fr',
         '$2a$10$YDLgoWqSqAwp0s49oimqtOmfHxpll/egis2tk4FavKcLQcF36mGHO',
         'Jean',
         'Dupont',
         '0611223344',
         '12 allée des Truites, 69001 Lyon',
         'true',
         'false',
         'false',
         'false',
         'ROLE_USER'
     ),
     (
         '22222222-0000-0000-0000-000000000003',
         '0',
         'marie.martin@email.fr',
         '$2a$10$YDLgoWqSqAwp0s49oimqtOmfHxpll/egis2tk4FavKcLQcF36mGHO',
         'Marie',
         'Martin',
         '0622334455',
         '5 impasse du Brochet, 33000 Bordeaux',
         'true',
         'false',
         'false',
         'false',
         'ROLE_USER'
     );

-- ------------------------------------------------------------
-- Produits
-- ------------------------------------------------------------
INSERT INTO product (id, version, category_id, name, description, price, stock_quantity, is_active) VALUES
    (
    '33333333-0000-0000-0000-000000000001',
    '0',
    '11111111-0000-0000-0000-000000000001',
    'Canne télescopique Pro 3m',
    'Canne télescopique en carbone haute résistance, idéale pour la pêche en rivière.',
    49.99, 25, TRUE
    ),
    (
    '33333333-0000-0000-0000-000000000002',
    '0',
    '11111111-0000-0000-0000-000000000001',
    'Canne spinning Expert 2.4m',
    'Canne spinning légère avec action rapide, parfaite pour les carnassiers.',
    89.90, 15, TRUE
    ),
    (
    '33333333-0000-0000-0000-000000000003',
    '0',
    '11111111-0000-0000-0000-000000000002',
    'Moulinet Shimano FX 2500',
    'Moulinet frontal robuste avec frein avant réglable, 5 roulements à billes.',
    59.99, 20, TRUE
    ),
    (
    '33333333-0000-0000-0000-000000000004',
    '0',
    '11111111-0000-0000-0000-000000000003',
    'Boîte de leurres souples (20 pcs)',
    'Assortiment de 20 leurres souples colorés, toutes tailles.',
    14.50, 50, TRUE
    ),
    (
    '33333333-0000-0000-0000-000000000005',
    '0',
    '11111111-0000-0000-0000-000000000004',
    'Veste de pêche imperméable',
    'Veste technique imperméable avec nombreuses poches et capuche amovible.',
    129.00, 10, TRUE
    ),
    (
    '33333333-0000-0000-0000-000000000006',
    '0',
    '11111111-0000-0000-0000-000000000005',
    'Boîte à leurres 3 compartiments',
    'Boîte de rangement étanche avec 3 compartiments ajustables.',
    9.99, 40, TRUE
    );

-- ------------------------------------------------------------
-- Commandes + lignes de commande
-- ------------------------------------------------------------
INSERT INTO order_table (id, version, user_id, status, total_amount, stripe_payment_id, shipping_address) VALUES
     (
         '44444444-0000-0000-0000-000000000001',
         '0',
         '22222222-0000-0000-0000-000000000002',
         'delivered',
         149.88,
         'pi_test_stripe_001',
         '12 allée des Truites, 69001 Lyon'
     ),
     (
         '44444444-0000-0000-0000-000000000002',
         '0',
         '22222222-0000-0000-0000-000000000003',
         'paid',
         74.49,
         'pi_test_stripe_002',
         '5 impasse du Brochet, 33000 Bordeaux'
     );

INSERT INTO order_item (order_id, version, product_id, quantity, unit_price) VALUES
    ('44444444-0000-0000-0000-000000000001', '0', '33333333-0000-0000-0000-000000000001', 2, 49.99),
    ('44444444-0000-0000-0000-000000000001', '0',  '33333333-0000-0000-0000-000000000006', 1,  9.99),
    ('44444444-0000-0000-0000-000000000001', '0',  '33333333-0000-0000-0000-000000000004', 2, 14.50),
    ('44444444-0000-0000-0000-000000000002', '0',  '33333333-0000-0000-0000-000000000003', 1, 59.99),
    ('44444444-0000-0000-0000-000000000002', '0',  '33333333-0000-0000-0000-000000000004', 1, 14.50);

-- ------------------------------------------------------------
-- Demandes de permis
-- ------------------------------------------------------------
INSERT INTO fishing_permit (user_id, version, permit_type, status, requested_date, valid_from, valid_until) VALUES
    (
    '22222222-0000-0000-0000-000000000002',
    '0',
    'Eau douce - annuel',
    'approved',
    CURRENT_DATE - INTERVAL '30 days',
    CURRENT_DATE - INTERVAL '25 days',
    CURRENT_DATE + INTERVAL '340 days'
    ),
    (
    '22222222-0000-0000-0000-000000000003',
    '0',
    'Mer - journée',
    'pending',
    CURRENT_DATE - INTERVAL '2 days',
    NULL,
    NULL
    );

-- ------------------------------------------------------------
-- Concours
-- ------------------------------------------------------------
INSERT INTO contest (id, version, name, description, location, contest_date, max_participants, is_open) VALUES
    (
    '55555555-0000-0000-0000-000000000001',
    '0',
    'Championnat du Lac de Paladru',
    'Concours de pêche à la carpe en équipe de 2. Pesée le soir à 18h.',
    'Lac de Paladru, Isère',
    CURRENT_DATE + INTERVAL '30 days',
    40,
    TRUE
    ),
    (
    '55555555-0000-0000-0000-000000000002',
    '0',
    'Trophée de la Truite',
    'Pêche à la mouche en rivière, catégories junior et senior.',
    'Rivière Ain, Ain',
    CURRENT_DATE + INTERVAL '60 days',
    60,
    TRUE
    ),
    (
    '55555555-0000-0000-0000-000000000003',
    '0',
    'Open Carnassiers Rhône',
    'Concours de pêche aux leurres sur le Rhône. Catch & release obligatoire.',
    'Rhône, Lyon',
    CURRENT_DATE - INTERVAL '10 days',
    80,
    FALSE
    );

-- ------------------------------------------------------------
-- Inscriptions aux concours
-- ------------------------------------------------------------
INSERT INTO contest_registration (user_id, version, contest_id, status) VALUES
    ('22222222-0000-0000-0000-000000000002', '0', '55555555-0000-0000-0000-000000000001', 'confirmed'),
    ('22222222-0000-0000-0000-000000000003',  '0', '55555555-0000-0000-0000-000000000001', 'confirmed'),
    ('22222222-0000-0000-0000-000000000002',  '0', '55555555-0000-0000-0000-000000000002', 'confirmed');


-- ------------------------------------------------------------
-- Rôles (permissions)
-- ------------------------------------------------------------
INSERT INTO role (id, authority) VALUES
    ('aaaaaaaa-0000-0000-0000-000000000001', 'ROLE_USER'),
    ('aaaaaaaa-0000-0000-0000-000000000002', 'ROLE_ADMIN');

-- ------------------------------------------------------------
-- Liaisons entre utilisateurs et rôles
-- ------------------------------------------------------------
INSERT INTO app_user_role (app_user_id, role_id) VALUES
    ('22222222-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000002'),
    ('22222222-0000-0000-0000-000000000002', 'aaaaaaaa-0000-0000-0000-000000000001'),
    ('22222222-0000-0000-0000-000000000003', 'aaaaaaaa-0000-0000-0000-000000000001');
