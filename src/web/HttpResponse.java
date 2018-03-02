package web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.NavigableMap;
import java.util.TreeMap;

public class HttpResponse {

    private static final String protocol = "HTTP/1.0";
    private static final String FILE_NOT_FOUND = "./wwwroot/404Error.html";

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
                }
                else if (f.getName().endsWith(".jpg")) {
                    setContentType(ContentType.JPG);
                }else {
                    setContentType(ContentType.TEXT);
                }
            } catch (IOException e) {
                System.err.println("Error while reading " + f);
            }
            return this;
        } else {
            try {
                FileInputStream reader = new FileInputStream(this.FILE_NOT_FOUND);
                int length = reader.available();
                body = new byte[length];
                reader.read(body);
                reader.close();
                setContentType(ContentType.HTML);
            }
            catch (IOException e){
                System.err.println("Error while reading " + f);
            }
            return this;
//            return new HttpResponse(StatusCode.NOT_FOUND)
//                    .withHtmlBody("<html><body>File " + f + " not found.</body></html>");
        }

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

    public static class StatusCode {
        public static final String OK = "200 OK";
        public static final String NOT_FOUND = "404 Not Found";
        public static final String NOT_IMPLEMENTED = "501 Not Implemented";
    }

    public static class ContentType {
        public static final String TEXT = "text/plain";
        public static final String HTML = "text/html";
        public static final String CSS = "text/css";
        public static final String JS = "text/javascript";
        public static final String JPG = "image/jpeg";
    }

}
