package org.apio3.Types;

public class WorkChapter {
	public boolean Downloaded;
	public String ChapterID;
	public String WorkID;
	public int ChapterIndex;
	public String Title;
	public String Summary;
	public String StartNotes;
	public String Body;
	public String EndNotes;
	public int Words;
	
	public String UploadDate;
	
	
	public String oneLineExport(){
		StringBuilder str = new StringBuilder();
			str.append("\nChapter ").append(ChapterIndex).append(": ").append(Title);
			str.append("\n").append(UploadDate).append(", ").append(ChapterID);
			str.append("\n").append(Summary).append("\n").append(StartNotes);
			str.append("\n").append(Body);
			str.append("\n").append(EndNotes).append("\n");
		return str.toString();
	}
}
