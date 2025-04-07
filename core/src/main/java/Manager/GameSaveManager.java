package Manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

/**
 * Saves the game state.
 *
 * @author Zoey Lee, Heather Kim
 * @version 2025
 */
public final class GameSaveManager {
    private static final String SAVE_FILE = "savedgame.json";

    private GameSaveManager() {
    }

    /**
     * Saves the game state.
     *
     * @param state the GameState object to be saved
     */
    // Save the game state to a JSON file
    public static void saveGame(final GameState state) {
        Json json = new Json();
        // Serialize the state object into a pretty-printed JSON string
        String data = json.prettyPrint(state);
        FileHandle file = Gdx.files.local(SAVE_FILE);
        file.writeString(data, false);  // Overwrite any existing file
        Gdx.app.log("GameSaveManager", "Game saved:\n" + data);
    }

    /**
     * Loads the game state from the JSON file.
     *
     * @return the deserialized GameState, or null if no saved game exists
     */
    public static GameState loadGame() {
        FileHandle file = Gdx.files.local(SAVE_FILE);
        if (!file.exists()) {
            Gdx.app.log("GameSaveManager", "No saved game found!");
            return null;
        }
        String data = file.readString();
        Json json = new Json();
        GameState state = json.fromJson(GameState.class, data);
        Gdx.app.log("GameSaveManager", "Game loaded:\n" + data);
        return state;
    }
}
