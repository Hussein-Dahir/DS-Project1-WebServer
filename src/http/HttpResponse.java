package http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.NavigableMap;
import java.util.TreeMap;

import http.constants.ContentType;
import http.constants.StatusCode;

public class HttpResponse {

	private static final String protocol = "HTTP/1.0";

	private String status;
	private NavigableMap<String, String> headers = new TreeMap<String, String>();
	private byte[] body = null;

	public HttpResponse(String status) {
		this.status = status;
		setDate(new Date());
	}

	public HttpResponse withFile(File f) {
		if (f.isFile()) {
			try {
				FileInputStream reader = new FileInputStream(f);
				int length = reader.available();
				body = new byte[length];
				reader.read(body);
				reader.close();

				setContentLength(length);
				if (f.getName().endsWith(".htm") || f.getName().endsWith(".html")) {
					setContentType(ContentType.HTML);
				} else if (f.getName().endsWith(".css")) {
					setContentType(ContentType.CSS);
				} else if (f.getName().endsWith(".js")) {
					setContentType(ContentType.JS);
				} else {
					setContentType(ContentType.TEXT);
				}
			} catch (IOException e) {
				System.err.println("Error while reading " + f);
			}
		} else {

			File fileNotFoundErrorPage = new File("data/404Error.html");
			
			try {
				FileInputStream reader = new FileInputStream(fileNotFoundErrorPage);
				
				int length = reader.available();
				body = new byte[length];
				reader.read(body);
				reader.close();
				
				setContentType(ContentType.HTML);
				setContentLength(length);
				this.status = StatusCode.NOT_FOUND;
				
			} catch (IOException e) {
				System.err.println("Error while reading " + fileNotFoundErrorPage);
			}
		}
		
		return this;
	}

	public HttpResponse withHtmlBody(String msg) {
		setContentLength(msg.getBytes().length);
		setContentType(ContentType.HTML);
		body = msg.getBytes();
		return this;
	}

	public void setDate(Date date) {
		headers.put("Date", date.toString());
	}

	public void setContentLength(long value) {
		headers.put("Content-Length", String.valueOf(value));
	}

	public void setContentType(String value) {
		headers.put("Content-Type", value);
	}

	public void removeBody() {
		body = null;
	}

	@Override
	public String toString() {
		String result = protocol + " " + status + "\n";
		for (String key : headers.descendingKeySet()) {
			result += key + ": " + headers.get(key) + "\n";
		}
		result += "\r\n";
		if (body != null) {
			result += new String(body);
		}
		return result;
	}
}
