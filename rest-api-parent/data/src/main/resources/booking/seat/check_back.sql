select bp.id 
from book_passenger AS bp 
join book_flight as bf on bf.booking = bp.booking
where bp.place_back = ?