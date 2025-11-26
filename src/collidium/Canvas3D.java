package collidium;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import javax.swing.JPanel;

public class Canvas3D extends JPanel {
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable whatever acceleration Swing can provide
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                           RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Your drawing code here
        g2d.setColor(Color.RED);
        g2d.fillRect(50, 50, 100, 100);
    }
    
}
