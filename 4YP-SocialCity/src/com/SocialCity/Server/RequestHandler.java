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
import com.SocialCity.TwitterAnalysis.HashTag;
import com.SocialCity.TwitterAnalysis.TweetByArea;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

//Simple URL parse simulating a REST service. Following URL patterns accepted:
	// -http://localhost:8080/oneFactor/factorNumber/booleanForUsingWards(T)OrBoroughs(F)
	//-http://localhost:8080/hashTagFactors/tag1/tag2
public class RequestHandler extends AbstractHandler {
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		String reply;
		String[] paths = request.getPathInfo().split("/");
		try{
			switch (paths[1]) {
				case "oneFactor": 
					int factor = Integer.parseInt(paths[2]);
					boolean useWards = Boolean.parseBoolean(paths[3]);
					if (factor < 0 || factor > 7) {throw new Exception();}
					reply = new ResponseMaker().oneFactor(factor, useWards);
					response.getWriter().println(reply);
					break;
				case "hashTagFactors":
					reply = new ResponseMaker().hashTags(paths[2], paths[3]);
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
		Server server = new Server(8080);
		server.setHandler(new RequestHandler());
		
		server.start();
		server.join();
		//HashTag.topHashTags();
		//HashTag.tagLocationInfo();
	}
}
