drop table COMPONENT cascade;
drop table PRODUCT cascade;
drop table SUPPLIERS cascade;
drop table ORDERS cascade;
drop table ORDER_ITEMS cascade;
drop table USERS cascade;
drop table USER_ROLES cascade;
drop table COMP_PROD cascade;


CREATE TABLE COMPONENT(
	COMPONENT_ID INT NOT NULL AUTO_INCREMENT, 
	NAME VARCHAR(50),
	PRICE INT NOT NULL,
	STOCK_NUM INT NOT NULL,
	DESCRIPTION VARCHAR(200),
	PRIMARY KEY(COMPONENT_ID)
);

-- PRODUCT CONTAINS ALL PRODUCTS IN THE SHOP


CREATE TABLE PRODUCT(
	PRODUCT_ID INT  NOT NULL AUTO_INCREMENT,
	BRAND VARCHAR(30),
	DESCRIPTION VARCHAR(200),
	PRIMARY KEY(PRODUCT_ID)
        
	
);

-- This is a junction table to support many-to-many relations between 
-- product and component tables

CREATE TABLE COMP_PROD(
    COMPONENT_ID int NOT NULL,
    PRODUCT_ID int NOT NULL,
    CONSTRAINT PK_COMP_PROD PRIMARY KEY
    (
        COMPONENT_ID,
        PRODUCT_ID
    ),
    FOREIGN KEY (COMPONENT_ID) REFERENCES COMPONENT (COMPONENT_ID),
    FOREIGN KEY (PRODUCT_ID) REFERENCES PRODUCT (PRODUCT_ID)
);
	




-- ONE ORDER MAY CONTAIN A LOT OF PRODUCTS

CREATE TABLE ORDERS(
	ORDER_ID INT NOT NULL AUTO_INCREMENT,
	BUYER_NAME VARCHAR(100),
	SHIPPING_ADRESS VARCHAR(100),
	SHIPPING_ZIPCODE VARCHAR(10),
	SHIPPING_CITY VARCHAR(30),
	PRIMARY KEY(ORDER_ID)
);

-- EACH DISTINCT ORDER ITEMS	


CREATE TABLE ORDER_ITEMS(
	ORDER_ITEM_ID INT NOT NULL AUTO_INCREMENT,
	ORDER_ID INT,
	PRODUCT_ID INT,
	QUANTITY INT,
	PRIMARY KEY(ORDER_ITEM_ID),
	FOREIGN KEY(ORDER_ID) REFERENCES ORDERS(ORDER_ID),
	FOREIGN KEY(PRODUCT_ID) REFERENCES PRODUCT(PRODUCT_ID)
);


create table USERS(
       USER_NAME varchar(15) not null primary key,
       USER_PASS varchar(15) not null,
       NAME varchar(100) not null,
       STREET_ADDRESS varchar(100)not null,
       ZIP_CODE varchar(10) not null,
       CITY varchar(30) not null,
       COUNTRY varchar(30) not null
);

create table USER_ROLES(
	USER_NAME varchar(15) not null,
	ROLE_NAME varchar(15) not null,
	primary key (USER_NAME, ROLE_NAME)
);






-- SOME AUTHORS. THE AUTHOR_ID WILL BE GENERATED BY THE SEQUENCE

INSERT INTO COMPONENT(NAME, PRICE,STOCK_NUM) VALUES('MB', 8000,5);
INSERT INTO COMPONENT(NAME, PRICE,STOCK_NUM) VALUES('RAM', 100,20);
INSERT INTO COMPONENT(NAME, PRICE,STOCK_NUM) VALUES('VGA', 4500,7);
INSERT INTO COMPONENT(NAME, PRICE,STOCK_NUM) VALUES('CPU', 20000,9);
INSERT INTO COMPONENT(NAME, PRICE,STOCK_NUM) VALUES('HDD', 9000,11);
INSERT INTO COMPONENT(NAME, PRICE,STOCK_NUM) VALUES('MONITOR',12000,30);
INSERT INTO COMPONENT(NAME, PRICE,STOCK_NUM) VALUES('DVDROM',1500,45);


INSERT INTO PRODUCT(BRAND, DESCRIPTION) VALUES('HP','A good choice for game');
INSERT INTO PRODUCT(BRAND, DESCRIPTION) VALUES('APPLE','A office choice');


insert into USERS(USER_NAME, USER_PASS, NAME, STREET_ADDRESS, ZIP_CODE, CITY, COUNTRY) 
     VALUES('tomcat','tacmot','Tom Cat','Apache Road', '34 567', 'Petaluma', 'USA');
insert into USERS(USER_NAME, USER_PASS, NAME, STREET_ADDRESS, ZIP_CODE, CITY, COUNTRY) 
     VALUES('gyro','glurk','Gyro Gearloose','Duck Road', '78 901', 'Ducksbury', 'USA');
insert into USERS(USER_NAME, USER_PASS, NAME, STREET_ADDRESS, ZIP_CODE, CITY, COUNTRY) 
     VALUES('admin', 'glurk','System user', 'Polacksbacken', '752 37', 'Uppsala', 'Sweden');

insert into USER_ROLES(USER_NAME, ROLE_NAME) VALUES('tomcat','tomcat');
insert into USER_ROLES(USER_NAME, ROLE_NAME) VALUES('gyro', 'tomcat');
insert into USER_ROLES(USER_NAME, ROLE_NAME) VALUES('admin','manager');
insert into USER_ROLES(USER_NAME, ROLE_NAME) VALUES('admin','admin');
insert into USER_ROLES(USER_NAME, ROLE_NAME) VALUES('admin','tomcat');