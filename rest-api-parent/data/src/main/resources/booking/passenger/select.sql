select 
	passenger.id as id, 
	passenger.number_id as numberId, 
	passenger.first_name as firstName, 
	passenger.last_name as lastName, 
	passenger.birth_date as birthDate, 
	passenger.document_number as documentNumber, 
	book_passenger.place_from as placeFrom, 
	book_passenger.place_back as placeBack 
from book_passenger 
join passenger on passenger.id = book_passenger.passenger 
where book_passenger.booking = ?