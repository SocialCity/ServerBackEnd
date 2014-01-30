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
	
	private ArrayList<W_Classification> classifications = null;

	public WordScore(String word, Double val, Double active, Double image, W_Classification classification){
		classifications = new ArrayList<W_Classification>();
		this.val = val;
		this.active = active;
		this.image = image;
		this.word = word;
		this.classifications.add(classification);
	}
	
/*	public WordScore(Double val, Double active){
		this.val = val;
		this.active = active;
	} */
	
	public void add_classification(W_Classification classification){
		classifications.add(classification);
	}
	
	public ArrayList<W_Classification> get_classification(){
		return classifications;
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
