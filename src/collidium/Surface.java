package collidium;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.HierarchyEvent;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JPanel;

public class Surface extends JPanel implements Runnable {

    private final double evoStep = 0.008;
    private final double maxStep = 1;

    private Thread mixThread;
    private volatile boolean isRunning = false;

    private volatile double time = 0;

    private double x = 0, y = 0;

    public Surface() {
        super();

        setFocusable(true);
        requestFocusInWindow();

        // this.addKeyListener(new SurfaceTrap());

        // Start when visible
        this.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                if (isShowing()) {
                    startGame();
                } else {
                    stopGame();
                }
            }
        });
    }

    private void startGame() {
        if (this.isRunning) return;

        System.out.println("Starting the mix thread...");
        this.mixThread = new Thread(this);
        this.mixThread.start();
        this.isRunning = true;
    }

    private void stopGame() {
        if (this.mixThread == null) return;

        System.out.println("Stopping the mix thread...");
        try {
            this.mixThread.join(1000);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        this.isRunning = false;
    }

    private void evo(double dt) {
        this.x += 20 * dt;
        this.y += 20 * dt;
        // System.out.println("Time: " + this.time + " => " + dt);
    }

    protected void paintComponent(Graphics g) {
        // super.paintComponent(g);
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
        g2d.fillRect((int)this.x, (int)this.y, 100, 100);
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        g2d.drawString("Time: " + Math.floor(this.time), width/2, height/2);

        g.dispose();
    }

    @Override
    public void run() {
        long frames = 0;
        double lastEvoTime = (double)System.nanoTime() / 1_000_000_000f;

        while (this.isRunning) {
            double sysTime = (double)System.nanoTime() / 1_000_000_000f;
            double elapsed = sysTime - lastEvoTime;

            while (elapsed >= evoStep) {
                this.time += evoStep;
                this.evo(evoStep);
                elapsed -= evoStep;
                lastEvoTime += evoStep;
            }

            this.repaint();
            frames++;
        }

        try {
            // release CPU
            Thread.sleep(7);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
