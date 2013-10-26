package com.SocialCity.Server;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.SocialCity.Area.BoundaryMap;
import com.SocialCity.SocialFactor.SocialFactors;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
//Forms any query responses and returns the JSON of it
public class ResponseMaker {

	public String oneFactor(int factorNumber, boolean useWards) {
		
		MongoClient mongoClient;
		HashMap<String, ArrayList<String>> nameMap = BoundaryMap.returnWardMap(useWards);//gets either the ward or borough location data
		ArrayList<SocialFactors> listOfData = new ArrayList<SocialFactors>();//stores the info to return
		ArrayList<String> neighbours;
		DBCollection coll = null;
		DBCursor locales;
		SocialFactors sF;
		SocialFactors friend;
		BasicDBObject query;
		HashSet<String> checked = new HashSet<String>();
		HashSet<String> toCheck = new HashSet<String>();
		HashSet<String> tempChecked = new HashSet<String>();
		boolean recheck = true;
		Gson gson = new Gson();
		
		try {
			//gets the database
			mongoClient = new MongoClient("localhost");
			//mongoClient.dropDatabase("areas");
			DB db = mongoClient.getDB( "areas" );
			
			if (useWards){//get ward info
				coll = db.getCollection("wards");
			}
			else {//get borough info
				coll = db.getCollection("boroughs");
				System.out.println(coll.getCount());
			}
			
			locales = coll.find();
			
			while(locales.hasNext()){//go through every location
				sF = new SocialFactors(locales.next());
				if (!(checked.contains(sF.getLocation().get(0)))) {//make sure location hasnt been checked yet
					neighbours = nameMap.get(sF.getLocation().get(0));
					tempChecked.addAll(neighbours);//add bordering locales for checking
					while (recheck) {//means locations where combined and new neighbours need to be checked
						recheck = false;
						for (String n : neighbours) {
							//check the neighbour is not the current location or in checked
							if (!(n.equals(sF.getLocation().get(0))) && !(checked.contains(n))) {
								//gets new neighbour
								query = new BasicDBObject("locations", new BasicDBObject("ward0", n));
								friend = new SocialFactors (coll.findOne(query));
								//check if the two locations need to be combined
								if (combine(sF, friend, factorNumber)){
									recheck = true;
									//add new neighbours to list to check next iteration
									toCheck.addAll(nameMap.get(friend.getLocation().get(0)));
								}
							}
						}
						
						//remove any neighbours that where just checked
						toCheck.removeAll(tempChecked);
						neighbours.clear();
						neighbours.addAll(toCheck);//get new list to check
						tempChecked.addAll(toCheck);
						toCheck.clear();
						checked.addAll(sF.getLocation());//make sure all locations combined are not checked again
					}
					recheck = true;
					listOfData.add(sF);
					neighbours.clear();
					tempChecked.clear();
				}
			}
		
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return gson.toJson(listOfData);//convert list to JSON for return
	}
	
	//Case statement to decide which factor to use. Use these numbers in URL requests
	private boolean combine(SocialFactors sF, SocialFactors friend, int factorNumber) {
		boolean combined = false;
		
		switch (factorNumber) {
		case 0: if (sF.getCrimeRate()*0.9 < friend.getCrimeRate() || sF.getCrimeRate() * 1.1 > friend.getCrimeRate() || 
						friend.getCrimeRate()*0.9 < sF.getCrimeRate() || friend.getCrimeRate() * 1.1 > sF.getCrimeRate()){
					sF.combineLocations(friend);
					combined = true;
				}
				break;
		case 1: if (sF.getHousePrice()*0.9 < friend.getHousePrice() || sF.getHousePrice() * 1.1 > friend.getHousePrice() || 
						friend.getHousePrice()*0.9 < sF.getHousePrice() || friend.getHousePrice() * 1.1 > sF.getHousePrice()){
					sF.combineLocations(friend);
					combined = true;
					//System.out.println("shitters");
				}
				break;
		case 2: if (sF.getEducationRating()*0.9 < friend.getEducationRating() || sF.getEducationRating() * 1.1 > friend.getEducationRating() || 
						friend.getEducationRating()*0.9 < sF.getEducationRating() || friend.getEducationRating() * 1.1 > sF.getEducationRating()){
					sF.combineLocations(friend);
					combined = true;
				}
				break;
		case 3: if (sF.getTransportRating()*0.9 < friend.getTransportRating() || sF.getTransportRating() * 1.1 > friend.getTransportRating() || 
						friend.getTransportRating()*0.9 < sF.getTransportRating() || friend.getTransportRating() * 1.1 > sF.getTransportRating()){
					sF.combineLocations(friend);
					combined = true;
				}
				break;
		case 4: if (sF.getMeanAge()*0.9 < friend.getMeanAge() || sF.getMeanAge() * 1.1 > friend.getMeanAge() || 
						friend.getMeanAge()*0.9 < sF.getMeanAge() || friend.getMeanAge() * 1.1 > sF.getMeanAge()){
					sF.combineLocations(friend);
					combined = true;
				}
				break;
		case 5: if (sF.getDrugRate()*0.9 < friend.getDrugRate() || sF.getDrugRate() * 1.1 > friend.getDrugRate() || 
						friend.getDrugRate()*0.9 < sF.getDrugRate() || friend.getDrugRate() * 1.1 > sF.getDrugRate()){
					sF.combineLocations(friend);
					combined = true;
				}
				break;
		case 6: if (sF.getEmploymentRate()*0.9 < friend.getEmploymentRate() || sF.getEmploymentRate() * 1.1 > friend.getEmploymentRate() || 
						friend.getEmploymentRate()*0.9 < sF.getEmploymentRate() || friend.getEmploymentRate() * 1.1 > sF.getEmploymentRate()){
					sF.combineLocations(friend);
					combined = true;
				}
				break;
		case 7: if (sF.getVoteTurnout()*0.9 < friend.getVoteTurnout() || sF.getVoteTurnout() * 1.1 > friend.getVoteTurnout() || 
						friend.getVoteTurnout()*0.9 < sF.getVoteTurnout() || friend.getVoteTurnout() * 1.1 > sF.getVoteTurnout()){
					sF.combineLocations(friend);
					combined = true;
				}
				break;						
		}
		return combined;
	}
}
