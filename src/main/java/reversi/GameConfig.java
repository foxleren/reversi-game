package reversi;

public record GameConfig(int boardSize) {
    public GameConfig {
        if (boardSize < 4 || boardSize > 8) {
            throw new IllegalArgumentException("Error: invalid board size.");
        }
    }
}

