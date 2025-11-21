package control;

import scene.Scene;
import scene.SpaceObject;
import scene.Camera;
import render.Render;
import render.Stars;

public class KeyController {
	Render theRender;
	Stars theStars;
	Scene theScene;
	public boolean keyUp = false;
	public boolean keyDown = false;
	public boolean keyRight = false;
	public boolean keyLeft = false;
	public boolean rollRight = false;
	public boolean rollLeft = false;
	public boolean Fire = false;
	public boolean MissileLauncher = false;
	public boolean MissileLauncherCam = false;
	public boolean SpeedUp = false;
	public boolean SpeedDown = false;
	public boolean ZeroSpeed = false;
	public boolean CruiseSpeed = false;
	public boolean MatchSpeed = false;

	public boolean FType[] = new boolean[13];
	public int Functional[] = new int[13];
	public int Id[] = new int[13];
	
	public KeyController(Render theRender, Stars theStars, Scene theScene) {
		this.theRender = theRender;
		this.theStars = theStars;
		this.theScene = theScene;

		for (int i=0; i<13; i++) Functional[i] = -1;
	}

	public void applyKeys(Camera theCamera) {
		if (keyUp) {
			theCamera.decPitch();
			theStars.shiftY(-theCamera.PitchFactor);
		}
		if (keyDown) {
			theCamera.incPitch();
			theStars.shiftY(theCamera.PitchFactor);
		}
		if (keyRight) {
			theCamera.decYaw();
			theStars.shiftX(-theCamera.YawFactor);
		}
		if (keyLeft) {
			theCamera.incYaw();
			theStars.shiftX(theCamera.YawFactor);
		}
		if (rollRight) {
			theCamera.incRoll();
		}
		if (rollLeft) {
			theCamera.decRoll();
		}
		if (SpeedUp) {
			theCamera.CameraSpeed+= 25;
		}
		if (SpeedDown) {
			theCamera.CameraSpeed-= 25;
		}
		if (CruiseSpeed) {
			theCamera.CameraSpeed = 400;
		}
		if (ZeroSpeed) {
			theCamera.CameraSpeed = 0;
		}
		if (MatchSpeed) {
			if (theRender.indexCross >= 0) {
				theCamera.CameraSpeed = 
				theScene.Objects.Objects[theRender.indexCross].currentSpeed;
			}
		}
	}

	public void applyKeys(SpaceObject theObject) {
		if (keyUp) {
			theObject.decPitch();
		}
		if (keyDown) {
			theObject.addPitch();
		}
		if (keyRight) {
			theObject.decYaw();
		}
		if (keyLeft) {
			theObject.addYaw();
		}
		if (Fire) {
			theObject.tryToFire();
		}
		if (MissileLauncher) {
			theObject.tryToLaunch(false);
		}
		if (MissileLauncherCam) {
			theObject.tryToLaunch(true);
		}
		if (SpeedUp) {
			theObject.increaseSpeed();
		}
		if (SpeedDown) {
			theObject.decreaseSpeed();
		}
		if (ZeroSpeed) {
			if (theObject.currentSpeed > 0) theObject.decreaseSpeed();
			else if (theObject.currentSpeed < 0) theObject.increaseSpeed();
		}
		if (CruiseSpeed) {
			if (theObject.currentSpeed > theObject.theModel.cruiseSpeed) theObject.decreaseSpeed();
			else if (theObject.currentSpeed < theObject.theModel.cruiseSpeed) theObject.increaseSpeed();
		}
		if (MatchSpeed) {
			int target = theObject.taskScreen;
			if (target >= 0) {
				if (theScene.Objects.Objects[target].currentSpeed > theObject.currentSpeed)
					theObject.increaseSpeed();
				else if (theScene.Objects.Objects[target].currentSpeed < theObject.currentSpeed)
					theObject.decreaseSpeed();
			}       
		}
	}
}