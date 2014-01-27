package com.SocialCity.TwitterAnalysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.HashMap;

import com.SocialCity.TwitterAnalysis.WordScore;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TwitterAnalyser {
	
	private static Pattern Whitespace = Pattern.compile("\\s+");
	//hash map containing the DAL scores
	private HashMap<String, WordScore> DAL_hashmap = new HashMap<String, WordScore>();
	// hash set containing the words in the wordnet core dictionary
	private HashSet<String> Wordnet_hashset = new HashSet<String>();
	private Twokenizer tw = null;

	public TwitterAnalyser(String DAL_file_path, String Wordnet_file_path){
		//build the hash set used for analysis
		ArrayList<WordScore> DAL_scores = readDAL(DAL_file_path);
		ArrayList<String> wordnet_words = readWordNet(Wordnet_file_path);
		buildHashMap(DAL_hashmap, DAL_scores);
		buildHashSet(Wordnet_hashset, wordnet_words);
		//build twokenizer
		tw = new Twokenizer();
	}
		
		//remove large amounts of whitespace down to a single space
	public String squeezeWhitespace(String input) {
		Matcher whitespaceMatcher = Whitespace.matcher(input);
		return whitespaceMatcher.replaceAll(" ").trim();
	}
	
	private void buildHashSet(HashSet<String> hashset, ArrayList<String> words) {
		for (int i = 0; i < words.size(); i++){
			hashset.add(words.get(i));		}
	}
	
	//build the hash set used for word matching with the analyser
	private void buildHashMap(HashMap<String, WordScore> hashmap, ArrayList<WordScore> wordScores){
		WordScore ws;
		for (int i = 0; i < wordScores.size(); i++){
			ws = wordScores.get(i);
			hashmap.put(ws.get_word(), ws);
		}
	}
	
	//read in wordnet list and return an arraylist of words
	private ArrayList<String> readWordNet(String file){
		BufferedReader br;
		String line;
		ArrayList<String> words = new ArrayList<String>();

		//read in file and create arraylist of scores
		try{
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("#")){
					
					line = squeezeWhitespace(line);
					words.add(line);
				}
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return words;
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
		token = token.toLowerCase();

		//is a matched word in the DAL set
		if (DAL_hashmap.containsKey(token)){
			//word is in the map set, return the wordscore object held in the hashmap
			return DAL_hashmap.get(token);
		}
		// is a matched word in the Wordnet Core
		if (Wordnet_hashset.contains(token)){
			return new WordScore(token, -1.0, -1.0, -1.0, W_Classification.WORDNET);
		}
		//is a hastag
		if (token.startsWith("#")){
			return new WordScore(token, -1.0, -1.0, -1.0, W_Classification.HASHTAG);
		}
		//retweet
		if (token.equals("rt") || token.equals("retweet"))
		{
			return new WordScore(token, -1.0, -1.0, -1.0, W_Classification.RETWEET);
		}
		//directed @ tweet
		if (token.startsWith("@"))
		{
			return new WordScore(token, -1.0, -1.0, -1.0, W_Classification.ATSymbol);
		}
		
		else 			// no word in mapset, return invalid wordscore object
			return new WordScore(token, -1.0, -1.0, -1.0, W_Classification.UNMATCHED);
	}
	
	private TweetScore analyse_tweet(String tweet){
		//analyse a tweet

		//squeeze whitespace and tokenize tweet
		tweet = tw.squeezeWhitespace(tweet);
		
		//arraylist for holding the individual tokens
		ArrayList<String> tokens = new ArrayList<String>(tw.simpleTokenize(tweet));
		
		//arraylist for holding scores of individual tokens
		ArrayList<WordScore> token_scores = new ArrayList<WordScore>();
		
		//arraylist for holding matched words from the tweet
		ArrayList<String> matched_words = new ArrayList<String>();
		
		
		//*************************************************************************************//
		//*************** Put all tokens through the analyse_token method                   ***//
		//*************** Returning token_scores for all tokens (even if invalid/unmatched) ***//
		
		//analyse all tokens for the tweet
		Iterator<String> it_tk = tokens.iterator();
		while (it_tk.hasNext()){
			token_scores.add(analyse_token(it_tk.next()));
		}
		
		//********************************************************************//
		
		//generate tweet score
		double avg_val = 0.0;
		double avg_active = 0.0;
		double avg_image = 0.0;
		double matched_ratio = 0.0;
		
		double total_val = 0.0;
		double total_active = 0.0;
		double total_image = 0.0;
		double total_matched_words = 0.0;
		
		WordScore score = null;
		int total_count = 0;
		int DAL_count = 0;
		
		//*************************************************************************************************//
		// iteratre through tokens to generate activity, valience, imagery and matched words for the tweet //
		
		Iterator<WordScore> it_ws = token_scores.iterator();
		ArrayList<String> hashtags = new ArrayList<String>();
		ArrayList<String> AT_tags = new ArrayList<String>();
		boolean retweet = false;

		while (it_ws.hasNext()){
			total_count++;
			score = it_ws.next();
			// check if DAL word
			if (score.get_classification() == W_Classification.DAL_WORD){
				DAL_count++;
				total_matched_words++;
				total_active = total_active + score.get_active();
				total_val = total_val + score.get_valience();
				total_image = total_image + score.get_image();
				matched_words.add(score.get_word());
			}
			// check if WordNet word
			else if (score.get_classification() == W_Classification.WORDNET)
			{
				matched_words.add(score.get_word());
				total_matched_words++;
			}
			else if (score.get_classification() == W_Classification.HASHTAG){
				hashtags.add(score.get_word());
				total_matched_words++;
			}
			else if (score.get_classification() == W_Classification.RETWEET){
				retweet = true;
				total_matched_words++;
			}
			else if (score.get_classification() == W_Classification.ATSymbol){
				AT_tags.add(score.get_word());
				total_matched_words++;
				
			}
		}
		
		
		//*** calculate averages for this tweet ***/
		DAL_Classification dal_classification;

		//check numbers are valid
		if (DAL_count != 0 && total_active != 0.0)
		{
			//calculate averages
			avg_active = total_active / DAL_count;
			avg_val = total_val / DAL_count;
			avg_image = total_image / DAL_count;
			dal_classification = DAL_Classification.VALID;

		}
		else // no DAL matches, invalid values for active, valience and imagery
		{
			avg_active = -1.0;
			avg_val = -1.0;
			avg_image = -1.0;
			dal_classification = DAL_Classification.INVALID;

		}
		//check numbers are valid
		if (total_matched_words != 0)
		{
			matched_ratio = total_matched_words / total_count;
		}
		else // no matched words in this tweet
		{
			matched_ratio = 0.0;
		}
			
		
		
				
		//build new tweet score object and return it
		TweetScore ts =  new TweetScore(tweet, avg_val, avg_active, avg_image, matched_ratio, dal_classification, retweet);
		ts.add_matched_words(matched_words);
		ts.add_hashtags(hashtags);
		ts.add_at_tags(AT_tags);
		return ts;
	}

	public Tweet_Info_Bloc analyse_tweets(ArrayList<Tweet_Obj> tweets){
		
		//arraylist used for the scores of individual tweets
		ArrayList<TweetScore> scores = new ArrayList<TweetScore>(); 
	
		//analyse tweets
		String text = null;
		Iterator<Tweet_Obj> i = tweets.iterator();
		while (i.hasNext()){
			//pass and analyse the tweet
			scores.add(this.analyse_tweet(i.next().get_Tweet_text()));
		}
		
		//build and return bloc
		return build_bloc(scores);
	}
	
	private Tweet_Info_Bloc build_bloc(ArrayList<TweetScore> scores){
		
		// info bloc containing results
		Tweet_Info_Bloc results_bloc = new Tweet_Info_Bloc(scores);
		//add the scores to the bloc
			
		return results_bloc;
		
	}

}

	

