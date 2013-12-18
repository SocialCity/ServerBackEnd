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
	
	private W_Classification classification = null;

	public WordScore(String word, Double val, Double active, Double image, W_Classification classification){
		this.val = val;
		this.active = active;
		this.image = image;
		this.word = word;
		this.classification = classification;
	}
	
/*	public WordScore(Double val, Double active){
		this.val = val;
		this.active = active;
	} */
	
	public W_Classification get_classification(){
		return classification;
	}
	
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
