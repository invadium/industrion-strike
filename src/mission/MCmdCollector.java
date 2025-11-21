package mission;

public class MCmdCollector {
	public static final int maxProcedures = 256;
	public static final int maxCommands = 0xFFFF;

	public int cntProc = 0;
	public Procedure theProc[] = new Procedure[maxProcedures];
	public int cntCommands = 0;
	public MissionCommand theCommands[] = new MissionCommand[maxCommands];

	public MCmdCollector() {
		
	}

	public void addProc(Procedure newProcedure) throws InterpreterException {
		if (cntProc < maxProcedures && cntCommands < (maxCommands)) {
			theProc[cntProc] = newProcedure;
			newProcedure.startPoint = cntCommands;
			cntProc++;
		} else throw new InterpreterException("no room for the new procedure");
	}

	public void storeCmd(MissionCommand newCmd) throws InterpreterException {
		if (cntCommands < maxCommands) {
			theCommands[cntCommands] = newCmd;
			cntCommands++;
} else throw new InterpreterException("no room for the new command");
	}

	public int findProc(int min, int sec) {
		for (int i=0; i<cntProc; i++)
			if (theProc[i].iType == Procedure.procTime && min == theProc[i].x
				&& sec == theProc[i].y) return i;
		return -1;
	}
	
	public int findLabel(String strName, int curStart) {
			int res = -1;
			int cp = curStart;

			while (this.theCommands[cp].type != MissionCommand.cmdEndProc
				&& cp < this.cntCommands) {
					if (this.theCommands[cp].type == MissionCommand.cmdLabel
						&& this.theCommands[cp].strValue.equals(strName)) {
							res = cp;
							break;
					}
					cp++;
			}

			return res;
	}

	public int findPeriod(int time, int index) {
		index++;
		for (int i=index; i<cntProc; i++)
			if (theProc[i].iType == Procedure.procPeriod
				&& (time % theProc[i].x) == 0) return i;
		return -1;
	}

	public int findProc(String strName) {
		for (int i=0; i<cntProc; i++)
			if (theProc[i].iType == Procedure.procNormal
				&& theProc[i].strName.equals(strName)) return i;
		return -1;
	}
}