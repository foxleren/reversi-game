package utils;

import java.util.Scanner;

public class Reader {
    private static final int CODE_EXIT = -1;
    private static final int CODE_BACK = -2;
    private static final int CODE_MENU = -3;
    private static final int CODE_UNKNOWN = -4;

    public static boolean tryReadExit(int val) {
        //Printer.printSystemMessage("You have left game. Have a nice day.");
        return val == CODE_EXIT;
    }

    public static boolean tryReadBack(int val) {
        //System.out.println("Pressed MOVE BACK");
        //makeMoveBack();
        return val == CODE_BACK;
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
            //Printer.printSystemMessage("Pressed EXIT");
            //isAppRunning = false;
            return CODE_EXIT;
        }
        if (word.equals("menu")) {
            //Printer.printSystemMessage("Pressed MENU");
            //isGameRunning = false;
            return CODE_MENU;
        }
        if (word.equals("back")) {
            //Printer.printSystemMessage("Pressed BACK");
            return CODE_BACK;
        }
        //Printer.printSystemMessage("Unknown command.");
        return CODE_UNKNOWN;
    }
}
