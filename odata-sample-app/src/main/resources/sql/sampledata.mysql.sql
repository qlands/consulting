INSERT INTO Manufacturer(name, street, city, zipCode, country, updated) VALUES ('Toyota', 'Toyota City', 'Osaka', '99999', 'Japan', CURDATE());
INSERT INTO Manufacturer(name, street, city, zipCode, country, updated) VALUES ('Nissan', 'Nissan City', 'Osaka', '99999', 'Japan', CURDATE());
INSERT INTO Manufacturer(name, street, city, zipCode, country, updated) VALUES ('Honda', 'Honda City', 'Osaka', '99999', 'Japan', CURDATE());

INSERT INTO Car(model, currency, price, modelYear, imagePath, updated, manufacturerId) VALUES ('Toyota Echo', 'USD', 100, '2014', '', CURDATE(), 1);
INSERT INTO Car(model, currency, price, modelYear, imagePath, updated, manufacturerId) VALUES ('Toyota Camry', 'USD', 100, '2014', '', CURDATE(), 1);
INSERT INTO Car(model, currency, price, modelYear, imagePath, updated, manufacturerId) VALUES ('Nissan Sentra', 'USD', 100, '2014', '', CURDATE(), 2);
INSERT INTO Car(model, currency, price, modelYear, imagePath, updated, manufacturerId) VALUES ('Honda Civic', 'USD', 100, '2014', '', CURDATE(), 3);
INSERT INTO Car(model, currency, price, modelYear, imagePath, updated, manufacturerId) VALUES ('Honda Accord', 'USD', 100, '2014', '', CURDATE(), 3);

INSERT INTO users (user_name, user_pass) VALUES ('admin', 's3cr3t@99');
INSERT INTO users (user_name, user_pass) VALUES ('user', 's3cr3t@99');
INSERT INTO roles (role_name) VALUES ('manager');
INSERT INTO roles (role_name) VALUES ('operator');
INSERT INTO user_roles (user_name, role_name) VALUES ('admin', 'manager');
INSERT INTO user_roles (user_name, role_name) VALUES ('user', 'operator');
INSERT INTO user_permissions (user_name, entity, permissions) VALUES ('admin', 'cars', 'crud');
INSERT INTO user_permissions (user_name, entity, permissions) VALUES ('admin', 'manufacturers', 'crud');

