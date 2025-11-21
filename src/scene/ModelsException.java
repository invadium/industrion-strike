package scene;

public class ModelsException extends Exception{
	public int iType;
	public String strDescr;
	private String strFileName;
	private int posLine;
	private int posChar;

	public ModelsException (int iType, String descr, 
					String strFileName, int posLine, int posChar) {
		this.iType = iType;
		this.strDescr = descr;
		this.strFileName = strFileName;
		this.posLine = posLine;
		this.posChar = posChar;
	}

	public String toString() {
		return "reading model in '" + strFileName + "' at " + (posLine+1) + "." + posChar + " \n"
		+ "error #" + iType + ": "+ strDescr;
	}
}