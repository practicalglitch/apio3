package org.apio3;


import org.apio3.Types.*;
import org.jsoup.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.io.Console;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Interpreter {
	
	public static DateTimeFormatter AO3ListDateFormat = DateTimeFormatter.ofPattern("d MMM yyyy");
	
	// Returns list of works given a URL that shows lists of works.
	public static Work[] GetWorksList(String html) {
		Document doc = Jsoup.parse(html);
		Elements articles = doc.getElementsByAttributeValue("role", "article");
		
		List<Work> Works = new ArrayList<>();
		
		for(Element article : articles) {
			Work work = new Work();
			
			//ID, Author, Title
			
			//"work_[id]" -> {"work", "[id]"} -> "[id]"
			work.Id = article.id().split("_")[1];
			Element author = article.getElementsByAttributeValue("rel", "author").first();
			if (author != null) {
				work.AuthorLoc = author.attributes().get("href");
				work.Author = author.text();
			} else {
				work.AuthorLoc = "";
				work.Author = "Anonymous";
			}
			
			work.Title = article.getElementsByAttributeValue("href", "/works/" + work.Id).first().text();
			
			Element stats = article.getElementsByClass("stats").first();
			
			// words, comments, kudos, bookmarks, hits, chapters
			FillBasicStats(stats, work);
			
			//Date Updated
			work.DateUpdated = LocalDate.parse(article.getElementsByClass("datetime").first().text(), AO3ListDateFormat).format(DateTimeFormatter.ISO_LOCAL_DATE);
			
			//Date published isnt fucking put here...
			work.DatePublished = LocalDate.EPOCH.format(DateTimeFormatter.ISO_LOCAL_DATE); // thisll have no consequences
			
			
			// Mandatory Tags
			
			Elements tags = article.getElementsByClass("required-tags").first().getElementsByAttribute("title");
			
			
			for(Element tag : tags) {
				String tagtext = tag.attributes().get("title");
				// Ratings
				if(Arrays.asList(Work.ValidRatings).contains(tagtext))
					work.Rating = tagtext;
				// Pairings
				// If in the case of one pairing...
				else if (Arrays.asList(Work.ValidPairings).contains(tagtext)) {
					work.Category = new String[1];
					work.Category[0] = tagtext;
				}
				// If in the case of multiple pairings...
				else if (tagtext.contains(", ")) {
					// Split by ", " and check if every part of it is indeed a pairing
					var possiblePairings = tagtext.split(", ");
					boolean isPairing = true;
					for(var possiblePairing : possiblePairings)
						if(!Arrays.asList(Work.ValidPairings).contains(possiblePairing))
							isPairing = false;
					if(isPairing){
						work.Category = possiblePairings;
						break;
					}
				}
				// Finished-ness
				else if(tagtext.equals("Work In Progress"))
					work.Finished = false;
				else if(tagtext.equals("Finished"))
					work.Finished = true;
			}
			
			
			
			Element tagParent = article.getElementsByClass("tags commas").first();
			Element warning = tagParent.getElementsByClass("warnings").first();

			
			work.Warning = warning.getElementsByAttribute("href").first().text();
			
			// Relationships, chars, freeforms
			
			work.Relationships = getTextArray(tagParent.getElementsByClass("relationships"));
			work.Characters = getTextArray(tagParent.getElementsByClass("characters"));
			work.Freeforms = getTextArray(tagParent.getElementsByClass("freeforms"));
			
			
			// Lang
			
			work.Language = stats.getElementsByAttribute("lang").text();
			
			
			//Fandoms
			
			Elements fandomElements = article.getElementsByClass("fandoms heading").first().getElementsByClass("tag");
			
			List<String> fandoms = new ArrayList<>();
			List<String> fandomsLoc = new ArrayList<>();
			
			for(Element fandomElement : fandomElements){
				fandoms.add(fandomElement.text());
				fandomsLoc.add(fandomElement.attributes().get("href"));
			}
			
			work.Fandoms = fandoms.toArray(new String[0]);
			work.FandomsLoc = fandomsLoc.toArray(new String[0]);
			
			// Summary
			
			work.Summary = article.getElementsByClass("userstuff summary").html();
			
			
			Works.add(work);
		}
		return Works.toArray(new Work[0]);
	}
	
	// htmlDownload from https://archiveofourown.org/downloads/[WorkID]/[title_name].html
	// Title name removes all non-alphanumeric and replaces spaces with _
	// Returns noted as "downloaded"
	public static WorkChapter[] GetWorkContents(String htmlDownload) {
		Document doc = Jsoup.parse(htmlDownload);
		
		
		Element chapterList = doc.getElementById("chapters");
		// I'm not really sure what to name this.
		// It's a collection of chapter elements, formatted, in order:
		// meta group -> title, summary, start notes
		// userstuff -> body
		// endnotes# -> end notes, optional
		// and repeat
		// it's awful but what can you do?
		
		List<WorkChapter> chapters = new ArrayList<>();
		
		for(Element element : chapterList.children()){
			if(element.className().equals("meta group")) {
				WorkChapter chap = new WorkChapter();
				chap.ChapterIndex = chapters.size() + 1; // not inserted yet so +1
				chap.Title = element.getElementsByClass("heading").first().text();
				
				// Both summary and notes are named "userstuff". Augh.
				var userstuff = element.getElementsByClass("userstuff");
				
				//If there's two, just assign first instance as summary, second as notes.
				if(!userstuff.isEmpty()){
					chap.Summary = userstuff.first().html();
					chap.StartNotes = userstuff.last().html();
					
					if(!element.text().contains("Chapter Summary")){
						chap.Summary = "";
					}
					// Have to add that second condition because
					// If you have end notes it puts that at the start....
					if (!element.text().contains("Chapter Notes") || (element.text().contains("Chapter Notes") && element.text().contains("See the end of the chapter for notes"))){
						chap.StartNotes = "";
					}
				} else {
					chap.Summary = "";
					chap.StartNotes = "";
				}
				chap.Downloaded = true;
				chapters.add(chap);
			}
			else if (element.className().equals("userstuff")){
				chapters.get(chapters.size() - 1).Body = element.html(); //get last added chapter, set body
			} else if(element.id().contains("endnotes")){
				// End notes come in form endnotes[chap num], indexed from 1.
				// This removes endnotes, parses to int, subtracts 1.
				int chapNo = Integer.parseInt(element.id().replace("endnotes", "")) - 1;
				chapters.get(chapNo).EndNotes = element.getElementsByClass("userstuff").first().html();
			}
		}
		return chapters.toArray(new WorkChapter[0]);
	}
	
	
	public static WorkChapter GetChapterContents(String htmlChapter){
		Document doc = Jsoup.parse(htmlChapter);
		
		WorkChapter chapter = new WorkChapter();
		
		chapter.Title = doc.getElementsByClass("title").last().text();
		
		chapter.ChapterIndex = Integer.parseInt(chapter.Title.split(" ")[1].replace(":", ""));
		chapter.Title = chapter.Title.replaceFirst("Chapter [0-9]*: ", "");
		
		chapter.Body = doc.getElementById("chapters").children().first().getElementsByAttributeValue("role", "article").first().html();
		//this hidden text is just randomly in the fucking chapter
		//regex remove!!! (<h3 class="landmark heading" id="work">Chapter Text</h3>)
		chapter.Body.replaceFirst("<h3 class=\"landmark heading\" id=\"work\">Chapter Text<\\/h3>", "");
		
		Elements prefaces = doc.getElementsByClass("chapter preface group");
		
		Element summary = prefaces.first().getElementById("summary");
		if(summary != null)
			chapter.Summary = summary.getElementsByClass("userstuff").first().html();
		Element startNotes = prefaces.first().getElementById("notes");
		if(startNotes != null) {
			// regex: <h3 class="heading">Notes:<\/h3>
			// see regex comment above... this one shows, tho.
			chapter.StartNotes = startNotes.html().replaceFirst("<h3 class=\"heading\">Notes:<\\/h3>", "");
		}
		if(prefaces.size() == 2)
			chapter.EndNotes = prefaces.last().getElementsByClass("userstuff").first().html();
		
		return chapter;
	}
	
	// htmlDownload from archiveofourown.org/works/[id]/navigate
	// Does not populate rest of the stuff - only title and date
	// Returns noted as "undownloaded"
	public static WorkChapter[] GetTitlesDatesID(String htmlNavigate){
		Document doc = Jsoup.parse(htmlNavigate);
		Elements elementChapters = doc.getElementsByClass("chapter index group").first().children();
		
		List<WorkChapter> workChapters = new ArrayList<>();
		
		for(Element eChap : elementChapters){
			WorkChapter chapter = new WorkChapter();
			chapter.Downloaded = false;
			chapter.Title = eChap.children().first().text().replaceFirst("[0-9]*?\\. ", "");
			chapter.ChapterIndex = workChapters.size() + 1;
			// returns /works/49788343/chapters/126257599 -> { "", "works", "49788343", "chapters", "126257599"}
			var splitUrl = eChap.children().first().attributes().get("href").split("/");
			chapter.ChapterID = splitUrl[4];
			chapter.WorkID = splitUrl[2];
			chapter.UploadDate = LocalDate.parse(eChap.children().last().text().replace("(", "").replace(")", "")).format(DateTimeFormatter.ISO_LOCAL_DATE);
			workChapters.add(chapter);
		}
		return  workChapters.toArray(new WorkChapter[0]);
	}
	
	public static String GetDownloadUrl(String htmlChapter){
		Document doc = Jsoup.parse(htmlChapter);
		//Get HTML download URL
		return "https://archiveofourown.org" + doc.getElementsByClass("expandable secondary").first().children().last().children().first().attributes().get("href");
	}
	
	public static List<Fandom> GetFandoms(String htmlFandomList){
		List<Fandom> fandoms = new ArrayList<>();
		Document doc = Jsoup.parse(htmlFandomList);
		// get fandom list name
		var category = doc.getElementsByClass("fandoms-index region").first().getElementsByClass("heading")
				.first().text().replace("Fandoms > ", "");
		// letterlists are just group of fandoms all starting w same letter
		var letterlists = doc.getElementsByClass("alphabet fandom index group ").first().children();
		for (Element letterlist : letterlists) {
			// get each fandom element in the letterlist
			var fandomElements = letterlist.getElementsByClass("tags index group").first().children();
			for (Element fandomElement : fandomElements) {
				Fandom fandom = new Fandom();
				// My Awesome Fandom (40)
				String fulltxt = fandomElement.text();
				fandom.Name = fandomElement.children().first().text();
				fandom.Url = fandomElement.children().first().attr("href").replace("/tags/", "").replace("/works", "");
				fandom.Category = category;
				String countText = fulltxt
						.replace(fandom.Name, "")
						.replace("(", "").replace(")", "").strip();
				if(!countText.isEmpty())
					fandom.WorksCount = Integer.parseInt(countText);
				else
					fandom.WorksCount = -1;
				fandoms.add(fandom);
			}
		}
		return fandoms;
	}
	
	
	// words, comments, kudos, bookmarks, hits, chapters
	private static void FillBasicStats(Element statsElement, Work work){
		//The stats are a PITA, because there are TWO elements with the same class. one is just the fucking label
		// Chapters
		var possibleChapterElements = statsElement.getElementsByClass("chapters");
		for(Element possibleChapterElement : possibleChapterElements){
			//Regex: "\d*\/(\?|\d*)". Matches "[#]/(? OR [#])"
			if(possibleChapterElement.text().matches("\\d*\\/(\\?|\\d*)")){ // TODO: Precompile this, perhaps
				var split = possibleChapterElement.text().split("/");
				work.ChaptersAvailable = Integer.parseInt(split[0]);
				if(Objects.equals(split[1], "?")) // Handle if tot chaps is unknown
					work.ChaptersTotal = -1;
				else
					work.ChaptersTotal = Integer.parseInt(split[1]);
			}
		}
		
		// Words
		for(Element element : statsElement.getElementsByClass("words"))
			if(!element.text().equals("Words:"))
				work.Words = Integer.parseInt(element.text().replace(",", ""));
		
		//Comments
		for(Element element : statsElement.getElementsByClass("comments"))
			if(!element.text().equals("Comments:"))
				work.Comments = Integer.parseInt(element.text().replace(",", ""));
		
		//Kudos
		for(Element element : statsElement.getElementsByClass("kudos"))
			if(!element.text().equals("Kudos:"))
				work.Kudos = Integer.parseInt(element.text().replace(",", ""));
		
		//Bookmarks
		for(Element element : statsElement.getElementsByClass("bookmarks"))
			if(!element.text().equals("Bookmarks:"))
				work.Bookmarks = Integer.parseInt(element.text().replace(",", ""));
		
		//Hits
		for(Element element : statsElement.getElementsByClass("hits"))
			if(!element.text().equals("Hits:"))
				work.Hits = Integer.parseInt(element.text().replace(",", ""));
	}
	
	// Gets all work data from a work url
	// Give it HTML of a work (archiveofourown.org/works/[id])
	public static Work GetWorkMetadata(String html, String id){
		
		//try {
			
			Work work = new Work();
			Document doc = Jsoup.parse(html);
			Element metaData = doc.getElementsByClass("work meta group").first();
			
			
			// this is cursed
			// it gets the "Entire Work" button, gets the url associated, and splits it up
			// Form of URl given: /works/[id]?view_full_work=true
			//
			//work.Id = doc.getElementsByClass("chapter entire").first().children().first().attributes().get("href").split("/", -1)[2].split("\\?")[0];
			work.Id = id;
			
			// Mandatory Tags
			
			work.Rating = metaData.getElementsByClass("rating tags").last().getElementsByClass("tag").first().text();
			
			work.Warning = metaData.getElementsByClass("warning tags").last().getElementsByClass("tag").first().text();
			
			work.Category = getTagToList(metaData.getElementsByClass("category tags"));
			
			//Fandoms
			
			Elements fandomElements = metaData.getElementsByClass("fandom tags").last().getElementsByClass("tag");
			
			List<String> fandoms = new ArrayList<>();
			List<String> fandomsLoc = new ArrayList<>();
			
			for (Element fandomElement : fandomElements) {
				fandoms.add(fandomElement.text());
				fandomsLoc.add(fandomElement.attributes().get("href"));
			}
			
			work.Fandoms = fandoms.toArray(new String[0]);
			work.FandomsLoc = fandomsLoc.toArray(new String[0]);
			
			work.Relationships = getTagToList(metaData.getElementsByClass("relationship tags"));
			if (work.Relationships == null)
				work.Relationships = new String[0];
			
			work.Characters = getTagToList(metaData.getElementsByClass("character tags"));
			if (work.Characters == null)
				work.Characters = new String[0];
			
			work.Freeforms = getTagToList(metaData.getElementsByClass("freeform tags"));
			if (work.Freeforms == null)
				work.Freeforms = new String[0];
			
			work.Language = metaData.getElementsByClass("language").last().text();
			
			// words, comments, kudos, bookmarks, hits, chapters
			UpdateWorkStats(work, html);
			
			Element preface = doc.getElementsByClass("preface group").first();
			
			
			work.Title = preface.getElementsByClass("title heading").first().text();
			Element author = preface.getElementsByAttributeValue("rel", "author").first();
			work.AuthorLoc = author.attributes().get("href");
			work.Author = author.text();
			
			work.Summary = preface.getElementsByClass("summary module").first().getElementsByClass("userstuff").first().html();
			
			return work;
		/*} catch (Exception e){
			System.out.println(e.toString());
			System.out.println(html);
			return null;
		}*/
	}
	
	// Updates work stats
	// Give it HTML of a work (archiveofourown.org/works/[id])
	// Returns true if work has been updated (Judged by update date)
	// Returns false if no changes
	public static boolean UpdateWorkStats(Work work, String htmlChapter){
		
		Document doc = Jsoup.parse(htmlChapter);
		
		Elements potentialStats = doc.getElementsByClass("stats");
		
		Element stats = null;
		// There are three elements with class stats.
		// One contains just the label stats
		// One is the parent of the third stats element
		// One actually has all the info under it in several children
		// So this is just filtering out the first two.
		for(Element potentialStat: potentialStats)
			if(potentialStat.children().size() > 1)
				stats = potentialStat;
		// This has zero chance to null throw, I'm sure.
		
		// words, comments, kudos, bookmarks, hits
		FillBasicStats(stats, work);
		
		//Date Updated
		for(Element element : stats.getElementsByClass("published"))
			if(!element.text().equals("Published:"))
				work.DatePublished = LocalDate.parse(element.text()).format(DateTimeFormatter.ISO_LOCAL_DATE);
		
		var oldUpdatedDate = work.DateUpdated;
		
		for(Element element : stats.getElementsByClass("status")) {
			if (!(element.text().equals("Updated:") || element.text().equals("Completed:")))
				work.DateUpdated = LocalDate.parse(element.text()).format(DateTimeFormatter.ISO_LOCAL_DATE);
			else if(element.text().equals("Updated:"))
				work.Finished = false;
			else if(element.text().equals("Completed:"))
				work.Finished = true;
		}
		
		if(oldUpdatedDate != work.DateUpdated)
			return true;
		return false;
	}
	
	public static String[] getTextArray(Elements elements){
		List<String> list = new ArrayList<>();
		for(Element element : elements)
			list.add(element.text());
		return list.toArray(new String[0]);
	}
	
	
	// Elements so i can just pass in the Elements list and check after if it actually exists
	private static String[] getTagToList(Elements parents){
		if(!parents.isEmpty()) { //sometimes it doesnt exist
			var tags = parents.last().getElementsByClass("tag");
			List<String> tagList = new ArrayList<>();
			for (Element tag : tags)
				tagList.add(tag.text());
			return tagList.toArray(new String[0]);
		}
		return null;
	}
	
	
}
