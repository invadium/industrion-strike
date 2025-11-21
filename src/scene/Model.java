package scene;

import java.awt.*;
import primitives.Coord3D;
import primitives.RColor;
import primitives.Primitive;

public class Model {
	public static final int maxWeapons = 24;
	public static final int maxLaunchers = 8;
	public boolean isShip = false;
	public boolean isFighter = false;
	public boolean isStatic = false;
	public boolean isWeapon = false;
	public boolean isMissile = false;
	public boolean isSpace = false;
	public int createdObjects = 0;
	public int Cost = 100;
	public String strModelName;
	public String strIDName;
	public Image imgModel;
	public double radius;
	public double range; //range for missile
	public static final int maxPrimitives = 100;
	public int cntFrames = 0;
	public int cntPrimitives;
	public Primitive thePrimitives[];
	public Model theFragment;
	public Model theBlow;

	//AI
	public double reachRange = 5000;

	//weapon systems
	public int fx = 0;
	public int targetFactor = 1600;
	public int rechargeTime = 0;
	public int cntWeapons = 0;
	public int fragments = 10;
	public int blows = 5;
	public int lifeTime = -1;
	public Model weaponRack[] = new Model[maxWeapons];
	public Coord3D rackCoord[] = new Coord3D[maxWeapons];
	public byte weaponSector[] = new byte[maxWeapons];
	public int cntLaunchers = 0;
	public Coord3D launchersCoord[] = new Coord3D[maxLaunchers];
	public Model missilesRack[] = new Model[maxLaunchers];
	public int missileCapacity[] = new int[maxLaunchers];

	//ship properties
	public RColor farColor = new RColor(200, 200, 200);
	public int farSize = 1;
	public int radarMark = 1;
	public int modelType = 0;
	public int FuelTank = 0;
	public int startSpeed = 0;
	public int maxSpeed = 100;
	public int cruiseSpeed = 75;
	public int acceleration = 1;
	public int fuelDepletion = 10;
	public int maxYaw = 60;
	public int maxPitch = 60;
	public int maxRoll = 60;
	public int yawAcceleration = 10;
	public int pitchAcceleration = 10;
	public int rollAcceleration = 10;
	public int yawFuelDepletion = 10;
	public int pitchFuelDepletion = 10;
	public int rollFuelDepletion = 10;
	public int Shield = 100;
	public int Hull = 100;
	public int Energy = 100;
	public int shieldRecharge = 1;
	public int energyRecharge = 1;

	public Model () {
		this.strModelName = "";
		thePrimitives = new Primitive[maxPrimitives];
		cntPrimitives = 0;
	}
	
	public Model (String strModelName) {
		this.strModelName = strModelName;
		thePrimitives = new Primitive[maxPrimitives];
		cntPrimitives = 0;
	}

	public boolean addPrimitive(Primitive thePrimitive) {
		if (cntPrimitives >= maxPrimitives) return false;
		thePrimitives[cntPrimitives] = thePrimitive;
		cntPrimitives++;
		return true;
	}

	public void increaseWeaponScale(int index, double factor) {
		rackCoord[index].x *= factor;
		rackCoord[index].y *= factor;
		rackCoord[index].z *= factor;
	}

	public void reduceWeaponScale(int index, double factor) {
		rackCoord[index].x /= factor;
		rackCoord[index].y /= factor;
		rackCoord[index].z /= factor;		
	}

	public void shiftWeapon(int index, double sx, double sy, double sz) {
		rackCoord[index].x += sx;
		rackCoord[index].y += sy;
		rackCoord[index].z += sz;
	}
}