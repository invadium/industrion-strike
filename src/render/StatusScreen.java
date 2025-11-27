package render;

import java.awt.*;

import engine.Strike;
import scene.Scene;
import scene.Camera;
import scene.SpaceObject;

public class StatusScreen {
	Strike applet;
	Graphics canvas;
	Scene scene;
	Camera camera;
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
	

	public StatusScreen(Strike applet, Graphics canvas, Scene scene, Camera camera) {
		this.applet = applet;
		this.canvas = canvas;
		this.scene = scene;
		this.camera = camera;
	}

	public void clearAll() {           
		canvas.setColor(Color.black);
		canvas.fillRect(x, y, w, h);		
	}

	public void clear() {
		canvas.setColor(Color.black);
		canvas.fillRect(x, y, w, 12);		
	}

	public void drawEmpty(int Bx, int By) {
		canvas.setColor(Color.black);
		canvas.fillRect(Bx - 5, By - 13, BarWidth + 10, BarHeight + 15);
	}

	public void drawBar(int Bx, int By, int min, int max, int cur, 
					Color C, String Descr, int visLevel) {
		if (cur > max) cur = max;
		double coef = (double)(cur - min) / (double)(max - min);
		int level = (int)((double)BarHeight * coef);

		canvas.setColor(Color.black);
		canvas.fillRect(Bx, By-13, BarWidth+3, BarHeight-level+13);
		canvas.setColor(Color.green);
		canvas.drawRect(Bx, By, BarWidth + 2, BarHeight + 1);

		canvas.setColor(C);
		canvas.fillRect(Bx+1, By+BarHeight-level+1, BarWidth, level);
		canvas.setColor(Color.black);
		canvas.drawLine(Bx+1, By+1, Bx+1, By+BarHeight);
		canvas.drawLine(Bx+BarWidth+1, By+1, Bx+BarWidth+1, By+BarHeight);

		String strLevel = "" + visLevel;
		FontMetrics FM = canvas.getFontMetrics();
		int sx = (BarWidth - FM.stringWidth(strLevel)) / 2;
		canvas.setColor(Color.black);
		canvas.fillRect(Bx+BarWidth+3, By - 13, 2, BarHeight + 15);
		canvas.fillRect(Bx - 5, By - 13, 5, BarHeight + 15);
		canvas.fillRect(Bx - 5, By+BarHeight+2, BarWidth + 10, 13);
		canvas.setColor(Color.green);
		canvas.drawString(strLevel, Bx + sx, By - 1);
		canvas.drawString(Descr, Bx, By + BarHeight + 10);
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
		canvas.setFont(screenFontL);
		if (camera.iRelatedObject == -1) {
			if (lid != -1 || camSpeed != camera.CameraSpeed) {
				resetAllRegs();
				clearAll();
				canvas.setColor(Color.green);
				canvas.drawString("FreeCamera", x, y+10);
				canvas.drawString("Camera speed: " + camera.CameraSpeed, x, y+20);
				camSpeed = camera.CameraSpeed;
				lid = -1;
			}
		} else {
			SpaceObject spaceObject;
			spaceObject = scene.Objects.Objects[camera.iRelatedObject];
			//class & name
			if (lid != spaceObject.id) {
				clear();
				switch(scene.Objects.Objects[camera.iRelatedObject].Side) {
						case 0: canvas.setColor(Color.white); break;			 //white - neutral
						case 1: canvas.setColor(new Color(100, 255, 100)); break; //green
						case 2: canvas.setColor(new Color(255, 70, 70)); break;   //red
						case 3: canvas.setColor(new Color(255, 200, 100)); break; //yellow
						case 4: canvas.setColor(new Color(255, 113, 255)); break; //pink
						default: canvas.setColor(Color.white);				 //white - unknown
				}
				canvas.drawString(spaceObject.model.strIDName
					+ ": " + spaceObject.strObjectName, x, y+10);
			}
			canvas.setFont(screenFont);
			//speed
			if (lSpeed != spaceObject.currentSpeed || mSpeed != spaceObject.model.maxSpeed)
			  drawBar(x + 5, y + 25, -spaceObject.model.maxSpeed, spaceObject.model.maxSpeed,
				spaceObject.currentSpeed, Color.blue, "SPD", spaceObject.currentSpeed);
			//fuel
			if (lFuel != spaceObject.currentFuel || mFuel != spaceObject.model.FuelTank)
			  drawBar(x + 30, y + 25, 0, spaceObject.model.FuelTank,
				spaceObject.currentFuel, Color.red, "FUL", spaceObject.currentFuel / 100);
			//speed zero line
			canvas.setColor(Color.green);
			canvas.drawLine(x+2, y+75, x+BarWidth+10, y+75);
			//shield
			if (lShield != spaceObject.Shield || mShield != spaceObject.model.Shield)
			  drawBar(x + 55, y + 25, 0, spaceObject.model.Shield,
				spaceObject.Shield, Color.yellow, "SHD", spaceObject.Shield);
			//hull
			if (lHull != spaceObject.Hull || mHull != spaceObject.model.Hull)
			  drawBar(x + 80, y + 25, 0, spaceObject.model.Hull,
				spaceObject.Hull, Color.green, "HUL", spaceObject.Hull);
			//energy
			if (lEnergy != spaceObject.Energy || mEnergy != spaceObject.model.Energy)
			  drawBar(x + 105, y + 25, 0, spaceObject.model.Energy,
				spaceObject.Energy, Color.yellow, "LEB", spaceObject.Energy);
			//missiles
			if (spaceObject.model.cntLaunchers > 0 &&
			     (lLauncher != spaceObject.currentLauncher
			     || lMissiles != spaceObject.missilesLoad[spaceObject.currentLauncher]
			     || mMissiles != spaceObject.model.missileCapacity[spaceObject.currentLauncher])) {
			  drawBar(x + 130, y + 25, 0, spaceObject.model.missileCapacity[spaceObject.currentLauncher],
				spaceObject.missilesLoad[spaceObject.currentLauncher], Color.red,
				spaceObject.model.missilesRack[spaceObject.currentLauncher].strIDName,
				spaceObject.missilesLoad[spaceObject.currentLauncher]);
			  lMissiles = spaceObject.missilesLoad[spaceObject.currentLauncher];				
			  mMissiles = spaceObject.model.missileCapacity[spaceObject.currentLauncher];
			}

			//missile recharge
			if (spaceObject.model.cntLaunchers > 0 &&
			    (mRecharge !=  spaceObject.model.missilesRack[spaceObject.currentLauncher].rechargeTime
			     || lRecharge != spaceObject.missileRecharge[spaceObject.currentLauncher])) {
			  drawBar(x + 155, y + 25, 0, spaceObject.model.missilesRack[spaceObject.currentLauncher].rechargeTime,
				spaceObject.model.missilesRack[spaceObject.currentLauncher].rechargeTime
				- spaceObject.missileRecharge[spaceObject.currentLauncher],
				new Color(200, 0, 40), "RCH",
				(spaceObject.missileRecharge[spaceObject.currentLauncher] + 19) / 20);
			  mRecharge = spaceObject.model.missilesRack[spaceObject.currentLauncher].rechargeTime;
			  lRecharge = spaceObject.missileRecharge[spaceObject.currentLauncher];
			}

			if (spaceObject.model.cntLaunchers == 0
			    && mMissiles != -13) {
				canvas.setColor(Color.black);
				canvas.fillRect(x + 125, y + 12, 50, 128);
				mMissiles = -13;
				lMissiles = -1;
				mRecharge = -1;
				lRecharge = -1;
			}

			if (lBlank == -1) {
				canvas.setColor(Color.black);
				canvas.fillRect(x + 175, y + 12, 25, 128);
				lBlank = 1;
			}

			//save last values
			lid = spaceObject.id;
			lSpeed = spaceObject.currentSpeed;
			lFuel = spaceObject.currentFuel;
			lShield = spaceObject.Shield;
			lHull = spaceObject.Hull;
			lEnergy = spaceObject.Energy;
			lLauncher = spaceObject.currentLauncher;
			mSpeed = spaceObject.model.maxSpeed;
			mFuel = spaceObject.model.FuelTank;
			mShield = spaceObject.model.Shield;
			mHull = spaceObject.model.Hull;
			mEnergy = spaceObject.model.Energy;
		}
	}

	
	public void clearifyPosition(int w, int h) {
		this.x = w - this.w - 10;
		this.y = applet.radar.y + applet.radar.h + 10;
	}
}