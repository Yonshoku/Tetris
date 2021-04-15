package tetromino;

import java.awt.Color;

import tetris.TetrisField;
import tetris.Direction;

public class Tetromino implements Cloneable{
    private TetrisField field;

    private Color color;
    private int x, y;
    private int[][] piece;

    public Tetromino(TetrisField field, Color color, int[][] piece) {
        this.field = field;
        this.color = color;
        this.piece = piece;
    }

    public void rotate(Direction dir) {
        int[][] piece = getPiece();
        int[][] rotatedPiece = new int[piece.length][piece[0].length];

        if (dir == Direction.CLOCKWISE) {
            for (int i = 0; i < piece.length; i++) {
                for (int j = 0; j < piece[i].length; j++) {
                    rotatedPiece[i][j] = piece[piece[i].length - j - 1][i];
                }
            }
        } else if (dir == Direction.COUNTERCLOCKWISE){
            for (int i = 0; i < piece.length; i++) {
                for (int j = 0; j < piece[i].length; j++) {
                    rotatedPiece[i][j] = piece[j][piece[i].length - i - 1];
                }
            }
        }

        this.piece = rotatedPiece;
    }

    @Override
    public Object clone(){  
        try{  
            return super.clone();  
        }catch(Exception e){ 
            return null; 
        }
    }


    // Getters and setters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    } 

    public int[][] getPiece() {
        return this.piece;
    }

    public void setPiece(int[][] piece) {
        this.piece = piece;
    } 

    public void setY(int y) {
        this.y = y;
    }

    public Color getColor() {
        return color;
    }

}
