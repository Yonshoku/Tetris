package tetromino;

import tetris.TetrisField;

import java.awt.Color;

public class J extends Tetromino{

    public J(TetrisField field) {
        super(field, Color.BLUE, new int[][] {
            {1, 0, 0},
            {1, 1, 1},
            {0, 0, 0},
        });

        setX((field.getCols() / 2) - 2);
        setY(field.getHiddenRows() >= 22 ? 21 : field.getHiddenRows() - 1);
    }

}
