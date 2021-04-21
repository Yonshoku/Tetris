package tetris;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class SidePanel extends JPanel{
    private int width = 200;
    private Insets sidePanelInsets = new Insets(20, 20, 0, 20);

    private static JLabel scoreNumLabel = new JLabel("0", JLabel.CENTER);
    private static JLabel levelNumLabel = new JLabel("1", JLabel.CENTER);

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, 0);
    }

    public SidePanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(sidePanelInsets));
        
        // Level panel
        JLabel levelWordLabel = new JLabel ("Level:", JLabel.CENTER);
        levelWordLabel.setFont(new Font(levelWordLabel.getName(), Font.BOLD, 30));
        levelWordLabel.setForeground(Color.WHITE);

        levelNumLabel.setFont(new Font(scoreNumLabel.getName(), Font.BOLD, 25));
        levelNumLabel.setForeground(Color.WHITE);

        JPanel levelPanel = new JPanel();

        levelPanel.setLayout(new BorderLayout());
        levelPanel.setBackground(new Color(20, 20, 20));
        levelPanel.setPreferredSize(new Dimension(width, 75)); 
        levelPanel.setMaximumSize(new Dimension(width, 75)); 

        levelPanel.add(levelWordLabel, BorderLayout.NORTH);
        levelPanel.add(levelNumLabel, BorderLayout.SOUTH);

        // Score panel
        JLabel scoreWordLabel = new JLabel("Score:", JLabel.CENTER);
        scoreWordLabel.setFont(new Font(scoreWordLabel.getName(), Font.BOLD, 30));
        scoreWordLabel.setForeground(Color.WHITE);

        scoreNumLabel.setFont(new Font(scoreNumLabel.getName(), Font.BOLD, 25));
        scoreNumLabel.setForeground(Color.WHITE);

        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new BorderLayout());
        scorePanel.setBackground(new Color(20, 20, 20));
        scorePanel.setPreferredSize(new Dimension(width, 75)); 
        scorePanel.setMaximumSize(new Dimension(width, 75)); 

        scorePanel.add(scoreWordLabel, BorderLayout.NORTH);
        scorePanel.add(scoreNumLabel, BorderLayout.SOUTH);

        add(scorePanel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(levelPanel);

        // Next tetromino panel
    }

    static void setScore(int score) {
        scoreNumLabel.setText(String.valueOf(score));
    }

    static void setLevel(int level) {
        levelNumLabel.setText(String.valueOf(level));
    }
}
