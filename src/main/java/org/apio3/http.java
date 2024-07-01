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
