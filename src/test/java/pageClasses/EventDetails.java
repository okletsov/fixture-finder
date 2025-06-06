package pageClasses;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class EventDetails {

    private static final Logger Log = LogManager.getLogger(EventDetails.class.getName());

    private final WebDriver driver;
    private final boolean eventFinished;
    private final String homeTeamName;
    private final int homeLeaguePos;
    private final int awayLeaguePos;
    private final int leagueTeamsCount;

    public EventDetails(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
        this.eventFinished = isFinished.getDomAttribute("value").equals("1");
        this.homeTeamName = homeTeam.findElement(By.xpath("preceding-sibling::div")).getText();
        String homeTeamId = getIdFromHref(homeTeam.findElement(By.xpath("parent::*")).getDomProperty("href"));
        String awayTeamId = getIdFromHref(awayTeam.findElement(By.xpath("parent::*")).getDomProperty("href"));
        this.homeLeaguePos = setLeaguePos(homeTeamId);
        this.awayLeaguePos = setLeaguePos(awayTeamId);
        this.leagueTeamsCount = leagueTeams.size();
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

    private int setLeaguePos(String teamId) {
        String css = ".glib-participant-" + teamId;
        int tableOrderIndex = Integer.parseInt(driver.findElement(By.cssSelector(css)).getDomAttribute("data-def-order"));
        return tableOrderIndex + 1;
    }

    public int getHomeLeaguePos() {
        return homeLeaguePos;
    }

    public int getAwayLeaguePos() {
        return awayLeaguePos;
    }

    public int getLeagueTeamsCount() {
        return leagueTeamsCount;
    }

    public int getHomeLeaguePosPct() {
        return homeLeaguePos*100/leagueTeamsCount;
    }

    public int getAwayLeaguePosPct() {
        return awayLeaguePos*100/leagueTeamsCount;
    }
}
