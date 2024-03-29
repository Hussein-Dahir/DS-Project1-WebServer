package web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import http.HttpRequest;
import http.HttpResponse;
import http.constants.HttpMethod;
import http.constants.StatusCode;

public class Connection implements Runnable {

	private Server server;
	private Socket client;
	private InputStream in;
	private OutputStream out;

	public Connection(Socket cl, Server s) {
		client = cl;
		server = s;
	}

	@Override
	public void run() {
		try {

			in = client.getInputStream();
			out = client.getOutputStream();

			HttpRequest request = HttpRequest.parseRequest(in);

			System.out.println();
			System.out.println(
					"Request for " + request.getUrl() + 
					" is being processed by socket at " + 
					client.getInetAddress() +":"+ client.getPort()
					);

			System.out.println();
			System.out.println("------------------------ REQUEST BEGIN ----------------------------------");
			System.out.println(request);
			System.out.println("---------------------------- REQUEST END --------------------------------");
			
			// if request url is "/", then forward it to "index.html"
			if(request.getUrl().equals("/")){
				request.setUrl("/index.html");
			}

			HttpResponse response;
			String method = request.getMethod();
			
			if (method.equals(HttpMethod.GET) || method.equals(HttpMethod.HEAD)) {
				
				File f = new File(server.getWebRoot() + request.getUrl());
				response = new HttpResponse(StatusCode.OK).withFile(f);
				
				if (method.equals(HttpMethod.HEAD)) {
					response.removeBody();
				}
				
			} else {
				response = new HttpResponse(StatusCode.NOT_IMPLEMENTED);
			}

			respond(response);

			in.close();
			out.close();
		} catch (IOException e) {
			// System.err.println("Error in client's IO.");
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				System.err.println("Error while closing client socket.");
			}
		}
	}

	public void respond(HttpResponse response) throws IOException {
		String toSend = response.toString();
		PrintWriter writer = new PrintWriter(out);
		writer.write(toSend);
		writer.flush();

		byte[] body = response.getBody();
		if (body != null) 
			out.write(response.getBody());
	}
}
