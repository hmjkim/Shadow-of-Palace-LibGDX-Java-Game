package Sprites;

/**
 * Enumerates type for royal consort types.
 *
 * @author Zoey Lee, Heather Kim
 * @version 2025
 */
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

    /**
     * Constructs a RoyalConsort type.
     *
     * @param name the display name
     * @param frontImagePath the path to the front image
     * @param regionName the region name for texture mapping
     * @param frameDimensions the dimensions for each walking direction
     */
    RoyalConsortType(final String name, final String frontImagePath,
                     final String regionName, final FrameRegionDimensions[] frameDimensions) {
        this.name = name;
        this.frontImagePath = frontImagePath;
        this.regionName = regionName;
        this.frameDimensions = frameDimensions;
    }

    /**
     * Returns the consort's display name.
     *
     * @return the name of the consort
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the path to the consort's front image.
     *
     * @return the front image path
     */
    public String getFrontImagePath() {
        return frontImagePath;
    }

    /**
     * Returns the consort's region name.
     *
     * @return the region name
     */
    public String getRegionName() {
        return regionName;
    }

    /**
     * Returns the frame dimensions for the consort.
     *
     * @return an array of FrameRegionDimensions for each walking direction
     */
    public FrameRegionDimensions[] getFrameDimensions() {
        return frameDimensions;
    }
}
