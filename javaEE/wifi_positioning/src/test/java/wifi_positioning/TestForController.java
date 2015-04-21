package wifi_positioning;

import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.http.HttpStatus;

public class TestForController {
public static void main(String[] args) throws Exception{
	String url = "http://localhost:8080/wifi_positioning/calibrate";
	
	URL link = new URL(url);
	
	HttpURLConnection con = (HttpURLConnection) link.openConnection();
	
	con.setRequestProperty("x-idMobile", "1");
	con.setRequestProperty("x-x", "3.4");
	con.setRequestProperty("x-y", "1.6");
	con.setRequestProperty("x-intensityMap", "22");
	
	con.connect();
	
	if(con.getResponseCode() != HttpStatus.OK.value()){
		System.out.println("error");
	}	
}
	
}
