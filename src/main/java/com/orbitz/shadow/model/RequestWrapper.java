package com.orbitz.shadow.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestWrapper {
	private Request request;
	private Modifiers modifiers;
	
	public RequestWrapper(@JsonProperty("request") Request req, @JsonProperty("modifiers") Modifiers mods){
		this.request = req;
		this.modifiers = mods;
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public Modifiers getModifiers() {
		return modifiers;
	}

	public void setModifiers(Modifiers modifiers) {
		this.modifiers = modifiers;
	}
	
	

}
