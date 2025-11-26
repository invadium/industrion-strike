package scene;

import math.CMath;
import scene.Camera;
import control.Task;
import control.Metric;
import math.Vector;

public class SOCollector {
	Camera camera;
	final int maxSpaceObjects = 128;
	public int cntSpaceObjects;
	public SpaceObject Objects[];
	public int curIndex, secIndex;
	public int idCounter = 0;
	
	public SOCollector(Camera camera) {
		this.camera = camera;
		Objects = new SpaceObject[maxSpaceObjects];
		cntSpaceObjects = 0;
	}

	public void startListing() {
		curIndex = 0;
	}
	public void secondListing() {
		secIndex = 0;
	}

	public boolean isPlaced(int i) {
		//check condition on binding camera to object
		if (camera.freeBind && Objects[i].isLive &&
			(Objects[i].model.isShip || Objects[i].model.isFighter
			|| Objects[i].model.isMissile))
			return true;
		if ( Objects[i].isLive
			   && (Objects[i].model.isShip || Objects[i].model.isFighter)
		 	   && Objects[i].PlayerPlaced
			   && (Objects[i].Side == camera.Side || camera.Side == 0))
			return true;
		return false;
	}

	public int getPrevIndex(int startIndex) {
		startIndex--;
		while (startIndex >= 0 && !isPlaced(startIndex)) startIndex--;
		if (startIndex >= 0) return startIndex;
		startIndex = cntSpaceObjects - 1;
		while (startIndex >= 0 && !isPlaced(startIndex)) startIndex--;
		if (startIndex >= 0) return startIndex;
		return -1;
	}

	public int getNextIndex(int startIndex) {
		startIndex++;
		while (startIndex < cntSpaceObjects && !isPlaced(startIndex)) startIndex++;
		if (startIndex < cntSpaceObjects) return startIndex;
		startIndex = 0;
		while (startIndex < cntSpaceObjects && !isPlaced(startIndex)) startIndex++;
		if (startIndex < cntSpaceObjects) return startIndex;
		return -1;
	}

	public int getNavTarget(int startIndex) {
		startIndex++;
		while (startIndex < cntSpaceObjects && (Objects[startIndex].isLive == false
				|| Objects[startIndex].model.isStatic == false)) startIndex++;
		if (startIndex < cntSpaceObjects) return startIndex;
		startIndex = 0;
		while (startIndex < cntSpaceObjects && (Objects[startIndex].isLive == false
				|| Objects[startIndex].model.isStatic == false)) startIndex++;
		if (startIndex < cntSpaceObjects) return startIndex;
		return -1;
	}

	public int getFoeTarget(int startIndex, int side) {
		startIndex++;
		while (startIndex < cntSpaceObjects && (Objects[startIndex].isLive == false
				|| Objects[startIndex].Side == side
				|| Objects[startIndex].Side == 0
				|| (Objects[startIndex].model.isFighter == false
				&& Objects[startIndex].model.isShip == false
				&& Objects[startIndex].model.isMissile == false
				&& Objects[startIndex].model.isStatic == false)
				|| startIndex == camera.iRelatedObject)) startIndex++;
		if (startIndex < cntSpaceObjects) return startIndex;
		startIndex = 0;
		while (startIndex < cntSpaceObjects && (Objects[startIndex].isLive == false
				|| Objects[startIndex].Side == side
				|| Objects[startIndex].Side == 0
				|| (Objects[startIndex].model.isFighter == false
				&& Objects[startIndex].model.isShip == false
				&& Objects[startIndex].model.isMissile == false
				&& Objects[startIndex].model.isStatic == false)
				|| startIndex == camera.iRelatedObject)) startIndex++;
		if (startIndex < cntSpaceObjects) return startIndex;
		return -1;
	}

	public int getFriendTarget(int startIndex, int side) {
		startIndex++;
		while (startIndex < cntSpaceObjects && (Objects[startIndex].isLive == false
				|| Objects[startIndex].Side != side
				|| (Objects[startIndex].model.isFighter == false
				&& Objects[startIndex].model.isShip == false
				&& Objects[startIndex].model.isMissile == false
				&& Objects[startIndex].model.isStatic == false)
				|| startIndex == camera.iRelatedObject)) startIndex++;
		if (startIndex < cntSpaceObjects) return startIndex;
		startIndex = 0;
		while (startIndex < cntSpaceObjects && (Objects[startIndex].isLive == false
				|| Objects[startIndex].Side != side
				|| (Objects[startIndex].model.isFighter == false
				&& Objects[startIndex].model.isShip == false
				&& Objects[startIndex].model.isMissile == false
				&& Objects[startIndex].model.isStatic == false)
				|| startIndex == camera.iRelatedObject)) startIndex++;
		if (startIndex < cntSpaceObjects) return startIndex;
		return -1;
	}



	public int getNextTarget(int startIndex) {
		startIndex++;
		while (startIndex < cntSpaceObjects && (Objects[startIndex].isLive == false
				|| (Objects[startIndex].model.isFighter == false
				&& Objects[startIndex].model.isShip == false
				&& Objects[startIndex].model.isMissile == false
				&& Objects[startIndex].model.isStatic == false)
				|| startIndex == camera.iRelatedObject)) startIndex++;
		if (startIndex < cntSpaceObjects) return startIndex;
		startIndex = 0;
		while (startIndex < cntSpaceObjects && (Objects[startIndex].isLive == false
				|| (Objects[startIndex].model.isFighter == false
				&& Objects[startIndex].model.isShip == false
				&& Objects[startIndex].model.isMissile == false
				&& Objects[startIndex].model.isStatic == false)
				|| startIndex == camera.iRelatedObject)) startIndex++;
		if (startIndex < cntSpaceObjects) return startIndex;
		return -1;
	}

	public int getPrevTarget(int startIndex) {
		startIndex--;
		while (startIndex >= 0 && (Objects[startIndex].isLive == false
				|| (Objects[startIndex].model.isFighter == false
				&& Objects[startIndex].model.isShip == false
				&& Objects[startIndex].model.isMissile == false
				&& Objects[startIndex].model.isStatic == false)
				|| startIndex == camera.iRelatedObject)) startIndex--;
		if (startIndex >= 0) return startIndex;
		startIndex = cntSpaceObjects - 1;
		while (startIndex >= 0 && (Objects[startIndex].isLive == false
				|| (Objects[startIndex].model.isFighter == false
				&& Objects[startIndex].model.isShip == false
				&& Objects[startIndex].model.isMissile == false
				&& Objects[startIndex].model.isStatic == false)
				|| startIndex == camera.iRelatedObject)) startIndex--;
		if (startIndex >= 0) return startIndex;
		return -1;
	}

	public int lastIndex = -1;
	public SpaceObject getNext() {
		while (curIndex < cntSpaceObjects && Objects[curIndex].isLive == false) curIndex++;
		if (curIndex < cntSpaceObjects) { 
			curIndex++;
			this.lastIndex = curIndex - 1;
			return Objects[curIndex - 1];
		}
		return null;
	}

	public SpaceObject getSecNext() {
		while (secIndex < cntSpaceObjects && Objects[secIndex].isLive == false) secIndex++;
		if (secIndex < cntSpaceObjects) { 
			secIndex++;
			return Objects[secIndex - 1];
		}
		return null;
	}


	public boolean add(SpaceObject obj) {

		if (cntSpaceObjects < maxSpaceObjects) {
			obj.model.createdObjects++;
			if (obj.strObjectName.equals(""))
				obj.strObjectName = obj.model.strModelName + " " + obj.model.createdObjects;
			obj.Index = cntSpaceObjects;
			obj.id = idCounter;
			idCounter++;
			Objects[cntSpaceObjects] = obj;
			cntSpaceObjects++;
			return true;
		}

		//find dead objects and try to insert new instead
		int i=0;
		while (i < cntSpaceObjects && Objects[i].isLive == true) i++;
		if (i < cntSpaceObjects) {
			//Ok. We've find it
			obj.model.createdObjects++;
			if (obj.strObjectName.equals(""))
				obj.strObjectName = obj.model.strModelName + " " + obj.model.createdObjects;
			obj.Index = i;
			obj.id = idCounter;
			idCounter++;
			Objects[i] = obj;
			return true;
		}
		return false;
	}

	public int getIndexByName(String strName) {
		int index = -1, i = 0;
		while (i < cntSpaceObjects) {
			if (Objects[i].isLive == true)
				if (strName.equals(Objects[i].strObjectName)) index = i;
			i++;
		}
		return index;
	}

	public double nearestEnemyDistance;
	public int getNearestAttacker(int idx) {
		if (idx < 0) return -1;
		int res = -1;
		double ndist;
		double nearestEnemyDistance = 1000000000;
		for (int i = 0; i<cntSpaceObjects; i++) {
			if (Objects[i].model.isFighter
				&& Objects[i].currentTarget == idx
				&& (Objects[i].targetType == Task.Attack 
					|| Objects[i].targetType == Task.SupportAttack)
				&& Objects[i].isLive) {
				ndist = Objects[i].getDistance(Objects[idx]);
				if (ndist < nearestEnemyDistance && ndist < Metric.attackingEnemyVicinity) {
					nearestEnemyDistance = ndist;
					res = i;
				}
			}
		}
		return res;
	}

	public int getNearestEnemy(int idx) {
		if (idx < 0) return -1;
		int res = -1;
		double ndist;
		nearestEnemyDistance = 1000000000;
		for (int i = 0; i<cntSpaceObjects; i++) {
			if ((Objects[i].model.isFighter || Objects[i].model.isShip)
				&& Objects[i].Side != Objects[idx].Side
				&& Objects[i].Side != 0
				&& Objects[i].isLive) {
				ndist = Objects[i].getDistance(Objects[idx]);
				if (ndist < nearestEnemyDistance && ndist < Metric.enemyVicinity) {
					nearestEnemyDistance = ndist;
					res = i;
				}
			}
		}
		return res;
	}

	public int getNearestEnemyShip(int idx) {
		if (idx < 0) return -1;
		int res = -1;
		double ndist;
		nearestEnemyDistance = 1000000000;
		for (int i = 0; i<cntSpaceObjects; i++) {
			if ((Objects[i].model.isFighter || Objects[i].model.isShip
				 || Objects[i].model.isMissile)
				&& Objects[i].Side != Objects[idx].Side
				&& Objects[i].Side != 0
				&& Objects[i].isLive) {
				ndist = Objects[i].getDistance(Objects[idx]);
				if (ndist < nearestEnemyDistance && ndist < Metric.enemyVicinity) {
					nearestEnemyDistance = ndist;
					res = i;
				}
			}
		}
		return res;
	}

	public int getNextInVicinity(int idx, int tidx, double mdist) {
		if (idx < 0) return -1;
		int res = -1;
		double ndist;
		tidx++;

		if (tidx >= 0 && tidx <cntSpaceObjects)
		for (int i = tidx; i<cntSpaceObjects; i++) {
			if (Objects[i].isLive && (Objects[i].model.isFighter
				  || Objects[i].model.isShip || Objects[i].model.isMissile)
				&& Objects[i].Side != Objects[idx].Side
				&& Objects[i].Side != 0) {
				  ndist = Objects[i].getDistance(Objects[idx]);
				  if (ndist < mdist) {
					  nearestEnemyDistance = ndist;
					  res = i;
					  return res;
				  }
			}
		}

		for (int i = 0; i<cntSpaceObjects; i++) {
			if (Objects[i].isLive &&  (Objects[i].model.isFighter
				  || Objects[i].model.isShip || Objects[i].model.isMissile)
				&& Objects[i].Side != Objects[idx].Side
				&& Objects[i].Side != 0) {
				  ndist = Objects[i].getDistance(Objects[idx]);
				  if (ndist < mdist) {
					  nearestEnemyDistance = ndist;
					  res = i;
					  return res;
				  }
			}
		}

		return (-1);
	}
	
	int preStore = 0;
	int smartStore = 0;
	public int getSmartNext(int idx) {
		int res = igetSmartNext(idx);
		preStore = smartStore;
		smartStore = res;
		return res;		
	}

	private int igetSmartNext(int idx) {
		int res = -1;
		int dx = idx + 1;
		if (dx < 0) dx = 0;
		if (dx >= this.cntSpaceObjects) dx = 0;		
		
		if (idx >= 0 && idx <cntSpaceObjects) {
		//rand shift
		int rdx = preStore + 1;
		if (rdx < 0) rdx = 0;
		if (rdx >= this.cntSpaceObjects) rdx = 0;		
		//find action enemy
		for (int i = rdx; i<cntSpaceObjects; i++) {
			if ((Objects[i].model.isFighter || Objects[i].model.isShip
				 || Objects[i].model.isMissile)
				&& Objects[i].Side != Objects[idx].Side
				&& Objects[i].actionPoints != 0
				&& Objects[i].isLive) {				
					return i;
			}
		}		
		for (int i = 0; i < rdx; i++) {
			if ((Objects[i].model.isFighter || Objects[i].model.isShip
				 || Objects[i].model.isMissile)
				&& Objects[i].Side != Objects[idx].Side
				&& Objects[i].actionPoints != 0
				&& Objects[i].isLive) {				
					return i;
			}
		}
				
		//find action ship
		for (int i = rdx; i<cntSpaceObjects; i++) {
			if ((Objects[i].model.isFighter || Objects[i].model.isShip
				 || Objects[i].model.isMissile)
				&& Objects[i].actionPoints != 0
				&& Objects[i].isLive) {				
					return i;
			}
		}		
		for (int i = 0; i < rdx; i++) {
			if ((Objects[i].model.isFighter || Objects[i].model.isShip
				 || Objects[i].model.isMissile)
				&& Objects[i].actionPoints != 0
				&& Objects[i].isLive) {				
					return i;
			}
		}
		
		//find enemy
		for (int i = rdx; i<cntSpaceObjects; i++) {			
			if ((Objects[i].model.isFighter || Objects[i].model.isShip
				 || Objects[i].model.isMissile)
				&& Objects[i].Side != Objects[idx].Side
				&& Objects[i].Side != 0
				&& Objects[i].isLive) {
					return i;				
			}
		}		
		for (int i = 0; i < rdx; i++) {
			if ((Objects[i].model.isFighter || Objects[i].model.isShip
				 || Objects[i].model.isMissile)
				&& Objects[i].Side != Objects[idx].Side
				&& Objects[i].Side != 0
				&& Objects[i].isLive) {				
					return i;
			}
		}		
		}


		//find any	
		for (int i = dx; i<cntSpaceObjects; i++) {
			if ((Objects[i].model.isFighter || Objects[i].model.isShip
				 || Objects[i].model.isMissile)
				&& Objects[i].Side != 0
				&& Objects[i].isLive) {	
				return i;
			}
		}
		for (int i = 0; i < dx; i++) {
			if ((Objects[i].model.isFighter || Objects[i].model.isShip
				 || Objects[i].model.isMissile)
				&& Objects[i].Side != 0
				&& Objects[i].isLive) {
					return i;
			}
		}
		
		return res;
	}

	public byte getObjectSector(SpaceObject B, int weapon, SpaceObject T) {
		try {
			//System.out.println(B.strObjectName + " class " + B.theModel.strModelName);
			//System.out.println(T.strObjectName + " class " + T.theModel.strModelName);
			Vector dir = new Vector(T.x - B.x, T.y - B.y, T.z - B.z);
			dir.mul(B.matRotate);
			dir.x -= B.model.rackCoord[weapon].x;
			dir.y -= B.model.rackCoord[weapon].y;
			dir.z -= B.model.rackCoord[weapon].z;	
			return dir.getSector();
		} catch (Exception ex) {
			return 0;
		}
	}

	public int getNextInSector(int idx, int tidx, double mdist, int weapon, byte Sector) {
		if (idx < 0) return -1;
		int res = -1;
		double ndist;
		tidx++;

		if (tidx >= 0)
		for (int i = tidx; i<cntSpaceObjects; i++) {
			if (Objects[i].isLive && (Objects[i].model.isFighter
				  || Objects[i].model.isShip || Objects[i].model.isMissile)
				&& Objects[i].Side != Objects[idx].Side
				&& Objects[i].Side != 0) {
				  ndist = Objects[i].getDistance(Objects[idx]);
				  if (ndist < mdist && ndist < Metric.shipLaserRange) 
				  if ((Sector & getObjectSector(Objects[idx], weapon, Objects[i])) != 0){
					  nearestEnemyDistance = ndist;
					  res = i;
					  return res;
				  }
			}
		}

		for (int i = 0; i<cntSpaceObjects; i++) {
			if (Objects[i].isLive &&  (Objects[i].model.isFighter
				  || Objects[i].model.isShip || Objects[i].model.isMissile)
				&& Objects[i].Side != Objects[idx].Side
				&& Objects[i].Side != 0) {
				  ndist = Objects[i].getDistance(Objects[idx]);
				  if (ndist < mdist && ndist < Metric.shipLaserRange)
				  if ((Sector & getObjectSector(Objects[idx], weapon, Objects[i])) != 0){
					 nearestEnemyDistance = ndist;
					 res = i;
					 return res;
				  }
			}
		}

		return (-1);
	}

	public int getFiOnTarget(SpaceObject C, Vector dir) {
		Vector out = C.getOutVector();
		double CosFi = dir.getCosFi(out);
		int Fi = (int)(CMath.M.acos(CosFi) / CMath.dFactor);
		return Fi;
	}

}