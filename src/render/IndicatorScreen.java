package render;

import java.awt.*;
import java.applet.*;
import media.Media;
import scene.Scene;
import scene.Camera;
import scene.SpaceObject;
import scene.Action;
import control.Task;
import engine.Area;
import engine.AreaControl;
import engine.AreaListener;
import engine.Strike;

public class IndicatorScreen implements AreaListener {
	private final static String AUTOPILOT = "Autopilot";
	private final static String TIMER = "Timer";
	
	Strike theApplet;
	Graphics theCanvas;
	Helper helper = Helper.getInstance();
	Scene theScene;
	Camera theCamera;
	SpaceObject theObject;
	Media theMedia;
	Font screenFont = new Font("Courier", Font.PLAIN, 9);
	Font screenFontL = new Font("Courier", Font.BOLD, 16);
	int x = 370;
	int y = 400;
	int w = 150;
	int h = 110;

	boolean Expaired = false;
	boolean Camera = false;
	boolean HumanControl = false;
	boolean MissileLock = false;
	boolean CollisionMenace = false;
	boolean LowFuel = false;
	int mTime = 0;
	int sTime = 0;

	public IndicatorScreen(Strike theApplet, Graphics theCanvas, Scene theScene,
						Camera theCamera, Media theMedia) {
		this.theApplet = theApplet;
		this.theCanvas = theCanvas;
		this.theScene = theScene;
		this.theCamera = theCamera;
		this.theMedia = theMedia;
		
		// create active zones
		// coordinates dont matter here - will be updated later
		this.theApplet.areaControl.addArea(
				this, AUTOPILOT, AreaControl.AUTOPILOT, x, y, w, 25);
		this.theApplet.areaControl.addArea(
				this, TIMER, AreaControl.TIMER, x, y + 50, w, 25);
	}

	private void clearAll() {           
		theCanvas.setColor(Color.black);
		theCanvas.fillRect(x, y, w, h);		
	}

	public void expaired() {
		HumanControl = MissileLock = CollisionMenace = LowFuel = false;
		Expaired = true;
	}
	


	public void drawIndicator(boolean Value, int sy,
			String trueValue, String falseValue, Color trueColor, Color falseColor) {
		//theCanvas.setColor(Color.black);
		//theCanvas.fillRect(x, y + sy, w, 25);
		Color color;
		if (Value) color = new Color(40, 0, 150);
		else color = new Color(40, 0, 100);
		helper.theCanvas.setColor(color);
		helper.drawArea(x, y + sy, w, 25);
		
		FontMetrics FM = theCanvas.getFontMetrics();
		String strValue = "";
		if (Value) {
			theCanvas.setColor(trueColor);
			strValue = trueValue;
		} else {
			theCanvas.setColor(falseColor);
			strValue = falseValue;
		}
		int sx = (this.w - FM.stringWidth(strValue)) / 2;
		theCanvas.drawString(strValue, x + sx, y+sy+17);
	}

	public void drawValue(String strValue, int sy, Color valColor) {
		//theCanvas.setColor(Color.black);
		//theCanvas.fillRect(x, y + sy, w, 25);
		Color color;
		if (theScene.statis) color = new Color(100, 100, 200);
		else color = new Color(40, 0, 100);
		helper.theCanvas.setColor(color);
		helper.drawArea(x, y + sy, w, 25);
		
		theCanvas.setColor(valColor);
		FontMetrics FM = theCanvas.getFontMetrics();
		int sx = (this.w - FM.stringWidth(strValue)) / 2;
		theCanvas.drawString(strValue, x + sx, y+sy+17);
	}

	public void drawEmpty(int sy) {
		theCanvas.setColor(Color.black);
		theCanvas.fillRect(x, y + sy, w, h - sy);
	}

	public void draw() {
        try {
 		theCanvas.setFont(screenFontL);

		if (theCamera.iRelatedObject != -1) {
			if (Camera == true) {
				Camera = false;
				Expaired = true;				
			}
			// Auto/Manual indicator
			if (Expaired || HumanControl != theScene.Objects.Objects[theCamera.iRelatedObject].HumanControlled) {
				HumanControl = theScene.Objects.Objects[theCamera.iRelatedObject].HumanControlled;
				drawIndicator(HumanControl, 0, "MANUAL", "AUTO", Color.red, Color.blue);
			}
			//Collision indicator
			boolean collise = false;
			if (theScene.Objects.Objects[theCamera.iRelatedObject].currentTarget == Action.AvoidCollision)
				collise = true;
			else theMedia.auAlarm.stop();

			if (Expaired || CollisionMenace != collise) {
				CollisionMenace = collise;
                
				if (theScene.Objects.Objects[theCamera.iRelatedObject].HumanControlled)
					if (CollisionMenace == false) theMedia.auAlarm.stop();
						else theMedia.auAlarm.play();
                

				Color nc = new Color(120, 0, 0);
				drawIndicator(collise, 25, "COLLISION", "COLLISION", Color.red, nc);
			}

			if (Expaired || mTime != theScene.minTime || sTime != theScene.secTime) {
				mTime = theScene.minTime;
				sTime = theScene.secTime;
				drawValue("Time " + mTime + ":" + sTime, 50, Color.green);
			}

			//fill the rest of space
			drawEmpty(75);

			Expaired = false;	
		} else {
			if (Expaired || !Camera
					 || mTime != theScene.minTime
					 || sTime != theScene.secTime) {
				theMedia.auAlarm.stop();
				clearAll();
				
				if (Expaired || !Camera || mTime != theScene.minTime || sTime != theScene.secTime) {
					mTime = theScene.minTime;
					sTime = theScene.secTime;
					drawValue("Time " + mTime + ":" + sTime, 50, Color.green);
				}
					
				Camera = true;
				Expaired = false;
			}
		}
        
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	
	
	public void areaClick(Area area, int cx, int cy, int clicks) {
		if (area.getType() == AreaControl.AUTOPILOT
				&& theCamera.iRelatedObject != -1) {
			theScene.Objects.Objects[theCamera.iRelatedObject].takeControl();
		} else if (area.getType() == AreaControl.TIMER) {
			if (theScene.turnBased) {
				theScene.nextParty();
			} else {
				if (theScene.statis) theScene.statis = false;
					else theScene.statis = true;
			}
			this.expaired();
		}
	}
	
	public void areaRightClick(Area area, int cx, int cy, int clicks) {
		if (area.getType() == AreaControl.TIMER) {
			theScene.changeTiming();
		}
	}
	
	public void areaWheel(Area area, int value) {}
	
	public void clearifyPosition(int w, int h) {
		this.x = w - theApplet.theTaskScreen.w - this.w - 20;
		this.y = h - this.h - 10;
		
		// update active areas
		theApplet.areaControl.updateAreaCoords(
				AUTOPILOT, x, y, this.w, 25);
		theApplet.areaControl.updateAreaCoords(
				TIMER, x, y + 50, this.w, 25);
	}
}