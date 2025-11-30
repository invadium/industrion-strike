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
        
        int width  = this.getWidth();
        int height = this.getHeight();
        
        // Enable whatever acceleration Swing can provide
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                           RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.getHSBColor(.1f, .05f, 0.1f));
        g2d.fillRect(0, 0, width, height);
        
        // Your drawing code here
        g2d.setColor(Color.getHSBColor(.5f, .5f, .4f));
        g2d.fillRect(50, 50, 100, 100);

        g2d.setColor(Color.RED);
        g2d.drawString("Hello Canvas!", width/2, height/2);
    }
    
}
