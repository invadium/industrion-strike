package math;

/**
 * Represents 4x3 matrix.
 * Used in 3d transformation operations.
 * 
 * @author Igor Khotin
 *
 */
public class Matrix43 {
	public double vals[][];

	public Matrix43() {
		vals = new double[4][3];
		this.unit();
	}

	public Matrix43(Matrix43 mat) {
		vals = new double[4][3];
		this.copy(mat);
	}

	public void unit() {
		for(int i=0; i<4; i++)
		  for(int j=0; j<3; j++) vals[i][j] = 0;
		vals[0][0] = 1;
		vals[1][1] = 1;
		vals[2][2] = 1;
	}

	public void copy(Matrix43 mat) {
		for(int i=0; i<4; i++)
		  for(int j=0; j<3; j++) this.vals[i][j] = mat.vals[i][j];
	}

	public void setRow(int row, Vector vec) {
		vals[0][row] = vec.x;
		vals[1][row] = vec.y;
		vals[2][row] = vec.z;
	}

	public void setRotateOX(int alpha) {
		vals[0][0] = 1;
		vals[0][1] = 0;
		vals[0][2] = 0;

		vals[1][0] = 0;
		vals[1][1] = CMath.cos(alpha);
		vals[1][2] = -CMath.sin(alpha);
		
		vals[2][0] = 0;
		vals[2][1] = CMath.sin(alpha);
		vals[2][2] = CMath.cos(alpha);
	}

	public void setRotateOY(int beta) {
		vals[0][0] = CMath.cos(beta);
		vals[0][1] = 0;
		vals[0][2] = -CMath.sin(beta);

		vals[1][0] = 0;
		vals[1][1] = 1;
		vals[1][2] = 0;
		
		vals[2][0] = CMath.sin(beta);
		vals[2][1] = 0;
		vals[2][2] = CMath.cos(beta);
	}

	public void setRotateOZ(int gamma) {
		vals[0][0] = CMath.cos(gamma);
		vals[0][1] = -CMath.sin(gamma);
		vals[0][2] = 0;

		vals[1][0] = CMath.sin(gamma);
		vals[1][1] = CMath.cos(gamma);
		vals[1][2] = 0;
		
		vals[2][0] = 0;
		vals[2][1] = 0;
		vals[2][2] = 1;
	}

	public void setTrans(double x, double y, double z) {
		vals[3][0] = x;
		vals[3][1] = y;
		vals[3][2] = z;
	}

	public void affineInverse() {
		Matrix43 mat = new Matrix43();
		mat.copy(this);
		
		this.vals[0][0] = mat.vals[0][0];
		this.vals[0][1] = mat.vals[1][0];
		this.vals[0][2] = mat.vals[2][0];
		
		this.vals[1][0] = mat.vals[0][1];
		this.vals[1][1] = mat.vals[1][1];
		this.vals[1][2] = mat.vals[2][1];

		this.vals[2][0] = mat.vals[0][2];
		this.vals[2][1] = mat.vals[1][2];
		this.vals[2][2] = mat.vals[2][2];		
	}

	public void add(Matrix43 mat) {
		for(int i=0; i<4; i++)
		  for(int j=0; j<3; j++) this.vals[i][j] += mat.vals[i][j];
	}

	public void mul(double val) {
		for(int i=0; i<4; i++)
		  for(int j=0; j<3; j++) this.vals[i][j] *= val;
	}

   	public void mul(Matrix43 mat) {
		Matrix43 m = new Matrix43(this);
		Vector v = new Vector(this.vals[3][0], this.vals[3][1], this.vals[3][2]);
		
		v.mul(mat);
		
		this.vals[0][0]	=	m.vals[0][0]*mat.vals[0][0] + m.vals[0][1]*mat.vals[1][0] + m.vals[0][2]*mat.vals[2][0];
		this.vals[0][1]	=	m.vals[0][0]*mat.vals[0][1] + m.vals[0][1]*mat.vals[1][1] + m.vals[0][2]*mat.vals[2][1];
		this.vals[0][2]	=	m.vals[0][0]*mat.vals[0][2] + m.vals[0][1]*mat.vals[1][2] + m.vals[0][2]*mat.vals[2][2];
		
		this.vals[1][0]	=	m.vals[1][0]*mat.vals[0][0] + m.vals[1][1]*mat.vals[1][0] + m.vals[1][2]*mat.vals[2][0];
		this.vals[1][1]	=	m.vals[1][0]*mat.vals[0][1] + m.vals[1][1]*mat.vals[1][1] + m.vals[1][2]*mat.vals[2][1];
		this.vals[1][2]	=	m.vals[1][0]*mat.vals[0][2] + m.vals[1][1]*mat.vals[1][2] + m.vals[1][2]*mat.vals[2][2];
		                                                                       
		this.vals[2][0]	=	m.vals[2][0]*mat.vals[0][0] + m.vals[2][1]*mat.vals[1][0] + m.vals[2][2]*mat.vals[2][0];
		this.vals[2][1]	=	m.vals[2][0]*mat.vals[0][1] + m.vals[2][1]*mat.vals[1][1] + m.vals[2][2]*mat.vals[2][1];
		this.vals[2][2]	=	m.vals[2][0]*mat.vals[0][2] + m.vals[2][1]*mat.vals[1][2] + m.vals[2][2]*mat.vals[2][2];
		
		this.vals[3][0]	=	v.x + mat.vals[3][0];
		this.vals[3][1]	=	v.y + mat.vals[3][1];
		this.vals[3][2]	=	v.z + mat.vals[3][2];
	}
}