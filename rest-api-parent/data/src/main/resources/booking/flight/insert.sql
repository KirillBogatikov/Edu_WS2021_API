insert into book_flight
	(id, booking, flight)
	values (?, ?, ?);
update flight 
	set availability = availability - 1 
	where id = ?;