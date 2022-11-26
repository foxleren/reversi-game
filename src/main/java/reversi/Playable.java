package reversi;

import exceptions.MoveBackException;
import exceptions.NotFoundMoveException;
import reversi.Board;
import reversi.Reversi;

public interface Playable {
    Reversi.MoveCoords getUserCoords() throws MoveBackException;

    Reversi.MoveCoords getBotCoords(Board board) throws NotFoundMoveException;

    void makeMove(Reversi.MoveCoords coords);

    void makeMoveBack();

    void backupMove(Board board);
}
