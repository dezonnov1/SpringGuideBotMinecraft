-- 1. Таблица версий (используем SERIAL вместо AUTO_INCREMENT)
CREATE TABLE minecraft_versions (
    version_id BIGSERIAL PRIMARY KEY,
    version_name VARCHAR(20) NOT NULL UNIQUE
);

-- 2. Таблица параметров
CREATE TABLE server_properties (
    property_id BIGSERIAL PRIMARY KEY,
    parameter VARCHAR(100) NOT NULL UNIQUE,
    possible_values TEXT,
    default_value VARCHAR(100),
    description TEXT,
    recommendation TEXT
);

-- 3. Связующая таблица (Many-to-Many)
CREATE TABLE server_property_versions (
    property_id BIGINT NOT NULL,
    version_id BIGINT NOT NULL,
    PRIMARY KEY (property_id, version_id),
    CONSTRAINT fk_property FOREIGN KEY (property_id) REFERENCES server_properties(property_id) ON DELETE CASCADE,
    CONSTRAINT fk_version FOREIGN KEY (version_id) REFERENCES minecraft_versions(version_id) ON DELETE CASCADE
);

-- (Опционально) Индекс для ускорения поиска по версии
CREATE INDEX idx_spv_version ON server_property_versions(version_id);
