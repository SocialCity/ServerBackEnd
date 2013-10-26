package com.SocialCity.Server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

//Simple URL parse simulating a REST service. Following URL patterns accepted:
	// -http://localhost:8080/oneFactor/factorNumber/booleanForUsingWards(T)OrBoroughs(F)
public class RequestHandler extends AbstractHandler {
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		String[] paths = request.getPathInfo().split("/");
		try{
			switch (paths[1]) {
				case "oneFactor": 
					int factor = Integer.parseInt(paths[2]);
					boolean useWards = Boolean.parseBoolean(paths[3]);
					if (factor < 0 || factor > 7) {throw new Exception();}
					String reply = new ResponseMaker().oneFactor(factor, useWards);
					response.getWriter().println(reply);
					break;
				default: throw new Exception();
				
			}
		}
		catch (Exception e) {
			response.getWriter().println("<h1>404 YOU JIZZ BAGEL</h1>");
		}
	}
	//Main to start server
	public static void main(String[] args) throws Exception
	{
		Server server = new Server(8080);
		server.setHandler(new RequestHandler());
		
		server.start();
		server.join();
	}
}
