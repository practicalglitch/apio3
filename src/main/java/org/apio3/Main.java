package org.apio3;

import org.apio3.Types.Fandom;
import org.apio3.Types.Work;
import org.apio3.Types.WorkChapter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
	public static void main(String[] args) {
		
		/*var list = ApiO3.GetListOfRecentWorks("Blue%20Archive%20(Video%20Game)", 1);
		StringBuilder str = new StringBuilder();
		for(Work work : list)
			str.append(work.oneLineOutput());
		writeToFile(str.toString(), "out/worklist.txt");*/
		
		
		var work = ApiO3.GetWorkMetadata("54179023");
		writeToFile(work.oneLineOutput(), "out/workmd_" + ("54179023") + ".txt");
		
		/*var chapters = ApiO3.DownloadWholeWork(work.Id);
		str = new StringBuilder();
		for(WorkChapter chapter : chapters)
			str.append(chapter.oneLineExport());
		writeToFile(str.toString(), "out/workchapters_" + work.Id + ".txt");
		
		var metadatas = ApiO3.GetChapterMetadatas(work.Id);
		str = new StringBuilder();
		for(WorkChapter metadata : metadatas)
			str.append(metadata.oneLineExport());
		writeToFile(str.toString(), "out/workchmd_" + work.Id + ".txt");*/
		
		//var chap = ApiO3.DownloadSingleChapter("126161509");
		//writeToFile(chap.oneLineExport(), "out/workch3_" + "49788343" + ".txt");
		
		//var fandoms = ApiO3.GetAllFandoms();
		
		//StringBuilder builder = new StringBuilder();
		//for(Fandom fandom : fandoms) {
		//	builder.append(fandom.WorksCount).append(" | ").append(fandom.Name).append(" : ").append(fandom.Url).append(", ").append(fandom.Category).append("\n");
		//}
		//writeToFile(builder.toString(), "out/fandoms.txt");
		
		//var work = ApiO3.GetWorkMetadata("52388716");
		//writeToFile(work.oneLineOutput(), "out/workmd_" + work.Id + ".txt");
	}
	public static void writeToFile(String data, String path){
		try {
			File newTextFile = new File(path);

			FileWriter fw = new FileWriter(newTextFile);
			fw.write(data);
			fw.close();

		} catch (IOException iox) {
			//do stuff with exception
			iox.printStackTrace();
		}
	}
	
	public static String readFromFile(String path){
		try {
			return new String(Files.readAllBytes(Paths.get(path)));
		} catch (IOException iox) {
			iox.printStackTrace();
		}
		return null;
	}

}


