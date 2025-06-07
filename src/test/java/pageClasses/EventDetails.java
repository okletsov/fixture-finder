package pageClasses;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventDetails {

    private static final Logger Log = LogManager.getLogger(EventDetails.class.getName());

    private final WebDriver driver;
    private final boolean eventFinished;
    private final String homeTeamName;
    private final String homeTeamId;
    private final String awayTeamId;

    public EventDetails(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
        this.eventFinished = isFinished.getDomAttribute("value").equals("1");
        this.homeTeamName = homeTeam.findElement(By.xpath("preceding-sibling::div")).getText();
        this.homeTeamId = getIdFromHref(homeTeam.findElement(By.xpath("parent::*")).getDomProperty("href"));
        this.awayTeamId = getIdFromHref(awayTeam.findElement(By.xpath("parent::*")).getDomProperty("href"));
    }

    @FindBy(css = ".breadcrumb__ul > :nth-child(2) a")
    public WebElement sport;

    @FindBy(css = ".breadcrumb__ul > :nth-child(3) a")
    public WebElement country;

    @FindBy(css = ".breadcrumb__ul > :nth-child(4) a")
    public WebElement league;

    @FindBy(id = "isFinished")
    public WebElement isFinished;

    @FindBy(id = "js-score")
    public WebElement mainScore;

    @FindBy(id = "js-partial")
    public WebElement detailedScore;

    @FindBy(css = "#best-odds-0 > tr")
    public List<WebElement> oddsList;

    @FindBy(css = "#best-odds-0 > tr [data-pos='1'] .icon__decreasing")
    public List<WebElement> droppingOdds;

    @FindBy(id = "homeParticipantIdHeader")
    public WebElement homeTeam;

    @FindBy(id = "awayParticipantIdHeader")
    public WebElement awayTeam;

    @FindBy(css = "#standingsComponent  tbody > tr")
    public List<WebElement> leagueTeams;

    @FindBy(id = "match-date")
    public WebElement dateTimeScheduled;

    @FindBy(css = "#glib-stats tbody")
    public List<WebElement> standingsTables;

//    Note: :not([data-tttid='5']) helps to exclude friendly games
    @FindBy(css = "#js-mutual-table :not([data-tttid='5']) .head-to-head__row .table-main__participantAway [alt]")
    List<WebElement> allH2hAwayTeamNames;

    public String getSport() {
        return sport.getText();
    }

    public String getCountry() {
        return country.getText();
    }

    public String getLeague() {
        return league.getText();
    }

    public String getMainScore() {
        if (eventFinished) {
            return mainScore.getText();
        } else {
            return null;
        }
    }

    public String getDetailedScore() {
        if (eventFinished) {
            return detailedScore.getText();
        } else {
            return null;
        }
    }

    public String getResult(String href) {

//        Finding web elements with home, draw and away odds
        String xpath = "//div[@id='js-mutual-table']//*[@href='" + href + "']/parent::*/following-sibling::*[1]//*[contains(@class, 'table-main__odds ')]";
        List<WebElement> gameOdds = driver.findElements(By.xpath(xpath));

        int oddsWinner = -1;
        for(int i = 0; i <= gameOdds.size() - 1; i++) {
            WebElement element = gameOdds.get(i);
            String classProp = element.getDomAttribute("class");

//            Identifying event outcome index based on the presence of a specific class
            if (classProp.contains("oddsWinnerBold")) {
                oddsWinner = i;
            }
        }

//        Converting event outcome to string
        if (oddsWinner == 0) {
            return "home";
        } else if (oddsWinner == 1) {
            return "draw";
        } else if (oddsWinner == 2) {
            return "away";
        } else {
            return null;
        }
    }

    public int getDroppingOddsCount() {
        return droppingOdds.size();
    }

    public int getDroppingOddsPct() {
        return droppingOdds.size()*100/oddsList.size();
    }

    public String getHomeTeamName() {
        return this.homeTeamName;
    }

    public List<WebElement> getPlayedAwayH2hEvents() {
//        Only return events where the team in question played away
        List<WebElement> narrowedEvents = new ArrayList<>();
        for (WebElement el: allH2hAwayTeamNames) {
            if (el.getDomAttribute("alt").equals(homeTeamName)) {
                narrowedEvents.add(el);
            }
        }
        return narrowedEvents;
    }

    public String getEventDateByEvent(WebElement el) {
        WebElement date = el.findElement(By.xpath(
                "parent::*/parent::*/parent::*/preceding-sibling::div[1]/*[@class='mobileHidden']"));
        return date.getText();
    }

    public List<WebElement> getOddsByEvent(WebElement el) {
        return el.findElements(By.xpath(
                "parent::*/parent::*/parent::*/following-sibling::*/*/*[@data-odd]"));
    }

    public BigDecimal getAwayOdds(List<WebElement> eventOdds) {
        return new BigDecimal(eventOdds.get(2).getText());
    }

    private String getIdFromHref(String href) {
        return href.substring(0, href.length() - 1).substring(href.substring(0, href.length() - 1).lastIndexOf('/') + 1);
    }

    public int getHomeLeaguePos() {
        String css = ".glib-participant-" + homeTeamId;
        int tableOrderIndex = Integer.parseInt(driver.findElement(By.cssSelector(css)).getDomAttribute("data-def-order"));
        return tableOrderIndex + 1;
    }

    public int getAwayLeaguePos() {
        String css = ".glib-participant-" + awayTeamId;
        int tableOrderIndex = Integer.parseInt(driver.findElement(By.cssSelector(css)).getDomAttribute("data-def-order"));
        return tableOrderIndex + 1;
    }

    public int getLeagueTeamsCount() {
        return leagueTeams.size();
    }

    public int getHomeLeaguePosPct() {
        return getHomeLeaguePos()*100/getLeagueTeamsCount();
    }

    public int getAwayLeaguePosPct() {
        return getAwayLeaguePos()*100/getLeagueTeamsCount();
    }

    public int getTeamForm(String teamId) {
        String winsCss = ".glib-participant-" + teamId + " .form-w";
        String drawsCss = ".glib-participant-" + teamId + " .form-d";
        String lossesCss = ".glib-participant-" + teamId + " .form-l";

        String wins = String.valueOf(driver.findElements(By.cssSelector(winsCss)).size());
        String draws = String.valueOf(driver.findElements(By.cssSelector(drawsCss)).size());
        String losses = String.valueOf(driver.findElements(By.cssSelector(lossesCss)).size());

        return Integer.parseInt(wins + draws + losses);
    }

    public String getHomeTeamId() {
        return homeTeamId;
    }

    public String getAwayTeamId() {
        return awayTeamId;
    }

    public String getDateTimeScheduled() {
        return dateTimeScheduled.getText();
    }

    public int getLeagueGamesPlayed() {
        String css = ".glib-participant-" + homeTeamId + " .matches_played";
        return Integer.parseInt(driver.findElement(By.cssSelector(css)).getText());
    }

    public boolean isTournamentOk() {
        /*
            The condition below makes multiple checks:
                1. Making sure the standings table exists (number of tables not 0)
                2. Making sure there are no conferences (number of tables no more than 1)
                3. Making sure tournament type is not a cup (or the number of tables would be 0)
         */
        int standingsTablesCount = standingsTables.size();
        if (standingsTablesCount == 1) {
            return true;
        } else if (standingsTablesCount > 1) {
            Log.info("Phase 2 evaluation failed: there is more than 1 standings table\n");
            return false;
        } else {
            Log.info("Phase 2 evaluation failed: no standings table found\n");
            return false;
        }
    }

    public boolean isLastH2hGameOk() {

        List<WebElement> events = getPlayedAwayH2hEvents();
//        Checking if last h2h game where the team in question played away exists
        if (events.isEmpty()) {
            Log.info("Phase 2 evaluation failed: no away h2h games for " + homeTeamName + "\n");
            return false;
        }

//        Checking if that game was within last year
        String eventDateString = getEventDateByEvent(events.get(0));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        LocalDate lastAwayEventDate = LocalDate.parse(eventDateString, formatter);
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);

        if (lastAwayEventDate.isBefore(oneYearAgo)) {
            Log.info(" Phase 2 evaluation failed: last away h2h game for " + homeTeamName + "was more than a year ago\n");
            return false;
        }

//        Checking if odds for the event exist
        if (getOddsByEvent(events.get(0)).isEmpty()) {
            Log.info("Phase 2 evaluation failed: odds for the last h2h game don't exist\n");
            return false;
        }

        return true;
    }

    public boolean isStandingsOk() {
//        Checking for the number of teams in a league
        int teamsCount = getLeagueTeamsCount();
        if (teamsCount < 15) {
            Log.info("Phase 2 evaluation failed: number of teams in the league is " + teamsCount + "\n");
            return false;
        }

//        Checking for the number of games played
        int gamesPlayed = getLeagueGamesPlayed();
        if (gamesPlayed < 15) {
            Log.info("Phase 2 evaluation failed: number of games played is " + gamesPlayed + "\n");
            return false;
        }

//        Checking if the game matters
        int roundsInLeague = (teamsCount - 1) * 2;
        if (roundsInLeague - gamesPlayed <= 3) {
            Log.info("Phase 2 evaluation failed: game might be useless, " + gamesPlayed + "/" + roundsInLeague + "games played\n");
            return false;
        }

        return true;
    }
}
