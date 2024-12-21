package com.thirdeye.stocksmanager.externelcontrollers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.thirdeye.stocksmanager.utils.AllMicroservicesData;

@Service
public class Thirdeye_Guider_Connection {
	
	@Value("${updaterMicroserviceName}")
    private String updaterMicroserviceName;
    
    @Autowired
	AllMicroservicesData allMicroservicesData;

    @Autowired
    private RestTemplate restTemplate;
    
    private static final Logger logger = LoggerFactory.getLogger(Thirdeye_Guider_Connection.class);
    
    public Map<String, Boolean> restartAllMicroservices() {
    	try {
    	    HttpHeaders headers = new HttpHeaders();
    	    headers.set("Content-Type", "application/json");
    	    HttpEntity<Void> request = new HttpEntity<>(headers);
    	    String microserviceUrl = allMicroservicesData.allMicroservices.get(updaterMicroserviceName).getMicroserviceUrl();
    	    Integer microserviceUniqueId = allMicroservicesData.allMicroservices.get(updaterMicroserviceName).getMicroserviceUniqueId();
    	    Integer currentUniqueId = allMicroservicesData.current.getMicroserviceUniqueId();
    	    String url = microserviceUrl + "api/update/updateallmicroservices/" + microserviceUniqueId + "/" + currentUniqueId +"/0";
    	    logger.info("Url to restart all microservices {} : ", url);
    	    ResponseEntity<Map<String, Boolean>> response = restTemplate.exchange(
    	        url,
    	        HttpMethod.POST,
    	        request,
    	        new ParameterizedTypeReference<Map<String, Boolean>>() {}
    	    );
    	    return response.getBody();
    	} catch (Exception e) {
    	    logger.error("Error while sending request to update holded Stock Viewer: ", e);
    	    return null; 
    	}
    }
}
