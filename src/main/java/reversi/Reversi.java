package reversi;

import exceptions.MoveBackException;
import exceptions.NotFoundMoveException;
import utils.Printer;
import utils.Reader;

import java.util.Scanner;

public class Reversi extends Game implements Runnable {
    //private static GameConfig gameConfig;

    private static boolean isAppRunning = true;

    private static boolean isConfigReady = false;

    private static boolean isGameRunning = false;

    private static Board board;
    //private static Board backBoard;

    private static int indexOfActiveUser = 0;

    private static int indexOfInactiveUser = 1;

    private static final int USER1_VALUE = 0;

    private static final int USER2_VALUE = 1;

    // private static final int[] usersScores = {0, 0};

    private static int skipRoundCounter = 0;
    private static int roundCounter = 1;

    //private static int backupRound = 1;
    //private static boolean isPrevMoveBack = false;

    private static Player user1;
    private static Player user2;

    @Override
    public void run() {
        Printer.printIntroduction();
        while (isAppRunning) {
            runMenu();
            while (isGameRunning) {
                initGameConfig();
                if (isConfigReady) {
                    board = new Board(getGameConfig().boardSize());
                    user1 = new Player(USER1_VALUE);
                    user2 = new Player(USER2_VALUE);
                    setUsersScore();
                    while (isGameRunning) {
                        playGame();
                    }
                }
            }
        }
    }

    @Override
    public void shutdown() {

    }

    @Override
    void initGameConfig() {
        try {
            Printer.printSystemMessage("Enter size of game board (4 || 6 || 8): ");
            int size = Reader.readData();
//            if (checkExit(size)) {
//                return;
//            }
            if (size < 4 || size > 8 || size % 2 != 0) {
                throw new IllegalArgumentException("Error: invalid board size. Init of config will be restarted.");
            }
            Printer.printSystemMessage("Enter quantity of users (1 == PVE; 2 == PVP): ");
            int quantity = Reader.readData();
//            if (checkExit(quantity)) {
//                return;
//            }
            if (quantity < 1 || quantity > 2) {
                throw new IllegalArgumentException("Error: invalid users quantity. Init of config will be restarted.");
            }
            setGameConfig(new GameConfig(size, quantity));
            isConfigReady = true;
            Printer.printSuccessMessage("reversi.interfaces.Game config is set successfully.");
        } catch (IllegalArgumentException ex) {
            Printer.printExceptionMessage(ex.getMessage());
        }
    }

    private void runMenu() {
        boolean isMenuRunning = true;
        Scanner sc = new Scanner(System.in);
        int option;
        while (isMenuRunning) {
            Printer.printMenu();
            option = sc.nextInt();
            switch (option) {
                case 1 -> {
                    isMenuRunning = false;
                    isGameRunning = true;
                    Printer.printSystemMessage("You have started game init.");
                }
                case 2 -> {
                    isMenuRunning = false;
                    isGameRunning = false;
                    Printer.printSystemMessage("You have left game. Have a nice day!");
                }
                default -> {
                    Printer.printExceptionMessage("Invalid option. Repeat.");
                }
            }
        }
    }

    private void setUsersScore() {
        int user1Score = 0, user2Score = 0;
        var arr = board.getArr();
        var size = board.getSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (arr[i][j] == indexOfActiveUser) {
                    user1Score++;
                } else if (arr[i][j] == indexOfInactiveUser) {
                    user2Score++;
                }
            }
        }
        user1.setUserScore(user1Score);
        user2.setUserScore(user2Score);
    }

    private void playGame() {
        try {
            board.printBoard();
            Printer.printUsersScore(user1.getUserScore(), user2.getUserScore(), roundCounter);
            if (!isGameOver()) {
                if (isAnyMoves()) {
                    MoveCoords coords;
                    if (indexOfActiveUser == user1.getIndexOfUser()) {
                        coords = user1.getUserCoords();
                        user1.makeMove(coords);
                    } else {
                        if (getGameConfig().usersQuantity() == 2) {
                            coords = user2.getUserCoords();
                        } else {
                            coords = user2.getBotCoords(board);
                        }
                        user2.makeMove(coords);
                    }
                } else {
                    throw new NotFoundMoveException(String.format("User %d has skipped round", indexOfActiveUser + 1));
                }
            }
            setNextRound();
        } catch (IllegalArgumentException ex) {
            Printer.printExceptionMessage(ex.getMessage());
        } catch (NotFoundMoveException ex) {
            setActiveUser();
            Printer.printExceptionMessage(ex.getMessage());
            skipRoundCounter++;
            if (skipRoundCounter == 2) {
                findWinner();
                isGameRunning = false;
            }
        } catch (MoveBackException ex) {
            Printer.printSystemMessage(ex.getMessage());
            //makeMoveBack();
            //isPrevMoveBack = true;
            //makeMoveBack();
        }
    }

    private void setNextRound() {
        setActiveUser();
        if (indexOfActiveUser == USER1_VALUE) {
            roundCounter++;
        }
    }

//    private static void backUpBoard() {
//        for (int i = 0; i < board.size; i++) {
//            for (int j = 0; j < board.size; j++) {
//                backBoard.arr[i][j] = board.arr[i][j];
//            }
//        }
//        backBoard.emptyValuesQuantity = board.emptyValuesQuantity;
//        backupRound = roundCounter;
//    }

    private boolean isGameOver() {
        if (board.getEmptyValuesQuantity() == 0) {
            findWinner();
            isGameRunning = false;
            return true;
        }
        return false;
    }

    private void findWinner() {
        if (user1.getUserScore() > user2.getUserScore()) {
            System.out.println("reversi.interfaces.Game over. User 1 has won!\n");
        } else if (user1.getUserScore() < user2.getUserScore()) {
            System.out.println("reversi.interfaces.Game over. User 2 has won!\n");
        } else {
            System.out.println("reversi.interfaces.Game over. The game ended in a draw.\n");
        }
    }

    private boolean isAnyMoves() throws NotFoundMoveException {
        int emptyValuesQuantity = 0;
        var arr = board.getArr();
        int size = board.getSize(), emptyValue = board.getEmptyValue();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (arr[i][j] == emptyValue) {
                    emptyValuesQuantity += 1;
                    if (tryNextMove(i, j, false)) {
                        System.out.println("ABLE: " + (i + 1) + " " + (j + 1));
                        return true;
                    }
                }
            }
        }
        if (emptyValuesQuantity == 0) {
            throw new NotFoundMoveException("Board is full.");
        }
        throw new NotFoundMoveException(String.format("User %d has skipped round.", indexOfActiveUser + 1));
    }

    private void setActiveUser() {
        indexOfActiveUser ^= 1;
        indexOfInactiveUser ^= 1;
    }

    public record MoveCoords(int x, int y) {
        public MoveCoords {
            if (x < 0 || x >= board.getSize() || y < 0 || y >= board.getSize()) {
                throw new IllegalArgumentException("Error: incorrect coords.");
            }
            if (board.getArr()[x][y] != board.getEmptyValue()) {
                throw new IllegalArgumentException("Error: these coords have already been occupied.");
            }
        }
    }

    private MoveCoords getBotCoords() throws NotFoundMoveException {
        var arr = board.getArr();
        int size = board.getSize(), emptyValue = board.getEmptyValue();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (arr[i][j] == emptyValue) {
                    if (tryNextMove(i, j, false)) {
                        //System.out.printf("Bot try (%d %d)\n", i + 1, j + 1);
//                    checkMovePossibility(i, j, true);
                        return new MoveCoords(i, j);
                    }
                }
            }
        }
        throw new NotFoundMoveException(String.format("User %d has skipped round", indexOfActiveUser + 1));
    }

    private void makeBotMove(MoveCoords botCoords) {
//        board.setValue(botCoords.x, botCoords.y, user2Value);
//        tryNextMove(botCoords.x, botCoords.y, true);
//        usersScores[user2Value] += 1;
//        Printer.printMessageForUser(String.format("Bot move is: (%d %d)\n", botCoords.x + 1, botCoords.y + 1));
    }

//    private void makeMoveBack() {
//        if (roundCounter > 1) {
////            for (int i = 0; i < board.size; i++) {
////                for (int j = 0; j < board.size; j++) {
////                    board.arr[i][j] = backBoard.arr[i][j];
////                }
////            }
//            //backBoard.emptyValuesQuantity = board.emptyValuesQuantity;
//            board.arr = Arrays.copyOf(backBoard.arr, board.size);
//            board.emptyValuesQuantity = backBoard.emptyValuesQuantity;
//            roundCounter--;
//            //setActiveUser();
//            setUsersScore();
//            Printer.printSystemMessage("Successful move back");
//            return;
//        }
//        Printer.printExceptionMessage("Can't make move back.");
//    }


    public static void addMoveToBoard(int x, int y, int index) {
        board.setValue(x, y, index);
    }

    public static boolean tryNextMove(int x, int y, boolean confirmMoves) {
        int active = indexOfActiveUser;
        var arr = board.getArr();
        var size = board.getSize();
        var emptyValue = board.getEmptyValue();

        var vScore = getMoveScore(checkTopVertical(x, y, false), checkBottomVertical(x, y, false));
        var hScore = getMoveScore(checkLeftHorizontal(x, y, false), checkRightHorizontal(x, y, false));
        var mScore = getMoveScore(checkLeftMainDiagonal(x, y, false), checkRightMainDiagonal(x, y, false));
        var aScore = getMoveScore(checkLeftAntiDiagonal(x, y, false), checkRightAntiDiagonal(x, y, false));

//        if  (arr[x][y] == active) {
//            return false;
//        }

//        System.out.println("Vertical score: " + vScore);
//        System.out.println("Horizontal score: " + hScore);
//        System.out.println("Main score: " + mScore);
//        System.out.println("Anti score: " + aScore);
//        board.printArray();
//        System.out.println("Coords: " + (x + 1) + " " + (y + 1));

        boolean isPossible = false;
        if (x < size - 1 && arr[x + 1][y] != emptyValue && arr[x + 1][y] != active) {
            if (vScore > 0) {
                isPossible = true;
                //System.out.println(1);
                if (confirmMoves) {
                    confirmMove(checkTopVertical(x, y, true));
                }
            }
        }
        if (1 < x && arr[x - 1][y] != emptyValue && arr[x - 1][y] != active) {
            if (vScore > 0) {
                isPossible = true;
                //System.out.println(2);
                if (confirmMoves) {
                    confirmMove(checkBottomVertical(x, y, true));
                }
            }
        }
        if (y < size - 1 && arr[x][y + 1] != emptyValue && arr[x][y + 1] != active) {
            if (hScore > 0) {
                isPossible = true;
                //System.out.println(3);
                if (confirmMoves) {
                    confirmMove(checkRightHorizontal(x, y, true));
                }
            }
        }
        if (1 < y && arr[x][y - 1] != emptyValue && arr[x][y - 1] != active) {
            if (hScore > 0) {
                isPossible = true;
                //System.out.println(4);
                if (confirmMoves) {
                    confirmMove(checkLeftHorizontal(x, y, true));
                }
            }
        }
        if (1 < x && 1 < y && arr[x - 1][y - 1] != emptyValue && arr[x - 1][y - 1] != active) {
            if (mScore > 0) {
                isPossible = true;
                //System.out.println(5);
                if (confirmMoves) {
                    confirmMove(checkLeftMainDiagonal(x, y, true));
                }
            }
        }
        if (x < size - 1 && y < size - 1 && arr[x + 1][y + 1] != emptyValue && arr[x + 1][y + 1] != active) {
            if (mScore > 0) {
                isPossible = true;
                //System.out.println(6);
                if (confirmMoves) {
                    confirmMove(checkRightMainDiagonal(x, y, true));
                }
            }
        }
        if (x < size - 1 && 1 < y && arr[x + 1][y - 1] != emptyValue && arr[x + 1][y - 1] != active) {
            if (aScore > 0) {
                isPossible = true;
                // System.out.println(7);
                if (confirmMoves) {
                    confirmMove(checkLeftAntiDiagonal(x, y, true));
                }
            }
        }
        if (1 < x && y < size - 1 && arr[x - 1][y + 1] != emptyValue && arr[x - 1][y + 1] != active) {
            if (aScore > 0) {
                isPossible = true;
                //System.out.println(8);
                if (confirmMoves) {
                    confirmMove(checkRightAntiDiagonal(x, y, true));
                }
            }
        }
        return isPossible;
    }

    private static void confirmMove(int score) {
        System.out.println("SCore" + score);
        if (indexOfActiveUser == user1.getIndexOfUser()) {
            user1.setUserScore(user1.getUserScore() + score);
            user2.setUserScore(user2.getUserScore() - score);
        } else {
            user2.setUserScore(user2.getUserScore() + score);
            user1.setUserScore(user1.getUserScore() - score);
        }
    }


    private static int getMoveScore(int a, int b) {
        //System.out.println("Score: " + a + " " + b);
        if (a > 0) {
            return a;
        }
        return b;
    }

    private static int checkTopVertical(int row, int col, boolean needReplace) {
        int topCount = 0;
        var arr = board.getArr();
        int size = board.getSize();
        for (int i = row + 1; i < size; i++) {
            if (arr[i][col] == indexOfInactiveUser) {
                if (needReplace && i < size - 1) {
                    arr[i][col] = indexOfActiveUser;
                }
                topCount++;
            } else if (arr[i][col] == indexOfActiveUser) {
                return topCount;
            } else {
                return 0;
            }
        }
        return 0;
    }

    private static int checkBottomVertical(int row, int col, boolean needReplace) {
        int bottomCount = 0;
        var arr = board.getArr();
        for (int i = row - 1; i >= 0; i--) {
            if (arr[i][col] == indexOfInactiveUser) {
                if (needReplace) {
                    arr[i][col] = indexOfActiveUser;
                }
                bottomCount++;
            } else if (arr[i][col] == indexOfActiveUser) {
                return bottomCount;
            } else {
                return 0;
            }
        }
        return 0;
    }

    private static int checkLeftHorizontal(int row, int col, boolean needReplace) {
        int leftCount = 0;
        var arr = board.getArr();
        for (int j = col - 1; j >= 0; j--) {
            if (arr[row][j] == indexOfInactiveUser) {
                if (needReplace) {
                    arr[row][j] = indexOfActiveUser;
                }
                leftCount++;
            } else if (arr[row][j] == indexOfActiveUser) {
                return leftCount;
            } else {
                return 0;
            }
        }
        return 0;
    }

    private static int checkRightHorizontal(int row, int col, boolean needReplace) {
        int rightCount = 0;
        var arr = board.getArr();
        int size = board.getSize();
        for (int j = col + 1; j < size; j++) {
            if (arr[row][j] == indexOfInactiveUser) {
                if (needReplace && j < size - 1) {
                    arr[row][j] = indexOfActiveUser;
                }
                rightCount++;
            } else if (arr[row][j] == indexOfActiveUser) {
                return rightCount;
            } else {
                return 0;
            }
        }
        return 0;
    }

    private static int checkLeftMainDiagonal(int row, int col, boolean needReplace) {
        int leftCount = 0;
        var arr = board.getArr();
        for (int i = row - 1; i >= 0; i--) {
            for (int j = col - 1; j >= 0; j--) {
                if (i - row == j - col) {
                    if (arr[i][j] == indexOfInactiveUser) {
                        if (needReplace && i > 0 && j > 0) {
                            arr[i][j] = indexOfActiveUser;
                        }
                        leftCount++;
                    } else if (arr[i][j] == indexOfActiveUser) {
                        return leftCount;
                    } else {
                        return 0;
                    }
                }
            }
        }
        return 0;
    }

    private static int checkRightMainDiagonal(int row, int col, boolean needReplace) {
        int rightCount = 0;
        var arr = board.getArr();
        int size = board.getSize();
        for (int i = row + 1; i < size; i++) {
            for (int j = col + 1; j < size; j++) {
                if (i - row == j - col) {
                    if (arr[i][j] == indexOfInactiveUser) {
                        if (needReplace && i < size - 1 && j < size - 1) {
                            arr[i][j] = indexOfActiveUser;
                        }
                        rightCount++;
                    } else if (arr[i][j] == indexOfActiveUser) {
                        return rightCount;
                    } else {
                        return 0;
                    }
                }
            }
        }
        return 0;
    }

    private static int checkLeftAntiDiagonal(int row, int col, boolean needReplace) {
        int leftCount = 0;
        var arr = board.getArr();
        int size = board.getSize();
        for (int i = row + 1; i < size; i++) {
            for (int j = col - 1; j >= 0; j--) {
                if (i + j == row + col) {
                    if (arr[i][j] == indexOfInactiveUser) {
                        if (needReplace && i < size - 1 && j > 0) {
                            arr[i][j] = indexOfActiveUser;
                        }
                        leftCount++;
                    } else if (arr[i][j] == indexOfActiveUser) {
                        return leftCount;
                    } else {
                        return 0;
                    }
                }
            }
        }
        return 0;
    }

    private static int checkRightAntiDiagonal(int row, int col, boolean needReplace) {
        int rightCount = 0;
        var arr = board.getArr();
        int size = board.getSize();
        for (int i = row + 1; i >= 0; i--) {
            for (int j = col + 1; j < size; j++) {
                if (i + j == row + col) {
                    if (arr[i][j] == indexOfInactiveUser) {
                        if (needReplace && i > 0 && j < size - 1) {
                            arr[i][j] = indexOfActiveUser;
                        }
                        rightCount++;
                    } else if (arr[i][j] == indexOfActiveUser) {
                        return rightCount;
                    } else {
                        return 0;
                    }
                }
            }
        }
        return 0;
    }


    // check CODE_VARS
//    private boolean isExit(int val) {
//        if (val == CODE_EXIT) {
//            Printer.printSystemMessage("You have left game. Have a nice day.");
//            isGameRunning = false;
//            isAppRunning = false;
//            return true;
//        }
//        return false;
//    }
//
//    private boolean checkBack(int val) {
//        if (val == KeyReader.CODE_BACK) {
//            System.out.println("Pressed MOVE BACK");
//            //makeMoveBack();
//            return true;
//        }
//        return false;
//    }
}
