package render;

import java.awt.*;
import java.applet.*;

import scene.SpaceObject;

import engine.Area;
import engine.AreaControl;
import engine.AreaListener;
import engine.Strike;

public class Radar implements AreaListener {
	private static final String RADAR = "Radar";
	private static final int MAX = 1024;
	Strike applet;
	Graphics bufCanvas;
	Graphics appCanvas;

	Image imgRadar;
	// position
	public int x = 530;
	public int y = 10;
	public int h = 200;
	public int w = 200;
	
	int markCount = 0;
	int[] mark_x = new int[MAX];
	int[] mark_y = new int[MAX];
	int[] mark_index = new int[MAX]; 
	
	int h2, w2, h4, w4;
	final int maxZoom = 10000;
	final int minZoom = 200;
	final int zoomStep = 200;
	int zoom = 600;
	Font radarFont = new Font("Courier", Font.PLAIN, 9);

	public Radar(Strike applet, Graphics appCanvas) {		
		this.applet = applet;
		this.applet.areaControl.addArea(this, RADAR, AreaControl.RADAR, this.x,this.y, this.w, this.h);
		
		this.appCanvas = appCanvas;
		imgRadar = applet.createImage(w, h);
		this.bufCanvas = imgRadar.getGraphics();
		w2 = w / 2;
		h2 = h / 2;
		w4 = w / 4;
		h4 = h / 4;				
	}

	public void clear() {
		this.markCount = 0;		
		bufCanvas.setColor(Color.black);
		bufCanvas.fillRect(0, 0, w, h);	

		bufCanvas.setColor(Color.blue);
		bufCanvas.drawLine(w2, 0, w2, h);
		bufCanvas.drawLine(w4, 0, w4, h);
		bufCanvas.drawLine(w2 + w4, 0, w2 + w4, h);

		bufCanvas.drawLine(0, h2, w, h2);
		bufCanvas.drawLine(0, h4, w, h4);
		bufCanvas.drawLine(0, h2 + h4, w, h2 + h4);

		bufCanvas.drawRect(h2-10, w2-10, 20, 20);
	}
	
	public void addMark(int mindex, double x, double y, double z, int mark, Color mColor) {
		if (this.markCount >= MAX) return;
		
		int ix = (int)x / this.zoom + w2;
		int iy = (int)z / this.zoom + h2;
		int is = (int)y / this.zoom;
		iy = h - iy;
		
		bufCanvas.setColor(mColor);
		if (ix>0 && ix<w && iy+is>0 && iy+is<h) {
			// remember it
			mark_x[markCount] = ix;
			mark_y[markCount] = iy + is;
			mark_index[markCount] = mindex;
			markCount++;		
			
			bufCanvas.drawLine(ix, iy, ix, iy + is);
			bufCanvas.fillOval(ix - mark/2, iy+is-mark/2, mark, mark);
		}				
	}

	public void addTargeted(double x, double y, double z, int mark) {
		int ix = (int)x / this.zoom + w2;
		int iy = (int)z / this.zoom + h2;
		int is = (int)y / this.zoom;
		iy = h - iy;

		bufCanvas.setColor(Color.yellow);
		bufCanvas.drawRect(ix-mark/2-1, iy+is-mark/2-1, mark+2, mark+2);
	}

	public void drawRadar() {
		bufCanvas.setColor(Color.green);
		bufCanvas.setFont(radarFont);
		String strRange = "Range " + zoom*10 + "m";
		FontMetrics FM = bufCanvas.getFontMetrics();
		bufCanvas.drawString(strRange, w - FM.stringWidth(strRange), 10);

		appCanvas.drawImage(imgRadar, x, y, applet);
	}

	public void increaseZoom() {
		if (zoom > minZoom) zoom -= zoomStep;
	}

	public void decreaseZoom() {
		if (zoom < maxZoom) zoom += zoomStep;
	}
	
	public void areaClick(Area area, int cx, int cy, int clicks) {
         int min = 10000;
         int index = -1;
         for (int i = 0; i < markCount; i++) {
             int dx = mark_x[i] - cx;
             int dy = mark_y[i] - cy;
             if (dx < 0) dx *= -1;
             if (dy < 0) dy *= -1;
             int d = dx + dy;
             if (d < min) {
                 min = d;
                 index = i;
             }
         }
            
         if (index != -1 && mark_index[index] != applet.camera.iRelatedObject) {    
            if (clicks == 1) applet.selectTarget(mark_index[index]);
            else {
                applet.selectTarget(mark_index[index]);
                applet.taskScreen.setPrimary();
            }
		 }
	}
	
	public void areaRightClick(Area area, int cx, int cy, int clicks) {
        int min = 10000;
        int index = -1;
        for (int i = 0; i < markCount; i++) {
            int dx = mark_x[i] - cx;
            int dy = mark_y[i] - cy;
            if (dx < 0) dx *= -1;
            if (dy < 0) dy *= -1;
            int d = dx + dy;
            if (d < min) {
                min = d;
                index = i;
            }
        }
            
        if (index != -1 && mark_index[index] != applet.camera.iRelatedObject) {
            if (clicks == 1) {
                applet.selectTarget(mark_index[index]);
                if (applet.scene.Objects.isPlaced(mark_index[index])) {
                    applet.camera.iRelatedObject =
					    mark_index[index];
                        applet.media.auTeleport.play();
                }
            } else {
                applet.selectTarget(mark_index[index]);
                applet.taskScreen.setSecondary();
            }
		}
	}
	
	public void areaWheel(Area area, int value) {
		if (value > 0) {
			this.decreaseZoom();
			this.decreaseZoom();
		} else if (value < 0) {
			this.increaseZoom();
			this.increaseZoom();					
		}	
	}
	
	public void clearifyPosition(int w, int h) {
		this.x = w - this.w - 10;
		this.y = 10;
		
		// update active areas
		applet.areaControl.updateAreaCoords(
				RADAR, this.x,this.y, this.w, this.h);
	}
}