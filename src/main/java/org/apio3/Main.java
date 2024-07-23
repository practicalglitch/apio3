package org.apio3;

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
		
		
		//var work = ApiO3.GetWorkMetadata("55339249");
		//writeToFile(work.oneLineOutput(), "out/workmd_" + ("55339249") + ".txt");
		/*
		String Id = "41297265";
		var chapters = ApiO3.DownloadWholeWork(Id);
		StringBuilder str = new StringBuilder();
		for(WorkChapter chapter : chapters)
			str.append(chapter.oneLineExport());
		writeToFile(str.toString(), "out/workchapters_" + Id + ".txt");
		*/
		/*var metadatas = ApiO3.GetChapterMetadatas(work.Id);
		str = new StringBuilder();
		for(WorkChapter metadata : metadatas)
			str.append(metadata.oneLineExport());
		writeToFile(str.toString(), "out/workchmd_" + work.Id + ".txt");*/
		
		//var chap = ApiO3.DownloadSingleChapter("144958609");
		//writeToFile(chap.oneLineExport(), "workch3_" + "144958609" + ".txt");

		//if("/tags/Blue%20Archive%20(Video%20Game)/works".matches("\\/tags\\/.+\\/works"))
		//	System.out.println("ye");
		
		//var fandoms = ApiO3.GetAllFandoms();
		
		//StringBuilder builder = new StringBuilder();
		//for(Fandom fandom : fandoms) {
		//	builder.append(fandom.WorksCount).append(" | ").append(fandom.Name).append(" : ").append(fandom.Url).append(", ").append(fandom.Category).append("\n");
		//}
		//writeToFile(builder.toString(), "out/fandoms.txt");
		
		
		
		/*
		var work = ApiO3.GetWorkMetadata("55700539");
		writeToFile(work.oneLineOutput(), "out/workmd_" + work.Id + ".txt");*/
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


