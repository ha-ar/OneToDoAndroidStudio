package com.vector.onetodo;

import java.io.Serializable;

public class Person implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4322748841234891697L;
	private String name;
	private String email;

	public Person(String n, String e) {
		name = n;
		email = e;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public String toString() {
		return name;
	}
}