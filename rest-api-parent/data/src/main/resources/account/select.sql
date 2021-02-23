select 
	id, 
	first_name as firstName, 
	last_name as lastName, 
	phone as phone, 
	document_number as documentNumber,
	password as hash 
from users 
where id = ?