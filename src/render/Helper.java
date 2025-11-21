package render;

import java.awt.Color;
import java.awt.Graphics;

public class Helper {
	public static Helper instance;
	public Graphics theCanvas;
	
	public Helper(Graphics theCanvas) {
		this.theCanvas = theCanvas;
		instance = this;
	}
	
	public static Helper getInstance() {
		return instance;
	}
	
	private int acb = 40;
	public void drawArea(int x, int y, int w, int h) {
		Color baseColor = theCanvas.getColor();
		int r = baseColor.getRed();
		int g = baseColor.getGreen();
		int b = baseColor.getBlue();
		int sr = (r - acb) / h;
		int sg = (g - acb) / h;
		int sb = (b - acb) / h;
		
		for (int i = 0; i < h; i++) {
			Color color = new Color(r, g, b);
			r -= sr; g -= sg; b -= sb;
			theCanvas.setColor(color);
			theCanvas.drawLine(x, y + i, x + w - 1, y + i);
		}
	}
}
