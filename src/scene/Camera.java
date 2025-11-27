package scene;

import engine.Strike;
import math.*;

public class Camera {
	public static final int cntRelationTypes = 8;
	private Strike applet;
	public int iRelatedObject = -1;
	public int iRelationType = 0;
	public int iTargetObject = -1;
	public boolean freeBind = false;
	public boolean smartBind = false;
	public int smartTime = 0;
	public int Side = 0; //current camera side
	public int CameraSpeed = -40;
	public double x, y, z;			//cameras coordinates
	public int alpha, beta, gamma;	//cameras angles in the beggining
	public Matrix43 matRotate;		//rotation matrix
	//rotation vectors - needed to construct rotation matrix
	public Vector Right, Up, Out, UpW;  
	public Vector Dirrection;
	
	//camera properties
	public double d = 600;
	//public double SightRange = 75000.0;
	//public double ShipSightRange = 250000.0;
	public double SightRange = 30000.0;
	public double ShipSightRange = 100000.0;
	public int leaveObjectSpeed = -100;
	public int ScreenX = 10;
	public int ScreenY = 10;
	public int ScreenWidth = 510;
	public int ScreenHeight = 380;
	public int ScreenShiftX;
	public int ScreenShiftY;
	public final int PitchFactor = 180;
	public final int YawFactor = 180;
	public final int RollFactor = 120;

	//debbuging values
	public double xVector, yVector, zVector;
	public double cxVector, cyVector, czVector;
	public double xAlpha, xBeta;

	public Camera() {
		//set properties by default
		ScreenShiftX = ScreenWidth / 2;
		ScreenShiftY = ScreenHeight / 2;

		x = y = z = 0.0;
		alpha = beta = gamma = 0;
		this.Dirrection = new Vector(0, 0, 1);
		this.setRotate(this.Dirrection);
		this.postRotate();
	}

	public Camera(Strike applet, int iRelatedObject, double x, double y, double z, int alpha, int beta, int gamma){
		this.applet = applet;
        
		//set properties by default
		ScreenShiftX = ScreenWidth / 2;
		ScreenShiftY = ScreenHeight / 2;

		//set start values
		this.iRelatedObject = iRelatedObject;
		this.x = x;
		this.y = y;
		this.z = z;
		this.alpha = alpha;
		this.beta = beta;
		this.gamma = gamma;
		this.Dirrection = new Vector(0, 0, 1);
		this.setRotate(this.Dirrection);
		this.postRotate();
	}

	public void setViewProperties(double focusDistance, int width, int height) {
		d = focusDistance;
		ScreenWidth = width;
		ScreenHeight = height;
		ScreenShiftX = width / 2;
		ScreenShiftY = height / 2;
	}

	public void setRotate(Vector dir) {
		matRotate = new Matrix43();
		Out = new Vector(dir.x, dir.y, dir.z);	
		Out.normalise();
		matRotate.setRow(2, Out);
		
		UpW = new Vector(0, 1, 0);
		double UpWDotOut = UpW.dot(Out);
		Vector MOut = new Vector(Out);
		MOut.mul(UpWDotOut);
		Up = new Vector(UpW);
		Up.sub(MOut);
		if (Up.normalise() == false) System.out.println("Up normalise has been faild!!!");
		matRotate.setRow(1, Up);
		
		Right = new Vector(Up);
		Right.mul(Out);
		Right.normalise();
		matRotate.setRow(0, Right);
	}

	public void postRotate() {
		changeYaw(alpha);
		changePitch(beta);
		changeRoll(gamma);
	}

	public Vector getOutVector() {
		Vector out = new Vector(matRotate.vals[0][2], 
				matRotate.vals[1][2], 
				matRotate.vals[2][2]);
		return out;
	}

	private void checkAlpha() {
		if (alpha < 0) alpha += (360 * 60);
		if (alpha >= (360 * 60)) alpha -= (360 * 60);
	}
	private void checkBeta() {
		if (beta < 0) beta += (360 * 60);
		if (beta >= (360 * 60)) beta -= (360 * 60);
	}
	private void checkGamma() {
		if (gamma < 0) gamma += (360 * 60);
		if (gamma >= (360 * 60)) gamma -= (360 * 60);
	}

	public void rotateAlpha(int d) {
		d %= (360 * 60);
		alpha += d;
		checkAlpha();
	}

	public void rotateBeta(int d) {
		d %= (360 * 60);
		beta += d;
		checkBeta();
	}

	public void rotateGamma(int d) {
		d %= (360 * 60);
		gamma += d;
		checkGamma();
	}

	public void changePitch(int delta) {
		Matrix43 mat = new Matrix43();
		mat.setRotateOX(delta);
		matRotate.mul(mat);
	}
	public void changeYaw(int delta) {
		Matrix43 mat = new Matrix43();
		mat.setRotateOY(delta);
		matRotate.mul(mat);
	}
	public void changeRoll(int delta) {
		Matrix43 mat = new Matrix43();
		mat.setRotateOZ(delta);
		matRotate.mul(mat);
	}

	public void incPitch() {
		Matrix43 mat = new Matrix43();
		mat.setRotateOX(PitchFactor);
		matRotate.mul(mat);
	}
	public void decPitch() {
		Matrix43 mat = new Matrix43();
		mat.setRotateOX(-PitchFactor);
		matRotate.mul(mat);
	}
	public void incYaw() {
		Matrix43 mat = new Matrix43();
		mat.setRotateOY(YawFactor);
		matRotate.mul(mat);
	}
	public void decYaw() {
		Matrix43 mat = new Matrix43();
		mat.setRotateOY(-YawFactor);
		matRotate.mul(mat);
	}
	public void incRoll() {
		Matrix43 mat = new Matrix43();
		mat.setRotateOZ(RollFactor);
		matRotate.mul(mat);
	}
	public void decRoll() {
		Matrix43 mat = new Matrix43();
		mat.setRotateOZ(-RollFactor);
		matRotate.mul(mat);
	}

	public void moveForward(double distance) {
		/*
		Forward camera moving.
		The third row of rotation matrix consists the Out vector projection.
		Since Out vector is pointing exectly in the same dirrection with camera
		we can take all projections and multiplay them on movement value. 
		*/
		Vector movement = new Vector(matRotate.vals[0][2] * distance, 
				matRotate.vals[1][2] * distance, 
				matRotate.vals[2][2] * distance);
		this.x += movement.x;
		this.y += movement.y;
		this.z += movement.z;
	}

	public void moveForward() {
		Vector movement = new Vector(matRotate.vals[0][2] * (CameraSpeed / 2), 
				matRotate.vals[1][2] * (CameraSpeed / 2), 
				matRotate.vals[2][2] * (CameraSpeed / 2));
		this.x += movement.x;
		this.y += movement.y;
		this.z += movement.z;
	}

	public void moveUp(double distance) {
		/*
		Doing the same as forward camera moving but using the Up vector
		*/
		Vector movement = new Vector(matRotate.vals[0][1] * distance, 
				matRotate.vals[1][1] * distance, 
				matRotate.vals[2][1] * distance);
		this.x += movement.x;
		this.y += movement.y;
		this.z += movement.z;
	}

	public void moveStrafe(double distance) {
		/*
		Doing the same as forward camera moving but using the Right vector
		*/
		Vector movement = new Vector(matRotate.vals[0][0] * distance, 
				matRotate.vals[1][0] * distance, 
				matRotate.vals[2][0] * distance);
		this.x += movement.x;
		this.y += movement.y;
		this.z += movement.z;
	}

	public void copyObj(SpaceObject obj) {
		this.x = obj.x;
		this.y = obj.y;
		this.z = obj.z;
		matRotate.copy(obj.matRotate);
	}

	public void leaveObject() {
		this.iRelatedObject = -1;
		this.CameraSpeed = leaveObjectSpeed;
	}
	
	public void clearifyPosition(int w, int h) {
		if (applet.tacticalFriendly.isVisible())
			this.ScreenX = applet.tacticalFriendly.x
				+ applet.tacticalFriendly.w + 10;
		else this.ScreenX = 10;
		this.ScreenY = 10;
		if (applet.tacticalFriendly.isVisible())
			this.ScreenWidth = applet.radar.x
				- applet.tacticalFriendly.w - 30;
		else this.ScreenWidth = applet.radar.x - 20;
		this.ScreenHeight = h - applet.scene.messageScreen.h - 30;
		this.ScreenShiftX = ScreenWidth / 2;
		this.ScreenShiftY = ScreenHeight / 2;
	}
}