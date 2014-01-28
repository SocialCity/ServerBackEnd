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
	private double transportRating;
	private double drugRate;
	private double unemploymentRate;
	private double incapacityBenefit;

	private Emotion sentiment;
	private double childInNoWorkHouse;
	private double schoolAbscences;
	private double GCSEScore;
	private double deliberateFires;
	private double incomeSupport;
	
	static Gson gson = new Gson();
	
	public SocialFactors(String wardCode) {
		this.location = new ArrayList<String>();
		location.add(wardCode);
		crimeRate = -1;
		housePrice = -1;
		transportRating = -1;
		drugRate = -1;
		unemploymentRate = -1;
		incapacityBenefit = -1;
		childInNoWorkHouse = -1;
		schoolAbscences = -1;
		GCSEScore = -1;
		deliberateFires = -1;
		incomeSupport = -1;
	}
	
	public static SocialFactors parseJSON(String location) {
		SocialFactors sF = gson.fromJson(location, SocialFactors.class);
		return sF;
	}
	
	public SocialFactors(DBObject readIn) {
		crimeRate = (double) readIn.get("crimeRate");
		housePrice = (double) readIn.get("housePrice");
		transportRating = (double) readIn.get("transportRating");
		drugRate = (double) readIn.get("drugRate");
		unemploymentRate = (double) readIn.get("unemploymentRate");
		incapacityBenefit = (double) readIn.get("incapacityBenefit");
		childInNoWorkHouse = (double) readIn.get("childInNoWorkHouse");
		schoolAbscences = (double) readIn.get("schoolAbscences");
		GCSEScore = (double) readIn.get("GCSEScore");
		deliberateFires = (double) readIn.get("deliberateFires");
		incomeSupport = (double) readIn.get("incomeSupport");
		
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
	public double getTransportRating(){return transportRating;}
	
	public ArrayList<String> getLocation(){return location;}
	public Emotion getSentiment(){return sentiment;}
	
	public void setCrimeRate(double crimeRate){
		this.crimeRate = crimeRate;
	}
	
	public void setHousePrice(double housePrice){
		this.housePrice = housePrice;
	}
	
	public void setTransportRating(double transportRating) {
		this.transportRating = transportRating;
	}
	
	public void setSentiment (Emotion sentiment) {
		this.sentiment = sentiment;
	}
	
	public double getFactorValue(int factorNumber) {
		switch (factorNumber) {
		case 0: return this.crimeRate;
		case 1: return this.housePrice;
		case 2: return this.GCSEScore;
		case 3: return this.transportRating;
		case 4: return this.schoolAbscences;
		case 5: return this.drugRate;
		case 6: return this.unemploymentRate;
		case 7: return this.childInNoWorkHouse;
		case 8: return this.deliberateFires;
		case 9: return this.incapacityBenefit;
		case 10: return this.incomeSupport;
		default: return -1;
		}
	}
	
	public String getFactorName(int factorNumber) {
		switch (factorNumber) {
		case 0: return "crimeRate";
		case 1: return "housePrice";
		case 2: return "GCSEScore";
		case 3: return "transportRating";
		case 4: return "schoolAbscences";
		case 5: return "drugRate";
		case 6: return "unemploymentRate";
		case 7: return "childInNoWorkHouse";
		case 8: return "deliberateFires";
		case 9: return "incapacityBenefit";
		case 10: return "incomeSupport";
		default: return "";
		}
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
		
		if (transportRating == -1) {
			transportRating = factors.getTransportRating();
		}
		else if (factors.getTransportRating() != -1) {
			transportRating = (transportRating + factors.getTransportRating())/2;
		}
		
		if (drugRate== -1) {
			drugRate = factors.getDrugRate();
		}
		else if (factors.getDrugRate() != -1) {
			drugRate= (drugRate + factors.getDrugRate())/2;
		}
		
		if (unemploymentRate == -1) {
			unemploymentRate = factors.getUnemploymentRate();
		}
		else if (factors.getUnemploymentRate() != -1) {
			unemploymentRate = (unemploymentRate + factors.getUnemploymentRate())/2;
		}
		
		if (incomeSupport == -1) {
			incomeSupport = factors.getIncomeSupport();
		}
		else if (factors.getIncomeSupport() != -1) {
			incomeSupport = (incomeSupport + factors.getIncomeSupport())/2;
		}
		
		if (incapacityBenefit == -1) {
			incapacityBenefit = factors.getIncapacityBenefit();
		}
		else if (factors.getIncapacityBenefit() != -1) {
			incapacityBenefit = (incapacityBenefit + factors.getIncapacityBenefit())/2;
		}

		if (GCSEScore == -1) {
			GCSEScore = factors.getGCSEScore();
		}
		else if (factors.getGCSEScore() != -1) {
			GCSEScore = (GCSEScore + factors.getGCSEScore())/2;
		}

		if (childInNoWorkHouse == -1) {
			childInNoWorkHouse = factors.getChildInNoWorkHouse();
		}
		else if (factors.getChildInNoWorkHouse() != -1) {
			childInNoWorkHouse = (childInNoWorkHouse + factors.getChildInNoWorkHouse())/2;
		}
		
		if (schoolAbscences == -1) {
			schoolAbscences = factors.getSchoolAbscences();
		}
		else if (factors.getSchoolAbscences() != -1) {
			schoolAbscences = (schoolAbscences + factors.getSchoolAbscences())/2;
		}
		
		if (deliberateFires == -1) {
			deliberateFires = factors.getDeliberateFires();
		}
		else if (factors.getDeliberateFires() != -1) {
			deliberateFires = (deliberateFires + factors.getDeliberateFires())/2;
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
		factors.append("transportRating", transportRating);
		factors.append("housePrice", housePrice);
		factors.append("crimeRate", crimeRate);
		factors.append("unemploymentRate", unemploymentRate);
		factors.append("drugRate", drugRate);
		return factors;
	}

	public double getUnemploymentRate() {
		return unemploymentRate;
	}

	public void setUnemploymentRate(double unemploymentRate) {
		this.unemploymentRate = unemploymentRate;
	}

	public double getDrugRate() {
		return drugRate;
	}

	public void setDrugRate(double drugRate) {
		this.drugRate = drugRate;
	}

	public void setIncapacityBenefit(double incapacityBenefit) {
		this.incapacityBenefit = incapacityBenefit;
		
	}
	
	public double getIncapacityBenefit() {
		return incapacityBenefit;
	}

	public void setIncomeSupport(double incomeSupport) {
		this.incomeSupport = incomeSupport;
		
	}
	
	public double getIncomeSupport() {
		return incomeSupport;
		
	}

	public void setDeliberateFires(double deliberateFires) {
		this.deliberateFires = deliberateFires;
		
	}
	
	public double getDeliberateFires() {
		return deliberateFires;
		
	}

	public double getGCSEScore() {
		return GCSEScore;
		
	}
	
	public void setGCSEScore(double GCSEScore) {
		this.GCSEScore = GCSEScore;
		
	}

	public void setSchoolAbscences(double schoolAbscences) {
		this.schoolAbscences = schoolAbscences;
		
	}

	public double getSchoolAbscences() {
		return schoolAbscences;
		
	}
	
	public void setChildInNoWorkHouse(double childInNoWorkHouse) {
		this.childInNoWorkHouse = childInNoWorkHouse;
		
	}
	
	public double getChildInNoWorkHouse() {
		return childInNoWorkHouse;
		
	}
}
