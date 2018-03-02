package main;

import web.Server;

public class Main {
	public static void main(String[] args) {
		int port = 80;
		String webRoot = "data";
		int maxThreads = 100;
		new Thread(new Server(port, webRoot, maxThreads)).start();
	}
}
