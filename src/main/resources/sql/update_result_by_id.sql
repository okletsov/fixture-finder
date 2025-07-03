UPDATE
	event
SET
	main_score = :main_score,
	detailed_score = :detailed_score,
	result = :result
WHERE
	id = :id;