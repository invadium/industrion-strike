package render;

import java.awt.*;
import java.applet.*;

import mission.DataElement;
import media.Media;
import scene.Scene;

public class ResultScreen {
	Applet applet;
	Graphics bufCanvas;
	Graphics appCanvas;
	Scene scene;
	Media media;
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

	public ResultScreen(Applet applet, Graphics appCanvas, Scene scene, Media media) {
		this.applet = applet;
		this.appCanvas = appCanvas;
		this.scene = scene;
		this.media = media;

		imgBuffer = applet.createImage(applet.getWidth(), applet.getHeight());
		this.bufCanvas = imgBuffer.getGraphics();
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
			int mis = scene.Interpreter.getiVariable("Mission");

			String strMissionResult = "Mission finished";
			if (mis < 0) strMissionResult = "Mission failed";
			if (mis > 0) strMissionResult = "Mission is successful";
			
			bufCanvas.setFont(screenFontL);
			FontMetrics FM = bufCanvas.getFontMetrics();
			int sx = (applet.getWidth() - FM.stringWidth(strMissionResult)) / 2;

			bufCanvas.setColor(Color.black);
			bufCanvas.drawString(strMissionResult, sx+4, 54);
			bufCanvas.setColor(FColor);
			bufCanvas.drawString(strMissionResult, sx, 50);
	}

	private void drawScore() {
		Color FColor = new Color(255, 200, 60);
		int iDet = 1;
		int Sides = scene.Interpreter.getiVariable("Sides");
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
					bufCanvas.setFont(screenFontH);
					bufCanvas.setColor(Color.black);
					bufCanvas.drawString(strHead, startx-73, starty + j*sy+2);
					bufCanvas.setColor(FColor);
					bufCanvas.drawString(strHead, startx-75, starty + j*sy);
				}
		}

		for (int i = 1; i <= Sides; i++) {
			String sideName = scene.Interpreter.getsVariable("SideName" + i);
			if (sideName.equals("")) sideName = "Side " + i;
			iDet++;
			if (iDet < this.iDetail) {
				bufCanvas.setFont(screenFontN);
				bufCanvas.setColor(Color.black);
				bufCanvas.drawString(sideName, startx + i*sx+2, starty - 60+2);
				bufCanvas.setColor(FColor);
				bufCanvas.drawString(sideName, startx + i*sx, starty - 60);
			}

			String sideInfo = scene.Interpreter.getsVariable("SideInfo" + i);
			if (!sideInfo.equals("")) {
				iDet++;
				if (iDet < this.iDetail) {
					bufCanvas.setFont(screenFontN);
					bufCanvas.setColor(Color.black);
					bufCanvas.drawString(sideInfo, startx + i*sx+2, starty - 35+2);
					bufCanvas.setColor(FColor);
					bufCanvas.drawString(sideInfo, startx + i*sx, starty - 35);
				}
			}

			int iVal = 0;
		  	for (int j = 0; j < 7; j++) {
				switch (j) {
				  case 0: iVal = scene.Interpreter.getiVariable("Score" + i); break;
				  case 1: iVal = scene.Interpreter.getiVariable("ShipsDestroy" + i); break;
				  case 2: iVal = scene.Interpreter.getiVariable("ShipsLost" + i); break;
				  case 3: iVal = scene.Interpreter.getiVariable("FightersDestroy" + i); break;
				  case 4: iVal = scene.Interpreter.getiVariable("FightersLost" + i); break;
				  case 5: iVal = scene.Interpreter.getiVariable("StaticDestroy" + i); break;
				  case 6: iVal = scene.Interpreter.getiVariable("StaticLost" + i); break;
				}
				iDet++;
				if (iDet < this.iDetail) {
					bufCanvas.setFont(screenFontH);
					bufCanvas.setColor(Color.black);
					bufCanvas.drawString("" + iVal, startx + i*sx+2, starty + j*sy+2);
					bufCanvas.setColor(FColor);
					bufCanvas.drawString("" + iVal, startx + i*sx, starty + j*sy);
				}
			}
			
		}

		iDet++;
		if (iDet < this.iDetail) {
			String strPress = "Press Enter to continue...";
			bufCanvas.setFont(screenFontE);
			FontMetrics FM = bufCanvas.getFontMetrics();
			int stx = (applet.getWidth() - FM.stringWidth(strPress)) / 2;
			bufCanvas.setColor(Color.black);
			bufCanvas.drawString(strPress, stx+2, applet.getHeight() - 50);
			bufCanvas.setColor(FColor);
			bufCanvas.drawString(strPress, stx, applet.getHeight() - 50);
		}


		if (!noMoreDetails) media.auPing.play();
		if (iDet <= this.iDetail) this.noMoreDetails = true;
	}

	public void draw() {
		if (isExpaired) {
			bufCanvas.drawImage(media.imgResult, 0, 0, 
				applet.getWidth(), applet.getHeight(), applet);

			drawResult();
			drawScore();

			appCanvas.drawImage(imgBuffer, 0, 0, this.applet);
			isExpaired = false;
		}
	}
}