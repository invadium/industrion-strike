package scene;

public class SceneException extends Exception{
	public String strDescr;
	private String strFileName;
	private int posLine;
	private int posChar;

	public SceneException (String descr, 
					String strFileName, int posLine, int posChar) {
		this.strDescr = descr;
		this.strFileName = strFileName;
		this.posLine = posLine;
		this.posChar = posChar;
	}

	public String toString() {
		return "reading mission in '" + strFileName + "' at " + (posLine+1) + "." + posChar + " \n"
		+ "error: "+ strDescr;
	}
}