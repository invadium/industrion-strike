package control;

public class Task {
	static public final int None = 0;
	static public final int Defend = 1;
	static public final int Attack = 2;
	static public final int FollowPlan = 3;
	static public final int SupportAttack = 4;

	public int iType = None;
	public int iTarget = -1;
	public int id = -1;
	public int iSecondaryTarget = -1;
	public int sid = -1;
	public boolean isActual = true;
	public boolean isSecondaryActual = true;

	public Task() {
	}

	public void setTask(int iType) {
		this.iType = iType;
	}
	
	public void setPrimaryNotActual() {
		iTarget = -1;
		isActual = false;
	}

	public void setSecondaryNotActual() {
		iSecondaryTarget = -1;
		isActual = false;
	}

	public int secondExists(boolean isPrimary) {
		if (isPrimary && iSecondaryTarget >=0 && isSecondaryActual) return 2;
		if (!isPrimary && iTarget >=0 && isActual) return 1;
		return 0;
	}
}