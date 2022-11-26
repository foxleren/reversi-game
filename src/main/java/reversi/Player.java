package reversi;

import exceptions.MoveBackException;
import exceptions.NotFoundMoveException;
import utils.Printer;
import utils.Reader;

import java.util.Stack;

import static reversi.Reversi.addMoveToBoard;
import static reversi.Reversi.tryNextMove;

public class Player implements Playable {
    private int indexOfUser;

    private int sessionScore = 0;
    private int userScore = 0;
    private int lastScore = 0;

    private Stack<Board> boardBackup = new Stack<>();

    public int getIndexOfUser() {
        return indexOfUser;
    }

    public void setIndexOfUser(int indexOfUser) {
        this.indexOfUser = indexOfUser;
    }

    public int getUserScore() {
        return userScore;
    }

    public void setUserScore(int userScore) {
        this.userScore = userScore;
    }

    public Player(int indexOfUser) {
        this.indexOfUser = indexOfUser;
    }

    @Override
    public Reversi.MoveCoords getUserCoords() throws MoveBackException {
        Printer.printSystemMessage(String.format("User %d: enter move coords:", getIndexOfUser() + 1));
        System.out.print("i = ");
        int x = Reader.readData();
        if (Reader.tryReadBack(x)) {
            throw new MoveBackException("Tried to make back move...");
        }
        System.out.print("j = ");
        int y = Reader.readData();
        if (Reader.tryReadBack(x)) {
            throw new MoveBackException("Tried to make back move...");
        }
        return new Reversi.MoveCoords(x - 1, y - 1);
    }

    @Override
    public Reversi.MoveCoords getBotCoords(Board board) throws NotFoundMoveException {
        var arr = board.getArr();
        int size = board.getSize(), emptyValue = board.getEmptyValue();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (arr[i][j] == emptyValue) {
                    if (tryNextMove(i, j, false)) {
                        //System.out.printf("Bot try (%d %d)\n", i + 1, j + 1);
//                    checkMovePossibility(i, j, true);
                        return new Reversi.MoveCoords(i, j);
                    }
                }
            }
        }
        throw new NotFoundMoveException(String.format("User %d has skipped round", getIndexOfUser() + 1));
    }

    @Override
    public void makeMove(Reversi.MoveCoords coords) {
        int x = coords.x(), y = coords.y();
        if (!tryNextMove(x, y, false)) {
            throw new IllegalArgumentException("Error: move isn't possible!");
        }
        tryNextMove(x, y, true);
        addMoveToBoard(x, y, getIndexOfUser());
        setUserScore(getUserScore() + 1);
    }

    @Override
    public void makeMoveBack() {

    }

    @Override
    public void backupMove(Board board) {

    }
}
