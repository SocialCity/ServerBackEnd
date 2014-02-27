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
	private HashSet<String> Wordnet_noun_hashset = new HashSet<String>();
	private HashSet<String> Wordnet_adjective_hashset = new HashSet<String>();
	private HashSet<String> Wordnet_verb_hashset = new HashSet<String>();
	
	private ArrayList<HashSet> Category_hashsets = new ArrayList<HashSet>();
	private ArrayList<String> Category_names = new ArrayList<String>();

	private Twokenizer tw = null;

	public TwitterAnalyser(String DAL_file_path, String Wordnet_file_path, String Categories_file){
		//build the hash set used for analysis
		ArrayList<WordScore> DAL_scores = readDAL(DAL_file_path);
		ArrayList<String> wordnet_noun_words = readWordNet(Wordnet_file_path, "n");
		ArrayList<String> wordnet_verb_words = readWordNet(Wordnet_file_path, "v");
		ArrayList<String> wordnet_adjective_words = readWordNet(Wordnet_file_path, "a");
		
		buildHashMap(DAL_hashmap, DAL_scores);
		buildHashSet(Wordnet_noun_hashset, wordnet_noun_words);
		buildHashSet(Wordnet_verb_hashset, wordnet_verb_words);
		buildHashSet(Wordnet_adjective_hashset, wordnet_adjective_words);

		//get dynamic categories
		ArrayList<ArrayList<String>> Categories_text_list = readCategories(Categories_file);
		
		Iterator<ArrayList<String>> it_c = Categories_text_list.iterator();
		
		while (it_c.hasNext()){
			ArrayList<String> list = (ArrayList) it_c.next();
			Category_names.add(list.get(0));
			list.remove(0);
			HashSet hs = new HashSet();
			buildHashSet(hs, list);
			Category_hashsets.add(hs);
		}
		//build dynamic hash sets for categories
		
		
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
	private ArrayList<String> readWordNet(String file, String start_symbol){
		BufferedReader br;
		String line;
		ArrayList<String> words = new ArrayList<String>();

		//read in file and create arraylist of scores
		try{
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("#")){
					if (line.startsWith(start_symbol)){
					line = line.substring(1);
					line = squeezeWhitespace(line);
					words.add(line);
				//	System.out.println(line);
					}
				
				}
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return words;
	}
	
	private ArrayList<ArrayList<String>> readCategories(String file){
		BufferedReader br;
		String line;
		ArrayList<ArrayList<String>> categories = new ArrayList<ArrayList<String>>();
		String[] values = null;
		ArrayList<String> c = new ArrayList<String>();
		
		//read in file and create arraylist of scores
		try{
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("#")){
					line = squeezeWhitespace(line);
					
					//new category found
					if(line.startsWith("$")){
						if (c.size() > 0){
							categories.add(c);
						}
						c = new ArrayList<String>();
						c.add(line.substring(1).toLowerCase());
					}
					
					if(line.startsWith("!")){
						values = line.split(",");
						c.add(values[0].substring(1).toLowerCase());
						for (int i = 1; i < values.length; i ++)
						{
							c.add(values[i].toLowerCase());
						}
					}
				
				}
		}
			categories.add(c);
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return categories;
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
		ArrayList<W_Classification> classifications = new ArrayList<W_Classification>();
		ArrayList<String> category_classifications = new ArrayList<String>();
		WordScore result = null;
		//is a matched word in the DAL set
		if (DAL_hashmap.containsKey(token)){
			//word is in the map set, return the wordscore object held in the hashmap
			result =  DAL_hashmap.get(token);
		}
		// is a matched word in the Wordnet Core
		if (Wordnet_noun_hashset.contains(token)){
			classifications.add(W_Classification.WORDNET_NOUN);
		}
		if (Wordnet_verb_hashset.contains(token)){
			classifications.add(W_Classification.WORDNET_VERB);
		}
		if (Wordnet_adjective_hashset.contains(token)){
			classifications.add(W_Classification.WORDNET_ADJ);
		}
		//is a hastag
		if (token.startsWith("#")){
			classifications.add(W_Classification.HASHTAG);
		}
		//retweet
		if (token.equals("rt") || token.equals("retweet") || token.equals("retwet")|| token.equals("ret"))
		{
			classifications.add(W_Classification.RETWEET);
		}
		//directed @ tweet
		if (token.startsWith("@"))
		{
			classifications.add(W_Classification.ATSymbol);
		}
		
		//matched to categories hashsets
		
		Iterator<HashSet> cat_it = Category_hashsets.iterator(); 
		int j = 0;
		while(cat_it.hasNext()){
			if (cat_it.next().contains(token)){
				category_classifications.add(Category_names.get(j));
			}
			j++;
		}
		

		
		//generate output
		
		if(result == null){
			
				result = new WordScore(token, -1.0, -1.0, -1.0, W_Classification.UNMATCHED);
			
			if (classifications.size() >= 1){
				//got a classification (or more) but not DAL
				for (int i = 0; i<classifications.size();  i++)
				{
					result.add_classification(classifications.get(i));
				}	
			}
			if (category_classifications.size() >= 1){
				//got a classification (or more) but not DAL
				for (int i = 0; i<category_classifications.size();  i++)
				{
					result.add_category_classification(category_classifications.get(i));
				}	
			}
		}
		else{
			//return DAL plus any other classifications
			for (int i = 0; i<classifications.size();  i++)
				{
					result.add_classification(classifications.get(i));
				}
			for (int i = 0; i<category_classifications.size();  i++)
			{
				result.add_category_classification(category_classifications.get(i));
			}
		}
			
		return result;
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
		ArrayList<String> matched_DAL_words = new ArrayList<String>();
		ArrayList<String> matched_nouns = new ArrayList<String>();
		ArrayList<String> matched_verbs = new ArrayList<String>();
		ArrayList<String> matched_adjectives = new ArrayList<String>();

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
		
		double DAL_ratio = 0.0;
		
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
		ArrayList<String> category_classifications = new ArrayList<String>();
		boolean retweet = false;

		while (it_ws.hasNext()){
			total_count++;
			score = it_ws.next();
			// check if DAL word
			
			if (score.get_classification().contains(W_Classification.DAL_WORD)){
				DAL_count++;
				total_matched_words++;
				total_active = total_active + score.get_active();
				total_val = total_val + score.get_valience();
				total_image = total_image + score.get_image();
				matched_DAL_words.add(score.get_word());
			}
			// check if WordNet word
			if ((score.get_classification().contains(W_Classification.WORDNET_NOUN)))
			{
				matched_nouns.add(score.get_word());
				total_matched_words++;
			}
			if ((score.get_classification().contains(W_Classification.WORDNET_VERB)))
			{
				matched_verbs.add(score.get_word());
				total_matched_words++;
			}
			if ((score.get_classification().contains(W_Classification.WORDNET_ADJ)))
			{
				matched_adjectives.add(score.get_word());
				total_matched_words++;
			}
			
			if (score.get_classification().contains(W_Classification.HASHTAG)){
				hashtags.add(score.get_word());
				total_matched_words++;
			}
			if (score.get_classification().contains(W_Classification.RETWEET)){
				retweet = true;
				total_matched_words++;
			}
			if (score.get_classification().contains(W_Classification.ATSymbol)){
				AT_tags.add(score.get_word());
				total_matched_words++;
			}
			
			//check dynamic category matches
			for (int n = 0; n < Category_names.size(); n++){
				if (score.get_category_classification().contains(Category_names.get(n))){
					category_classifications.add(Category_names.get(n));
				}
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
			DAL_ratio = DAL_count / total_matched_words;
			dal_classification = DAL_Classification.VALID;

		}
		else // no DAL matches, invalid values for active, valience and imagery
		{
			avg_active = -1.0;
			avg_val = -1.0;
			avg_image = -1.0;
			DAL_ratio = 0;
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
			
		
		
				
		//build new tweet score objects and return them
		TweetScore ts =  new TweetScore(tweet, avg_val, avg_active, avg_image, matched_ratio, dal_classification,DAL_ratio, retweet);
		ts.set_matched_DAL_words(matched_DAL_words);
		ts.set_Matched_nouns(matched_nouns);
		ts.set_Matched_verbs(matched_verbs);
		ts.set_Matched_adjectives(matched_adjectives);
		ts.add_hashtags(hashtags);
		ts.add_at_tags(AT_tags);
		ts.set_category_matches(category_classifications);
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
		Tweet_Info_Bloc results_bloc = new Tweet_Info_Bloc(scores, Category_names);
		//add the scores to the bloc
			
		return results_bloc;
		
	}

}

	

