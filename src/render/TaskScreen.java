package render;

import java.awt.*;
import java.applet.*;
import media.Media;
import scene.Scene;
import scene.Camera;
import scene.SpaceObject;
import scene.Action;
import control.Task;

public class TaskScreen {
	Applet applet;
	Graphics canvas;
	Scene scene;
	Camera camera;
	SpaceObject spaceObject;
	Media media;
	Font screenFont = new Font("Courier", Font.PLAIN, 9);
	Font screenFontL = new Font("Courier", Font.PLAIN, 12);
	int x = 530;
	int y = 370;
	int w = 200;
	int h = 140;

	int iType = -13, iPrimary = -13, iSecondary = -13, iAgression = -13;
	int iTarget = -13, iSpeed = -13, iShield = -13, iHull = -13;
	int tType = -13, tTarget = -13;
	int iPlan = -1;
	int iDistance = -1;
	int iTaskScreen = -1;
	int iCam = -1;

	public TaskScreen(Applet applet, Graphics canvas, Scene scene,
						Camera camera, Media media) {
		this.applet = applet;
		this.canvas = canvas;
		this.scene = scene;
		this.camera = camera;
		this.media = media;
	}

	private void clearAll() {           
		canvas.setColor(Color.black);
		canvas.fillRect(x, y, w, h);		
	}

	public void expaired() {
	 	iType = iPrimary = iSecondary = iAgression
	 		= iTarget = iSpeed = iShield = iHull =
			  tType = tTarget = -13;
		iPlan = -13;
		iDistance = -1;	
		iCam = -1;
		iTaskScreen = -1;
	}


	private void drawTask() {
 		canvas.setFont(screenFontL);
		canvas.setColor(Color.green);

		if (camera.iRelatedObject != -1) {
			iCam = -1;
			spaceObject = scene.Objects.Objects[camera.iRelatedObject];

			if (iType != spaceObject.currentTask.iType  || iAgression != spaceObject.agressionLevel
				|| iPrimary != scene.Objects.Objects[camera.iRelatedObject].currentTask.iTarget
				|| iSecondary != scene.Objects.Objects[camera.iRelatedObject].currentTask.iSecondaryTarget) {

				canvas.setColor(Color.black);
				canvas.fillRect(x, y, w, 40);
				canvas.setColor(Color.green);

				//Task Type
				switch(spaceObject.currentTask.iType) {
					case Task.Defend: canvas.drawString("Follow:", x, y + 10);
								  break;
					case Task.Attack: canvas.drawString("Attack:", x, y + 10);
								  break;
					default: canvas.drawString("None:", x, y + 10);
				}

				//Primary Task
				if (scene.Objects.Objects[camera.iRelatedObject].currentTask.iTarget == -1) {
					canvas.drawString("---- empty ----", x + 45, y + 10);
				} else {
					canvas.drawString(
						scene.Objects.Objects[spaceObject.currentTask.iTarget].model.strIDName + ": " +
						scene.Objects.Objects[spaceObject.currentTask.iTarget].strObjectName,
						x + 45, y + 10);
				}

				//Secondary Task
				if (scene.Objects.Objects[camera.iRelatedObject].currentTask.iSecondaryTarget == -1) {
					canvas.drawString("---- empty ----", x + 45, y + 22);
				} else {
					canvas.drawString(
						scene.Objects.Objects[spaceObject.currentTask.iSecondaryTarget].model.strIDName + ": " +
						scene.Objects.Objects[spaceObject.currentTask.iSecondaryTarget].strObjectName,
						x + 45, y + 22);
				}

				//Agressive level
				canvas.setColor(Color.cyan);
				canvas.drawString("AL:", x, y+34);
				for (int i=1; i<5; i++) canvas.drawString(""+i, x+i*10+10, y+34);
				canvas.setColor(Color.red);
				canvas.drawString("" + spaceObject.agressionLevel, x+spaceObject.agressionLevel*10+10, y+34);

				iType = spaceObject.currentTask.iType;
				iPrimary = scene.Objects.Objects[camera.iRelatedObject].currentTask.iTarget;
				iSecondary = scene.Objects.Objects[camera.iRelatedObject].currentTask.iSecondaryTarget;
				iAgression = spaceObject.agressionLevel;
			}

			/****************************************
			//only for debbuging purposes
			//show current target name
			String strType = "none of all";
			canvas.setColor(Color.black);
			canvas.fillRect(x + 70, y + 22, 130, 14);
			canvas.setColor(Color.green);
			if (spaceObject.currentTarget > 0) {
				if (spaceObject.typeManoeuvre == Action.OppositeTarget)
					strType = "removal with "
					+ scene.Objects.Objects[spaceObject.currentTarget].model.strIDName
					+ ": " + scene.Objects.Objects[object.currentTarget].strObjectName;
				else if (spaceObject.targetType == Task.Attack) strType 
				  = "attacking " + scene.Objects.Objects[spaceObject.currentTarget].model.strIDName
					+ ": " + scene.Objects.Objects[spaceObject.currentTarget].strObjectName;
				else if (spaceObject.targetType == Task.SupportAttack) strType 
				  = "attacking " + scene.Objects.Objects[spaceObject.currentTarget].model.strIDName
					+ ": " + scene.Objects.Objects[spaceObject.currentTarget].strObjectName;
				else if (spaceObject.targetType == Task.Defend) strType = "following "
					+ scene.Objects.Objects[spaceObject.currentTarget].model.strIDName + ": "
					+ scene.Objects.Objects[spaceObject.currentTarget].strObjectName;
				else if (spaceObject.targetType == Task.None) strType = "none ";
			}   else if (spaceObject.currentTarget == Action.AvoidCollision) strType = "avoid collision";
			else if (spaceObject.currentTarget == Action.AvoidFire) strType = "avoid fire";
			else strType = "none";
			canvas.drawString(strType, x + 75, y+34);
			/******************************************/

			//Flight Plan 
			if (spaceObject.flightPlan != iPlan) {
				canvas.setColor(Color.black);
				canvas.fillRect(x + 60, y + 22, 140, 14);
				canvas.setColor(Color.green);
				canvas.drawString(scene.plan.getName(spaceObject), x + 60, y+34);
			}
			

			//****************************************
			//			targeted object
			//****************************************
			if (spaceObject.taskScreen != -1 && scene.Objects.Objects[spaceObject.taskScreen].isLive) {
				//model image
				if (iTarget != spaceObject.taskScreen) {
					if (scene.Objects.Objects[spaceObject.taskScreen].model.imgModel != null) {
						canvas.drawImage(scene.Objects.Objects[spaceObject.taskScreen].model.imgModel,
									x, y + 40, applet);
					} else {
						canvas.setColor(Color.black);
						canvas.fillRect(x, y + 40, 70, 70);
					}
				}

				//speed, shield, hull
				if (iSpeed != scene.Objects.Objects[spaceObject.taskScreen].currentSpeed
					|| iShield != scene.Objects.Objects[spaceObject.taskScreen].Shield
					|| iHull != scene.Objects.Objects[spaceObject.taskScreen].Hull) {
					canvas.setColor(Color.black);
					canvas.fillRect(x + 70, y + 40, w - 70, 70);
					canvas.setColor(Color.green);
					canvas.drawString("SPD: " + scene.Objects.Objects[spaceObject.taskScreen].currentSpeed, x + 75, y + 60);
					canvas.drawString("SHL: " + scene.Objects.Objects[spaceObject.taskScreen].Shield, x + 75, y + 75);
					canvas.drawString("HUL: " + scene.Objects.Objects[spaceObject.taskScreen].Hull, x + 75, y + 90);
					iSpeed = scene.Objects.Objects[spaceObject.taskScreen].currentSpeed;
					iShield = scene.Objects.Objects[spaceObject.taskScreen].Shield;
					iHull = scene.Objects.Objects[spaceObject.taskScreen].Hull;
	

					//target targeting system
					String strType = "none of all";
					canvas.setColor(Color.black);
					canvas.fillRect(x + 75, y + 94, 125, 14);
					canvas.setColor(Color.green);

 	           		SpaceObject obj = scene.Objects.Objects[spaceObject.taskScreen];
					if (obj.currentTarget > 0) {						
						if (obj.typeManoeuvre == Action.OppositeTarget)
							strType = "O "
							+ scene.Objects.Objects[obj.currentTarget].model.strIDName
							+ ": " + scene.Objects.Objects[obj.currentTarget].strObjectName;

						else if (obj.targetType == Task.Attack) strType 
							  = "A " + scene.Objects.Objects[obj.currentTarget].model.strIDName
							+ ": " + scene.Objects.Objects[obj.currentTarget].strObjectName;

						else if (spaceObject.targetType == Task.SupportAttack) strType 
						  = "A " + scene.Objects.Objects[obj.currentTarget].model.strIDName
							+ ": " + scene.Objects.Objects[obj.currentTarget].strObjectName;

						else if (obj.targetType == Task.Defend) strType = "F "
							+ scene.Objects.Objects[obj.currentTarget].model.strIDName + ": "
							+ scene.Objects.Objects[obj.currentTarget].strObjectName;
						else if (obj.targetType == Task.None) strType = "none";

					} else if (obj.currentTarget == Action.AvoidCollision) strType = "avoid collision";

					else if (obj.currentTarget == Action.AvoidFire) strType = "avoid fire";
					else strType = "none";

					canvas.drawString(strType, x + 75, y+105);
				}


    			if (iTarget != spaceObject.taskScreen) {
					canvas.setColor(Color.black);
					canvas.fillRect(x, y + 110, w, 14);
				  	switch(scene.Objects.Objects[spaceObject.taskScreen].Side) {
						case 0: canvas.setColor(Color.white); break;			 	//white - neutral
						case 1: canvas.setColor(new Color(100, 255, 100)); break;//green
						case 2: canvas.setColor(new Color(255, 70, 70)); break;  //red
						case 3: canvas.setColor(new Color(255, 200, 100)); break;//yellow
						case 4: canvas.setColor(new Color(255, 113, 255)); break;//pink
						default: canvas.setColor(Color.white);				 //white - unknown
					}
					canvas.drawString(
						scene.Objects.Objects[spaceObject.taskScreen].model.strIDName + ": " +
						scene.Objects.Objects[spaceObject.taskScreen].strObjectName,
						x, y + 124);

					iTarget = spaceObject.taskScreen;
				}

				//distance
				int dist = (int)spaceObject.getDistance(scene.Objects.Objects[spaceObject.taskScreen]);
				if (dist!=iDistance) {
					canvas.setColor(Color.black);
					canvas.fillRect(x, y + 124, w, 16);
					canvas.setColor(Color.green);
					canvas.drawString("distance: " + dist/10 + "m", x, y + 136);
					iDistance = dist;
				}
				iTaskScreen = -1;
			} else {
				if (iTaskScreen == -1) {
					this.expaired();
					iTaskScreen = 1;
					spaceObject.taskScreen = -1;
					canvas.setColor(Color.black);
					canvas.fillRect(x, y + 35, w, 105);
				}
			}
		} else {
			if (iCam == -1) {
				this.expaired();
				iCam = 1;
				clearAll();
			}
		}
	}

	public void drawTaskScreen() {
		drawTask();
	}


	public void nav() {
	if (camera.iRelatedObject != -1)
		if (scene.Objects.Objects[camera.iRelatedObject].taskScreen != -1) {
			media.auPing.play();
			int t = scene.Objects.getNavTarget(scene.Objects.Objects[camera.iRelatedObject].taskScreen);
			scene.Objects.Objects[camera.iRelatedObject].setTaskScreen(t);
		} else {
			int t = scene.Objects.getNavTarget(0);
			scene.Objects.Objects[camera.iRelatedObject].setTaskScreen(t);
		}
	}

	public void foe() {
	if (camera.iRelatedObject != -1)
		if (scene.Objects.Objects[camera.iRelatedObject].taskScreen != -1) {
			media.auPing.play();
			int t = scene.Objects.getFoeTarget(scene.Objects.Objects[camera.iRelatedObject].taskScreen,
				scene.Objects.Objects[camera.iRelatedObject].Side);
			scene.Objects.Objects[camera.iRelatedObject].setTaskScreen(t);
		} else {
			int t = scene.Objects.getFoeTarget(0, scene.Objects.Objects[camera.iRelatedObject].Side);
			scene.Objects.Objects[camera.iRelatedObject].setTaskScreen(t);
		}
	}

	public void friend() {
	if (camera.iRelatedObject != -1)
		if (scene.Objects.Objects[camera.iRelatedObject].taskScreen != -1) {
			media.auPing.play();
			int t = scene.Objects.getFriendTarget(scene.Objects.Objects[camera.iRelatedObject].taskScreen,
				scene.Objects.Objects[camera.iRelatedObject].Side);
			scene.Objects.Objects[camera.iRelatedObject].setTaskScreen(t);
		} else {
			int t = scene.Objects.getFriendTarget(0, scene.Objects.Objects[camera.iRelatedObject].Side);
			scene.Objects.Objects[camera.iRelatedObject].setTaskScreen(t);
		}
	}

	public void setAttacker() {
	  if (camera.iRelatedObject != -1) {
		int t = scene.Objects.getNearestAttacker(camera.iRelatedObject);
		if (t != -1) {
			media.auPing.play();
			scene.Objects.Objects[camera.iRelatedObject].setTaskScreen(t);
		}
	  }
	}

	public void setTargetAttacker() {
	  if (camera.iRelatedObject != -1) {
		if (scene.Objects.Objects[camera.iRelatedObject].taskScreen < 0) return;
		int t = scene.Objects.getNearestAttacker(scene.Objects.Objects[camera.iRelatedObject].taskScreen);
		if (t != -1) {
			media.auPing.play();
			scene.Objects.Objects[camera.iRelatedObject].setTaskScreen(t);
		}
	  }
	}

	public void prev() {
	if (camera.iRelatedObject != -1)
		if (scene.Objects.Objects[camera.iRelatedObject].taskScreen != -1) {
			media.auPing.play();
			int t = scene.Objects.getPrevTarget(scene.Objects.Objects[camera.iRelatedObject].taskScreen);
			scene.Objects.Objects[camera.iRelatedObject].setTaskScreen(t);
		} else {
			int t = scene.Objects.getPrevTarget(0);
			scene.Objects.Objects[camera.iRelatedObject].setTaskScreen(t);
		}
	}

	public void next() {
	if (camera.iRelatedObject != -1)
		if (scene.Objects.Objects[camera.iRelatedObject].taskScreen != -1) {
			media.auPing.play();
			int t = scene.Objects.getNextTarget(scene.Objects.Objects[camera.iRelatedObject].taskScreen);
			scene.Objects.Objects[camera.iRelatedObject].setTaskScreen(t);
		} else {
			int t = scene.Objects.getNextTarget(0);
			scene.Objects.Objects[camera.iRelatedObject].setTaskScreen(t);
		}
	}

	public void setAgression(int level) {
		if (camera.iRelatedObject != -1) {
			media.auPing.play();
			scene.Objects.Objects[camera.iRelatedObject].agressionLevel = level;
			scene.Objects.Objects[camera.iRelatedObject].resetTarget();
		}
	}

	public void resetTarget() {
		if (camera.iRelatedObject != -1) {
			scene.Objects.Objects[camera.iRelatedObject].resetTarget();
			scene.Objects.Objects[camera.iRelatedObject].currentTask.iType
				= Task.None;
			scene.Objects.Objects[camera.iRelatedObject].currentTask.iTarget
				= -1;
			scene.Objects.Objects[camera.iRelatedObject].currentTask.iSecondaryTarget
				= -1;
			media.auPing.play();
		}
	}

	public void setPrimary() {
		if (camera.iRelatedObject != -1) {
			media.auPing.play();
			scene.Objects.Objects[camera.iRelatedObject].resetTarget();
			scene.Objects.Objects[camera.iRelatedObject].currentTask.iTarget
				= scene.Objects.Objects[camera.iRelatedObject].taskScreen;
			scene.Objects.Objects[camera.iRelatedObject].currentTask.id
		= scene.Objects.Objects[scene.Objects.Objects[camera.iRelatedObject].currentTask.iTarget].id;
			scene.Objects.Objects[camera.iRelatedObject].currentTask.isActual = true;

			SpaceObject obj = scene.Objects.Objects[camera.iRelatedObject];
			if (scene.Objects.Objects[obj.currentTask.iTarget].Side == obj.Side)
				setDefend();
			else {
				if (scene.Objects.Objects[camera.iRelatedObject].model.isFighter) 
					setAttack();
				else
					setDefend();
			}
		}
	}

	public void setSecondary() {
		if (camera.iRelatedObject != -1) {
			media.auPing.play();
			scene.Objects.Objects[camera.iRelatedObject].resetTarget();
			scene.Objects.Objects[camera.iRelatedObject].currentTask.iSecondaryTarget
				= scene.Objects.Objects[camera.iRelatedObject].taskScreen;
			scene.Objects.Objects[camera.iRelatedObject].currentTask.sid
		= scene.Objects.Objects[scene.Objects.Objects[camera.iRelatedObject].currentTask.iSecondaryTarget].id;
			scene.Objects.Objects[camera.iRelatedObject].currentTask.isSecondaryActual = true;

			SpaceObject obj = scene.Objects.Objects[camera.iRelatedObject];
			if (scene.Objects.Objects[obj.currentTask.iSecondaryTarget].Side == obj.Side)
				setDefend();
			else {
				if (scene.Objects.Objects[camera.iRelatedObject].model.isFighter) 
					setAttack();
				else
					setDefend();
			}
		}
	}
	
	public void setDefend() {
		if (camera.iRelatedObject != -1
			&& (scene.Objects.Objects[camera.iRelatedObject].model.isFighter
				|| scene.Objects.Objects[camera.iRelatedObject].model.isShip)) {
			media.auPing.play();
			scene.Objects.Objects[camera.iRelatedObject].resetTarget();
			scene.Objects.Objects[camera.iRelatedObject].currentTask.iType = Task.Defend;
		}
	}

	public void setAttack() {
		if (camera.iRelatedObject != -1 && scene.Objects.Objects[camera.iRelatedObject].model.isFighter) {
			media.auPing.play();
			scene.Objects.Objects[camera.iRelatedObject].resetTarget();
			scene.Objects.Objects[camera.iRelatedObject].currentTask.iType = Task.Attack;
		}
	}

	public void setPrevPlan() {
		if (camera.iRelatedObject != -1) {
			media.auPing.play();
			int plan = scene.plan.getPrevPlan(scene.Objects.Objects[camera.iRelatedObject]);
			scene.Objects.Objects[camera.iRelatedObject].flightPlan = plan;
		}
	}

	public void setNextPlan() {
		if (camera.iRelatedObject != -1) {
			media.auPing.play();
			int plan = scene.plan.getNextPlan(scene.Objects.Objects[camera.iRelatedObject]);
			if (plan != -1) scene.Objects.Objects[camera.iRelatedObject].flightPlan = plan;
		}
	}
	
	public void clearifyPosition(int w, int h) {
		this.x = w - this.w - 10;
		this.y = h - this.h - 10;
	}
}