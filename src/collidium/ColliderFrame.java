package collidium;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import collidium.dna.MegaMix;
import collidium.mix.Mix;

public class ColliderFrame extends JFrame {

    private GraphicsEnvironment graphicsEnvironment;
    private GraphicsDevice device;

    private boolean isFullScreen = false;

    public ColliderFrame(String title) {
        super(title);

        this.setup();
    }

    public void showFrame() {
        this.setVisible(true);
    }

    public void hideFrame() {
        // this.setVisible(false);
        this.dispose();
    }

    private void enterFullScreen() {
        this.hideFrame();
        this.setResizable(false);
        if (this.device.isFullScreenSupported()) {
            // true fullscreen
            this.setUndecorated(true);
            this.device.setFullScreenWindow(this);
        } else {
            // just maximize
            this.setExtendedState(JFrame.MAXIMIZED_BOTH);
            this.setUndecorated(true);
        }
        this.isFullScreen = true;
        this.showFrame();
    }

    private void exitFullScreen() {
        this.hideFrame();
        if (device.isFullScreenSupported()) {
            device.setFullScreenWindow(null);
        }
        this.setUndecorated(false);
        this.setExtendedState(JFrame.NORMAL);
        this.setSize(800, 600);
        this.setResizable(true);
        this.isFullScreen = false;
        this.showFrame();
    }

    public void toggleFullScreen() {
        if (this.isFullScreen) this.exitFullScreen();
        else this.enterFullScreen();
    }

    private void setup() {
        // enable hardware acceleration when possible
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("sun.java2d.d3d", "true");

        Mix mix = new MegaMix();
        mix.init();
        this.add(new Surface(mix));
        this.setSize(800, 600);

        this.graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        this.device = graphicsEnvironment.getDefaultScreenDevice();

        this.setupTraps();

        this.enterFullScreen();

        if (device.isDisplayChangeSupported()) {
            System.out.println("Changing the display mode");
            DisplayMode displayMode = new DisplayMode(640, 480, 32, DisplayMode.REFRESH_RATE_UNKNOWN);
            device.setDisplayMode(displayMode);
        } else {
            System.out.println("Display mode change is not supported!");
        }

        this.showFrame();

    }

    private void setupTraps() {
        InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = this.getRootPane().getActionMap();

        // close window handler
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //device.setFullScreenWindow(null);
                System.exit(0);
            }
        });

        // F11 to toggle full screen
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), "toggleFullScreen");
        actionMap.put("toggleFullScreen", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleFullScreen();
            }
        });

        // ESC to exit
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "exit");
        actionMap.put("exit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //device.setFullScreenWindow(null);
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        ColliderFrame frame = new ColliderFrame("Collider");
    }
}
