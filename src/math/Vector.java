package math;

/**
 * Represents vector in 3d space.
 * 
 * @author Igor Khotin
 *
 */
public class Vector {
	public double x, y, z;

	public Vector() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector(Vector vec) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
	}

	public void set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void set(Vector vec) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
	}

	public void zero() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public void shift(double sx, double sy, double sz) {
		x += sx;
		y += sy;
		z += sz;
	}

	public double getLength() {
		double length = x*x + y*y + z*z;
		length = CMath.M.sqrt(length);
		return length;
	}

	public boolean normalise() {
		double length = this.getLength();	
		if (length == 0) return false;
		x = x / length;
		y = y / length;
		z = z / length;
		return true;
	}

	public void add(Vector vec) {
		this.x += vec.x;
		this.y += vec.y;
		this.z += vec.z;
	}

	public void add(double x, double y, double z)
	{
		this.x += x;
		this.y += y;
		this.z += z;
	}

	public void sub(Vector vec) {
		this.x -= vec.x;
		this.y -= vec.y;
		this.z -= vec.z;
	}

	public void negative() {
		x = -x;
		y = -y;
		z = -z;
	}

	public void mul(double val) {
		this.x *= val;
		this.y *= val;
		this.z *= val;
	}

	public void mul(Vector vec) {
		double x = this.x;
		double y = this.y;
		double z = this.z;
		
		this.x = y * vec.z - z * vec.y;
		this.y = z * vec.x - x * vec.z;
		this.z = x * vec.y - y * vec.x;
	}

	public double dot(Vector vec) {
		double dot;
		dot = this.x * vec.x + this.y * vec.y + this.z * vec.z;
		return dot;
	}

	public void mul(Matrix43 mat) {
		double x = this.x;
		double y = this.y;
		double z = this.z;
		this.x = x * mat.vals[0][0] + y * mat.vals[1][0] + z * mat.vals[2][0] + mat.vals[3][0];
		this.y = x * mat.vals[0][1] + y * mat.vals[1][1] + z * mat.vals[2][1] + mat.vals[3][1];
		this.z = x * mat.vals[0][2] + y * mat.vals[1][2] + z * mat.vals[2][2] + mat.vals[3][2];
	}

	public void rotateOX(int alpha) {
		double nx, ny, nz;
		alpha %= (360*60);
		nx = x;
		ny = y * CMath.cos(alpha) - z * CMath.sin(alpha);
		nz = y * CMath.sin(alpha) + z * CMath.cos(alpha);	
		x = nx;
		y = ny;
		z = nz;
	}

	public void rotateOY(int beta) {
		double nx, ny, nz;
		beta %= (360*60);
		nx = x * CMath.cos(beta) + z * CMath.sin(beta);
		ny = y;
		nz = z * CMath.cos(beta) - x * CMath.sin(beta);
		x = nx;
		y = ny;
		z = nz;
	}

	public void rotateOZ(int gamma) {
		double nx, ny, nz;
		gamma %= (360*60);
		nx = x * CMath.cos(gamma) - y * CMath.sin(gamma);
		ny = x * CMath.sin(gamma) + y * CMath.cos(gamma);
		nz = z;
		x = nx;
		y = ny;
		z = nz;
	}

	public double getCosFi(Vector vec) {
		//get the cos of angle between vectors
		double cosFi;
		cosFi = (this.x * vec.x + this.y * vec.y + this.z * vec.z) 
				/ (this.getLength() * vec.getLength());
		return cosFi;
	}

	public double getCosFiN(Vector vec) {
		//get the cos of angle between vectors (for normal vectors)
		double cosFi;
		cosFi = (this.x * vec.x + this.y * vec.y + this.z * vec.z);
		return cosFi;
	}

	public int getAlpha() {
		int alpha;

		if (y == 0 && z == 0) return -1;
		else {
			double alpha_r = CMath.M.atan2(y, z);
			if (alpha_r < 0) alpha_r += (CMath.M.PI * 2);
			alpha = (int)(alpha_r / CMath.dFactor);
		}
		return alpha;
	}

	public int getBeta() {
		int beta;

		if (x == 0 && z == 0) return -1;
		else {
			double beta_r = CMath.M.atan2(x, z);
			if (beta_r < 0) beta_r += (CMath.M.PI * 2);
			beta = (int)(beta_r / CMath.dFactor);			
		}
		return beta;
	}

	public byte getSector() {
		byte sector = 1;
		if (x<0 && y<0 && z>=0) return (byte)sector;
		if (x>=0 && y<0 && z>=0) return (byte)(sector<<1);
		if (x<0 && y>=0 && z>=0) return (byte)(sector<<2);
		if (x>=0 && y>=0 && z>=0) return (byte)(sector<<3);
		if (x<0 && y<0 && z<0) return (byte)(sector<<4);
		if (x>=0 && y<0 && z<0) return (byte)(sector<<5);
		if (x<0 && y>=0 && z<0) return (byte)(sector<<6);
		if (x>=0 && y>=0 && z<0) return (byte)(sector<<7);
		return 0;
	}
}
