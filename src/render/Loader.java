package render;

import java.awt.*;
import java.applet.*;

public class Loader {
	Applet applet;
	Graphics canvas;

	int x = 200;
	int y = 200;
	int w = 300;
	int h = 15;

	public int min = 0;
	public int max = 100;
	public int status = 0;
	public String strStatus = "Loading...";


	public Loader(Applet applet, Graphics canvas) {
		this.applet = applet;
		this.canvas = canvas;
	}

	public void increase(int iValue) {
		status += iValue;
		if (this.status > this.max) this.status = this.max;
	}

	public void draw() {
        this.x = (applet.getWidth() - 300) / 2;
        this.y = (applet.getHeight() / 2);
        
		double coef = (double)(status - min) / (double)(max - min);
		int level = (int)((double)w * coef);

		canvas.setColor(new Color(255, 255, 255));
		canvas.drawRect(x - 1, y - 1, w + 2, h + 2);

		canvas.setColor(new Color(110, 110, 110));
		canvas.fillRect(x, y, level, h);
		canvas.setColor(new Color(80, 80, 80));
		canvas.fillRect(x + level, y, w - level, h);
	

		FontMetrics FM = canvas.getFontMetrics();
		int sx = (this.w - FM.stringWidth(this.strStatus)) / 2;

		canvas.setColor(new Color(255, 255, 255));
		canvas.drawString(strStatus, x + sx, y + h - 5);
	}
}