package com.example.database.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Topic {
	public String downloadeUrl ;
	public String uid;
	public String author;
	public String title;
	public String body;
	public int starCount = 0;

	public int voteCount = 0;
	public Map<String, Boolean> votes = new HashMap<>();

	public int joinCount = 0;
	public Map<String, Boolean> joins = new HashMap<>();

	public int redeemCount = 0;

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
		result.put("Title", title);
		result.put("TimeAdd", ServerValue.TIMESTAMP);


		return result;
	}
}