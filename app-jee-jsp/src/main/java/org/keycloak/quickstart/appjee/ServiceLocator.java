package org.keycloak.quickstart.appjee;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author <a href="mailto:mstrukel@redhat.com">Marko Strukelj</a>
 */
public class ServiceLocator {
	
	private static final String SERVICE_URI_INIT_PARAM_NAME = "serviceUrl";

	public static URL getServiceUrl(HttpServletRequest req) {
		URL serviceUrl = null;
		
		try {
			String url = req.getServletContext().getInitParameter(SERVICE_URI_INIT_PARAM_NAME);
		    if (url != null && !url.contains("localhost")){
		    	try {
					serviceUrl = new URL(url);
					return serviceUrl;
				} catch (MalformedURLException e){
					throw new RuntimeException("Malformed URL: " + url);
				}
		    }
	
			String host = req.getLocalAddr();
	
			if (host.equals("localhost")) {
				try {
					host = java.net.InetAddress.getLocalHost().getHostAddress();
				} catch (Exception e) {
				}
			}
			
			try {
				serviceUrl = new URL("http://" + host + ":8080/service");
				return serviceUrl;
			} catch (MalformedURLException e){
				throw new RuntimeException("Malformed URL: " + host);
			}
		} finally {
			System.out.println("Using Service URL " + serviceUrl);
		}
	}
}
