package com.SocialCity.SocialFactor;

import com.SocialCity.TwitterAnalysis.Word_Stats;

public class Word {
	private String word;
	private double pleasantness;
	private double activation;
	private double imagery;
	private double frequency;
	
	public Word (String word) {
		setWord(word);
	}
	
	public Word (String word, double f, double p, double a, double i) {
		setWord(word);
		setPleasantness(p);
		setActivation(a);
		setImagery(i);
		setFrequency(f);
	}
	
	public Word (Word_Stats s) {
		word = s.get_word();
		pleasantness = s.get_valience_mean();
		activation = s.get_activity_mean();
		imagery = s.get_imagery_mean();
		frequency = s.get_Frequency();
	}
	
	public Word () {}
	
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	
	public double getPleasantness() {
		return pleasantness;
	}
	public void setPleasantness(double pleasantness) {
		this.pleasantness = pleasantness;
	}
	
	public double getActivation() {
		return activation;
	}
	public void setActivation(double activation) {
		this.activation = activation;
	}
	
	public double getImagery() {
		return imagery;
	}
	public void setImagery(double imagery) {
		this.imagery = imagery;
	}

	public double getFrequency() {
		return frequency;
	}

	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}
}
