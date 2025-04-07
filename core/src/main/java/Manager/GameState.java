package Manager;

import Sprites.RoyalConsortType;

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

    // Default constructor
    public GameState() {
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
        playerHealth = 5;
    }

    public float getPlayerXPosition() {
        return playerXPosition;
    }

    public float getPlayerYPosition() {
        return playerYPosition;
    }

    public String getPlayerRank() {
        return playerRank;
    }

    public int getStage() {
        return stage;
    }

    public int getWaterCount() {
        return waterCount;
    }

    public int getHerbCount() {
        return herbCount;
    }

    public int getStoneCount() {
        return stoneCount;
    }

    public int getPoisonCount() {
        return poisonCount;
    }

    public int getInvincibilityCount() {
        return invincibilityCount;
    }

    public int getHeartRecoveryCount() {
        return heartRecoveryCount;
    }

    public float getPlayerHealth() {
        return playerHealth;
    }

    public void setPlayerXPosition(float playerXPosition) {
        this.playerXPosition = playerXPosition;
    }

    public void setPlayerYPosition(float playerYPosition) {
        this.playerYPosition = playerYPosition;
    }

    public void setPlayerRank(String playerRank) {
        this.playerRank = playerRank;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public void setWaterCount(int waterCount) {
        this.waterCount = waterCount;
    }

    public void setHerbCount(int herbCount) {
        this.herbCount = herbCount;
    }

    public void setStoneCount(int stoneCount) {
        this.stoneCount = stoneCount;
    }

    public void setPoisonCount(int poisonCount) {
        this.poisonCount = poisonCount;
    }

    public void setInvincibilityCount(int invincibilityCount) {
        this.invincibilityCount = invincibilityCount;
    }

    public void setHeartRecoveryCount(int heartRecoveryCount) {
        this.heartRecoveryCount = heartRecoveryCount;
    }

    public void setPlayerHealth(float playerHealth) {
        this.playerHealth = playerHealth;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GameState{");
        sb.append("playerXPosition=").append(playerXPosition);
        sb.append(", playerYPosition=").append(playerYPosition);
        sb.append(", playerRank='").append(playerRank).append('\'');
        sb.append(", stage=").append(stage);
        sb.append(", waterCount=").append(waterCount);
        sb.append(", herbCount=").append(herbCount);
        sb.append(", stoneCount=").append(stoneCount);
        sb.append(", poisonCount=").append(poisonCount);
        sb.append(", invincibilityCount=").append(invincibilityCount);
        sb.append(", heartRecoveryCount=").append(heartRecoveryCount);
        sb.append(", playerHealth=").append(playerHealth);
        sb.append('}');
        return sb.toString();
    }
}


