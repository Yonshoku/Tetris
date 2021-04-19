package tetromino;

import java.util.ArrayList;
import java.util.Random;

import tetris.TetrisField;

public class TetrominoFactory {
        
	private TetrisField field;
    private ArrayList<Tetromino> tetrominos = new ArrayList<Tetromino>();
    private ArrayList<Tetromino> bag = new ArrayList<Tetromino>();

    public TetrominoFactory(TetrisField field) {
		this.field = field;

        tetrominos.add(new I(field));
        tetrominos.add(new J(field));
        tetrominos.add(new O(field));
        tetrominos.add(new T(field));
        tetrominos.add(new Z(field));
        tetrominos.add(new L(field));
        tetrominos.add(new S(field));
    }

    public Object clone(){  
        try{  
            return super.clone();  
        }catch(Exception e){ 
            return null; 
        }
    }

    // Give the player each of 7 tetrominoes 
    public Tetromino getRandomTetromino() {
        if (bag.size() == 0) {
            bag = createBag();
        } 

        Tetromino r = bag.remove(new Random().nextInt(bag.size()));
        return r;
    }

    public ArrayList<Tetromino> createBag() {
        ArrayList<Tetromino> bag = new ArrayList<Tetromino>();

        for (Tetromino t : tetrominos) {
            bag.add((Tetromino) t.clone());
        }

        return bag;
    }
}

