package mission;

public class Procedure {
	public static final int procNormal = 0;
	public static final int procTime = 1;
	public static final int procPlace = 2;
	public static final int procPeriod = 3;

	public int iType = procTime;
	public int startPoint = 0;
	public String strName;
	int x = 0; //x or minutes or period in seconds or name
	int y = 0; //y or seconds
	int z = 0; //z
	int r = 0; //radius

	public Procedure (String strName) {
		this.iType = procNormal;
		this.strName = strName;
	}

	public Procedure (int x) {
		this.x = x;
		this.iType = procPeriod;
	}

	public Procedure (int x, int y) {
		this.x = x;
		this.y = y;
		this.iType = procTime;
	}

	public Procedure (int x, int y, int z, int r) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.r = r;
	}
}