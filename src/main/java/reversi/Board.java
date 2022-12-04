package reversi;

import utils.Printer;

import static utils.ArrayUtils.copyFloatArrayByValue;
import static utils.ArrayUtils.copyIntArrayByValue;

public class Board {
    private final int size;
    private final int[][] arr;

    private final float[][] possibleMovesArr;

    private int possibleMoveCount = 0;

    public enum BoardValues {
        USER1(0),

        USER2(1),

        EMPTY(-1),

        POSSIBLE(3);

        private final int code;

        BoardValues(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    public float[][] getPossibleMovesArr() {
        return possibleMovesArr;
    }


    public Board(int size) {
        this.size = size;
        this.arr = new int[size][size];
        this.possibleMovesArr = new float[size][size];
        setDefaultPosition();
    }

    public Board(Board other) {
        this.size = other.getSize();
        this.possibleMoveCount = other.possibleMoveCount;
        this.arr = copyIntArrayByValue(other.arr);
        this.possibleMovesArr = copyFloatArrayByValue(other.possibleMovesArr);
    }

    private void setDefaultPosition() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                arr[i][j] = BoardValues.EMPTY.code;
            }
        }

        arr[size / 2 - 1][size / 2 - 1] = 0;
        arr[size / 2][size / 2] = 0;
        arr[size / 2 - 1][size / 2] = 1;
        arr[size / 2][size / 2 - 1] = 1;
    }

    public void setPossibleMoveCount(int possibleMoveCount) {
        this.possibleMoveCount = possibleMoveCount;
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
                        if (el != BoardValues.EMPTY.code) {
                            if (el == 0) {
                                System.out.print(Printer.ANSI_GREEN + " @ " + Printer.ANSI_RESET + "|");
                            } else if (el == 1) {
                                System.out.print(Printer.ANSI_YELLOW + " $ " + Printer.ANSI_RESET + "|");
                            } else if (el == 3) {
                                System.out.print(" * " + "|");
                            }
                        } else {
                            System.out.print("   |");
                        }
                    }
                }
            }
            System.out.println();
        }
    }

    public int getPossibleMoveCount() {
        return possibleMoveCount;
    }

    public int[][] getArr() {
        return arr;
    }

    public int getSize() {
        return size;
    }
}