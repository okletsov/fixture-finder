package helpers;

import java.math.BigDecimal;

public class EventMetadata {

    private final String eventName;
    private final BigDecimal homeOdds;
    private final int homeClicks;
    private final int homeClicksPct;
    private final String href;

    public EventMetadata(String eventName, BigDecimal homeOdds, int homeClicks, int homeClicksPct, String href) {
        this.eventName = eventName;
        this.homeOdds = homeOdds;
        this.homeClicks = homeClicks;
        this.homeClicksPct = homeClicksPct;
        this.href = href;
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

    public String getHref() {
        return href;
    }
}
