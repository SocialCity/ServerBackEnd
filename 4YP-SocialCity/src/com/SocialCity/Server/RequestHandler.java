package com.SocialCity.Server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.SocialCity.DataParsers.ExcelParsing;
import com.SocialCity.DatabaseSwitching.Updaters;
import com.SocialCity.TwitterAnalysis.HashTag;
import com.SocialCity.TwitterAnalysis.TweetByArea;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

//Simple URL parse simulating a REST service. Following URL patterns accepted:
	//-http://localhost:8080/oneFactor/factorNumber/booleanForUsingWards(true)OrBoroughs(false)/booleanForCombining(true for combine)/booleanForAllFactors(true returns every
	//		factor, false just the factor requested)
	//-http://localhost:8080/twoFactors/factorNumber1/factorNumber2/booleanForUsingWards(true)OrBoroughs(false)/booleanForCombining(true for combine)/booleanForAllFactors(true returns every
	//		factor, false just the factors requested)
	//-http://localhost:8080/hashTagFactors/tag1/tag2
	//-http://localhost:8080/hashTagList
	//-http://localhost:8080/devicesForBorough/boroughCode
	//-http://localhost:8080/deviceList
	//-http://localhost:8080/deviceFactor/device1
	//-http://localhost:8080/factorList
	//-http://localhost:8080/timestamps
public class RequestHandler extends AbstractHandler {
	
	private static ResponseMaker rM;
	
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		String reply;
		String[] paths = request.getPathInfo().split("/");
		boolean useWards;
		boolean combine;
		boolean all;
		String year = null;
		String time = null;
		
		try{
			switch (paths[1]) {
				case "oneFactor": 
					int factor = Integer.parseInt(paths[2]);
					useWards = Boolean.parseBoolean(paths[3]);
					combine = Boolean.parseBoolean(paths[4]);
					all = Boolean.parseBoolean(paths[5]);
					if (paths.length > 6) {
						year = paths[6];
					}
					if (factor < 0 || factor > 8) {throw new Exception();}
					reply = rM.oneFactor(factor, useWards, combine, all, year);
					response.getWriter().println(reply);
					break;
				case "twoFactors":
					int factor1 = Integer.parseInt(paths[2]);
					int factor2 = Integer.parseInt(paths[3]);
					useWards = Boolean.parseBoolean(paths[4]);
					combine = Boolean.parseBoolean(paths[5]);
					all = Boolean.parseBoolean(paths[6]);
					if (paths.length > 7) {
						year = paths[7];
					}
					if (factor1 < 0 || factor1 > 8 || factor2 < 0 || factor2 > 8) {throw new Exception();}
					reply = rM.twoFactors(factor1, factor2, useWards, combine, all, year);
					response.getWriter().println(reply);
					break;
				case "hashTagFactors":
					if (paths.length > 4) {
						time = paths[4];
					}
					reply = rM.hashTags(paths[2], paths[3], time);
					response.getWriter().println(reply);
					break;
				case "hashTagList":
					if (paths.length > 2) {
						time = paths[2];
					}
					System.out.println("wat");
					reply = rM.hashTagList(time);
					response.getWriter().println(reply);
					break;
				case "devicesForBorough":
					if (paths.length > 3) {
						time = paths[3];
					}
					reply = rM.devicesForBorough(paths[2],time);
					response.getWriter().println(reply);
					break;
				case "deviceList":
					if (paths.length > 2) {
						time = paths[2];
					}
					reply = rM.getDevice(time);
					response.getWriter().println(reply);
					break;
				case "deviceFactor":
					if (paths.length > 3) {
						time = paths[3];
					}
					reply = rM.getDeviceFactors(paths[2], time);
					response.getWriter().println(reply);
					break;
				case "factorList":
					reply = rM.getFactorList();
					response.getWriter().println(reply);
					break;
				case "timestamps":
					reply = rM.getTimes();
					response.getWriter().println(reply);
					break;
				default: throw new Exception();
				
			}
		}
		catch (Exception e) {
			response.getWriter().println("<h1>404 - Check yo' self fore you wreck yo' self</h1>");
		}
	}
	//Main to start server
	public static void main(String[] args) throws Exception
	{
		rM = new ResponseMaker();
		//rM.getDeviceFactors("foursquare");
		//Server server = new Server(8080);
		//server.setHandler(new RequestHandler());
		
		//server.start();
		//server.join();
		
		new ExcelParsing().parse();
		Updaters.update("tweets");
		//System.out.println(HashTag.getTagList());
		//System.out.println(new TweetByArea().reTweets());
		//new TweetByArea().deviceBreakdown();
		//new TweetByArea().deviceFactors();
		/*BasicDBObject query = new BasicDBObject("source", "<a href=\"http://www.handmark.com\" rel=\"nofollow\">TweetCaster for iOS</a>");
		MongoClient mongoClient = new MongoClient("localhost");
		DB db2 = mongoClient.getDB( "deviceBreakdown" );
		DBCollection deviceStore = db2.getCollection("deviceFactors");
		
		DBCursor result = deviceStore.find();
		
		while (result.hasNext()){
			System.out.println(result.next());
		}*/
	
	}
}
