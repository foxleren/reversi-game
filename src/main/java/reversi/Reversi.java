package reversi;

import exceptions.MoveBackException;
import exceptions.NotFoundMoveException;
import utils.Printer;
import utils.Reader;

import java.util.Scanner;

public class Reversi extends Game {
    private static boolean isAppRunning = true;

    private static boolean isConfigReady = false;

    private static boolean isGameRunning = false;

    private static Board board;

    private static int indexOfActiveUser = 0;

    private static int indexOfInactiveUser = 1;

    public enum GameMode {
        PVE_NORMAL(1), PVE_HARD(2), PVP(3);
        private final int code;

        GameMode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    private static int skipRoundCounter = 0;
    private static int roundCounter = 1;

    private static Player user1;
    private static Player user2;

    private final int[] sessionScores = new int[2];

    public void run() {
        try {
            Printer.printIntroduction();
            while (isAppRunning) {
                runMenu();
                while (isGameRunning) {
                    initGameConfig();
                    if (isConfigReady) {
                        board = new Board(gameConfig.boardSize());
                        user1 = new Player(Board.BoardValues.USER1.getCode());
                        user2 = new Player(Board.BoardValues.USER2.getCode());
                        roundCounter = 1;
                        skipRoundCounter = 0;
                        while (isGameRunning) {
                            playRound();
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Printer.printExceptionMessage(ex.getMessage());
        }
    }

    @Override
    void initGameConfig() {
        try {
            Printer.printSystemMessage("Enter size of game board (4 || 6 || 8): ");
            int size = Reader.readData();
            if (isExit(size)) {
                return;
            }
            if (size < 4 || size > 8 || size % 2 != 0) {
                throw new IllegalArgumentException("Error: invalid board size. Init of config will be restarted.");
            }
            Printer.printSystemMessage("Enter game mode users (1 - PVE Normal; 2 - PVE Hard; 3 - PVP): ");
            int gameMode = Reader.readData();
            if (isExit(gameMode)) {
                return;
            }
            if (gameMode < 1 || gameMode > 3) {
                throw new IllegalArgumentException("Error: invalid game mode. Init of config will be restarted.");
            }
            setGameConfig(new GameConfig(size, gameMode));
            isConfigReady = true;
            Printer.printSuccessMessage("Game config is set successfully.");
        } catch (IllegalArgumentException ex) {
            Printer.printExceptionMessage(ex.getMessage());
        }
    }

    static void setPossibleMoves(int[][] arr, float[][] f) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                if (f[i][j] > 0) {
                    arr[i][j] = Board.BoardValues.POSSIBLE.getCode();
                }
            }
        }
    }

    private void printPossibleMoves() {
        System.out.print("Possible moves: ");
        var size = board.getSize();
        var arr = board.getArr();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (arr[i][j] == Board.BoardValues.POSSIBLE.getCode()) {
                    System.out.printf("(%d, %d) ", i + 1, j + 1);
                }
            }
        }
        System.out.println();
    }

    private void runMenu() {
        Scanner sc = new Scanner(System.in);
        int option;
        Printer.printMenu();
        option = sc.nextInt();
        switch (option) {
            case 1 -> {
                isGameRunning = true;
                Printer.printSystemMessage("You have started game init.");
            }
            case 2 -> {
                if (isConfigReady) {
                    Printer.printSessionStats(sessionScores[0], sessionScores[1]);
                } else {
                    Printer.printExceptionMessage("Need to start your first game.");
                }
            }
            case 3 -> {
                isGameRunning = false;
                isAppRunning = false;
                Printer.printSystemMessage("You have left game. Have a nice day!");
            }
            default -> {
                Printer.printExceptionMessage("Invalid option. Repeat.");
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
        if (indexOfActiveUser == Board.BoardValues.USER1.getCode()) {
            user1.setUserScore(user1Score);
            user2.setUserScore(user2Score);
        } else {
            user2.setUserScore(user1Score);
            user1.setUserScore(user2Score);
        }
    }

    public static void setIndexOfActiveUser(int indexOfActiveUser) {
        Reversi.indexOfActiveUser = indexOfActiveUser;
    }

    public static void setIndexOfInactiveUser(int indexOfInactiveUser) {
        Reversi.indexOfInactiveUser = indexOfInactiveUser;
    }

    private void playRound() {
        try {
            setUsersScore();
            removeAllPossibleMoves(board.getArr());
            var n = validateMoves(board.getArr(), board.getPossibleMovesArr());
            setPossibleMoves(board.getArr(), board.getPossibleMovesArr());
            board.setPossibleMoveCount(n);
            board.printBoard();
            Printer.printUsersScore(user1.getUserScore(), user2.getUserScore(), roundCounter);
            if (board.getPossibleMoveCount() == 0) {
                throw new NotFoundMoveException(String.format("User %d has skipped round", indexOfActiveUser + 1));
            }
            MoveCoords coords;
            if (indexOfActiveUser == user1.getIndexOfUser()) {
                user1.backupMove(board);
                printPossibleMoves();
                coords = user1.getUserCoords();
                user1.makeMove(coords, board);
            } else {
                if (gameConfig.gameMode() == GameMode.PVP.code) {
                    printPossibleMoves();
                    coords = user2.getUserCoords();
                } else {
                    coords = user2.getBotCoords(board);
                }
                user2.makeMove(coords, board);
                if (gameConfig.gameMode() != GameMode.PVP.code) {
                    Printer.printMessageForUser2("BOT MADE MOVE (" + (coords.x + 1) + " , " + (coords.y + 1) + ")");
                }
            }
            setNextRound();
        } catch (IllegalArgumentException ex) {
            Printer.printExceptionMessage(ex.getMessage());
        } catch (NotFoundMoveException ex) {
            skipRound();
            if (skipRoundCounter != 2) {
                Printer.printExceptionMessage(ex.getMessage());
            }
        } catch (MoveBackException ex) {
            Printer.printSystemMessage(ex.getMessage());
            makeMoveBack();
        }
    }

    private void skipRound() {
        if (++skipRoundCounter == 2) {
            finishGame();
            return;
        }
        setActiveUser();
    }

    void finishGame() {
        board.printBoard();
        Printer.printUsersScore(user1.getUserScore(), user2.getUserScore(), roundCounter);
        findWinner();
        isGameRunning = false;
    }

    private void makeMoveBack() {
        if (roundCounter == 1) {
            Printer.printExceptionMessage("Can't make move back. It's initial move.");
            return;
        }
        if (indexOfActiveUser == user1.getIndexOfUser() && gameConfig.gameMode() != GameMode.PVP.code) {
            board = user1.makeMoveBack();
            roundCounter--;
            setUsersScore();
            Printer.printSystemMessage("Successful move back.");
            return;
        }
        Printer.printExceptionMessage("Can't make move back. This option is available only for PVE modes.");
    }

    static void removeAllPossibleMoves(int[][] arr) {
        var size = board.getSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (arr[i][j] == Board.BoardValues.POSSIBLE.getCode()) {
                    arr[i][j] = Board.BoardValues.EMPTY.getCode();
                }
            }
        }
    }

    static boolean isMoveValid(int i, int j) {
        return board.getArr()[i][j] == Board.BoardValues.POSSIBLE.getCode();
    }

    private void setNextRound() {
        setActiveUser();
        if (indexOfActiveUser == Board.BoardValues.USER1.getCode()) {
            roundCounter++;
        }
    }

    private void findWinner() {
        if (user1.getUserScore() > user2.getUserScore()) {
            sessionScores[0] += 1;
            System.out.println("Game over. User 1 has won!\n");
        } else if (user1.getUserScore() < user2.getUserScore()) {
            sessionScores[1] += 1;
            System.out.println("Game over. User 2 has won!\n");
        } else {
            sessionScores[0] += 1;
            sessionScores[1] += 1;
            System.out.println("Game over. The game ended in a draw.\n");
        }
    }

    static void setActiveUser() {
        indexOfActiveUser ^= 1;
        indexOfInactiveUser ^= 1;
    }

    public record MoveCoords(int x, int y) {
        public MoveCoords {
            if (x < 0 || x >= board.getSize() || y < 0 || y >= board.getSize()) {
                throw new IllegalArgumentException("Error: incorrect coords.");
            }
            if (board.getArr()[x][y] != Board.BoardValues.EMPTY.getCode() && board.getArr()[x][y] != Board.BoardValues.POSSIBLE.getCode()) {
                throw new IllegalArgumentException("Error: these coords have already been occupied.");
            }
        }
    }

    public static void addMoveToBoard(int x, int y, int index) {
        board.setValue(x, y, index);
    }

    static void conquer(int row, int col, int[][] arr) {
        int x = 0;
        int y = 0;
        int size = board.getSize();

        for (int rowdelta = -1; rowdelta <= 1; rowdelta++) {
            for (int coldelta = -1; coldelta <= 1; coldelta++) {

                if (row + rowdelta < 0 || row + rowdelta >= size || col + coldelta < 0 || col + coldelta >= size || (rowdelta == 0 && coldelta == 0)) {
                    continue;
                }

                if (arr[row + rowdelta][col + coldelta] == indexOfInactiveUser) {
                    x = row + rowdelta;
                    y = col + coldelta;

                    while (true) {
                        x += rowdelta;
                        y += coldelta;

                        if (x < 0 || x >= size || y < 0 || y >= size) {
                            break;
                        }

                        if (arr[x][y] == -1) {
                            break;
                        }

                        if (arr[x][y] == indexOfActiveUser) {
                            while (arr[x -= rowdelta][y -= coldelta] == indexOfInactiveUser) {
                                arr[x][y] = indexOfActiveUser;
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    static int validateMoves(int[][] arr, float[][] possibleMoves) {
        int x = 0;
        int y = 0;
        int possibleMovesCounter = 0;
        int size = board.getSize();

        for (int row = 0; row < size; row++)
            for (int col = 0; col < size; col++)
                possibleMoves[row][col] = 0;

        for (int row = 0; row < size; row++)
            for (int col = 0; col < size; col++) {
                if (arr[row][col] != -1 && arr[row][col] != 3) {
                    continue;
                }

                for (int rowdelta = -1; rowdelta <= 1; rowdelta++)
                    for (int coldelta = -1; coldelta <= 1; coldelta++) {

                        if (row + rowdelta < 0 || row + rowdelta >= size || col + coldelta < 0 || col + coldelta >= size || (rowdelta == 0 && coldelta == 0)) {
                            continue;
                        }

                        if (arr[row + rowdelta][col + coldelta] == indexOfInactiveUser) {
                            x = row + rowdelta;
                            y = col + coldelta;

                            float c = 0;

                            while (true) {
                                x += rowdelta;
                                y += coldelta;
                                if (isEdge(x, y)) {
                                    c += 2;
                                } else {
                                    c += 1;
                                }

                                if (x < 0 || x >= size || y < 0 || y >= size) {
                                    break;
                                }

                                if (arr[x][y] == -1) {
                                    break;
                                }

                                if (arr[x][y] == indexOfActiveUser) {
                                    if (possibleMoves[row][col] == 0) {
                                        if (isCorner(row, col)) {
                                            c += 2.8;
                                        } else if (isEdge(row, col)) {
                                            c += 2.4;
                                        } else {
                                            c++;
                                        }
                                        possibleMovesCounter++;
                                    }
                                    possibleMoves[row][col] += c;
                                    break;
                                }
                            }
                        }
                    }
            }
        return possibleMovesCounter;
    }

    private static boolean isEdge(int i, int j) {
        return (i == 0 || i == board.getSize() - 1 || j == 0 || j == board.getSize() - 1);
    }

    private static boolean isCorner(int i, int j) {
        return (i == 0 && j == 0) || (i == 0 && j == board.getSize() - 1) || (i == board.getSize() - 1 && j == 0) || (i == board.getSize() - 1 && j == board.getSize() - 1);
    }

    private boolean isExit(int val) {
        if (val == Reader.ReaderCodes.CODE_EXIT.getCode()) {
            Printer.printSystemMessage("You have left game. Have a nice day.");
            isGameRunning = false;
            isAppRunning = false;
            return true;
        }
        return false;
    }
}
