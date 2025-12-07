SELECT
	id
from event e
where e.date_scheduled < now() + INTERVAL 2 hour
	and e.`result` is null
order by e.date_scheduled desc;