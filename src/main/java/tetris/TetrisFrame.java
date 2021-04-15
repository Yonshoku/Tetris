package tetris;

import java.awt.BorderLayout;
import javax.swing.JFrame;

public class TetrisFrame extends JFrame{
    public TetrisFrame() {
        super();
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        

        setLayout(new BorderLayout());

        TetrisField field = new TetrisField();
        
        add(field, BorderLayout.WEST);
        add(new SidePanel(), BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
    }

}

