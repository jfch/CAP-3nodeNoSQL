package com.example;

import java.util.ArrayList;
import java.util.List;

public class AnEntity {
	String Key;	
	//int Value;
	Value  value;
	//List<Value>  value;
	
	AnEntity(){
		value = new Value();
		
	}
	public String getKey() {
		return Key;
	}
	public void setKey(String key) {
		Key = key;
	}
	
	/*
	public int getValue() {
		return Value;
	}
	public void setValue(int value) {
		Value = value;
	}
	*/	

	public Value getValue() {
		return value;
	}
	public void setValue(Value value) {
		this.value = value;
	}

	public class Value{
		String Name;
		String Address;
		public String getName() {
			return Name;
		}
		public void setName(String name) {
			Name = name;
		}
		public String getAddress() {
			return Address;
		}
		public void setAddress(String address) {
			Address = address;
		}
		
	}

}
