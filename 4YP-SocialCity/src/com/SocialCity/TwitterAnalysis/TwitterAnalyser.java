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
	private HashMap<String, WordScore> DAL_map = new HashMap<String, WordScore>();
	private Twokenizer tw = null;

	public TwitterAnalyser(String file_path){
		//build the hash set used for analysis
		ArrayList<WordScore> word_scores = readDAL(file_path);
		buildHashSet(word_scores);
		//build twokenizer, squeeze whitespace and tokenize tweet
		tw = new Twokenizer();
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
			DAL_map.put(ws.get_word(), ws);
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
							Double.parseDouble(values[3]),
							W_Classification.DAL_WORD ));
				}
				
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return scores;
	}
	
	private WordScore analyse_token(String token){
		//analyse individual word/token
		
		//is a matched word in the DAL set
		if (DAL_map.containsKey(token)){
			//word is in the map set, return the wordscore object held in the hashmap
			return DAL_map.get(token);
		}
		//is a retweet indicator
		//is a hastag
		//is a noun
		else
			// no word in mapset, return invalid wordscore object
			return new WordScore(token, -1.0, -1.0, -1.0, W_Classification.UNMATCHED);
	}
	
	public TweetScore analyse_tweet(String tweet){
		//analyse a tweet
		
		//squeeze whitespace and tokenize tweet
		tweet = tw.squeezeWhitespace(tweet);
		
		//arraylist for holding the individual tokens
		ArrayList<String> tokens = new ArrayList<String>(tw.simpleTokenize(tweet));
		
		//arraylist for holding scores of individual tokens
		ArrayList<WordScore> token_scores = new ArrayList<WordScore>();
		
		//analyse all tokens for the tweet
		Iterator<String> it_tk = tokens.iterator();
		while (it_tk.hasNext()){
			token_scores.add(analyse_token(it_tk.next()));
		}
		
		//generate tweet score
		double avg_val = 0;
		double avg_active = 0;
		double avg_image = 0;
		double matched_ratio = 0;
		
		double total_val = 0;
		double total_active = 0;
		double total_image = 0;
		double total_matched_words = 0;
		
		WordScore score = null;
		int total_count = 0;
		int valid_count = 0;
		
		Iterator<WordScore> it_ws = token_scores.iterator();
		while (it_ws.hasNext()){
			total_count++;
			score = it_ws.next();
			//check if the wordscore object is invalid 
			if (score.get_classification() == W_Classification.UNMATCHED){
				continue;
			}
			//otherwise continue to total the scores
			else{
				valid_count++;
				total_active = total_active + score.get_active();
				total_val = total_val + score.get_valience();
				total_image = total_image + score.get_image();
				total_matched_words++;
			}				
		}
		
		//calculate averages
		avg_active = total_active / valid_count;
		avg_val = total_val / valid_count;
		avg_image = total_image / valid_count;
		matched_ratio = total_matched_words / total_count;

		
		//build new tweet score object and return it
		return new TweetScore(tweet, avg_val, avg_active, avg_image, matched_ratio);
	}
	
}
	

