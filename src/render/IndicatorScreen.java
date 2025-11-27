package render;

import java.awt.*;
import media.Media;
import scene.Scene;
import scene.Camera;
import scene.SpaceObject;
import scene.Action;
import engine.Area;
import engine.AreaControl;
import engine.AreaListener;
import engine.Strike;

public class IndicatorScreen implements AreaListener {
	private final static String AUTOPILOT = "Autopilot";
	private final static String TIMER = "Timer";
	
	Strike applet;
	Graphics canvas;
	Helper helper = Helper.getInstance();
	Scene scene;
	Camera camera;
	SpaceObject spaceObject;
	Media media;
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

	public IndicatorScreen(Strike applet, Graphics canvas, Scene scene,
						Camera camera, Media media) {
		this.applet = applet;
		this.canvas = canvas;
		this.scene = scene;
		this.camera = camera;
		this.media = media;
		
		// create active zones
		// coordinates dont matter here - will be updated later
		this.applet.areaControl.addArea(
				this, AUTOPILOT, AreaControl.AUTOPILOT, x, y, w, 25);
		this.applet.areaControl.addArea(
				this, TIMER, AreaControl.TIMER, x, y + 50, w, 25);
	}

	private void clearAll() {           
		canvas.setColor(Color.black);
		canvas.fillRect(x, y, w, h);		
	}

	public void expaired() {
		HumanControl = MissileLock = CollisionMenace = LowFuel = false;
		Expaired = true;
	}
	


	public void drawIndicator(boolean Value, int sy,
			String trueValue, String falseValue, Color trueColor, Color falseColor) {
		//canvas.setColor(Color.black);
		//canvas.fillRect(x, y + sy, w, 25);
		Color color;
		if (Value) color = new Color(40, 0, 150);
		else color = new Color(40, 0, 100);
		helper.canvas.setColor(color);
		helper.drawArea(x, y + sy, w, 25);
		
		FontMetrics FM = canvas.getFontMetrics();
		String strValue = "";
		if (Value) {
			canvas.setColor(trueColor);
			strValue = trueValue;
		} else {
			canvas.setColor(falseColor);
			strValue = falseValue;
		}
		int sx = (this.w - FM.stringWidth(strValue)) / 2;
		canvas.drawString(strValue, x + sx, y+sy+17);
	}

	public void drawValue(String strValue, int sy, Color valColor) {
		//canvas.setColor(Color.black);
		//canvas.fillRect(x, y + sy, w, 25);
		Color color;
		if (scene.statis) color = new Color(100, 100, 200);
		else color = new Color(40, 0, 100);
		helper.canvas.setColor(color);
		helper.drawArea(x, y + sy, w, 25);
		
		canvas.setColor(valColor);
		FontMetrics FM = canvas.getFontMetrics();
		int sx = (this.w - FM.stringWidth(strValue)) / 2;
		canvas.drawString(strValue, x + sx, y+sy+17);
	}

	public void drawEmpty(int sy) {
		canvas.setColor(Color.black);
		canvas.fillRect(x, y + sy, w, h - sy);
	}

	public void draw() {
        try {
 		canvas.setFont(screenFontL);

		if (camera.iRelatedObject != -1) {
			if (Camera == true) {
				Camera = false;
				Expaired = true;				
			}
			// Auto/Manual indicator
			if (Expaired || HumanControl != scene.Objects.Objects[camera.iRelatedObject].HumanControlled) {
				HumanControl = scene.Objects.Objects[camera.iRelatedObject].HumanControlled;
				drawIndicator(HumanControl, 0, "MANUAL", "AUTO", Color.red, Color.blue);
			}
			//Collision indicator
			boolean collise = false;
			if (scene.Objects.Objects[camera.iRelatedObject].currentTarget == Action.AvoidCollision)
				collise = true;
			else media.auAlarm.stop();

			if (Expaired || CollisionMenace != collise) {
				CollisionMenace = collise;
                
				if (scene.Objects.Objects[camera.iRelatedObject].HumanControlled)
					if (CollisionMenace == false) media.auAlarm.stop();
						else media.auAlarm.play();
                

				Color nc = new Color(120, 0, 0);
				drawIndicator(collise, 25, "COLLISION", "COLLISION", Color.red, nc);
			}

			if (Expaired || mTime != scene.minTime || sTime != scene.secTime) {
				mTime = scene.minTime;
				sTime = scene.secTime;
				drawValue("Time " + mTime + ":" + sTime, 50, Color.green);
			}

			//fill the rest of space
			drawEmpty(75);

			Expaired = false;	
		} else {
			if (Expaired || !Camera
					 || mTime != scene.minTime
					 || sTime != scene.secTime) {
				media.auAlarm.stop();
				clearAll();
				
				if (Expaired || !Camera || mTime != scene.minTime || sTime != scene.secTime) {
					mTime = scene.minTime;
					sTime = scene.secTime;
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
				&& camera.iRelatedObject != -1) {
			scene.Objects.Objects[camera.iRelatedObject].takeControl();
		} else if (area.getType() == AreaControl.TIMER) {
			if (scene.turnBased) {
				scene.nextParty();
			} else {
				if (scene.statis) scene.statis = false;
					else scene.statis = true;
			}
			this.expaired();
		}
	}
	
	public void areaRightClick(Area area, int cx, int cy, int clicks) {
		if (area.getType() == AreaControl.TIMER) {
			scene.changeTiming();
		}
	}
	
	public void areaWheel(Area area, int value) {}
	
	public void clearifyPosition(int w, int h) {
		this.x = w - applet.taskScreen.w - this.w - 20;
		this.y = h - this.h - 10;
		
		// update active areas
		applet.areaControl.updateAreaCoords(
				AUTOPILOT, x, y, this.w, 25);
		applet.areaControl.updateAreaCoords(
				TIMER, x, y + 50, this.w, 25);
	}
}