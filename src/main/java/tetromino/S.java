package tetromino;

import tetris.TetrisField;

import java.awt.Color;

public class S extends Tetromino{

    public S(TetrisField field) {
        super(field, Color.GREEN, new int[][] {
            {0, 0, 0},
            {0, 1, 1},
            {1, 1, 0}
        });

        setX((field.getCols() / 2) - 2);
        setY(field.getHiddenRows() >= 23 ? 22 : field.getHiddenRows() - 1);
    }

}
