
CREATE TABLE users
(
	id int NOT NULL auto_increment primary key,
  	username character varying(60) NOT NULL,
  	password character varying(20) NOT NULL,
  	role character varying(20) NOT NULL,
  	email character varying(60) NOT NULL,
  	enabled boolean NOT NULL
);

CREATE TABLE orders 
(
	id int NOT NULL auto_increment primary key,
	account character varying(20) NOT NULL,
	amount real,
	control boolean,
	executionId character varying(60)
);

CREATE TABLE budgets
(
	id int NOT NULL auto_increment primary key,
	account character varying(20) NOT NULL,
	amount real
);

insert into users(username,password,role,email,enabled) values ('mujo.mujic','mujo','referent','almir.pehratovic@gmail.com',true);
insert into users(username,password,role,email,enabled) values ('nizama.buljubasic','nizama','kontrolor','almir.pehratovic@gmail.com',true);
insert into users(username,password,role,email,enabled) values ('dzevad.alihodzic','dzevad','menadzer','almir.pehratovic@gmail.com',true);
insert into orders(account,amount,control) values ('611111', 120.5, false);
insert into orders(account,amount,control) values ('311111', 1800, false);
insert into orders(account,amount,control) values ('611111', 15, false);

insert into budgets(account,amount) values ('611111', 4000);
insert into budgets(account,amount) values ('311111', 100);
