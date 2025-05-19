package helpers;

import java.math.BigDecimal;

public class EventMetadata {

    private String eventName;
    private BigDecimal homeOdds;
    private int homeClicks;
    private int homeClicksPct;

    public EventMetadata(String eventName, BigDecimal homeOdds, int homeClicks, int homeClicksPct) {
        this.eventName = eventName;
        this.homeOdds = homeOdds;
        this.homeClicks = homeClicks;
        this.homeClicksPct = homeClicksPct;
    }

    public String getEventName() {
        return eventName;
    }

    public BigDecimal getHomeOdds() {
        return homeOdds;
    }

    public int getHomeClicks() {
        return homeClicks;
    }

    public int getHomeClicksPct() {
        return homeClicksPct;
    }

}
