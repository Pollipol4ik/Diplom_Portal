CREATE TABLE IF NOT EXISTS telegram_account (
                                                id BIGSERIAL PRIMARY KEY,
                                                chat_id BIGINT NOT NULL UNIQUE,
                                                account_id BIGINT NOT NULL,
                                                auth_token VARCHAR(512),
                                                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                last_active TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                CONSTRAINT fk_telegram_account_account FOREIGN KEY (account_id)
                                                    REFERENCES account(id) ON DELETE CASCADE
);

CREATE INDEX idx_telegram_account_chat_id ON telegram_account(chat_id);
CREATE INDEX idx_telegram_account_account_id ON telegram_account(account_id);


CREATE TABLE IF NOT EXISTS user_subscription (
                                                 id BIGSERIAL PRIMARY KEY,
                                                 account_id BIGINT NOT NULL,
                                                 subscription_type VARCHAR(20) NOT NULL,
                                                 direction_id bigint,
                                                 subject_id BIGINT,
                                                 topic_id BIGINT,
                                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                 is_active BOOLEAN NOT NULL DEFAULT TRUE,
                                                 CONSTRAINT fk_user_subscription_account FOREIGN KEY (account_id)
                                                     REFERENCES account(id) ON DELETE CASCADE,
                                                 CONSTRAINT fk_user_subscription_direction FOREIGN KEY (direction_id)
                                                     REFERENCES direction(id) ON DELETE CASCADE,
                                                 CONSTRAINT fk_user_subscription_subject FOREIGN KEY (subject_id)
                                                     REFERENCES subject(id) ON DELETE CASCADE,
                                                 CONSTRAINT fk_user_subscription_topic FOREIGN KEY (topic_id)
                                                     REFERENCES subject_topic(id) ON DELETE CASCADE,
                                                 CONSTRAINT unique_subscription UNIQUE (account_id, subscription_type, direction_id, subject_id, topic_id)
);

CREATE INDEX idx_user_subscription_account_id ON user_subscription(account_id);
CREATE INDEX idx_user_subscription_type ON user_subscription(subscription_type);
CREATE INDEX idx_user_subscription_direction ON user_subscription(direction_id);
CREATE INDEX idx_user_subscription_subject ON user_subscription(subject_id);
CREATE INDEX idx_user_subscription_topic ON user_subscription(topic_id);
CREATE INDEX idx_user_subscription_active ON user_subscription(is_active);
