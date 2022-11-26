package reversi;

import utils.Printer;

public class Board {
    private final int size;
    private int[][] arr;
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
        // arr = new int[][]{{0, 0, 0, -1}, {1, 1, 0, 0}, {1, 1, 0, 0}, {1, 1, 1, 0}};
        //arr = new int[][]{{0, 0, 0, 0}, {0,0,0,0}, {1,1,1,1}, {1,1,1,1}};
        //arr = new int[][]{{0, 1, -1, 1}, {-1, 0, 1, 0}, {-1, 1, 0, -1}, {-1, -1, -1, -1}};
        // arr = new int[][]{{1, -1, 1, 0}, {-1, 1, 1, 0}, {-1, 0, 0, -1}, {-1, -1, -1, -1}};
//            arr = new int[][]{
//                    {1, 0, -1, -1},
//                    {-1, 1, 0, -1},
//                    {1, 1, 0, -1},
//                    {0, 1, 0, -1},
//            };
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
                                System.out.print(Printer.ANSI_GREEN + " @ " + Printer.ANSI_RESET + "|");
                            } else {
                                System.out.print(Printer.ANSI_YELLOW + " $ " + Printer.ANSI_RESET + "|");
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

    public int[][] getArr() {
        return arr;
    }

    public int getSize() {
        return size;
    }
}