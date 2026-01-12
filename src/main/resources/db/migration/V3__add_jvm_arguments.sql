CREATE TABLE jvm_arguments (
    id BIGSERIAL PRIMARY KEY,
    argument VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE INDEX idx_jvm_argument ON jvm_arguments(argument);
