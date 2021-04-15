package tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import javax.swing.JPanel;

public class SidePanel extends JPanel{
    private int width = 200;

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, 0);
    }

    public SidePanel() {
        setBackground(Color.GRAY);
        setLayout(new GridLayout());
    }
}
