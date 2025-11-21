package render;

import java.applet.*;
import java.awt.*;
import java.util.*;
import math.*;
import scene.Camera;

public class Stars {
	Random R;
	Applet theApplet;
	Graphics theCanvas;
	Camera theCamera;
	final int maxStars = 50;
	final int maxColors = 4;
	int cntStars;
	int StarsX[];
	int StarsY[];
	int StarsC[];
	double ShiftX, ShiftY;
	Color SC0, SC1, SC2, SC3;

	public Stars(Applet theApplet, Graphics theCanvas, Camera theCamera) {
		this.theApplet = theApplet;
		this.theCanvas = theCanvas;
		this.theCamera = theCamera;
	}
    
    public void create() {
		//create stars
		StarsX = new int[maxStars];
		StarsY = new int[maxStars];
		StarsC = new int[maxStars];
		R = new Random();

		for (int i=0; i<maxStars; i++) {
			StarsX[i] = (int)(R.nextFloat() * theCamera.ScreenWidth);
			StarsY[i] = (int)(R.nextFloat() * theCamera.ScreenHeight);
			StarsC[i] = (int)(R.nextFloat() * maxColors);
		}
		cntStars = maxStars;

		//calculate shifts
		int alpha = (int)(CMath.M.atan2(theCamera.ScreenShiftY, theCamera.d) / CMath.dFactor);
		ShiftY =  (double)theCamera.ScreenShiftY / (double)alpha;
		int beta = (int)(CMath.M.atan2(theCamera.ScreenShiftX, theCamera.d) / CMath.dFactor);
		ShiftX =  (double)theCamera.ScreenShiftX / (double)beta;

		//create colors
		SC0 = new Color(255, 255, 255);
		SC1 = new Color(255, 150, 150);
		SC2 = new Color(255, 255, 150);
		SC3 = new Color(150, 150, 255);
    }
	
	public void updateCanvas(Graphics theCanvas) {
		this.theCanvas = theCanvas;
	}
	
	public void draw() {
		for(int i=0; i<cntStars; i++) {
			switch(StarsC[i]) {
			case 0:	theCanvas.setColor(SC0);
					break;
			case 1: theCanvas.setColor(SC1);
					break;
			case 2: theCanvas.setColor(SC2);
					break;
			case 3: theCanvas.setColor(SC3);
					break;
			}
			//theCanvas.setColor(Color.white);
			theCanvas.drawLine(StarsX[i], StarsY[i], StarsX[i], StarsY[i]);
		}
	}

	public void shiftX(int val) {
		int c = (int)((double)val * ShiftX);
		for (int i=0; i<cntStars; i++) {
			StarsX[i] += c;
			if (StarsX[i]<0 || StarsX[i]>theCamera.ScreenWidth) {
				//regenerate point
				if (c < 0) StarsX[i] = theCamera.ScreenWidth - (int)(R.nextFloat() * c);
					else StarsX[i] = (int)(R.nextFloat() * c);
				StarsY[i] = (int)(R.nextFloat() * theCamera.ScreenHeight);
				StarsC[i] = (int)(R.nextFloat() * maxColors);
			}
		}
	}

	public void shiftY(int val) {
		int c = (int)((double)val * ShiftY);
		for (int i=0; i<cntStars; i++) {
			StarsY[i] += c;
			if (StarsY[i]<0 || StarsY[i]>theCamera.ScreenHeight) {
				//regenerate point
				StarsX[i] = (int)(R.nextFloat() * theCamera.ScreenWidth);
				if (c < 0) StarsY[i] = theCamera.ScreenHeight - (int)(R.nextFloat() * c);
					else StarsY[i] = (int)(R.nextFloat() * c);
				StarsC[i] = (int)(R.nextFloat() * maxColors);
			}
		}
	}

	public void rotate(int val) {
		/*
		double ox, oy;
		int x, y;
		double a = (double)val * CMath.dFactor;
		for (int i=0; i<cntStars; i++) {
			ox = (double)(StarsX[i] - theCamera.ScreenShiftX);
			oy = (double)(StarsY[i] - theCamera.ScreenShiftY);
			oy *= -1;
			x = (int)(ox * CMath.M.cos(a) + oy * CMath.M.sin(a));
			y = (int)(- ox * CMath.M.sin(a) + oy * CMath.M.cos(a));	
			y *= -1;
			StarsX[i] = x + theCamera.ScreenShiftX;
			StarsY[i] = y + theCamera.ScreenShiftY;
		}
		*/
	}
}