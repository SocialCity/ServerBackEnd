package com.SocialCity.TwitterAnalysis;

import com.SocialCity.TwitterAnalysis.Twokenizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WordScore {
	
	private Double val = null;
	private Double active = null;
	private Double image = null;
	private String word = null;
	
	private String W_Classification = null;

	public WordScore(String word, Double val, Double active, Double image){
		this.val = val;
		this.active = active;
		this.image = image;
		this.word = word;
	}
	
/*	public WordScore(Double val, Double active){
		this.val = val;
		this.active = active;
	} */
	
	public Double get_valience(){
		return val;
	}
	
	public String get_word(){
		return word;
	}
	
	public Double get_active(){
		return active;
	}
	
	public Double get_image(){
		return image;
	}

	
}
