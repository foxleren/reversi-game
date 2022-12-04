package utils;

import java.util.Scanner;

public class Reader {
    public enum ReaderCodes {
        CODE_EXIT(-1),

        CODE_BACK(-2),

        CODE_MENU(-3),

        CODE_UNKNOWN(-4);

        private final int code;

        ReaderCodes(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    public static boolean tryReadExit(int val) {
        return val == ReaderCodes.CODE_EXIT.code;
    }

    public static boolean tryReadBack(int val) {
        return val == ReaderCodes.CODE_BACK.code;
    }

    public static int readData() {
        Scanner sc = new Scanner(System.in);
        if (sc.hasNextInt()) {
            return sc.nextInt();
        }
        if (sc.hasNextLine()) {
            return readKeyWord(sc.nextLine());
        }
        return -1;
    }

    public static int readKeyWord(String word) {
        if (word.equals("exit")) {
            return ReaderCodes.CODE_EXIT.getCode();
        }
        if (word.equals("menu")) {
            return ReaderCodes.CODE_MENU.getCode();
        }
        if (word.equals("back")) {
            return ReaderCodes.CODE_BACK.getCode();
        }
        return ReaderCodes.CODE_UNKNOWN.getCode();
    }
}
