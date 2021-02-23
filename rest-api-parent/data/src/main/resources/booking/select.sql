select 
	booking.id as id,
	booking.code as code,
	user_booking."user" as "userId"
from booking 
join user_booking on user_booking.booking = ? 
where booking.id = ?