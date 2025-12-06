package collidium;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.HierarchyEvent;
import java.awt.Color;

import javax.swing.JPanel;

import collidium.mix.Context;
import collidium.mix.Mix;

public class Surface extends JPanel implements Runnable {

    private final float evoStep = 0.008f;
    private final double maxStep = 1;

    private Thread mixThread;
    private volatile boolean isRunning = false;

    private volatile float time = 0;

    private double x = 0, y = 0;

    private Mix mix;

    public Surface(Mix mix) {
        super();

        this.mix = mix;

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

    private void evo(float dt) {
        this.mix.evo(dt);
    }

    protected void paintComponent(Graphics g) {
        // super.paintComponent(g);

        // setup the mix environment
        int width  = this.getWidth();
        int height = this.getHeight();
        this.mix.env.width = width;
        this.mix.env.height = height;

        // create a drawing context?
        Graphics2D g2d = (Graphics2D) g;
        Context ctx = new Context(this.mix, g2d);

        // Enable whatever acceleration Swing can provide
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                           RenderingHints.VALUE_ANTIALIAS_ON);


        this.mix.draw(ctx);

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
                this.mix.env.time = this.time;
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
