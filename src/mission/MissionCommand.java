package mission;

public class MissionCommand {
	public int type = -1;
	public String strValue;
	public int iValue;

	public static final int cmdEndProc = -13;

	public static final int cmdClearStack = 0;
	public static final int cmdPushInt = 1;
	public static final int cmdPushStr = 2;
	public static final int cmdPopInt = 3;
	public static final int cmdPopStr = 4;
	public static final int cmdCreate = 5;
	public static final int cmdBindCameraTo = 6;
    public static final int cmdBindViewCameraTo = 7;
	public static final int cmdSmartCameraOn = 8;
	public static final int cmdSmartCameraOff = 9;

	//objects set commands
	public static final int cmdSetSpeed = 501;
	public static final int cmdSetFuel = 502;
	public static final int cmdSetShield = 503;
	public static final int cmdSetHull = 504;
	public static final int cmdSetEnergy = 505;
	public static final int cmdSetSide = 506;
	public static final int cmdSetPlayerControlled = 507;
	public static final int cmdSetPlayerPlaced = 508;
	public static final int cmdSetAIControlled = 509;
	public static final int cmdSetMissionControlled = 510;
	public static final int cmdSetHumanControlled = 511;
	public static final int cmdSetNetControlled = 512;
	public static final int cmdSetFlightPlan = 513;
	public static final int cmdSetPlanPosition = 514;
	public static final int cmdSetYaw = 515;
	public static final int cmdSetPitch = 516;
	public static final int cmdSetRoll = 517;
	public static final int cmdSetX = 518;
	public static final int cmdSetY = 519;
	public static final int cmdSetZ = 520;
	public static final int cmdSetLive = 521;
	public static final int cmdSetTargetScreen = 522;
	public static final int cmdSetPrimaryTarget = 523;
	public static final int cmdSetSecondaryTarget = 524;
	public static final int cmdSetTargetType = 525;
	public static final int cmdSetAgression = 526;
	public static final int cmdSetKillProc = 527;
	public static final int cmdSetReachProc = 528;
	public static final int cmdSetLife = 529;
	public static final int cmdSetLoad = 530;
	public static final int cmdSetLifeTime = 531;

	//get property commands
	public static final int cmdGetX = 601;
	public static final int cmdGetY = 602;
	public static final int cmdGetZ = 603;
	public static final int cmdGetSpeed = 604;
	public static final int cmdGetShield= 605;
	public static final int cmdGetHull = 606;
	public static final int cmdGetFuel = 607;

	//ships commands
	public static final int cmdYaw = 101;
	public static final int cmdPitch = 102;
	public static final int cmdRoll = 103;
	public static final int cmdIncreaseSpeed = 104;
	public static final int cmdDecreaseSpeed = 105;
	public static final int cmdAdjustSpeed = 106;

	//mission language
	public static final int cmdCallProc = 201;
	public static final int cmdRetProc = 202;
	public static final int cmdLabel = 203;
	public static final int cmdGoTo = 204;
	public static final int cmdSetVariable = 205;
	public static final int cmdGetVariable = 206;
	public static final int cmdDelVariable = 207;
	public static final int cmdIf = 208;
	public static final int cmdIfElse = 209;

	//operators
	public static final int cmdAdd = 301;
	public static final int cmdSub = 302;
	public static final int cmdDiv = 303;
	public static final int cmdMul = 304;
	public static final int cmdMod = 305;
	public static final int cmdCmp = 306;
	public static final int cmdNeq = 307;
	public static final int cmdLs  = 308;
	public static final int cmdLsq = 309;
	public static final int cmdGr  = 310;
	public static final int cmdGrq = 311;
	public static final int cmdAnd = 312;
	public static final int cmdOr = 313;

	//environment
	public static final int cmdMsg = 401;
	public static final int cmdCMsg = 402;
	public static final int cmdSMsg = 403;
	public static final int cmdSCMsg = 404;
	public static final int cmdAddPlanPosition = 405;
	public static final int cmdAddPlanPosition1 = 406;
	public static final int cmdAddPlanPosition2 = 407;
	public static final int cmdAddPlanPositionName = 408;
	public static final int cmdShowFinish = 409;

	public MissionCommand() {
	}

	public MissionCommand(int type) {
		this.type = type;
	}

	public MissionCommand(int type, String strValue) {
		this.type = type;
		this.strValue = strValue;
	}

	public MissionCommand(int type, int iValue) {
		this.type = type;
		this.iValue = iValue;
	}
}