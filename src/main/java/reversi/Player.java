package reversi;

import exceptions.MoveBackException;
import exceptions.NotFoundMoveException;
import utils.Printer;
import utils.Reader;

import java.util.Arrays;
import java.util.Stack;

import static reversi.Reversi.*;

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
        int maxScore = 0;
        int maxI = -1, maxJ = -1;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (arr[i][j] == 3) {
                    arr[i][j] = -1;
                }
                if (arr[i][j] == emptyValue) {
                    var score = tryNextMove(i, j, false);
                    if (score > maxScore) {
                        //System.out.printf("Bot try (%d %d)\n", i + 1, j + 1);
//                    checkMovePossibility(i, j, true);
                        maxScore = score;
                        maxI = i;
                        maxJ = j;
                        //return new Reversi.MoveCoords(i, j);
                    }
                }
            }
        }
        if (maxScore > 0) {
            return new MoveCoords(maxI, maxJ);
        }
        throw new NotFoundMoveException(String.format("User %d has skipped round", getIndexOfUser() + 1));
    }

    @Override
    public void makeMove(Reversi.MoveCoords coords) {
        int x = coords.x(), y = coords.y();
        if (tryNextMove(x, y, false) == 0) {
            throw new IllegalArgumentException("Error: move isn't possible!");
        }
        tryNextMove(x, y, true);
        addMoveToBoard(x, y, getIndexOfUser());
        setUserScore(getUserScore() + 1);
    }

    @Override
    public Board makeMoveBack() {
        if (boardBackup.size() > 1) {
            var b = boardBackup.get(boardBackup.size() - 2);
            boardBackup.pop();
            boardBackup.pop();
            //return boardBackup.get(boardBackup.size() - 2);
            return b;
        }
        var b = boardBackup.peek();
        //return boardBackup.peek();
        boardBackup.pop();
        return b;
    }

    @Override
    public void backupMove(Board board) {
        var arr = board.getArr();
        int[][] b = new int[board.getSize()][board.getSize()];
        for (int i = 0; i < board.getSize(); i++) {
            System.arraycopy(arr[i], 0, b[i], 0, board.getSize());
        }
        Board tmp = new Board(board.getSize());
        tmp.setArr(b);
        tmp.setPossibleMoveCount(board.getPossibleMoveCount());
        boardBackup.push(tmp);
    }
}
