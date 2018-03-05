package main;

import web.Server;

public class Main {
	public static void main(String[] args) {
		int port = 8899;
		String webRoot = "data";
		int maxThreads = 100;
		
		System.out.println("Server is Starting on port: " + port);
		
		new Thread(new Server(port, webRoot, maxThreads)).start();
	}
}
