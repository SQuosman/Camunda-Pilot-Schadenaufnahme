package camundaPilot.Schadenaufnahme;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import connectjar.org.apache.http.HttpHost;

//import org.glassfish.jersey.client.JerseyClientBuilder;

public class RestTest {

	public static void main(String[] args) {
		HttpHost fiddlerProxy = new HttpHost("localhost", 8888);
		
		//URL zusammenbauen
//		String base = "http://services.gisgraphy.com/reversegeocoding/search";
//		UriBuilder url = UriBuilder.fromUri(base).queryParam("lat", "48.48.8723789988").queryParam("lng", "2.2996401787");
		String base = "http://services.groupkt.com/state/search/IND";
		UriBuilder url = UriBuilder.fromUri(base).queryParam("text", "pradesh");
		
		//client mit Proxy erzeugen
		ClientConfig config = new ClientConfig().property(ClientProperties.PROXY_URI, fiddlerProxy);
		Client client = ClientBuilder.newClient(config);
		//target erzeugen
		WebTarget target = client.target(url);		
		
//		Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
		Invocation.Builder builder = target.request();
		System.out.println(target.getUri());

		Response response = builder.get();
		
		boolean hasEntity = response.hasEntity();
		int status = response.getStatus();
		String entity = response.getEntity().getClass().toString();
		String jsonResponse = response.readEntity(String.class);
		
		System.out.println("Response: " + jsonResponse);

	}

}
