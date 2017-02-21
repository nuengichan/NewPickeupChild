package com.example.database.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
	public String username;
	public String email;
	public String status;

	public User() {
		// Default constructor required for calls to DataSnapshot.getValue(User.class)
	}

	public User(String username, String email , String status) {
		this.username = username;
		this.email = email;
		this.status = status;
	}
}