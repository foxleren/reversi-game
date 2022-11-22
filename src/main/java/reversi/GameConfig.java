package reversi;

public record GameConfig(int boardSize, int usersQuantity) {
    public GameConfig {
        if (boardSize < 4 || boardSize > 8 || boardSize % 2 != 0) {
            throw new IllegalArgumentException("Error: invalid board size. Init of config will be restarted.");
        }
        if (usersQuantity < 1 || usersQuantity > 2) {
            throw new IllegalArgumentException("Error: invalid users quantity. Init of config will be restarted.");
        }
    }
}

