package tetromino;

import tetris.TetrisField;

import java.awt.Color;

public class Z extends Tetromino{

    public Z(TetrisField field) {
        super(field, Color.RED, new int[][] {
            {0, 0, 0},
            {1, 1, 0},
            {0, 1, 1}
        });

        setX((field.getCols() / 2) - 2);
        setY(field.getHiddenRows() >= 23 ? 22 : field.getHiddenRows() - 1);
    }

}
