package tetromino;

import tetris.TetrisField;

import java.awt.Color;

public class I extends Tetromino{

    public I(TetrisField field) {
        super(field, Color.CYAN, new int[][] {
            {0, 0, 0, 0},
            {1, 1, 1, 1},
            {0, 0, 0, 0},
            {0, 0, 0, 0}
            
        });

        setX((field.getCols() / 2) - 2);
        setY(field.getHiddenRows() >= 24 ? 23 : field.getHiddenRows() - 1);
    }


}
