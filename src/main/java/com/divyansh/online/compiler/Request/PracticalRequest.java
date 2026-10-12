package com.divyansh.online.compiler.Request;

import org.springframework.web.multipart.MultipartFile;

public class PracticalRequest {

	private String email;
	private String title;
	private String type;
	private String hints;
	private String relatedTopic;
	private MultipartFile question;
	private MultipartFile testcase;
	private MultipartFile testcaseAns;
	
	public PracticalRequest(String email, String title,  MultipartFile question, MultipartFile testcase, 
			MultipartFile testcaseAns,String type, String hints, String relatedTopic) {
		super();
		this.email = email;
		this.title = title;
		this.question = question;
		this.testcase = testcase;
		this.testcaseAns = testcaseAns;
		this.type = type;
		this.hints = hints;
		this.relatedTopic = relatedTopic;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHints() {
		return hints;
	}

	public void setHints(String hints) {
		this.hints = hints;
	}

	public String getRelatedTopic() {
		return relatedTopic;
	}

	public void setRelatedTopic(String relatedTopic) {
		this.relatedTopic = relatedTopic;
	}

	public MultipartFile getQuestion() {
		return question;
	}

	public void setQuestion(MultipartFile question) {
		this.question = question;
	}

	public MultipartFile getTestcase() {
		return testcase;
	}

	public void setTestcase(MultipartFile testcase) {
		this.testcase = testcase;
	}

	public MultipartFile getTestcaseAns() {
		return testcaseAns;
	}

	public void setTestcaseAns(MultipartFile testcaseAns) {
		this.testcaseAns = testcaseAns;
	}
	
	
	
}
