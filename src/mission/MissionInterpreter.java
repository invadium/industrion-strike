package mission;

import java.awt.*;
import java.util.Dictionary;
import java.util.Hashtable;

import control.Task;
import scene.*;
import control.PlanPosition;

public class MissionInterpreter {
	Camera camera;
	ModelsCollector Models;
	Scene scene;
	SOCollector Objects;

	public Hashtable Heap = new Hashtable();
	public MCmdCollector commands = new MCmdCollector();
	InterpretersStack Stack = new InterpretersStack();
	boolean isStoring = false;

	public MissionInterpreter(Camera camera, ModelsCollector Models,
			Scene scene, SOCollector Objects) {
		this.camera = camera;
		this.Models = Models;
		this.scene = scene;
		this.Objects = Objects;
	}

	public void createProcedure(Procedure newProcedure) throws InterpreterException {
		if (isStoring) throw new InterpreterException ("wrong place to declarate procedure");
		isStoring = true;
		commands.addProc(newProcedure);
	}

	public void closeProcedure()  throws InterpreterException {
		MissionCommand cmd = new MissionCommand(MissionCommand.cmdEndProc);
		commands.storeCmd(cmd);
		isStoring = false;
	}

	public void exitProcedure()  throws InterpreterException {
		MissionCommand cmd = new MissionCommand(MissionCommand.cmdEndProc);
		commands.storeCmd(cmd);
	}

	public void send(MissionCommand cmd) throws InterpreterException {
		try {
			if (isStoring) commands.storeCmd(cmd);
				else this.run(cmd);
		} catch (InterpreterException e){
			Stack.dumpValues(10);
			throw e;
		}
	}

	int cur = 0; //current command pointer
	int curStart = 0;//current proc start
	public void runProc(int index) {
		try {			
			if (index>=0 && index<commands.cntProc) {
				cur = commands.proc[index].startPoint;
				curStart = cur;
				int topStack = Stack.getTop();
				while (commands.commands[cur].type != MissionCommand.cmdEndProc
						&& commands.commands[cur].type != MissionCommand.cmdRetProc) {
					try {
						this.run(commands.commands[cur]);
					} catch (InterpreterException e) {
						System.out.println("exception at point " + cur + " command #" + commands.commands[cur].type);
						System.out.println(e.toString());
					}
					cur++;
				}
				Stack.setTop(topStack);
			} else throw new InterpreterException("procedure with index " + index + " doesn't exist");
		} catch (InterpreterException e) {
			System.out.println("exception at point " + cur + " command #" + commands.commands[cur].type);
			System.out.println(e.toString());
		}
	}

	public void run(MissionCommand cmd) throws InterpreterException {
		switch (cmd.type) {
			//mission language
			case MissionCommand.cmdCallProc: doCallProc(cmd); break;
			case MissionCommand.cmdLabel: break;
			case MissionCommand.cmdGoTo: doGoTo(cmd); break;
			case MissionCommand.cmdPushInt: doPushInt(cmd); break;
			case MissionCommand.cmdPushStr: doPushStr(cmd); break;
			case MissionCommand.cmdCreate: doCreate(); break;
			case MissionCommand.cmdBindCameraTo: doBindCameraTo(); break;
            case MissionCommand.cmdBindViewCameraTo: doBindViewCameraTo(); break;
			case MissionCommand.cmdSetVariable: doSetVariable(cmd); break;
			case MissionCommand.cmdDelVariable: doDelVariable(cmd); break;
			case MissionCommand.cmdGetVariable: doGetVariable(cmd); break;
			case MissionCommand.cmdIf: doIf(); break;

			//operators
			case MissionCommand.cmdAdd: doAdd(); break;
			case MissionCommand.cmdSub: doSub(); break;
			case MissionCommand.cmdDiv: doDiv(); break;
			case MissionCommand.cmdMul: doMul(); break;
			case MissionCommand.cmdMod: doMod(); break;
			case MissionCommand.cmdCmp: doCmp(); break;
			case MissionCommand.cmdNeq: doNeq(); break;
			case MissionCommand.cmdLs:  doLs();  break;
			case MissionCommand.cmdLsq: doLsq(); break;
			case MissionCommand.cmdGr:  doGr();  break;
			case MissionCommand.cmdGrq: doGrq(); break;
			case MissionCommand.cmdAnd: doAnd(); break;
			case MissionCommand.cmdOr: doOr(); break;

			//set dirrectirves
			case MissionCommand.cmdSetSpeed: doSetSpeed(); break;
			case MissionCommand.cmdSetFuel: doSetFuel(); break;
			case MissionCommand.cmdSetShield: doSetShield(); break;
			case MissionCommand.cmdSetHull: doSetHull(); break;
			case MissionCommand.cmdSetEnergy: doSetEnergy(); break;
			case MissionCommand.cmdSetSide: doSetSide(); break;
			case MissionCommand.cmdSetPlayerPlaced: doSetPlayerPlaced(); break;
			case MissionCommand.cmdSetPlayerControlled: doSetPlayerControlled(); break;
			case MissionCommand.cmdSetAIControlled: doSetAIControlled(); break;
			case MissionCommand.cmdSetMissionControlled: doSetMissionControlled(); break;
			case MissionCommand.cmdSetHumanControlled: doSetHumanControlled(); break;
			case MissionCommand.cmdSetNetControlled: doSetNetControlled(); break;
			case MissionCommand.cmdSetFlightPlan: doSetFlightPlan(); break;
			case MissionCommand.cmdSetPlanPosition: doSetPlanPosition(); break;
			case MissionCommand.cmdSetYaw: doSetYaw(); break;
			case MissionCommand.cmdSetPitch: doSetPitch(); break;
			case MissionCommand.cmdSetRoll: doSetRoll(); break;
			case MissionCommand.cmdSetX: doSetX(); break;
			case MissionCommand.cmdSetY: doSetY(); break;
			case MissionCommand.cmdSetZ: doSetZ(); break;
			case MissionCommand.cmdSetTargetScreen: doSetTargetScreen(); break;
			case MissionCommand.cmdSetPrimaryTarget: doSetPrimaryTarget(); break;
			case MissionCommand.cmdSetSecondaryTarget: doSetSecondaryTarget(); break;
			case MissionCommand.cmdSetTargetType: doSetTargetType(); break;
			case MissionCommand.cmdSetAgression: doSetAgression(); break;
			case MissionCommand.cmdSetKillProc: doSetKillProc(); break;
			case MissionCommand.cmdSetReachProc: doSetReachProc(); break;
			case MissionCommand.cmdSetLife: doSetLife(); break;
			case MissionCommand.cmdSetLoad: doSetLoad(); break;
			case MissionCommand.cmdSetLifeTime: doSetLifeTime(); break;

			//get dirrectives
			case MissionCommand.cmdGetX: doGetX(); break;
			case MissionCommand.cmdGetY: doGetY(); break;
			case MissionCommand.cmdGetZ: doGetZ(); break;

			//ship's commands
			case MissionCommand.cmdYaw: doYaw(); break;
			case MissionCommand.cmdPitch: doPitch(); break;
			case MissionCommand.cmdRoll: doRoll(); break;
			case MissionCommand.cmdIncreaseSpeed: doIncreaseSpeed(); break;
			case MissionCommand.cmdDecreaseSpeed: doDecreaseSpeed(); break;
			case MissionCommand.cmdAdjustSpeed: doAdjustSpeed(); break;

			//environment
			case MissionCommand.cmdMsg: doMsg(); break;
			case MissionCommand.cmdSMsg: doSMsg(); break;
			case MissionCommand.cmdCMsg: doCMsg(); break;
			case MissionCommand.cmdSCMsg: doSCMsg(); break;
			case MissionCommand.cmdShowFinish: doShowFinish(); break;
			case MissionCommand.cmdAddPlanPosition: doAddPlanPosition(0); break;
			case MissionCommand.cmdAddPlanPosition1: doAddPlanPosition(1); break;
			case MissionCommand.cmdAddPlanPosition2: doAddPlanPosition(2); break;
			case MissionCommand.cmdAddPlanPositionName: doAddPlanPosition(3); break;
			case MissionCommand.cmdSmartCameraOn: doSmartCameraOn(); break;
			case MissionCommand.cmdSmartCameraOff: doSmartCameraOff(); break;

			default: throw new InterpreterException("unknown command");
		}
	}

	private void doCallProc(MissionCommand cmd) throws InterpreterException {
		int index = commands.findProc(cmd.strValue);
		if (index == -1) throw new InterpreterException("procedure " + cmd.strValue + " doesn't exist");
		int lastCur = this.cur;
		int lastStart = this.curStart;
		runProc(index);
		this.curStart = lastStart;
		this.cur = lastCur;
	}
	
	private void doGoTo(MissionCommand cmd) throws InterpreterException {
		int iLabel = commands.findLabel(cmd.strValue, this.curStart);
		if (iLabel != -1) {
				this.cur = iLabel;
		} else {
				throw new InterpreterException("undefined label: '" + cmd.strValue + "'");
		}
	}

	private void doPushInt(MissionCommand cmd) throws InterpreterException {
		Stack.push(cmd.iValue);
	}

	private void doPushStr(MissionCommand cmd) throws InterpreterException {
		Stack.push(cmd.strValue);
	}

	private void doCreate()
			throws InterpreterException {
		int z = Stack.popi();
		int y = Stack.popi();
		int x = Stack.popi();
		String strModel = Stack.pops();
		String strName = Stack.pops();

		int model = Models.getIndexByName(strModel);
		if (model == -1) throw new InterpreterException("unknown model '" + strModel + "'");
		SpaceObject spaceObject = new SpaceObject(strName,
			Models.models[model], (double)x, (double)y, (double)z);

		Objects.add(spaceObject);
	}

	private void doBindCameraTo() throws InterpreterException {
		String strName = Stack.pops();

		int index = Objects.getIndexByName(strName);
		if (index < 0) throw new InterpreterException("object '" + strName + "' dos't exist");
		if (camera.iRelatedObject != -1)
			if (Objects.Objects[camera.iRelatedObject].HumanControlled)
				Objects.Objects[camera.iRelatedObject].HumanControlled = false;
		camera.iRelatedObject = index;
	}
    
    private void doBindViewCameraTo() throws InterpreterException {
        int view = Stack.popi();
		String strName = Stack.pops();

		int index = Objects.getIndexByName(strName);
		if (index < 0) throw new InterpreterException("object '" + strName + "' dos't exist");
		if (camera.iRelatedObject != -1)
			if (Objects.Objects[camera.iRelatedObject].HumanControlled)
				Objects.Objects[camera.iRelatedObject].HumanControlled = false;
            
		camera.iRelatedObject = index;
        if (view < 0 || view > 8) view = 0;
        camera.iRelationType = view;
	}

	private void doMsg() throws InterpreterException {
		if (Stack.topType() == DataElement.strElement) {
			String strMsg = Stack.pops();
			scene.messageScreen.push(strMsg);
		} else {
			String strMsg = "" + Stack.popi();
			scene.messageScreen.push(strMsg);
		}
	}

	private void doCMsg() throws InterpreterException {
		int b = Stack.popi();
		int g = Stack.popi();
		int r = Stack.popi();
		Color msgColor = new Color(r, g, b);

		if (Stack.topType() == DataElement.strElement) {
			String strMsg = Stack.pops();
			scene.messageScreen.push(strMsg, msgColor);
		} else {
			String strMsg = "" + Stack.popi();
			scene.messageScreen.push(strMsg, msgColor);
		}
	}

	private void doSMsg() throws InterpreterException {
		if (Stack.topType() == DataElement.strElement) {
			String strMsg = Stack.pops();
			scene.messageScreen.spush(strMsg);
		} else {
			String strMsg = "" + Stack.popi();
			scene.messageScreen.spush(strMsg);
		}
	}

	private void doSCMsg() throws InterpreterException {
		int b = Stack.popi();
		int g = Stack.popi();
		int r = Stack.popi();
		Color msgColor = new Color(r, g, b);

		if (Stack.topType() == DataElement.strElement) {
			String strMsg = Stack.pops();
			scene.messageScreen.spush(strMsg, msgColor);
		} else {
			String strMsg = "" + Stack.popi();
			scene.messageScreen.spush(strMsg, msgColor);
		}
	}

	private void doShowFinish() throws InterpreterException {
		scene.end = true;
		scene.cntEnd = 0;
	}
	
	private void doSmartCameraOn() throws InterpreterException {
		camera.smartBind = true;
	}
	
	private void doSmartCameraOff() throws InterpreterException {
		camera.smartBind = false;
	}

	private void doSetVariable(MissionCommand cmd) throws InterpreterException {
		//DataElement newDE = new DataElement(Stack.pop());
		DataElement newDE = Stack.pop();
		Heap.put(cmd.strValue, newDE);
	}

	private void doDelVariable(MissionCommand cmd) throws InterpreterException {
		Heap.remove(cmd.strValue);
	}

	private void doGetVariable(MissionCommand cmd) throws InterpreterException {
		DataElement dataElement = (DataElement)Heap.get(cmd.strValue);
		if (dataElement == null) throw new InterpreterException("variable " + cmd.strValue + " hasn't been inicialized");
		Stack.push(dataElement);
	}

	private void doIf() throws InterpreterException {
		int iValue = Stack.popi();
		if (iValue == 0) this.cur++;
	}


	private void doSetSpeed() throws InterpreterException {
		int iSpeed = Stack.popi();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");
		Objects.Objects[index].currentSpeed = iSpeed;
	}

	private void doSetFuel() throws InterpreterException {
		int iFuel = Stack.popi();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");		
		Objects.Objects[index].currentFuel = iFuel;
	}

	private void doSetShield() throws InterpreterException {
		int iShield = Stack.popi();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");
		Objects.Objects[index].Shield = iShield;
	}

	private void doSetYaw() throws InterpreterException {
		int i = Stack.popi();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");
		Objects.Objects[index].currentYaw = i;
	}

	private void doSetPitch() throws InterpreterException {
		int i = Stack.popi();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");
		Objects.Objects[index].currentPitch = i;
	}

	private void doSetRoll() throws InterpreterException {
		int i = Stack.popi();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");
		Objects.Objects[index].currentRoll = i;
	}

	private void doSetX() throws InterpreterException {
		int i = Stack.popi();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");
		Objects.Objects[index].x = (double)i;
	}

	private void doSetY() throws InterpreterException {
		int i = Stack.popi();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");
		Objects.Objects[index].y = (double)i;
	}

	private void doSetZ() throws InterpreterException {
		int i = Stack.popi();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");
		Objects.Objects[index].z = (double)i;
	}

	private void doSetHull() throws InterpreterException {
		int iHull = Stack.popi();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");		
		Objects.Objects[index].Hull = iHull;
	}

	private void doSetEnergy() throws InterpreterException {
		int iEnergy = Stack.popi();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");		
		Objects.Objects[index].Energy = iEnergy;
	}

	private void doSetSide() throws InterpreterException {
		int i = Stack.popi();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");		
		Objects.Objects[index].Side = i;
	}

	private void doSetPlayerPlaced() throws InterpreterException {
		int i = Stack.popi();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");		
		if (i==0) Objects.Objects[index].PlayerPlaced = false;
			else Objects.Objects[index].PlayerPlaced = true;
	}

	private void doSetPlayerControlled() throws InterpreterException {
		int i = Stack.popi();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");		
		if (i==0) Objects.Objects[index].PlayerControlled = false;
			else Objects.Objects[index].PlayerControlled = true;		
	}

	private void doSetAIControlled() throws InterpreterException {
		int i = Stack.popi();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");		
		if (i==0) Objects.Objects[index].AIControlled = false;
			else Objects.Objects[index].AIControlled = true;		
	}

	private void doSetMissionControlled() throws InterpreterException {
		int i = Stack.popi();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");		
		if (i==0) Objects.Objects[index].MissionControlled = false;
			else Objects.Objects[index].MissionControlled = true;		
	}

	private void doSetHumanControlled() throws InterpreterException {
		int i = Stack.popi();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");		
		if (i==0) Objects.Objects[index].HumanControlled = false;
			else Objects.Objects[index].HumanControlled = true;
		
	}

	private void doSetNetControlled() throws InterpreterException {
		int i = Stack.popi();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");		
		if (i==0) Objects.Objects[index].NetControlled = false;
			else Objects.Objects[index].NetControlled = true;		
	}

	private void doSetFlightPlan() throws InterpreterException {
		int i = Stack.popi();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");		
		Objects.Objects[index].flightPlan = i;
	}

	private void doSetPlanPosition() throws InterpreterException {
		int i = Stack.popi();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");		
		Objects.Objects[index].flightPlanPosition = i;
	}

	private void doSetTargetScreen() throws InterpreterException {
		String strTarget = Stack.pops();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");
		int target = Objects.getIndexByName(strTarget);
		if (index == -1) throw new InterpreterException("object '" + strTarget + "' dos't exist");
		
		Objects.Objects[index].setTaskScreen(target);
	}

	private void doSetPrimaryTarget() throws InterpreterException {
		String strTarget = Stack.pops();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");
		int target = Objects.getIndexByName(strTarget);
		if (index == -1) throw new InterpreterException("object '" + strTarget + "' dos't exist");
		
		Objects.Objects[index].setPrimaryTarget(target);
	}

	private void doSetSecondaryTarget() throws InterpreterException {
		String strTarget = Stack.pops();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");
		int target = Objects.getIndexByName(strTarget);
		if (index == -1) throw new InterpreterException("object '" + strTarget + "' dos't exist");
		
		Objects.Objects[index].setSecondaryTarget(target);
	}

	private void doSetTargetType() throws InterpreterException {
		String strTarget = Stack.pops();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");
		int type = Task.None;
		if (strTarget.equals("ATTACK")) type = Task.Attack;
		else if (strTarget.equals("FOLLOW")) type = Task.Defend;
		
		Objects.Objects[index].currentTask.iType = type;
	}

	private void doSetAgression() throws InterpreterException {
		int i = Stack.popi();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");		
		Objects.Objects[index].agressionLevel = i;
	}

	private void doSetKillProc() throws InterpreterException {
		String strProc = Stack.pops();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");		
		int kill = commands.findProc(strProc);

		if (kill >= 0) Objects.Objects[index].killProcedure = kill;
	}

	private void doSetReachProc() throws InterpreterException {
		String strProc = Stack.pops();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");
		int reach = commands.findProc(strProc);

		if (reach >= 0) Objects.Objects[index].reachProcedure = reach;
	}

	private void doSetLife() throws InterpreterException {
		int i = Stack.popi();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");

		if (i > 0) scene.Objects.Objects[index].isLive = true;
		else scene.Objects.Objects[index].isLive = false;
	}

	private void doSetLoad() throws InterpreterException {
		int quantity = Stack.popi();
		int bay = Stack.popi();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");
		
		scene.Objects.Objects[index].missilesLoad[bay] = quantity;
	}
	
	private void doSetLifeTime() throws InterpreterException {
		int time = Stack.popi();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");
		
		scene.Objects.Objects[index].lifeTime = time;
	}


	//****************************************************
	//					Get dirrectives
	//*****************************************************
	private void doGetX() throws InterpreterException {
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");

		Stack.push((int)scene.Objects.Objects[index].x / 10);
	}

	private void doGetY() throws InterpreterException {
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");

		Stack.push((int)scene.Objects.Objects[index].y / 10);
	}

	private void doGetZ() throws InterpreterException {
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");

		Stack.push((int)scene.Objects.Objects[index].z / 10);
	}
	//***************************************************

	private void doAddPlanPosition(int iCmd) throws InterpreterException {
		int agression = 1;
		if (iCmd != 3) agression = Stack.popi();
		String strPrimary = "", strSecondary = "";
		int iPrimary = -1, iSecondary = -1;

		if (iCmd == 2) {
			strSecondary = Stack.pops();
			strPrimary = Stack.pops();
		} else if (iCmd == 1) {
			strPrimary = Stack.pops();
		} else if (iCmd == 3) {
			strPrimary = Stack.pops();
		}

		int type = Stack.popi();
		int side = Stack.popi();
		int plan = Stack.popi();

		PlanPosition pos;
		if (iCmd == 3) pos = new PlanPosition(side, plan, type, 1, strPrimary);
		else if (iCmd == 2) pos = new PlanPosition(side, plan, type, agression, strPrimary, strSecondary);
		else if (iCmd == 1) pos = new PlanPosition(side, plan, type, agression, strPrimary);
		else pos = new PlanPosition(side, plan, type, agression);

		scene.plan.add(pos);
	}

	private void doAdd() throws InterpreterException {
		int secType = Stack.topType();
		if (secType == DataElement.strElement) {
			String strSecondary = Stack.pops();
			if (Stack.topType() != DataElement.strElement) throw new InterpreterException("string value in stack expected");
			String strPrimary = Stack.pops();
			String strRes = strPrimary + strSecondary;
			Stack.push(strRes);
		} else if(secType == DataElement.intElement) {
			int iSecondary = Stack.popi();
			int priType = Stack.topType();
			if (priType == DataElement.intElement) {
            	int iPrimary = Stack.popi();						
				int iRes = iPrimary + iSecondary;
				Stack.push(iRes);
			} else if(priType == DataElement.strElement) {
				String strPrimary = Stack.pops();
				String strRes = strPrimary + iSecondary;
				Stack.push(strRes);
			}
		} else throw new InterpreterException("operation + can't be applied to data element in stack");
	}

	private void doSub() throws InterpreterException {
		int iSecondary = Stack.popi();
		int iPrimary = Stack.popi();
		int iRes = iPrimary - iSecondary;
		Stack.push(iRes);
	}

	private void doDiv() throws InterpreterException {
		int iSecondary = Stack.popi();
		int iPrimary = Stack.popi();
		int iRes = (int)(iPrimary / iSecondary);
		Stack.push(iRes);
	}

	private void doMul() throws InterpreterException {
		int iSecondary = Stack.popi();
		int iPrimary = Stack.popi();
		int iRes = iPrimary * iSecondary;
		Stack.push(iRes);
	}

	private void doMod() throws InterpreterException {
		int iSecondary = Stack.popi();
		int iPrimary = Stack.popi();
		int iRes = iPrimary % iSecondary;
		Stack.push(iRes);
	}

	private void doCmp() throws InterpreterException {
		int secType = Stack.topType();
		if (secType == DataElement.intElement) {
			//compare numbers
			int iSecondary = Stack.popi();
			int iPrimary = Stack.popi();
			int iRes = 0;
			if (iPrimary == iSecondary) iRes = 1;
			Stack.push(iRes);
		} else if (secType == DataElement.strElement) {
			//compare strings
			String strSecondary = Stack.pops();
			String strPrimary = Stack.pops();
			int iRes = 0;
			if (strPrimary.equals(strSecondary)) iRes = 1;
			Stack.push(iRes);
		} else throw new InterpreterException("string or number value in stack expected");
	}

	private void doNeq() throws InterpreterException {
		int secType = Stack.topType();
		if (secType == DataElement.intElement) {
			//compare numbers
			int iSecondary = Stack.popi();
			int iPrimary = Stack.popi();
			int iRes = 0;
			if (iPrimary != iSecondary) iRes = 1;
			Stack.push(iRes);
		} else if (secType == DataElement.strElement) {
			//compare strings
			String strSecondary = Stack.pops();
			String strPrimary = Stack.pops();
			int iRes = 1;
			if (strPrimary.equals(strSecondary)) iRes = 0;
			Stack.push(iRes);
		} else throw new InterpreterException("string or number value in stack expected");
	}

	private void doLs() throws InterpreterException {
		int iSecondary = Stack.popi();
		int iPrimary = Stack.popi();
		int iRes = 0;
		if (iPrimary < iSecondary) iRes = 1;
		Stack.push(iRes);
	}

	private void doLsq() throws InterpreterException {
		int iSecondary = Stack.popi();
		int iPrimary = Stack.popi();
		int iRes = 0;
		if (iPrimary <= iSecondary) iRes = 1;
		Stack.push(iRes);
	}

	private void doGr() throws InterpreterException {
		int iSecondary = Stack.popi();
		int iPrimary = Stack.popi();
		int iRes = 0;
		if (iPrimary > iSecondary) iRes = 1;
		Stack.push(iRes);
	}

	private void doGrq() throws InterpreterException {
		int iSecondary = Stack.popi();
		int iPrimary = Stack.popi();
		int iRes = 0;
		if (iPrimary >= iSecondary) iRes = 1;
		Stack.push(iRes);
	}

	private void doAnd() throws InterpreterException {
		int iSecondary = Stack.popi();
		int iPrimary = Stack.popi();
		int iRes = 0;
		if (iPrimary > 0 && iSecondary > 0) iRes = 1;
		Stack.push(iRes);
	}

	private void doOr() throws InterpreterException {
		int iSecondary = Stack.popi();
		int iPrimary = Stack.popi();
		int iRes = 0;
		if (iPrimary > 0 || iSecondary > 0) iRes = 1;
		Stack.push(iRes);
	}

	private void doYaw() throws InterpreterException {
		int i = Stack.popi();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' doesn't exist");
		Objects.Objects[index].starsRotation = false;
		Objects.Objects[index].changeYaw(i);
	}

	private void doPitch() throws InterpreterException {
		int i = Stack.popi();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' doesn't exist");
		Objects.Objects[index].starsRotation = false;
		Objects.Objects[index].changePitch(i);
	}

	private void doRoll() throws InterpreterException {
		int i = Stack.popi();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' dos't exist");
		Objects.Objects[index].starsRotation = false;
		Objects.Objects[index].changeRoll(i);
	}

	private void doIncreaseSpeed() throws InterpreterException {
		int i = Stack.popi();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' doesn't exist");
		Objects.Objects[index].increaseSpeed();
	}

	private void doDecreaseSpeed() throws InterpreterException {
		int i = Stack.popi();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' doesn't exist");
		Objects.Objects[index].decreaseSpeed();
	}
	
	private void doAdjustSpeed() throws InterpreterException {
		int i = Stack.popi();
		String strName = Stack.pops();
		int index = Objects.getIndexByName(strName);
		if (index == -1) throw new InterpreterException("object '" + strName + "' doesn't exist");
		Objects.Objects[index].adjustedSpeed = i;
	}


	//**********************************************
	//This is the gate for the SDL variables 
	//**********************************************
	public DataElement getVariable(String varName) {		
		DataElement dataElement = (DataElement)Heap.get(varName);
		if (dataElement != null) return new DataElement(dataElement);
		else return null;
	}

	public int getiVariable(String varName) {
		DataElement dataElement = (DataElement)Heap.get(varName);
		if (dataElement == null) return 0;
		if (dataElement.type != DataElement.intElement) return 0;
		return dataElement.iValue;
	}

	public String getsVariable(String varName) {
		DataElement dataElement = (DataElement)Heap.get(varName);
		if (dataElement == null) return "";
		if (dataElement.type != DataElement.strElement) return "";
		return dataElement.strValue;
	}

	public void setVariable (String varName, int iValue) {
		DataElement newDE = new DataElement(iValue);
		Heap.put(varName, newDE);
	}

	public void setVariable (String varName, String strValue) {
		DataElement newDE = new DataElement(strValue);
		Heap.put(varName, newDE);
	}

	public void setVariable (String varName, DataElement dataElement) {
		Heap.put(varName, dataElement);
	}
}