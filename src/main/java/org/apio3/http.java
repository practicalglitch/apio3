package org.apio3;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class http {
	public static String Get(String URL){
		try {
			URL url = new URL(URL);

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}

			reader.close();
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
