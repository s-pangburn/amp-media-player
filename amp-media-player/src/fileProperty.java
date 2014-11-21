
public class fileProperty {
	private String title;
	private String filePath;
	private String time;
	
	fileProperty(String t, String fp, String tm) {
		title = t;
		filePath = fp;
		time = tm;
	}
	
	public void setTitle(String t) {
		title = t;
	}
	
	public void setFilePath(String fp) {
		filePath = fp;
	}
	
	public void setTime(String tm) {
		time = tm;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public String getTime() {
		return time;
	}
	
	//toString included for debugging purposes
	public String toString() {
		return "<" + title + ", " + filePath + " - " + time + ">";
	}
}
