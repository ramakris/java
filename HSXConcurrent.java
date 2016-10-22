import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.concurrent.*;

/**
 * @author rabhupat  ramakris@gmail.com
 *
 */
public class HSXConcurrent {

	/**
	 * @param args
	 */
	
	public static String chatURL = "http://192.168.11.54:8080/RestGateway/hsx/chat/connection";
	//public static String videoURL = "http://192.168.11.54:8080/RestGateway/hsx/videochat/new";
	public static String videoURL = "http://192.168.210.101:8080/RestGateway/hsx/videochat/new";
	
	public static String SAMLTokenFile = "c:\\saml.txt";
	public static String PayloadLFile = "c:\\payload.txt";
	public static int noThreads;
	public static int noReq;
	public static String SAMLString = "";
	public static String PayloadString = "";


	public static void main(String[] args) {
		if (args.length != 2)
		{
			System.out.println("Usage: java HSXPerformanceTest <no_of_threads>   <no_of_requests/thread>  <chat/video 1 for chat and 2 for video>");
			System.out.println("Example: java HSXPerformanceTest  12 100  ");
			System.exit(0);
		}	
		 
		 noThreads= Integer.parseInt(args[0]);
		 noReq= Integer.parseInt(args[1]);
	 //	int selection =Integer.parseInt(args[2]);
		//Loading the Payload and SAML file 
		try {
		SAMLString=readText(SAMLTokenFile);
		PayloadString=readText(PayloadLFile);
		}
		catch (IOException e) { System.out.println("Cannot Open File the PayLoad/SAML File"); }
		ExecutorService exec = Executors.newFixedThreadPool(noThreads);
		for (int i=0; i < noThreads; i++)

		{
			exec.execute(new HSXClient("Thread_" + i));
		    	
			
		}
		
	}// end of main
	public static  String readText(String fileName) throws IOException
	{
	    String str= "";
		if (fileName != null) {
			File file = new File(fileName);
			BufferedReader br = null;
			try {
	          br = new BufferedReader(new FileReader(file));
	          str = br.readLine();   
	          }
	          
			finally {
				if (br != null)
					try { br.close(); } catch (IOException e ) { System.out.println("Exception closing file:" + fileName); }
				}
				}
	    return str;
	}  //End of readText

	} //End

class HSXClient implements Runnable
{
	long startTime ;
	long endTime;
	private String name;
    
	public HSXClient(String n)
	{
		this.name=n;
	}
	
	public String getName()
	{
	return this.name;
	}
	
	public void run()
	{
	startTime =  new Date().getTime();
	int i =0;
	int ok =0;
	int noResponse=0;
	System.out.println("Running Thread " + this.getName());
	
	while (i < HSXConcurrent.noReq)
	{
	try {
		
	    URL url = new URL(HSXConcurrent.videoURL);
	    HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
	    httpCon.setDoOutput(true);
	    httpCon.setRequestMethod("POST");
	    httpCon.setRequestProperty("Accept", "application/xml");
	    httpCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	    httpCon.setConnectTimeout(30000);
	    OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream()); 
	    out.write(HSXConcurrent.PayloadString);
	    out.close();
	   // System.out.println("Code: " + httpCon.getResponseCode());
		//System.out.println("Response Message:" + httpCon.getResponseMessage());
		
		 if (httpCon.getResponseCode()==200)
		 {
			ok++; 
		 }
		 
		i++;
		
		// Remove the below comments in case you need to trap the ResponseBody
       /*
		 BufferedReader br = new BufferedReader(new InputStreamReader((httpCon.getInputStream())));
	    String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}
		*/		
	}
	
	catch (Exception e) 
	{ 
		System.err.println("No response from the Server. May need to Login to Equinix VPN ");
		System.exit(1);
		
	}
	
	} //end of while
	
	endTime = new Date().getTime();
	
	float elapsedTime=(endTime-startTime)/1000;	
	
	System.out.println(this.getName() + " Processed " + ok +  " Chat/Video Sessions in " + elapsedTime + " secs at " + (float)(elapsedTime/ok) + " secs");
	
	} /// end of run
	
}//end of HSXClientExecutor
	

