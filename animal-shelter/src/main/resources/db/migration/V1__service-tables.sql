CREATE TABLE IF NOT EXISTS webapp.species
(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS webapp.user
(
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    birth_date DATE,
    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(255) UNIQUE,
    vet_code VARCHAR(255) UNIQUE,
    profile_picture VARCHAR(255),
    role VARCHAR(255),
    email_verified BOOLEAN NOT NULL,
    email_verification_token VARCHAR(255),
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS webapp.pet
(
    id BIGSERIAL PRIMARY KEY,
    species_id BIGINT REFERENCES webapp.species(id) ON DELETE CASCADE,
    breed VARCHAR(255) NOT NULL,
    name VARCHAR(255) UNIQUE,
    age INT,
    gender VARCHAR(255),
    description TEXT,
    image_url VARCHAR(255),
    status VARCHAR(128),
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS webapp.medical_record
(
    id BIGSERIAL PRIMARY KEY,
    pet_id BIGINT REFERENCES webapp.pet(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES webapp.user(id) ON DELETE SET NULL,
    diagnosis TEXT,
    treatment TEXT,
    prescription TEXT,
    examination_date DATE,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS webapp.adoption_request
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id BIGINT REFERENCES webapp.user(id) ON DELETE CASCADE,
    pet_id BIGINT REFERENCES webapp.pet(id) ON DELETE CASCADE,
    status VARCHAR(128),
    review_date TIMESTAMP,
    request_date TIMESTAMP DEFAULT NOW()
    );

CREATE TABLE IF NOT EXISTS webapp.notification
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id BIGINT REFERENCES webapp.user(id) ON DELETE CASCADE,
    adoption_request_id UUID REFERENCES webapp.adoption_request(id) ON DELETE SET NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN,
    created_at TIMESTAMP DEFAULT NOW()
);

