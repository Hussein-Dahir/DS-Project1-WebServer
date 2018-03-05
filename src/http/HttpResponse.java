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
    private NavigableMap<String, String> headers = new TreeMap<>();
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
                } else if (f.getName().endsWith(".jpg")) {
                    setContentType(ContentType.JPG);
                    headers.put("X-Content-Type-Options", "nosniff");
                    headers.put("X-Frame-Options", "SameOrigin");
                    headers.put("x-xss-protection", "1; mode=block");
//                    body = Base64.getEncoder().encode(body);
//                    body = Base64.getEncoder().encode(ImageIO.read(f));
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

    private void setDate(Date date) {
        headers.put("Date", date.toString());
    }

    private void setContentLength(long value) {
        headers.put("Content-Length", String.valueOf(value));
    }

    private void setContentType(String value) {
        headers.put("Content-Type", value);
    }

    public void removeBody() {
        body = null;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(protocol + " " + status + "\n");
        for (String key : headers.descendingKeySet()) {
            result.append(key).append(": ").append(headers.get(key)).append("\n");
        }
        result.append("\r\n");
        if (body != null) {
            result.append(new String(body));
        }
        return result.toString();
    }
}
