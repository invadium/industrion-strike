/****************************************************
 *
 *  Industrion Strike strategic space simulation game
 *  Distributed under General Public License
 *
 *  @author Igor Khotin (aka Shock)
 *  @version 1.2, June 2008
 *
 ****************************************************/

package engine;


import java.applet.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import javax.swing.Timer;
import java.net.*;

import parser.*;
import mission.*;
import scene.*;
import render.*;
import control.*;
import media.*;

/**
 * Mail applet class.
 * The entry point of whole application.
 * Events are also managed here
 * 
 * @author Igor Khotin
 *
 */
public class Strike extends Applet implements ActionListener,
		KeyListener, MouseListener, MouseWheelListener, ComponentListener, Runnable {
    private static final int FINAL_FONT_SIZE = 18;
            
	Thread flowThread;
	boolean preparation = true;
	Exception eRunTime;
	Timer ticker;
	Graphics bufCanvas;
	Graphics appCanvas;

	int iBufferHeight, iBufferWidth;
	Image imgBuffer;

	public AreaControl areaControl = new AreaControl();	
	public Loader theLoader;
	public Media theMedia;
	Controller AI;
	ModelsCollector Models;
	public Scene scene;
	public Camera camera;
	Stars theStars;
	public Radar radar;
	public StatusScreen statusScreen;
	public TaskScreen taskScreen;
	public TacticalScreen tacticalFriendly;
	ResultScreen result;
	public Render theRender;
	public Helper theHelper;
	

public void init() {
	theLoader = new Loader(this, this.getGraphics());
	theMedia = new Media(this);
	flowThread = new Thread(this);
	flowThread.start();
}

public void run() {
	camera = new Camera(this, -1, 0.0, 0.0, -6000.0, 0, 0, 0);
	//create screen buffer
	imgBuffer = createImage(camera.ScreenWidth, camera.ScreenHeight);
	iBufferWidth = getSize().width;
	iBufferHeight = getSize().height;

	bufCanvas = imgBuffer.getGraphics();
	appCanvas = this.getGraphics();
	this.setBackground(new Color(80, 80, 80));
	//this.setBackground(Color.black);

	try {
		theHelper = new Helper(appCanvas);
        theStars = new Stars(this, bufCanvas, camera);
		theLoader.increase(20); repaint();
		theLoader.increase(10); repaint();
		Models = new scene.ModelsCollector(this, bufCanvas, "data/models/" + this.getParameter("models"));
		theLoader.increase(20); repaint();
		String strMissionName = "missions/" + this.getParameter("mission");
		scene = new Scene(this, Models, camera, theStars, theMedia,
								strMissionName);
		theLoader.increase(40); repaint();
		radar = new Radar(this, appCanvas);
		statusScreen = new StatusScreen(this, appCanvas, scene, camera);
		taskScreen = new TaskScreen(this, appCanvas, scene, camera, theMedia);
		tacticalFriendly = new TacticalScreen(this, appCanvas, scene, camera);
		result = new ResultScreen(this, appCanvas, scene, theMedia);
		theRender = new Render(this, bufCanvas, theMedia, scene, camera, radar);
		AI = new Controller(scene, camera, Models, theMedia, theStars, theRender);
		theLoader.increase(20); repaint();				
	} catch(ModelsException e) {
		System.out.println(e.toString());
		eRunTime = e;
		repaint();
		return;
	} catch(ParserException e) {
		System.out.println(e.toString());
		eRunTime = e;
		repaint();
		return;
	} catch(SceneException e) {
		System.out.println(e.toString());
		eRunTime = e;
		repaint();
		return;
	} catch(InterpreterException e) {
		System.out.println(e.toString());
		eRunTime = e;
		repaint();
		return;
	}

	//start construction procedure
	int iProc = scene.Interpreter.theCommands.findProc("CONSTRUCTOR");
	if (iProc != -1) scene.Interpreter.runProc(iProc);
	
	this.clearifyPositions();
    theStars.create();
	
	statusScreen.expaired();
	taskScreen.expaired();
	result.expaired();
	scene.theMessageScreen.expaired();
	scene.theIndicatorScreen.expaired();
	preparation = false;	
	//set timer
	ticker = new Timer(50, this);
	ticker.start();
	
	this.addKeyListener(this);
	this.addMouseListener(this);
	this.addMouseWheelListener(this);	
	this.addComponentListener(this);
}

public String getAppletInfo() {
	return "Dominion Strike strategic space simulation game. "
		+ " Version 1.1, April 2008."
		+ " Copyrights by Shock (C) 2004-2008"
		+ " Distributed under General Public License";
}

public void start() {
	theMedia.auNoise.loop();	
}

public void stop() {
	theMedia.auNoise.stop();
}


public void drawException(Exception e) {
	Font theFont = new Font("Courier", Font.PLAIN, 13);
	appCanvas.setColor(Color.black);
	appCanvas.fillRect(0, 0, this.getSize().width, this.getSize().height);	
	appCanvas.setColor(Color.red);
	appCanvas.setFont(theFont);
	appCanvas.drawString(e.toString(), 0, 40);
}

public void drawLoadException(Exception e) {
	Font theFont = new Font("Courier", Font.PLAIN, 11);
    appCanvas.setFont(theFont);
	appCanvas.setColor(new Color(0, 0, 0));
    appCanvas.drawString(e.toString(), 10, this.getHeight() / 2 + 40);
    appCanvas.setColor(new Color(200, 200, 200));
    appCanvas.drawString(e.toString(), 12, this.getHeight() / 2 + 42);
}

public void drawCrossHair() {
	switch(theRender.targetCross) {
		case 0:	bufCanvas.setColor(Color.green); break;
		case 1: bufCanvas.setColor(Color.yellow); break;
		case 2: bufCanvas.setColor(Color.red); break;
	}

	bufCanvas.drawLine(camera.ScreenShiftX - 20, camera.ScreenShiftY, camera.ScreenShiftX - 10, camera.ScreenShiftY);
	bufCanvas.drawLine(camera.ScreenShiftX + 10, camera.ScreenShiftY, camera.ScreenShiftX + 20, camera.ScreenShiftY);

	bufCanvas.drawLine(camera.ScreenShiftX, camera.ScreenShiftY - 20, camera.ScreenShiftX, camera.ScreenShiftY -10);
	bufCanvas.drawLine(camera.ScreenShiftX, camera.ScreenShiftY + 10, camera.ScreenShiftX, camera.ScreenShiftY + 20);
}

public void drawFrame() {
	bufCanvas.setColor(Color.green);
	bufCanvas.drawRect(camera.ScreenShiftX - 36, camera.ScreenShiftY - 36,
		71, 71);
}

public void drawPauseInfo() {
	Font F = new Font("Courier", Font.BOLD, 28);
	bufCanvas.setFont(F);
	bufCanvas.setColor(Color.red);
	bufCanvas.drawString("PAUSE", 202, 160);

	Font F2 = new Font("Courier", Font.BOLD, 16);
	bufCanvas.setFont(F2);
	bufCanvas.drawString("Press any key to continue", 160, 240);
}

public void drawEndMissionInfo() {
	scene.cntEnd++;
	if (scene.cntEnd > 20) scene.cntEnd = 0;

	if (scene.cntEnd < 11) {
		int mission = scene.Interpreter.getiVariable("Mission");
		Font F2 = new Font("Courier", Font.BOLD, FINAL_FONT_SIZE);
		bufCanvas.setFont(F2);
		bufCanvas.setColor(Color.yellow);

		int sx = 0;
		String strMsg;
		FontMetrics FM = bufCanvas.getFontMetrics();

		if (mission > 0) {
			strMsg = "Mission is successful";
			sx = (camera.ScreenWidth - FM.stringWidth(strMsg))/2;
			bufCanvas.drawString(strMsg, sx, 335);
		} else if (mission < -1) {
			strMsg = "Mission failed";
			sx = (camera.ScreenWidth - FM.stringWidth(strMsg))/2;
			bufCanvas.drawString(strMsg, sx, 335);
		}

		strMsg = "Press spacebar to fihish the mission";
		sx = (camera.ScreenWidth + 20 - FM.stringWidth(strMsg))/2;
		bufCanvas.drawString(strMsg, sx, 360);
		if (scene.cntEnd == 1) theMedia.auSignal.play();
	}
}

public void drawDebugInfo() {
	//draws some debug information
	//only for debuf purposes

	Font F = new Font("Courier", Font.PLAIN, 12);
	bufCanvas.setFont(F);
	bufCanvas.setColor(Color.yellow);
}

public void paint (Graphics g) {
	if (preparation) {
		theLoader.draw();
		if (eRunTime != null) drawLoadException(eRunTime);
		return;
	}
	if (eRunTime != null) { drawException(eRunTime); return; }

	statusScreen.expaired();
	taskScreen.expaired();
	result.expaired();
	scene.theMessageScreen.expaired();
	scene.theIndicatorScreen.expaired();
	update();
}

public void repaint(){
	paint(appCanvas);
}

public void update() {
	if (scene. result) {
		result.draw();
		return;
	}
	bufCanvas.setColor(Color.black);
	bufCanvas.fillRect(0, 0, iBufferWidth, iBufferHeight);	
	if (theRender.drawStars) theStars.draw();
	theRender.drawRender();
	//drawDebugInfo();
	if (theRender.drawCross && (camera.iRelatedObject == -1 || camera.iRelationType == 0))
		drawCrossHair();
	if (theRender.drawFrame) drawFrame();

	if (scene.pause) drawPauseInfo();
	if (scene.end) drawEndMissionInfo();
	appCanvas.drawImage(imgBuffer, camera.ScreenX, camera.ScreenY, this);
    
	radar.drawRadar();
	statusScreen.drawStatusScreen();
	taskScreen.drawTaskScreen();
	tacticalFriendly.drawTacticalScreen();
	scene.theMessageScreen.draw();
	scene.theIndicatorScreen.draw();
}

public void repaint(int time) {
}

public void repaint(int x, int y, int w, int h) {
}

public void repaint(int time, int x, int y, int w, int h) {
}

private void followSuccess() {
	int res = scene.Interpreter.getiVariable("Mission");
	String strDestination;
	if (res > 0) strDestination = this.getParameter("success");
	else strDestination = this.getParameter("failure");
	if (strDestination == null) return;
	if (strDestination.equals("")) return;

	try {
		StringTokenizer Path = new StringTokenizer(getDocumentBase().getProtocol()
				+ "://"+ getDocumentBase().getHost() + "/" + getDocumentBase().getFile(), "/");
		int cnt = Path.countTokens() - 1;
		String strPath = "";
		for (int i = 0; i<cnt; i++) strPath = strPath + Path.nextToken() + "/";

		System.out.println(strPath + strDestination);
		URL destURL = new URL(strPath + strDestination);
		this.getAppletContext().showDocument(destURL);	
	} catch (MalformedURLException e) {
		System.out.println(e.toString());
	}
}

private void showObjectives() {
		String strObjectives = this.getParameter("objectives");
		if (strObjectives == null) return;
		if (strObjectives.equals("")) return;

		try {
			StringTokenizer Path = new StringTokenizer(getDocumentBase().getProtocol()
				+ "://"+ getDocumentBase().getHost() + "/" + getDocumentBase().getFile(), "/");
			int cnt = Path.countTokens() - 1;
			String strPath = "";
			for (int i = 0; i<cnt; i++) strPath = strPath + Path.nextToken() + "/";

			URL helpURL = new URL(strPath + strObjectives);

			scene.pause = true;
			this.getAppletContext().showDocument(helpURL, "Mission Objectives");	
		} catch (MalformedURLException e) {
		}
}

private void storeTarget(int index) {
	if (camera.iRelatedObject < 0) return;
	if (scene.Objects.Objects[camera.iRelatedObject].taskScreen < 0) return;

	AI.Keys.FType[index] = false;
	AI.Keys.Functional[index] = 
		scene.Objects.Objects[camera.iRelatedObject].taskScreen;
	AI.Keys.Id[index] = scene.Objects.Objects[camera.iRelatedObject].taskScreenID;
	System.out.println("Store " + scene.Objects.Objects[camera.iRelatedObject].taskScreen
		+ " id: " + scene.Objects.Objects[camera.iRelatedObject].taskScreenID);
}

private void storeCamera(int index) {
	if (camera.iRelatedObject < 0) return;

	AI.Keys.FType[index] = true;
	AI.Keys.Functional[index] = camera.iRelatedObject;
	AI.Keys.Id[index] = scene.Objects.Objects[camera.iRelatedObject].id;
}

private void restorePosition(int binding) {
	if (AI.Keys.Functional[binding] < 0) return;
	if (AI.Keys.Id[binding] != scene.Objects.Objects[AI.Keys.Functional[binding]].id) return;

	if (AI.Keys.FType[binding])
		camera.iRelatedObject = AI.Keys.Functional[binding];
	else {
		System.out.println("ReStore " + AI.Keys.Functional[binding]
		+ " id: " + AI.Keys.Id[binding]);

		scene.Objects.Objects[camera.iRelatedObject].taskScreen = AI.Keys.Functional[binding];
		scene.Objects.Objects[camera.iRelatedObject].taskScreenID = AI.Keys.Id[binding];
	}
}

public boolean mouseDown(java.awt.Event event, int x, int y) {
	return true;
}

public boolean mouseMove(java.awt.Event event, int x, int y) {
	return true;
}

public void mouseReleased(MouseEvent e) {
}

public void mousePressed(MouseEvent e) {
}

public void mouseClicked(MouseEvent e) {	
	Area area = areaControl.probe(e.getX(), e.getY());
	if (area != null) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			if (e.isAltDown()) area.areaRightClick(e.getClickCount());
			else area.areaClick(e.getClickCount());
		} else area.areaRightClick(e.getClickCount());
	}
}

public void mouseEntered(MouseEvent e) {
}

public void mouseExited(MouseEvent e) {
}

public void mouseWheelMoved(MouseWheelEvent e) {
	if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
		int rot = e.getWheelRotation();
		
		Area area = areaControl.probe(e.getX(), e.getY());
		if (area != null) {
			area.areaWheel(rot);
		}
	}
}

public void keyPressed(KeyEvent e) {   
	int key = e.getKeyCode();
	
	//check for pause
	if (scene.pause) {
		scene.pause = false;
		return;
	}
	if (scene.end) {
		if (key == 32) {
			//end the mission
			scene.end = false;
			scene.result = true;
			return;
		} else if (key == 27) {
			scene.end = false;
			return;
		}
	}
	if (scene.result) {
		if (key == 10) followSuccess();
		return;
	}

	//check Shift
	if (e.isShiftDown()) {		
		if (key == KeyEvent.VK_Q)
			if (camera.iRelatedObject != -1) {
				camera.iRelationType++;
				if (camera.iRelationType > camera.cntRelationTypes) camera.iRelationType = 0;
			}

		//Functional
		if (key == KeyEvent.VK_F5) this.storeTarget(5);
		if (key == KeyEvent.VK_F6) this.storeTarget(6);
		if (key == KeyEvent.VK_F7) this.storeTarget(7);
		if (key == KeyEvent.VK_F8) this.storeTarget(8);
		if (key == KeyEvent.VK_F9) this.storeTarget(9);
		if (key == KeyEvent.VK_F10) this.storeTarget(10);
		if (key == KeyEvent.VK_F11) this.storeTarget(11);
		if (key == KeyEvent.VK_F12) this.storeTarget(12);

		//Shift + 1-8
		if (camera.iRelatedObject != -1) {
			if (key == KeyEvent.VK_1) camera.iRelationType = 0;
			if (key == KeyEvent.VK_2) camera.iRelationType = 1;
			if (key == KeyEvent.VK_3) camera.iRelationType = 2;
			if (key == KeyEvent.VK_4) camera.iRelationType = 3;
			if (key == KeyEvent.VK_5) camera.iRelationType = 4;
			if (key == KeyEvent.VK_6) camera.iRelationType = 5;
			if (key == KeyEvent.VK_7) camera.iRelationType = 6;
			if (key == KeyEvent.VK_8) camera.iRelationType = 7;
			if (key == KeyEvent.VK_9) camera.iRelationType = 8;
		}

		return;
	}

	//check Ctrl
	if (e.isControlDown()) {
        System.out.println("key is " + key);
		//Ctrl + Z smart camera binding
		if (key == 90) {
			if (camera.smartBind) camera.smartBind = false;
			else {
				camera.smartBind = true;
				camera.smartTime = 20;
			}
		}
		// Ctrl + X free camera binding
		if (key == 88) {
			if (camera.freeBind) camera.freeBind = false;
				else camera.freeBind = true;
		}
        
        // Ctrl + D wireframe visualization switcher
        if (key == 68) {
            theRender.switchVisualization();
            this.clearifyPositions();
        }
        
		// Ctrl + C haircross visualization
		if (key == 67)
			if (theRender.drawCross) theRender.drawCross = false;
				else theRender.drawCross = true;
		// Ctrl + V frame visualization
		if (key == 86) {
			if (theRender.drawFrame) theRender.drawFrame = false;
				else theRender.drawFrame = true;
		}
		// Ctrl + S stars visualization
		if (key == 83)
		  if (theRender.drawStars) theRender.drawStars = false;
			else theRender.drawStars = true;
            
		// Ctrl + P - pause
		if (key == 80) scene.pause = true;
		// Ctrl + T - timing
		if (key == 84) scene.changeTiming();

		//Functional
		if (key == KeyEvent.VK_F5) storeCamera(5);
		if (key == KeyEvent.VK_F6) storeCamera(6);
		if (key == KeyEvent.VK_F7) storeCamera(7);
		if (key == KeyEvent.VK_F8) storeCamera(8);
		if (key == KeyEvent.VK_F9) storeCamera(9);
		if (key == KeyEvent.VK_F10) storeCamera(10);
		if (key == KeyEvent.VK_F11) storeCamera(11);
		if (key == KeyEvent.VK_F12) storeCamera(12);


		return;
	};


	if (key == 32) {
		//space pressed
		if (camera.iRelatedObject != -1) {
			scene.Objects.Objects[camera.iRelatedObject].setTaskScreen(theRender.indexCross);
			theMedia.auPing.play();
		} else if (theRender.indexCross != -1) {
			if (scene.Objects.isPlaced(theRender.indexCross)) {
				camera.iRelatedObject = theRender.indexCross;
				theMedia.auTeleport.play();
			}
		}
		return;
	}
	if (key == 10) {
		//enter pressed
		if (camera.iRelatedObject != -1) {
            if (scene.Objects.Objects[camera.iRelatedObject].taskScreen != -1)
            if (scene.Objects.isPlaced(scene.Objects.Objects[camera.iRelatedObject].taskScreen)) {
                camera.iRelatedObject = scene.Objects.Objects[camera.iRelatedObject].taskScreen;
                theMedia.auTeleport.play();
            }
        } else {
            // free camera
            // create new navigation point
            int model = Models.getIndexByName("Nav Point");
            if (model != -1) {
                SpaceObject theObject = new SpaceObject("", Models.theModels[model],
                    camera.x, camera.y, camera.z);
                scene.Objects.add(theObject);
            }
        }
		return;
	}
	if (key == 27) {
		scene.end = true;
		scene.cntEnd = 0;
		return;
	}

	//functional
	if (key == KeyEvent.VK_F1) {
		scene.pause = true;
		try {
			URL helpURL = new URL(getCodeBase() + "Help.html");
			this.getAppletContext().showDocument(helpURL, "Help");	
		} catch (MalformedURLException exception) {
		}
	}

	if (key == KeyEvent.VK_F2) {
		scene.pause = true;
		try {
			URL helpURL = new URL(getCodeBase() + "KeyRef.html");
			this.getAppletContext().showDocument(helpURL, "Key Reference");	
		} catch (MalformedURLException exception) {
		}
	}

	if (key == KeyEvent.VK_F3) {
		showObjectives();
		return;
	}

	if (key == KeyEvent.VK_F4) {
		if (scene.turnBased) {
			scene.nextParty();
		} else {
			if (scene.statis) scene.statis = false;
				else scene.statis = true;
		}
		scene.theIndicatorScreen.expaired();
		return;
	}

	int binding = -1;
	if (key == KeyEvent.VK_F5) binding = 5;
	if (key == KeyEvent.VK_F6) binding = 6;
	if (key == KeyEvent.VK_F7) binding = 7;
	if (key == KeyEvent.VK_F8) binding = 8;
	if (key == KeyEvent.VK_F9) binding = 9;
	if (key == KeyEvent.VK_F10) binding = 10;
	if (key == KeyEvent.VK_F11) binding = 11;
	if (key == KeyEvent.VK_F12) binding = 12;
	if (binding != -1) restorePosition(binding);

	//message screen
	if (key == KeyEvent.VK_END)  scene.theMessageScreen.end();
	if (key == KeyEvent.VK_HOME) scene.theMessageScreen.home();
	if (key == KeyEvent.VK_PAGE_DOWN) scene.theMessageScreen.pageDown();
	if (key == KeyEvent.VK_PAGE_UP) scene.theMessageScreen.pageUp();

	//task control
	if (key == '[') taskScreen.prev();
	if (key == ']') taskScreen.next();
	if (key == KeyEvent.VK_P) taskScreen.nav();
	if (key == KeyEvent.VK_O) taskScreen.foe();
	if (key == KeyEvent.VK_I) taskScreen.friend();
	if (key == KeyEvent.VK_U) taskScreen.setAttacker();
	if (key == KeyEvent.VK_Y) taskScreen.setTargetAttacker();
	if (key == KeyEvent.VK_SEMICOLON) taskScreen.setPrimary();
	if (key == KeyEvent.VK_QUOTE) taskScreen.setSecondary();
	if (key == KeyEvent.VK_K) taskScreen.setDefend();
	if (key == KeyEvent.VK_L) taskScreen.setAttack();
	if (key == KeyEvent.VK_J) taskScreen.resetTarget();
	if (key == KeyEvent.VK_N) taskScreen.setPrevPlan();
	if (key == KeyEvent.VK_M) taskScreen.setNextPlan();

	if (key == '1') taskScreen.setAgression(1);
	if (key == '2') taskScreen.setAgression(2);
	if (key == '3') taskScreen.setAgression(3);
	if (key == '4') taskScreen.setAgression(4);
	if (key == KeyEvent.VK_Q) 
		if (camera.iRelatedObject != -1) {
			scene.Objects.Objects[camera.iRelatedObject].HumanControlled = false;
			camera.leaveObject();
			theMedia.auTeleport.play();
		}
	if (key == KeyEvent.VK_W) {
		int lindex = camera.iRelatedObject;
		int nindex = scene.Objects.getPrevIndex(camera.iRelatedObject);
		if (nindex != -1 && nindex != lindex) {
			if (camera.iRelatedObject != -1)
				scene.Objects.Objects[camera.iRelatedObject].HumanControlled = false;
			camera.iRelatedObject = nindex;
			theMedia.auTeleport.play();
		}
	}
	if (key == KeyEvent.VK_E) {
		int lindex = camera.iRelatedObject;
		int nindex = scene.Objects.getNextIndex(camera.iRelatedObject);
		if (nindex != -1 && nindex != lindex) {
			if (camera.iRelatedObject != -1)
				scene.Objects.Objects[camera.iRelatedObject].HumanControlled = false;
			camera.iRelatedObject = nindex;
			theMedia.auTeleport.play();
		}
	}
	if (key == KeyEvent.VK_T && camera.iRelatedObject != -1) {
		scene.Objects.Objects[camera.iRelatedObject].takeControl();
	}
	if (key == KeyEvent.VK_G  && camera.iRelatedObject != -1) {
		scene.Objects.Objects[camera.iRelatedObject].switchToNextLauncher();
	}

	//ship's control
	if (key == KeyEvent.VK_A) AI.Keys.SpeedUp = true;
	if (key == KeyEvent.VK_Z) AI.Keys.SpeedDown = true;
	if (key == KeyEvent.VK_S) AI.Keys.ZeroSpeed = true;
	if (key == KeyEvent.VK_X) AI.Keys.CruiseSpeed = true;
	if (key == KeyEvent.VK_D) AI.Keys.MatchSpeed = true;
	if (key == KeyEvent.VK_C) AI.Keys.Fire = true;
	if (key == KeyEvent.VK_V) AI.Keys.MissileLauncher = true;
	if (key == KeyEvent.VK_B) AI.Keys.MissileLauncherCam = true;
	if (key == KeyEvent.VK_F) scene.Objects.Objects[camera.iRelatedObject].adjustedSpeed =
		scene.Objects.Objects[camera.iRelatedObject].currentSpeed;
	if (key == KeyEvent.VK_UP) {
		AI.Keys.keyUp = true;
	}
	if (key == KeyEvent.VK_DOWN) {
		AI.Keys.keyDown = true;
	}
	if (key == KeyEvent.VK_RIGHT) {
		AI.Keys.keyRight = true;
	}
	if (key == KeyEvent.VK_LEFT) {
		AI.Keys.keyLeft = true;		
	}
	if (key == ',') AI.Keys.rollLeft = true;
	if (key == '.') AI.Keys.rollRight = true;

	if (key == '=') radar.increaseZoom();
	if (key == '-') radar.decreaseZoom();
    
	return;
}

public void keyReleased(KeyEvent e) {
	int key = e.getKeyCode();
	
	if (key == KeyEvent.VK_A) AI.Keys.SpeedUp = false;
	if (key == KeyEvent.VK_Z) AI.Keys.SpeedDown = false;
	if (key == KeyEvent.VK_S) AI.Keys.ZeroSpeed = false;
	if (key == KeyEvent.VK_X) AI.Keys.CruiseSpeed = false;
	if (key == KeyEvent.VK_D) AI.Keys.MatchSpeed = false;
	if (key == KeyEvent.VK_C) AI.Keys.Fire = false;
	if (key == KeyEvent.VK_V) AI.Keys.MissileLauncher = false;
	if (key == KeyEvent.VK_B) AI.Keys.MissileLauncherCam = false;
	if (key == KeyEvent.VK_UP) AI.Keys.keyUp = false;
	if (key == KeyEvent.VK_DOWN) AI.Keys.keyDown = false;
	if (key == KeyEvent.VK_RIGHT) AI.Keys.keyRight = false;
	if (key == KeyEvent.VK_LEFT) AI.Keys.keyLeft = false;
	if (key == ',') AI.Keys.rollLeft = false;
	if (key == '.') AI.Keys.rollRight = false;
}

public void keyTyped(KeyEvent e) {}

public void selectTarget(int i) {	
	if (camera.iRelatedObject != -1) {
		scene.Objects.Objects[camera.iRelatedObject].setTaskScreen(i);
		theMedia.auPing.play();
	} else {
		if (scene.Objects.isPlaced(i)) {
			camera.iRelatedObject = i;
			theMedia.auTeleport.play();
		}
	}
	return;	
}

private void mainFlow() {
	for (int i = 0; i<scene.timing; i++) {
		scene.Time++;
		scene.secTicker++;
		if (scene.secTicker == 20) {
			scene.secTicker = 0;
			scene.secTime++;
			if (scene.secTime == 60) {
				scene.secTime = 0;
				scene.minTime++;
			}
			//turn flow
			if (scene.turnBased && scene.actionTime != 0) {
				scene.actionCounter++;
				if (scene.actionCounter >= scene.actionTime)
					scene.nextParty();
			}
		}
		AI.takeControl();
	}
}

private void staticFlow() {
	if (scene.turnBased && scene.turnTime != 0) {
		scene.staticTicker++;
		if (scene.staticTicker == 20) {
			scene.staticTicker = 0;
			scene.turnCounter++;
			if (scene.turnCounter >= scene.turnTime)
				scene.nextParty();
		}
	}
}

public void actionPerformed(ActionEvent ev) {
	if (ev.getSource() == ticker) {
		if (!scene.pause && !scene.result) {
			if (!scene.statis) {
				mainFlow();
			} else {
				staticFlow();
				AI.takeStatis();
			}

			update();
		} else if (scene.result) {
			result.detail();
			update(); 
		} else {
			update();
		}
	}
}

public void clearifyPositions() {
	radar.clearifyPosition(this.getWidth(), this.getHeight());
	statusScreen.clearifyPosition(this.getWidth(), this.getHeight());
	taskScreen.clearifyPosition(this.getWidth(), this.getHeight());
	tacticalFriendly.clearifyPosition(this.getWidth(), this.getHeight());
	scene.theIndicatorScreen.clearifyPosition(this.getWidth(), this.getHeight());
	scene.theMessageScreen.clearifyPosition(this.getWidth(), this.getHeight());
	camera.clearifyPosition(this.getWidth(), this.getHeight());
	
	// recreate rendering plate
	imgBuffer = createImage(camera.ScreenWidth, camera.ScreenHeight);
	iBufferWidth = getSize().width;
	iBufferHeight = getSize().height;

	bufCanvas = imgBuffer.getGraphics();
	
	this.theStars.updateCanvas(bufCanvas);
	this.Models.updateCanvas(bufCanvas);
	this.theRender.updateCanvas(bufCanvas);
}

public void componentHidden(ComponentEvent e) {}

public void componentMoved(ComponentEvent e) {}

public void componentResized(ComponentEvent e) {
	this.clearifyPositions();
}

public void componentShown(ComponentEvent e) {}

}
