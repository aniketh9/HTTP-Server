
import java.io.IOException;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class BasicHttpServer {

	private static ServerSocket basicServerSocket;

	// BasicHttpServer main class that accepts client socket connection and
	// creates separate thread for each request

	public static void main(String args[]) {

		try {

			basicServerSocket = new ServerSocket(0);

			ConcurrentHashMap<String, Integer> acessCount = new ConcurrentHashMap<String, Integer>(16, 0.9f, 1);

			System.out.println("Server is listening at port number: " + basicServerSocket.getLocalPort()
					+ " and hosted on : " + InetAddress.getLocalHost().getHostName());

			while (true) {

				Socket clientConnection = basicServerSocket.accept();

				HttpRequestProcessor request = new HttpRequestProcessor(clientConnection, acessCount);

				Thread thread = new Thread(request);

				thread.start();
			}
		} catch (SocketException e) {
			System.err.println("SocketException: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
	}
}
