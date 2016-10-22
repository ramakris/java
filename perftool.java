import java.util.*;
import java.io.*;
import java.net.*;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.InetAddress;


/** Performance and Stress testing tool for Ultraseek 
 * @version	1.0 Jan 2006
 * @author	rbhupath@autonomy.com
 * requires JDK 1.4
 */

public class perftool 
{	
	
	int queryCount=0;
	static String host = null;
	public static Vector queryStrings = new Vector(30000);
	static String filename="SearchTerm.txt";
	String name;
	/* no of queries for each thread */
	public static int count;
	public static String URL = new String();
	
	public static void main(String[] args) {
	
		if (args.length < 3)
		{
			System.out.println("Usage: java pertool <no_of_threads> <hostname:portno> <filename_for_QueryStrings> <no_of_requests> ");
			System.out.println("Example: java perftool 10  godavari:8765 /users/rbhupath/SearchTerm.txt 100 ");
			System.exit(0);
		}
		int noThreads= Integer.parseInt(args[0]);
		host= args[1];
		filename= args[2];
		count = Integer.parseInt(args[3]);
		
		try {
		readText(filename);
		}
		catch (IOException e) { System.out.println("Could not open the file:" + filename); System.exit(1); }
		
		Thread threads[] = new Thread[noThreads];
		URL = "http://" + host + "/query.html?charset=iso-8859-1&qt=";
		
		for(int i=0; i < noThreads; i++)
		{
			threads[i] = new MyThread();
			threads[i].setName("Thread " + Integer.toString(i));
			threads[i].start();
			System.out.println("Starting " + threads[i].getName());
			
		}
		
		
} //end of main
	
	public static  void readText(String fileName) throws IOException
	 {
      
       if (fileName != null) {
           File file = new File(fileName);
           BufferedReader br = null;
           try {
               br = new BufferedReader(new FileReader(file));
               String line = null;
               while ((line = br.readLine()) != null) { 
                   queryStrings.addElement(line);
               }
               
           } 
	finally {
		if (br != null)
		try { br.close(); } catch (IOException e ) { System.out.println("Exception closing file:" + fileName); }

	}
	}

   }  //end of readText

	
}/// end of class perftool

class MyThread extends Thread
{
	long startTime ;
	long endTime;
	String url =null;
	public void run()
	{
	startTime =  new Date().getTime();
	//System.out.println("In the Run  Method" + this.getName());	
	int i =0;
	int ok =0;
	int noResponse=0;
	
	while (i < perftool.count)
	{
	try {
		String substr= (String) perftool.queryStrings.elementAt(new Random().nextInt(perftool.queryStrings.size()));
		url=perftool.URL + URLEncoder.encode(substr,"UTF-8");
		
		URL u = new URL(url);
		URLConnection connection =  u.openConnection();
		HttpURLConnection httpConn = (HttpURLConnection) connection;
		//ConnectionTimeout is available from  JDK 1.5 onwards 
		//httpConn.setConnectTimeout(2000);
		httpConn.setDoInput(true);
		httpConn.setDoOutput(true);
		 if (httpConn.getResponseCode()==200)
		 {
			ok++; 
		 }
		i++;
		
		if (httpConn.getResponseCode()==401)
			{ System.out.println("No response from Ultraseek Server.Check your server name. ");
				noResponse++;
				break;
			}
	}
	
	catch (Exception e) 
	{ 
		System.err.println("No response from Ultraseek Server ");
		System.exit(1);
		
	}
	
	} //end of while
	endTime = new Date().getTime();
	
	long elapsedTime=(endTime-startTime)/1000;	
	System.out.println(this.getName() + " Processed " + ok  + " queries in " + elapsedTime + " secs");
	
	} /// end of run
	
}//end of MyThread
	
	

