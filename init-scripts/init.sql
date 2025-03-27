\c chatdb

CREATE TABLE users (
    id                  SERIAL PRIMARY KEY,
    username            VARCHAR(127) UNIQUE NOT NULL,
    password            VARCHAR(255) NOT NULL,
    link_profile_photo  VARCHAR(2048)
);

CREATE TABLE chats (
    id                  SERIAL PRIMARY KEY,
    is_group            BOOLEAN NOT NULL,
    group_name          VARCHAR(127),
    link_group_photo    VARCHAR(2048)
);

CREATE TABLE messages (
    id                  SERIAL PRIMARY KEY,
    text                TEXT UNIQUE NOT NULL,
    sender_user_id      INTEGER NOT NULL,
    reciever_chat_id    INTEGER NOT NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_sender_user FOREIGN KEY (sender_user_id) REFERENCES users (id),
    CONSTRAINT fk_receiver_chat FOREIGN KEY (reciever_chat_id) REFERENCES chats (id)
);

CREATE TABLE users_chats (
    user_id             INTEGER NOT NULL,
    chat_id             INTEGER NOT NULL,

    PRIMARY KEY (user_id, chat_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_chat FOREIGN KEY (chat_id) REFERENCES chats (id)
);


INSERT INTO users (username, password, link_profile_photo) VALUES
('Giacomo', '123', 'https://media.tenor.com/-GLxXYXKdlAAAAAe/cinema-absolute.png'),
('Alessandro', '123', 'https://media.tenor.com/-GLxXYXKdlAAAAAe/cinema-absolute.png'),
('Elia', '123', 'https://media.tenor.com/-GLxXYXKdlAAAAAe/cinema-absolute.png'),
('Matteo', '123', 'https://media.tenor.com/-GLxXYXKdlAAAAAe/cinema-absolute.png'),
('Renzo', '123', 'https://media.tenor.com/-GLxXYXKdlAAAAAe/cinema-absolute.png');

INSERT INTO chats (is_group, group_name, link_group_photo) VALUES
(false, NULL, NULL),
(false, NULL, NULL),
(true, 'Project Team', 'https://media.tenor.com/-GLxXYXKdlAAAAAe/cinema-absolute.png'),
(true, 'Friends', 'https://media.tenor.com/-GLxXYXKdlAAAAAe/cinema-absolute.png');

INSERT INTO users_chats (user_id, chat_id) VALUES
(1, 1),
(2, 1),
(1, 2),
(3, 2),
(1, 3),
(2, 3),
(3, 3),
(4, 3),
(1, 4),
(3, 4),
(4, 4);

INSERT INTO messages (text, sender_user_id, reciever_chat_id, created_at) VALUES
('Hey Jane, how are you?', 1, 1, '2023-06-10 09:30:00'),
('I''m good, thanks! How about you?', 2, 1, '2023-06-10 09:32:00'),
('Doing well, working on the new project', 1, 1, '2023-06-10 09:35:00'),
('Hi Alex, did you see the latest updates?', 1, 2, '2023-06-10 10:15:00'),
('Yes, I''m reviewing them now', 3, 2, '2023-06-10 10:20:00'),
('Team meeting tomorrow at 10 AM', 1, 3, '2023-06-10 11:00:00'),
('I''ll prepare the presentation', 2, 3, '2023-06-10 11:05:00'),
('I''ll take notes during the meeting', 4, 3, '2023-06-10 11:10:00'),
('Who''s up for dinner this weekend?', 3, 4, '2023-06-10 18:00:00'),
('I''m in!', 1, 4, '2023-06-10 18:05:00'),
('Me too!', 4, 4, '2023-06-10 18:10:00');