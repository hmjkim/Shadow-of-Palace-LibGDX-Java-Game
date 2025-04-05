package Sprites;

public enum RoyalConsortType {
    GWI_IN("Gwi In", "royalconsorts/gwi-in-front.png", "gwi-in"),
    SUK_WON("Suk Won", "royalconsorts/suk-won-front.png", "suk-won"),
    SUK_BIN("Suk Bin", "royalconsorts/suk-bin-front.png", "suk-bin"),
    BIN("Bin", "royalconsorts/bin-front.png", "bin"),
    QUEEN("Queen", "royalconsorts/queen-front.png", "queen"),;

    private final String name;
    private final String frontImagePath;
    private final String regionName;

    RoyalConsortType(final String name, final String frontImagePath, final String regionName) {
        this.name = name;
        this.frontImagePath = frontImagePath;
        this.regionName = regionName;
    }

    public String getName() {
        return name;
    }

    public String getFrontImagePath() {
        return frontImagePath;
    }

    public String getRegionName() { return regionName; }
}


