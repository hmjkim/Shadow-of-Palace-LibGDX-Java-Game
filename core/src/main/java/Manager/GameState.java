package Manager;

import Sprites.RoyalConsortType;

import java.util.Objects;

/**
 * Represents the game state.
 *
 * @author Zoey Lee, Heather Kim
 * @version 2025
 */
public class GameState {
    // Player's position (Box2D world units)
    private float playerXPosition;
    private float playerYPosition;

    // Player's rank
    private String playerRank;

    // Current stage or level
    private int stage;

    // Inventory counts
    private int waterCount;
    private int herbCount;
    private int stoneCount;
    private int poisonCount;
    private int invincibilityCount;
    private int heartRecoveryCount;

    // Player's health
    private float playerHealth;

    /**
     * Constructs a GameState.
     */
    // Default constructor
    public GameState() {
        final int startHealth = 5;
        playerXPosition = 0;
        playerYPosition = 0;
        playerRank = RoyalConsortType.GWI_IN.name();
        stage = 1;
        waterCount = 0;
        herbCount = 0;
        stoneCount = 0;
        poisonCount = 0;
        invincibilityCount = 0;
        heartRecoveryCount = 0;
        playerHealth = startHealth;
    }

    /**
     * Returns the player's X position.
     *
     * @return the player's X position.
     */
    public float getPlayerXPosition() {
        return playerXPosition;
    }

    /**
     * Returns the player's Y position.
     *
     * @return the player's X position.
     */
    public float getPlayerYPosition() {
        return playerYPosition;
    }

    /**
     * Returns the player's rank.
     *
     * @return a String representing the player's rank
     */
    public String getPlayerRank() {
        return playerRank;
    }

    /**
     * Returns the stage.
     *
     * @return the stage
     */
    public int getStage() {
        return waterCount;
    }

    /**
     * Returns the water ingredient count.
     *
     * @return water count
     */
    public int getWaterCount() {
        return waterCount;
    }

    /**
     * Returns the herb ingredient count.
     *
     * @return herb count
     */
    public int getHerbCount() {
        return herbCount;
    }

    /**
     * Returns the stone ingredient count.
     *
     * @return stone count
     */
    public int getStoneCount() {
        return stoneCount;
    }

    /**
     * Returns the poison item count.
     *
     * @return the poison count
     */
    public int getPoisonCount() {
        return poisonCount;
    }

    /**
     * Returns the invincibility item count.
     *
     * @return the invincibility count
     */
    public int getInvincibilityCount() {
        return invincibilityCount;
    }

    /**
     * Returns the heart recovery item count.
     *
     * @return the heart recovery count
     */
    public int getHeartRecoveryCount() {
        return heartRecoveryCount;
    }

    /**
     * Returns the player's health.
     *
     * @return the player's health value
     */
    public float getPlayerHealth() {
        return playerHealth;
    }

    /**
     * Sets the player's X position.
     *
     * @param playerXPosition the new X coordinate
     */
    public void setPlayerXPosition(final float playerXPosition) {
        this.playerXPosition = playerXPosition;
    }

    /**
     * Sets the player's Y position.
     *
     * @param playerYPosition the new Y coordinate
     */
    public void setPlayerYPosition(final float playerYPosition) {
        this.playerYPosition = playerYPosition;
    }

    /**
     * Sets the player's rank.
     *
     * @param playerRank the new rank as a String
     */
    public void setPlayerRank(final String playerRank) {
        this.playerRank = playerRank;
    }

    /**
     * Sets the stage.
     *
     * @param stage the new stage value
     */
    public void setStage(final int stage) {
        this.stage = stage;
    }

    /**
     * Sets the water ingredient count.
     *
     * @param waterCount the new water count
     */
    public void setWaterCount(final int waterCount) {
        this.waterCount = waterCount;
    }

    /**
     * Sets the herb ingredient count.
     *
     * @param herbCount the new herb count
     */
    public void setHerbCount(final int herbCount) {
        this.herbCount = herbCount;
    }

    /**
     * Sets the stone ingredient count.
     *
     * @param stoneCount the new stone count
     */
    public void setStoneCount(final int stoneCount) {
        this.stoneCount = stoneCount;
    }

    /**
     * Sets the poison item count.
     *
     * @param poisonCount the new poison count
     */
    public void setPoisonCount(final int poisonCount) {
        this.poisonCount = poisonCount;
    }

    /**
     * Sets the invincibility item count.
     *
     * @param invincibilityCount the new invincibility count
     */
    public void setInvincibilityCount(final int invincibilityCount) {
        this.invincibilityCount = invincibilityCount;
    }

    /**
     * Sets the heart recovery item count.
     *
     * @param heartRecoveryCount the new heart recovery count
     */
    public void setHeartRecoveryCount(final int heartRecoveryCount) {
        this.heartRecoveryCount = heartRecoveryCount;
    }

    /**
     * Sets the player's health.
     *
     * @param playerHealth the new health value
     */
    public void setPlayerHealth(final float playerHealth) {
        this.playerHealth = playerHealth;
    }

    /**
     * Determines if the GameState is equal to another object.
     *
     * @param obj the object to compare
     * @return true if the GameState is equal to the other object, false otherwise
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

        GameState gameState = (GameState) obj;
        return Objects.equals(this.playerXPosition, gameState.playerXPosition)
            && Objects.equals(this.playerYPosition, gameState.playerYPosition)
            && Objects.equals(this.playerRank, gameState.playerRank)
            && Objects.equals(this.stage, gameState.stage)
            && Objects.equals(this.waterCount, gameState.waterCount)
            && Objects.equals(this.herbCount, gameState.herbCount)
            && Objects.equals(this.stoneCount, gameState.stoneCount)
            && Objects.equals(this.poisonCount, gameState.poisonCount)
            && Objects.equals(this.invincibilityCount, gameState.invincibilityCount)
            && Objects.equals(this.heartRecoveryCount, gameState.heartRecoveryCount)
            && Objects.equals(this.playerHealth, gameState.playerHealth);
    }

    /**
     * Gets the hashcode of the GameState.
     *
     * @return the hashcode of the GameState as an integer
     */
    @Override
    public int hashCode() {
        int result = Objects.hashCode(this.playerXPosition);
        final int prime = 14;
        result = prime * result + Objects.hashCode(this.playerYPosition);
        result = prime * result + Objects.hashCode(this.playerRank);
        result = prime * result + Objects.hashCode(this.stage);
        result = prime * result + Objects.hashCode(this.waterCount);
        result = prime * result + Objects.hashCode(this.herbCount);
        result = prime * result + Objects.hashCode(this.stoneCount);
        result = prime * result + Objects.hashCode(this.poisonCount);
        result = prime * result + Objects.hashCode(this.invincibilityCount);
        result = prime * result + Objects.hashCode(this.heartRecoveryCount);
        result = prime * result + Objects.hashCode(this.playerHealth);
        return result;
    }

    /**
     * Gets the string representation of the GameState.
     *
     * @return the string representation of the GameState
     */
    @Override
    public String toString() {
        final StringBuilder sb;
        sb = new StringBuilder("GameState {");
        sb.append("playerXPosition='").append(playerXPosition).append('\'');
        sb.append(", playerYPosition='").append(playerYPosition).append('\'');
        sb.append(", playerRank='").append(playerRank).append('\'');
        sb.append(", stage='").append(stage).append('\'');
        sb.append(", waterCount='").append(waterCount).append('\'');
        sb.append(", herbCount='").append(herbCount).append('\'');
        sb.append(", stoneCount='").append(stoneCount).append('\'');
        sb.append(", poisonCount='").append(poisonCount).append('\'');
        sb.append(", invincibilityCount='").append(invincibilityCount).append('\'');
        sb.append(", heartRecoveryCount='").append(heartRecoveryCount).append('\'');
        sb.append(", playerHealth='").append(playerHealth);
        sb.append('}');
        return sb.toString();
    }
}
