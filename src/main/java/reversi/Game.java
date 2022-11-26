package reversi;

public abstract class Game {
    private static GameConfig gameConfig;

    abstract void initGameConfig();

    public static GameConfig getGameConfig() {
        return gameConfig;
    }

    public static void setGameConfig(GameConfig gameConfig) {
        Game.gameConfig = gameConfig;
    }
}
