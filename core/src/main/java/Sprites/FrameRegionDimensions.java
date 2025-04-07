package Sprites;

import java.util.Objects;

/**
 * Represents frame dimensions.
 *
 * @author Zoey Lee, Heather Kim
 * @version 2025
 */
public class FrameRegionDimensions {
    private final int frameWidth;
    private final int frameHeight;

    /**
     * Constructs a FrameRegionDimensions with the specified frameWidth, frameHeight.
     *
     * @param frameWidth the width of the frame
     * @param frameHeight the height of the frame
     */
    public FrameRegionDimensions(final int frameWidth, final int frameHeight) {
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
    }

    /**
     * Returns the frame width.
     *
     * @return the width of the frame
     */
    public int getFrameWidth() {
        return frameWidth;
    }

    /**
     * Returns the frame height.
     *
     * @return the height of the frame
     */
    public int getFrameHeight() {
        return frameHeight;
    }

    /**
     * Determines if the FrameRegionDimensions is equal to another object.
     *
     * @param obj the object to compare
     * @return true if the FrameRegionDimensions is equal to the other object, false otherwise
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj.getClass() == this.getClass())) {
            return false;
        }

        FrameRegionDimensions frameRegionDimensions = (FrameRegionDimensions) obj;
        return Objects.equals(this.frameHeight, frameRegionDimensions.frameHeight)
            && Objects.equals(this.frameWidth, frameRegionDimensions.frameWidth);
    }

    /**
     * Gets the hashcode of the FrameRegionDimensions.
     *
     * @return the hashcode of the FrameRegionDimensions as an integer
     */
    @Override
    public int hashCode() {
        int result = Objects.hashCode(this.frameHeight);
        final int prime = 16;
        result = prime * result + Objects.hashCode(this.frameWidth);
        return result;
    }

    /**
     * Gets the string representation of the FrameRegionDimensions.
     *
     * @return the string representation of the FrameRegionDimensions
     */
    @Override
    public String toString() {
        final StringBuilder sb;
        sb = new StringBuilder("FrameRegionDimensions {");
        sb.append("frameHeight='").append(this.frameHeight).append('\'');
        sb.append("frameWidth='").append(this.frameWidth);
        sb.append('}');
        return sb.toString();
    }
}
