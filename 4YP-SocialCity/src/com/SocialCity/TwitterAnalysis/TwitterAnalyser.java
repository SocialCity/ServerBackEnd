package com.SocialCity.TwitterAnalysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import com.SocialCity.TwitterAnalysis.WordScore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TwitterAnalyser {
	
	private static Pattern Whitespace = Pattern.compile("\\s+");
	private HashMap<String, WordScore> map = new HashMap<String, WordScore>();

	public TwitterAnalyser(String file_path){
		//build the hash set used for analysis
		ArrayList<WordScore> word_scores = readDAL(file_path);
		buildHashSet(word_scores);
	}
		//remove large amounts of whitespace down to a single space
	public String squeezeWhitespace(String input) {
		Matcher whitespaceMatcher = Whitespace.matcher(input);
		return whitespaceMatcher.replaceAll(" ").trim();
	}
	
	//build the hash set used for word matching with the analyser
	private void buildHashSet(ArrayList<WordScore> wordScores){
		WordScore ws;
		for (int i = 0; i < wordScores.size(); i++){
			ws = wordScores.get(i);
			map.put(ws.get_word(), ws);
		}
	}
	
	//read in the DAL file and return a set of word scores
	private ArrayList<WordScore> readDAL(String file){
		BufferedReader br;
		String line;
		ArrayList<WordScore> scores = new ArrayList();
		String[] values = null;
		
		//read in file and create arraylist of scores
		try{
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("#")){
					
					line = squeezeWhitespace(line);
					values = line.split(" ");
					//create score object
					scores.add(new WordScore(values[0], 
							Double.parseDouble(values[1]), 
							Double.parseDouble(values[2]), 
							Double.parseDouble(values[3])));
				}
				
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return scores;
	}
	
	private WordScore analyse_token(String token){
		
		return null;
	}
	
	public TweetScore analyse_tweet(ArrayList<String> tokens){
		
		return null;
	}
	
}
	

