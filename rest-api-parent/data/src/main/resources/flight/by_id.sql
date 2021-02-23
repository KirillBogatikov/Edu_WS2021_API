select 
	flight.id as flightId, 
	flight.code as code, 
	flight.from_date_time as fromDateTime, 
	flight.to_date_time as toDateTime, 
	flight.cost as cost, 
	flight.availability as availability, 
	placeFrom.id as fromId, 
	placeFrom.city as fromCity, 
	placeFrom.airport as fromAirport, 
	placeFrom.iata as fromIata, 
	placeTo.id as toId, 
	placeTo.city as toCity, 
	placeTo.airport as toAirport, 
	placeTo.iata as toIata 
from flight 
join place as placeFrom on placeFrom.id = flight.from_place_id 
join place as placeTo on placeTo.id = flight.to_place_id 
where flight.id = ?