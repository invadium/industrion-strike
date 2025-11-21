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
	Applet theApplet;
	Graphics theCanvas;
	Scene theScene;
	Camera theCamera;
	SpaceObject theObject;
	Media theMedia;
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

	public TaskScreen(Applet theApplet, Graphics theCanvas, Scene theScene,
						Camera theCamera, Media theMedia) {
		this.theApplet = theApplet;
		this.theCanvas = theCanvas;
		this.theScene = theScene;
		this.theCamera = theCamera;
		this.theMedia = theMedia;
	}

	private void clearAll() {           
		theCanvas.setColor(Color.black);
		theCanvas.fillRect(x, y, w, h);		
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
 		theCanvas.setFont(screenFontL);
		theCanvas.setColor(Color.green);

		if (theCamera.iRelatedObject != -1) {
			iCam = -1;
			theObject = theScene.Objects.Objects[theCamera.iRelatedObject];

			if (iType != theObject.currentTask.iType  || iAgression != theObject.agressionLevel
				|| iPrimary != theScene.Objects.Objects[theCamera.iRelatedObject].currentTask.iTarget
				|| iSecondary != theScene.Objects.Objects[theCamera.iRelatedObject].currentTask.iSecondaryTarget) {

				theCanvas.setColor(Color.black);
				theCanvas.fillRect(x, y, w, 40);
				theCanvas.setColor(Color.green);

				//Task Type
				switch(theObject.currentTask.iType) {
					case Task.Defend: theCanvas.drawString("Follow:", x, y + 10);
								  break;
					case Task.Attack: theCanvas.drawString("Attack:", x, y + 10);
								  break;
					default: theCanvas.drawString("None:", x, y + 10);
				}

				//Primary Task
				if (theScene.Objects.Objects[theCamera.iRelatedObject].currentTask.iTarget == -1) {
					theCanvas.drawString("---- empty ----", x + 45, y + 10);
				} else {
					theCanvas.drawString(
						theScene.Objects.Objects[theObject.currentTask.iTarget].theModel.strIDName + ": " +
						theScene.Objects.Objects[theObject.currentTask.iTarget].strObjectName,
						x + 45, y + 10);
				}

				//Secondary Task
				if (theScene.Objects.Objects[theCamera.iRelatedObject].currentTask.iSecondaryTarget == -1) {
					theCanvas.drawString("---- empty ----", x + 45, y + 22);
				} else {
					theCanvas.drawString(
						theScene.Objects.Objects[theObject.currentTask.iSecondaryTarget].theModel.strIDName + ": " +
						theScene.Objects.Objects[theObject.currentTask.iSecondaryTarget].strObjectName,
						x + 45, y + 22);
				}

				//Agressive level
				theCanvas.setColor(Color.cyan);
				theCanvas.drawString("AL:", x, y+34);
				for (int i=1; i<5; i++) theCanvas.drawString(""+i, x+i*10+10, y+34);
				theCanvas.setColor(Color.red);
				theCanvas.drawString("" + theObject.agressionLevel, x+theObject.agressionLevel*10+10, y+34);

				iType = theObject.currentTask.iType;
				iPrimary = theScene.Objects.Objects[theCamera.iRelatedObject].currentTask.iTarget;
				iSecondary = theScene.Objects.Objects[theCamera.iRelatedObject].currentTask.iSecondaryTarget;
				iAgression = theObject.agressionLevel;
			}

			/****************************************
			//only for debbuging purposes
			//show current target name
			String strType = "none of all";
			theCanvas.setColor(Color.black);
			theCanvas.fillRect(x + 70, y + 22, 130, 14);
			theCanvas.setColor(Color.green);
			if (theObject.currentTarget > 0) {
				if (theObject.typeManoeuvre == Action.OppositeTarget)
					strType = "removal with "
					+ theScene.Objects.Objects[theObject.currentTarget].theModel.strIDName
					+ ": " + theScene.Objects.Objects[theObject.currentTarget].strObjectName;
				else if (theObject.targetType == Task.Attack) strType 
				  = "attacking " + theScene.Objects.Objects[theObject.currentTarget].theModel.strIDName
					+ ": " + theScene.Objects.Objects[theObject.currentTarget].strObjectName;
				else if (theObject.targetType == Task.SupportAttack) strType 
				  = "attacking " + theScene.Objects.Objects[theObject.currentTarget].theModel.strIDName
					+ ": " + theScene.Objects.Objects[theObject.currentTarget].strObjectName;
				else if (theObject.targetType == Task.Defend) strType = "following "
					+ theScene.Objects.Objects[theObject.currentTarget].theModel.strIDName + ": "
					+ theScene.Objects.Objects[theObject.currentTarget].strObjectName;
				else if (theObject.targetType == Task.None) strType = "none ";
			}   else if (theObject.currentTarget == Action.AvoidCollision) strType = "avoid collision";
			else if (theObject.currentTarget == Action.AvoidFire) strType = "avoid fire";
			else strType = "none";
			theCanvas.drawString(strType, x + 75, y+34);
			/******************************************/

			//Flight Plan 
			if (theObject.flightPlan != iPlan) {
				theCanvas.setColor(Color.black);
				theCanvas.fillRect(x + 60, y + 22, 140, 14);
				theCanvas.setColor(Color.green);
				theCanvas.drawString(theScene.thePlan.getName(theObject), x + 60, y+34);
			}
			

			//****************************************
			//			targeted object
			//****************************************
			if (theObject.taskScreen != -1 && theScene.Objects.Objects[theObject.taskScreen].isLive) {
				//model image
				if (iTarget != theObject.taskScreen) {
					if (theScene.Objects.Objects[theObject.taskScreen].theModel.imgModel != null) {
						theCanvas.drawImage(theScene.Objects.Objects[theObject.taskScreen].theModel.imgModel,
									x, y + 40, theApplet);
					} else {
						theCanvas.setColor(Color.black);
						theCanvas.fillRect(x, y + 40, 70, 70);
					}
				}

				//speed, shield, hull
				if (iSpeed != theScene.Objects.Objects[theObject.taskScreen].currentSpeed
					|| iShield != theScene.Objects.Objects[theObject.taskScreen].Shield
					|| iHull != theScene.Objects.Objects[theObject.taskScreen].Hull) {
					theCanvas.setColor(Color.black);
					theCanvas.fillRect(x + 70, y + 40, w - 70, 70);
					theCanvas.setColor(Color.green);
					theCanvas.drawString("SPD: " + theScene.Objects.Objects[theObject.taskScreen].currentSpeed, x + 75, y + 60);
					theCanvas.drawString("SHL: " + theScene.Objects.Objects[theObject.taskScreen].Shield, x + 75, y + 75);
					theCanvas.drawString("HUL: " + theScene.Objects.Objects[theObject.taskScreen].Hull, x + 75, y + 90);
					iSpeed = theScene.Objects.Objects[theObject.taskScreen].currentSpeed;
					iShield = theScene.Objects.Objects[theObject.taskScreen].Shield;
					iHull = theScene.Objects.Objects[theObject.taskScreen].Hull;
	

					//target targeting system
					String strType = "none of all";
					theCanvas.setColor(Color.black);
					theCanvas.fillRect(x + 75, y + 94, 125, 14);
					theCanvas.setColor(Color.green);

 	           		SpaceObject obj = theScene.Objects.Objects[theObject.taskScreen];
					if (obj.currentTarget > 0) {						
						if (obj.typeManoeuvre == Action.OppositeTarget)
							strType = "O "
							+ theScene.Objects.Objects[obj.currentTarget].theModel.strIDName
							+ ": " + theScene.Objects.Objects[obj.currentTarget].strObjectName;

						else if (obj.targetType == Task.Attack) strType 
							  = "A " + theScene.Objects.Objects[obj.currentTarget].theModel.strIDName
							+ ": " + theScene.Objects.Objects[obj.currentTarget].strObjectName;

						else if (theObject.targetType == Task.SupportAttack) strType 
						  = "A " + theScene.Objects.Objects[obj.currentTarget].theModel.strIDName
							+ ": " + theScene.Objects.Objects[obj.currentTarget].strObjectName;

						else if (obj.targetType == Task.Defend) strType = "F "
							+ theScene.Objects.Objects[obj.currentTarget].theModel.strIDName + ": "
							+ theScene.Objects.Objects[obj.currentTarget].strObjectName;
						else if (obj.targetType == Task.None) strType = "none";

					} else if (obj.currentTarget == Action.AvoidCollision) strType = "avoid collision";

					else if (obj.currentTarget == Action.AvoidFire) strType = "avoid fire";
					else strType = "none";

					theCanvas.drawString(strType, x + 75, y+105);
				}


    			if (iTarget != theObject.taskScreen) {
					theCanvas.setColor(Color.black);
					theCanvas.fillRect(x, y + 110, w, 14);
				  	switch(theScene.Objects.Objects[theObject.taskScreen].Side) {
						case 0: theCanvas.setColor(Color.white); break;			 	//white - neutral
						case 1: theCanvas.setColor(new Color(100, 255, 100)); break;//green
						case 2: theCanvas.setColor(new Color(255, 70, 70)); break;  //red
						case 3: theCanvas.setColor(new Color(255, 200, 100)); break;//yellow
						case 4: theCanvas.setColor(new Color(255, 113, 255)); break;//pink
						default: theCanvas.setColor(Color.white);				 //white - unknown
					}
					theCanvas.drawString(
						theScene.Objects.Objects[theObject.taskScreen].theModel.strIDName + ": " +
						theScene.Objects.Objects[theObject.taskScreen].strObjectName,
						x, y + 124);

					iTarget = theObject.taskScreen;
				}

				//distance
				int dist = (int)theObject.getDistance(theScene.Objects.Objects[theObject.taskScreen]);
				if (dist!=iDistance) {
					theCanvas.setColor(Color.black);
					theCanvas.fillRect(x, y + 124, w, 16);
					theCanvas.setColor(Color.green);
					theCanvas.drawString("distance: " + dist/10 + "m", x, y + 136);
					iDistance = dist;
				}
				iTaskScreen = -1;
			} else {
				if (iTaskScreen == -1) {
					this.expaired();
					iTaskScreen = 1;
					theObject.taskScreen = -1;
					theCanvas.setColor(Color.black);
					theCanvas.fillRect(x, y + 35, w, 105);
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
	if (theCamera.iRelatedObject != -1)
		if (theScene.Objects.Objects[theCamera.iRelatedObject].taskScreen != -1) {
			theMedia.auPing.play();
			int t = theScene.Objects.getNavTarget(theScene.Objects.Objects[theCamera.iRelatedObject].taskScreen);
			theScene.Objects.Objects[theCamera.iRelatedObject].setTaskScreen(t);
		} else {
			int t = theScene.Objects.getNavTarget(0);
			theScene.Objects.Objects[theCamera.iRelatedObject].setTaskScreen(t);
		}
	}

	public void foe() {
	if (theCamera.iRelatedObject != -1)
		if (theScene.Objects.Objects[theCamera.iRelatedObject].taskScreen != -1) {
			theMedia.auPing.play();
			int t = theScene.Objects.getFoeTarget(theScene.Objects.Objects[theCamera.iRelatedObject].taskScreen,
				theScene.Objects.Objects[theCamera.iRelatedObject].Side);
			theScene.Objects.Objects[theCamera.iRelatedObject].setTaskScreen(t);
		} else {
			int t = theScene.Objects.getFoeTarget(0, theScene.Objects.Objects[theCamera.iRelatedObject].Side);
			theScene.Objects.Objects[theCamera.iRelatedObject].setTaskScreen(t);
		}
	}

	public void friend() {
	if (theCamera.iRelatedObject != -1)
		if (theScene.Objects.Objects[theCamera.iRelatedObject].taskScreen != -1) {
			theMedia.auPing.play();
			int t = theScene.Objects.getFriendTarget(theScene.Objects.Objects[theCamera.iRelatedObject].taskScreen,
				theScene.Objects.Objects[theCamera.iRelatedObject].Side);
			theScene.Objects.Objects[theCamera.iRelatedObject].setTaskScreen(t);
		} else {
			int t = theScene.Objects.getFriendTarget(0, theScene.Objects.Objects[theCamera.iRelatedObject].Side);
			theScene.Objects.Objects[theCamera.iRelatedObject].setTaskScreen(t);
		}
	}

	public void setAttacker() {
	  if (theCamera.iRelatedObject != -1) {
		int t = theScene.Objects.getNearestAttacker(theCamera.iRelatedObject);
		if (t != -1) {
			theMedia.auPing.play();
			theScene.Objects.Objects[theCamera.iRelatedObject].setTaskScreen(t);
		}
	  }
	}

	public void setTargetAttacker() {
	  if (theCamera.iRelatedObject != -1) {
		if (theScene.Objects.Objects[theCamera.iRelatedObject].taskScreen < 0) return;
		int t = theScene.Objects.getNearestAttacker(theScene.Objects.Objects[theCamera.iRelatedObject].taskScreen);
		if (t != -1) {
			theMedia.auPing.play();
			theScene.Objects.Objects[theCamera.iRelatedObject].setTaskScreen(t);
		}
	  }
	}

	public void prev() {
	if (theCamera.iRelatedObject != -1)
		if (theScene.Objects.Objects[theCamera.iRelatedObject].taskScreen != -1) {
			theMedia.auPing.play();
			int t = theScene.Objects.getPrevTarget(theScene.Objects.Objects[theCamera.iRelatedObject].taskScreen);
			theScene.Objects.Objects[theCamera.iRelatedObject].setTaskScreen(t);
		} else {
			int t = theScene.Objects.getPrevTarget(0);
			theScene.Objects.Objects[theCamera.iRelatedObject].setTaskScreen(t);
		}
	}

	public void next() {
	if (theCamera.iRelatedObject != -1)
		if (theScene.Objects.Objects[theCamera.iRelatedObject].taskScreen != -1) {
			theMedia.auPing.play();
			int t = theScene.Objects.getNextTarget(theScene.Objects.Objects[theCamera.iRelatedObject].taskScreen);
			theScene.Objects.Objects[theCamera.iRelatedObject].setTaskScreen(t);
		} else {
			int t = theScene.Objects.getNextTarget(0);
			theScene.Objects.Objects[theCamera.iRelatedObject].setTaskScreen(t);
		}
	}

	public void setAgression(int level) {
		if (theCamera.iRelatedObject != -1) {
			theMedia.auPing.play();
			theScene.Objects.Objects[theCamera.iRelatedObject].agressionLevel = level;
			theScene.Objects.Objects[theCamera.iRelatedObject].resetTarget();
		}
	}

	public void resetTarget() {
		if (theCamera.iRelatedObject != -1) {
			theScene.Objects.Objects[theCamera.iRelatedObject].resetTarget();
			theScene.Objects.Objects[theCamera.iRelatedObject].currentTask.iType
				= Task.None;
			theScene.Objects.Objects[theCamera.iRelatedObject].currentTask.iTarget
				= -1;
			theScene.Objects.Objects[theCamera.iRelatedObject].currentTask.iSecondaryTarget
				= -1;
			theMedia.auPing.play();
		}
	}

	public void setPrimary() {
		if (theCamera.iRelatedObject != -1) {
			theMedia.auPing.play();
			theScene.Objects.Objects[theCamera.iRelatedObject].resetTarget();
			theScene.Objects.Objects[theCamera.iRelatedObject].currentTask.iTarget
				= theScene.Objects.Objects[theCamera.iRelatedObject].taskScreen;
			theScene.Objects.Objects[theCamera.iRelatedObject].currentTask.id
		= theScene.Objects.Objects[theScene.Objects.Objects[theCamera.iRelatedObject].currentTask.iTarget].id;
			theScene.Objects.Objects[theCamera.iRelatedObject].currentTask.isActual = true;

			SpaceObject obj = theScene.Objects.Objects[theCamera.iRelatedObject];
			if (theScene.Objects.Objects[obj.currentTask.iTarget].Side == obj.Side)
				setDefend();
			else {
				if (theScene.Objects.Objects[theCamera.iRelatedObject].theModel.isFighter) 
					setAttack();
				else
					setDefend();
			}
		}
	}

	public void setSecondary() {
		if (theCamera.iRelatedObject != -1) {
			theMedia.auPing.play();
			theScene.Objects.Objects[theCamera.iRelatedObject].resetTarget();
			theScene.Objects.Objects[theCamera.iRelatedObject].currentTask.iSecondaryTarget
				= theScene.Objects.Objects[theCamera.iRelatedObject].taskScreen;
			theScene.Objects.Objects[theCamera.iRelatedObject].currentTask.sid
		= theScene.Objects.Objects[theScene.Objects.Objects[theCamera.iRelatedObject].currentTask.iSecondaryTarget].id;
			theScene.Objects.Objects[theCamera.iRelatedObject].currentTask.isSecondaryActual = true;

			SpaceObject obj = theScene.Objects.Objects[theCamera.iRelatedObject];
			if (theScene.Objects.Objects[obj.currentTask.iSecondaryTarget].Side == obj.Side)
				setDefend();
			else {
				if (theScene.Objects.Objects[theCamera.iRelatedObject].theModel.isFighter) 
					setAttack();
				else
					setDefend();
			}
		}
	}
	
	public void setDefend() {
		if (theCamera.iRelatedObject != -1
			&& (theScene.Objects.Objects[theCamera.iRelatedObject].theModel.isFighter
				|| theScene.Objects.Objects[theCamera.iRelatedObject].theModel.isShip)) {
			theMedia.auPing.play();
			theScene.Objects.Objects[theCamera.iRelatedObject].resetTarget();
			theScene.Objects.Objects[theCamera.iRelatedObject].currentTask.iType = Task.Defend;
		}
	}

	public void setAttack() {
		if (theCamera.iRelatedObject != -1 && theScene.Objects.Objects[theCamera.iRelatedObject].theModel.isFighter) {
			theMedia.auPing.play();
			theScene.Objects.Objects[theCamera.iRelatedObject].resetTarget();
			theScene.Objects.Objects[theCamera.iRelatedObject].currentTask.iType = Task.Attack;
		}
	}

	public void setPrevPlan() {
		if (theCamera.iRelatedObject != -1) {
			theMedia.auPing.play();
			int plan = theScene.thePlan.getPrevPlan(theScene.Objects.Objects[theCamera.iRelatedObject]);
			theScene.Objects.Objects[theCamera.iRelatedObject].flightPlan = plan;
		}
	}

	public void setNextPlan() {
		if (theCamera.iRelatedObject != -1) {
			theMedia.auPing.play();
			int plan = theScene.thePlan.getNextPlan(theScene.Objects.Objects[theCamera.iRelatedObject]);
			if (plan != -1) theScene.Objects.Objects[theCamera.iRelatedObject].flightPlan = plan;
		}
	}
	
	public void clearifyPosition(int w, int h) {
		this.x = w - this.w - 10;
		this.y = h - this.h - 10;
	}
}