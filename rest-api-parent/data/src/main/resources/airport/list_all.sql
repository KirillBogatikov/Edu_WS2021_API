select 
	airport.iata as airportIata, 
	airport.name as airportName 
from airport 
where airport.iata ~ ? or airport.name ~ ?