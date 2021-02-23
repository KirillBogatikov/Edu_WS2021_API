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
	airportFrom.iata as fromIata, 
	placeTo.id as toId, 
	placeTo.city as toCity, 
	placeTo.airport as toAirport, 
	airportTo.iata as toIata 
from flight 
join place as placeFrom on placeFrom.id = flight.from_place_id 
join place as placeTo on placeTo.id = flight.to_place_id 
join airport as airportFrom on airportFrom."name" = placeFrom.airport 
join airport as airportTo on airportTo."name" = placeTo.airport 
where 
	airportFrom.iata = ? and 
	airportTo.iata = ? and 
	flight.from_date_time >= ? and flight.to_date_time <= ? and 
	flight.availability >= ?