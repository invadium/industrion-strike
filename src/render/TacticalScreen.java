package render;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import scene.Camera;
import scene.Scene;
import engine.Strike;

public class TacticalScreen {
	Strike theApplet;
	Graphics theCanvas;
	Helper theHelper = Helper.getInstance();
	Scene theScene;
	Camera theCamera;
	Font screenFont = new Font("Courier", Font.BOLD, 9);
	Font screenFontL = new Font("Courier", Font.PLAIN, 12);
	boolean visible = false;
	public int x = 10;
	public int y = 10;
	public int w = 200;
	public int h = 200;
	
	public TacticalScreen(Strike theApplet, Graphics theCanvas,
			Scene theScene, Camera theCamera) {
		this.theApplet = theApplet;
		this.theCanvas = theCanvas;
		this.theScene = theScene;
		this.theCamera = theCamera;
	}
	
	public void drawTacticalScreen() {
		if (!visible) return;
		
		theCanvas.setFont(screenFont);
		FontMetrics FM = theCanvas.getFontMetrics();
		
		theHelper.theCanvas.setColor(new Color(40, 0, 100));
		theHelper.drawArea(x, y, w, 20);
		
		String strValue = "Tactical: Friendly";
		theCanvas.setColor(new Color(0xff, 0xe0, 0x32));
		int sx = (this.w - FM.stringWidth(strValue)) / 2;
		theCanvas.drawString(strValue, x + sx, y + 12);
	}
	
	public void clearifyPosition(int w, int h) {
		
	}
	
	public boolean isVisible() {
		return this.visible;
	}
	
	public void setVisible() {
		this.visible = true;
		theApplet.clearifyPositions();
	}
}
