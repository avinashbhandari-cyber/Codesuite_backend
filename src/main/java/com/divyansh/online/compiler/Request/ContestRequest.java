package com.divyansh.online.compiler.Request;

import org.springframework.web.multipart.MultipartFile;

public class ContestRequest {

	private String email;
	private String title;
	private String status;
	private MultipartFile about;
	
	public ContestRequest(String email, String title, String status, MultipartFile about) {
		super();
		this.email = email;
		this.title = title;
		this.status = status;
		this.about = about;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public MultipartFile getAbout() {
		return about;
	}

	public void setAbout(MultipartFile about) {
		this.about = about;
	}
	
	
}
