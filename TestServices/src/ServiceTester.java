import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Properties;

import org.json.JSONObject;

public class ServiceTester {

	public static void main(String[] args) throws Exception {
		//String upc = "786936807370";
		Properties prop = new Properties();
		prop.load(new FileInputStream("token.properties"));
		String access_token = prop.getProperty("upc_token");
		String tomato_token = prop.getProperty("tomato_token");
		
		ArrayList<String> upcList = new ArrayList<String>();
		
		upcList.add("786936807370");
		upcList.add("012569736658");
		upcList.add("025192328824");
		upcList.add("883929057832");
		upcList.add("014381126327");
		upcList.add("786936242164");
		upcList.add("786936242126");
		
		for(String upc : upcList)
		{
		String dvdName = getDvdName(upc, access_token);
		
		JSONObject imdbInfo = getImdbInfo(dvdName, tomato_token);
	    
		StringBuilder output = new StringBuilder();
	    System.out.println(imdbInfo.getJSONArray("movies").getJSONObject(0).getString("title"));
	    imdbInfo = imdbInfo.getJSONArray("movies").getJSONObject(0);
	    output.append("Title: " + imdbInfo.getString("title") + "\n");
	    output.append("Rated: " + imdbInfo.getString("mpaa_rating") + "\n");
	    output.append("Runtime: " + imdbInfo.getString("runtime") + "\n");
	    
	    System.out.println(output.toString());
	    
	    System.out.println();

		}

	}
	
	private static String getDvdName(String upc, String access_token) throws Exception
	{
		URL upcURL = new URL("http://www.searchupc.com/handlers/upcsearch.ashx?request_type=3&access_token="+ access_token + "&upc=" + upc);
		URLConnection upcCon = upcURL.openConnection();
		
	    BufferedReader in = new BufferedReader(new InputStreamReader(upcCon.getInputStream()));

	    String inputLine;
	    StringBuilder builder = new StringBuilder();

	    while ((inputLine = in.readLine()) != null) 
	        builder.append(inputLine);
	    in.close();

	    JSONObject upcResponse = new JSONObject(builder.toString());
	    System.out.println(upcResponse);
	    System.out.println(upcResponse.getJSONObject("0").getString("productname"));
	    
	    String dvdName = upcResponse.getJSONObject("0").getString("productname");
	    return dvdName;
	    
	}
	
	private static JSONObject getImdbInfo(String dvdName, String tomato_token) throws Exception
	{
		dvdName = dvdName.toLowerCase();
		System.out.println(dvdName);
		if(dvdName.contains("("))
			dvdName = dvdName.substring(0, dvdName.indexOf("("));
		System.out.println(dvdName);
		if(dvdName.contains("collector"))
			dvdName = dvdName.substring(0, dvdName.indexOf("collector"));
			
		dvdName = URLEncoder.encode(dvdName, "UTF-8");
		//URL dvdURL = new URL("http://www.imdbapi.com/?i=&t=" + dvdName);
		URL dvdURL = new URL("http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey=" +
							tomato_token +"&q="+dvdName+"&page_limit=1");
		System.out.println(dvdURL.toString());
		//URL dvdURL = new URL("http://www.imdb.com/xml/find?json=1&nr=1&tt=on&q=" + dvdName);
		URLConnection upcCon = dvdURL.openConnection();
		
	    BufferedReader in = new BufferedReader(new InputStreamReader(upcCon.getInputStream()));

	    String inputLine;
	    StringBuilder builder = new StringBuilder();

	    while ((inputLine = in.readLine()) != null) 
	        builder.append(inputLine);
	    in.close();

	    JSONObject imdbResponse = new JSONObject(builder.toString());
	
	    return imdbResponse;
	}

}
