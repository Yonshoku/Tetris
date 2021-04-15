// TODO: fix before lock delay
// fix game over
// getFullLines()
// deleteFullLine()
// change background
// make scoring
// make score and next tetromino layout elements
// music
// sounds <16-04-21, yourname> //

package tetris;

import java.util.Timer;
import java.util.TimerTask;
import java.awt.Color;

import tetromino.*;

public class Tetris {
	private TetrisField field;

    private Timer fallTimer = new Timer();
    private long fallPeriod = 500L;
    private long beforeLockDelay = 500L;

    private TetrominoFactory tetrominoFactory;
    private Tetromino curTetromino;
    private Color[][] colorsOfLocked;

    public Tetris(TetrisField field) {
		this.field = field;

        tetrominoFactory = new TetrominoFactory(field);
        colorsOfLocked = new Color[field.getHiddenRows()][field.getCols()];

        newFall();
    }

    void newFall() {
        // Clear previous fall
        fallTimer.cancel();
        fallTimer.purge();

        if (isGameOver()) {
            gameOver();
            return;
        }

        // Do new fall
        newTetromino();

        TimerTask fall = new TimerTask() {
            public void run() {
                move(Direction.DOWN);
                processMove(Direction.DOWN);
            }
        };

        fallTimer = new Timer();
        fallTimer.scheduleAtFixedRate(fall, 0, fallPeriod);
    }

    boolean intersectsWithWalls() {
        int[][] piece = curTetromino.getPiece();

        for (int i = 0; i < piece.length; i++) {
            for (int j = 0; j < piece[i].length; j++) {
                if (piece[i][j] > 0) {
                    // Walls
                    if (curTetromino.getX() + j < 0 || curTetromino.getX() + j > field.getCols() - 1) { 
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    boolean intersectsWithBottom() {
        int[][] piece = curTetromino.getPiece();
         
        for (int i = 0; i < piece.length; i++) {
            for (int j = 0; j < piece[i].length; j++) {
                if (piece[i][j] > 0) {
                    if (curTetromino.getY() - i <= 0) {// Bottom
                        return true;
                    }
                }
            }
        }
            
        return false;

    }

    boolean intersectsWithLockedTetromino() {
        int[][] piece = curTetromino.getPiece();

        for (int i = 0; i < piece.length; i++) {
            for (int j = 0; j < piece[i].length; j++) {
                if (piece[i][j] > 0) {
                    if (colorsOfLocked[curTetromino.getY() - i][curTetromino.getX() + j] != null) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    boolean intersects() {
        return intersectsWithWalls() || intersectsWithBottom() || intersectsWithLockedTetromino();
    }

    void lock() {
        int[][] piece = curTetromino.getPiece();

        for (int i = 0; i < piece.length; i++) {
            for (int j = 0; j < piece[i].length; j++) {
                if (piece[i][j] > 0) {
                    colorsOfLocked[curTetromino.getY() - i][curTetromino.getX() + j] = curTetromino.getColor();
                }
            }
        }

    }

    void newTetromino() {
        curTetromino = tetrominoFactory.getRandomTetromino();
        field.setCurrentTetromino(curTetromino);
        field.setColorsOfLocked(colorsOfLocked); 
    }

    void move(Direction dir) {
        // Try to move, if intersects then move back
        if (dir == Direction.DOWN) {
            curTetromino.setY(curTetromino.getY() - 1);

        } else if (dir == Direction.UP) {
            curTetromino.setY(curTetromino.getY() + 1);

        } else if (dir == Direction.LEFT) {
            curTetromino.setX(curTetromino.getX() - 1);

        } else if (dir == Direction.RIGHT) {
            curTetromino.setX(curTetromino.getX() + 1);

        }

        field.repaint();
    }

    void processMove(Direction dir) {
        // Move back if tetromino intersects with something
        if (dir == Direction.DOWN && (intersectsWithBottom() || intersectsWithLockedTetromino())) { 
            move(Direction.UP);

            // Make a delay before lock
            fallTimer.schedule(new TimerTask() {
                public void run() {
                    lock();
                    newFall();
                }
            }, beforeLockDelay);

        } else if (dir == Direction.LEFT && (intersectsWithWalls() || intersectsWithLockedTetromino())) {
            move(Direction.RIGHT);
        } else if (dir == Direction.RIGHT && (intersectsWithWalls() || intersectsWithLockedTetromino())) {
            move(Direction.LEFT);
        }
    }

    void rotate(Direction dir) {
        curTetromino.rotate(dir);
        processRotation(dir);
        field.repaint();
    }

    void processRotation(Direction dir) {
        if (dir == Direction.CLOCKWISE) {
            if (intersects()) {
                curTetromino.rotate(Direction.COUNTERCLOCKWISE);
            }

        } else if (dir == Direction.COUNTERCLOCKWISE) {
            if (intersects()) {
                curTetromino.rotate(Direction.CLOCKWISE);
            }
        }
    }

    boolean isGameOver() {
        // Check if the first row upper than visible field contains any tetrominoes
        for (int i = 0; i < colorsOfLocked[field.getRows()].length; i++) {
            if (colorsOfLocked[field.getRows() - 1][i] != null)
                return true;
        }

        return false;
    }

    void gameOver() {
        field.gameOver();
    }

}
