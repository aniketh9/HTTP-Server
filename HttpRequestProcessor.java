
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

/*
 *  HttpRequestProcessor class implements Runnable interface for 
 processing the client Http request and generates appropriate
 Http response based on resource availability. */

public class HttpRequestProcessor implements Runnable {

	Socket clientSocket;
	String requestedFilename = "";
	String clientIP = "";
	boolean isResourceAvailable = true;
	ConcurrentHashMap<String, Integer> accessCount; // ConcurrentHashMap
													// persists the access count
													// a given resource

	// HttpRequestProcessor constructor

	public HttpRequestProcessor(Socket socket, ConcurrentHashMap<String, Integer> resourceaccessCount) {

		this.clientSocket = socket;
		this.accessCount = resourceaccessCount;
		this.clientIP = socket.getInetAddress().getHostAddress();

	}

	// Implementation of run method which is invoked when a thread start method
	// is called inside BasicHttpServer main class

	public void run() {
		try {
			sendGetResponse(processGetRequest());
			if (isResourceAvailable) {
				System.out.println(requestedFilename + "|" + clientIP + "|" + accessCount.get(requestedFilename));
			}
		} catch (Exception e) {
			System.err.println("Exception of type" + e.getClass() + " has occured: " + e.getMessage());
		}
	}

	// processGetRequest is called to process client http request

	private String processGetRequest() {

		String requestLine = "";
		InputStream clientInputStream = null;

		try {

			clientInputStream = clientSocket.getInputStream();
		} catch (SocketException e) {
			System.err.println("SocketException: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}

		BufferedReader requestReader = new BufferedReader(new InputStreamReader(clientInputStream));

		try {
			requestLine = requestReader.readLine();
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}

		String[] splitRequestLine = requestLine.split(" ");
		String fileName = splitRequestLine[1];
		this.requestedFilename = fileName;
		accessCount.putIfAbsent(fileName, 0);
		return fileName;

	}

	// sendGetResponse checks for request file resource and generates response
	// with either "200 Ok" or "404 Not Found" Status based on the availability
	// of resource

	private void sendGetResponse(String filename) {

		String requestedFile = "." + filename;
		Calendar calenderInstance = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
		TimeZone gmtTime = TimeZone.getTimeZone("GMT");
		dateFormat.setTimeZone(gmtTime);
		String statusLine = null;
		String Date = "Date: "
				+ java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneId.of("GMT")))
				+ "\r\n";
		String Server = "Server: BasicHTTPServer/0.1" + "\r\n";
		String Content_Type = null;
		String Response_Body = null;
		File fileRequested = new File(requestedFile);
		Long lastmodifiedStr = fileRequested.lastModified();
		calenderInstance.setTimeInMillis(lastmodifiedStr);
		String Last_Modified = "Last-Modified: " + dateFormat.format(calenderInstance.getTime()) + "\r\n";
		Long resource_Length = fileRequested.length();
		String Content_Length = "Content-Length: " + resource_Length.toString() + "\r\n";
		FileInputStream fileInputStream = null;
		byte[] fileReader = new byte[2048];
		int countBytes = 0;

		DataOutputStream clientOutputStream = null;

		try {
			clientOutputStream = new DataOutputStream(clientSocket.getOutputStream());
			if (fileRequested.exists() && !fileRequested.isDirectory()) {
				fileInputStream = new FileInputStream(requestedFile);
				statusLine = "HTTP/1.0 200 OK" + "\r\n";
				Content_Type = "Content-Type: " + Utility.getContentType(requestedFile) + "\r\n";
				clientOutputStream.writeBytes(statusLine);
				clientOutputStream.writeBytes(Date);
				clientOutputStream.writeBytes(Server);
				clientOutputStream.writeBytes(Content_Type);
				clientOutputStream.writeBytes(Last_Modified);
				clientOutputStream.writeBytes(Content_Length);
				clientOutputStream.writeBytes("\r\n");
				while ((countBytes = fileInputStream.read(fileReader)) != -1)

				{
					clientOutputStream.write(fileReader, 0, countBytes);
				}
				accessCount.replace(filename, accessCount.get(filename) + 1);
				fileInputStream.close();
				clientOutputStream.close();
				clientSocket.close();
			}

			else {
				this.isResourceAvailable = false;
				statusLine = "HTTP/1.0 404 Not Found" + "\r\n";
				Content_Type = "Content-Type: " + "text/html" + "\r\n";
				Response_Body = "<HTML>" + "<HEAD><TITLE>ERROR:404 File Not Found</TITLE></HEAD>"
						+ "<BODY>Requested File is Not Found, Please enter a valid File Name</BODY></HTML>";
				clientOutputStream.writeBytes(statusLine);
				clientOutputStream.writeBytes(Date);
				clientOutputStream.writeBytes(Server);
				clientOutputStream.writeBytes(Content_Type);
				clientOutputStream.writeBytes("\r\n");
				clientOutputStream.writeBytes(Response_Body);
				clientOutputStream.writeBytes("\r\n");
				clientOutputStream.close();
				clientSocket.close();
			}

		} catch (SocketException e) {
			System.err.println("SocketException: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
	}

}
