package org.apio3;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class http {
	public static String Get(String URL){
		String content = null;
		URLConnection connection = null;
		try {
			connection =  new URL(URL).openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36");
			Scanner scanner = new Scanner(connection.getInputStream());
			scanner.useDelimiter("\\Z");
			content = scanner.next();
			scanner.close();
		}catch ( Exception ex ) {
			ex.printStackTrace();
		}
		return content;
	}
}
