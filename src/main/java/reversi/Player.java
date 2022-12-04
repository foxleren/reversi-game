package reversi;

import exceptions.MoveBackException;
import exceptions.NotFoundMoveException;
import utils.Printer;
import utils.Reader;

import java.util.Arrays;
import java.util.Stack;

import static reversi.Reversi.*;
import static utils.ArrayUtils.*;

public class Player implements Playable {
    private final int indexOfUser;

    private int userScore = 0;

    private final Stack<Board> boardBackup = new Stack<>();

    public int getIndexOfUser() {
        return indexOfUser;
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
        if (Reader.tryReadBack(y)) {
            throw new MoveBackException("Tried to make back move...");
        }
        return new Reversi.MoveCoords(x - 1, y - 1);
    }

    @Override
    public Reversi.MoveCoords getBotCoords(Board board) throws NotFoundMoveException {
        if (getGameConfig().gameMode() == GameMode.PVE_NORMAL.getCode()) {
            return findCoordsForBeginnerBot(board);
        }
        return findCoordsForProfessionalBot(board);
    }

    private MoveCoords findCoordsForProfessionalBot(Board board) {
        Printer.printSystemMessage("PROFI BOT");
        float maxScore = -1000;
        int maxI = -1, maxJ = -1;
        float score = 0;
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                var arr = copyIntArrayByValue(board.getArr());
                var nextPosMoves = copyFloatArrayByValue(board.getPossibleMovesArr());
                if (board.getPossibleMovesArr()[i][j] > 0) {
                    score = board.getPossibleMovesArr()[i][j];
                    setIndexOfActiveUser(1);
                    setIndexOfInactiveUser(0);
                    conquer(i, j, arr);
                    arr[i][j] = 1;
                    setActiveUser();
                    removeAllPossibleMoves(arr);
                    validateMoves(arr, nextPosMoves);
                    setPossibleMoves(arr, nextPosMoves);
                    score -= findMaxInFloatArray(nextPosMoves);
                    if (score > maxScore) {
                        maxScore = score;
                        maxI = i;
                        maxJ = j;
                    }
                }
            }
        }
        setActiveUser();
        return new MoveCoords(maxI, maxJ);
    }

    private MoveCoords findCoordsForBeginnerBot(Board board) {
        Printer.printSystemMessage("BEGINNER BOT");
        var possibleMoves = board.getPossibleMovesArr();
        float maxScore = 0;
        int maxI = -1, maxJ = -1;
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                if (possibleMoves[i][j] > maxScore) {
                    maxScore = possibleMoves[i][j];
                    maxI = i;
                    maxJ = j;
                }
            }
        }
        return new MoveCoords(maxI, maxJ);
    }

    @Override
    public void makeMove(Reversi.MoveCoords coords, Board board) {
        int x = coords.x(), y = coords.y();
        if (!isMoveValid(x, y)) {
            throw new IllegalArgumentException("Error: move isn't possible!");
        }
        conquer(x, y, board.getArr());
        addMoveToBoard(x, y, indexOfUser);
    }

    @Override
    public Board makeMoveBack() {
        Board tmp;
        if (boardBackup.size() > 1) {
            tmp = boardBackup.get(boardBackup.size() - 2);
            boardBackup.pop();
            boardBackup.pop();
            return tmp;
        }
        tmp = boardBackup.peek();
        boardBackup.pop();
        return tmp;
    }

    @Override
    public void backupMove(Board board) {
        var tmp = new Board(board);
        boardBackup.push(tmp);
    }
}
