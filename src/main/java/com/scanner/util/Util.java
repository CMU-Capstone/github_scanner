package com.scanner.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import com.scanner.model.Repo;

/**
 * The utility for the project
 * */
public class Util {
	
	public static String convert(String path) {
		String prefix = "https://raw.githubusercontent.com/";
		String suffix = "/master/README.md";
		String content = path.substring(19);
		return prefix + content + suffix;
	}
	
	public static Repo buildRepo(String path) {
		URL url;
		int count = 0;
		String[] arr = new String[4];
		StringBuilder text = new StringBuilder();
		try {
			url = new URL(path);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String inputLine;
			while (count < 4) {
				inputLine = in.readLine();
				text.append(inputLine).append("\n");
				if (inputLine.length() == 0 || inputLine.startsWith("#")) {
					continue;
				}
				arr[count++] = inputLine;
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Repo repo = new Repo(arr[0], arr[1], arr[2], arr[3], text.toString());
		System.out.println(repo);
		return repo;
	}

}

