package scene;

import java.awt.*;
import math.*;
import primitives.RColor;
import mission.MissionInterpreter;
import mission.DataElement;
import scene.SOCollector;
import scene.Camera;
import scene.Action;
import render.Stars;
import control.Task;
import media.Media;

public class SpaceObject {
	static public SOCollector Objects;
	static public Scene theScene;
	static public Media theMedia;
	static public Camera theCamera;
	static public Stars theStars;
	public boolean starsRotation = true;
	public int Index = -1;
	public int id = -1;
	public String strObjectName;
	public Model theModel;
	public int Platform = -1; //object
	public int Source = -1; //object
	public int frame = 0;
	public double x, y, z;
	public double fx, fy, fz;
	public int alpha, beta, gamma;
	public Matrix43 matRotate;		//rotation matrix
	public Matrix43 objRotate;		//object rotation matrix (inverse to matRotate)
	public Matrix43 oldRotate;
	public Matrix43 dirRotate;
	//rotation vectors - needed to construct rotation matrix
	public Vector Right, Up, Out, UpW;  
	public Vector Dirrection;

	//run-time
	public int killProcedure = -1;
	public int reachProcedure = -1;

	public boolean colorExtension;
	public RColor ownColor;
	public RColor farColor;

	//ships properties
	public boolean isLive = true;
	public int lifeTime = -1;
	public int Side = 0;
	public boolean PlayerPlaced = true;
	public boolean PlayerControlled = true;
	public boolean AIControlled = true;
	public boolean MissionControlled = false;
	public boolean HumanControlled = false;
	public boolean NetControlled = false;

	public int currentFuel = 0;
	public int currentSpeed = 0;
	public int adjustedSpeed = 0;
	public int currentYaw = 0;
	public int oldYaw = 0;
	public int yawDrive = 0;
	public int currentPitch = 0;
	public int oldPitch = 0;
	public int pitchDrive = 0;
	public int currentRoll = 0;
	public int rollDrive = 0;
	public int Energy;
	public int eBuffer = 0;
	public int Shield;
	public int Hull;

	//weapon systems
	public int weaponRecharge[] = new int[Model.maxWeapons];
	public boolean weaponLoad = true;
	public int currentLauncher = 0;
	public int missilesLoad[] = new int[Model.maxLaunchers];
	public int missileRecharge[] = new int[Model.maxLaunchers];

	//targeting system
	public int flightPlan = -1;
	public int flightPlanPosition = 0;
	public int priManoeuvre = 0;
	public int secManoeuvre = 0;
	public int typeManoeuvre = 0;
	public int typeAManoeuvre = 0;
	public int typeBManoeuvre = 0;
	public int currentTarget = -1;
	public int targetType = 0;
	public int Tid = -1;
	public boolean isPrimary = true;
	public int taskScreen = -1;
	public int taskScreenID = -1;
	public int agressionLevel = 1;
	public Task currentTask = new Task();
	public int hits = 0;
	public int actionPoints = 0;

	public SpaceObject(String strName, Model theModel,
						double x, double y, double z) {
		this.colorExtension = false;
		this.strObjectName = strName;
		this.theModel = theModel;
		this.x = x;
		this.y = y;
		this.z = z;
		this.currentSpeed = theModel.startSpeed;
		this.currentFuel = theModel.FuelTank;
		this.farColor = theModel.farColor;
		this.Energy = theModel.Energy;
		this.Shield = theModel.Shield;
		this.Hull = theModel.Hull;
		this.lifeTime = theModel.lifeTime;
		//load launchers
		for (int i = 0; i < this.theModel.cntLaunchers; i++) {
			this.missilesLoad[i] = theModel.missileCapacity[i];
		}

		this.Dirrection = new Vector(0, 0, 1);
		this.setRotate(this.Dirrection);
	}

	public SpaceObject(String strName, Model theModel,
						Color ownColor, double x, double y, double z) {
		this.colorExtension = true;
		this.ownColor = new RColor(ownColor);
		this.strObjectName = strName;
		this.theModel = theModel;
		this.x = x;
		this.y = y;
		this.z = z;
		this.currentFuel = theModel.FuelTank;
		this.Energy = theModel.Energy;
		this.Shield = theModel.Shield;
		this.Hull = theModel.Hull;
		this.lifeTime = theModel.lifeTime;

		this.Dirrection = new Vector(0, 0, 1);
		this.setRotate(this.Dirrection);
		this.postRotate();
	}

	public Vector getOutVector() {
		Vector out = new Vector(matRotate.vals[0][2], 
				matRotate.vals[1][2], 
				matRotate.vals[2][2]);
		return out;
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
		if (Up.normalise() == false) System.out.println("Error! Up normalise has been faild!!!");
		matRotate.setRow(1, Up);
		
		Right = new Vector(Up);
		Right.mul(Out);
		Right.normalise();
		matRotate.setRow(0, Right);

		objRotate = new Matrix43(matRotate);
		objRotate.affineInverse();
		oldRotate = new Matrix43(matRotate);
	}

	public void postRotate() {
		changeYaw(alpha);
		changePitch(beta);
		changeRoll(gamma);
	}

	public void undoPitch() {
		matRotate.copy(oldRotate);
		objRotate.copy(matRotate);
		objRotate.affineInverse();
		currentPitch = oldPitch;
		pitchDrive = 0;
	}
	public void undoYaw() {
		matRotate.copy(oldRotate);
		objRotate.copy(matRotate);
		objRotate.affineInverse();
		currentYaw = oldYaw;
		yawDrive = 0;
	}

	public int getYawCorrection(int base) {
		int cr = base;
		int da = currentYaw + base;
		while (da != 0) {
			if (da < 0) {
				da += theModel.yawAcceleration;
				if (da < 0) da = 0;
			} else {
				da -= theModel.yawAcceleration;
				if (da < 0) da = 0;				
			}
			cr += da;
		}
		return cr;
	}

	public int getPitchCorrection(int base) {
		int cr = base;
		int da = currentPitch + base;
		while (da != 0) {
			if (da < 0) {
				da += theModel.pitchAcceleration;
				if (da < 0) da = 0;
			} else {
				da -= theModel.pitchAcceleration;
				if (da < 0) da = 0;				
			}
			cr += da;
		}
		return cr;
	}


	public void addPitch() {
		oldRotate.copy(matRotate);
		oldPitch = currentPitch;
		if (currentPitch < theModel.maxPitch)
			if (currentFuel < theModel.pitchFuelDepletion) return;
			else currentFuel -= theModel.pitchFuelDepletion;
		
		currentPitch += theModel.pitchAcceleration;
		if (currentPitch > theModel.maxPitch) currentPitch = theModel.maxPitch;
		
		Matrix43 mat = new Matrix43();
		mat.setRotateOX(currentPitch);
		matRotate.mul(mat);
		if (theCamera.iRelatedObject == this.Index && starsRotation)
				theStars.shiftY(currentPitch);
		pitchDrive = 1;

		objRotate.copy(matRotate);
		objRotate.affineInverse();
	}
	public void decPitch() {
		oldRotate.copy(matRotate);
		oldPitch = currentPitch;
		if (currentPitch > -theModel.maxPitch)
			if (currentFuel < theModel.pitchFuelDepletion) return;
			else currentFuel -= theModel.pitchFuelDepletion;
		currentPitch -= theModel.pitchAcceleration;
		if (currentPitch < -theModel.maxPitch) currentPitch = -theModel.maxPitch;
		
		Matrix43 mat = new Matrix43();
		mat.setRotateOX(currentPitch);
		matRotate.mul(mat);
		if (theCamera.iRelatedObject == this.Index && starsRotation)
				theStars.shiftY(currentPitch);
		pitchDrive = 1;

		objRotate.copy(matRotate);
		objRotate.affineInverse();
	}
	public void addYaw() {
		oldRotate.copy(matRotate);
		oldYaw = currentYaw;
		if (currentYaw < theModel.maxYaw)
			if (currentFuel < theModel.yawFuelDepletion) return;
			else currentFuel -= theModel.yawFuelDepletion;
		currentYaw += theModel.yawAcceleration;
		if (currentYaw > theModel.maxYaw) currentYaw = theModel.maxYaw;
		
		Matrix43 mat = new Matrix43();
		mat.setRotateOY(currentYaw);
		matRotate.mul(mat);
		if (theCamera.iRelatedObject == this.Index && starsRotation)
				theStars.shiftX(currentYaw);
		yawDrive = 1;

		objRotate.copy(matRotate);
		objRotate.affineInverse();
	}
	public void decYaw() {
		oldRotate.copy(matRotate);
		oldYaw = currentYaw;
		if (currentYaw > -theModel.maxYaw)
			if (currentFuel < theModel.yawFuelDepletion) return;
			else currentFuel -= theModel.yawFuelDepletion;
		currentYaw -= theModel.yawAcceleration;
		if (currentYaw < -theModel.maxYaw) currentYaw = -theModel.maxYaw;
		
		Matrix43 mat = new Matrix43();
		mat.setRotateOY(currentYaw);
		matRotate.mul(mat);
		if (theCamera.iRelatedObject == this.Index && starsRotation)
				theStars.shiftX(currentYaw);
		yawDrive = 1;

		objRotate.copy(matRotate);
		objRotate.affineInverse();
	}


	public void changePitch(int delta) {
		oldRotate.copy(matRotate);
		oldPitch = currentPitch;
		Matrix43 mat = new Matrix43();
		mat.setRotateOX(delta);
		matRotate.mul(mat);
		if (theCamera.iRelatedObject == this.Index && starsRotation)
				theStars.shiftY(delta);

		objRotate.copy(matRotate);
		objRotate.affineInverse();
	}

	public void changeYaw(int delta) {
		oldRotate.copy(matRotate);
		oldYaw = currentYaw;
		Matrix43 mat = new Matrix43();
		mat.setRotateOY(-delta);
		matRotate.mul(mat);
		if (theCamera.iRelatedObject == this.Index && starsRotation)
					theStars.shiftX(-delta);

		objRotate.copy(matRotate);
		objRotate.affineInverse();
	}

	public void changeRoll(int delta) {
		Matrix43 mat = new Matrix43();
		mat.setRotateOZ(delta);
		matRotate.mul(mat);

		objRotate.copy(matRotate);
		objRotate.affineInverse();
	}

	public void changeDirPitch(int delta) {
		Matrix43 mat = new Matrix43();
		mat.setRotateOX(delta);
		dirRotate.mul(mat);
	}

	public void changeDirYaw(int delta) {
		Matrix43 mat = new Matrix43();
		mat.setRotateOY(-delta);
		dirRotate.mul(mat);
	}

	public void moveForward(double distance) {
		/*
		Forward camera moving.
		The third row of rotation matrix consists the Out vector projection.
		Since Out vector is pointing exectly in the same dirrection with camera
		we can take all projections and multiplay them on movement value. 
		*/
		Vector movement;
		if (dirRotate == null)
			movement = new Vector(matRotate.vals[0][2] * distance, 
					matRotate.vals[1][2] * distance, 
					matRotate.vals[2][2] * distance);
		else
			movement = new Vector(dirRotate.vals[0][2] * distance, 
					dirRotate.vals[1][2] * distance, 
					dirRotate.vals[2][2] * distance);

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

	public void futureMove(double distance) {
		Vector movement = new Vector(matRotate.vals[0][2] * distance, 
				matRotate.vals[1][2] * distance, 
				matRotate.vals[2][2] * distance);
		this.fx = this.x + movement.x;
		this.fy = this.y + movement.y;
		this.fz = this.z + movement.z;
	}

	private void Fire(int i) {
		SpaceObject laser = new SpaceObject("", 
			theModel.weaponRack[i], this.x, this.y, this.z);
		laser.matRotate.copy(this.matRotate);
		laser.objRotate.copy(this.objRotate);
		laser.moveStrafe(theModel.rackCoord[i].x);
		laser.moveUp(theModel.rackCoord[i].y);
		laser.moveForward(theModel.rackCoord[i].z);
		laser.Source = this.Index;
		laser.Side = this.Side;
		laser.currentSpeed = laser.theModel.maxSpeed;
		laser.AIControlled = false;
		this.Energy -= laser.theModel.Shield; //decrease laser energy bank level
		this.weaponRecharge[i] = laser.theModel.rechargeTime;
		Objects.add(laser);
		this.actionPoints = 20;
		theMedia.auWeapon[laser.theModel.fx].play();
	}

	public void tryToFire() {
		if (this.theModel.cntWeapons == 0) return;
	    if (this.Energy >= this.theModel.weaponRack[0].Shield
		&& this.Energy >= this.theModel.weaponRack[theModel.cntWeapons - 1].Shield)
		if (weaponLoad) {
			weaponLoad = false;
			for (int i=0; i<theModel.cntWeapons; i++) 
				if (this.Energy >= this.theModel.weaponRack[i].Shield
				    && this.weaponRecharge[i] == 0) Fire(i);
		} else {
			weaponLoad = true;
			for (int i=theModel.cntWeapons-1; i>=0; i--) 
				if (this.Energy >= this.theModel.weaponRack[i].Shield
				    && this.weaponRecharge[i] == 0) Fire(i);
		}
	}

	private void Launch(boolean isBindCamera) {
		SpaceObject missile = new SpaceObject("", 
			theModel.missilesRack[this.currentLauncher], this.x, this.y, this.z);
		missile.matRotate.copy(this.matRotate);
		missile.objRotate.copy(this.objRotate);
		missile.moveStrafe(theModel.launchersCoord[this.currentLauncher].x);
		missile.moveUp(theModel.launchersCoord[this.currentLauncher].y);
		missile.moveForward(theModel.launchersCoord[this.currentLauncher].z);
		missile.Source = this.Index;
		missile.Side = this.Side;
		missile.currentSpeed += this.currentSpeed;
		if (missile.currentSpeed > missile.theModel.maxSpeed) missile.currentSpeed = missile.theModel.maxSpeed;
		missile.AIControlled = true;
		if (missile.theModel.isFighter) {
			missile.currentTask.iType = Task.Defend;
			missile.currentTask.iTarget = this.Index;
			missile.currentTask.id = this.id;
			missile.agressionLevel = 4;
		} else {
			missile.currentTask.iType = Task.Attack;
			missile.currentTask.iTarget = this.taskScreen;
			missile.currentTask.id = Objects.Objects[this.taskScreen].id;
			missile.taskScreen = this.taskScreen;
		}
		this.missilesLoad[currentLauncher]--;
		this.missileRecharge[this.currentLauncher] = missile.theModel.rechargeTime;
		Objects.add(missile);
		if (isBindCamera) theCamera.iRelatedObject = missile.Index;
		this.actionPoints = 20;
		theMedia.auWeapon[missile.theModel.fx].play();
	}

	public void tryToLaunch(boolean isBindCamera) {
		if (this.theModel.cntLaunchers == 0) return;
		if (this.missilesLoad[currentLauncher] > 0 && this.missileRecharge[currentLauncher] == 0) {
			if (this.taskScreen != -1) Launch(isBindCamera);
		}
	}

	private void TurrentFire(int i, int alpha, int beta) {
		SpaceObject laser = new SpaceObject("", 
			theModel.weaponRack[i], this.x, this.y, this.z);
		laser.matRotate.copy(this.matRotate);
		laser.objRotate.copy(this.objRotate);
		laser.moveStrafe(theModel.rackCoord[i].x);
		laser.moveUp(theModel.rackCoord[i].y);
		laser.moveForward(theModel.rackCoord[i].z);
		laser.starsRotation = false;
		laser.changeYaw(beta);
		laser.changePitch(-alpha);
		laser.Source = this.Index;
		laser.Side = this.Side;
		laser.currentSpeed = laser.theModel.maxSpeed;
		laser.AIControlled = false;
		this.Energy -= laser.theModel.Shield; //decrease laser energy bank level
		this.weaponRecharge[i] = laser.theModel.rechargeTime;
		Objects.add(laser);
		this.actionPoints = 40;
		theMedia.auWeapon[laser.theModel.fx].play();
	}


	public void tryToCapFire() {
		if (this.theModel.cntWeapons == 0) return;
	int cTarget = -1;
	for (int i = 0; i < this.theModel.cntWeapons; i++) {
		if (weaponRecharge[i] != 0) continue;
		//find enemy object in vicinity 5000 meters
		cTarget = Objects.getNextInSector(this.Index, cTarget, 50000, 
								i, theModel.weaponSector[i]);
		if (cTarget == -1) continue;
		if (this.Energy < theModel.weaponRack[i].Shield) break;
		//find dirrection vector
		SpaceObject T = Objects.Objects[cTarget];
		int estimatedTime = (int)this.getDistance(T) / this.theModel.weaponRack[i].targetFactor << 1;
		T.futureMove(estimatedTime * T.currentSpeed / 2);
		Vector dir = new Vector(
			T.fx - (this.x),
			T.fy - (this.y),
			T.fz - (this.z));
		dir.mul(matRotate);
		dir.x -= theModel.rackCoord[i].x;
		dir.y -= theModel.rackCoord[i].y;
		dir.z -= theModel.rackCoord[i].z;
		int beta = dir.getBeta();
		dir.rotateOY(-beta);
		int alpha = dir.getAlpha();
		TurrentFire(i, alpha, beta);
	}}

	public void increaseSpeed() {
		if (currentFuel < theModel.fuelDepletion) return;
		currentSpeed += theModel.acceleration;
		if (currentSpeed > theModel.maxSpeed) currentSpeed = theModel.maxSpeed;
		else currentFuel -= theModel.fuelDepletion;
	}

	public void decreaseSpeed() {
		if (currentFuel < theModel.fuelDepletion) return;
		currentSpeed -= theModel.acceleration;
		if (currentSpeed < -theModel.maxSpeed) currentSpeed = -theModel.maxSpeed;
		else currentFuel -= theModel.fuelDepletion;
	}

	public void inertYaw() {
	  if (this.AIControlled || this.HumanControlled)
		if (currentYaw != 0) {
			if (currentFuel >= theModel.yawFuelDepletion) currentFuel -= theModel.yawFuelDepletion;
			if (currentYaw > 0) {
				currentYaw -= theModel.yawAcceleration;
				if (currentYaw < 0) currentYaw = 0;
			} else {
				currentYaw += theModel.yawAcceleration;
				if (currentYaw > 0) currentYaw = 0;
			}			
		}
	}

	public void inertPitch() {
	  if (this.AIControlled || this.HumanControlled)
		if (currentPitch != 0) {
			if (currentFuel >= theModel.pitchFuelDepletion) currentFuel -= theModel.pitchFuelDepletion;
			if (currentPitch > 0) {
				currentPitch -= theModel.pitchAcceleration;
				if (currentPitch < 0) currentPitch = 0;
			} else {
				currentPitch += theModel.pitchAcceleration;
				if (currentPitch > 0) currentPitch = 0;
			}
		}
	}

	public void setTaskScreen(int i) {	
		if (i < 0) return;
		this.taskScreen = i;
		this.taskScreenID = Objects.Objects[i].id;		
	}

	public void moveControl() {
		//task screen control
		if (this.taskScreen >= 0)
			if (Objects.Objects[this.taskScreen].isLive == false
				|| Objects.Objects[this.taskScreen].id != this.taskScreenID) {
				this.taskScreen = -1;
				this.taskScreenID = -1;
			}

		//change current frame
		if (this.theModel.cntFrames > 0) {
			this.frame++;
			if (this.frame>this.theModel.cntFrames) this.frame = 1;
		}
		//action points counter
		if (this.actionPoints > 0) this.actionPoints--;
		//recharge weapon
		for (int i=0; i<this.theModel.cntWeapons; i++) {
			if (this.weaponRecharge[i] > 0) this.weaponRecharge[i]--;
		}
		//recharge missile
		for (int i=0; i<this.theModel.cntLaunchers; i++) {
			if (this.missileRecharge[i] > 0) this.missileRecharge[i]--;
		}
		//inert rotate
		if (pitchDrive != 0) pitchDrive--;
		else {
			inertPitch();
			if (currentPitch != 0) changePitch(currentPitch);
		}
		if (yawDrive != 0) yawDrive--;
		else {
			inertYaw();			
			if (currentYaw != 0) changeYaw(-currentYaw);
		}
		//move
		this.moveForward(this.currentSpeed >> 1);
	}

	public void energyControl() {
		if (this.theModel.isFighter || this.theModel.isShip) {
			//energy control
			if (this.Energy < this.theModel.Energy) {
				this.Energy += this.theModel.energyRecharge;
				if (this.Energy > this.theModel.Energy) this.Energy = this.theModel.Energy;
			}
 			//shield control
			if (this.Shield < this.theModel.Shield && this.Energy >= this.theModel.shieldRecharge) {
				this.eBuffer += this.theModel.shieldRecharge;
				this.Energy -= this.theModel.shieldRecharge;
				this.Shield += this.eBuffer / 20;
				this.eBuffer = this.eBuffer % 20;
				if (this.Shield > this.theModel.Shield)  this.Shield = this.theModel.Shield;
			}
			if (this.Shield == this.theModel.Shield) this.hits = 0;
		}
	}

	public double getDistance(SpaceObject obj) {
		double x = this.x - obj.x;
		double y = this.y - obj.y;
		double z = this.z - obj.z;
		x *= x;
		y *= y;
		z *= z;
		return CMath.M.sqrt(x + y + z);
	}

	public void setPrimaryTarget(int i) {
		if (i < 0) return;
		currentTask.iTarget = i;
		currentTask.id = Objects.Objects[i].id;
	}

	public void setSecondaryTarget(int i) {
		if (i < 0) return;
		this.currentTask.iSecondaryTarget = i;
		this.currentTask.sid = Objects.Objects[i].id;
	}

	public void setTarget(boolean isPrimary) {
		if (isPrimary && currentTask.isActual) {
			this.currentTarget = this.currentTask.iTarget;
			this.Tid = this.currentTask.id;
			this.targetType = this.currentTask.iType;
			this.isPrimary = true;
		} else if (!isPrimary && currentTask.isSecondaryActual){
			this.currentTarget = this.currentTask.iSecondaryTarget;
			this.Tid = this.currentTask.sid;
			this.targetType = this.currentTask.iType;
			this.isPrimary = false;
		}
	}

	public void resetTarget() {
		this.currentTarget = Action.None;
		this.typeManoeuvre = 0;
		this.targetType = 0;
	}

	public void avoidFire() {
		this.currentTarget = Action.AvoidFire;
		this.typeAManoeuvre = (int)(CMath.R.nextFloat()*8);
		this.typeBManoeuvre = (int)(CMath.R.nextFloat()*8);
		this.priManoeuvre = (int)(CMath.R.nextFloat()*4) * 10 + 10;
		this.secManoeuvre = (int)(CMath.R.nextFloat()*4) * 10 + 10;
		this.actionPoints += 40;
	}

	public void switchToNextLauncher() {
		this.currentLauncher++;
		if (this.currentLauncher >= this.theModel.cntLaunchers) this.currentLauncher = 0;
	}

	public void switchLauncher() {
		int i = this.currentLauncher;
		while (i < this.theModel.cntLaunchers
		       && this.missilesLoad[i] == 0) i++;
		if (i == this.theModel.cntLaunchers) {
			int j = 0;
			while (j < this.theModel.cntLaunchers
			       && this.missilesLoad[j] == 0) j++;
			if (j < this.theModel.cntLaunchers) this.currentLauncher = j;
		} else this.currentLauncher = i;
	}

	public void createFragments() {
		for (int i = 0; i < this.theModel.fragments; i++) {
			SpaceObject fragment = new SpaceObject("fragment",
			this.theModel.theFragment, this.x, this.y, this.z);
			fragment.dirRotate = new Matrix43();
			fragment.dirRotate.copy(this.matRotate);

			int alpha = (int)(CMath.R.nextFloat() * 21600);
			int beta = (int)(CMath.R.nextFloat() * 21600);
			int yawSpeed = (int)(CMath.R.nextFloat() * 20) * 60;
			int pitchSpeed = (int)(CMath.R.nextFloat() * 20) * 60;
			int speed = ((int)(CMath.R.nextFloat() * 15) + 1) * 15;
			int life = (int)(CMath.R.nextFloat() * 200);
			int iframe = (int)(CMath.R.nextFloat() * 9) + 1;
			
			fragment.changeDirYaw(alpha);
			fragment.changeDirPitch(beta);
			fragment.currentYaw = yawSpeed;
			fragment.currentPitch = pitchSpeed;
			fragment.frame = iframe;
			fragment.lifeTime = life;
			fragment.Source = this.Index;
			fragment.currentSpeed = speed;
			fragment.AIControlled = false;
			Objects.add(fragment);
		}
	}

	public void createBlows() {
		for (int i = 0; i < this.theModel.blows; i++) {
			SpaceObject blow = new SpaceObject("blow",
			this.theModel.theBlow, this.x, this.y, this.z);

			int yawSpeed = (int)(CMath.R.nextFloat() * 10) * 200 + 100;
			int pitchSpeed = (int)(CMath.R.nextFloat() * 10) * 200 + 100;    
			int iframe = (int)(CMath.R.nextFloat() * 9) + 1;
			
			blow.currentSpeed = 0;
			blow.currentYaw = yawSpeed;
			blow.currentPitch = pitchSpeed;
			blow.frame = iframe;
			blow.Source = this.Index;
			blow.AIControlled = false;
			Objects.add(blow);
		}
	}

	public void reachTarget() {
		//proc control
		if (this.reachProcedure != -1) {
			theScene.Interpreter.setVariable("ReachedObject", Objects.Objects[this.currentTarget].strObjectName);
			theScene.Interpreter.setVariable("ActionObject", this.strObjectName);
			theScene.Interpreter.runProc(this.reachProcedure);
		}
	}

	public void kill(int bySide) {
		this.isLive = false;
		
		//fragments control
		if ((this.theModel.isShip || this.theModel.isFighter
			|| this.theModel.isStatic) && this.theModel.theFragment != null)
				createFragments();

		//blows control
		if ((this.theModel.isShip || this.theModel.isFighter
			|| this.theModel.isStatic) && this.theModel.theBlow != null)
				createBlows();

		//score control
		if (this.theModel.isShip || this.theModel.isFighter 
			|| this.theModel.isStatic || this.theModel.isMissile) {
			//reduce score from own side
			int iScore = theScene.Interpreter.getiVariable("Score" + this.Side);
			iScore -= this.theModel.Cost;
			theScene.Interpreter.setVariable("Score" + this.Side, iScore);
			//add score to foe
			if (bySide != -1 && bySide != this.Side) {
				iScore = theScene.Interpreter.getiVariable("Score" + bySide);
				iScore += this.theModel.Cost;
				theScene.Interpreter.setVariable("Score" + bySide, iScore);
			}
	
			if (this.theModel.isShip) {
				int iCount = theScene.Interpreter.getiVariable("ShipsLost" + this.Side);
				iCount ++; theScene.Interpreter.setVariable("ShipsLost" + this.Side, iCount);
				if (bySide != -1 && bySide != this.Side) {
					iCount = theScene.Interpreter.getiVariable("ShipsDestroy" + bySide);
					iCount ++; theScene.Interpreter.setVariable("ShipsDestroy" + bySide, iCount);
				}
			}
			if (this.theModel.isFighter) {
				int iCount = theScene.Interpreter.getiVariable("FightersLost" + this.Side);
				iCount ++; theScene.Interpreter.setVariable("FightersLost" + this.Side, iCount);
				if (bySide != -1 && bySide != this.Side) {
					iCount = theScene.Interpreter.getiVariable("FightersDestroy" + bySide);
					iCount ++; theScene.Interpreter.setVariable("FightersDestroy" + bySide, iCount);
				}
			}
			if (this.theModel.isStatic) {
				int iCount = theScene.Interpreter.getiVariable("StaticLost" + this.Side);
				iCount ++; theScene.Interpreter.setVariable("StaticLost" + this.Side, iCount);
				if (bySide != -1 && bySide != this.Side) {
					iCount = theScene.Interpreter.getiVariable("StaticDestroy" + bySide);
					iCount ++; theScene.Interpreter.setVariable("StaticDestroy" + bySide, iCount);
				}
			}
		}

		//proc control
		if (this.killProcedure != -1) theScene.Interpreter.runProc(this.killProcedure);
	}

	public void takeTarget(SpaceObject W) {
		this.currentTarget = W.currentTarget;
		this.targetType = W.targetType;
		this.Tid = W.Tid;
	}

	public void takeControl() {
		if (this.HumanControlled) this.HumanControlled = false;
		else if (this.PlayerControlled) this.HumanControlled = true;
	}
}