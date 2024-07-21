CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS accounts (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        user_id BIGINT NOT NULL,
                                        currency VARCHAR(3) NOT NULL,
                                        balance DECIMAL(19, 4) NOT NULL,
                                        CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)
);
