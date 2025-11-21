package render;

import java.awt.*;
import java.applet.*;

public class Loader {
	Applet theApplet;
	Graphics theCanvas;

	int x = 200;
	int y = 200;
	int w = 300;
	int h = 15;

	public int min = 0;
	public int max = 100;
	public int status = 0;
	public String strStatus = "Loading...";


	public Loader(Applet theApplet, Graphics theCanvas) {
		this.theApplet = theApplet;
		this.theCanvas = theCanvas;
	}

	public void increase(int iValue) {
		status += iValue;
		if (this.status > this.max) this.status = this.max;
	}

	public void draw() {
        this.x = (theApplet.getWidth() - 300) / 2;
        this.y = (theApplet.getHeight() / 2);
        
		double coef = (double)(status - min) / (double)(max - min);
		int level = (int)((double)w * coef);

		theCanvas.setColor(new Color(255, 255, 255));
		theCanvas.drawRect(x - 1, y - 1, w + 2, h + 2);

		theCanvas.setColor(new Color(110, 110, 110));
		theCanvas.fillRect(x, y, level, h);
		theCanvas.setColor(new Color(80, 80, 80));
		theCanvas.fillRect(x + level, y, w - level, h);
	

		FontMetrics FM = theCanvas.getFontMetrics();
		int sx = (this.w - FM.stringWidth(this.strStatus)) / 2;

		theCanvas.setColor(new Color(255, 255, 255));
		theCanvas.drawString(strStatus, x + sx, y + h - 5);
	}
}