package Sprites;

public enum RoyalConsortType {
    GWI_IN("Gwi In", "royalconsorts/gwi-in-front.png"),
    SUK_WON("Suk Won", "royalconsorts/suk-won-front.png"),
    SUK_BIN("Suk Bin", "royalconsorts/suk-bin-front.png"),
    BIN("Bin", "royalconsorts/bin-front.png"),
    QUEEN("Queen", "royalconsorts/queen-front.png");

    private final String name;
    private final String frontImagePath;

    RoyalConsortType(final String name, final String frontImagePath) {
        this.name = name;
        this.frontImagePath = frontImagePath;
    }

    public String getName() {
        return name;
    }

    public String getFrontImagePath() {
        return frontImagePath;
    }
}


