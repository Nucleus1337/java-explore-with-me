drop table if exists events;
drop table if exists users;
drop table if exists categories;

CREATE TABLE if not exists users (
	id int8 GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	user_name varchar(100) NULL,
	user_email varchar(256) NULL,
	CONSTRAINT users_pk PRIMARY KEY (id),
	CONSTRAINT users_unique UNIQUE (user_email)
);

CREATE TABLE if not exists categories (
	id int8 GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	category_name varchar(50) NOT NULL,
	CONSTRAINT categories_name_unique UNIQUE (category_name),
	CONSTRAINT categories_pk PRIMARY KEY (id)
);

CREATE TABLE if not exists events (
	id int8 GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	annotation varchar(2000) NOT NULL,
	category_id int8 NOT NULL,
	description varchar(7000) NOT NULL,
	event_date timestamp without time zone NOT NULL,
	lat float4 NOT NULL,
	lon float4 NOT NULL,
	paid bool NULL,
	participant_limit int2 NULL,
	request_moderation bool NULL,
	title varchar(120) NULL,
	created timestamp without time zone not null,
	user_id int8,
	state varchar(100) not null,
	published timestamp without time zone,
	CONSTRAINT events_pk PRIMARY KEY (id),
	CONSTRAINT events_categories_fk FOREIGN KEY (category_id) REFERENCES categories(id)
	constraint events_users_fk foreign key(user_id) references users(id);
);