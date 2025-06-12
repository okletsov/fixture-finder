package genericHelpers;

import java.math.BigDecimal;

public class EventMetadata {

    private final String eventName;
    private final BigDecimal homeOdds;
    private final BigDecimal drawOdds;
    private final BigDecimal awayOdds;
    private final int homeClicks;
    private final int homeClicksPct;
    private final String href;
    private final String id;

    public EventMetadata(
            String eventName,
            BigDecimal homeOdds,
            BigDecimal drawOdds,
            BigDecimal awayOdds,
            int homeClicks,
            int homeClicksPct,
            String href,
            String id
    ) {
        this.eventName = eventName;
        this.homeOdds = homeOdds;
        this.drawOdds = drawOdds;
        this.awayOdds = awayOdds;
        this.homeClicks = homeClicks;
        this.homeClicksPct = homeClicksPct;
        this.href = href;
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public BigDecimal getHomeOdds() {
        return homeOdds;
    }

    public BigDecimal getDrawOdds() {
        return drawOdds;
    }

    public BigDecimal getAwayOdds() {
        return awayOdds;
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

    public String getId() {
        return id;
    }

}
