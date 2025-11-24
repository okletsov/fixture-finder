package genericHelpers;

import java.math.BigDecimal;

public class EventMetadata {

    private final String eventName;
    private final BigDecimal homeOdds;
    private final BigDecimal drawOdds;
    private final BigDecimal awayOdds;
    private final String href;
    private final String id;

    public EventMetadata(
            String eventName,
            BigDecimal homeOdds,
            BigDecimal drawOdds,
            BigDecimal awayOdds,
            String href,
            String id
    ) {
        this.eventName = eventName;
        this.homeOdds = homeOdds;
        this.drawOdds = drawOdds;
        this.awayOdds = awayOdds;
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

    public String getHref() {
        return href;
    }

    public String getId() {
        return id;
    }

}
