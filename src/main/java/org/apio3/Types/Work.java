package org.apio3.Types;

public class Work {
	public String Id;
	public String Title;
	public String Author;
	public String AuthorLoc;
	public String Rating;
	
	public static String[] ValidRatings = new String[] {
			"General Audiences",
			"Teen And Up Audiences",
			"Not Rated",
			"Explicit",
			"Mature"
	};
	public String[] Category;
	public static String[] ValidPairings = new String[] {
			"F/F",
			"F/M",
			"Gen",
			"M/M",
			"Multi",
			"Other",
			"No Category"
	};
	public String Warning;
	public boolean Finished;
	public String[] Relationships;
	public String[] Characters;
	public String[] Freeforms;
	public String Language;
	public String[] Fandoms;
	public String[] FandomsLoc;
	
	public String Summary;
	
	// stats
	
	//Supposed to be LocalDates but Gson SUCKS and cant serialize it
	public String DatePublished;
	public String DateUpdated;
	public int ChaptersAvailable;
	public int ChaptersTotal; // ? == -1
	public int Words;
	public int Comments;
	public int Kudos;
	public int Bookmarks;
	public int Hits;
	
	public WorkChapter[] Contents;

	public String oneLineOutput(){
		StringBuilder str = new StringBuilder();

		str.append(" Id: ").append(Id);
		str.append("\nTitle: ").append(Title);
		str.append("\nAuthor: ").append(Author);
		str.append("\nALoc: ").append(AuthorLoc);
		str.append("\nRating: ").append(Rating);
		str.append("\nPairing/Category: ");
		for (String paring : Category) str.append(paring).append(", ");
		str.append("\nWarning: ").append(Warning);
		str.append("\nFin?: ").append(Finished);
		str.append("\nRel: ");
		for (String relationship : Relationships) str.append(relationship).append(", ");
		str.append("\nChar: ");
		for (String character : Characters) str.append(character).append(", ");
		str.append("\nAddTags: ");
		for (String addtag : Freeforms) str.append(addtag).append(", ");
		str.append("\nLang: ").append(Language);
		str.append("\nFandoms: ");
		for (String fandom : Fandoms) str.append(fandom).append(", ");
		str.append("\nsum:\n").append(Summary);
		
		str.append("Stats\nChAv: ").append(ChaptersAvailable);
		str.append("\nChTot: ").append(ChaptersTotal);
		str.append("\nWords: ").append(Words);
		str.append("\nComments: ").append(Comments);
		str.append("\nKudos: ").append(Kudos);
		str.append("\nBookmarks: ").append(Bookmarks);
		str.append("\nHits: ").append(Hits);
		
		str.append("\nPublished: ").append(DatePublished.toString());
		str.append("\nUpdated: ").append(DateUpdated.toString());
		return str.toString();
	}
}
