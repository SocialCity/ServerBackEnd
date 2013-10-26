package com.SocialCity.DataParsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.SocialCity.Area.Box;
//code adapted from: http://www.mkyong.com/java/how-to-read-and-parse-csv-file-in-java/
public class CvsParsing {

	public static void boundsParse() {
		String csvFile = "resources/london-wards.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		ArrayList<Box> wards = new ArrayList<Box>();
		ArrayList<Box> wardsCheck = new ArrayList<Box>();
		Box box;
		
		try {
			//create box for locations
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
			    // use comma as separator
				String[] country = line.split(cvsSplitBy);
				box = new Box(country[0].replaceAll("/t", "").replaceFirst(" Ward", ""));
				box.setBottomLat(Double.parseDouble(country[1].replaceAll("/t", "")));
				box.setBottomLong(Double.parseDouble(country[2].replaceAll("/t", "")));
				box.setTopLat(Double.parseDouble(country[3].replaceAll("/t", "")));
				box.setTopLong(Double.parseDouble(country[4].replaceAll("/t", "")));
				wards.add(box);
				wardsCheck.add(box);
			}
	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		//makes file with border lists
		try {
			FileWriter write = new FileWriter(new File("resources/bordersNames.txt"));
			
			for (Box b:wards) {
				write.write(b.getName() + "\n");
				for (Box b2:wardsCheck) {
					if (!(b.getName().equals(b2.getName()))) {
						if(b2.getBottomLat()<=b.getTopLat() && b2.getTopLat()>=b.getBottomLat() 
								&& b2.getTopLong()>=b.getBottomLong() && b2.getBottomLong()<=b.getTopLong()){
							write.write(b2.getName() + "\n");
						}
						else if (b.getBottomLat()<=b2.getTopLat() && b.getTopLat()>=b2.getBottomLat() 
								&& b.getTopLong()>=b2.getBottomLong() && b.getBottomLong()<=b2.getTopLong()) {
							write.write(b2.getName() + "\n");
						}
					
					}
				}
				write.write("---\n");
			}
			write.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
