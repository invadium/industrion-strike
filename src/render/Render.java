package render;

import math.*;
import java.applet.*;
import java.awt.*;

import engine.Strike;
import primitives.RColor;
import scene.Scene;
import scene.Camera;
import scene.SpaceObject;
import media.Media;

public class Render {
	Strike theApplet;
	Graphics theCanvas;
	Media theMedia;
	Scene theScene;
	Camera theCamera;
	Radar theRadar;
	private Visualizer theVisualizer;
    private Visualizer plateVisualizer;
    private Visualizer frameVisualizer;
	RECollector theRECollector;

	public boolean drawCross = true;
	public boolean drawFrame = false;
	public boolean drawStars = true;
	public int targetCross = 0;
	public int indexCross;
	private int crossD;

	public Render(Strike theApplet, Graphics theCanvas, Media theMedia,
				Scene theScene, Camera theCamera, Radar theRadar) {
		this.theApplet = theApplet;
		this.theCanvas = theCanvas;
		this.theScene = theScene;
		this.theCamera = theCamera;
		this.theRadar = theRadar;
		this.theMedia = theMedia;
        plateVisualizer = new PlateVisualizer(theApplet, theCanvas, theMedia);
        frameVisualizer = new FrameVisualizer(theApplet, theCanvas, theMedia);
		theVisualizer = plateVisualizer;
        
		theRECollector = new RECollector();	
	}
    
    public void switchVisualization() {
        if (this.theVisualizer == plateVisualizer) this.theVisualizer = frameVisualizer;
        else this.theVisualizer = plateVisualizer;
    }
	
	public void updateCanvas(Graphics theCanvas) {
		this.theCanvas = theCanvas;
		theVisualizer.updateCanvas(theCanvas);
	}

	private void findCloseToCross(int index, int x, int y) {
		x -= theCamera.ScreenShiftX;
		y -= theCamera.ScreenShiftY;
		int d = x*x + y*y;
		if (d < crossD) {
			crossD = d;
			indexCross = index;
		}
	}

	private void bindCamera() {
		//move camera to the related object
		if (theCamera.iRelatedObject != -1) {
			theCamera.copyObj(theScene.Objects.Objects[theCamera.iRelatedObject]);
			if (theCamera.iRelationType == 0);
			else if (theCamera.iRelationType == 1) {
				if (theScene.Objects.Objects[theCamera.iRelatedObject].theModel.isShip) {
					//behind
					theCamera.moveForward(-theScene.Objects.Objects[theCamera.iRelatedObject].theModel.radius * 25);
					theCamera.moveUp(-theScene.Objects.Objects[theCamera.iRelatedObject].theModel.radius * 12);
					theCamera.changePitch(-900);
				} else {
					//close behind
					theCamera.moveForward(-theScene.Objects.Objects[theCamera.iRelatedObject].theModel.radius * 15);
					theCamera.moveUp(-theScene.Objects.Objects[theCamera.iRelatedObject].theModel.radius * 8);
					theCamera.changePitch(-600);
				}
			}
			else if (theCamera.iRelationType == 2) {
				//behind
				theCamera.moveForward(-theScene.Objects.Objects[theCamera.iRelatedObject].theModel.radius * 25);
				theCamera.moveUp(-theScene.Objects.Objects[theCamera.iRelatedObject].theModel.radius * 12);
				theCamera.changePitch(-900);
			}
			else if (theCamera.iRelationType == 3) {
				//top-behind 45 degrees view
				theCamera.moveForward(-theScene.Objects.Objects[theCamera.iRelatedObject].theModel.radius * 10);
				theCamera.moveUp(-theScene.Objects.Objects[theCamera.iRelatedObject].theModel.radius * 10);
				theCamera.changePitch(-2700);
			}
			else if (theCamera.iRelationType == 4) {
				//top view
				theCamera.moveForward(-theScene.Objects.Objects[theCamera.iRelatedObject].theModel.radius * 2);
				theCamera.moveUp(-theScene.Objects.Objects[theCamera.iRelatedObject].theModel.radius * 20);
				theCamera.changePitch(-5000);
			}
			else if (theCamera.iRelationType == 5) {
				//right view
				theCamera.moveStrafe(theScene.Objects.Objects[theCamera.iRelatedObject].theModel.radius * 10);
				theCamera.moveUp(-theScene.Objects.Objects[theCamera.iRelatedObject].theModel.radius * 2);
				theCamera.changeYaw(5400);
				theCamera.changePitch(-600);
			}
			else if (theCamera.iRelationType == 6) {
				//close front view
				theCamera.moveForward(theScene.Objects.Objects[theCamera.iRelatedObject].theModel.radius * 15);
				theCamera.moveUp(-theScene.Objects.Objects[theCamera.iRelatedObject].theModel.radius * 7);
				theCamera.moveStrafe(-theScene.Objects.Objects[theCamera.iRelatedObject].theModel.radius * 2);
				theCamera.changeYaw(11600);
				theCamera.changePitch(-1800);
			}
			else if (theCamera.iRelationType == 7) {
				//top-front 15-degrees view
				theCamera.moveForward(theScene.Objects.Objects[theCamera.iRelatedObject].theModel.radius * 15);
				theCamera.moveUp(-theScene.Objects.Objects[theCamera.iRelatedObject].theModel.radius * 5);
				theCamera.changeYaw(10800);
				theCamera.changePitch(-900);
			}
			else if (theCamera.iRelationType == 8) {
				//top-front 15-degrees view
				theCamera.moveForward(theScene.Objects.Objects[theCamera.iRelatedObject].theModel.radius * 40);
				theCamera.moveUp(-theScene.Objects.Objects[theCamera.iRelatedObject].theModel.radius * 10);
				theCamera.changeYaw(10800);
				theCamera.changePitch(-120);
			}
	    }
	}

	private void checkTargetCross() {
		targetCross = 0;
		if (theCamera.iRelatedObject == -1) return;
		if (theCamera.iRelationType != 0) return;
		int iTarget = theScene.Objects.Objects[theCamera.iRelatedObject].taskScreen;
		if (iTarget < 0) return;
		SpaceObject C = theScene.Objects.Objects[theCamera.iRelatedObject];
		if (C.theModel.cntWeapons == 0) return;
		SpaceObject T = theScene.Objects.Objects[iTarget];
	
			//find future object coordinates
			int fireTime = (int)C.getDistance(T) / C.theModel.weaponRack[0].maxSpeed << 1;
			T.futureMove(fireTime * T.currentSpeed / 2);
			Vector dir = new Vector(T.fx - C.x, T.fy - C.y, T.fz - C.z);
			int baseFi = theScene.Objects.getFiOnTarget(C, dir);

		if (baseFi < 60 || baseFi > 359*60) targetCross = 2;
		else if (baseFi < 60*2 || baseFi > 358*60) targetCross = 1;
	}

	public void drawRender() {
		int i, j;
		SpaceObject obj;
		Color markColor;
		crossD = 9999999;
		indexCross = -1;

		theRadar.clear();

		bindCamera();
		checkTargetCross();
		
		//rendering
		RenderedElement theElement;
		primitives.Point center = new primitives.Point();
		theRECollector.clearList();
		theScene.Objects.startListing();
		while ((obj = theScene.Objects.getNext()) != null) {
		  center.set(0, 0, 0);
		  center.renderPoint(theCamera, obj);
		  if (obj.theModel.radarMark > 0) {
			  switch(obj.Side) {
				case 0: markColor = Color.white; break;				 //white - neutral			
				case 1: markColor = new Color(100, 255, 100); break; //green
				case 2: markColor = new Color(255, 70, 70); break;   //red
				case 3: markColor = new Color(255, 200, 100); break; //yellow
				case 4: markColor = new Color(255, 113, 255); break; //pink
				default: markColor = Color.white;				     //white - unknown
			  }
			  theRadar.addMark(theScene.Objects.lastIndex, center.P.x, center.P.y, center.P.z, obj.theModel.radarMark, markColor);
		  }

		  if (((obj.Index != theCamera.iRelatedObject && theCamera.iRelationType == 0)
			|| theCamera.iRelationType != 0)
			  && center.ipx > -theCamera.ScreenWidth * 3
			  && center.ipx < theCamera.ScreenWidth * 4
			  && center.ipy > -theCamera.ScreenHeight * 3
			  && center.ipy < theCamera.ScreenHeight * 4) {

			  //check for cross hair
			  if (center.z_buffer > 0 && !obj.theModel.isSpace && !obj.theModel.isWeapon)
			  	findCloseToCross(obj.Index, center.ipx, center.ipy);

			  if(((center.z_buffer > theCamera.ShipSightRange && obj.theModel.isShip) ||
				  (center.z_buffer > theCamera.SightRange && !obj.theModel.isShip))
				  && obj.theModel.isSpace==false) {
					theElement = new RenderedElement();
					theElement.PrimitiveColor = obj.farColor;
					theElement.cntPoints = 1;
					theElement.ix1 = center.ipx;
					theElement.iy1 = center.ipy;
					theElement.d1 = center.z_buffer;
					theElement.calcAverageDistance();		
					theElement.volume = obj.theModel.farSize;
					theRECollector.addElement(theElement);
			  } else for (j = 0; j < obj.theModel.cntPrimitives; j++) {
				if ((obj.frame >= obj.theModel.thePrimitives[j].sFrame
					    && obj.frame <= obj.theModel.thePrimitives[j].eFrame)
					 || obj.theModel.thePrimitives[j].sFrame == 0) {
				  obj.theModel.thePrimitives[j].renderProection(
							theScene, theCamera, obj);
				  theRECollector.addElement(obj.theModel.thePrimitives[j].getRenderedElement(obj));
				}
			  }

	
		    if (theCamera.iRelatedObject != -1) {
			  //add target mark
			  if (obj.Index == theScene.Objects.Objects[theCamera.iRelatedObject].taskScreen
					&& center.z_buffer > 0) {
				RColor RC = new RColor(Color.yellow);
				RenderedElement RE = new RenderedElement();
				RE.cntPoints = 4;
				int sh;
				if((center.z_buffer > theCamera.ShipSightRange && obj.theModel.isShip) ||
				  (center.z_buffer > theCamera.SightRange && !obj.theModel.isShip)) sh = 2;
				else sh = 5;
				RE.ix1 = center.ipx - sh; RE.iy1 = center.ipy - sh;
				RE.ix2 = center.ipx + sh; RE.iy2 = center.ipy - sh;
				RE.ix3 = center.ipx + sh; RE.iy3 = center.ipy + sh;
				RE.ix4 = center.ipx - sh; RE.iy4 = center.ipy + sh;
				RE.distance = 1;
				RE.isWareframed = true;
				RE.PrimitiveColor = RC;
				theRECollector.addElement(RE);
			  }

			  //add primary target mark
			  if (obj.Index == theScene.Objects.Objects[theCamera.iRelatedObject].currentTask.iTarget
					&& center.z_buffer > 0) {
				RColor RC = new RColor(255, 100, 100);
				RenderedElement RE = new RenderedElement();
				RE.cntPoints = 4;
				int sh;
				if((center.z_buffer > theCamera.ShipSightRange && obj.theModel.isShip) ||
				  (center.z_buffer > theCamera.SightRange && !obj.theModel.isShip)) sh = 5;
				else sh = 11;
				RE.ix1 = center.ipx; RE.iy1 = center.ipy - sh;
				RE.ix2 = center.ipx + sh; RE.iy2 = center.ipy;
				RE.ix3 = center.ipx; RE.iy3 = center.ipy + sh;
				RE.ix4 = center.ipx - sh; RE.iy4 = center.ipy;
				RE.distance = 1;
				RE.isWareframed = true;
				RE.PrimitiveColor = RC;
				theRECollector.addElement(RE);
			  }

			  //add secondary target mark
			  if (obj.Index == theScene.Objects.Objects[theCamera.iRelatedObject].currentTask.iSecondaryTarget
					&& center.z_buffer > 0) {
				RColor RC = new RColor(48, 255, 255);
				RenderedElement RE = new RenderedElement();
				RE.cntPoints = 4;
				int sh;
				if((center.z_buffer > theCamera.ShipSightRange && obj.theModel.isShip) ||
				  (center.z_buffer > theCamera.SightRange && !obj.theModel.isShip)) sh = 4;
				else sh = 10;
				RE.ix1 = center.ipx; RE.iy1 = center.ipy - sh;
				RE.ix2 = center.ipx + sh; RE.iy2 = center.ipy;
				RE.ix3 = center.ipx; RE.iy3 = center.ipy + sh;
				RE.ix4 = center.ipx - sh; RE.iy4 = center.ipy;
				RE.distance = 1;
				RE.isWareframed = true;
				RE.PrimitiveColor = RC;
				theRECollector.addElement(RE);
			  }
		  	}
		  }
		}

		if (theCamera.iRelatedObject!=-1)
		if (theScene.Objects.Objects[theCamera.iRelatedObject].taskScreen!=-1) {
			obj = theScene.Objects.Objects[theScene.Objects.Objects[theCamera.iRelatedObject].taskScreen];
			center.set(0, 0, 0);
		    center.renderPoint(theCamera, obj);
	  		theRadar.addTargeted(center.P.x, center.P.y, center.P.z, obj.theModel.radarMark);
		}

		if (theRECollector.cntElements > 0) {
			//drawing
			i = theRECollector.iTail;
			while (i != -1) {
				theVisualizer.drawRenderedElement(theRECollector.Elements[i]);
				i = theRECollector.Elements[i].prevElement;
			}
		}
	}
}