package web;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;

public class Server implements Runnable {

	private final String SSL_PASSWORD = "mypassword";
	private final String SSL_CERTIFICATE_FILE_PATH = "sslCertficate/ssl-certificate.jks";
	
	private ServerSocket server;
	private final String webRoot;
	private ExecutorService threadsPool;

	private final int port;
	private final int threadsLimit;

	public Server(int port, String webRoot, int maxThreads) {
		this.port = port;
		this.threadsLimit = maxThreads;
		this.webRoot = webRoot;
	}

	@Override
	public void run() {
		try {

			char[] passphrase = SSL_PASSWORD.toCharArray();
			KeyStore keystore = KeyStore.getInstance("JKS");
			keystore.load(new FileInputStream(SSL_CERTIFICATE_FILE_PATH), passphrase);
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
			keyManagerFactory.init(keystore, passphrase);
			SSLContext context = SSLContext.getInstance("SSL");
			KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();
			context.init(keyManagers, null, null);
			SSLServerSocketFactory sslServerSocketFactory = context.getServerSocketFactory();
			
			server = sslServerSocketFactory.createServerSocket(port);
			threadsPool = Executors.newFixedThreadPool(threadsLimit);

		} catch (IOException e) {
			System.err.println("Cannot listen on port " + port);
			e.printStackTrace();
			System.exit(1);
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyManagementException e) {
			System.err.println("Error with SSL Certificate");
			e.printStackTrace();
			System.exit(1);
		}

		while (!Thread.interrupted()) {
			try {
				threadsPool.execute(new Thread(new Connection(server.accept(), this)));
			} catch (IOException e) {
				System.err.println("Cannot accept client.");
			}
		}

		this.close();
	}

	public void close() {
		try {
			server.close();
		} catch (IOException e) {
			System.err.println("Error while closing server socket.");
		}
		threadsPool.shutdown();
		try {
			if (!threadsPool.awaitTermination(10, TimeUnit.SECONDS)) 
				threadsPool.shutdownNow();
		} catch (InterruptedException e) {}
	}

	public String getWebRoot() {
		return webRoot;
	}
}
