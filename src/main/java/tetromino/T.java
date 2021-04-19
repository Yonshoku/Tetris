package tetromino;

import tetris.TetrisField;

import java.awt.Color;

public class T extends Tetromino{

    public T(TetrisField field) {
        super(field, new Color(154, 31, 236), new int[][] {
            {0, 1, 0},
            {1, 1, 1},
            {0, 0, 0}
        });

        setX((field.getCols() / 2) - 2);
        setY(field.getHiddenRows() >= 24 ? 23 : field.getHiddenRows() - 1);
    }

}
