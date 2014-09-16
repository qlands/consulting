INSERT INTO Manufacturer(manufacturerId, name, street, city, zipCode, country, updated) VALUES (1, 'Toyota', 'Toyota City', 'Osaka', '99999', 'Japan', TODAY);
INSERT INTO Manufacturer(manufacturerId, name, street, city, zipCode, country, updated) VALUES (2, 'Nissan', 'Nissan City', 'Osaka', '99999', 'Japan', TODAY);
INSERT INTO Manufacturer(manufacturerId, name, street, city, zipCode, country, updated) VALUES (3, 'Honda', 'Honda City', 'Osaka', '99999', 'Japan', TODAY);

INSERT INTO Car(carId, model, currency, price, modelYear, imagePath, updated, manufacturerId) VALUES (11, 'Toyota Echo', 'USD', 100, '2014', '', TODAY, 1);
INSERT INTO Car(carId, model, currency, price, modelYear, imagePath, updated, manufacturerId) VALUES (12, 'Toyota Camry', 'USD', 100, '2014', '', TODAY, 1);
INSERT INTO Car(carId, model, currency, price, modelYear, imagePath, updated, manufacturerId) VALUES (21, 'Nissan Sentra', 'USD', 100, '2014', '', TODAY, 1);
INSERT INTO Car(carId, model, currency, price, modelYear, imagePath, updated, manufacturerId) VALUES (31, 'Honda Civic', 'USD', 100, '2014', '', TODAY, 1);
INSERT INTO Car(carId, model, currency, price, modelYear, imagePath, updated, manufacturerId) VALUES (32, 'Honda Accord', 'USD', 100, '2014', '', TODAY, 1);
