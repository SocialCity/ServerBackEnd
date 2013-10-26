package com.SocialCity.SocialFactor;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

//Simple object for the social factors of a location
public class SocialFactors {
	
	private ArrayList<String> location;
	private double crimeRate;
	private double housePrice;
	private double educationRating;
	private double transportRating;
	
	private double meanAge;
	private double drugRate;
	private double employmentRate;
	private double voteTurnout;

	private Emotion sentiment;
	static Gson gson = new Gson();
	
	public SocialFactors(String wardCode) {
		this.location = new ArrayList<String>();
		location.add(wardCode);
		crimeRate = -1;
		housePrice = -1;
		educationRating = -1;
		transportRating = -1;
		meanAge = -1;
		drugRate = -1;
		employmentRate = -1;
		voteTurnout = -1;
	}
	
	public static SocialFactors parseJSON(String location) {
		SocialFactors sF = gson.fromJson(location, SocialFactors.class);
		return sF;
	}
	
	public SocialFactors(DBObject readIn) {
		crimeRate = (double) readIn.get("crimeRate");
		housePrice = (double) readIn.get("housePrice");
		educationRating = (double) readIn.get("educationRating");
		transportRating = (double) readIn.get("transportRating");
		meanAge = (double) readIn.get("meanAge");
		drugRate = (double) readIn.get("drugRate");
		employmentRate = (double) readIn.get("employmentRate");
		voteTurnout = (double) readIn.get("voteTurnout");
		this.location = new ArrayList<String>();
		BasicDBObject locations = (BasicDBObject) readIn.get("locations");
		int i = 0;
		while (!(locations.getString("ward"+i) == null)){
			//System.out.println(locations.getString("ward"+i) + " " + i);
			location.add(locations.getString("ward"+i));
			i++;
		}
	} 
	
	public double getCrimeRate(){return crimeRate;}
	public double getHousePrice(){return housePrice;}
	public double getEducationRating(){return educationRating;}
	public double getTransportRating(){return transportRating;}
	public ArrayList<String> getLocation(){return location;}
	public Emotion getSentiment(){return sentiment;}
	
	public void setCrimeRate(double crimeRate){
		this.crimeRate = crimeRate;
	}
	
	public void setHousePrice(double housePrice){
		this.housePrice = housePrice;
	}
	
	public void setEducationRating(double educationRating) {
		this.educationRating = educationRating;
	}
	
	public void setTransportRating(double transportRating) {
		this.transportRating = transportRating;
	}
	
	public void setSentiment (Emotion sentiment) {
		this.sentiment = sentiment;
	}
	
	public void combineLocations(SocialFactors factors) {
		if (crimeRate == -1) {
			crimeRate = factors.getCrimeRate();
		}
		else if (factors.getCrimeRate() != -1) {
			crimeRate = (crimeRate + factors.getCrimeRate())/2;
		}
		
		if (housePrice == -1) {
			housePrice = factors.getHousePrice();
		}
		else if(factors.getHousePrice() != -1) {
			housePrice = (housePrice + factors.getHousePrice())/2;
		}
		
		if (educationRating == -1) {
			educationRating = factors.getEducationRating();
		}
		else if (factors.getEducationRating()!= -1) {
			educationRating = (educationRating + factors.getEducationRating())/2;
		}
		
		if (transportRating == -1) {
			transportRating = factors.getTransportRating();
		}
		else if (factors.getTransportRating() != -1) {
			transportRating = (transportRating + factors.getTransportRating())/2;
		}
		
		if (meanAge == -1) {
			meanAge= factors.getMeanAge();
		}
		else if (factors.getMeanAge() != -1) {
			meanAge = (meanAge+ factors.getMeanAge())/2;
		}
		
		if (drugRate== -1) {
			drugRate = factors.getDrugRate();
		}
		else if (factors.getDrugRate() != -1) {
			drugRate= (drugRate + factors.getDrugRate())/2;
		}
		
		if (employmentRate == -1) {
			employmentRate = factors.getEmploymentRate();
		}
		else if (factors.getEmploymentRate() != -1) {
			employmentRate = (employmentRate + factors.getEmploymentRate())/2;
		}
		
		if (voteTurnout == -1) {
			voteTurnout = factors.getVoteTurnout();
		}
		else if (factors.getVoteTurnout() != -1) {
			voteTurnout= (voteTurnout+ factors.getVoteTurnout())/2;
		}

		//sentiment.averageSentiments(factors.getSentiment());
		location.addAll(factors.getLocation());
	}
	
	public BasicDBObject getDBObject() {
		BasicDBObject factors = new BasicDBObject();
		BasicDBObject locations = new BasicDBObject();
		
		for (int i = 0; i<location.size(); i++) {
			locations.append("ward"+i, location.get(i));
		}
		
		factors.append("locations", locations);
		factors.append("educationRating", educationRating);
		factors.append("transportRating", transportRating);
		factors.append("housePrice", housePrice);
		factors.append("crimeRate", crimeRate);
		factors.append("meanAge", meanAge);
		factors.append("employmentRate", employmentRate);
		factors.append("drugRate", drugRate);
		factors.append("voteTurnout", voteTurnout);
		return factors;
	}

	public double getMeanAge() {
		return meanAge;
	}

	public void setMeanAge(double meanAge) {
		this.meanAge = meanAge;
	}

	public double getEmploymentRate() {
		return employmentRate;
	}

	public void setEmploymentRate(double employmentRate) {
		this.employmentRate = employmentRate;
	}

	public double getVoteTurnout() {
		return voteTurnout;
	}

	public void setVoteTurnout(double voteTurnout) {
		this.voteTurnout = voteTurnout;
	}

	public double getDrugRate() {
		return drugRate;
	}

	public void setDrugRate(double drugRate) {
		this.drugRate = drugRate;
	}
}
