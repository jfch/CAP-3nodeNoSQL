package com.example;

import java.util.ArrayList;
import java.util.List;

import com.example.SMImplVersion2.OID;
import com.example.SM.Record;
import com.example.SM.SMException;

public class AddressList {
	
	List<String> key;
	List<SM.OID> keyOIDlist;
	List<SM.OID> nameOIDlist;
	List<SM.OID> addressOIDlist;
	
	AddressList(){
		key = new ArrayList();
		keyOIDlist = new ArrayList();
		nameOIDlist = new ArrayList();
		addressOIDlist = new ArrayList();
		
	}

	public List<String> getKey() {
		return key;
	}

	public void setKey(List<String> key) {
		this.key = key;
	}

	public List<SM.OID> getKeyOIDlist() {
		return keyOIDlist;
	}

	public void setKeyOIDlist(List<SM.OID> keyOIDlist) {
		this.keyOIDlist = keyOIDlist;
	}

	public List<SM.OID> getNameOIDlist() {
		return nameOIDlist;
	}

	public void setNameOIDlist(List<SM.OID> nameOIDlist) {
		this.nameOIDlist = nameOIDlist;
	}

	public List<SM.OID> getAddressOIDlist() {
		return addressOIDlist;
	}

	public void setAddressOIDlist(List<SM.OID> addressOIDlist) {
		this.addressOIDlist = addressOIDlist;
	}
	
	

}
