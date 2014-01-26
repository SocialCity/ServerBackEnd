package com.SocialCity.DataParsers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CollectionReader {

	/**
	 * @param args
	 */
	public static String returnName(String collection) {
		String line = "";
		
		try {
			BufferedReader br = new BufferedReader(new FileReader("resources/databaseNames/"+collection+".txt"));
			line = br.readLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return line;
	}
	
	public static void editName(String collection, String newName) throws IOException {
		File file = new File("resources/databaseNames/"+collection+".txt");
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(newName);
		bw.close();
	}
	

}
