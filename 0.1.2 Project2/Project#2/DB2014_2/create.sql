CREATE TABLE IF NOT EXISTS `db2014_2`.`categories` (
  `CATEGORY` 		INT(5) 			NOT NULL,
  `CATEGORYNAME` 	VARCHAR(45) 	NOT NULL,
PRIMARY KEY (`CATEGORY`));
CREATE TABLE IF NOT EXISTS `db2014_2`.`region` (
  `COUNTRY`		 	VARCHAR(45) 	NOT NULL,
  `REGION` 			INT(1) 			NOT NULL,
PRIMARY KEY 		(`COUNTRY`));
CREATE TABLE IF NOT EXISTS `db2014_2`.`customers` (
  `CUSTOMERID` 		INT 			NOT NULL,
  `FIRSTNAME` 		VARCHAR(45) 	NOT NULL,
  `LASTNAME` 		VARCHAR(45) 	NOT NULL,
  `ADDRESS1` 		VARCHAR(45) 	NOT NULL,
  `CITY` 			VARCHAR(45) 	NOT NULL,
  `STATE` 			VARCHAR(2) 		NULL,
  `ZIP` 			INT 			NULL,
  `COUNTRY` 		VARCHAR(45) 	NOT NULL,
  `EMAIL` 			VARCHAR(45) 	NULL,
  `PHONE` 			VARCHAR(45)		NULL,
  `CREDITCARDTYPE` 	INT(2) 			NOT NULL,
  `CREDITCARD` 		VARCHAR(45)		NOT NULL,
  `CREDITCARDEXPIRATION` VARCHAR(10) NOT NULL,
  `USERNAME` 		VARCHAR(45) 	NOT NULL,
  `PASSWORD` 		VARCHAR(45) 	NOT NULL,
  `AGE` 			INT 			NULL,
  `INCOME` 			INT 			NULL,
  `GENDER` 			VARCHAR(1) 		NULL,
PRIMARY KEY 		(`CUSTOMERID`),
INDEX 				`country_idx` 	(`COUNTRY` ASC),
UNIQUE INDEX 		`USERNAME_UNIQUE` (`USERNAME` ASC),
CONSTRAINT 			`region`
  FOREIGN KEY 		(`COUNTRY`)
  REFERENCES 		`db2014_2`.`region` (`COUNTRY`));
CREATE TABLE IF NOT EXISTS `db2014_2`.`products` (
  `PROD_ID` 		INT 			NOT NULL,
  `CATEGORY` 		INT(5) 			NOT NULL,
  `TITLE` 			VARCHAR(45) 	NOT NULL,
  `ACTOR` 			VARCHAR(45) 	NOT NULL,
  `PRICE` 			DECIMAL(10,2) 	NOT NULL,
  `QUAN_IN_STOCK` 	INT 			NOT NULL,
  `SALES` 			INT 			NOT NULL,
  PRIMARY KEY 		(`PROD_ID`),
  INDEX 			`PRO_CAT_idx` 	(`CATEGORY` ASC),
  CONSTRAINT 		`PRO_CAT`
    FOREIGN KEY 	(`CATEGORY`)
    REFERENCES 		`db2014_2`.`categories` (`CATEGORY`));
CREATE TABLE IF NOT EXISTS `db2014_2`.`orders` (
  `ORDERID` 		INT 			NOT NULL,
  `ORDERDATE` 		DATE 			NOT NULL,
  `CUSTOMERID` 		INT 			NOT NULL,
  `NETAMOUNT` 		DECIMAL(10,2) 	NOT NULL,
  `TAX` 			DECIMAL(10,2) 	NOT NULL,
  `TOTALAMOUNT` 	DECIMAL(10,2) 	NOT NULL,
  PRIMARY KEY 		(`ORDERID`),
  INDEX 			`order_cust_idx` (`CUSTOMERID` ASC),
  CONSTRAINT 		`order_cust`
    FOREIGN KEY 	(`CUSTOMERID`)
    REFERENCES 		`db2014_2`.`customers` (`CUSTOMERID`));
CREATE TABLE IF NOT EXISTS `db2014_2`.`orderlines` (
  `ORDERLINEID` 	INT 			NOT NULL,
  `ORDERID` 		INT 			NOT NULL,
  `PROD_ID` 		INT 			NOT NULL,
  `QUANTITY` 		INT 			NOT NULL,
  PRIMARY KEY 		(`ORDERID`, `ORDERLINEID`),
  INDEX 			`order_idx` 	(`ORDERID` ASC),
  INDEX 			`product_idx` 	(`PROD_ID` ASC),
  CONSTRAINT 		`order`
    FOREIGN KEY 	(`ORDERID`)
    REFERENCES 		`db2014_2`.`orders` (`ORDERID`),
  CONSTRAINT 		`product`
    FOREIGN KEY 	(`PROD_ID`)
    REFERENCES 		`db2014_2`.`products` (`PROD_ID`));
CREATE TABLE IF NOT EXISTS `db2014_2`.`address2` (
  `CUSTOMERID` 		INT 			NOT NULL,
  `ADDRESS2` 		VARCHAR(45) 	NOT NULL,
  PRIMARY KEY 		(`CUSTOMERID`),
  CONSTRAINT 		`ADD_CUST`
    FOREIGN KEY 	(`CUSTOMERID`)
    REFERENCES 		`db2014_2`.`customers` (`CUSTOMERID`));
CREATE TABLE IF NOT EXISTS `db2014_2`.`special` (
  `PROD_ID` 		INT 			NOT NULL,
  PRIMARY KEY 		(`PROD_ID`),
  CONSTRAINT 		`special_id`
    FOREIGN KEY 	(`PROD_ID`)
    REFERENCES 		`db2014_2`.`products` (`PROD_ID`)); 
CREATE TABLE IF NOT EXISTS `db2014_2`.`categories_nokey` (
  `CATEGORY` 		INT(5) 			NOT NULL,
  `CATEGORYNAME` 	VARCHAR(45) 	NOT NULL);
CREATE TABLE IF NOT EXISTS `db2014_2`.`region_nokey` (
  `COUNTRY`		 	VARCHAR(45) 	NOT NULL,
  `REGION` 			INT(1) 			NOT NULL);
CREATE TABLE IF NOT EXISTS `db2014_2`.`customers_nokey` (
  `CUSTOMERID` 		INT 			NOT NULL,
  `FIRSTNAME` 		VARCHAR(45) 	NOT NULL,
  `LASTNAME` 		VARCHAR(45) 	NOT NULL,
  `ADDRESS1` 		VARCHAR(45) 	NOT NULL,
  `CITY` 			VARCHAR(45) 	NOT NULL,
  `STATE` 			VARCHAR(2) 		NULL,
  `ZIP` 			INT 			NULL,
  `COUNTRY` 		VARCHAR(45) 	NOT NULL,
  `EMAIL` 			VARCHAR(45) 	NULL,
  `PHONE` 			VARCHAR(45) 	NULL,
  `CREDITCARDTYPE` 	INT(2) 			NOT NULL,
  `CREDITCARD` 		VARCHAR(45) 	NOT NULL,
  `CREDITCARDEXPIRATION` VARCHAR(10) NOT NULL,
  `USERNAME` 		VARCHAR(45) 	NOT NULL,
  `PASSWORD` 		VARCHAR(45) 	NOT NULL,
  `AGE` 			INT 			NULL,
  `INCOME` 			INT 			NULL,
  `GENDER` 			VARCHAR(1) 		NULL);
CREATE TABLE IF NOT EXISTS `db2014_2`.`products_nokey` (
  `PROD_ID` 		INT 			NOT NULL,
  `CATEGORY` 		INT(5) 			NOT NULL,
  `TITLE` 			VARCHAR(45) 	NOT NULL,
  `ACTOR` 			VARCHAR(45) 	NOT NULL,
  `PRICE` 			DECIMAL(10,2) 	NOT NULL,
  `QUAN_IN_STOCK` 	INT 			NOT NULL,
  `SALES` 			INT 			NOT NULL);
CREATE TABLE IF NOT EXISTS `db2014_2`.`orders_nokey` (
  `ORDERID` 		INT 			NOT NULL,
  `ORDERDATE` 		DATE 			NOT NULL,
  `CUSTOMERID` 		INT 			NOT NULL,
  `NETAMOUNT` 		DECIMAL(10,2) 	NOT NULL,
  `TAX` 			DECIMAL(10,2) 	NOT NULL,
  `TOTALAMOUNT` 	DECIMAL(10,2) 	NOT NULL);
CREATE TABLE IF NOT EXISTS `db2014_2`.`orderlines_nokey` (
  `ORDERLINEID` 	INT 			NOT NULL,
  `ORDERID` 		INT 			NOT NULL,
  `PROD_ID` 		INT 			NOT NULL,
  `QUANTITY` 		INT 			NOT NULL);
CREATE TABLE IF NOT EXISTS `db2014_2`.`address2_nokey` (
  `CUSTOMERID` 		INT 			NOT NULL,
  `ADDRESS2` 		VARCHAR(45) 	NOT NULL);
CREATE TABLE IF NOT EXISTS `db2014_2`.`special_nokey` (
  `PROD_ID` 		INT 			NOT NULL);