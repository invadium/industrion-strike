package render;

import java.awt.*;
import java.applet.*;

import engine.Strike;
import scene.Scene;
import scene.Camera;
import scene.SpaceObject;

public class StatusScreen {
	Strike theApplet;
	Graphics theCanvas;
	Scene theScene;
	Camera theCamera;
	Font screenFont = new Font("Courier", Font.PLAIN, 9);
	Font screenFontL = new Font("Courier", Font.PLAIN, 12);
	int x = 530;
	int y = 220;
	int w = 200;
	int h = 140;
	int BarHeight = 100;
	int BarWidth = 15;

	int lid = -13;
	int camSpeed = -10000000;
	int lSpeed = -1, lFuel = -1, lShield = -1, lHull = -1, lEnergy = -1, lMissiles = -1, lLauncher = -1, lRecharge = -1;
	int mSpeed = -1, mFuel = -1, mShield = -1, mHull = -1, mEnergy = -1, mMissiles = -1, mRecharge = -1;
	int lBlank = -1;
	

	public StatusScreen(Strike theApplet, Graphics theCanvas, Scene theScene, Camera theCamera) {
		this.theApplet = theApplet;
		this.theCanvas = theCanvas;
		this.theScene = theScene;
		this.theCamera = theCamera;
	}

	public void clearAll() {           
		theCanvas.setColor(Color.black);
		theCanvas.fillRect(x, y, w, h);		
	}

	public void clear() {
		theCanvas.setColor(Color.black);
		theCanvas.fillRect(x, y, w, 12);		
	}

	public void drawEmpty(int Bx, int By) {
		theCanvas.setColor(Color.black);
		theCanvas.fillRect(Bx - 5, By - 13, BarWidth + 10, BarHeight + 15);
	}

	public void drawBar(int Bx, int By, int min, int max, int cur, 
					Color C, String Descr, int visLevel) {
		if (cur > max) cur = max;
		double coef = (double)(cur - min) / (double)(max - min);
		int level = (int)((double)BarHeight * coef);

		theCanvas.setColor(Color.black);
		theCanvas.fillRect(Bx, By-13, BarWidth+3, BarHeight-level+13);
		theCanvas.setColor(Color.green);
		theCanvas.drawRect(Bx, By, BarWidth + 2, BarHeight + 1);

		theCanvas.setColor(C);
		theCanvas.fillRect(Bx+1, By+BarHeight-level+1, BarWidth, level);
		theCanvas.setColor(Color.black);
		theCanvas.drawLine(Bx+1, By+1, Bx+1, By+BarHeight);
		theCanvas.drawLine(Bx+BarWidth+1, By+1, Bx+BarWidth+1, By+BarHeight);

		String strLevel = "" + visLevel;
		FontMetrics FM = theCanvas.getFontMetrics();
		int sx = (BarWidth - FM.stringWidth(strLevel)) / 2;
		theCanvas.setColor(Color.black);
		theCanvas.fillRect(Bx+BarWidth+3, By - 13, 2, BarHeight + 15);
		theCanvas.fillRect(Bx - 5, By - 13, 5, BarHeight + 15);
		theCanvas.fillRect(Bx - 5, By+BarHeight+2, BarWidth + 10, 13);
		theCanvas.setColor(Color.green);
		theCanvas.drawString(strLevel, Bx + sx, By - 1);
		theCanvas.drawString(Descr, Bx, By + BarHeight + 10);
	}

	private void resetAllRegs() {
		lSpeed = lFuel = lShield = lHull = lEnergy = lMissiles = lLauncher = lRecharge = -1;
		mSpeed = mFuel = mShield = mHull = mEnergy = mMissiles = mRecharge = -1;
		lBlank = -1;
	}

	public void expaired() {
		resetAllRegs();
		lid = -13;
		camSpeed = -10000000;
	}

	public void drawStatusScreen() {
		theCanvas.setFont(screenFontL);
		if (theCamera.iRelatedObject == -1) {
			if (lid != -1 || camSpeed != theCamera.CameraSpeed) {
				resetAllRegs();
				clearAll();
				theCanvas.setColor(Color.green);
				theCanvas.drawString("FreeCamera", x, y+10);
				theCanvas.drawString("Camera speed: " + theCamera.CameraSpeed, x, y+20);
				camSpeed = theCamera.CameraSpeed;
				lid = -1;
			}
		} else {
			SpaceObject theObject;
			theObject = theScene.Objects.Objects[theCamera.iRelatedObject];
			//class & name
			if (lid != theObject.id) {
				clear();
				switch(theScene.Objects.Objects[theCamera.iRelatedObject].Side) {
						case 0: theCanvas.setColor(Color.white); break;			 //white - neutral
						case 1: theCanvas.setColor(new Color(100, 255, 100)); break; //green
						case 2: theCanvas.setColor(new Color(255, 70, 70)); break;   //red
						case 3: theCanvas.setColor(new Color(255, 200, 100)); break; //yellow
						case 4: theCanvas.setColor(new Color(255, 113, 255)); break; //pink
						default: theCanvas.setColor(Color.white);				 //white - unknown
				}
				theCanvas.drawString(theObject.theModel.strIDName
					+ ": " + theObject.strObjectName, x, y+10);
			}
			theCanvas.setFont(screenFont);
			//speed
			if (lSpeed != theObject.currentSpeed || mSpeed != theObject.theModel.maxSpeed)
			  drawBar(x + 5, y + 25, -theObject.theModel.maxSpeed, theObject.theModel.maxSpeed,
				theObject.currentSpeed, Color.blue, "SPD", theObject.currentSpeed);
			//fuel
			if (lFuel != theObject.currentFuel || mFuel != theObject.theModel.FuelTank)
			  drawBar(x + 30, y + 25, 0, theObject.theModel.FuelTank,
				theObject.currentFuel, Color.red, "FUL", theObject.currentFuel / 100);
			//speed zero line
			theCanvas.setColor(Color.green);
			theCanvas.drawLine(x+2, y+75, x+BarWidth+10, y+75);
			//shield
			if (lShield != theObject.Shield || mShield != theObject.theModel.Shield)
			  drawBar(x + 55, y + 25, 0, theObject.theModel.Shield,
				theObject.Shield, Color.yellow, "SHD", theObject.Shield);
			//hull
			if (lHull != theObject.Hull || mHull != theObject.theModel.Hull)
			  drawBar(x + 80, y + 25, 0, theObject.theModel.Hull,
				theObject.Hull, Color.green, "HUL", theObject.Hull);
			//energy
			if (lEnergy != theObject.Energy || mEnergy != theObject.theModel.Energy)
			  drawBar(x + 105, y + 25, 0, theObject.theModel.Energy,
				theObject.Energy, Color.yellow, "LEB", theObject.Energy);
			//missiles
			if (theObject.theModel.cntLaunchers > 0 &&
			     (lLauncher != theObject.currentLauncher
			     || lMissiles != theObject.missilesLoad[theObject.currentLauncher]
			     || mMissiles != theObject.theModel.missileCapacity[theObject.currentLauncher])) {
			  drawBar(x + 130, y + 25, 0, theObject.theModel.missileCapacity[theObject.currentLauncher],
				theObject.missilesLoad[theObject.currentLauncher], Color.red,
				theObject.theModel.missilesRack[theObject.currentLauncher].strIDName,
				theObject.missilesLoad[theObject.currentLauncher]);
			  lMissiles = theObject.missilesLoad[theObject.currentLauncher];				
			  mMissiles = theObject.theModel.missileCapacity[theObject.currentLauncher];
			}

			//missile recharge
			if (theObject.theModel.cntLaunchers > 0 &&
			    (mRecharge !=  theObject.theModel.missilesRack[theObject.currentLauncher].rechargeTime
			     || lRecharge != theObject.missileRecharge[theObject.currentLauncher])) {
			  drawBar(x + 155, y + 25, 0, theObject.theModel.missilesRack[theObject.currentLauncher].rechargeTime,
				theObject.theModel.missilesRack[theObject.currentLauncher].rechargeTime
				- theObject.missileRecharge[theObject.currentLauncher],
				new Color(200, 0, 40), "RCH",
				(theObject.missileRecharge[theObject.currentLauncher] + 19) / 20);
			  mRecharge = theObject.theModel.missilesRack[theObject.currentLauncher].rechargeTime;
			  lRecharge = theObject.missileRecharge[theObject.currentLauncher];
			}

			if (theObject.theModel.cntLaunchers == 0
			    && mMissiles != -13) {
				theCanvas.setColor(Color.black);
				theCanvas.fillRect(x + 125, y + 12, 50, 128);
				mMissiles = -13;
				lMissiles = -1;
				mRecharge = -1;
				lRecharge = -1;
			}

			if (lBlank == -1) {
				theCanvas.setColor(Color.black);
				theCanvas.fillRect(x + 175, y + 12, 25, 128);
				lBlank = 1;
			}

			//save last values
			lid = theObject.id;
			lSpeed = theObject.currentSpeed;
			lFuel = theObject.currentFuel;
			lShield = theObject.Shield;
			lHull = theObject.Hull;
			lEnergy = theObject.Energy;
			lLauncher = theObject.currentLauncher;
			mSpeed = theObject.theModel.maxSpeed;
			mFuel = theObject.theModel.FuelTank;
			mShield = theObject.theModel.Shield;
			mHull = theObject.theModel.Hull;
			mEnergy = theObject.theModel.Energy;
		}
	}

	
	public void clearifyPosition(int w, int h) {
		this.x = w - this.w - 10;
		this.y = theApplet.theRadar.y + theApplet.theRadar.h + 10;
	}
}