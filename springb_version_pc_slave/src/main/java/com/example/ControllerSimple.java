package com.example;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.SM.Record;
import com.example.SM.SMException;
import com.example.SMImplVersion2.OID;

@RestController
public class ControllerSimple {

	//AddressList addressList = new AddressList();
	//SMImplVersion2 sm = new SMImplVersion2();
	AddressList addressList;
	SMImplVersion2 sm;
	ControllerSimple (){
		addressList = new AddressList();
		sm = new SMImplVersion2();
	}
	
	@RequestMapping("/")
    public String index2() {
        return "Welcome to home page!";
    }

    @RequestMapping("/hello")
    public String index() {
        return "Hello World";
    }
    
    @RequestMapping(value="/crud_c_simple", method=RequestMethod.POST)
    public ResponseEntity<AnEntity> crud_c_simple (@RequestBody AnEntity anentity){
	
    	/*
    	if(anentity!=null){
    		int c=anentity.getValue()+99;
    		anentity.setValue(c);
    	}
    	ResponseEntity<AnEntity> a = new ResponseEntity<AnEntity>(anentity, HttpStatus.OK);
    	return a;
    	//return new ResponseEntity<AnEntity>(anentity, HttpStatus.OK) ;    
    	 */
    	return new ResponseEntity<AnEntity>(anentity, HttpStatus.OK) ;
    }
    @RequestMapping(value="/crud_c_full", method=RequestMethod.POST)
    public ResponseEntity<AnEntity> crud_c_full (@RequestBody AnEntity anentity){
		
    	//oid=store(requestbody.var1)
    	//a=fetch(oid);
    	//return ResponseEntity;
    	if(anentity!=null){    		
    			int c=Integer.parseInt(anentity.getValue().getAddress())+99;
    			anentity.getValue().setAddress(Integer.toString(c));   	  		
    	}
    	ResponseEntity<AnEntity> a = new ResponseEntity<AnEntity>(anentity, HttpStatus.OK);
    	return a;
    	//return new ResponseEntity<AnEntity>(anentity, HttpStatus.OK) ;    	
    }
    //create (CRUD_C)
    @RequestMapping(value="/crud_c_final", method=RequestMethod.POST)
    public Map<String, Object> createKey(@RequestBody AnEntity anentity) throws SMException, IOException{
    	
    	System.out.println("request create received!");
    	/**
   	  	* Master/Slave mode: 3 nodes
   	  	* Slave node code  for create
   	  	*/
    	//CP Master-slave mode: judge if slave node is in partition state 
    	boolean partitionstate = false;
    	RestTemplate restTemplate = new RestTemplate();
    	 ((SimpleClientHttpRequestFactory)restTemplate.getRequestFactory()).setConnectTimeout(1000*30);
    	 String masterIp_subnet = "172.30.0.65";
    	 try {//if can not get response from master, this node is in partition state
             String response_test = restTemplate.getForObject("http://"+ masterIp_subnet +":8080/", String.class);
         } catch (Exception e) {
        	 partitionstate =true;
        }    
    	if(partitionstate){
    		System.out.println("======Service not available. Partition Status under CP Master/Slave Mode.======");
    		Map<String, Object> response0 = new HashMap<String, Object>();
	    	//return ip and created doc
	 	    String myIP2 = InetAddress.getLocalHost().getHostAddress();
	 	    System.out.println("=============host IP: "+myIP2+"==================");
	    	response0.put("Service not available", "Partition Status under CP Master/Slave Mode");
    		return response0;
    	}
    	//if request sent from client, communicate with master via subnet
    	HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
   	 	String visitorIP = getIpAddr(request);
   	 	System.out.println("=============visiting IP: "+ visitorIP +"==================");
   		String masterIp1 = "54.191.214.30";
    	String masterIp2 = "172.30.0.65";
    	if((!visitorIP.equals(masterIp1)) && (!visitorIP.equals(masterIp2))){    		
    		String uri_0 =  "http://"+ masterIp2 +":8080/crud_c_final";
    		//RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Auth-Token", "e348bc22-5efa-4299-9142-529f07a18ac9");
            HttpEntity<AnEntity> requestEntity  = new HttpEntity<AnEntity>(anentity, headers);
            Map<String, Object> map_node0;
            System.out.println("----------------------SENDING.REQUEST---------------------");	           
            map_node0 = restTemplate.postForObject(uri_0, requestEntity, Map.class);            
            System.out.println("----------------------MASTER NODE RETURNED---------------------");    		
    	}
    	
    	if(anentity!=null){    		 
    		String key = anentity.getKey();
    		String name = anentity.getValue().getName();
    		String address = anentity.getValue().getAddress();
    		
			
	    	Record found = null;
	        SM.OID rec_key_oid = null;
	    	Record rec_key = new Record(20);	    	
	    	rec_key.setBytes(key.getBytes());
	        SM.OID rec_name_oid = null;
	    	Record rec_name = new Record(20);	    	
	    	rec_name.setBytes(name.getBytes());
	        SM.OID rec_add_oid = null;
	    	Record rec_add = new Record(20);	    	
	    	rec_add.setBytes(address.getBytes());
	    	// store & fetch
	         try {
	           System.out.println( "Storing: key" ) ;
	           rec_key_oid = (SM.OID) sm.store(rec_key);	           	           
	           found = sm.fetch(rec_key_oid); 					//rec found
	           System.out.print( "Fetch Got Rec: \n" ) ;
	           System.out.write(found.getBytes(0,0));
	           System.out.println("") ;
	           System.out.println("Fetch Successful!");
	           String foundstr = new String(found.getBytes(0,0));
	           foundstr = foundstr.substring(0,foundstr.indexOf('\u0000'));	           
	           anentity.setKey(foundstr);
	           
	           /*//testing hashCode()
	           System.out.println("!---!");
	           Record finding = new Record(20);//	   
		       finding.setBytes(key.getBytes());//
		       if(rec_key.equals(finding)){
		        	 System.out.println("equal--xxxx!");
		        }else{
		        	System.out.println("noqual--xxxx!");
		        }
		       //SM.OID oid_finding = sm.new OID(finding.hashCode());
		       System.out.println(finding.hashCode());//
		       System.out.println(rec_key.hashCode());//
		       System.out.println("!---!");   
		       //testing */
		       
	           /*//testing
	           System.out.print("xxx---"+rec_key.toString());
	           System.out.print( "oid_getkey: \n" ) ;           
	           System.out.print(rec_key_oid.getKey() ) ;
	           System.out.print( "\n" ) ;
	           System.out.print( "oid.tostring: \n" ) ;
	           System.out.print(rec_key_oid.toString()) ;
	           System.out.print( "\n" ) ;
	           */
	           
	           System.out.println( "Storing: name" ) ;
	           rec_name_oid = (SM.OID) sm.store(rec_name);
	           found = sm.fetch(rec_name_oid); //rec found
	           System.out.print( "Fetch Got Rec: \n" ) ;
	           System.out.write(found.getBytes(0,0));
	           System.out.println("") ;
	           System.out.println("Fetch Successful!");
	           foundstr = new String(found.getBytes(0,0));
	           foundstr = foundstr.substring(0,foundstr.indexOf('\u0000'));
	           anentity.getValue().setName(foundstr);
	           
	           System.out.println( "Storing: address" ) ;
	           rec_add_oid = (SM.OID) sm.store(rec_add);	       
	           found = sm.fetch(rec_add_oid); //rec found
	           System.out.print( "Fetch Got Rec: \n" ) ;
	           System.out.write(found.getBytes(0,0));
	           System.out.println("") ;
	           System.out.println("Fetch Successful!");
	           foundstr = new String(found.getBytes(0,0));
	           foundstr = foundstr.substring(0,foundstr.indexOf('\u0000'));
	           anentity.getValue().setAddress(foundstr); 
	           
	           //save to addresslist
	           addressList.getKey().add(key);					
	           addressList.getNameOIDlist().add(rec_name_oid);	
	           addressList.getAddressOIDlist().add(rec_add_oid);
	           addressList.getKeyOIDlist().add(rec_key_oid);           
	           
	           System.out.print( "-----test-------n" ) ;
	           found = sm.fetch(addressList.getKeyOIDlist().get(0)); 		//key
	           System.out.write(found.getBytes(0,0));
	           System.out.println( "OKOKOKOKOKOKOKOKO" ) ; 
	           sm.flush();
	         } catch ( Exception e ) {
	           System.out.println( "Fetch Failed!" ) ; 
	         }    	        
	         
	         //return
	    	 System.out.println("Key-Value Created. (key:" +key + "). Return !");
	    	 //ResponseEntity<AnEntity> a = new ResponseEntity<AnEntity>(anentity, HttpStatus.OK);
	    	 //return a;	
	    	 Map<String, Object> response = new HashMap<String, Object>();
	    	 //return ip and created doc
	 	     String myIP2 = InetAddress.getLocalHost().getHostAddress();
	 	     System.out.println("=============host IP: "+myIP2+"==================");
	    	 response.put("host IP", myIP2);
	    	 response.put("record created", anentity);
	    	 //Other method: DateTimeFormat.ISO.DATE_TIME : 2011-07-11T21:28:59.564+01:00
	    	 SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");	 
	    	 //response.put("time", df.format(new Date()));
         	 return response;
    	}
    	else{	
    		return null;
    	}
    }
    //delete key(CRUD_D)
    @RequestMapping(value="/crud_d/{id}", method=RequestMethod.POST)
    public Map<String, Object> deleteBykey(@PathVariable("id") String id ) throws SMException, IOException{
    		    	
    	System.out.println("request delete received!");
    	/**
   	  	* Master/Slave mode: 3 nodes
   	  	* Slave node code  for delete
   	  	*/
    	//CP Master-slave mode: judge if slave node is in partition state 
    	boolean partitionstate = false;
    	RestTemplate restTemplate = new RestTemplate();
    	 ((SimpleClientHttpRequestFactory)restTemplate.getRequestFactory()).setConnectTimeout(1000*30);
    	 String masterIp_subnet = "172.30.0.65";
    	 try {//if can not get response from master, this node is in partition state
             String response_test = restTemplate.getForObject("http://"+ masterIp_subnet +":8080/", String.class);
         } catch (Exception e) {
        	 partitionstate =true;
        }    
    	if(partitionstate){
    		System.out.println("======Service not available. Partition Status under CP Master/Slave Mode.======");
    		Map<String, Object> response0 = new HashMap<String, Object>();
	    	//return ip and created doc
	 	    String myIP2 = InetAddress.getLocalHost().getHostAddress();
	 	    System.out.println("=============host IP: "+myIP2+"==================");
	    	response0.put("Service not available", "Partition Status under CP Master/Slave Mode");
    		return response0;
    	}
    	//if request sent from client, communicate with master via subnet
    	HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
   	 	String visitorIP = getIpAddr(request);
   	 	System.out.println("=============visiting IP: "+ visitorIP +"==================");
    	String masterIp1 = "54.191.214.30";
    	String masterIp2 = "172.30.0.65";
    	if((!visitorIP.equals(masterIp1)) && (!visitorIP.equals(masterIp2))){    		
    		String uri_0 =  "http://"+ masterIp2 +":8080/crud_d/"+id;
    		//RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Auth-Token", "e348bc22-5efa-4299-9142-529f07a18ac9");
            //HttpEntity<AnEntity> requestEntity  = new HttpEntity<AnEntity>(id, headers);
            Map<String, Object> map_node0;
            System.out.println("----------------------SENDING.REQUEST---------------------");	           
            map_node0 = restTemplate.postForObject(uri_0, "", Map.class);            
            System.out.println("----------------------MASTER NODE RETURNED---------------------");    		
    	}  	
    	
    	
    	Map<String, Object> response = new HashMap<String, Object>();
    	try{ 
    		for(int i=0; i<addressList.getKey().size(); i++){
    			System.out.println("keyx---" + addressList.getKey().get(i));
    		}
    		int indexfound = -1;
        	for(int i=0; i<addressList.getKey().size(); i++){
        		if(id.equals(addressList.getKey().get(i))){
        			indexfound = i;
        			System.out.println("Found index---" + indexfound);
        		}
        	}
        	if(indexfound == -1){
        		System.out.println("results not found!");        		
            	response.put("key to delete not found", id);
            	return response;
    			
    		}
        	Record found;
        	String foundstr;        	
        	//AnEntity anentity = new AnEntity();
        	
        	addressList.getKey().remove(indexfound);
	    	sm.delete(addressList.getKeyOIDlist().get(indexfound));
	    	addressList.getKeyOIDlist().remove(indexfound);
	    	sm.delete(addressList.getNameOIDlist().get(indexfound));
	    	addressList.getNameOIDlist().remove(indexfound);
	        sm.delete(addressList.getAddressOIDlist().get(indexfound));
	        addressList.getAddressOIDlist().remove(indexfound);
	        
	        sm.flush();               
	        
	        System.out.println("Delete Successfully!");
	        response.put("key value deleted", id);
	        //return ip and created doc
	 	    String myIP2 = InetAddress.getLocalHost().getHostAddress();
	 	    System.out.println("=============host IP: "+myIP2+"==================");
        	return response;
    	} catch ( Exception e ) {
	           System.out.println( "error, maybe delete" ) ; 
	    }
		return null; 
    }   
    
    //update (CRUD_U)
    @RequestMapping(value="/crud_u", method=RequestMethod.POST)
    public Map<String, Object> updateByKey(@RequestBody AnEntity anentity) throws SMException, IOException{

    	System.out.println("request update received!");
    	
    	/**
   	  	* Master/Slave mode: 3 nodes
   	  	* Slave node code  for update
   	  	*/
    	//CP Master-slave mode: judge if slave node is in partition state 
    	boolean partitionstate = false;
    	RestTemplate restTemplate = new RestTemplate();
    	 ((SimpleClientHttpRequestFactory)restTemplate.getRequestFactory()).setConnectTimeout(1000*30);
    	 String masterIp_subnet = "172.30.0.65";
    	 try {//if can not get response from master, this node is in partition state
             String response_test = restTemplate.getForObject("http://"+ masterIp_subnet +":8080/", String.class);
         } catch (Exception e) {
        	 partitionstate =true;
        }           
    	if(partitionstate){
    		System.out.println("======Service not available. Partition Status under CP Master/Slave Mode.======");
    		Map<String, Object> response0 = new HashMap<String, Object>();
	    	//return ip and created doc
	 	    String myIP2 = InetAddress.getLocalHost().getHostAddress();
	 	    System.out.println("=============host IP: "+myIP2+"==================");
	    	response0.put("Service not available", "Partition Status under CP Master/Slave Mode");
    		return response0;
    	}
    	//if request sent from client, communicate with master via subnet
    	HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
   	 	String visitorIP = getIpAddr(request);
   	 	System.out.println("=============visiting IP: "+ visitorIP +"==================");
   		String masterIp1 = "54.191.214.30";
    	String masterIp2 = "172.30.0.65";
    	if((!visitorIP.equals(masterIp1)) && (!visitorIP.equals(masterIp2))){    		
    		String uri_0 =  "http://"+ masterIp2 +":8080/crud_u";
    		//RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Auth-Token", "e348bc22-5efa-4299-9142-529f07a18ac9");
            HttpEntity<AnEntity> requestEntity  = new HttpEntity<AnEntity>(anentity, headers);
            Map<String, Object> map_node0;
            System.out.println("----------------------SENDING.REQUEST---------------------");	           
            map_node0 = restTemplate.postForObject(uri_0, requestEntity, Map.class);            
            System.out.println("----------------------MASTER NODE RETURNED---------------------");    		
    	}   	
    	
    	Map<String, Object> response = new HashMap<String, Object>();
        
    	String key = null;
    	String name;
    	String address;
    	if(anentity!=null){    		 
    		key = anentity.getKey();
    		name = anentity.getValue().getName();
    		address = anentity.getValue().getAddress();    		
			
	    	Record found = null;
	        SM.OID rec_key_oid = null;
	    	Record rec_key = new Record(20);	    	
	    	rec_key.setBytes(key.getBytes());
	        SM.OID rec_name_oid = null;
	    	Record rec_name = new Record(20);	    	
	    	rec_name.setBytes(name.getBytes());
	        SM.OID rec_add_oid = null;
	    	Record rec_add = new Record(20);	    	
	    	rec_add.setBytes(address.getBytes());
	    	
	    	try{ 
	    		for(int i=0; i<addressList.getKey().size(); i++){
	    			System.out.println("keyx---" + addressList.getKey().get(i));
	    		}
	    		int indexfound = -1;
	        	for(int i=0; i<addressList.getKey().size(); i++){
	        		if(key.equals(addressList.getKey().get(i))){
	        			indexfound = i;
	        			System.out.println("Found index---" + indexfound);
	        		}
	        	}
	        	if(indexfound == -1){
	        		System.out.println("results not found!");        		
	            	response.put("key to update not found", key);
	            	return response;
	    			
	    		}
	        	sm.update(addressList.getKeyOIDlist().get(indexfound), rec_key);
	        	sm.update(addressList.getNameOIDlist().get(indexfound), rec_name);
	        	sm.update(addressList.getAddressOIDlist().get(indexfound), rec_add);	        	
	    	}catch ( Exception e ) {
		           System.out.println( "error" ) ; 
		    }
	    	sm.flush();        
	         
	        response.put("key value updated", key);
	        //return ip and created doc
	 	    String myIP2 = InetAddress.getLocalHost().getHostAddress();
	 	    System.out.println("=============host IP: "+myIP2+"==================");
			return response;
    	}
		return null;	    	
	        
    }
    //read (CRUD_R)    
    @RequestMapping(value="/crud_r/{id}", method=RequestMethod.GET)
    public Map<String, Object> readByKey(@PathVariable("id") String id ) throws SMException, IOException{
    		    	
    	System.out.println("request read received!");
    	
    	/**
   	  	* Master/Slave mode: 3 nodes
   	  	* Slave node code  for read
   	  	*/
    	//CP Master-slave mode: judge if slave node is in partition state 
    	boolean partitionstate = false;
    	RestTemplate restTemplate = new RestTemplate();
    	 ((SimpleClientHttpRequestFactory)restTemplate.getRequestFactory()).setConnectTimeout(1000*30);
    	 String masterIp_subnet = "172.30.0.65";
    	 try {//if can not get response from master, this node is in partition state
             String response_test = restTemplate.getForObject("http://"+ masterIp_subnet +":8080/", String.class);
         } catch (Exception e) {
        	 partitionstate =true;
        }
    	if(partitionstate){
    		System.out.println("======Service not available. Partition Status under CP Master/Slave Mode.======");
    		Map<String, Object> response0 = new HashMap<String, Object>();
	    	//return ip and created doc
	 	    String myIP2 = InetAddress.getLocalHost().getHostAddress();
	 	    System.out.println("=============host IP: "+myIP2+"==================");
	    	response0.put("Service not available", "Partition Status under CP Master/Slave Mode");
    		return response0;
    	}
    	
    	
    	//retrun the client ip
    	HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        System.out.println("=============visiting IP: "+getIpAddr(request)+"==================");
        //return my ip address (external ip and loopback ip)
	    String myIP = InetAddress.getLocalHost().getHostAddress();
	    System.out.println("=============host IP: "+myIP+"==================");
	    String myIP2 = InetAddress.getLoopbackAddress().getHostAddress();
	    System.out.println("=============host IP2: "+myIP2+"==================");
	    Enumeration en = NetworkInterface.getNetworkInterfaces();
	    while(en.hasMoreElements()){
	        NetworkInterface ni=(NetworkInterface) en.nextElement();
	        Enumeration ee = ni.getInetAddresses();
	        while(ee.hasMoreElements()) {
	            InetAddress ia= (InetAddress) ee.nextElement();
	            System.out.println("kk--"+ia.getHostAddress());
	        }
	     }
	    
    
    	try{     		
    		int indexfound = -1;
        	for(int i=0; i<addressList.getKey().size(); i++){
        		if(id.equals(addressList.getKey().get(i))){
        			indexfound = i;        			
        		}
        	}
        	if(indexfound == -1){
        		System.out.println("results not found!");
    			return null;
    		}
        	Record found;
        	String foundstr;        	
        	AnEntity anentity = new AnEntity();
        	
        	//found = sm.fetch(addressList.getKeyOIDlist().get(0)); 		//key
	    	found = sm.fetch(addressList.getKeyOIDlist().get(indexfound)); 	//key
	    	foundstr = new String(found.getBytes(0,0));
	        foundstr = foundstr.substring(0,foundstr.indexOf('\u0000'));
	        anentity.setKey(foundstr);  
	        System.out.println("Found index---" +  anentity.getKey());
	        
	    	found = sm.fetch(addressList.getNameOIDlist().get(indexfound)); //name
	    	foundstr = new String(found.getBytes(0,0));
	        foundstr = foundstr.substring(0,foundstr.indexOf('\u0000'));
	        anentity.getValue().setName(foundstr);
	        
	        found = sm.fetch(addressList.getAddressOIDlist().get(indexfound)); 	//address
	       	foundstr = new String(found.getBytes(0,0));
	        foundstr = foundstr.substring(0,foundstr.indexOf('\u0000'));
	        anentity.getValue().setAddress(foundstr);	        
	        
	        sm.flush();
	        System.out.println("Read Successfully. (key:" + id + "). Return !");
	        System.out.println("Return !!");
		    //ResponseEntity<AnEntity> a = new ResponseEntity<AnEntity>(anentity, HttpStatus.OK);
		    //return a;
		    //return ip and created doc
	        Map<String, Object> response = new HashMap<String, Object>();
	 	    String myIP3 = InetAddress.getLocalHost().getHostAddress();
	 	    System.out.println("=============host IP: "+myIP3+"==================");
	    	response.put("host IP", myIP3);
	    	response.put("record created", anentity);
	    	SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");	 
	    	//response.put("time", df.format(new Date()));
	    	
        	return response;
    	} catch ( Exception e ) {
	           System.out.println( "error, maybe fetch" ) ; 
	    }
		return null; 
    }
    
    
    @RequestMapping(value="/read/{id}", method=RequestMethod.GET)
    //public static String read(@PathVariable("id") String id ) {
    public static ResponseEntity<String> read(@PathVariable("id") String id ) {
    	   	
    	System.out.println("testing");
    	return new ResponseEntity<>(HttpStatus.CREATED);   	
    	
    } 
    
    
    /* return client ip; para, request object
     */
    public String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            System.out.println("Proxy-Client-IP----" + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            System.out.println("Proxy-Client-IP----" + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            System.out.println("getRemoteAddr-Client-IP----" + ip);
        }
        return ip;
    }

}