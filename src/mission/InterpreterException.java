package mission;

public class InterpreterException extends Exception{
	public String strDescr;

	public InterpreterException (String descr) {
		this.strDescr = descr;
	}

	public String toString() {
		return "interpreter error: " + strDescr;
	}
}