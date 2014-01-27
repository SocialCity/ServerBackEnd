package com.SocialCity.TwitterAnalysis;

//used to prepare the tweet for sentiment analysis

public class Tweet_Obj {
	private String tweet_text;
	
	public Tweet_Obj(String text){
		tweet_text = text;
	}

	public String get_Tweet_text() {
		return tweet_text;
	}

	public void set_Tweet_text(String tweet_text) {
		this.tweet_text = tweet_text;
	}

}
