package org.apio3;

import org.apio3.Types.Fandom;
import org.apio3.Types.Work;
import org.apio3.Types.WorkChapter;

import java.util.ArrayList;
import java.util.List;

public class ApiO3 {
	
	
	// Note that recent works do NOT have creation date. The date is set to Epoch.
	// Give tag and page, will return list of works
	// Cannot handle search queries
	public static Work[] GetListOfRecentWorks(String tag, int page){
		String url = "https://archiveofourown.org/tags/" + tag + "/works";
		if(page > 1)
			url = url + "?page=" + page;
		String rawHtml = http.Get(url);
		if(rawHtml == null)
			return null;
		return Interpreter.GetWorksList(rawHtml);
	}
	
	// Downloads all chapter data of a work, body and all
	public static WorkChapter[] DownloadWholeWork(String workId){
		
		String urlWork = "https://archiveofourown.org/works/" + workId;
		String rawHtml = http.Get(urlWork);
		if(rawHtml == null)
			return null;
		
		String urlNaviagtion = "https://archiveofourown.org/works/" + workId + "/navigate";
		String rawNavigation = http.Get(urlNaviagtion);
		if(rawNavigation == null)
			return null;
		WorkChapter[] chapterMetadatas = Interpreter.GetTitlesDatesID(rawNavigation);
		
		String urlDownload = Interpreter.GetDownloadUrl(rawHtml);
		String rawHtmlDownload = http.Get(urlDownload);
		if(rawHtmlDownload == null)
			return null;
		WorkChapter[] chapters = Interpreter.GetWorkContents(rawHtmlDownload);
		
		// Merge the two
		for(int i = 0; i < chapterMetadatas.length; i++){
			chapters[i].ChapterID = chapterMetadatas[i].ChapterID;
			chapters[i].WorkID = chapterMetadatas[i].WorkID;
			chapters[i].UploadDate = chapterMetadatas[i].UploadDate;
		}
		return chapters;
	}
	
	// gets all chapter metadata, does not download body + info
	public static WorkChapter[] GetChapterMetadatas(String workId){
		String urlNaviagtion = "https://archiveofourown.org/works/" + workId + "/navigate";
		String rawNavigation = http.Get(urlNaviagtion);
		if(rawNavigation == null)
			return null;
		return Interpreter.GetTitlesDatesID(rawNavigation);
	}
	
	// Gets all metadata of the work
	public static Work GetWorkMetadata(String workId){
		// Put view adult to prevent annoying screen
		System.out.println("Getting workid: " + workId);
		String urlWork = "https://archiveofourown.org/works/" + workId + "?view_adult=true";
		System.out.println("URL: " + urlWork);
		
		String rawHtml = null;
		for(int i = 0; i < 2; i++) {
			rawHtml = http.Get(urlWork);
			// I don't even know anymore.
			assert rawHtml != null;
			if(!rawHtml.contains("adult content"))
				break;
			if(i == 1)
				System.out.println("Fuck!");
		}
		
		return Interpreter.GetWorkMetadata(rawHtml, workId);
	}
	
	// Downloads one chapter based on chapter ID
	// Get chapter IDs via getchaptermetadatas
	// return false if fail
	// return true if works
	public static boolean DownloadSingleChapter(WorkChapter chapter){
		// /works/49788343/chapters/126257599
		String url = "https://archiveofourown.org/works/" + chapter.WorkID + "/chapters/" + chapter.ChapterID;
		String rawHtml = http.Get(url);
		if(rawHtml == null)
			return false;
		var chapterData = Interpreter.GetChapterContents(rawHtml);
		
		chapter.Title = chapterData.Title;
		chapter.StartNotes = chapterData.StartNotes;
		chapter.Summary = chapterData.Summary;
		chapter.Body = chapterData.Body;
		chapter.EndNotes = chapterData.EndNotes;
		return true;
	}
	
	public static WorkChapter DownloadSingleChapter(String id){
		String url = "https://archiveofourown.org/chapters/" + id + "?view_adult=true";
		String rawHtml = http.Get(url);
		if(rawHtml == null)
			return null;
		System.out.println(rawHtml);
		var chapterData = Interpreter.GetChapterContents(rawHtml);
		var chapter = new WorkChapter();
		
		chapter.Title = chapterData.Title;
		chapter.ChapterIndex = chapterData.ChapterIndex;
		chapter.StartNotes = chapterData.StartNotes;
		chapter.Summary = chapterData.Summary;
		chapter.Body = chapterData.Body;
		chapter.EndNotes = chapterData.EndNotes;
		return chapter;
	}
	
	public static Fandom[] GetAllFandoms(){
		String[] FandomLinks = {
				"Anime *a* Manga",
				"Books *a* Literature",
				"Cartoons *a* Comics *a* Graphic Novels",
				"Celebrities *a* Real People",
				"Movies",
				"Music *a* Bands",
				"Other Media",
				"Theater",
				"TV Shows",
				"Video Games",
				"Uncategorized Fandoms"
		};
		
		List<Fandom> fandoms = new ArrayList<>();
		
		for(String FandomLink : FandomLinks){
			String rawHtml = http.Get("https://archiveofourown.org/media/"+FandomLink+"/fandoms");
			if(rawHtml == null)
				return null;
			fandoms.addAll(Interpreter.GetFandoms(rawHtml));
		}
		return fandoms.toArray(new Fandom[0]);
	}
	
}
