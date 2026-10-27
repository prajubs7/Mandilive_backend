CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(100)  NOT NULL,
    email         VARCHAR(150)  NOT NULL,
    phone         VARCHAR(20),
    password_hash VARCHAR(255)  NOT NULL,
    role          VARCHAR(20)   NOT NULL,
    created_at    TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT ck_users_role CHECK (role IN ('FARMER', 'BUYER', 'ADMIN'))
);

CREATE TABLE farmer_profile (
    id       BIGSERIAL PRIMARY KEY,
    user_id  BIGINT       NOT NULL,
    district VARCHAR(100) NOT NULL,
    state    VARCHAR(100) NOT NULL,
    CONSTRAINT uk_farmer_profile_user UNIQUE (user_id),
    CONSTRAINT fk_farmer_profile_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE RESTRICT
);

CREATE INDEX idx_farmer_profile_state_district ON farmer_profile (state, district);