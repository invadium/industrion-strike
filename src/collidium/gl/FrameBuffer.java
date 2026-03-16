package collidium.gl;

import java.awt.image.*;

import collidium.mix.Context;

public class FrameBuffer {

    public boolean visible = true;

    public int width = 0, height = 0;

    public BufferedImage buffer;

    protected WritableRaster raster;

    public int pixels[];

    public FrameBuffer() {}

    private void createBuffer(int width, int height) {
        this.buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.raster = this.buffer.getRaster();
        this.pixels = new int[width * height];

        // DEBUG random-fill the framebuffer
        for (int i = 0; i < this.pixels.length; i++) {
            int x = i % width;
            int y = i / width;

            // fill in the xor pattern
            int r = (x^y)&0xff;
            int g = (x*2^y*2)&0xff;
            int b = (x*4^y*4)&0xff;
            this.pixels[i] = (0xFF << 24) | (r << 16) | (g << 8) | b;
            // color pattern
            // this.pixels[i] = (x * y) | ((x ^ y) << 8) | ((x + y) << 16);
            // random fill
            // this.pixels[i] = (int)(Math.random() * 255);
        }
    }

    public void adjust(Context ctx) {
        this.width = ctx.mix.env.width;
        this.height = ctx.mix.env.height;
        if (this.buffer == null || this.buffer.getWidth() != width
            || this.buffer.getHeight() != height) {
                this.createBuffer(width, height);
        }
    }

    public void syncOut() {
        this.buffer.getRGB(0, 0, width, height, null, 0, width);
    }

    public void syncIn() {
        this.buffer.setRGB(0, 0, width, height, pixels, 0, width);
    }

}
