package Sprites;

public enum RoyalConsortType {
    GWI_IN("Gwi In",
        "royalconsorts/static/gwi-in-front.png",
        "gwi-in",
        new FrameRegionDimensions[] {
            new FrameRegionDimensions(16, 24), // Row 0: Walk Up
            new FrameRegionDimensions(16, 24), // Row 1: Walk Right
            new FrameRegionDimensions(16, 24)  // Row 2: Walk Down
        }),
    SUK_WON("Suk Won",
        "royalconsorts/static/suk-won-front.png",
        "suk-won",
        new FrameRegionDimensions[] {
            new FrameRegionDimensions(14, 24),
            new FrameRegionDimensions(14, 24),
            new FrameRegionDimensions(14, 24)
        }),
    SUK_BIN("Suk Bin",
        "royalconsorts/static/suk-bin-front.png",
        "suk-bin",
        new FrameRegionDimensions[] {
            new FrameRegionDimensions(14, 24),
            new FrameRegionDimensions(13, 24),
            new FrameRegionDimensions(14, 24)
        }),
    BIN("Bin",
        "royalconsorts/static/bin-front.png",
        "bin",
        new FrameRegionDimensions[] {
            new FrameRegionDimensions(20, 26),
            new FrameRegionDimensions(17, 26),
            new FrameRegionDimensions(20, 26)
        }),
    QUEEN("Queen",
        "royalconsorts/static/queen-front.png",
        "queen",
        new FrameRegionDimensions[] {
            new FrameRegionDimensions(20, 26),
            new FrameRegionDimensions(17, 26),
            new FrameRegionDimensions(20, 26)
        });

    private final String name;
    private final String frontImagePath;
    private final String regionName;
    private final FrameRegionDimensions[] frameDimensions;

    RoyalConsortType(final String name, final String frontImagePath, final String regionName, final FrameRegionDimensions[] frameDimensions) {
        this.name = name;
        this.frontImagePath = frontImagePath;
        this.regionName = regionName;
        this.frameDimensions = frameDimensions;
    }

    public String getName() {
        return name;
    }

    public String getFrontImagePath() {
        return frontImagePath;
    }

    public String getRegionName() {
        return regionName;
    }

    public FrameRegionDimensions[] getFrameDimensions() {
        return frameDimensions;
    }
}
