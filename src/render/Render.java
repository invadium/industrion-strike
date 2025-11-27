package render;

import math.*;
import java.awt.*;

import engine.Strike;
import primitives.RColor;
import scene.Scene;
import scene.Camera;
import scene.SpaceObject;
import media.Media;

public class Render {
	Strike applet;
	Graphics canvas;
	Media media;
	Scene scene;
	Camera camera;
	Radar radar;
	private Visualizer visualizer;
    private Visualizer plateVisualizer;
    private Visualizer frameVisualizer;
	RECollector reCollector;

	public boolean drawCross = true;
	public boolean drawFrame = false;
	public boolean drawStars = true;
	public int targetCross = 0;
	public int indexCross;
	private int crossD;

	public Render(Strike applet, Graphics canvas, Media media,
				Scene scene, Camera camera, Radar radar) {
		this.applet = applet;
		this.canvas = canvas;
		this.scene = scene;
		this.camera = camera;
		this.radar = radar;
		this.media = media;
        plateVisualizer = new PlateVisualizer(applet, canvas, media);
        frameVisualizer = new FrameVisualizer(applet, canvas, media);
		visualizer = plateVisualizer;
        
		reCollector = new RECollector();	
	}
    
    public void switchVisualization() {
        if (this.visualizer == plateVisualizer) this.visualizer = frameVisualizer;
        else this.visualizer = plateVisualizer;
    }
	
	public void updateCanvas(Graphics canvas) {
		this.canvas = canvas;
		visualizer.updateCanvas(canvas);
	}

	private void findCloseToCross(int index, int x, int y) {
		x -= camera.ScreenShiftX;
		y -= camera.ScreenShiftY;
		int d = x*x + y*y;
		if (d < crossD) {
			crossD = d;
			indexCross = index;
		}
	}

	private void bindCamera() {
		//move camera to the related object
		if (camera.iRelatedObject != -1) {
			camera.copyObj(scene.Objects.Objects[camera.iRelatedObject]);
			if (camera.iRelationType == 0);
			else if (camera.iRelationType == 1) {
				if (scene.Objects.Objects[camera.iRelatedObject].model.isShip) {
					//behind
					camera.moveForward(-scene.Objects.Objects[camera.iRelatedObject].model.radius * 25);
					camera.moveUp(-scene.Objects.Objects[camera.iRelatedObject].model.radius * 12);
					camera.changePitch(-900);
				} else {
					//close behind
					camera.moveForward(-scene.Objects.Objects[camera.iRelatedObject].model.radius * 15);
					camera.moveUp(-scene.Objects.Objects[camera.iRelatedObject].model.radius * 8);
					camera.changePitch(-600);
				}
			}
			else if (camera.iRelationType == 2) {
				//behind
				camera.moveForward(-scene.Objects.Objects[camera.iRelatedObject].model.radius * 25);
				camera.moveUp(-scene.Objects.Objects[camera.iRelatedObject].model.radius * 12);
				camera.changePitch(-900);
			}
			else if (camera.iRelationType == 3) {
				//top-behind 45 degrees view
				camera.moveForward(-scene.Objects.Objects[camera.iRelatedObject].model.radius * 10);
				camera.moveUp(-scene.Objects.Objects[camera.iRelatedObject].model.radius * 10);
				camera.changePitch(-2700);
			}
			else if (camera.iRelationType == 4) {
				//top view
				camera.moveForward(-scene.Objects.Objects[camera.iRelatedObject].model.radius * 2);
				camera.moveUp(-scene.Objects.Objects[camera.iRelatedObject].model.radius * 20);
				camera.changePitch(-5000);
			}
			else if (camera.iRelationType == 5) {
				//right view
				camera.moveStrafe(scene.Objects.Objects[camera.iRelatedObject].model.radius * 10);
				camera.moveUp(-scene.Objects.Objects[camera.iRelatedObject].model.radius * 2);
				camera.changeYaw(5400);
				camera.changePitch(-600);
			}
			else if (camera.iRelationType == 6) {
				//close front view
				camera.moveForward(scene.Objects.Objects[camera.iRelatedObject].model.radius * 15);
				camera.moveUp(-scene.Objects.Objects[camera.iRelatedObject].model.radius * 7);
				camera.moveStrafe(-scene.Objects.Objects[camera.iRelatedObject].model.radius * 2);
				camera.changeYaw(11600);
				camera.changePitch(-1800);
			}
			else if (camera.iRelationType == 7) {
				//top-front 15-degrees view
				camera.moveForward(scene.Objects.Objects[camera.iRelatedObject].model.radius * 15);
				camera.moveUp(-scene.Objects.Objects[camera.iRelatedObject].model.radius * 5);
				camera.changeYaw(10800);
				camera.changePitch(-900);
			}
			else if (camera.iRelationType == 8) {
				//top-front 15-degrees view
				camera.moveForward(scene.Objects.Objects[camera.iRelatedObject].model.radius * 40);
				camera.moveUp(-scene.Objects.Objects[camera.iRelatedObject].model.radius * 10);
				camera.changeYaw(10800);
				camera.changePitch(-120);
			}
	    }
	}

	private void checkTargetCross() {
		targetCross = 0;
		if (camera.iRelatedObject == -1) return;
		if (camera.iRelationType != 0) return;
		int iTarget = scene.Objects.Objects[camera.iRelatedObject].taskScreen;
		if (iTarget < 0) return;
		SpaceObject C = scene.Objects.Objects[camera.iRelatedObject];
		if (C.model.cntWeapons == 0) return;
		SpaceObject T = scene.Objects.Objects[iTarget];
	
			//find future object coordinates
			int fireTime = (int)C.getDistance(T) / C.model.weaponRack[0].maxSpeed << 1;
			T.futureMove(fireTime * T.currentSpeed / 2);
			Vector dir = new Vector(T.fx - C.x, T.fy - C.y, T.fz - C.z);
			int baseFi = scene.Objects.getFiOnTarget(C, dir);

		if (baseFi < 60 || baseFi > 359*60) targetCross = 2;
		else if (baseFi < 60*2 || baseFi > 358*60) targetCross = 1;
	}

	public void drawRender() {
		int i, j;
		SpaceObject obj;
		Color markColor;
		crossD = 9999999;
		indexCross = -1;

		radar.clear();

		bindCamera();
		checkTargetCross();
		
		//rendering
		RenderedElement element;
		primitives.Point center = new primitives.Point();
		reCollector.clearList();
		scene.Objects.startListing();
		while ((obj = scene.Objects.getNext()) != null) {
		  center.set(0, 0, 0);
		  center.renderPoint(camera, obj);
		  if (obj.model.radarMark > 0) {
			  switch(obj.Side) {
				case 0: markColor = Color.white; break;				 //white - neutral			
				case 1: markColor = new Color(100, 255, 100); break; //green
				case 2: markColor = new Color(255, 70, 70); break;   //red
				case 3: markColor = new Color(255, 200, 100); break; //yellow
				case 4: markColor = new Color(255, 113, 255); break; //pink
				default: markColor = Color.white;				     //white - unknown
			  }
			  radar.addMark(scene.Objects.lastIndex, center.P.x, center.P.y, center.P.z, obj.model.radarMark, markColor);
		  }

		  if (((obj.Index != camera.iRelatedObject && camera.iRelationType == 0)
			|| camera.iRelationType != 0)
			  && center.ipx > -camera.ScreenWidth * 3
			  && center.ipx < camera.ScreenWidth * 4
			  && center.ipy > -camera.ScreenHeight * 3
			  && center.ipy < camera.ScreenHeight * 4) {

			  //check for cross hair
			  if (center.z_buffer > 0 && !obj.model.isSpace && !obj.model.isWeapon)
			  	findCloseToCross(obj.Index, center.ipx, center.ipy);

			  if(((center.z_buffer > camera.ShipSightRange && obj.model.isShip) ||
				  (center.z_buffer > camera.SightRange && !obj.model.isShip))
				  && obj.model.isSpace==false) {
					element = new RenderedElement();
					element.PrimitiveColor = obj.farColor;
					element.cntPoints = 1;
					element.ix1 = center.ipx;
					element.iy1 = center.ipy;
					element.d1 = center.z_buffer;
					element.calcAverageDistance();		
					element.volume = obj.model.farSize;
					reCollector.addElement(element);
			  } else for (j = 0; j < obj.model.cntPrimitives; j++) {
				if ((obj.frame >= obj.model.primitives[j].sFrame
					    && obj.frame <= obj.model.primitives[j].eFrame)
					 || obj.model.primitives[j].sFrame == 0) {
				  obj.model.primitives[j].renderProection(
							scene, camera, obj);
				  reCollector.addElement(obj.model.primitives[j].getRenderedElement(obj));
				}
			  }

	
		    if (camera.iRelatedObject != -1) {
			  //add target mark
			  if (obj.Index == scene.Objects.Objects[camera.iRelatedObject].taskScreen
					&& center.z_buffer > 0) {
				RColor RC = new RColor(Color.yellow);
				RenderedElement RE = new RenderedElement();
				RE.cntPoints = 4;
				int sh;
				if((center.z_buffer > camera.ShipSightRange && obj.model.isShip) ||
				  (center.z_buffer > camera.SightRange && !obj.model.isShip)) sh = 2;
				else sh = 5;
				RE.ix1 = center.ipx - sh; RE.iy1 = center.ipy - sh;
				RE.ix2 = center.ipx + sh; RE.iy2 = center.ipy - sh;
				RE.ix3 = center.ipx + sh; RE.iy3 = center.ipy + sh;
				RE.ix4 = center.ipx - sh; RE.iy4 = center.ipy + sh;
				RE.distance = 1;
				RE.isWareframed = true;
				RE.PrimitiveColor = RC;
				reCollector.addElement(RE);
			  }

			  //add primary target mark
			  if (obj.Index == scene.Objects.Objects[camera.iRelatedObject].currentTask.iTarget
					&& center.z_buffer > 0) {
				RColor RC = new RColor(255, 100, 100);
				RenderedElement RE = new RenderedElement();
				RE.cntPoints = 4;
				int sh;
				if((center.z_buffer > camera.ShipSightRange && obj.model.isShip) ||
				  (center.z_buffer > camera.SightRange && !obj.model.isShip)) sh = 5;
				else sh = 11;
				RE.ix1 = center.ipx; RE.iy1 = center.ipy - sh;
				RE.ix2 = center.ipx + sh; RE.iy2 = center.ipy;
				RE.ix3 = center.ipx; RE.iy3 = center.ipy + sh;
				RE.ix4 = center.ipx - sh; RE.iy4 = center.ipy;
				RE.distance = 1;
				RE.isWareframed = true;
				RE.PrimitiveColor = RC;
				reCollector.addElement(RE);
			  }

			  //add secondary target mark
			  if (obj.Index == scene.Objects.Objects[camera.iRelatedObject].currentTask.iSecondaryTarget
					&& center.z_buffer > 0) {
				RColor RC = new RColor(48, 255, 255);
				RenderedElement RE = new RenderedElement();
				RE.cntPoints = 4;
				int sh;
				if((center.z_buffer > camera.ShipSightRange && obj.model.isShip) ||
				  (center.z_buffer > camera.SightRange && !obj.model.isShip)) sh = 4;
				else sh = 10;
				RE.ix1 = center.ipx; RE.iy1 = center.ipy - sh;
				RE.ix2 = center.ipx + sh; RE.iy2 = center.ipy;
				RE.ix3 = center.ipx; RE.iy3 = center.ipy + sh;
				RE.ix4 = center.ipx - sh; RE.iy4 = center.ipy;
				RE.distance = 1;
				RE.isWareframed = true;
				RE.PrimitiveColor = RC;
				reCollector.addElement(RE);
			  }
		  	}
		  }
		}

		if (camera.iRelatedObject!=-1)
		if (scene.Objects.Objects[camera.iRelatedObject].taskScreen!=-1) {
			obj = scene.Objects.Objects[scene.Objects.Objects[camera.iRelatedObject].taskScreen];
			center.set(0, 0, 0);
		    center.renderPoint(camera, obj);
	  		radar.addTargeted(center.P.x, center.P.y, center.P.z, obj.model.radarMark);
		}

		if (reCollector.cntElements > 0) {
			//drawing
			i = reCollector.iTail;
			while (i != -1) {
				visualizer.drawRenderedElement(reCollector.Elements[i]);
				i = reCollector.Elements[i].prevElement;
			}
		}
	}
}