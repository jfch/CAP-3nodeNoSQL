package com.example;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        return "YEAH!!!!!!!!!!!!!!!!";
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
   
    @RequestMapping(value="/crud_c_final", method=RequestMethod.POST)
    public Map<String, Object> createKey(@RequestBody AnEntity anentity) throws SMException, IOException{
    	
    	System.out.println("request create received!");
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
	         
	    	 /**
	    	  * Master/Slave mode: 3 nodes
	    	  * Master node code  for create
	    	  */
	         //if request come from remote client, send request via subnet to other nodes;
	         //retrun the client ip
	     	 HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	     	 String visitorIP = getIpAddr(request);
	     	 System.out.println("=============visiting IP: "+getIpAddr(request)+"==================");
	         String slavePrivateIp1 = "172.30.1.80";
	         String slavePrivateIp2 = "172.30.2.93";
	         String uri_1 =  "http://"+slavePrivateIp1+":8080/crud_c_final";
	         String uri_2 =  "http://"+slavePrivateIp2+":8080/crud_c_final";
	         RestTemplate restTemplate = new RestTemplate();
	         HttpHeaders headers = new HttpHeaders();
	         headers.add("X-Auth-Token", "e348bc22-5efa-4299-9142-529f07a18ac9");
	         HttpEntity<AnEntity> requestEntity  = new HttpEntity<AnEntity>(anentity, headers);
	         Map<String, Object> map_snode1,map_snode2;
	         if((!visitorIP.equals(slavePrivateIp1)) && (!visitorIP.equals(slavePrivateIp2))){//sending request to other two nodes
	         //internal communiation via subnet;
	         System.out.println("----------------------SENDING.REQUEST---------------------");	           
	         map_snode1 = restTemplate.postForObject(uri_1, requestEntity, Map.class);
	         map_snode2 = restTemplate.postForObject(uri_2, requestEntity, Map.class);
	         System.out.println("----------------------SLAVE NODE RETURNED---------------------");   
	         }else{//else send to the other slave node
	        	 if(visitorIP.equals(slavePrivateIp1)){
	        		 map_snode2 = restTemplate.postForObject(uri_2, requestEntity, Map.class);
	        	 }else{
	        		 map_snode1 = restTemplate.postForObject(uri_1, requestEntity, Map.class);
	        	 }	        	
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
    @RequestMapping(value="/crud_d/{id}", method=RequestMethod.POST)
    public Map<String, Object> deleteBykey(@PathVariable("id") String id ) throws SMException, IOException{
    		    	
    	System.out.println("request delete received!");
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
	        
	        
	        /**
	    	  * Master/Slave mode: 3 nodes
	    	  * Master node code for delete
	    	  */
	         //if request come from remote client, send request via subnet to other nodes;
	         //retrun the client ip
	     	 HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	     	 String visitorIP = getIpAddr(request);
	     	 System.out.println("=============visiting IP: "+getIpAddr(request)+"==================");
	     	String slavePrivateIp1 = "172.30.1.80";
	         String slavePrivateIp2 = "172.30.2.93";
	         String uri_1 =  "http://"+slavePrivateIp1+":8080/crud_d/"+id;
	         String uri_2 =  "http://"+slavePrivateIp2+":8080/crud_d/"+id;
	         RestTemplate restTemplate = new RestTemplate();
	         HttpHeaders headers = new HttpHeaders();
	         headers.add("X-Auth-Token", "e348bc22-5efa-4299-9142-529f07a18ac9");
	         HttpEntity<String> requestEntity  = new HttpEntity<String>(id, headers);
	         Map<String, Object> map_snode1,map_snode2;
	         if((!visitorIP.equals(slavePrivateIp1)) && (!visitorIP.equals(slavePrivateIp2))){//sending request to other two nodes
	        //internal communiation via subnet;
	         System.out.println("----------------------SENDING.REQUEST---------------------");	           
	         map_snode1 = restTemplate.postForObject(uri_1, "", Map.class);
	         map_snode2 = restTemplate.postForObject(uri_2, "", Map.class);
	         System.out.println("----------------------SLAVE NODE RETURNED---------------------");   
	         }else{//else send to the other slave node
	        	 if(visitorIP.equals(slavePrivateIp1)){
	        		 map_snode2 = restTemplate.postForObject(uri_2, "", Map.class);
	        	 }else{
	        		 map_snode1 = restTemplate.postForObject(uri_1, "", Map.class);
	        	 }	        	
	         }        
	        
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
    
    @RequestMapping(value="/crud_u", method=RequestMethod.POST)
    public Map<String, Object> updateByKey(@RequestBody AnEntity anentity) throws SMException, IOException{

    	System.out.println("request update received!");
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
	        
	        
	        
	        /**
	    	  * Master/Slave mode: 3 nodes
	    	  * Master node code  for update
	    	  */
	         //if request come from remote client, send request via subnet to other nodes;
	         //retrun the client ip
	     	 HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	     	 String visitorIP = getIpAddr(request);
	     	 System.out.println("=============visiting IP: "+getIpAddr(request)+"==================");
	     	 String slavePrivateIp1 = "172.30.1.80";
	         String slavePrivateIp2 = "172.30.2.93";
	         String uri_1 =  "http://"+slavePrivateIp1+":8080/crud_u";
	         String uri_2 =  "http://"+slavePrivateIp2+":8080/crud_u";
	         RestTemplate restTemplate = new RestTemplate();
	         HttpHeaders headers = new HttpHeaders();
	         headers.add("X-Auth-Token", "e348bc22-5efa-4299-9142-529f07a18ac9");
	         HttpEntity<AnEntity> requestEntity  = new HttpEntity<AnEntity>(anentity, headers);
	         Map<String, Object> map_snode1,map_snode2;
	         if((!visitorIP.equals(slavePrivateIp1)) && (!visitorIP.equals(slavePrivateIp2))){//sending request to other two nodes
	         //internal communiation via subnet;
	         System.out.println("----------------------SENDING.REQUEST---------------------");	           
	         map_snode1 = restTemplate.postForObject(uri_1, requestEntity, Map.class);
	         map_snode2 = restTemplate.postForObject(uri_2, requestEntity, Map.class);
	         System.out.println("----------------------SLAVE NODE RETURNED---------------------");   
	         }else{//else send to the other slave node
	        	 if(visitorIP.equals(slavePrivateIp1)){
	        		 map_snode2 = restTemplate.postForObject(uri_2, requestEntity, Map.class);
	        	 }else{
	        		 map_snode1 = restTemplate.postForObject(uri_1, requestEntity, Map.class);
	        	 }	        	
	         }         
	         
	         
	        response.put("key value updated", key);
	        //return ip and created doc
	 	    String myIP2 = InetAddress.getLocalHost().getHostAddress();
	 	    System.out.println("=============host IP: "+myIP2+"==================");
			return response;
    	}
		return null;	    	
	        
    }
    
    
    @RequestMapping(value="/crud_r/{id}", method=RequestMethod.GET)
    public Map<String, Object> readByKey(@PathVariable("id") String id ) throws SMException, IOException{
    		    	
    	System.out.println("request read received!");
    	
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
	        
	        //RestTemplate restTemplate = new RestTemplate();
	    	//Response res = restTemplate.postForObject(uri, anentity,Response.class);
	        //return new ResponseEntity<>(HttpStatus.CREATED);
	        
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
    	//return "Hello read";
    	/*	
    	
    	String record = "1";
    	String uri =  "http://"+masterPrivateIp+":8080/internal/write/"+ key+"/"+record;
    	RestTemplate restTemplate = new RestTemplate();
    	Response res = restTemplate.postForObject(uri, null,Response.class);
    	*/
        //System.out.println(ResponseEntity);
    	/*
    	String masterPrivateIp = "127.0.0.1";
    	String key = "id";
    	String uri =  "http://"+masterPrivateIp+":8080/internal/write/"+ key+"/"+record;
    	RestTemplate restTemplate = new RestTemplate();
    	Response res = restTemplate.postForObject(uri, null,Response.class);
        return new ResponseEntity<>(HttpStatus.CREATED);
        */
    	/* testing template
    	//RestTemplate restTemplate = new RestTemplate();
    	//Response res = restTemplate.postForObject(uri, null,Response.class);
        //return new ResponseEntity<>(HttpStatus.CREATED);
         */
    	
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/{key}")
    public Map<String, Object> deleteSM(@PathVariable("key") String key) throws SMException{

    	System.out.println("start delete!");
    	SMImplVersion2 sm = new SMImplVersion2();
    	 Record found = null;
         SM.OID rec1_oid = null;
    	 Record rec1 = new Record(20);
         rec1.setBytes(key.getBytes());
      // store & fetch
         try {
           System.out.println( "Storing: test 1" ) ;
           rec1_oid = (SM.OID) sm.store(rec1);
           found = sm.fetch(rec1_oid);
           System.out.print( "XXXXXX: \n" ) ;           
           System.out.print(rec1_oid.getKey() ) ;
           System.out.print( "\n" ) ;
           System.out.print( "YYYYYY: \n" ) ;
           System.out.print(rec1_oid.toString()) ;
           System.out.print( "\n" ) ;
           System.out.print( "Fetch Got Rec: \n" ) ;
           System.out.write(found.getBytes(0,0));
           System.out.println("") ;
           System.out.println("Fetch Successful!");
         } catch ( Exception e ) {
           System.out.println( "Fetch Failed!" ) ; 
         }
    	
    	//SM sm = smRepository.findByKey(key);
    	//smRepository.deleteByKey(key);
    	//System.out.println("I am sending delete request to master!");
    	System.out.println("I am sending delete request to slave!");
    	/*
    	String uri1 = "http://"+Slave1IP+"/internal/SM/"+key;
    	String uri2 = "http://"+Slave2IP+"/internal/SM/"+key;
    	//String uri2 = "http://"+Slave1IP+":8080/internal/copy/"+sm.getKey+"/"+sm.getValue();
            RestTemplate restTemplate = new RestTemplate();
    	restTemplate.delte(uri1);
    	restTemplate.delte(uri2);
    	*/
    	String MasterIP ="1.1.1.1";
    	String sm1 ="RESPONSE SUCCESS";
    	Map<String, Object> response = new HashMap<String, Object>();
    	//reponse.put("", stu);
    	response.put("Master Node", key);
    	response.put("delete", sm1);
    	return response;
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