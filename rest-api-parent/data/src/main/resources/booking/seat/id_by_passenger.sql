select bp.id as id
from book_passenger bp
join passenger on (passenger.id = bp.passenger and passenger.number_id = ?)
where booking = ?