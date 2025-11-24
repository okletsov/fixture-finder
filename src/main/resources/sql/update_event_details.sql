UPDATE
	event
SET
	odds_home = :odds_home,
	odds_draw = :odds_draw,
	odds_away = :odds_away,
	dropping_odds_count = :dropping_odds_count,
	dropping_odds_pct = :dropping_odds_pct,
	date_updated = :date_updated
WHERE
	id = :id;