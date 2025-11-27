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
	static public Scene scene;
	static public Media media;
	static public Camera camera;
	static public Stars stars;
	public boolean starsRotation = true;
	public int Index = -1;
	public int id = -1;
	public String strObjectName;
	public Model model;
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

	public SpaceObject(String strName, Model model,
						double x, double y, double z) {
		this.colorExtension = false;
		this.strObjectName = strName;
		this.model = model;
		this.x = x;
		this.y = y;
		this.z = z;
		this.currentSpeed = model.startSpeed;
		this.currentFuel = model.FuelTank;
		this.farColor = model.farColor;
		this.Energy = model.Energy;
		this.Shield = model.Shield;
		this.Hull = model.Hull;
		this.lifeTime = model.lifeTime;
		//load launchers
		for (int i = 0; i < this.model.cntLaunchers; i++) {
			this.missilesLoad[i] = model.missileCapacity[i];
		}

		this.Dirrection = new Vector(0, 0, 1);
		this.setRotate(this.Dirrection);
	}

	public SpaceObject(String strName, Model model,
						Color ownColor, double x, double y, double z) {
		this.colorExtension = true;
		this.ownColor = new RColor(ownColor);
		this.strObjectName = strName;
		this.model = model;
		this.x = x;
		this.y = y;
		this.z = z;
		this.currentFuel = model.FuelTank;
		this.Energy = model.Energy;
		this.Shield = model.Shield;
		this.Hull = model.Hull;
		this.lifeTime = model.lifeTime;

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
				da += model.yawAcceleration;
				if (da < 0) da = 0;
			} else {
				da -= model.yawAcceleration;
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
				da += model.pitchAcceleration;
				if (da < 0) da = 0;
			} else {
				da -= model.pitchAcceleration;
				if (da < 0) da = 0;				
			}
			cr += da;
		}
		return cr;
	}


	public void addPitch() {
		oldRotate.copy(matRotate);
		oldPitch = currentPitch;
		if (currentPitch < model.maxPitch)
			if (currentFuel < model.pitchFuelDepletion) return;
			else currentFuel -= model.pitchFuelDepletion;
		
		currentPitch += model.pitchAcceleration;
		if (currentPitch > model.maxPitch) currentPitch = model.maxPitch;
		
		Matrix43 mat = new Matrix43();
		mat.setRotateOX(currentPitch);
		matRotate.mul(mat);
		if (camera.iRelatedObject == this.Index && starsRotation)
				stars.shiftY(currentPitch);
		pitchDrive = 1;

		objRotate.copy(matRotate);
		objRotate.affineInverse();
	}
	public void decPitch() {
		oldRotate.copy(matRotate);
		oldPitch = currentPitch;
		if (currentPitch > -model.maxPitch)
			if (currentFuel < model.pitchFuelDepletion) return;
			else currentFuel -= model.pitchFuelDepletion;
		currentPitch -= model.pitchAcceleration;
		if (currentPitch < -model.maxPitch) currentPitch = -model.maxPitch;
		
		Matrix43 mat = new Matrix43();
		mat.setRotateOX(currentPitch);
		matRotate.mul(mat);
		if (camera.iRelatedObject == this.Index && starsRotation)
				stars.shiftY(currentPitch);
		pitchDrive = 1;

		objRotate.copy(matRotate);
		objRotate.affineInverse();
	}
	public void addYaw() {
		oldRotate.copy(matRotate);
		oldYaw = currentYaw;
		if (currentYaw < model.maxYaw)
			if (currentFuel < model.yawFuelDepletion) return;
			else currentFuel -= model.yawFuelDepletion;
		currentYaw += model.yawAcceleration;
		if (currentYaw > model.maxYaw) currentYaw = model.maxYaw;
		
		Matrix43 mat = new Matrix43();
		mat.setRotateOY(currentYaw);
		matRotate.mul(mat);
		if (camera.iRelatedObject == this.Index && starsRotation)
				stars.shiftX(currentYaw);
		yawDrive = 1;

		objRotate.copy(matRotate);
		objRotate.affineInverse();
	}
	public void decYaw() {
		oldRotate.copy(matRotate);
		oldYaw = currentYaw;
		if (currentYaw > -model.maxYaw)
			if (currentFuel < model.yawFuelDepletion) return;
			else currentFuel -= model.yawFuelDepletion;
		currentYaw -= model.yawAcceleration;
		if (currentYaw < -model.maxYaw) currentYaw = -model.maxYaw;
		
		Matrix43 mat = new Matrix43();
		mat.setRotateOY(currentYaw);
		matRotate.mul(mat);
		if (camera.iRelatedObject == this.Index && starsRotation)
				stars.shiftX(currentYaw);
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
		if (camera.iRelatedObject == this.Index && starsRotation)
				stars.shiftY(delta);

		objRotate.copy(matRotate);
		objRotate.affineInverse();
	}

	public void changeYaw(int delta) {
		oldRotate.copy(matRotate);
		oldYaw = currentYaw;
		Matrix43 mat = new Matrix43();
		mat.setRotateOY(-delta);
		matRotate.mul(mat);
		if (camera.iRelatedObject == this.Index && starsRotation)
					stars.shiftX(-delta);

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
			model.weaponRack[i], this.x, this.y, this.z);
		laser.matRotate.copy(this.matRotate);
		laser.objRotate.copy(this.objRotate);
		laser.moveStrafe(model.rackCoord[i].x);
		laser.moveUp(model.rackCoord[i].y);
		laser.moveForward(model.rackCoord[i].z);
		laser.Source = this.Index;
		laser.Side = this.Side;
		laser.currentSpeed = laser.model.maxSpeed;
		laser.AIControlled = false;
		this.Energy -= laser.model.Shield; //decrease laser energy bank level
		this.weaponRecharge[i] = laser.model.rechargeTime;
		Objects.add(laser);
		this.actionPoints = 20;
		media.auWeapon[laser.model.fx].play();
	}

	public void tryToFire() {
		if (this.model.cntWeapons == 0) return;
	    if (this.Energy >= this.model.weaponRack[0].Shield
		&& this.Energy >= this.model.weaponRack[model.cntWeapons - 1].Shield)
		if (weaponLoad) {
			weaponLoad = false;
			for (int i=0; i<model.cntWeapons; i++) 
				if (this.Energy >= this.model.weaponRack[i].Shield
				    && this.weaponRecharge[i] == 0) Fire(i);
		} else {
			weaponLoad = true;
			for (int i=model.cntWeapons-1; i>=0; i--) 
				if (this.Energy >= this.model.weaponRack[i].Shield
				    && this.weaponRecharge[i] == 0) Fire(i);
		}
	}

	private void Launch(boolean isBindCamera) {
		SpaceObject missile = new SpaceObject("", 
			model.missilesRack[this.currentLauncher], this.x, this.y, this.z);
		missile.matRotate.copy(this.matRotate);
		missile.objRotate.copy(this.objRotate);
		missile.moveStrafe(model.launchersCoord[this.currentLauncher].x);
		missile.moveUp(model.launchersCoord[this.currentLauncher].y);
		missile.moveForward(model.launchersCoord[this.currentLauncher].z);
		missile.Source = this.Index;
		missile.Side = this.Side;
		missile.currentSpeed += this.currentSpeed;
		if (missile.currentSpeed > missile.model.maxSpeed) missile.currentSpeed = missile.model.maxSpeed;
		missile.AIControlled = true;
		if (missile.model.isFighter) {
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
		this.missileRecharge[this.currentLauncher] = missile.model.rechargeTime;
		Objects.add(missile);
		if (isBindCamera) camera.iRelatedObject = missile.Index;
		this.actionPoints = 20;
		media.auWeapon[missile.model.fx].play();
	}

	public void tryToLaunch(boolean isBindCamera) {
		if (this.model.cntLaunchers == 0) return;
		if (this.missilesLoad[currentLauncher] > 0 && this.missileRecharge[currentLauncher] == 0) {
			if (this.taskScreen != -1) Launch(isBindCamera);
		}
	}

	private void TurrentFire(int i, int alpha, int beta) {
		SpaceObject laser = new SpaceObject("", 
			model.weaponRack[i], this.x, this.y, this.z);
		laser.matRotate.copy(this.matRotate);
		laser.objRotate.copy(this.objRotate);
		laser.moveStrafe(model.rackCoord[i].x);
		laser.moveUp(model.rackCoord[i].y);
		laser.moveForward(model.rackCoord[i].z);
		laser.starsRotation = false;
		laser.changeYaw(beta);
		laser.changePitch(-alpha);
		laser.Source = this.Index;
		laser.Side = this.Side;
		laser.currentSpeed = laser.model.maxSpeed;
		laser.AIControlled = false;
		this.Energy -= laser.model.Shield; //decrease laser energy bank level
		this.weaponRecharge[i] = laser.model.rechargeTime;
		Objects.add(laser);
		this.actionPoints = 40;
		media.auWeapon[laser.model.fx].play();
	}


	public void tryToCapFire() {
		if (this.model.cntWeapons == 0) return;
	int cTarget = -1;
	for (int i = 0; i < this.model.cntWeapons; i++) {
		if (weaponRecharge[i] != 0) continue;
		//find enemy object in vicinity 5000 meters
		cTarget = Objects.getNextInSector(this.Index, cTarget, 50000, 
								i, model.weaponSector[i]);
		if (cTarget == -1) continue;
		if (this.Energy < model.weaponRack[i].Shield) break;
		//find dirrection vector
		SpaceObject T = Objects.Objects[cTarget];
		int estimatedTime = (int)this.getDistance(T) / this.model.weaponRack[i].targetFactor << 1;
		T.futureMove(estimatedTime * T.currentSpeed / 2);
		Vector dir = new Vector(
			T.fx - (this.x),
			T.fy - (this.y),
			T.fz - (this.z));
		dir.mul(matRotate);
		dir.x -= model.rackCoord[i].x;
		dir.y -= model.rackCoord[i].y;
		dir.z -= model.rackCoord[i].z;
		int beta = dir.getBeta();
		dir.rotateOY(-beta);
		int alpha = dir.getAlpha();
		TurrentFire(i, alpha, beta);
	}}

	public void increaseSpeed() {
		if (currentFuel < model.fuelDepletion) return;
		currentSpeed += model.acceleration;
		if (currentSpeed > model.maxSpeed) currentSpeed = model.maxSpeed;
		else currentFuel -= model.fuelDepletion;
	}

	public void decreaseSpeed() {
		if (currentFuel < model.fuelDepletion) return;
		currentSpeed -= model.acceleration;
		if (currentSpeed < -model.maxSpeed) currentSpeed = -model.maxSpeed;
		else currentFuel -= model.fuelDepletion;
	}

	public void inertYaw() {
	  if (this.AIControlled || this.HumanControlled)
		if (currentYaw != 0) {
			if (currentFuel >= model.yawFuelDepletion) currentFuel -= model.yawFuelDepletion;
			if (currentYaw > 0) {
				currentYaw -= model.yawAcceleration;
				if (currentYaw < 0) currentYaw = 0;
			} else {
				currentYaw += model.yawAcceleration;
				if (currentYaw > 0) currentYaw = 0;
			}			
		}
	}

	public void inertPitch() {
	  if (this.AIControlled || this.HumanControlled)
		if (currentPitch != 0) {
			if (currentFuel >= model.pitchFuelDepletion) currentFuel -= model.pitchFuelDepletion;
			if (currentPitch > 0) {
				currentPitch -= model.pitchAcceleration;
				if (currentPitch < 0) currentPitch = 0;
			} else {
				currentPitch += model.pitchAcceleration;
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
		if (this.model.cntFrames > 0) {
			this.frame++;
			if (this.frame>this.model.cntFrames) this.frame = 1;
		}
		//action points counter
		if (this.actionPoints > 0) this.actionPoints--;
		//recharge weapon
		for (int i=0; i<this.model.cntWeapons; i++) {
			if (this.weaponRecharge[i] > 0) this.weaponRecharge[i]--;
		}
		//recharge missile
		for (int i=0; i<this.model.cntLaunchers; i++) {
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
		if (this.model.isFighter || this.model.isShip) {
			//energy control
			if (this.Energy < this.model.Energy) {
				this.Energy += this.model.energyRecharge;
				if (this.Energy > this.model.Energy) this.Energy = this.model.Energy;
			}
 			//shield control
			if (this.Shield < this.model.Shield && this.Energy >= this.model.shieldRecharge) {
				this.eBuffer += this.model.shieldRecharge;
				this.Energy -= this.model.shieldRecharge;
				this.Shield += this.eBuffer / 20;
				this.eBuffer = this.eBuffer % 20;
				if (this.Shield > this.model.Shield)  this.Shield = this.model.Shield;
			}
			if (this.Shield == this.model.Shield) this.hits = 0;
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
		if (this.currentLauncher >= this.model.cntLaunchers) this.currentLauncher = 0;
	}

	public void switchLauncher() {
		int i = this.currentLauncher;
		while (i < this.model.cntLaunchers
		       && this.missilesLoad[i] == 0) i++;
		if (i == this.model.cntLaunchers) {
			int j = 0;
			while (j < this.model.cntLaunchers
			       && this.missilesLoad[j] == 0) j++;
			if (j < this.model.cntLaunchers) this.currentLauncher = j;
		} else this.currentLauncher = i;
	}

	public void createFragments() {
		for (int i = 0; i < this.model.fragments; i++) {
			SpaceObject fragment = new SpaceObject("fragment",
			this.model.fragment, this.x, this.y, this.z);
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
		for (int i = 0; i < this.model.blows; i++) {
			SpaceObject blow = new SpaceObject("blow",
			this.model.blow, this.x, this.y, this.z);

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
			scene.Interpreter.setVariable("ReachedObject", Objects.Objects[this.currentTarget].strObjectName);
			scene.Interpreter.setVariable("ActionObject", this.strObjectName);
			scene.Interpreter.runProc(this.reachProcedure);
		}
	}

	public void kill(int bySide) {
		this.isLive = false;
		
		//fragments control
		if ((this.model.isShip || this.model.isFighter
			|| this.model.isStatic) && this.model.fragment != null)
				createFragments();

		//blows control
		if ((this.model.isShip || this.model.isFighter
			|| this.model.isStatic) && this.model.blow != null)
				createBlows();

		//score control
		if (this.model.isShip || this.model.isFighter 
			|| this.model.isStatic || this.model.isMissile) {
			//reduce score from own side
			int iScore = scene.Interpreter.getiVariable("Score" + this.Side);
			iScore -= this.model.Cost;
			scene.Interpreter.setVariable("Score" + this.Side, iScore);
			//add score to foe
			if (bySide != -1 && bySide != this.Side) {
				iScore = scene.Interpreter.getiVariable("Score" + bySide);
				iScore += this.model.Cost;
				scene.Interpreter.setVariable("Score" + bySide, iScore);
			}
	
			if (this.model.isShip) {
				int iCount = scene.Interpreter.getiVariable("ShipsLost" + this.Side);
				iCount ++; scene.Interpreter.setVariable("ShipsLost" + this.Side, iCount);
				if (bySide != -1 && bySide != this.Side) {
					iCount = scene.Interpreter.getiVariable("ShipsDestroy" + bySide);
					iCount ++; scene.Interpreter.setVariable("ShipsDestroy" + bySide, iCount);
				}
			}
			if (this.model.isFighter) {
				int iCount = scene.Interpreter.getiVariable("FightersLost" + this.Side);
				iCount ++; scene.Interpreter.setVariable("FightersLost" + this.Side, iCount);
				if (bySide != -1 && bySide != this.Side) {
					iCount = scene.Interpreter.getiVariable("FightersDestroy" + bySide);
					iCount ++; scene.Interpreter.setVariable("FightersDestroy" + bySide, iCount);
				}
			}
			if (this.model.isStatic) {
				int iCount = scene.Interpreter.getiVariable("StaticLost" + this.Side);
				iCount ++; scene.Interpreter.setVariable("StaticLost" + this.Side, iCount);
				if (bySide != -1 && bySide != this.Side) {
					iCount = scene.Interpreter.getiVariable("StaticDestroy" + bySide);
					iCount ++; scene.Interpreter.setVariable("StaticDestroy" + bySide, iCount);
				}
			}
		}

		//proc control
		if (this.killProcedure != -1) scene.Interpreter.runProc(this.killProcedure);
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