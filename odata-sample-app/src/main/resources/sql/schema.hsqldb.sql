CREATE TABLE Manufacturer (
	manufacturerId BIGINT NOT NULL PRIMARY KEY,
	name VARCHAR(100),
	street VARCHAR(100),
	city VARCHAR(100),
	zipCode VARCHAR(100),
	country VARCHAR(100),
	updated DATE
);

CREATE TABLE Car (
	carId BIGINT NOT NULL PRIMARY KEY,
	model VARCHAR(100),
	currency VARCHAR(100),
	price REAL,
	modelYear VARCHAR(100),
	imagePath VARCHAR(100),
	updated DATE,
	manufacturerId BIGINT NOT NULL,
	FOREIGN KEY (manufacturerId) REFERENCES Manufacturer (manufacturerId)
);