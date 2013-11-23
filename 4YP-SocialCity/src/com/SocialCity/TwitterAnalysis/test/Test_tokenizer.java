package com.SocialCity.TwitterAnalysis.test;

import com.SocialCity.TwitterAnalysis.Twokenizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Test_tokenizer {

	public static void main(String[] args) {
	
		//testing twokenizer
		
				//find tweets
		ArrayList<String> tweets= getTweets();
		
		
		
		ArrayList<String> squeezed_tweets= new ArrayList<String>();
		ArrayList<List<String>> tokens= new ArrayList<List<String>>();
						
		Twokenizer twokenizer = new Twokenizer();		
				//tokenise tweets
		for (int i = 0; i < tweets.size(); i++){
			squeezed_tweets.add(twokenizer.squeezeWhitespace(tweets.get(i)));
			tokens.add(twokenizer.simpleTokenize(squeezed_tweets.get(i)));
		}
		
		for (int i =0; i<tokens.size(); i++){
			System.out.println("********* NEW TWEET *************");
			System.out.println(tweets.get(i));
			Iterator it = tokens.get(i).iterator();
			while (it.hasNext()){
			System.out.println(it.next());
			}
		}
		
		
				
	}
	
	public static ArrayList getTweets(){
		ArrayList<String> tweets= new ArrayList<String>();

		tweets.add("lolol :) this:D is): aXD tweet!");
		
		return tweets;
	}
}
