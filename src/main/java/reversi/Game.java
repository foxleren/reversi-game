package reversi;

public abstract class Game {
    protected static GameConfig gameConfig;

    public record GameConfig(int boardSize, int gameMode) {
    }

    abstract void initGameConfig();

    public static GameConfig getGameConfig() {
        return gameConfig;
    }

    public static void setGameConfig(GameConfig gameConfig) {
        Game.gameConfig = gameConfig;
    }
}
