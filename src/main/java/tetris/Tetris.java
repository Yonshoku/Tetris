// fix game over
// getFullLines()
// deleteFullLine()
// delete used curTetromino
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

    private TimerTask lockTimerTask; 
    private long lockDelay = 500L;
    private boolean lockStarted = false;

    private Timer maxLockXDurationTimer = new Timer();
    private Timer maxLockYDurationTimer = new Timer();
    private long maxLockXDuration = 500L;
    private long maxLockYDuration = 5000L;
    private int lockX, lockY;

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

        // New fall
        newTetromino();
        fallTimer = new Timer();
        fallTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                move(Direction.DOWN);
                processMove(Direction.DOWN);
            }
        }, 0, fallPeriod);
    }

    void restartFallTick() {
        fallTimer.cancel();
        fallTimer.purge();

        fallTimer = new Timer();
        fallTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                move(Direction.DOWN);
                processMove(Direction.DOWN);
            }
        }, 0, fallPeriod);
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

    }

    void processMove(Direction dir) {
        if (dir == Direction.DOWN) { 
            if (intersectsWithBottom() || intersectsWithLockedTetromino()) {
                move(Direction.UP);
            // Tetromino moved down, continue fall
            } else if (lockStarted) {
                stopLock();
            }

            // Start lock if something underneath
            move(Direction.DOWN);
            if ((intersectsWithBottom() || intersectsWithLockedTetromino()) && !lockStarted) {
                startLock();
                lockStarted = true;
            }
            move(Direction.UP);

        } else if (dir == Direction.LEFT) {
            if (intersectsWithWalls() || intersectsWithLockedTetromino()) {
                move(Direction.RIGHT);
            } else if (lockStarted) {
                stopLock();
                startLock();
                lockStarted = true;
            }
        } else if (dir == Direction.RIGHT) {
            if (intersectsWithWalls() || intersectsWithLockedTetromino()) {
                move(Direction.LEFT);
            } else if (lockStarted) {
                stopLock();
                startLock();
                lockStarted = true;
            }
        }


        field.repaint();
    }

    void startLock() {
        stopLock();

        lockTimerTask = new TimerTask() {
            public void run() {
                lock();
                newFall();
                lockStarted = false;
            }
        };
        
        fallTimer.schedule(lockTimerTask, lockDelay);
    }

    void stopLock() {
        if (lockTimerTask != null)
            lockTimerTask.cancel();

        lockStarted = false;
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

    int[] getFullLinesNums() {
        int fullLinesNum = 0;
        boolean full;

        // Count full lines
        for (int i = 0; i < colorsOfLocked.length; i++) {
            full = true;

            for (int j = 0; j < colorsOfLocked[i].length; j++) {
                if(colorsOfLocked[i][j] == null) {
                    full = false;
                    continue;
                }
            }

            if (full) {
                fullLinesNum++;
            }
        } 

        System.out.println("num is: " + fullLinesNum);

        int[] fullLinesNums = new int[fullLinesNum];
        int num = 0;
        for (int i = 0; i < colorsOfLocked.length; i++) {
            full = true;

            for (int j = 0; j < colorsOfLocked[i].length; j++) {
                if (colorsOfLocked[i][j] == null) {
                    full = false;
                    continue;
                }
            }

            if (full) {
                fullLinesNums[num] = i;
                num++;
            }
        }

        return fullLinesNums;
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
