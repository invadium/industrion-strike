package control;

import scene.SpaceObject;
import scene.Scene;

public class FlightPlan {
	Scene scene;
	public final int maxPositions = 200;

	public int cntPositions = 0;
	public PlanPosition Positions[] = new PlanPosition[maxPositions];

	public FlightPlan(Scene scene) {
		this.scene = scene;
	}

	public void add(PlanPosition position) {
		if (cntPositions < maxPositions) {
			Positions[cntPositions] = position;
			position.iPos = this.getMax(position.iSide, position.iPlan);
			cntPositions++;
		}
	}

	public void set(SpaceObject C, int i) {
		C.flightPlanPosition++;
		C.resetTarget();
		if (Positions[i].iType == Task.Attack || Positions[i].iType == Task.Defend) {
			C.currentTask.iType = Positions[i].iType;
			C.agressionLevel = Positions[i].iAgression;
			if (Positions[i].strTarget != null) {
				int index = scene.Objects.getIndexByName(Positions[i].strTarget);
				if (index == -1) C.currentTask.iTarget = -1;
				else {
					C.currentTask.iTarget = index;
					C.currentTask.id = scene.Objects.Objects[index].id;
					C.currentTask.isActual = true;
				}
			}
			if (Positions[i].strSecondaryTarget != null) {
				int index = scene.Objects.getIndexByName(Positions[i].strSecondaryTarget);
				if (index == -1) C.currentTask.iSecondaryTarget = -1;
				else {
					C.currentTask.iSecondaryTarget = index;
					C.currentTask.sid = scene.Objects.Objects[index].id;
					C.currentTask.isSecondaryActual = true;
				}
			}
		} else if (Positions[i].iType == PlanPosition.Jump) {
			//jump to the new flight plan position
			C.flightPlanPosition = Positions[i].iAgression;
		} else if (Positions[i].iType == PlanPosition.Name) {
		}
	}

	public int getMax(int iSide, int iPlan) {
		int resPos = 1;
		int i = 0;
		while (i < this.cntPositions) {
			if (Positions[i].iSide == iSide && Positions[i].iPlan == iPlan
				&& Positions[i].iPos >= resPos) resPos = Positions[i].iPos + 1;
			i++;
		}
		return resPos;
	}

	public int getNext(SpaceObject C) {
		if (C.flightPlan == -1) return -1;
		int res = -1;
		int nPos = C.flightPlanPosition + 1;
		int i = 0;
		while (i<cntPositions) {
			if (Positions[i].iSide == C.Side && Positions[i].iPlan == C.flightPlan
				&& Positions[i].iPos == nPos) res = i;
			i++;
		}
		return res;
	}

	public String getName(SpaceObject C) {
		if (C.flightPlan == -1) return "None";
		int res = -1;
		int i = 0;
		while (i<cntPositions) {
			if (Positions[i].iSide == C.Side && Positions[i].iPlan == C.flightPlan
				&& Positions[i].iType == PlanPosition.Name) res = i;
			i++;
		}
		if (res == -1) return "Plan #" + C.flightPlan;
		return Positions[res].strTarget;
	}

	public int getNextPlan(SpaceObject C) {
		int res = C.flightPlan + 1;
		if (res == 0) res = 1;
		int sres = -1;
		int i = 0;
		while (i < cntPositions) {
			if (Positions[i].iSide == C.Side && Positions[i].iPlan == res) sres = res;
			i++;
		}
		return sres;
	}

	public int getPrevPlan(SpaceObject C) {
		int res = C.flightPlan - 1;
		if (C.flightPlan == 0) return -1;
		int sres = -1;
		int i = 0;
		while (i < cntPositions) {
			if (Positions[i].iSide == C.Side && Positions[i].iPlan == res) sres = res;
			i++;
		}
		return sres;
	}
}