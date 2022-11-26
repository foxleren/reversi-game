package utils;

public class Printer {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public Printer() {
    }

    public static void printKeyPressedMessage(String m) {
        System.out.println(ANSI_PURPLE + m + ANSI_RESET);
    }

    public static void printSystemMessage(String m) {
        System.out.println(ANSI_BLUE + m + ANSI_RESET);
    }

    public static void printSuccessMessage(String m) {
        System.out.println(ANSI_GREEN + m + ANSI_RESET);
    }

    public static void printExceptionMessage(String m) {
        System.out.println(ANSI_RED + m + ANSI_RESET);
    }

    public static void printMessageForUser1(String m) {
        System.out.println(ANSI_GREEN + m + ANSI_RESET);
    }

    public static void printMessageForUser2(String m) {
        System.out.println(ANSI_YELLOW + m + ANSI_RESET);
    }

    public static void printGameResult() {

    }

    public static void printUsersScore(int score1, int score2, int round) {
        System.out.printf("ROUND %d: " + ANSI_GREEN + "->User 1<- %d" + ANSI_RESET + " | " + ANSI_YELLOW + "%d ->User 2<-\n" + ANSI_RESET, round, score1, score2);
    }

    public static void printIntroduction() {
        System.out.println("Welcome to Reversi reversi.interfaces.Game!");
    }

    public static void printMenu() {
        System.out.println("--->Menu<--- \n1. Start game\n2. End game");
    }
}