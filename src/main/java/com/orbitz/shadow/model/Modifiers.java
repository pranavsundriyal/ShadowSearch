package com.orbitz.shadow.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Modifiers {
	private String searchHost;
	private String transform;
	private String somethingElse;
	
	public Modifiers(@JsonProperty("searchHost") String sh, @JsonProperty("transform") String trans, @JsonProperty("somethingElse") String ex) {
		this.setSearchHost(sh);
		this.setTransform(trans);
		this.setSomethingElse(ex);
	}

	public String getSearchHost() {
		return searchHost;
	}

	public void setSearchHost(String searchHost) {
		this.searchHost = searchHost;
	}

	public String getTransform() {
		return transform;
	}

	public void setTransform(String transform) {
		this.transform = transform;
	}

	public String getSomethingElse() {
		return somethingElse;
	}

	public void setSomethingElse(String somethingElse) {
		this.somethingElse = somethingElse;
	}

}
