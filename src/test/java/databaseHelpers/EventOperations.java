package databaseHelpers;

import genericHelpers.DateTimeOperations;
import genericHelpers.EventMetadata;
import genericHelpers.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pageClasses.EventDetails;

import java.sql.Connection;
import java.util.HashMap;

public class EventOperations {

    private static final Logger Log = LogManager.getLogger(EventOperations.class.getName());

    private final Connection conn;
    private EventMetadata eventMetadata = null;
    private EventDetails eventDetails = null;

    public EventOperations(
            Connection conn,
            EventMetadata eventMetadata,
            EventDetails eventDetails
    ) {
        this.conn = conn;
        this.eventMetadata = eventMetadata;
        this.eventDetails = eventDetails;
    }

    public EventOperations(
            Connection conn
    ) {
        this.conn = conn;
    }

    public void addEvent() {

//        Getting necessary classes
        DateTimeOperations dtOp = new DateTimeOperations();
        Properties prop = new Properties();

        // Providing values for parameters
        HashMap<String, Object> paramValues = new HashMap<>();
        paramValues.put("id", eventMetadata.getId());
        paramValues.put("name", eventMetadata.getEventName());
        paramValues.put("sport", eventDetails.getSport());
        paramValues.put("country", eventDetails.getCountry());
        paramValues.put("league", eventDetails.getLeague());
        paramValues.put("odds_home", eventMetadata.getHomeOdds());
        paramValues.put("odds_draw", eventMetadata.getDrawOdds());
        paramValues.put("odds_away", eventMetadata.getAwayOdds());
        paramValues.put("home_clicks_count", eventMetadata.getHomeClicks());
        paramValues.put("home_clicks_pct", eventMetadata.getHomeClicksPct());
        paramValues.put("main_score", null);
        paramValues.put("detailed_score", null);
        paramValues.put("result", null);
        paramValues.put("dropping_odds_count", eventDetails.getDroppingOddsCount());
        paramValues.put("dropping_odds_pct", eventDetails.getDroppingOddsPct());
        paramValues.put("h2h_away_odds", eventDetails.getAwayOdds(
                eventDetails.getOddsByEvent(
                        eventDetails.getPlayedAwayH2hEvents().get(0))));
        paramValues.put("home_league_pos",eventDetails.getHomeLeaguePos());
        paramValues.put("home_league_pos_pct",eventDetails.getHomeLeaguePosPct());
        paramValues.put("away_league_pos",eventDetails.getAwayLeaguePos());
        paramValues.put("away_league_pos_pct",eventDetails.getAwayLeaguePosPct());
        paramValues.put("league_teams_count", eventDetails.getLeagueTeamsCount());
        paramValues.put("league_games_played", eventDetails.getLeagueGamesPlayed());
        paramValues.put("home_form", eventDetails.getTeamForm(eventDetails.getHomeTeamId()));
        paramValues.put("away_form", eventDetails.getTeamForm(eventDetails.getAwayTeamId()));
        paramValues.put("date_scheduled", eventDetails.getDateTimeScheduled());
        paramValues.put("date_updated", dtOp.getTimestamp());
        paramValues.put("url", eventMetadata.getHref());
        paramValues.put("strategy_id", prop.getStrategyId());
        paramValues.put("continent", null);

//        Generating sql
        SqlLoader sqlLoader = new SqlLoader("sql/insert_event.sql");
        String sql = sqlLoader.getSql(paramValues);

//        Run the insert query
        ExecuteQuery eq = new ExecuteQuery(conn, sql);
        if (eq.getRowsAffected() == 1) {
            Log.info("Event " + eventMetadata.getEventName() + " added to DB\n") ;
        } else {
            Log.error("Event was not inserted!\n");
        }
        eq.cleanUp();
    }

    public void updateEvent() {

        DateTimeOperations dtOp = new DateTimeOperations();

        HashMap<String, Object> paramValues = new HashMap<>();
        paramValues.put("odds_home", eventMetadata.getHomeOdds());
        paramValues.put("odds_draw", eventMetadata.getDrawOdds());
        paramValues.put("odds_away", eventMetadata.getAwayOdds());
        paramValues.put("home_clicks_count", eventMetadata.getHomeClicks());
        paramValues.put("home_clicks_pct", eventMetadata.getHomeClicksPct());
        paramValues.put("dropping_odds_count", eventDetails.getDroppingOddsCount());
        paramValues.put("dropping_odds_pct", eventDetails.getDroppingOddsPct());
        paramValues.put("date_updated", dtOp.getTimestamp());
        paramValues.put("id", eventMetadata.getId());

//        Generating sql
        SqlLoader sqlLoader = new SqlLoader("sql/update_event_details.sql");
        String sql = sqlLoader.getSql(paramValues);

//        Run the insert query
        ExecuteQuery eq = new ExecuteQuery(conn, sql);
        if (eq.getRowsAffected() == 1) {
            Log.info("Event " + eventMetadata.getEventName() + " updated!\n") ;
        } else {
            Log.error("Event was not updated!\n");
        }
        eq.cleanUp();
    }

    public void deleteEventById(String id) {
        HashMap<String, Object> paramValues = new HashMap<>();
        paramValues.put("id", id);

        SqlLoader sqlLoader = new SqlLoader("sql/delete_event.sql");
        String sql = sqlLoader.getSql(paramValues);

        ExecuteQuery eq = new ExecuteQuery(conn, sql);
        if (eq.getRowsAffected() == 1) {
            Log.info("Event " + id + " deleted!\n") ;
        } else {
            Log.error("Event was not deleted!\n");
        }
        eq.cleanUp();
    }

    public String getEventById(String id) {
        HashMap<String, Object> paramValues = new HashMap<>();
        paramValues.put("id", id);

        SqlLoader sqlLoader = new SqlLoader("sql/get_event_by_id.sql");
        String sql = sqlLoader.getSql(paramValues);

        DatabaseOperations dbOp = new DatabaseOperations();
        return dbOp.getSingleValue(conn, "id", sql);
    }
}
