insert into passenger 
	(id, number_id, first_name, last_name, birth_date, document_number)
	values (?, ?, ?, ?, ?, ?);
insert into book_passenger 
	(id, booking, passenger, place_from, place_back)
	values (?, ?, ?, ?, ?);