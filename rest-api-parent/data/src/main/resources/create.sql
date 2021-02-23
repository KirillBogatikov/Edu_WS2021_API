drop table if exists airport cascade;
create table airport (
	iata text, 
	name text
);

alter table airport 
	add constraint airport_primary_key primary key (iata), 
	add constraint airport_unique_name unique (name), 
	alter column name set not null;

drop table if exists place cascade;
create table place (
	id uuid,
	city text, 
	airport text, 
	iata text
);

alter table place 
	add constraint place_primary_key primary key (id), 
	add constraint place_airport_foreign_key foreign key (airport) 
		references airport(name) on delete cascade, 
	add constraint place_unique_city unique (city), 
	add constraint place_unique_iata unique (iata);
	
drop table if exists flight cascade; 
create table flight (
	id serial,
	code text,
	from_place_id uuid,
	from_date_time timestamp,
	to_place_id uuid,
	to_date_time timestamp, 
	cost real, 
	availability int 
); 

alter table flight 
	add constraint flight_primary_key primary key (id), 
	add constraint flight_from_foreign_key foreign key (from_place_id) 
		references place(id) on delete cascade, 
	add constraint flight_to_foreign_key foreign key (to_place_id) 
		references place(id) on delete cascade, 
	add constraint flight_unique_code unique (code);
	
drop table if exists passenger cascade;
create table passenger (
	id uuid,
	number_id serial,
	first_name text,
	last_name text,
	birth_date date,
	document_number text
);  

alter table passenger 
	add constraint passenger_primary_key primary key (id),
	alter column first_name set not null,
	alter column last_name set not null,
	alter column birth_date set not NULL,
	alter column document_number set not null;  
	
drop table if exists booking cascade;
create table booking (
	id uuid,
	code text
);

alter table booking 
	add constraint booking_primary_key primary key (id),
	alter column code set not null;
create index booking_code_index on booking(code);  

drop table if exists book_flight cascade;
create table book_flight (
	id uuid,
	booking uuid,
	flight int
);

alter table book_flight 
	add constraint book_flight_primary_key primary key (id),
	add constraint book_flight_booking_foreign_key foreign key (booking)
		references booking(id) on delete cascade,
	add constraint book_flight_flight_foreign_key foreign key (flight)
		references flight(id);  

drop table if exists book_passenger cascade;
create table book_passenger (
	id uuid,
	booking uuid,
	passenger uuid,
	place_from text,
	place_back text
);

alter table book_passenger 
	add constraint book_passenger_primary_key primary key (id),
	add constraint book_passenger_booking_foreign_key foreign key (booking)
		references booking(id) on delete cascade,
	add constraint book_passenger_passenger_foreign_key foreign key (passenger)
		references passenger(id);
		
drop table if exists users cascade;
create table users (
	id uuid,
	password bytea,
	first_name text,
	last_name text,
	phone text,
	document_number text
);

alter table users
	add constraint users_primary_key primary key (id),
	add constraint users_login_unique unique (phone),
	add constraint users_document_unique unique (document_number);
create index users_phone_index ON users(phone);
	
drop table if exists user_booking cascade;
create table user_booking (
	id uuid,
	"user" uuid,
	booking uuid
);

alter table user_booking
	add constraint user_booking_primary_key primary key (id),
	add constraint user_booking_user_foreign_key foreign KEY ("user") 
		references users(id) on delete cascade,
	add constraint user_booking_booking_foreign_key foreign KEY (booking) 
		references booking(id) on delete CASCADE;	
	