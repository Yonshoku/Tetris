package tetromino;

import tetris.TetrisField;

import java.awt.Color;

public class O extends Tetromino{

    public O(TetrisField field) {
        super(field, Color.YELLOW, new int[][] {
            {1, 1},
            {1, 1}
        });

        setX((field.getCols() / 2) - 1);
        setY(field.getHiddenRows() >= 22 ? 21 : field.getHiddenRows() - 1);
    }

}
