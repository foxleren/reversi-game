package reversi;

import exceptions.NotFoundMoveException;

import java.util.Scanner;

public class Reversi {
    private static GameConfig gameConfig;
    private static boolean isGameRunning = false;
    private static boolean isConfigReady = false;
    private static Board board;
    private static int indexOfActiveUser = 0;
    private static int indexOfInactiveUser = 1;
    private static final int user1Value = 0;
    private static final int user2Value = 1;
    private static int[] usersScores = {2, 2};

    public Reversi() {
    }

    private void initGameConfig() {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.print("Enter size of game board (4 || 6 || 8): ");
            int size = sc.nextInt();
            System.out.print("Enter quantity of users (1 == PVE; 2 == PVP): ");
            int quantity = sc.nextInt();
            gameConfig = new GameConfig(size, quantity);
            isConfigReady = true;
            System.out.println("Game config is set successfully.");
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void run() {
        Menu.showIntroduction();
        Menu.run();
        if (isGameRunning) {
            while (!isConfigReady) {
                initGameConfig();
            }
            board = new Board(gameConfig.boardSize());
        }
        while (isGameRunning) {
            playGame();
        }
    }

    private void playGame() {
        try {
            board.printBoard();
            if (!isGameOver()) {
                System.out.printf("SCORE: ->User 1<- %d | %d ->User 2<-\n", usersScores[0], usersScores[1]);
                if (gameConfig.usersQuantity() == 2 || indexOfActiveUser == user1Value) {
                    MoveCoords userCoords = getMoveCoords();
                    makeMove(userCoords);
                } else if (gameConfig.usersQuantity() == 1 && indexOfActiveUser == user2Value) {
                    MoveCoords botCoords = getBotCoords();
                    makeBotMove(botCoords);
                }
            }
            indexOfActiveUser ^= 1;
            indexOfInactiveUser ^= 1;
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        } catch (NotFoundMoveException ex) {
            System.out.println(ex.getMessage());
            indexOfActiveUser ^= 1;
            indexOfInactiveUser ^= 1;
        }
    }

    private MoveCoords getBotCoords() throws NotFoundMoveException {
        for (int i = 0; i < board.size; i++) {
            for (int j = 0; j < board.size; j++) {
                if (isMovePossible(i, j)) {
                    System.out.printf("Bot try (%d %d)\n", i + 1, j + 1);
                    return new MoveCoords(i, j);
                }
            }
        }
        throw new NotFoundMoveException(String.format("User %d has skipped round", indexOfActiveUser + 1));
    }


    private void makeBotMove(MoveCoords botCoords) {
        board.setValue(botCoords.x, botCoords.y, user2Value);
        System.out.printf("Bot move is: (%d %d)\n", botCoords.x + 1, botCoords.y + 1);
    }

    private boolean isGameOver() {
        if (board.getEmptyValuesQuantity() == 0) {
            findWinner();
            isGameRunning = false;
            return true;
        }
        return false;
    }

    private void findWinner() {
        int user1Points = 0, user2Points = 0;
        for (int i = 0; i < board.size; i++) {
            for (int j = 0; j < board.size; j++) {
                if (board.arr[i][j] == user1Value) {
                    user1Points++;
                } else {
                    user2Points++;
                }
            }
        }
        if (user1Points > user2Points) {
            System.out.println("Game over. User 1 has won!\n");
        } else if (user1Points < user2Points) {
            System.out.println("Game over. User 2 has won!\n");
        } else {
            System.out.println("Game over. The game ended in a draw.\n");
        }
    }

    record MoveCoords(int x, int y) {
        MoveCoords {
            if (x < 0 || x >= board.size || y < 0 || y >= board.size) {
                throw new IllegalArgumentException("Error: incorrect coords.");
            }
            if (board.arr[x][y] != board.getEmptyValue()) {
                throw new IllegalArgumentException("Error: these coords have already been occupied.");
            }
        }
    }

    private MoveCoords getMoveCoords() {
        Scanner sc = new Scanner(System.in);
        System.out.printf("User %d: enter move coords(int int):\n", indexOfActiveUser + 1);
        int x = sc.nextInt(), y = sc.nextInt();
        return new MoveCoords(x - 1, y - 1);
    }

    private void makeMove(MoveCoords coords) {
        if (!isMovePossible(coords.x, coords.y)) {
            throw new IllegalArgumentException("Error: move isn't possible!");
        }
        board.setValue(coords.x, coords.y, indexOfActiveUser);
        usersScores[indexOfActiveUser] += 1;
        //board.printArray();
    }

    private record MoveInfo(int score, boolean isFirst) {
    }

    private boolean isMovePossible(int x, int y) {
        int active = indexOfActiveUser;
        int size = board.size;
        var arr = board.arr;
        var emptyValue = board.emptyValue;
        //var inactiveUser = indexOfInactiveUser;

        //System.out.printf("%d %d %d %d \n", active, inactiveUser, arr[x][y], board.getEmptyValue());

//        System.out.println("Vertical score: " + getMoveScore(checkTopVertical(x, y, false), checkBottomVertical(x, y, false)));
//        System.out.println("Horizontal score: " + getMoveScore(checkLeftHorizontal(x, y, false), checkRightHorizontal(x, y, false)));
//        System.out.println("Main score: " + getMoveScore(checkLeftMainDiagonal(x, y, false), checkRightMainDiagonal(x, y, false)));
//        System.out.println("Anti score: " + getMoveScore(checkLeftAntiDiagonal(x, y, false), checkRightAntiDiagonal(x, y, false)));
        var vScore = getMoveScore(checkTopVertical(x, y, false), checkBottomVertical(x, y, false));
        var hScore = getMoveScore(checkLeftHorizontal(x, y, false), checkRightHorizontal(x, y, false));
        var mScore = getMoveScore(checkLeftMainDiagonal(x, y, false), checkRightMainDiagonal(x, y, false));
        var aScore = getMoveScore(checkLeftAntiDiagonal(x, y, false), checkRightAntiDiagonal(x, y, false));
//        System.out.println("Vertical score: " + vScore);
//        System.out.println("Horizontal score: " + hScore);
//        System.out.println("Main score: " + mScore);
//        System.out.println("Anti score: " + aScore);

//        System.out.println("Vertical score: " + vScore);
//        System.out.println("Horizontal score: " + hScore);
//        System.out.println("Main score: " + mScore);
//        System.out.println("Anti score: " + aScore);

        int score;
        boolean isPossible = false;
        //board.printArray();
        if (x < size - 1 && arr[x + 1][y] != emptyValue && arr[x + 1][y] != active) {
            if (vScore.score > 0) {
                score = checkTopVertical(x, y, true);
                usersScores[indexOfActiveUser] += score;
                usersScores[indexOfInactiveUser] -= score;
                isPossible = true;
            }
        }
        ////board.printArray();
        if (1 < x && arr[x - 1][y] != emptyValue && arr[x - 1][y] != active) {
            if (vScore.score > 0) {
                score = checkBottomVertical(x, y, true);
                usersScores[indexOfActiveUser] += score;
                usersScores[indexOfInactiveUser] -= score;
                isPossible = true;
            }
        }
        //board.printArray();
        if (y < size - 1 && arr[x][y + 1] != emptyValue && arr[x][y + 1] != active) {
            if (hScore.score > 0) {
                score = checkRightHorizontal(x, y, true);
                usersScores[indexOfActiveUser] += score;
                usersScores[indexOfInactiveUser] -= score;
                isPossible = true;
            }
        }
        if (1 < y && arr[x][y - 1] != emptyValue && arr[x][y - 1] != active) {
            if (hScore.score > 0) {
                score = checkLeftHorizontal(x, y, true);
                usersScores[indexOfActiveUser] += score;
                usersScores[indexOfInactiveUser] -= score;
                isPossible = true;
            }
        }
        //board.printArray();
        if (1 < x && 1 < y && arr[x - 1][y - 1] != emptyValue && arr[x - 1][y - 1] != active) {
            if (mScore.score > 0) {
                score = checkLeftMainDiagonal(x, y, true);
                usersScores[indexOfActiveUser] += score;
                usersScores[indexOfInactiveUser] -= score;
                isPossible = true;
            }
        }
        //board.printArray();
        if (x < size - 1 && y < size - 1 && arr[x + 1][y + 1] != emptyValue && arr[x + 1][y + 1] != active) {
            if (mScore.score > 0) {
                score = checkRightMainDiagonal(x, y, true);
                usersScores[indexOfActiveUser] += score;
                usersScores[indexOfInactiveUser] -= score;
                isPossible = true;
            }
        }
        //board.printArray();
        if (x < size - 1 && 1 < y && arr[x + 1][y - 1] != emptyValue && arr[x + 1][y - 1] != active) {
            if (aScore.score > 0) {
                score = checkLeftAntiDiagonal(x, y, true);
                usersScores[indexOfActiveUser] += score;
                usersScores[indexOfInactiveUser] -= score;
                isPossible = true;
            }
        }
        //board.printArray();
        if (1 < x && y < size - 1 && arr[x - 1][y + 1] != emptyValue && arr[x - 1][y + 1] != active) {
            if (aScore.score > 0) {
                score = checkRightAntiDiagonal(x, y, true);
                usersScores[indexOfActiveUser] += score;
                usersScores[indexOfInactiveUser] -= score;
                isPossible = true;
            }
        }
        //board.printArray();
        return isPossible;
    }

    private MoveInfo getMoveScore(int a, int b) {
        //System.out.println("Score: " + a + " " + b);
        if (a > 0) {
            return new MoveInfo(a, true);
        }
        return new MoveInfo(b, false);
    }

    private int checkTopVertical(int row, int col, boolean needReplace) {
        int topCount = 0;
        for (int i = row + 1; i < board.size; i++) {
            if (board.arr[i][col] == indexOfInactiveUser) {
                if (needReplace) {
                    board.arr[i][col] = indexOfActiveUser;
                }
                topCount++;
            } else if (board.arr[i][col] == indexOfActiveUser) {
                return topCount;
            } else {
                return 0;
            }
        }
        return 0;
    }

    private int checkBottomVertical(int row, int col, boolean needReplace) {
        int bottomCount = 0;
        for (int i = row - 1; i >= 0; i--) {
            if (board.arr[i][col] == indexOfInactiveUser) {
                if (needReplace) {
                    board.arr[i][col] = indexOfActiveUser;
                }
                bottomCount++;
            } else if (board.arr[i][col] == indexOfActiveUser) {
                return bottomCount;
            } else {
                return 0;
            }
        }
        return 0;
    }

    private int checkLeftHorizontal(int row, int col, boolean needReplace) {
        int leftCount = 0;
        for (int j = col - 1; j >= 0; j--) {
            if (board.arr[row][j] == indexOfInactiveUser) {
                if (needReplace) {
                    board.arr[row][j] = indexOfActiveUser;
                }
                leftCount++;
            } else if (board.arr[row][j] == indexOfActiveUser) {
                return leftCount;
            } else {
                return 0;
            }
        }
        return 0;
    }

    private int checkRightHorizontal(int row, int col, boolean needReplace) {
        int rightCount = 0;
        for (int j = col + 1; j < board.size; j++) {
            if (board.arr[row][j] == indexOfInactiveUser) {
                if (needReplace) {
                    board.arr[row][j] = indexOfActiveUser;
                }
                rightCount++;
            } else if (board.arr[row][j] == indexOfActiveUser) {
                return rightCount;
            } else {
                return 0;
            }
        }
        return 0;
    }


    private int checkLeftMainDiagonal(int row, int col, boolean needReplace) {
        int leftCount = 0;
        for (int i = row - 1; i >= 0; i--) {
            for (int j = col - 1; j >= 0; j--) {
                if (i - row == j - col) {
                    if (board.arr[i][j] == indexOfInactiveUser) {
                        if (needReplace) {
                            board.arr[i][j] = indexOfActiveUser;
                        }
                        leftCount++;
                    } else if (board.arr[i][j] == indexOfActiveUser) {
                        return leftCount;
                    } else {
                        return 0;
                    }
                }
            }
        }
        return 0;
    }

    private int checkRightMainDiagonal(int row, int col, boolean needReplace) {
        int rightCount = 0;
        for (int i = row + 1; i < board.size; i++) {
            for (int j = col + 1; j < board.size; j++) {
                if (i - row == j - col) {
                    if (board.arr[i][j] == indexOfInactiveUser) {
                        if (needReplace) {
                            board.arr[i][j] = indexOfActiveUser;
                        }
                        rightCount++;
                    } else if (board.arr[i][j] == indexOfActiveUser) {
                        return rightCount;
                    } else {
                        return 0;
                    }
                }
            }
        }
        return 0;
    }


    private int checkLeftAntiDiagonal(int row, int col, boolean needReplace) {
        int leftCount = 0;
        for (int i = row + 1; i < board.size; i++) {
            for (int j = col - 1; j >= 0; j--) {
                if (i + j == row + col) {
                    if (board.arr[i][j] == indexOfInactiveUser) {
                        if (needReplace) {
                            board.arr[i][j] = indexOfActiveUser;
                        }
                        leftCount++;
                    } else if (board.arr[i][j] == indexOfActiveUser) {
                        return leftCount;
                    } else {
                        return 0;
                    }
                }
            }
        }
        return 0;
    }

    private int checkRightAntiDiagonal(int row, int col, boolean needReplace) {
        int rightCount = 0;
        for (int i = row + 1; i >= 0; i--) {
            for (int j = col + 1; j < board.size; j++) {
                if (i + j == row + col) {
                    if (board.arr[i][j] == indexOfInactiveUser) {
                        if (needReplace) {
                            board.arr[i][j] = indexOfActiveUser;
                        }
                        rightCount++;
                    } else if (board.arr[i][j] == indexOfActiveUser) {
                        return rightCount;
                    } else {
                        return 0;
                    }
                }
            }
        }
        return 0;
    }

    private static class Board {
        private final int size;
        private final int[][] arr;
        private final int emptyValue = -1;
        private int emptyValuesQuantity;

        public Board(int size) {
            this.size = size;
            this.emptyValuesQuantity = size * size - 4;
            this.arr = new int[size][size];
            setDefaultPosition();
        }

        public int getEmptyValue() {
            return emptyValue;
        }

        private void setDefaultPosition() {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    arr[i][j] = emptyValue;
                }
            }

            arr[size / 2 - 1][size / 2 - 1] = 0;
            arr[size / 2][size / 2] = 0;
            arr[size / 2 - 1][size / 2] = 1;
            arr[size / 2][size / 2 - 1] = 1;
        }

        private void setDefaultBoard() {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    arr[i][j] = emptyValue;
                }
            }
        }

        public void setValue(int x, int y, int value) {
            arr[x][y] = value;
            emptyValuesQuantity--;
        }

        public void printBoard() {
            for (int i = 0; i < 2 * size + 2; i++) {
                if (i > 0) {
                    if (i % 2 == 0) {
                        System.out.printf("%d ", i / 2);
                    } else {
                        System.out.print("   ");
                    }
                }
                for (int j = 0; j < size + 1; j++) {
                    if (i == 0) {
                        if (j > 0) {
                            System.out.printf("   %d", j);
                        } else {
                            System.out.print("  ");
                        }
                    } else if (i % 2 != 0) {
                        if (j == 0) {
                            System.out.print("+");
                        } else if (j != size + 1) {
                            System.out.print("---+");
                        }
                    } else {
                        if (j == 0) {
                            System.out.print(" |");
                        } else if (j != size + 1) {
                            var el = arr[i / 2 - 1][j - 1];
                            if (el != emptyValue) {
                                if (el == 0) {
                                    System.out.print(" @ |");
                                } else {
                                    System.out.print(" O |");
                                }
                                //System.out.printf(" %d |", el + 1);
                            } else {
                                System.out.print("   |");
                            }
                        }
                    }
                }
                System.out.println();
            }
        }

        public void printArray() {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    System.out.printf(arr[i][j] + " ");
                }
                System.out.println();
            }
            System.out.println();
        }

        public int getEmptyValuesQuantity() {
            return emptyValuesQuantity;
        }
    }

    private static class Menu {
        public static void run() {
            boolean isMenuRunning = true;
            Scanner sc = new Scanner(System.in);
            int option;
            while (isMenuRunning) {
                showMenu();
                option = sc.nextInt();
                switch (option) {
                    case 1 -> {
                        isMenuRunning = false;
                        isGameRunning = true;
                        System.out.println("You have started game init.");
                    }
                    case 2 -> {
                        isMenuRunning = false;
                        isGameRunning = false;
                        System.out.println("You have left game. Have a nice day!");
                    }
                    default -> {
                        System.out.println("Invalid option. Repeat.");
                    }
                }
            }
        }

        public static void showIntroduction() {
            System.out.println("Welcome to Reversi Game!");
        }

        private static void showMenu() {
            System.out.println("--->Menu<--- \n1. Start Game\n2. End Game");
        }
    }
}
