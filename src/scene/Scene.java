package scene;

import java.awt.*;
import java.applet.*;
import render.MessageScreen;
import render.IndicatorScreen;
import scene.SpaceObject;
import scene.Camera;
import control.PlanPosition;
import control.FlightPlan;
import control.Task;
import engine.Strike;
import mission.*;
import render.Stars;
import media.Media;
import parser.*;

public class Scene {
	public boolean statis = false;
	public boolean pause = false;	
	public boolean end = false;
	public boolean turnBased = false;
	public int cntEnd = 0;
	public boolean result = false;
	public int timing = 1;
	public int Time = 0;
	public int secTime = 0;
	public int minTime = 0;
	public int secTicker = 0;
	public int staticTicker = 0;
	public int turnTime = 0;
	public int actionTime = 0;
	public int turnSides = 0;
	public int actionCounter = 0;
	public int turnCounter = 0;
	public int sides = 0;
	public SOCollector Objects;
	public FlightPlan plan;
	public RSun sun;
	public MissionInterpreter Interpreter;
	public MCmdCollector MissionCommands;
	private Strike applet;
	private ModelsCollector Models;
	private Media media;
	private Camera camera;
	private Stars stars;
	private Parser prs;
	public MessageScreen messageScreen;
	public IndicatorScreen indicatorScreen;

	public Scene (Strike applet, ModelsCollector Models,
			Camera camera, Stars stars,
			Media media, String strMissionFileName)
			throws SceneException, ParserException, InterpreterException {
		this.applet = applet;
		this.Models = Models;
		this.media = media;
		this.camera = camera;
		this.stars = stars;
		setEnvironment();
		this.Interpreter = new MissionInterpreter(camera, Models, this, Objects);
		this.MissionCommands = new MCmdCollector();
		this.messageScreen = new MessageScreen(applet, applet.getGraphics(), media);
		this.indicatorScreen = new IndicatorScreen(applet, applet.getGraphics(), this, camera, media);

		loadScene(strMissionFileName);
		createLights();
		
		checkInitialConditions();
	}
	
	private void checkInitialConditions() {
		this.sides = this.Interpreter.getiVariable("Sides");
		int Multiplayer = this.Interpreter.getiVariable("TurnBased");
		if (Multiplayer == 1) {
			this.turnBased = true;
			this.actionTime = this.Interpreter.getiVariable("ActionTime");
			this.turnTime = this.Interpreter.getiVariable("TurnTime");
			this.turnSides = this.Interpreter.getiVariable("TurnSides");
			
			this.statis = true;
			this.camera.Side = 1;
			this.messageScreen.push("Turn based on");
			this.showSideControl();
		}
	}

	private void setEnvironment() {
		plan = new FlightPlan(this);
		Objects = new SOCollector(camera);
		SpaceObject.Objects = Objects;
		SpaceObject.media = media;
		SpaceObject.stars = stars;
		SpaceObject.camera = camera;
		SpaceObject.scene = this;
	}

	private void loadScene(String strMissionFileName)
			throws SceneException, ParserException, InterpreterException {
		prs = new Parser(applet);		
		MissionCommand cmd;
		Token tok;

		System.out.println("loading mission file: " + strMissionFileName);
		try {
		if (prs.openStream(strMissionFileName)) {
			tok = prs.getToken(); isMatch(tok, Token.STRING, "MISSION");
			System.out.println("loading mission '" + prs.strFileName + "'...");
			
			while (tok!=null) {
				tok = prs.getToken(); if (tok == null) break;
				if (tok.type == Token.USD) {
					cmdSetVariable();
					continue;
				}
				if (tok.type == Token.NOT) {
					cmdDelVariable();
					continue;
				}
				isMatch(tok, Token.STRING);
				if (tok.strValue.equals("CREATE")) cmdCreate();
				else if (tok.strValue.equals("CALL")) cmdCallProc();
				else if (tok.strValue.equals("RET")) cmdRetProc();
				else if (tok.strValue.equals("LABEL")) cmdLabel();
				else if (tok.strValue.equals("GOTO")) cmdGoTo();
				else if (tok.strValue.equals("BIND_CAMERA_TO")) cmdBindCameraTo();
				else if (tok.strValue.equals("SMART_CAMERA_ON")) cmdSmartCameraOn();
				else if (tok.strValue.equals("SMART_CAMERA_OFF")) cmdSmartCameraOff();
				else if (tok.strValue.equals("YAW")) cmdYaw();
				else if (tok.strValue.equals("PITCH")) cmdPitch();
				else if (tok.strValue.equals("ROLL")) cmdRoll();
				else if (tok.strValue.equals("INCREASE_SPEED")) cmdIncreaseSpeed();
				else if (tok.strValue.equals("DECREASE_SPEED")) cmdDecreaseSpeed();
				else if (tok.strValue.equals("ADJUST_SPEED")) cmdAdjustSpeed();
				else if (tok.strValue.equals("SET")) cmdSet();
				else if (tok.strValue.equals("PLAN")) cmdPlan();
				else if (tok.strValue.equals("PROC")) cmdProc();
				else if (tok.strValue.equals("EXIT")) cmdExitProc();
				else if (tok.strValue.equals("END")) cmdEndProc();
				else if (tok.strValue.equals("IF")) cmdIf();
				else if (tok.strValue.equals("MESSAGE")) cmdMsg();
				else if (tok.strValue.equals("SHOW_FINISH")) cmdShowFinish();
			}
		} else throw new SceneException("can't open file " + strMissionFileName, "", 0, 0);
		} catch (InterpreterException e) {
			System.out.println("exception at " + prs.iLine + ":" + prs.iLineChar);
			throw e;
		}
	}

	private void cmdCreate()
			throws SceneException, ParserException, InterpreterException {
		Token tok;
		MissionCommand cmd;

		//push object name
		pushExpression();
		//push objects class
		tok = prs.getToken(); isMatch(tok, Token.STRING, "CLASS");		
		pushExpression();

		//push coordinates
		tok = prs.getToken(); isMatch(tok, Token.STRING, "AT");
		pushTExpression();
		pushTExpression();
		pushTExpression();
	
		tok = prs.getToken(); isMatch(tok, Token.SEMI);
		cmd = new MissionCommand(MissionCommand.cmdCreate);
		Interpreter.send(cmd);
	}

	private void cmdCallProc()
			throws SceneException, ParserException, InterpreterException {
		Token tok = prs.getToken(); isMatch(tok, Token.STRING);

		MissionCommand cmd = new MissionCommand(MissionCommand.cmdCallProc, tok.strValue);
		Interpreter.send(cmd);
		tok = prs.getToken(); isMatch(tok, Token.SEMI);
	}

	private void cmdRetProc()
			throws SceneException, ParserException, InterpreterException {
		MissionCommand cmd = new MissionCommand(MissionCommand.cmdRetProc);
		Interpreter.send(cmd);
		Token tok = prs.getToken(); isMatch(tok, Token.SEMI);
	}
	
	private void cmdLabel()
		throws SceneException, ParserException, InterpreterException {
		Token tok = prs.getToken(); isMatch(tok, Token.STRING);
		MissionCommand cmd = new MissionCommand(MissionCommand.cmdLabel, tok.strValue);
		Interpreter.send(cmd);
		tok = prs.getToken(); isMatch(tok, Token.SEMI);
	}
	
	private void cmdGoTo()
		throws SceneException, ParserException, InterpreterException {
		Token tok = prs.getToken(); isMatch(tok, Token.STRING);
		MissionCommand cmd = new MissionCommand(MissionCommand.cmdGoTo, tok.strValue);
		Interpreter.send(cmd);
		tok = prs.getToken(); isMatch(tok, Token.SEMI);
	}

	private void cmdBindCameraTo()
			throws SceneException, ParserException, InterpreterException {
		Token tok;
		MissionCommand cmd;

		pushExpression();
		tok = prs.getToken();
        
        if (tok.type != Token.NUMBER) {
            isMatch(tok, Token.SEMI);
            cmd = new MissionCommand(MissionCommand.cmdBindCameraTo);
            Interpreter.send(cmd);
		} else {
            prs.retToken(tok);
            pushExpression();
            tok = prs.getToken();
            isMatch(tok, Token.SEMI);
            isMatch(tok, Token.SEMI);
            cmd = new MissionCommand(MissionCommand.cmdBindViewCameraTo);
            Interpreter.send(cmd);
        }
		
	}
	
	private void cmdMsg()
		throws SceneException, ParserException, InterpreterException {
		Token tok;
		MissionCommand cmd;		
		boolean beep = false;
		boolean cl = false;

		pushExpression();
		tok = prs.getToken();
		if (tok.type != Token.STRING) isMatch(tok, Token.SEMI);
			else {
				if (tok.strValue.equals("BEEP")) {
					beep = true;
					tok = prs.getToken();
				}
				if (tok.type == Token.STRING) {
					//push RGB
					isMatch(tok, Token.STRING, "COLOR");
					cl = true;
					pushExpression();
					pushExpression();
					pushExpression();
					tok = prs.getToken(); isMatch(tok, Token.SEMI);
				} else isMatch(tok, Token.SEMI);
			}

		if (beep && cl) cmd = new MissionCommand(MissionCommand.cmdSCMsg);
		else if (beep) cmd = new MissionCommand(MissionCommand.cmdSMsg);
		else if (cl) cmd = new MissionCommand(MissionCommand.cmdCMsg);
		else cmd = new MissionCommand(MissionCommand.cmdMsg);
		Interpreter.send(cmd);
	}

	private void cmdShowFinish()
		throws SceneException, ParserException, InterpreterException {
		Token tok = prs.getToken(); isMatch(tok, Token.SEMI);
		MissionCommand cmd = new MissionCommand(MissionCommand.cmdShowFinish);
		Interpreter.send(cmd);
	}
	
	private void cmdSmartCameraOn() 
		throws SceneException, ParserException, InterpreterException {
		Token tok = prs.getToken(); isMatch(tok, Token.SEMI);
		MissionCommand cmd = new MissionCommand(MissionCommand.cmdSmartCameraOn);
		Interpreter.send(cmd);
	}
	
	private void cmdSmartCameraOff() 
		throws SceneException, ParserException, InterpreterException {
		Token tok = prs.getToken(); isMatch(tok, Token.SEMI);
		MissionCommand cmd = new MissionCommand(MissionCommand.cmdSmartCameraOff);
		Interpreter.send(cmd);
	}

	private void cmdIf()
			throws SceneException, ParserException, InterpreterException {
		Token tok;
		MissionCommand cmd;

		tok = prs.getToken(); isMatch(tok, Token.ORD);
		pushExpression();
		tok = prs.getToken(); isMatch(tok, Token.CRD);
	
		cmd = new MissionCommand(MissionCommand.cmdIf);
		Interpreter.send(cmd);
	}

	private void cmdSetVariable()
			throws SceneException, ParserException, InterpreterException {
		Token tok;
		MissionCommand cmd;

		tok = prs.getToken(); isMatch(tok, Token.STRING);
		String strVariable = tok.strValue;
		tok = prs.getToken(); isMatch(tok, Token.EQ);
		pushExpression(); //get value in stack;
		tok = prs.getToken(); isMatch(tok, Token.SEMI);
		
		cmd = new MissionCommand(MissionCommand.cmdSetVariable, strVariable);
		Interpreter.send(cmd);
	}

	private void cmdDelVariable()
			throws SceneException, ParserException, InterpreterException {
		Token tok;
		MissionCommand cmd;

		tok = prs.getToken(); isMatch(tok, Token.STRING);
		String strVariable = tok.strValue;
		tok = prs.getToken(); isMatch(tok, Token.SEMI);
		
		cmd = new MissionCommand(MissionCommand.cmdDelVariable, strVariable);
		Interpreter.send(cmd);
	}

	private void cmdSet()
			throws SceneException, ParserException, InterpreterException {
		Token tok;
		MissionCommand cmd;
		int c;

		pushExpression();
		tok = prs.getToken(); isMatch(tok, Token.STRING);
		if (tok.strValue.equals("SPEED")) c = MissionCommand.cmdSetSpeed;
		else if (tok.strValue.equals("FUEL")) c = MissionCommand.cmdSetFuel;
		else if (tok.strValue.equals("SHIELD")) c = MissionCommand.cmdSetShield;
		else if (tok.strValue.equals("HULL")) c = MissionCommand.cmdSetHull;
		else if (tok.strValue.equals("ENERGY")) c = MissionCommand.cmdSetEnergy;
		else if (tok.strValue.equals("SIDE")) c = MissionCommand.cmdSetSide;
		else if (tok.strValue.equals("PLAYER_PLACED")) c = MissionCommand.cmdSetPlayerPlaced;
		else if (tok.strValue.equals("PLAYER_CONTROLLED")) c = MissionCommand.cmdSetPlayerControlled;
		else if (tok.strValue.equals("AI_CONTROLLED")) c = MissionCommand.cmdSetAIControlled;
		else if (tok.strValue.equals("MISSION_CONTROLLED")) c = MissionCommand.cmdSetMissionControlled;
		else if (tok.strValue.equals("HUMAN_CONTROLLED")) c = MissionCommand.cmdSetHumanControlled;
		else if (tok.strValue.equals("NET_CONTROLLED")) c = MissionCommand.cmdSetNetControlled;
		else if (tok.strValue.equals("PLAN")) c = MissionCommand.cmdSetFlightPlan;
		else if (tok.strValue.equals("PLAN_POSITION")) c = MissionCommand.cmdSetPlanPosition;
		else if (tok.strValue.equals("YAW")) c = MissionCommand.cmdSetYaw;
		else if (tok.strValue.equals("PITCH")) c = MissionCommand.cmdSetPitch;
		else if (tok.strValue.equals("ROLL")) c = MissionCommand.cmdSetRoll;
   		else if (tok.strValue.equals("X")) c = MissionCommand.cmdSetX;
   		else if (tok.strValue.equals("Y")) c = MissionCommand.cmdSetY;
   		else if (tok.strValue.equals("Z")) c = MissionCommand.cmdSetZ;
		else if (tok.strValue.equals("TARGET_SCREEN")) c = MissionCommand.cmdSetTargetScreen;
		else if (tok.strValue.equals("PRIMARY")) c = MissionCommand.cmdSetPrimaryTarget;
		else if (tok.strValue.equals("SECONDARY")) c = MissionCommand.cmdSetSecondaryTarget;
		else if (tok.strValue.equals("TARGET_TYPE")) c = MissionCommand.cmdSetTargetType;
		else if (tok.strValue.equals("AGRESSION")) c = MissionCommand.cmdSetAgression;
		else if (tok.strValue.equals("KILLPROC")) c = MissionCommand.cmdSetKillProc;
		else if (tok.strValue.equals("REACHPROC")) c = MissionCommand.cmdSetReachProc;
		else if (tok.strValue.equals("LIFE")) c = MissionCommand.cmdSetLife;
		else if (tok.strValue.equals("LOAD")) c = MissionCommand.cmdSetLoad;
		else if (tok.strValue.equals("LIFE_TIME")) c = MissionCommand.cmdSetLifeTime;
		else c = -1;
		if (c == -1) raiseException("unknow modifier - " + tok.strValue);
		tok = prs.getToken(); isMatch(tok, Token.EQ);			
		pushExpression();
		tok = prs.getToken(); isMatch(tok, Token.SEMI);

		cmd = new MissionCommand(c);
		Interpreter.send(cmd);
	}

	private void cmdYaw()
			throws SceneException, ParserException, InterpreterException {
		Token tok;
		MissionCommand cmd;
		//push name
		pushExpression();
		//push value
		pushExpression();
		tok = prs.getToken(); isMatch(tok, Token.SEMI);		
		cmd = new MissionCommand(MissionCommand.cmdYaw);
		Interpreter.send(cmd);
	}

	private void cmdPitch()
			throws SceneException, ParserException, InterpreterException {
		Token tok;
		MissionCommand cmd;
		//push name
		pushExpression();
		//push value
		pushExpression();
		tok = prs.getToken(); isMatch(tok, Token.SEMI);		
		cmd = new MissionCommand(MissionCommand.cmdPitch);
		Interpreter.send(cmd);
	}

	private void cmdRoll()
			throws SceneException, ParserException, InterpreterException {
		Token tok;
		MissionCommand cmd;
		//push name
		pushExpression();
		//push value
		pushExpression();
		tok = prs.getToken(); isMatch(tok, Token.SEMI);		
		cmd = new MissionCommand(MissionCommand.cmdRoll);
		Interpreter.send(cmd);
	}

	private void cmdIncreaseSpeed()
			throws SceneException, ParserException, InterpreterException {
		Token tok;
		MissionCommand cmd;
		//push name
		pushExpression();
		//push value
		pushExpression();
		tok = prs.getToken(); isMatch(tok, Token.SEMI);		
		cmd = new MissionCommand(MissionCommand.cmdIncreaseSpeed);
		Interpreter.send(cmd);
	}

	private void cmdDecreaseSpeed()
			throws SceneException, ParserException, InterpreterException {
		Token tok;
		MissionCommand cmd;
		//push name
		pushExpression();
		//push value
		pushExpression();
		tok = prs.getToken(); isMatch(tok, Token.SEMI);		
		cmd = new MissionCommand(MissionCommand.cmdDecreaseSpeed);
		Interpreter.send(cmd);
	}
	
	private void cmdAdjustSpeed()
			throws SceneException, ParserException, InterpreterException {
		Token tok;
		MissionCommand cmd;
		//push name
		pushExpression();
		//push value
		pushExpression();
		tok = prs.getToken(); isMatch(tok, Token.SEMI);		
		cmd = new MissionCommand(MissionCommand.cmdAdjustSpeed);
		Interpreter.send(cmd);
	}

	private void cmdPlan()
			throws SceneException, ParserException, InterpreterException {
		Token tok;
		MissionCommand cmd;
		int iType = -1;
		int iObj = 0;

		//push plan #
		pushExpression();
		//push plans side
		tok = prs.getToken(); isMatch(tok, Token.STRING, "SIDE");
		pushExpression();

		//take positions type
		tok = prs.getToken(); isMatch(tok, Token.STRING);
		if (tok.strValue.equals("FOLLOW")) { pushValue(Task.Defend); iType = Task.Defend;}
		else if (tok.strValue.equals("ATTACK")) { pushValue(Task.Attack); iType = Task.Attack;}
		else if (tok.strValue.equals("JUMP")) { pushValue(PlanPosition.Jump); iType = PlanPosition.Jump;}
		else if (tok.strValue.equals("NAME")) { pushValue(PlanPosition.Name); iType = PlanPosition.Name;}
		else raiseException("unknown flight plan positions type");

		if (iType == Task.Defend || iType == Task.Attack) {
			//push first object name
			pushExpression();
			iObj = 1;
			tok = prs.getToken(); isMatch(tok, Token.STRING);
			if (tok.strValue.equals("AND")) {
				iObj = 2;
				pushExpression();
				tok = prs.getToken(); isMatch(tok, Token.STRING, "WITH");
			}
			tok = prs.getToken(); isMatch(tok, Token.STRING, "AGRESSION");
			pushExpression();
		} else if (iType == PlanPosition.Name) {
			iObj = 11;
			pushExpression();
		} else if (iType == PlanPosition.Jump) {
			tok = prs.getToken(); isMatch(tok, Token.STRING, "TO");
			pushExpression();
		}

		tok = prs.getToken(); isMatch(tok, Token.SEMI);
		if (iObj == 0) cmd = new MissionCommand(MissionCommand.cmdAddPlanPosition);
		else if (iObj == 1) cmd = new MissionCommand(MissionCommand.cmdAddPlanPosition1);
		else if (iObj == 11) cmd = new MissionCommand(MissionCommand.cmdAddPlanPositionName);
		else cmd = new MissionCommand(MissionCommand.cmdAddPlanPosition2);
		Interpreter.send(cmd);
	}

	private void cmdProc ()
			throws SceneException, ParserException, InterpreterException {
		Token tok;
		Procedure proc;
		int x = 0, y = 0, z = 0, r = 0;

		tok = prs.getToken(); isMatch(tok, Token.STRING);
		if (tok.strValue.equals("EVERY")) {
			//period
			tok = prs.getToken(); isMatch(tok, Token.NUMBER);
			x = tok.iValue;
			proc = new Procedure(x);
		} else if (tok.strValue.equals("ON")) {
			//time
			tok = prs.getToken(); isMatch(tok, Token.STRING, "TIME");
			tok = prs.getToken(); isMatch(tok, Token.NUMBER);
			x = tok.iValue;
			tok = prs.getToken(); isMatch(tok, Token.DOT);
			tok = prs.getToken(); isMatch(tok, Token.NUMBER);
			y = tok.iValue;
			proc = new Procedure(x, y);
		} else if (tok.strValue.equals("AT")) {
			//place
			tok = prs.getToken(); isMatch(tok, Token.NUMBER);
			x = tok.iValue;
			tok = prs.getToken(); isMatch(tok, Token.NUMBER);
			y = tok.iValue;
			tok = prs.getToken(); isMatch(tok, Token.NUMBER);
			z = tok.iValue;
			tok = prs.getToken(); isMatch(tok, Token.STRING, "AREA");
			tok = prs.getToken(); isMatch(tok, Token.NUMBER);
			r = tok.iValue;
			proc = new Procedure(x, y, z, r);
		} else {
			//normal procedure
			proc = new Procedure(tok.strValue);
		}

		tok = prs.getToken(); isMatch(tok, Token.SEMI);			
		Interpreter.createProcedure(proc);
	}

	private void cmdEndProc ()
			throws SceneException, ParserException, InterpreterException {
		Token tok;
		tok = prs.getToken(); isMatch(tok, Token.STRING, "PROC");
		tok = prs.getToken(); isMatch(tok, Token.SEMI);
		Interpreter.closeProcedure();
	}

	private void cmdExitProc ()
			throws SceneException, ParserException, InterpreterException {
		Token tok;
		tok = prs.getToken(); isMatch(tok, Token.SEMI);
		Interpreter.exitProcedure();
	}


	private void raiseException(String descr) throws SceneException {
		throw new SceneException(descr, prs.strFileName, prs.iLine, prs.iLineChar);
	}

	private void pushValue(int iValue)
			throws SceneException, ParserException, InterpreterException {
		MissionCommand cmd = new MissionCommand(MissionCommand.cmdPushInt, iValue);
		Interpreter.send(cmd);
	}

	private void pushValue(String strValue)
			throws SceneException, ParserException, InterpreterException {
		MissionCommand cmd = new MissionCommand(MissionCommand.cmdPushStr, strValue);
		Interpreter.send(cmd);
	}

	/*clone of pushExpression but number values 10 multiplied 'cause 
	  values in meters must be translated in decimeters*/
	private void pushTExpression()
			throws SceneException, ParserException, InterpreterException {
		Token tok;
		MissionCommand cmd;
		
		expr(true);
		Interpreter.send(new MissionCommand(MissionCommand.cmdPushInt, 10));
		Interpreter.send(new MissionCommand(MissionCommand.cmdMul));		
	}

	private void pushExpression()
			throws SceneException, ParserException, InterpreterException {
		Token tok;

		while(true) {
			expr(false);
			tok = prs.getToken();
			if (tok.type != Token.COMMA) {
				prs.retToken(tok);
				break;
			}
		}
	}

	private void expr(boolean takeNegative) 
		throws SceneException, ParserException, InterpreterException {
		Token tok;
		MissionCommand cmd;	

		//primary value
		tok = prs.getToken(); isMatchNull(tok);
		if (tok.type == Token.STRING && tok.strValue.equals("GET")) {
			pushGet();
		} else if (tok.type == Token.STRING) {
			cmd = new MissionCommand(MissionCommand.cmdPushStr, tok.strValue);
			Interpreter.send(cmd);
		} else if (tok.type == Token.NUMBER) {
			cmd = new MissionCommand(MissionCommand.cmdPushInt, tok.iValue);
			Interpreter.send(cmd);
		} else if (tok.type == Token.USD) {
			tok = prs.getToken(); isMatch(tok, Token.STRING);
			cmd = new MissionCommand(MissionCommand.cmdGetVariable, tok.strValue);
			Interpreter.send(cmd);
		} else if (tok.type == Token.ORD) {
			//round expression
			expr(takeNegative);
			tok = prs.getToken(); isMatch(tok, Token.CRD);
		} else if (tok.type == Token.CRD) {
			prs.retToken(tok);
			return;
		} else {
			throw new SceneException("string or number expected but "
					+ Token.Match[tok.type] + " has been found",
					prs.strFileName, prs.iLine, prs.iLineChar);
		}

		//operator
		int operator;
		if (!takeNegative) prs.takeNegative = false;
		tok = prs.getToken(); 
		if (isMatchOperator(tok)) {
			operator = tok.type;
			//second value
			expr(takeNegative);
			cmd = new MissionCommand(MissionCommand.cmdAdd);
			switch(operator) {
				case Token.ADD: cmd = new MissionCommand(MissionCommand.cmdAdd);
								break;
				case Token.SUB: cmd = new MissionCommand(MissionCommand.cmdSub);
								break;
				case Token.MUL: cmd = new MissionCommand(MissionCommand.cmdMul);
								break;
				case Token.DIV: cmd = new MissionCommand(MissionCommand.cmdDiv);
								break;
				case Token.MOD: cmd = new MissionCommand(MissionCommand.cmdMod);
								break;
				case Token.CMP: cmd = new MissionCommand(MissionCommand.cmdCmp);
								break;
				case Token.NEQ: cmd = new MissionCommand(MissionCommand.cmdNeq);
								break;
				case Token.LS:  cmd = new MissionCommand(MissionCommand.cmdLs);
								break;
				case Token.LSQ: cmd = new MissionCommand(MissionCommand.cmdLsq);
								break;
				case Token.GR:  cmd = new MissionCommand(MissionCommand.cmdGr);
								break;
				case Token.GRQ: cmd = new MissionCommand(MissionCommand.cmdGrq);
								break;
				case Token.AND: cmd = new MissionCommand(MissionCommand.cmdAnd);
								break;
				case Token.OR:  cmd = new MissionCommand(MissionCommand.cmdOr);
								break;
				default:		raiseException("unknow operator");
			}
			Interpreter.send(cmd);
		} else {
			prs.retToken(tok);
		}		
	}

	private void pushGet()
		throws SceneException, ParserException, InterpreterException {
		//get value from object
		Token tok;
		MissionCommand cmd;

		//push object name
		pushExpression();
		tok = prs.getToken(); isMatch(tok, Token.DOT);
		tok = prs.getToken(); isMatch(tok, Token.STRING);

		if (tok.strValue.equals("X")) cmd = new MissionCommand(MissionCommand.cmdGetX);
		else if(tok.strValue.equals("Y")) cmd = new MissionCommand(MissionCommand.cmdGetY);
		else if(tok.strValue.equals("Z")) cmd = new MissionCommand(MissionCommand.cmdGetZ);
		else {
			cmd = new MissionCommand(MissionCommand.cmdGetZ);
			raiseException("unknown object property : " + tok.strValue);
		}

		Interpreter.send(cmd);
	}

	private boolean isMatchOperator(Token tok) throws SceneException {
		if (tok == null)
			throw new SceneException("unexpected end of the file",
								prs.strFileName, prs.iLine, prs.iLineChar);
		switch (tok.type) {
			case Token.ADD: return true;
			case Token.SUB: return true;
			case Token.MUL: return true;
			case Token.DIV: return true;
			case Token.MOD: return true;
			case Token.CMP: return true;
			case Token.NEQ: return true;
			case Token.LS:  return true;
			case Token.LSQ: return true;
			case Token.GR:  return true;
			case Token.GRQ: return true;
			case Token.AND: return true;
			case Token.OR: 	return true;
		}
		return false;
	}


	private void isMatchNull(Token tok) throws SceneException {
		if (tok == null)
			throw new SceneException("unexpected end of the file",
								prs.strFileName, prs.iLine, prs.iLineChar);
	}

	private void isMatch(Token tok, int type) throws SceneException {
		if (tok == null)
			throw new SceneException("unexpected end of the file",
								prs.strFileName, prs.iLine, prs.iLineChar);
		if (tok.type!=type && tok.type == Token.STRING)
			throw new SceneException("'" + Token.Match[type] + "' expected but "
					+ Token.Match[tok.type] + "'" + tok.strValue + "' has been found",
					prs.strFileName, prs.iLine, prs.iLineChar);
		if (tok.type!=type && tok.type == Token.NUMBER)
			throw new SceneException("'" + Token.Match[type] + "' expected but "
					+ Token.Match[tok.type] + tok.iValue + " has been found",
					prs.strFileName, prs.iLine, prs.iLineChar);
		else if (tok.type!=type)
			throw new SceneException("'" + Token.Match[type] + "' expected but "
					+ Token.Match[tok.type] + " has been found",
					prs.strFileName, prs.iLine, prs.iLineChar);
	}

	private void isMatch(Token tok, int type, String strValue) throws SceneException {
		if (tok == null)
			throw new SceneException("unexpected end of the file",
								prs.strFileName, prs.iLine, prs.iLineChar);
		if (tok.type!=type || (tok.strValue.equals(strValue) == false))
			throw new SceneException("modifier '" + strValue + "' expected",
					prs.strFileName, prs.iLine, prs.iLineChar);
	}

	private void createLights() {
		sun = new RSun(400000000.0, -20000000.0, -1000000000.0, 1, 1, 1);
	}

	public void changeTiming() {
		this.timing+= this.timing;
		if (this.timing > 4) this.timing = 1;
		messageScreen.spush("Time set to x" + this.timing);
	}
	
	public void showSideControl() {
		messageScreen.spush(Interpreter.getsVariable("SideName" + camera.Side)
			+ " takes control");
	}
	
	public void switchCamera() {
		if (camera.iRelatedObject != -1)
			this.Objects.Objects[camera.iRelatedObject].HumanControlled = false;
		camera.leaveObject();
		int nindex = this.Objects.getNextIndex(-1);
		if (nindex != -1) {
			camera.iRelatedObject = nindex;
		}
	}
	
	public void nextParty() {
		//give control to the next party
		if (this.camera.Side == 0) this.statis = true;
		
		this.turnCounter = 0;
		this.camera.Side++;
		if (this.camera.Side > this.sides
		|| this.camera.Side > this.turnSides) {
			this.camera.Side = 0;
			this.statis = false;
			this.actionCounter = 0;
			return;
		}
		
		this.showSideControl();
		this.switchCamera();
	}
}