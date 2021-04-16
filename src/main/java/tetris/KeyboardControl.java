package tetris;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import java.awt.event.*;

public class KeyboardControl implements KeyListener{
    private Tetris tetris;

    private long beforeAutoshiftDelay = 135L;
    private long autoshiftPeriod = 70L;
    private Timer leftAutoshiftTimer, downAutoshiftTimer, rightAutoshiftTimer;
    private String leftAutoshiftTimerState = "stopped";
    private String rightAutoshiftTimerState = "stopped";
    private String downAutoshiftTimerState = "stopped";

    private HashMap<Integer, String> controlMap = new HashMap<Integer, String>();

    public KeyboardControl(Tetris tetris) {
		this.tetris = tetris;

        initControlMap();
    } 

    void initControlMap() {
        // Move control
        controlMap.put(KeyEvent.VK_A, "moveLeft");
        controlMap.put(KeyEvent.VK_LEFT, "moveLeft");
        controlMap.put(KeyEvent.VK_D, "moveRight");
        controlMap.put(KeyEvent.VK_RIGHT, "moveRight");
        controlMap.put(KeyEvent.VK_S, "moveDown");
        controlMap.put(KeyEvent.VK_DOWN, "moveDown");

        // Rotate control
        controlMap.put(KeyEvent.VK_W, "rotateClockwise");
        controlMap.put(KeyEvent.VK_X, "rotateClockwise");
        controlMap.put(KeyEvent.VK_UP, "rotateClockwise");
        controlMap.put(KeyEvent.VK_Z, "rotateCounterClockwise");
        controlMap.put(KeyEvent.VK_CONTROL, "rotateCounterClockwise");
    }

    @Override 
    public void keyReleased(KeyEvent e) {
        String action = controlMap.get(e.getKeyCode());
        if (action == null)
            return;

        // Moving left was stopped
        if (action.equals("moveLeft")) {
            // Resume right shift if it was paused
            if (rightAutoshiftTimerState.equals("paused")) {
                rightAutoshiftTimer = continueAutoshift(new TimerTask() {
                    public void run() {
                        tetris.move(Direction.RIGHT);
                        tetris.processMove(Direction.RIGHT);
                    }
                });

                rightAutoshiftTimerState = "running";
            }

            // Stop this autoshift
            leftAutoshiftTimer = stopAutoshift(leftAutoshiftTimer);
            leftAutoshiftTimerState = "stopped";
        }

        // Moving right was stopped
        if (action.equals("moveRight")) {
            // Resume left shift if it was paused
            if (leftAutoshiftTimerState.equals("paused")) {
                leftAutoshiftTimer = continueAutoshift(new TimerTask() {
                    public void run() {
                        tetris.move(Direction.LEFT);
                        tetris.processMove(Direction.LEFT);
                    }
                });

                leftAutoshiftTimerState = "running";
            }

            // Stop this autoshift
            rightAutoshiftTimer = stopAutoshift(rightAutoshiftTimer);
            rightAutoshiftTimerState = "stopped";
        }

        // Moving down was stopped
        if (action.equals("moveDown")) {
            downAutoshiftTimer = stopAutoshift(downAutoshiftTimer);
            downAutoshiftTimerState = "stopped";
        }
    }

    @Override 
    public void keyTyped(KeyEvent e) {};

    @Override
    public void keyPressed(KeyEvent e) {
        if (!controlMap.containsKey(e.getKeyCode()))
            return;

        // Move left
        if (controlMap.get(e.getKeyCode()).equals("moveLeft")) {
            if (leftAutoshiftTimerState.equals("stopped")) {
                tetris.move(Direction.LEFT);
                tetris.processMove(Direction.LEFT);

                leftAutoshiftTimer = startAutoshift(new TimerTask() {
                    public void run() {
                        tetris.move(Direction.LEFT);
                        tetris.processMove(Direction.LEFT);
                    }
                }); 

                leftAutoshiftTimerState = "running";

                // Stop right autoshift while there is left shift
                if (rightAutoshiftTimerState.equals("running")) {
                    stopAutoshift(rightAutoshiftTimer);
                    rightAutoshiftTimerState = "paused";
                }
            }
        }

        // Move right
        if (controlMap.get(e.getKeyCode()).equals("moveRight")) {
            if (rightAutoshiftTimerState.equals("stopped")) {
                tetris.move(Direction.RIGHT);
                tetris.processMove(Direction.RIGHT);

                rightAutoshiftTimer = startAutoshift(new TimerTask() {
                    public void run() {
                        tetris.move(Direction.RIGHT);
                        tetris.processMove(Direction.RIGHT);
                    }
                });

                rightAutoshiftTimerState = "running";

                // Stop left autoshift while there is right shift
                // It will be resumed as soon as right shift will be done
                if (leftAutoshiftTimerState.equals("running")) { 
                    stopAutoshift(leftAutoshiftTimer);
                    leftAutoshiftTimerState = "paused";
                }
            }
        }

        // Move down
        if (controlMap.get(e.getKeyCode()).equals("moveDown")) {
            if (downAutoshiftTimerState.equals("stopped")) {
                tetris.move(Direction.DOWN);
                tetris.processMove(Direction.DOWN);


                downAutoshiftTimer = startAutoshift(new TimerTask() {
                    public void run() {
                        tetris.move(Direction.DOWN);
                        tetris.processMove(Direction.DOWN);
                    }
                });

                downAutoshiftTimerState = "running";
            }
        }

        // Rotate clockwise
        if (controlMap.get(e.getKeyCode()).equals("rotateClockwise")) {
            tetris.rotate(Direction.CLOCKWISE);
        }

        // Rotate counter clockwise
        if (controlMap.get(e.getKeyCode()).equals("rotateCounterClockwise")) {
            tetris.rotate(Direction.COUNTERCLOCKWISE);
        }
    };

    Timer startAutoshift(TimerTask task) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, beforeAutoshiftDelay, autoshiftPeriod);
        return timer;
    }

    Timer continueAutoshift(TimerTask task) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, autoshiftPeriod);
        return timer;
    }

    Timer stopAutoshift(Timer timer) {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }

        timer = null;
        return timer;
    }
    
}
