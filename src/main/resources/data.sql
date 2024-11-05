INSERT INTO tb_roles (role_name) VALUES ('ADMIN');
INSERT INTO tb_roles (role_name) VALUES ('BASIC');
INSERT INTO tb_users (user_id, username, password) VALUES ('31565596-ec80-4f67-b4f0-f41e792296f7', 'admin', '$2a$12$cteFqpKoiFSA/0x7rguuL.sAAXRA4i5mbHL.V/qSfBTUNyt/qHkLa');
INSERT INTO tb_user_roles(role_id, user_id) VALUES (1, '31565596-ec80-4f67-b4f0-f41e792296f7');