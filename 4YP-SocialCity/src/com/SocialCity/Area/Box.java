package com.SocialCity.Area;

//small class used to check boundaries
public class Box {
	private double topLong;
	private double topLat;
	private double bottomLong;
	private double bottomLat;
	private String name;
	
	public Box(String name) {this.name = name;}
	
	public double getTopLong() {
		return topLong;
	}
	public void setTopLong(double topLong) {
		this.topLong = topLong;
	}
	public double getTopLat() {
		return topLat;
	}
	public void setTopLat(double topLat) {
		this.topLat = topLat;
	}
	public double getBottomLong() {
		return bottomLong;
	}
	public void setBottomLong(double bottomLong) {
		this.bottomLong = bottomLong;
	}
	public double getBottomLat() {
		return bottomLat;
	}
	public void setBottomLat(double bottomLat) {
		this.bottomLat = bottomLat;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
