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

CREATE TABLE users (
  user_name VARCHAR(20) NOT NULL PRIMARY KEY,
  user_pass VARCHAR(20) NOT NULL
);

CREATE TABLE roles (
  role_name VARCHAR(20) NOT NULL PRIMARY KEY
);

CREATE TABLE user_roles(
  user_name VARCHAR(20) NOT NULL,
  role_name VARCHAR(20) NOT NULL,
  PRIMARY KEY (user_name, role_name)
)