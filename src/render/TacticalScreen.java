package render;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import scene.Camera;
import scene.Scene;
import engine.Strike;

public class TacticalScreen {
	Strike applet;
	Graphics canvas;
	Helper helper = Helper.getInstance();
	Scene scene;
	Camera camera;
	Font screenFont = new Font("Courier", Font.BOLD, 9);
	Font screenFontL = new Font("Courier", Font.PLAIN, 12);
	boolean visible = false;
	public int x = 10;
	public int y = 10;
	public int w = 200;
	public int h = 200;
	
	public TacticalScreen(Strike applet, Graphics canvas,
			Scene scene, Camera camera) {
		this.applet = applet;
		this.canvas = canvas;
		this.scene = scene;
		this.camera = camera;
	}
	
	public void drawTacticalScreen() {
		if (!visible) return;
		
		canvas.setFont(screenFont);
		FontMetrics FM = canvas.getFontMetrics();
		
		helper.canvas.setColor(new Color(40, 0, 100));
		helper.drawArea(x, y, w, 20);
		
		String strValue = "Tactical: Friendly";
		canvas.setColor(new Color(0xff, 0xe0, 0x32));
		int sx = (this.w - FM.stringWidth(strValue)) / 2;
		canvas.drawString(strValue, x + sx, y + 12);
	}
	
	public void clearifyPosition(int w, int h) {
		
	}
	
	public boolean isVisible() {
		return this.visible;
	}
	
	public void setVisible() {
		this.visible = true;
		applet.clearifyPositions();
	}
}
