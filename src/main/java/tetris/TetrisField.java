package tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

import tetromino.*;

public class TetrisField extends JPanel{
    private Tetris tetris;
    private KeyboardControl keyboardControl;

    private int HiddenRows = 24;
    private int rows = 20;
    private int cols = 10;

    private int gridThickness = 1;
    private int squareSize = 35;
    private int height = squareSize * rows + gridThickness * rows + 1;
    private int width = squareSize * cols + gridThickness * cols + 1;

    private Tetromino curTetromino;
    private Color[][] colorsOfLocked;
 
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    public TetrisField() {
        setBackground(new Color(240, 240, 240));
        setFocusable(true);

        tetris = new Tetris(this); 
        keyboardControl = new KeyboardControl(tetris);
        addKeyListener(keyboardControl);
    }

    // Main painiting component
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        paintGrid(g);
        paintTetromino(g);
        paintLockedTetrominoes(g);
    }

    // Paint methods
    void paintGrid(Graphics g) {
        g.setColor(Color.BLACK);

        // Paint verticales
        for (int i = 0; i < 11; i++) {
            g.fillRect(i * squareSize + i * gridThickness, 0, gridThickness, height);
        }

        // Paint horizontales
        for (int i = 0; i < HiddenRows + 1; i++) {
            g.fillRect(0, i * squareSize + i, width, gridThickness);
        }
    }

    void paintTetromino(Graphics g) {
        int[][] piece = curTetromino.getPiece();
        int x, y;

        g.setColor(curTetromino.getColor());
        for (int i = 0; i < piece.length; i++) {
            for (int j = 0; j < piece[i].length; j++) {
                if (piece[i][j] > 0) {
                    x = curTetromino.getX() * squareSize + j * squareSize + j * gridThickness + curTetromino.getX() * gridThickness + gridThickness;
                    y = (rows - curTetromino.getY()) * squareSize + i * squareSize + i * gridThickness;
                    y += (rows - curTetromino.getY()) * gridThickness + gridThickness;
                    g.fillRect(x, y, squareSize, squareSize);
                } 
            }
        }
    }

    void paintLockedTetrominoes(Graphics g) {
        int x, y;

        if (colorsOfLocked != null) {
            for (int i = 0; i < colorsOfLocked.length; i++) {
                for (int j = 0; j < colorsOfLocked[0].length; j++) {
                    if (colorsOfLocked[i][j] != null) {
                        x = j * squareSize + j * gridThickness + gridThickness;
                        y = (rows - i) * squareSize + (rows - i) * gridThickness + gridThickness;
                        g.setColor(colorsOfLocked[i][j]);
                        g.fillRect(x, y, squareSize, squareSize);
                    }
                }
            }
        }

    }

    void gameOver() {
        removeKeyListener(keyboardControl);        
    }

    // Getters and setters
    public int getHiddenRows() {
        return HiddenRows;
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public void setCurrentTetromino(Tetromino t) {
        this.curTetromino = t;
    } 

    public void setColorsOfLocked(Color[][] colorsOfLocked) {
        this.colorsOfLocked = colorsOfLocked;
    } 
}    

