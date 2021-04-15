package tetris;

import javax.swing.SwingUtilities;

public class App 
{
    public static void main( String[] args )
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TetrisFrame().setVisible(true);
            }
        });
    }
}
