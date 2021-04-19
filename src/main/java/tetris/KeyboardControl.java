package tetris;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import java.awt.event.*;

public class KeyboardControl implements KeyListener{
    private Tetris tetris;

    private long beforeAutoshiftDelay = 150L;
    private long autoshiftPeriod = 60L;
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

        // Others
        controlMap.put(KeyEvent.VK_SPACE, "harddrop");
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
                rightAutoshiftTimer = startAutoshiftTimer(Direction.RIGHT, 0);
                rightAutoshiftTimerState = "running";
            }

            // Stop this autoshift
            leftAutoshiftTimer = stopAutoshiftTimer(leftAutoshiftTimer);
            leftAutoshiftTimerState = "stopped";
        }

        // Moving right was stopped
        if (action.equals("moveRight")) {
            // Resume left shift if it was paused
            if (leftAutoshiftTimerState.equals("paused")) {
                leftAutoshiftTimer = startAutoshiftTimer(Direction.LEFT, 0);
                leftAutoshiftTimerState = "running";
            }

            // Stop this autoshift
            rightAutoshiftTimer = stopAutoshiftTimer(rightAutoshiftTimer);
            rightAutoshiftTimerState = "stopped";
        }

        // Moving down was stopped
        if (action.equals("moveDown")) {
            downAutoshiftTimer = stopAutoshiftTimer(downAutoshiftTimer);
            downAutoshiftTimerState = "stopped";

            tetris.setNoFall(false);
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

                leftAutoshiftTimer = startAutoshiftTimer(Direction.LEFT, beforeAutoshiftDelay); 
                leftAutoshiftTimerState = "running";

                // Stop right autoshift while there is left shift
                if (rightAutoshiftTimerState.equals("running")) {
                    stopAutoshiftTimer(rightAutoshiftTimer);
                    rightAutoshiftTimerState = "paused";
                }
            }
        }

        // Move right
        if (controlMap.get(e.getKeyCode()).equals("moveRight")) {
            if (rightAutoshiftTimerState.equals("stopped")) {
                tetris.move(Direction.RIGHT);
                tetris.processMove(Direction.RIGHT);

                rightAutoshiftTimer = startAutoshiftTimer(Direction.RIGHT, beforeAutoshiftDelay);
                rightAutoshiftTimerState = "running";

                // Stop left autoshift while there is right shift
                // It will be resumed as soon as right shift will be done
                if (leftAutoshiftTimerState.equals("running")) { 
                    stopAutoshiftTimer(leftAutoshiftTimer);
                    leftAutoshiftTimerState = "paused";
                }
            }
        }

        // Move down
        if (controlMap.get(e.getKeyCode()).equals("moveDown")) {
            if (downAutoshiftTimerState.equals("stopped")) {
                tetris.move(Direction.DOWN);
                tetris.processMove(Direction.DOWN);
                tetris.setNoFall(true);

                downAutoshiftTimer = startAutoshiftTimer(Direction.DOWN, beforeAutoshiftDelay);
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

        // Harddrop 
        if (controlMap.get(e.getKeyCode()).equals("harddrop")){
            tetris.harddrop();
        }
    };

    Timer startAutoshiftTimer(final Direction dir, long beforeAutoshiftDelay) {
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                tetris.move(dir);
                tetris.processMove(dir);
            }
        }, beforeAutoshiftDelay, autoshiftPeriod);

        return timer;
    }

    Timer stopAutoshiftTimer(Timer timer) {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }

        timer = null;
        return timer;
    }
    
}
