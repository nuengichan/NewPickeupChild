package com.example.database.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Topic extends Post {
	public String downloadeUrl ;
	public String uid;
	public String author;
	public String title;
	public String body;
	public Map<String, String> Timeadd ;

	public Topic() {
		// Default constructor required for calls to DataSnapshot.getValue(Topic.class)
	}

	public Topic(String uid, String author, String title ) {
		this.uid = uid;
		this.author = author;
		this.title = title;


	}

	@Exclude
	public Map<String, Object> toMap() {
		HashMap<String, Object> result = new HashMap<>();
		result.put("uid", uid);
		result.put("author", author);
		result.put("title", title);
		result.put("body", body);
		result.put("TimeAdd",  Timeadd = ServerValue.TIMESTAMP);


		return result;
	}

//	//public Map<String, String> getTimestamp() {
//		return Timeadd;
//	}

//	public void setTimestamp(Map<String, String> timestamp) {
//		this.Timeadd = timestamp;
//	}
//	public String author() {
//
//		return author;
//	}
//
//
//	public String uid() {
//
//		return uid;
//	}


}