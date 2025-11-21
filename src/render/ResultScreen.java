package render;

import java.awt.*;
import java.applet.*;

import mission.DataElement;
import media.Media;
import scene.Scene;

public class ResultScreen {
	Applet theApplet;
	Graphics theCanvas;
	Graphics appCanvas;
	Scene theScene;
	Media theMedia;
	Image imgBuffer;

	long iDetail = 1;
	int iTimer = 0;
	int iQuant = 3;
	boolean noMoreDetails = false;

	Font screenFontN = new Font("Courier", Font.PLAIN, 18);
	Font screenFontE = new Font("Courier", Font.PLAIN, 20);
	Font screenFontH = new Font("Courier", Font.PLAIN, 18);
	Font screenFontL = new Font("Courier", Font.PLAIN, 32);
	boolean isExpaired = true;

	public ResultScreen(Applet theApplet, Graphics appCanvas, Scene theScene, Media theMedia) {
		this.theApplet = theApplet;
		this.appCanvas = appCanvas;
		this.theScene = theScene;
		this.theMedia = theMedia;

		imgBuffer = theApplet.createImage(theApplet.getWidth(), theApplet.getHeight());
		this.theCanvas = imgBuffer.getGraphics();
	}

	public void detail() {
		iTimer++;
		if (iTimer >= iQuant) {
			iTimer = 0;			
			isExpaired = true;
			iDetail++;
		}
	};

	public void expaired() {
		isExpaired = true;
	}

	private void drawResult() {
			Color FColor = new Color(255, 200, 60);
			int mis = theScene.Interpreter.getiVariable("Mission");

			String strMissionResult = "Mission finished";
			if (mis < 0) strMissionResult = "Mission failed";
			if (mis > 0) strMissionResult = "Mission is successful";
			
			theCanvas.setFont(screenFontL);
			FontMetrics FM = theCanvas.getFontMetrics();
			int sx = (theApplet.getWidth() - FM.stringWidth(strMissionResult)) / 2;

			theCanvas.setColor(Color.black);
			theCanvas.drawString(strMissionResult, sx+4, 54);
			theCanvas.setColor(FColor);
			theCanvas.drawString(strMissionResult, sx, 50);
	}

	private void drawScore() {
		Color FColor = new Color(255, 200, 60);
		int iDet = 1;
		int Sides = theScene.Interpreter.getiVariable("Sides");
		int startx = 140;
		int starty = 160;
		int sx = 145;
		int sy = 35;

		if (Sides == 0) return;
		for (int j = 0; j < 7; j++) {
				String strHead = "";
				switch (j) {
				  case 0: strHead = "Score"; break;
				  case 1: strHead = "Ships Destroyed"; break;
				  case 2: strHead = "Ships Lost"; break;
				  case 3: strHead = "Fighters Destroyed"; break;
				  case 4: strHead = "Fighters Lost"; break;
				  case 5: strHead = "Misc Destroyed"; break;
				  case 6: strHead = "Misc Lost"; break;
				}
				iDet++;
				if (iDet < this.iDetail) {
					theCanvas.setFont(screenFontH);
					theCanvas.setColor(Color.black);
					theCanvas.drawString(strHead, startx-73, starty + j*sy+2);
					theCanvas.setColor(FColor);
					theCanvas.drawString(strHead, startx-75, starty + j*sy);
				}
		}

		for (int i = 1; i <= Sides; i++) {
			String sideName = theScene.Interpreter.getsVariable("SideName" + i);
			if (sideName.equals("")) sideName = "Side " + i;
			iDet++;
			if (iDet < this.iDetail) {
				theCanvas.setFont(screenFontN);
				theCanvas.setColor(Color.black);
				theCanvas.drawString(sideName, startx + i*sx+2, starty - 60+2);
				theCanvas.setColor(FColor);
				theCanvas.drawString(sideName, startx + i*sx, starty - 60);
			}

			String sideInfo = theScene.Interpreter.getsVariable("SideInfo" + i);
			if (!sideInfo.equals("")) {
				iDet++;
				if (iDet < this.iDetail) {
					theCanvas.setFont(screenFontN);
					theCanvas.setColor(Color.black);
					theCanvas.drawString(sideInfo, startx + i*sx+2, starty - 35+2);
					theCanvas.setColor(FColor);
					theCanvas.drawString(sideInfo, startx + i*sx, starty - 35);
				}
			}

			int iVal = 0;
		  	for (int j = 0; j < 7; j++) {
				switch (j) {
				  case 0: iVal = theScene.Interpreter.getiVariable("Score" + i); break;
				  case 1: iVal = theScene.Interpreter.getiVariable("ShipsDestroy" + i); break;
				  case 2: iVal = theScene.Interpreter.getiVariable("ShipsLost" + i); break;
				  case 3: iVal = theScene.Interpreter.getiVariable("FightersDestroy" + i); break;
				  case 4: iVal = theScene.Interpreter.getiVariable("FightersLost" + i); break;
				  case 5: iVal = theScene.Interpreter.getiVariable("StaticDestroy" + i); break;
				  case 6: iVal = theScene.Interpreter.getiVariable("StaticLost" + i); break;
				}
				iDet++;
				if (iDet < this.iDetail) {
					theCanvas.setFont(screenFontH);
					theCanvas.setColor(Color.black);
					theCanvas.drawString("" + iVal, startx + i*sx+2, starty + j*sy+2);
					theCanvas.setColor(FColor);
					theCanvas.drawString("" + iVal, startx + i*sx, starty + j*sy);
				}
			}
			
		}

		iDet++;
		if (iDet < this.iDetail) {
			String strPress = "Press Enter to continue...";
			theCanvas.setFont(screenFontE);
			FontMetrics FM = theCanvas.getFontMetrics();
			int stx = (theApplet.getWidth() - FM.stringWidth(strPress)) / 2;
			theCanvas.setColor(Color.black);
			theCanvas.drawString(strPress, stx+2, theApplet.getHeight() - 50);
			theCanvas.setColor(FColor);
			theCanvas.drawString(strPress, stx, theApplet.getHeight() - 50);
		}


		if (!noMoreDetails) theMedia.auPing.play();
		if (iDet <= this.iDetail) this.noMoreDetails = true;
	}

	public void draw() {
		if (isExpaired) {
			theCanvas.drawImage(theMedia.imgResult, 0, 0, 
				theApplet.getWidth(), theApplet.getHeight(), theApplet);

			drawResult();
			drawScore();

			appCanvas.drawImage(imgBuffer, 0, 0, this.theApplet);
			isExpaired = false;
		}
	}
}