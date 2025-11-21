package control;

public class PlanPosition {
	public static final int Jump = -101;
	public static final int Name = -113;

	public String strTarget;
	public String strSecondaryTarget;
	public int iType = Task.None;
	public int iAgression = 1;
	public int iPlan = 0;
	public int iPos = 0;
	public int iSide = 0;

	public PlanPosition(int iSide, int iPlan, int iType, int iAgression) {
		this.iSide = iSide;
		this.iPlan = iPlan;
		this.iType = iType;
		this.iAgression = iAgression;
	}

	public PlanPosition(int iSide, int iPlan, int iType, 
						int iAgression, String strTarget) {
		this.iSide = iSide;
		this.iPlan = iPlan;
		this.iType = iType;
		this.iAgression = iAgression;
		this.strTarget = strTarget;
	}

	public PlanPosition(int iSide, int iPlan, int iType, int iAgression,
						String strTarget, String strSecondaryTarget) {
		this.iSide = iSide;
		this.iPlan = iPlan;
		this.iType = iType;
		this.iAgression = iAgression;
		this.strTarget = strTarget;
		this.strSecondaryTarget = strSecondaryTarget;
	}
}