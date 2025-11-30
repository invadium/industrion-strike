package collidium;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class ColliderFrame {

    public static void main(String[] args) {
        JFrame frame = new JFrame();

        // enable hardware acceleration when possible
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("sun.java2d.d3d", "true");
        
        frame.add(new Canvas3D());
        frame.setSize(800, 600);
        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);


        DisplayMode displayMode = new DisplayMode(640, 480, 32, 75);
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = environment.getDefaultScreenDevice();

        device.setFullScreenWindow(frame);

        // close window handler
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                device.setFullScreenWindow(null);
                System.exit(0);
            }
        });
    }
}
