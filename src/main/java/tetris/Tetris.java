// change background
// change colors
// print level
// draw 2 next tetrominos
// finish up scoring
// music
// fix gameOver
// sounds <16-04-21, yourname> //

package tetris;

import java.util.Timer;
import java.util.TimerTask;
import java.awt.Color;

import tetromino.*;

public class Tetris {
	private TetrisField field;

    private Timer fallTimer = new Timer();
    private boolean noFall = false;

    private TimerTask lockTimerTask; 
    private long lockDelay = 500L;
    private boolean lockStarted = false;

    private Timer maxLockSamePlaceDurationTimer = new Timer();
    private Timer maxLockSameRowDurationTimer = new Timer();
    private long maxLockSameRowDuration = 3000L;
    private long maxLockSamePlaceDuration = 510L;

    private TetrominoFactory tetrominoFactory;
    private Tetromino curTetromino;
    private Color[][] colorsOfLocked;

    private int level = 1;
    private int score = 0;
    private int levelUpGoal = 5;
    private int clearedLinesValues = 0;
    private long gameSpeed = 1000;

    public Tetris(TetrisField field) {
		this.field = field;

        tetrominoFactory = new TetrominoFactory(field);
        colorsOfLocked = new Color[field.getHiddenRows()][field.getCols()];

        newFall();
    }

    void newFall() {
        curTetromino = tetrominoFactory.getRandomTetromino();

        field.setCurrentTetromino(curTetromino);
        field.setColorsOfLocked(colorsOfLocked); 

        stopFallTimer();
        fallTimer = startFallTimer();
    }

    void stopFallTimer() {
        if (fallTimer != null) {
            fallTimer.cancel();
            fallTimer.purge();
            fallTimer = null;
        }
    }

    Timer startFallTimer() {
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (!noFall) {
                    move(Direction.DOWN);
                    processMove(Direction.DOWN);
                }
            }
        }, 0, gameSpeed);

        return timer;
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
                    try {
                        if (colorsOfLocked[curTetromino.getY() - i][curTetromino.getX() + j] != null) {
                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        continue;
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

        if (isGameOver()) {
            gameOver();
            return;
        }

        score();
        clearLines();
    }

    void score() {
        int linesToClear = getLinesToClearNum();
        
        if (linesToClear == 1) {
            clearedLinesValues += 1;
            score += level * 40;
        } else if (linesToClear == 2) {
            clearedLinesValues += 3;
            score += level * 100;
        } else if (linesToClear == 3) {
            clearedLinesValues += 5;
            score += level * 300;
        } else if (linesToClear == 4) {
            clearedLinesValues += 8;
            score += level * 1200;
        }

        if (clearedLinesValues >= levelUpGoal) {
            level += 1;
            levelUpGoal = level * 5;
            clearedLinesValues = 0;
        }

        SidePanel.setScore(score);
        gameSpeed = (long) (Math.pow(0.8 - ((level - 1) * 0.007), level - 1) * 1000);
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
                startMaxLockSamePlaceDurationTimer(curTetromino.getX(), curTetromino.getY() + 1);
                startMaxLockSameRowDurationTimer(curTetromino.getY() + 1);
                
            }
            move(Direction.UP);

        } else if (dir == Direction.LEFT) {
            if (intersectsWithWalls() || intersectsWithLockedTetromino()) {
                move(Direction.RIGHT);
            } else if (lockStarted) {
                stopLock();
                startLock();
            }
        } else if (dir == Direction.RIGHT) {
            if (intersectsWithWalls() || intersectsWithLockedTetromino()) {
                move(Direction.LEFT);
            } else if (lockStarted) {
                stopLock();
                startLock();
            }
        }

        field.repaint();
    }

    void startLock() {
        stopLock();
        lockStarted = true;

        lockTimerTask = new TimerTask() {
            public void run() {
                lock();
                newFall();
                stopMaxSameRowLockDurationTimer();
                stopMaxLockSamePlaceDurationTimer();
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

    void startMaxLockSameRowDurationTimer(final int lockY) {
        stopMaxSameRowLockDurationTimer(); 

        maxLockSameRowDurationTimer = new Timer();
        maxLockSameRowDurationTimer.schedule(new TimerTask() {
            public void run () {
                if (lockY == curTetromino.getY()) {
                    stopLock();
                    stopMaxLockSamePlaceDurationTimer();
                    lock();
                    newFall();
                }
            }
        }, maxLockSameRowDuration);
    }

    void stopMaxSameRowLockDurationTimer() {
        if (maxLockSameRowDurationTimer != null) {
            maxLockSameRowDurationTimer.cancel();
            maxLockSameRowDurationTimer.purge();
            maxLockSameRowDurationTimer = null;
        }
    }

    void startMaxLockSamePlaceDurationTimer(final int lockX, final int lockY) {
        stopMaxLockSamePlaceDurationTimer();

        maxLockSamePlaceDurationTimer = new Timer();
        maxLockSamePlaceDurationTimer.schedule(new TimerTask() {
            public void run () {
                if (lockX == curTetromino.getX() && lockY == curTetromino.getY()) {
                    stopLock();
                    stopMaxSameRowLockDurationTimer();
                    lock();
                    newFall();
                }
            }
        }, maxLockSamePlaceDuration);
    }

    void stopMaxLockSamePlaceDurationTimer() {
        if (maxLockSamePlaceDurationTimer != null) {
            maxLockSamePlaceDurationTimer.cancel();
            maxLockSamePlaceDurationTimer.purge();
            maxLockSamePlaceDurationTimer = null;
        } 
    }

    void stopAllLockTimers() {
        stopLock();
        stopMaxLockSamePlaceDurationTimer();
        stopMaxSameRowLockDurationTimer();
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
            } else if (lockStarted) {
                stopLock();
            }

        } else if (dir == Direction.COUNTERCLOCKWISE) {
            if (intersects()) {
                curTetromino.rotate(Direction.CLOCKWISE);
            } else if (lockStarted) {
                stopLock();
            }
        }
    }

    void harddrop() {
        while (!intersectsWithBottom() && !intersectsWithLockedTetromino()) {
            move(Direction.DOWN);
        }
        move(Direction.UP);
        stopAllLockTimers();
        lock();
        newFall();
    }

    int getLinesToClearNum() {
        int linesToClear = 0;
        boolean toClear = false;

        // Count full lines
        for (int i = 0; i < colorsOfLocked.length; i++) {
            toClear = true;

            for (int j = 0; j < colorsOfLocked[i].length; j++) {
                if(colorsOfLocked[i][j] == null) {
                    toClear = false;
                    continue;
                }
            }

            if (toClear) 
                linesToClear++;
        } 

        return linesToClear;
    }

    void clearLines() {
        boolean toClear = false;
        for (int i = colorsOfLocked.length - 1; i >= 0; i--) {
            toClear = true;

            for (int j = 0; j < colorsOfLocked[i].length; j++) {
                if (colorsOfLocked[i][j] == null) {
                    toClear = false;
                    continue;
                }
            }

            // Move down 1 line all lines from top to here 
            if (toClear) {
                for (int j = i; j < colorsOfLocked.length - 1; j++) {
                    for (int k = 0; k < colorsOfLocked[j].length; k++) {
                        colorsOfLocked[j][k] = colorsOfLocked[j + 1][k]; 
                    }
                }
            }

        }
    }

    boolean isGameOver() {
        // Check if the first row upper than visible field contains any tetrominoes
        for (int i = 0; i < colorsOfLocked[field.getRows()].length; i++) {
            if (colorsOfLocked[field.getRows() - 1][i] != null)
                return intersectsWithLockedTetromino();
        }

        return false;
    }

    void gameOver() {
        field.gameOver();
        stopAllLockTimers();
        stopFallTimer();
        noFall = true;
        System.out.println("game over");
    }

    // Getters and setters
    void setNoFall(boolean noFall) {
        this.noFall = noFall; 
    }

}
