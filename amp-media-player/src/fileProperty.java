
public class fileProperty {
	private String title;
	private String filePath;
	private String length;
	
	findData finder;
	
	fileProperty(String t, String fp, String l) {
		title = t;
		filePath = fp;
		length = l;
	}
	
	//S
	public void setTitle(String file) {
		title = finder.getTitle(file);
	}
	
	public void setFilePath(String file) {
		filePath = file;
	}
	
	public void setLength(String file) {
		length = finder.getLength(file);
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public String getLength() {
		return length;
	}
	
	//toString included for debugging purposes
	public String toString() {
		return "<" + title + ", " + filePath + ", " + length + ">";
	}
	
	public static void main(String[] args) {
		
	}
}
