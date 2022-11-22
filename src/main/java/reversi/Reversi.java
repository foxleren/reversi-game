package reversi;

import java.util.Scanner;

public class Reversi {
    private static GameConfig gameConfig;
    private static boolean isGameRunning = false;
    private static boolean isConfigReady = false;
    private static Board board;
    private static int indexOfUser = 0;

    public Reversi() {
    }

    private void initGameConfig() {
        try {
            System.out.print("Enter size of game board (4 <= size <= 8): ");
            Scanner sc = new Scanner(System.in);
            gameConfig = new GameConfig(sc.nextInt());
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
            MoveCoords userCoords = getMoveCoords();
            makeMove(userCoords);
            indexOfUser ^= 1;
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }
    }

    record MoveCoords(int x, int y) {
        MoveCoords {
            if (x < 0 || x >= board.size || y < 0 || y >= board.size) {
                throw new IllegalArgumentException("Error: incorrect coords.");
            }
            if (board.arr[x][y] != board.getEmptyValue()) {
                throw new IllegalArgumentException("Error: this coords have already been occupied.");
            }
        }
    }

    private MoveCoords getMoveCoords() {
        Scanner sc = new Scanner(System.in);
        System.out.printf("User %d: enter move coords(int int):\n", indexOfUser + 1);
        int x = sc.nextInt(), y = sc.nextInt();
        return new MoveCoords(x - 1, y - 1);
    }

    private void makeMove(MoveCoords coords) {
        board.setValue(coords.x, coords.y, indexOfUser + 1);
    }

    private static class Board {
        private int size;
        private final int[][] arr;
        private final int emptyValue = 0;

        public Board(int size) {
            this.size = size;
            this.arr = new int[size][size];
            setDefaultPosition();
        }

        public int getEmptyValue() {
            return emptyValue;
        }

        private void setDefaultPosition() {
            arr[size / 2 - 1][size / 2 - 1] = 1;
            arr[size / 2][size / 2] = 1;
            arr[size / 2 - 1][size / 2] = 2;
            arr[size / 2][size / 2 - 1] = 2;
        }

        private void setDefaultBoard() {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    arr[i][j] = emptyValue;
                }
            }
        }

        public void setValue(int x, int y, int value) {
            arr[x][y] = value;
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
                                System.out.printf(" %d |", el);
                            } else {
                                System.out.print("   |");
                            }
                        }
                    }
                }
                System.out.println();
            }
        }

        private Boolean isAble(int x, int y) {
//            int active = 1;
//
//            if (x < size - 1 && arr[x + 1][y] != emptyValue && arr[x + 1][y] != active) {
//                return true;
//            }
//            if (y < size - 1 && arr[x][y + 1] != emptyValue && arr[x][y + 1] != active) {
//                return true;
//            }
//            if (x < size - 1 && y < size - 1 && arr[x + 1][y + 1] != emptyValue && arr[x + 1][y + 1] != active) {
//                return true;
//            }
//            if (1 < x && arr[x - 1][y] != emptyValue && arr[x - 1][y] != active) {
//                return true;
//            }
//            if (1 < y && arr[x][y - 1] != emptyValue && arr[x][y - 1] != active) {
//                return true;
//            }
//            if (1 < x && 1 < y && arr[x - 1][y - 1] != emptyValue && arr[x - 1][y - 1] != active) {
//                return true;
//            }
//            if (x < size - 1 && 1 < y && arr[x + 1][y - 1] != emptyValue && arr[x + 1][y - 1] != active) {
//                return true;
//            }
//            if (1 < x && y < size - 1 && arr[x - 1][y + 1] != emptyValue && arr[x - 1][y + 1] != active) {
//                return true;
//            }
//            return false;
            return true;
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
