package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.NavigableMap;
import java.util.TreeMap;

public class HttpRequest {

	private String method;
	private String url;
	private String protocol;
	private NavigableMap<String, String> headers = new TreeMap<String, String>();

	private HttpRequest() {

	}

	public static HttpRequest parseRequest(InputStream in) throws IOException {

		HttpRequest request = new HttpRequest();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = reader.readLine();

		if (line == null) {
			throw new IOException("Server accepts only HTTP requests.");
		}

		String[] requestLine = line.split(" ", 3);

		if (requestLine.length != 3) {
			throw new IOException("Cannot parse request line from \"" + line + "\"");
		}

		if (!requestLine[2].startsWith("HTTP/")) {
			throw new IOException("Server accepts only HTTP requests.");
		}

		// if url contains "?", i.e. GET data
		if(requestLine[1].contains("?")){
			String url = requestLine[1].split("\\?")[0];
			request.url = url;
		}
		else
			request.url = requestLine[1];

		request.method = requestLine[0];
		request.protocol = requestLine[2];

		line = reader.readLine();
		while(line != null && !line.equals("")) {
			String[] header = line.split(": ", 2);
			if (header.length != 2)
				throw new IOException("Cannot parse header from \"" + line + "\"");
			else 
				request.headers.put(header[0], header[1]);

			line = reader.readLine();
		}

		return request;
	}

	public String getMethod() {
		return method;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url){
		this.url = url;
	}

	@Override
	public String toString() {
		String result = method + " " + url + " " + protocol + "\n";
		for (String key : headers.keySet()) {
			result += key + ": " + headers.get(key) + "\n";
		}
		result += "\r\n";

		return result;
	}
}
