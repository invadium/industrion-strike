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

import math.*;
import parser.*;
import primitives.*;
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
	public Scene theScene;
	public Camera theCamera;
	Stars theStars;
	public Radar theRadar;
	public StatusScreen theStatusScreen;
	public TaskScreen theTaskScreen;
	public TacticalScreen tacticalFriendly;
	ResultScreen theResult;
	public Render theRender;
	public Helper theHelper;
	

public void init() {
	theLoader = new Loader(this, this.getGraphics());
	theMedia = new Media(this);
	flowThread = new Thread(this);
	flowThread.start();
}

public void run() {
	theCamera = new Camera(this, -1, 0.0, 0.0, -6000.0, 0, 0, 0);
	//create screen buffer
	imgBuffer = createImage(theCamera.ScreenWidth, theCamera.ScreenHeight);
	iBufferWidth = getSize().width;
	iBufferHeight = getSize().height;

	bufCanvas = imgBuffer.getGraphics();
	appCanvas = this.getGraphics();
	this.setBackground(new Color(80, 80, 80));
	//this.setBackground(Color.black);

	try {
		theHelper = new Helper(appCanvas);
        theStars = new Stars(this, bufCanvas, theCamera);
		theLoader.increase(20); repaint();
		theLoader.increase(10); repaint();
		Models = new scene.ModelsCollector(this, bufCanvas, "data/models/" + this.getParameter("models"));
		theLoader.increase(20); repaint();
		String strMissionName = "missions/" + this.getParameter("mission");
		theScene = new Scene(this, Models, theCamera, theStars, theMedia,
								strMissionName);
		theLoader.increase(40); repaint();
		theRadar = new Radar(this, appCanvas);
		theStatusScreen = new StatusScreen(this, appCanvas, theScene, theCamera);
		theTaskScreen = new TaskScreen(this, appCanvas, theScene, theCamera, theMedia);
		tacticalFriendly = new TacticalScreen(this, appCanvas, theScene, theCamera);
		theResult = new ResultScreen(this, appCanvas, theScene, theMedia);
		theRender = new Render(this, bufCanvas, theMedia, theScene, theCamera, theRadar);
		AI = new Controller(theScene, theCamera, Models, theMedia, theStars, theRender);
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
	int iProc = theScene.Interpreter.theCommands.findProc("CONSTRUCTOR");
	if (iProc != -1) theScene.Interpreter.runProc(iProc);
	
	this.clearifyPositions();
    theStars.create();
	
	theStatusScreen.expaired();
	theTaskScreen.expaired();
	theResult.expaired();
	theScene.theMessageScreen.expaired();
	theScene.theIndicatorScreen.expaired();
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

	bufCanvas.drawLine(theCamera.ScreenShiftX - 20, theCamera.ScreenShiftY, theCamera.ScreenShiftX - 10, theCamera.ScreenShiftY);
	bufCanvas.drawLine(theCamera.ScreenShiftX + 10, theCamera.ScreenShiftY, theCamera.ScreenShiftX + 20, theCamera.ScreenShiftY);

	bufCanvas.drawLine(theCamera.ScreenShiftX, theCamera.ScreenShiftY - 20, theCamera.ScreenShiftX, theCamera.ScreenShiftY -10);
	bufCanvas.drawLine(theCamera.ScreenShiftX, theCamera.ScreenShiftY + 10, theCamera.ScreenShiftX, theCamera.ScreenShiftY + 20);
}

public void drawFrame() {
	bufCanvas.setColor(Color.green);
	bufCanvas.drawRect(theCamera.ScreenShiftX - 36, theCamera.ScreenShiftY - 36,
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
	theScene.cntEnd++;
	if (theScene.cntEnd > 20) theScene.cntEnd = 0;

	if (theScene.cntEnd < 11) {
		int mission = theScene.Interpreter.getiVariable("Mission");
		Font F2 = new Font("Courier", Font.BOLD, FINAL_FONT_SIZE);
		bufCanvas.setFont(F2);
		bufCanvas.setColor(Color.yellow);

		int sx = 0;
		String strMsg;
		FontMetrics FM = bufCanvas.getFontMetrics();

		if (mission > 0) {
			strMsg = "Mission is successful";
			sx = (theCamera.ScreenWidth - FM.stringWidth(strMsg))/2;
			bufCanvas.drawString(strMsg, sx, 335);
		} else if (mission < -1) {
			strMsg = "Mission failed";
			sx = (theCamera.ScreenWidth - FM.stringWidth(strMsg))/2;
			bufCanvas.drawString(strMsg, sx, 335);
		}

		strMsg = "Press spacebar to fihish the mission";
		sx = (theCamera.ScreenWidth + 20 - FM.stringWidth(strMsg))/2;
		bufCanvas.drawString(strMsg, sx, 360);
		if (theScene.cntEnd == 1) theMedia.auSignal.play();
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

	theStatusScreen.expaired();
	theTaskScreen.expaired();
	theResult.expaired();
	theScene.theMessageScreen.expaired();
	theScene.theIndicatorScreen.expaired();
	update();
}

public void repaint(){
	paint(appCanvas);
}

public void update() {
	if (theScene. result) {
		theResult.draw();
		return;
	}
	bufCanvas.setColor(Color.black);
	bufCanvas.fillRect(0, 0, iBufferWidth, iBufferHeight);	
	if (theRender.drawStars) theStars.draw();
	theRender.drawRender();
	//drawDebugInfo();
	if (theRender.drawCross && (theCamera.iRelatedObject == -1 || theCamera.iRelationType == 0))
		drawCrossHair();
	if (theRender.drawFrame) drawFrame();

	if (theScene.pause) drawPauseInfo();
	if (theScene.end) drawEndMissionInfo();
	appCanvas.drawImage(imgBuffer, theCamera.ScreenX, theCamera.ScreenY, this);
    
	theRadar.drawRadar();
	theStatusScreen.drawStatusScreen();
	theTaskScreen.drawTaskScreen();
	tacticalFriendly.drawTacticalScreen();
	theScene.theMessageScreen.draw();
	theScene.theIndicatorScreen.draw();
}

public void repaint(int time) {
}

public void repaint(int x, int y, int w, int h) {
}

public void repaint(int time, int x, int y, int w, int h) {
}

private void followSuccess() {
	int res = theScene.Interpreter.getiVariable("Mission");
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

			theScene.pause = true;
			this.getAppletContext().showDocument(helpURL, "Mission Objectives");	
		} catch (MalformedURLException e) {
		}
}

private void storeTarget(int index) {
	if (theCamera.iRelatedObject < 0) return;
	if (theScene.Objects.Objects[theCamera.iRelatedObject].taskScreen < 0) return;

	AI.Keys.FType[index] = false;
	AI.Keys.Functional[index] = 
		theScene.Objects.Objects[theCamera.iRelatedObject].taskScreen;
	AI.Keys.Id[index] = theScene.Objects.Objects[theCamera.iRelatedObject].taskScreenID;
	System.out.println("Store " + theScene.Objects.Objects[theCamera.iRelatedObject].taskScreen
		+ " id: " + theScene.Objects.Objects[theCamera.iRelatedObject].taskScreenID);
}

private void storeCamera(int index) {
	if (theCamera.iRelatedObject < 0) return;

	AI.Keys.FType[index] = true;
	AI.Keys.Functional[index] = theCamera.iRelatedObject;
	AI.Keys.Id[index] = theScene.Objects.Objects[theCamera.iRelatedObject].id;
}

private void restorePosition(int binding) {
	if (AI.Keys.Functional[binding] < 0) return;
	if (AI.Keys.Id[binding] != theScene.Objects.Objects[AI.Keys.Functional[binding]].id) return;

	if (AI.Keys.FType[binding])
		theCamera.iRelatedObject = AI.Keys.Functional[binding];
	else {
		System.out.println("ReStore " + AI.Keys.Functional[binding]
		+ " id: " + AI.Keys.Id[binding]);

		theScene.Objects.Objects[theCamera.iRelatedObject].taskScreen = AI.Keys.Functional[binding];
		theScene.Objects.Objects[theCamera.iRelatedObject].taskScreenID = AI.Keys.Id[binding];
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
	if (theScene.pause) {
		theScene.pause = false;
		return;
	}
	if (theScene.end) {
		if (key == 32) {
			//end the mission
			theScene.end = false;
			theScene.result = true;
			return;
		} else if (key == 27) {
			theScene.end = false;
			return;
		}
	}
	if (theScene.result) {
		if (key == 10) followSuccess();
		return;
	}

	//check Shift
	if (e.isShiftDown()) {		
		if (key == KeyEvent.VK_Q)
			if (theCamera.iRelatedObject != -1) {
				theCamera.iRelationType++;
				if (theCamera.iRelationType > theCamera.cntRelationTypes) theCamera.iRelationType = 0;
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
		if (theCamera.iRelatedObject != -1) {
			if (key == KeyEvent.VK_1) theCamera.iRelationType = 0;
			if (key == KeyEvent.VK_2) theCamera.iRelationType = 1;
			if (key == KeyEvent.VK_3) theCamera.iRelationType = 2;
			if (key == KeyEvent.VK_4) theCamera.iRelationType = 3;
			if (key == KeyEvent.VK_5) theCamera.iRelationType = 4;
			if (key == KeyEvent.VK_6) theCamera.iRelationType = 5;
			if (key == KeyEvent.VK_7) theCamera.iRelationType = 6;
			if (key == KeyEvent.VK_8) theCamera.iRelationType = 7;
			if (key == KeyEvent.VK_9) theCamera.iRelationType = 8;
		}

		return;
	}

	//check Ctrl
	if (e.isControlDown()) {
        System.out.println("key is " + key);
		//Ctrl + Z smart camera binding
		if (key == 90) {
			if (theCamera.smartBind) theCamera.smartBind = false;
			else {
				theCamera.smartBind = true;
				theCamera.smartTime = 20;
			}
		}
		// Ctrl + X free camera binding
		if (key == 88) {
			if (theCamera.freeBind) theCamera.freeBind = false;
				else theCamera.freeBind = true;
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
		if (key == 80) theScene.pause = true;
		// Ctrl + T - timing
		if (key == 84) theScene.changeTiming();

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
		if (theCamera.iRelatedObject != -1) {
			theScene.Objects.Objects[theCamera.iRelatedObject].setTaskScreen(theRender.indexCross);
			theMedia.auPing.play();
		} else if (theRender.indexCross != -1) {
			if (theScene.Objects.isPlaced(theRender.indexCross)) {
				theCamera.iRelatedObject = theRender.indexCross;
				theMedia.auTeleport.play();
			}
		}
		return;
	}
	if (key == 10) {
		//enter pressed
		if (theCamera.iRelatedObject != -1) {
            if (theScene.Objects.Objects[theCamera.iRelatedObject].taskScreen != -1)
            if (theScene.Objects.isPlaced(theScene.Objects.Objects[theCamera.iRelatedObject].taskScreen)) {
                theCamera.iRelatedObject = theScene.Objects.Objects[theCamera.iRelatedObject].taskScreen;
                theMedia.auTeleport.play();
            }
        } else {
            // free camera
            // create new navigation point
            int model = Models.getIndexByName("Nav Point");
            if (model != -1) {
                SpaceObject theObject = new SpaceObject("", Models.theModels[model],
                    theCamera.x, theCamera.y, theCamera.z);
                theScene.Objects.add(theObject);
            }
        }
		return;
	}
	if (key == 27) {
		theScene.end = true;
		theScene.cntEnd = 0;
		return;
	}

	//functional
	if (key == KeyEvent.VK_F1) {
		theScene.pause = true;
		try {
			URL helpURL = new URL(getCodeBase() + "Help.html");
			this.getAppletContext().showDocument(helpURL, "Help");	
		} catch (MalformedURLException exception) {
		}
	}

	if (key == KeyEvent.VK_F2) {
		theScene.pause = true;
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
		if (theScene.turnBased) {
			theScene.nextParty();
		} else {
			if (theScene.statis) theScene.statis = false;
				else theScene.statis = true;
		}
		theScene.theIndicatorScreen.expaired();
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
	if (key == KeyEvent.VK_END)  theScene.theMessageScreen.end();
	if (key == KeyEvent.VK_HOME) theScene.theMessageScreen.home();
	if (key == KeyEvent.VK_PAGE_DOWN) theScene.theMessageScreen.pageDown();
	if (key == KeyEvent.VK_PAGE_UP) theScene.theMessageScreen.pageUp();

	//task control
	if (key == '[') theTaskScreen.prev();
	if (key == ']') theTaskScreen.next();
	if (key == KeyEvent.VK_P) theTaskScreen.nav();
	if (key == KeyEvent.VK_O) theTaskScreen.foe();
	if (key == KeyEvent.VK_I) theTaskScreen.friend();
	if (key == KeyEvent.VK_U) theTaskScreen.setAttacker();
	if (key == KeyEvent.VK_Y) theTaskScreen.setTargetAttacker();
	if (key == KeyEvent.VK_SEMICOLON) theTaskScreen.setPrimary();
	if (key == KeyEvent.VK_QUOTE) theTaskScreen.setSecondary();
	if (key == KeyEvent.VK_K) theTaskScreen.setDefend();
	if (key == KeyEvent.VK_L) theTaskScreen.setAttack();
	if (key == KeyEvent.VK_J) theTaskScreen.resetTarget();
	if (key == KeyEvent.VK_N) theTaskScreen.setPrevPlan();
	if (key == KeyEvent.VK_M) theTaskScreen.setNextPlan();

	if (key == '1') theTaskScreen.setAgression(1);
	if (key == '2') theTaskScreen.setAgression(2);
	if (key == '3') theTaskScreen.setAgression(3);
	if (key == '4') theTaskScreen.setAgression(4);
	if (key == KeyEvent.VK_Q) 
		if (theCamera.iRelatedObject != -1) {
			theScene.Objects.Objects[theCamera.iRelatedObject].HumanControlled = false;
			theCamera.leaveObject();
			theMedia.auTeleport.play();
		}
	if (key == KeyEvent.VK_W) {
		int lindex = theCamera.iRelatedObject;
		int nindex = theScene.Objects.getPrevIndex(theCamera.iRelatedObject);
		if (nindex != -1 && nindex != lindex) {
			if (theCamera.iRelatedObject != -1)
				theScene.Objects.Objects[theCamera.iRelatedObject].HumanControlled = false;
			theCamera.iRelatedObject = nindex;
			theMedia.auTeleport.play();
		}
	}
	if (key == KeyEvent.VK_E) {
		int lindex = theCamera.iRelatedObject;
		int nindex = theScene.Objects.getNextIndex(theCamera.iRelatedObject);
		if (nindex != -1 && nindex != lindex) {
			if (theCamera.iRelatedObject != -1)
				theScene.Objects.Objects[theCamera.iRelatedObject].HumanControlled = false;
			theCamera.iRelatedObject = nindex;
			theMedia.auTeleport.play();
		}
	}
	if (key == KeyEvent.VK_T && theCamera.iRelatedObject != -1) {
		theScene.Objects.Objects[theCamera.iRelatedObject].takeControl();
	}
	if (key == KeyEvent.VK_G  && theCamera.iRelatedObject != -1) {
		theScene.Objects.Objects[theCamera.iRelatedObject].switchToNextLauncher();
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
	if (key == KeyEvent.VK_F) theScene.Objects.Objects[theCamera.iRelatedObject].adjustedSpeed =
		theScene.Objects.Objects[theCamera.iRelatedObject].currentSpeed;
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

	if (key == '=') theRadar.increaseZoom();
	if (key == '-') theRadar.decreaseZoom();
    
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
	if (theCamera.iRelatedObject != -1) {
		theScene.Objects.Objects[theCamera.iRelatedObject].setTaskScreen(i);
		theMedia.auPing.play();
	} else {
		if (theScene.Objects.isPlaced(i)) {
			theCamera.iRelatedObject = i;
			theMedia.auTeleport.play();
		}
	}
	return;	
}

private void mainFlow() {
	for (int i = 0; i<theScene.timing; i++) {
		theScene.Time++;
		theScene.secTicker++;
		if (theScene.secTicker == 20) {
			theScene.secTicker = 0;
			theScene.secTime++;
			if (theScene.secTime == 60) {
				theScene.secTime = 0;
				theScene.minTime++;
			}
			//turn flow
			if (theScene.turnBased && theScene.actionTime != 0) {
				theScene.actionCounter++;
				if (theScene.actionCounter >= theScene.actionTime)
					theScene.nextParty();
			}
		}
		AI.takeControl();
	}
}

private void staticFlow() {
	if (theScene.turnBased && theScene.turnTime != 0) {
		theScene.staticTicker++;
		if (theScene.staticTicker == 20) {
			theScene.staticTicker = 0;
			theScene.turnCounter++;
			if (theScene.turnCounter >= theScene.turnTime)
				theScene.nextParty();
		}
	}
}

public void actionPerformed(ActionEvent ev) {
	if (ev.getSource() == ticker) {
		if (!theScene.pause && !theScene.result) {
			if (!theScene.statis) {
				mainFlow();
			} else {
				staticFlow();
				AI.takeStatis();
			}

			update();
		} else if (theScene.result) {
			theResult.detail();
			update(); 
		} else {
			update();
		}
	}
}

public void clearifyPositions() {
	theRadar.clearifyPosition(this.getWidth(), this.getHeight());
	theStatusScreen.clearifyPosition(this.getWidth(), this.getHeight());
	theTaskScreen.clearifyPosition(this.getWidth(), this.getHeight());
	tacticalFriendly.clearifyPosition(this.getWidth(), this.getHeight());
	theScene.theIndicatorScreen.clearifyPosition(this.getWidth(), this.getHeight());
	theScene.theMessageScreen.clearifyPosition(this.getWidth(), this.getHeight());
	theCamera.clearifyPosition(this.getWidth(), this.getHeight());
	
	// recreate rendering plate
	imgBuffer = createImage(theCamera.ScreenWidth, theCamera.ScreenHeight);
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
