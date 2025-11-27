package control;

import scene.Scene;
import scene.SpaceObject;
import scene.Camera;
import render.Render;
import render.Stars;

public class KeyController {
	Render render;
	Stars stars;
	Scene scene;
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
	
	public KeyController(Render render, Stars stars, Scene scene) {
		this.render = render;
		this.stars = stars;
		this.scene = scene;

		for (int i=0; i<13; i++) Functional[i] = -1;
	}

	public void applyKeys(Camera camera) {
		if (keyUp) {
			camera.decPitch();
			stars.shiftY(-camera.PitchFactor);
		}
		if (keyDown) {
			camera.incPitch();
			stars.shiftY(camera.PitchFactor);
		}
		if (keyRight) {
			camera.decYaw();
			stars.shiftX(-camera.YawFactor);
		}
		if (keyLeft) {
			camera.incYaw();
			stars.shiftX(camera.YawFactor);
		}
		if (rollRight) {
			camera.incRoll();
		}
		if (rollLeft) {
			camera.decRoll();
		}
		if (SpeedUp) {
			camera.CameraSpeed+= 25;
		}
		if (SpeedDown) {
			camera.CameraSpeed-= 25;
		}
		if (CruiseSpeed) {
			camera.CameraSpeed = 400;
		}
		if (ZeroSpeed) {
			camera.CameraSpeed = 0;
		}
		if (MatchSpeed) {
			if (render.indexCross >= 0) {
				camera.CameraSpeed = 
				scene.Objects.Objects[render.indexCross].currentSpeed;
			}
		}
	}

	public void applyKeys(SpaceObject spaceObject) {
		if (keyUp) {
			spaceObject.decPitch();
		}
		if (keyDown) {
			spaceObject.addPitch();
		}
		if (keyRight) {
			spaceObject.decYaw();
		}
		if (keyLeft) {
			spaceObject.addYaw();
		}
		if (Fire) {
			spaceObject.tryToFire();
		}
		if (MissileLauncher) {
			spaceObject.tryToLaunch(false);
		}
		if (MissileLauncherCam) {
			spaceObject.tryToLaunch(true);
		}
		if (SpeedUp) {
			spaceObject.increaseSpeed();
		}
		if (SpeedDown) {
			spaceObject.decreaseSpeed();
		}
		if (ZeroSpeed) {
			if (spaceObject.currentSpeed > 0) spaceObject.decreaseSpeed();
			else if (spaceObject.currentSpeed < 0) spaceObject.increaseSpeed();
		}
		if (CruiseSpeed) {
			if (spaceObject.currentSpeed > spaceObject.model.cruiseSpeed) spaceObject.decreaseSpeed();
			else if (spaceObject.currentSpeed < spaceObject.model.cruiseSpeed) spaceObject.increaseSpeed();
		}
		if (MatchSpeed) {
			int target = spaceObject.taskScreen;
			if (target >= 0) {
				if (scene.Objects.Objects[target].currentSpeed > spaceObject.currentSpeed)
					spaceObject.increaseSpeed();
				else if (scene.Objects.Objects[target].currentSpeed < spaceObject.currentSpeed)
					spaceObject.decreaseSpeed();
			}       
		}
	}
}