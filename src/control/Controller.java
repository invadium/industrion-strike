package control;

import java.awt.*;

import math.*;
import scene.Action;
import scene.Scene;
import scene.Camera;
import scene.ModelsCollector;
import scene.SpaceObject;
import media.Media;
import render.Render;
import render.Stars;

public class Controller {
	Scene scene;
	Camera camera;
	ModelsCollector Models;
	Media media;
	Stars stars;
	Render render;
	public KeyController Keys;
	
	int energyTicker = 0;

	public Controller(Scene scene, Camera camera, ModelsCollector Models,
			Media media, Stars stars, Render render) {
		this.scene = scene;
		this.camera = camera;
		this.Models = Models;
		this.media = media;
		this.stars = stars;
		this.render = render;
		this.Keys = new KeyController(render, stars, scene);
	}

	private boolean closer(int baseFi, int Fi) {
		if (baseFi*baseFi < Fi*Fi) return false;
		return true;
	}

	private int whatCloser(int Fi_0, int Fi_1, int Fi_2) {
		int d0 = Fi_0*Fi_0;
		int d1 = Fi_1*Fi_1;
		int d2 = Fi_2*Fi_2;
		if (d1<=d0 && d1<=d2) return 1;
		if (d2<=d0 && d2<=d1) return 2;
		return 0;
	}

	private int whatCloser2(int Fi_0, int Fi_1, int Fi_2) {
		Fi_0 -= 90*60; Fi_1 -= 90*60; Fi_2 -= 90*60;
		int d0 = Fi_0*Fi_0;
		int d1 = Fi_1*Fi_1;
		int d2 = Fi_2*Fi_2;

		if (d1<=d0 && d1<=d2) return 1;
		if (d2<=d0 && d2<=d1) return 2;
		return 0;
	}

	private void PerpendicularVector (SpaceObject C) {
			SpaceObject T = scene.Objects.Objects[C.currentTarget];
			if (T == null) return;

			//******************
			//dirrection control
			//******************
			//find vector
			Vector dir = new Vector(T.x - C.x, T.y - C.y, T.z - C.z);
			int baseFi = scene.Objects.getFiOnTarget(C, dir);				
			if (baseFi < (90*60-30) || baseFi > (90*60+30)) {
				{	C.starsRotation = false;
					int nYaw = C.getYawCorrection(0);
					C.changeYaw(-nYaw);
					int yawFi_0 = scene.Objects.getFiOnTarget(C, dir);
					C.undoYaw();

					nYaw = C.getYawCorrection(C.model.yawAcceleration);
					C.changeYaw(-nYaw);
					int yawFi_1 = scene.Objects.getFiOnTarget(C, dir);
					C.undoYaw();

					nYaw = C.getYawCorrection(-C.model.yawAcceleration);
					C.changeYaw(-nYaw);
					int yawFi_2 = scene.Objects.getFiOnTarget(C, dir);
					C.undoYaw();
					
					C.starsRotation = true;
					int trn = whatCloser2(yawFi_0, yawFi_1, yawFi_2); 
					if (trn == 1) C.addYaw();
					if (trn == 2) C.decYaw();
				}
				
				baseFi = scene.Objects.getFiOnTarget(C, dir);
				if (baseFi < (90*60-30) || baseFi > (90*60+30)) {
					C.starsRotation = false;
					int nPitch = C.getPitchCorrection(0);
					C.changePitch(nPitch);
					int pitchFi_0 = scene.Objects.getFiOnTarget(C, dir);
					C.undoPitch();

					nPitch = C.getPitchCorrection(C.model.pitchAcceleration);
					C.changePitch(nPitch);
					int pitchFi_1 = scene.Objects.getFiOnTarget(C, dir);
					C.undoPitch();

					nPitch = C.getPitchCorrection(-C.model.pitchAcceleration);
					C.changePitch(nPitch);
					int pitchFi_2 = scene.Objects.getFiOnTarget(C, dir);
					C.undoPitch();
					
					C.starsRotation = true;
					int trn = whatCloser2(pitchFi_0, pitchFi_1, pitchFi_2); 
					if (trn == 1) C.addPitch();
					if (trn == 2) C.decPitch();
				}
			}

			//*****************
			//  speed control
			//*****************
			if (C.adjustedSpeed != 0) {
				if (C.currentSpeed < C.adjustedSpeed) C.increaseSpeed();
				else if (C.currentSpeed > C.adjustedSpeed) C.decreaseSpeed();
			} else {
				if (C.currentSpeed < C.model.cruiseSpeed) C.increaseSpeed();
				else if (C.currentSpeed > C.model.cruiseSpeed) C.decreaseSpeed();
			}

			double dist = C.getDistance(scene.Objects.Objects[C.currentTarget]);
			if (dist > Metric.turnFollowRange) C.currentTarget = Action.None;
			if (C.agressionLevel == 1 || C.agressionLevel == 2) {
					C.resetTarget();
					if (C.isPrimary) C.currentTask.setPrimaryNotActual();
					else C.currentTask.setSecondaryNotActual();
			}
	}

	private void OppositeVector (SpaceObject C) {
			SpaceObject T = scene.Objects.Objects[C.currentTarget];
			if (T == null) return;

			//******************
			//dirrection control
			//******************
			//find vector
			Vector dir = new Vector(T.x - C.x, T.y - C.y, T.z - C.z);
			int baseFi = scene.Objects.getFiOnTarget(C, dir);				
			if (baseFi < (180*60+30) && baseFi > (180*60-30)) {
				if (baseFi < (180*60+30) && baseFi > (180*60-30)) {
					C.starsRotation = false;
					int nYaw = C.getYawCorrection(0);
					C.changeYaw(-nYaw);
					int yawFi_0 = scene.Objects.getFiOnTarget(C, dir);
					C.undoYaw();

					nYaw = C.getYawCorrection(C.model.yawAcceleration);
					C.changeYaw(-nYaw);
					int yawFi_1 = scene.Objects.getFiOnTarget(C, dir);
					C.undoYaw();

					nYaw = C.getYawCorrection(-C.model.yawAcceleration);
					C.changeYaw(-nYaw);
					int yawFi_2 = scene.Objects.getFiOnTarget(C, dir);
					C.undoYaw();
					
					C.starsRotation = true;
					int trn = whatCloser(yawFi_0, yawFi_1, yawFi_2); 
					if (trn == 1) C.addYaw();
					if (trn == 2) C.decYaw();
				}
				
				baseFi = scene.Objects.getFiOnTarget(C, dir);
				if (baseFi < (180*60+30) && baseFi > (180*60-30)) {
					C.starsRotation = false;
					int nPitch = C.getPitchCorrection(0);
					C.changePitch(nPitch);
					int pitchFi_0 = scene.Objects.getFiOnTarget(C, dir);
					C.undoPitch();

					nPitch = C.getPitchCorrection(C.model.pitchAcceleration);
					C.changePitch(nPitch);
					int pitchFi_1 = scene.Objects.getFiOnTarget(C, dir);
					C.undoPitch();

					nPitch = C.getPitchCorrection(-C.model.pitchAcceleration);
					C.changePitch(nPitch);
					int pitchFi_2 = scene.Objects.getFiOnTarget(C, dir);
					C.undoPitch();
					
					C.starsRotation = true;
					int trn = whatCloser(pitchFi_0, pitchFi_1, pitchFi_2); 
					if (trn == 1) C.addPitch();
					if (trn == 2) C.decPitch();
				}
			}

			//*****************
			//  speed control
			//*****************
			if (C.currentSpeed < C.model.maxSpeed) C.increaseSpeed();

			double dist = C.getDistance(scene.Objects.Objects[C.currentTarget]);
			if (dist > Metric.turnBackRange) C.currentTarget = Action.None;
			if (dist > Metric.missileTurnBack && C.model.isMissile) C.currentTarget = Action.None;
	}

	private void FollowVector (SpaceObject C) {			
			SpaceObject T = scene.Objects.Objects[C.currentTarget];
			if (T == null) return;

			double dist = C.getDistance(scene.Objects.Objects[C.currentTarget]);

			//******************
			//dirrection control
			//******************
			//find vector
			Vector dir = new Vector(T.x - C.x, T.y - C.y, T.z - C.z);
			if (dist < Metric.fighterLaserRange && C.model.cntWeapons > 0)
			if (C.targetType == Task.Attack || C.targetType == Task.SupportAttack) {
				//find future object coordinates
				int fireTime = (int)C.getDistance(T) / C.model.targetFactor << 1;
				T.futureMove(fireTime * T.currentSpeed / 2);
				dir = new Vector(T.fx - C.x, T.fy - C.y, T.fz - C.z);
			}
			int baseFi = scene.Objects.getFiOnTarget(C, dir);				
			if (baseFi < 21570 && baseFi > 30) {
				if (baseFi < 21570 && baseFi > 30) {
					C.starsRotation = false;
					int nYaw = C.getYawCorrection(0);
					C.changeYaw(-nYaw);
					int yawFi_0 = scene.Objects.getFiOnTarget(C, dir);
					C.undoYaw();

					nYaw = C.getYawCorrection(C.model.yawAcceleration);
					C.changeYaw(-nYaw);
					int yawFi_1 = scene.Objects.getFiOnTarget(C, dir);
					C.undoYaw();

					nYaw = C.getYawCorrection(-C.model.yawAcceleration);
					C.changeYaw(-nYaw);
					int yawFi_2 = scene.Objects.getFiOnTarget(C, dir);
					C.undoYaw();
					
					C.starsRotation = true;
					int trn = whatCloser(yawFi_0, yawFi_1, yawFi_2); 
					if (trn == 1) C.addYaw();
					if (trn == 2) C.decYaw();
				}
				
				baseFi = scene.Objects.getFiOnTarget(C, dir);
				if (baseFi < 21570 && baseFi > 30) {
					C.starsRotation = false;
					int nPitch = C.getPitchCorrection(0);
					C.changePitch(nPitch);
					int pitchFi_0 = scene.Objects.getFiOnTarget(C, dir);
					C.undoPitch();

					nPitch = C.getPitchCorrection(C.model.pitchAcceleration);
					C.changePitch(nPitch);
					int pitchFi_1 = scene.Objects.getFiOnTarget(C, dir);
					C.undoPitch();

					nPitch = C.getPitchCorrection(-C.model.pitchAcceleration);
					C.changePitch(nPitch);
					int pitchFi_2 = scene.Objects.getFiOnTarget(C, dir);
					C.undoPitch();
					
					C.starsRotation = true;
					int trn = whatCloser(pitchFi_0, pitchFi_1, pitchFi_2); 
					if (trn == 1) C.addPitch();
					if (trn == 2) C.decPitch();
				}
			}

			//****************
			//  fire control
			//****************
			if (C.targetType == Task.Attack && C.model.isFighter && C.model.cntWeapons > 0)
			if (baseFi > 356*60 || baseFi < 4*60) {
				//here we are!!! target in our sight... shoot it!!!
				if (C.model.isMissile) C.actionPoints += 10;
				if (dist < Metric.fighterLaserRange && (C.agressionLevel == 1 || C.agressionLevel == 3 
					|| C.agressionLevel == 4 || (C.agressionLevel == 2 && C.missilesLoad[C.currentLauncher] == 0))) {					
					C.tryToFire();
				}
				if (C.model.cntLaunchers > 0)
				if (dist < C.model.missilesRack[C.currentLauncher].range
				   && (C.agressionLevel == 2 || C.agressionLevel == 3 || C.agressionLevel == 4)) {
					if (C.missilesLoad[C.currentLauncher] == 0) C.switchLauncher();
					if (C.missilesLoad[C.currentLauncher] > 0
					   && C.missileRecharge[C.currentLauncher] == 0) {
						C.setTaskScreen(C.currentTarget);
						C.tryToLaunch(false);
					}
				}
			}
			if (C.targetType == Task.SupportAttack && C.model.isFighter)
			if (baseFi > 356*60 || baseFi < 4*60) {
				//here we are!!! target in our sight... shoot it!!!
				if (C.model.cntLaunchers > 0) {
				  if (C.missilesLoad[C.currentLauncher] == 0) C.switchLauncher();
				  if (dist < C.model.missilesRack[C.currentLauncher].range
				        && C.missilesLoad[C.currentLauncher] > 0
					&& C.missileRecharge[C.currentLauncher] == 0) {
						C.setTaskScreen(C.currentTarget);
						C.tryToLaunch(false);
				  }
				}
				if (dist < Metric.fighterLaserRange) {
					C.tryToFire();
				}
			}

			//*****************
			//  speed control
			//*****************
			if (C.model.isShip || C.model.isFighter) {
				if (C.adjustedSpeed != 0) {
					if (C.currentSpeed > C.adjustedSpeed) C.decreaseSpeed();
					else if (C.currentSpeed < C.adjustedSpeed) C.increaseSpeed();
				} else {
					if (dist > Metric.cruiseRange && C.currentSpeed < C.model.maxSpeed) C.increaseSpeed();
					else if (dist < Metric.cruiseRange && C.currentSpeed > C.model.cruiseSpeed) C.decreaseSpeed();
					else if (dist < Metric.cruiseRange && C.currentSpeed < C.model.cruiseSpeed) C.increaseSpeed();
				}
			} else if (C.model.isMissile) {
				if (baseFi > 356*60 || baseFi < 4*60) {
					if (C.currentSpeed < C.model.maxSpeed) C.increaseSpeed();
				} if (baseFi > 280*60 || baseFi < 80*60) {
					if (C.currentSpeed > C.model.cruiseSpeed) C.decreaseSpeed();
					if (C.currentSpeed < C.model.cruiseSpeed) C.increaseSpeed();
				} else {
					if (C.currentSpeed < C.model.maxSpeed) C.increaseSpeed();
				}
				if (baseFi > 60*60 && baseFi < 300*60 && dist < Metric.oppositeRange)
					C.typeManoeuvre = Action.OppositeTarget;
			}
            
			//*****************
			//  task control
			//*****************
			if (dist <  (C.model.reachRange + C.model.radius
				 + scene.Objects.Objects[C.currentTarget].model.radius)
				  && C.targetType == Task.Defend && C.model.isFighter) {
				if (scene.Objects.Objects[C.currentTarget].model.isFighter) {
					//... code for a wingman
				} else {
					if (C.agressionLevel == 1 || C.agressionLevel == 2) {
						C.reachTarget();
						C.resetTarget();
						if (C.isPrimary) C.currentTask.setPrimaryNotActual();
						else C.currentTask.setSecondaryNotActual();
					}
					else {
						C.reachTarget();
						int next = C.currentTask.secondExists(C.isPrimary);
						if (next == 0) C.typeManoeuvre = Action.RoundTarget;
						else if (next == 1) C.setTarget(true);
						else if (next == 2) C.setTarget(false);
					}
				}
			}

			if (C.currentTarget != -1)
			if (dist < (C.model.reachRange + C.model.radius
				 + scene.Objects.Objects[C.currentTarget].model.radius)
				 && C.model.isShip) {
				C.reachTarget();
				C.resetTarget();
				if (C.isPrimary) C.currentTask.setPrimaryNotActual();
				else C.currentTask.setSecondaryNotActual();
			}
	}

	private void FindNewJob(SpaceObject C) {
		if (C.currentTask.iType != Task.None) {
			if (C.currentTask.iTarget > 0 && C.currentTask.isActual) {
				C.currentTarget = C.currentTask.iTarget;
				C.setTaskScreen(C.currentTarget);
				C.Tid = C.currentTask.id;
				C.targetType = C.currentTask.iType;
				C.isPrimary = true;
				double dist = C.getDistance(scene.Objects.Objects[C.currentTarget]);
				if (dist < Metric.oppositeRange && C.currentTask.iType == Task.Attack
					&& scene.Objects.Objects[C.currentTarget].currentSpeed < 35) C.typeManoeuvre = Action.OppositeTarget;
				else if (dist < (C.model.reachRange + C.model.radius
							+ scene.Objects.Objects[C.currentTarget].model.radius)
						 && C.model.isShip == true) C.resetTarget();
				else C.typeManoeuvre = Action.Follow;
			} else if (C.currentTask.iSecondaryTarget > 0 && C.currentTask.isSecondaryActual) {
				C.currentTarget = C.currentTask.iSecondaryTarget;
				C.setTaskScreen(C.currentTarget);
				C.Tid = C.currentTask.sid;
				C.targetType = C.currentTask.iType;
				C.isPrimary = false;
				double dist = C.getDistance(scene.Objects.Objects[C.currentTarget]);
				if (dist < Metric.oppositeRange && C.currentTask.iType == Task.Attack
					&& scene.Objects.Objects[C.currentTarget].currentSpeed < 35) C.typeManoeuvre = Action.OppositeTarget;
				else if (dist < (C.model.reachRange + C.model.radius
							  + scene.Objects.Objects[C.currentTarget].model.radius)
						 && C.model.isShip == true) C.resetTarget();
				else C.typeManoeuvre = Action.Follow;
			}
		}
	}

	private void followWingman(SpaceObject C, SpaceObject W) {
		int newTarget = -1;
		if (C.model.cntWeapons == 0) return;

		if (C.agressionLevel == 1) {
			if ((W.targetType == Task.Attack || W.targetType == Task.SupportAttack
				  && W.currentTarget != -1))
						newTarget = W.currentTarget;
			if (newTarget == -1) newTarget = scene.Objects.getNearestAttacker(W.Index);
			if (newTarget == -1) newTarget = scene.Objects.getNearestAttacker(C.Index);
		}
		if (C.agressionLevel == 2) {
			if (newTarget == -1) newTarget = scene.Objects.getNearestAttacker(W.Index);
			if (newTarget == -1) newTarget = scene.Objects.getNearestAttacker(C.Index);
			if (newTarget == -1)
				if ((W.targetType == Task.Attack || W.targetType == Task.SupportAttack
				  && W.currentTarget != -1))
						newTarget = W.currentTarget;
		}
		if (C.agressionLevel == 3) {
			newTarget = scene.Objects.getNearestAttacker(W.Index);
			if (newTarget == -1) newTarget = scene.Objects.getNearestAttacker(C.Index);
		}
		if (C.agressionLevel == 4) {
			C.takeTarget(W);
		}

		if (newTarget >= 0) {
			C.currentTarget = newTarget;
			C.setTaskScreen(newTarget);
			C.Tid = scene.Objects.Objects[newTarget].id;
			C.targetType = Task.SupportAttack;
			C.typeManoeuvre = 0;
			C.secManoeuvre = 600;
		}
	}

	private void FindNewTarget(SpaceObject C) {
		//usualy works for following fighters
		if (C.model.cntWeapons == 0) return;
		int newTarget = -1;
		if (C.agressionLevel == 1) newTarget = scene.Objects.getNearestAttacker(C.Index);
		if (C.agressionLevel == 2) newTarget = scene.Objects.getNearestEnemy(C.Index);
		if (C.agressionLevel == 3 || C.agressionLevel == 4) {
				if (C.agressionLevel == 3) {
					newTarget = scene.Objects.getNearestAttacker(C.currentTarget);
					if (newTarget == -1) scene.Objects.getNearestAttacker(C.Index);
				}
				if (C.agressionLevel == 4) newTarget = scene.Objects.getNearestEnemy(C.Index);
		}
		if (newTarget != -1) {
			C.currentTarget = newTarget;
			C.setTaskScreen(newTarget);
			C.Tid = scene.Objects.Objects[newTarget].id;
			C.targetType = Task.SupportAttack;
			C.typeManoeuvre = 0;
			C.secManoeuvre = 600;
		}
	}

	private int FindNearestTarget(SpaceObject C) {
		if (C.model.cntWeapons == 0) return -1;
		if (C.flightPlan != -1) return -1;

		int newTarget = -1;
		newTarget = scene.Objects.getNearestAttacker(C.Index);
		if (newTarget == -1)
			newTarget = scene.Objects.getNearestEnemy(C.Index);

		if (newTarget != -1) {
			C.currentTarget = newTarget;
			C.setTaskScreen(newTarget);
			C.Tid = scene.Objects.Objects[newTarget].id;
			C.targetType = Task.SupportAttack;
			C.typeManoeuvre = 0;
			C.secManoeuvre = 600;
		}

		return newTarget;
	}

	private void checkActuality(SpaceObject C) {
		if (C.currentTarget > 0 && C.targetType == Task.SupportAttack) {
			C.secManoeuvre--;
			if (C.secManoeuvre < 0) {
				C.currentTarget = -1;
				C.Tid = -1;
			}
		}
		if (C.currentTask.iTarget != -1)
		if (scene.Objects.Objects[C.currentTask.iTarget].isLive == false ||
			scene.Objects.Objects[C.currentTask.iTarget].id != C.currentTask.id) {
			//primary is not actual
			C.currentTask.isActual = false;
			C.currentTask.iTarget = -1;
			if (C.isPrimary) C.currentTarget = -1;
		}
		if (C.currentTask.iSecondaryTarget != -1)
		if (scene.Objects.Objects[C.currentTask.iSecondaryTarget].isLive == false ||
			scene.Objects.Objects[C.currentTask.iSecondaryTarget].id != C.currentTask.sid) {
			C.currentTask.isSecondaryActual = false;
			C.currentTask.iSecondaryTarget = -1;
			if (!C.isPrimary) C.currentTarget = -1;
		}
		if (C.currentTarget >= 0)
		if (scene.Objects.Objects[C.currentTarget].isLive == false ||
			scene.Objects.Objects[C.currentTarget].id != C.Tid) {
			C.currentTarget = -1;
			C.Tid = -1;
		}
	}

	private void checkOnFire(SpaceObject C) {
		if (!(C.targetType == Task.Attack && C.agressionLevel == 4)
			&& C.model.isFighter && C.model.maxSpeed != 0)
		if ((C.Shield < C.model.Shield/2 && C.hits > 0) || (C.hits > 2)) C.avoidFire();
	}

	private void FollowFlightPlan(SpaceObject C) {
		//take a new order from the flight plan		
		int next = scene.plan.getNext(C);
		if (next != -1) scene.plan.set(C, next);
	}

	private void AIControl(SpaceObject C) {
		//star fighter
		if (C.currentTarget == Action.AvoidCollision) {
			if (C.priManoeuvre > 0) {
				if (C.currentSpeed < C.model.cruiseSpeed) C.increaseSpeed();
				C.priManoeuvre--;
				switch(C.typeAManoeuvre) {
					case 0: C.addPitch(); break;
					case 1: C.decPitch(); break;
					case 2: C.addYaw(); break;
					case 3: C.decYaw(); break;
					case 4: C.addPitch(); C.addYaw(); break;
					case 5: C.addPitch(); C.decYaw(); break;
					case 6: C.decPitch(); C.addYaw(); break;
					case 7: C.decPitch(); C.decYaw(); break;
				}
			}
			else if (C.secManoeuvre > 0) {
				if (C.currentSpeed < C.model.cruiseSpeed) C.increaseSpeed();
				C.secManoeuvre--;
			}
			else {
				C.currentTarget = Action.None;
				C.typeAManoeuvre = Action.None;
			}
		} else if (C.currentTarget == Action.AvoidFire) {
			if (C.priManoeuvre > 0) {
				if (C.currentSpeed < C.model.maxSpeed) C.increaseSpeed();
				C.priManoeuvre--;
				C.hits = 0;
				switch(C.typeAManoeuvre) {
					case 0: C.addPitch(); break;
					case 1: C.decPitch(); break;
					case 2: C.addYaw(); break;
					case 3: C.decYaw(); break;
					case 4: C.addPitch(); C.addYaw(); break;
					case 5: C.addPitch(); C.decYaw(); break;
					case 6: C.decPitch(); C.addYaw(); break;
					case 7: C.decPitch(); C.decYaw(); break;
				}
			}
			else if (C.secManoeuvre > 0) {
				if (C.currentSpeed < C.model.cruiseSpeed) C.increaseSpeed();
				C.secManoeuvre--;
				if (C.secManoeuvre == 0 && C.hits > 0) C.avoidFire();				
				switch(C.typeBManoeuvre) {
					case 0: C.addPitch(); break;
					case 1: C.decPitch(); break;
					case 2: C.addYaw(); break;
					case 3: C.decYaw(); break;
					case 4: C.addPitch(); C.addYaw(); break;
					case 5: C.addPitch(); C.decYaw(); break;
					case 6: C.decPitch(); C.addYaw(); break;
					case 7: C.decPitch(); C.decYaw(); break;
				}
			}
			else {
				C.currentTarget = Action.None;
				C.typeAManoeuvre = Action.None;
				C.typeBManoeuvre = Action.None;
			}
		} else if (C.currentTarget >= 0) {
			if (C.typeManoeuvre == Action.OppositeTarget) OppositeVector(C);
			else if (C.typeManoeuvre == Action.RoundTarget && C.model.isFighter) PerpendicularVector(C);
			else FollowVector(C);
			checkActuality(C);
			if (C.priManoeuvre > 0) C.priManoeuvre--;
			else {
				if (C.currentTarget >= 0)
				if (C.model.isFighter && C.targetType == Task.Defend) {
					if (!scene.Objects.Objects[C.currentTarget].model.isFighter)
							FindNewTarget(C);
					else {
						if (scene.Objects.Objects[C.currentTarget].model.isFighter)
							followWingman(C, scene.Objects.Objects[C.currentTarget]);
					}
				}
				C.priManoeuvre = 10;
			}
			checkOnFire(C);
		} else if (C.currentTarget == Action.None) {
			FindNewJob(C);
			if (C.currentTarget == Action.None) {
				if (C.model.isFighter) {
					//we find neares enemy and attack it
					if (FindNearestTarget(C) == -1) {
						//there is no job for us - the ship stops and waits for orders
						if (C.currentSpeed > 0) C.decreaseSpeed();
						if (C.currentSpeed < 0) C.increaseSpeed();
						if (C.flightPlan != -1) {
							FollowFlightPlan(C);
						}
					}
				} else {
						if (C.currentSpeed > 0) C.decreaseSpeed();
						if (C.currentSpeed < 0) C.increaseSpeed();
						if (C.flightPlan != -1) {
							FollowFlightPlan(C);
						}
				}
				
			}
			checkOnFire(C);
		}
		//capital ship
		if (C.model.isShip && C.model.cntLaunchers > 0)
		if ((C.agressionLevel == 3 || C.agressionLevel == 4)
			&& C.missilesLoad[C.currentLauncher] > 0 && C.missileRecharge[C.currentLauncher] == 0) {
			int trg = scene.Objects.getNextInVicinity(C.Index,
				C.taskScreen, C.model.missilesRack[C.currentLauncher].range);
			if (trg>=0) {
				C.setTaskScreen(trg);
				C.tryToLaunch(false);
			}
		}
		if (C.model.isShip && (C.agressionLevel == 2 || C.agressionLevel == 4)) {
			C.tryToCapFire();
		}
	}

	private void existsControl(SpaceObject obj) {
		if (obj.lifeTime != -1) {
			obj.lifeTime--;
			if (obj.lifeTime <= 1) {
				obj.kill(-1);
				if (camera.iRelatedObject == obj.Index && obj.Source != -1)
					camera.iRelatedObject = obj.Source;
			}
		}
	}
	
	private boolean isCollise(SpaceObject obj, SpaceObject cobj) {
		double R = obj.model.radius + cobj.model.radius; R *= R;
		double x = obj.x - cobj.x; x *= x;
		double y = obj.y - cobj.y; y *= y;
		double z = obj.z - cobj.z; z *= z;
		if (x<R && y<R && z<R) return true;
		return false;
	}

	private boolean isFutureCollise(SpaceObject obj, SpaceObject cobj) {
		double R = obj.model.radius + cobj.model.radius; 
		R = R + R/2; R *= R;
		double x = obj.fx - cobj.fx; x *= x;
		double y = obj.fy - cobj.fy; y *= y;
		double z = obj.fz - cobj.fz; z *= z;
		if (x<R && y<R && z<R) return true;
		return false;
	}

	private void consolationMessage(SpaceObject obj, SpaceObject byObj) {
	if (obj.model.isFighter || obj.model.isShip) {
		if (byObj.model.isFighter || byObj.model.isShip || byObj.model.isStatic) {
			scene.messageScreen.push(obj.model.strIDName + ": " + obj.strObjectName
			    + " has been destroyed", Color.yellow);
			scene.messageScreen.push("in collision with "
			    + byObj.model.strIDName + ": " + byObj.strObjectName, Color.yellow);
		} else if ((byObj.model.isWeapon || byObj.model.isMissile) && byObj.Source > 0) {
			scene.messageScreen.push(obj.model.strIDName + ": " + obj.strObjectName
			    //+ " has been destroyed by "
				+ " destroyed by "
			    + scene.Objects.Objects[byObj.Source].model.strIDName + ": "
			    + scene.Objects.Objects[byObj.Source].strObjectName, Color.yellow);
		}
	}}

	private void collisionControl(SpaceObject obj) {
		SpaceObject cobj;
		scene.Objects.secondListing();
		while((cobj = scene.Objects.getSecNext()) != null) {
		if (cobj.Index != obj.Index 
			&& cobj.Source != obj.Index && obj.Source != cobj.Index
			&& (cobj.Source != obj.Source || cobj.Source == -1)
			&& cobj.model.radius != 0 && obj.model.radius != 0
			&& !(cobj.model.isWeapon && obj.model.isWeapon)) {
			//check for collision
			if (!(cobj.model.isFighter && obj.model.isFighter) && isCollise(obj, cobj)) {
				//System.out.println("Collision between " + obj.strObjectName 
				//					+ " and " + cobj.strObjectName);

				//collision occured;
				int Eobj = obj.Shield + obj.Hull;
				int Ecobj = cobj.Shield + cobj.Hull;
				if (obj.model.isMissile) Eobj = obj.model.Hull * Metric.missileExplossion;
				if (cobj.model.isMissile) Eobj = cobj.model.Hull * Metric.missileExplossion;

				if (obj.model.isWeapon && !cobj.model.isWeapon) cobj.hits++;
				if (cobj.model.isWeapon && !obj.model.isWeapon) obj.hits++;

				if (Eobj == Ecobj) {
					obj.kill(cobj.Side);
					cobj.kill(obj.Side);
					//make sound
					if (obj.model.isFighter || cobj.model.isFighter) media.auBlow.play();
					if (obj.model.isShip || cobj.model.isShip) media.auExplosion.play();

					//push message to the log
					consolationMessage(obj, cobj);
					consolationMessage(cobj, obj);
					//check cases, than camera binded to blowning object
					if (camera.iRelatedObject == obj.Index)
						if(obj.Source == -1) camera.leaveObject();
						else camera.iRelatedObject = obj.Source;
					if (camera.iRelatedObject == cobj.Index)
						if (cobj.Source == -1) camera.leaveObject();
						else camera.iRelatedObject = cobj.Source;
				} else if (Eobj > Ecobj) {
					cobj.kill(obj.Side);
					if (obj.model.isWeapon || obj.model.isMissile) {
						obj.isLive = false;
						if (camera.iRelatedObject == obj.Index && obj.Source != -1)
							camera.iRelatedObject = obj.Source;
					} 
					//push the message to the log
					consolationMessage(cobj, obj);
					//check camera binding
					if (camera.iRelatedObject == cobj.Index)
						if (cobj.Source == -1) camera.leaveObject();
						else camera.iRelatedObject = cobj.Source;
					//make sound
					if (cobj.model.isShip) media.auExplosion.play();
					if (cobj.model.isFighter) media.auBlow.play();
					if (obj.model.isMissile) media.auHit.play();
					obj.Shield -= Ecobj;
					if (obj.Shield < 0) {
						obj.Hull += obj.Shield;
						obj.Shield = 0;
					}
				} else {
					obj.kill(cobj.Side);
					if (cobj.model.isWeapon || cobj.model.isMissile) {
						cobj.isLive = false;
						if (camera.iRelatedObject == cobj.Index && cobj.Source != -1)
							camera.iRelatedObject = cobj.Source;
					}
					//push the message to the log
					consolationMessage(obj, cobj);
					//check camera binding
					if (camera.iRelatedObject == obj.Index)
						if (obj.Source == -1) camera.leaveObject();
						else camera.iRelatedObject = obj.Source;
					//make sound
					if (obj.model.isShip) media.auExplosion.play();
					if (obj.model.isFighter) media.auBlow.play();
					if (obj.model.isMissile) media.auHit.play();

					cobj.Shield -= Eobj;
					if (cobj.Shield < 0) {
						cobj.Hull += cobj.Shield;
						cobj.Shield = 0;
					}
				}
			} else {
				//check for future collisions
				if (obj.model.maxSpeed != 0)
				if ((obj.model.isFighter)
				 && (cobj.model.isShip || cobj.model.isMissile
					 || cobj.model.isStatic || cobj.model.isFighter)) {
					for (int i = 1; i < 20; i++) {
						obj.futureMove((double)(i * obj.currentSpeed));
						cobj.futureMove((double)(i * cobj.currentSpeed));
						if (isFutureCollise(obj, cobj)) obj.currentTarget = Action.preAvoidCollision;
					}
					if (obj.currentTarget == Action.preAvoidCollision) {
						obj.currentTarget = Action.AvoidCollision;
						if (cobj.model.isFighter) {
							//light manouvre
							if (obj.typeAManoeuvre == Action.None)
								obj.typeAManoeuvre = (int)(CMath.R.nextFloat()*2);
							if (obj.currentTask.iType == Task.Defend) obj.priManoeuvre = 20;
							else obj.priManoeuvre = (int)(CMath.R.nextFloat()*2) * 20 + 40;
							obj.secManoeuvre = 20;
						} else {
							//heavy manouvre
							if (obj.typeAManoeuvre == Action.None)
								obj.typeAManoeuvre = (int)(CMath.R.nextFloat()*8);
							if (obj.currentTask.iType == Task.Defend) obj.priManoeuvre = 20;
							else obj.priManoeuvre = (int)(CMath.R.nextFloat()*4) * 20 + 40;
							if (cobj.model.isShip) obj.secManoeuvre = 200;
							else obj.secManoeuvre = 100;
						}
					}
				}
				else if ((obj.model.isShip)
				 && (cobj.model.isShip || cobj.model.isStatic)) {
					for (int i = 1; i < 50; i++) {
						obj.futureMove((double)(i * obj.currentSpeed));
						cobj.futureMove((double)(i * cobj.currentSpeed));
						if (isFutureCollise(obj, cobj)) obj.currentTarget = Action.preAvoidCollision;
					}
					if (obj.currentTarget == Action.preAvoidCollision) {
						obj.currentTarget = Action.AvoidCollision;
						if (obj.typeAManoeuvre == Action.None)
							obj.typeAManoeuvre = (int)(CMath.R.nextFloat()*8);
						obj.priManoeuvre = (int)(CMath.R.nextFloat()*4) * 10 + 60;
						obj.secManoeuvre = 60;
					}
				}
			}
		} }
	}

	private void controlers() {
		if (camera.iRelatedObject == -1) camera.moveForward();
		SpaceObject obj;
		scene.Objects.startListing();
		while((obj = scene.Objects.getNext()) != null){
			if (obj.AIControlled == true && obj.HumanControlled == false)
					AIControl(obj);
			obj.moveControl();
			if (energyTicker == 0) obj.energyControl();
			existsControl(obj);
			collisionControl(obj);
		};
		if (energyTicker == 0) energyTicker = 5;
			else energyTicker--;
	}

	private void pilotControl() {
		if (camera.iRelatedObject != -1 &&
		scene.Objects.Objects[camera.iRelatedObject].HumanControlled == true) {
			//manual ship control
			Keys.applyKeys(scene.Objects.Objects[camera.iRelatedObject]);
		
 			if (scene.Objects.Objects[camera.iRelatedObject].model.isShip
			    && (scene.Objects.Objects[camera.iRelatedObject].agressionLevel == 2
					|| scene.Objects.Objects[camera.iRelatedObject].agressionLevel == 4)) {
				scene.Objects.Objects[camera.iRelatedObject].tryToCapFire();
			}
			if (scene.Objects.Objects[camera.iRelatedObject].currentTarget == Action.AvoidCollision)
				scene.Objects.Objects[camera.iRelatedObject].currentTarget = -1;
			if (scene.Objects.Objects[camera.iRelatedObject].currentTarget == Action.AvoidFire)
				scene.Objects.Objects[camera.iRelatedObject].currentTarget = -1;
		} else if (camera.iRelatedObject == -1) {
			//free camera control
			Keys.applyKeys(camera);
		}
	}

	private void missionControl() {
		int index = scene.Interpreter.commands.findProc(scene.minTime, scene.secTime);
		if (index != -1) scene.Interpreter.runProc(index);

		index = -1;
		do {
			index = scene.Interpreter.commands.findPeriod(scene.Time / 20, index);
			if (index != -1) scene.Interpreter.runProc(index);
		} while (index != -1);
	}
	
	public void smartCameraControl() {
		if (camera.smartTime > 0) camera.smartTime--;
		else {
			camera.smartTime = 80 + (int)(CMath.R.nextFloat()*4) * 20;
			int i = scene.Objects.getSmartNext(camera.iRelatedObject);
			if (i >= 0) {
				camera.iRelatedObject = i;
				camera.iRelationType = (int)(CMath.R.nextFloat()*12);
				if (camera.iRelationType > 8) camera.iRelationType = 2;
				//scene.messageScreen.push("action is " + scene.Objects.Objects[i].actionPoints);
			}
		}
	}

	public void takeControl() {
		if (scene.secTicker == 0) missionControl();
		pilotControl();
		controlers();
		if (camera.smartBind) smartCameraControl();
	}

	public void takeStatis() {
		if (camera.iRelatedObject == -1) {
			pilotControl();
			camera.moveForward();
		}
	}
}