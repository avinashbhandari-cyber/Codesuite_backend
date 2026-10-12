package com.divyansh.online.compiler.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.NaturalId;

@Entity
public class ContestEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(unique = true, nullable = false, updatable = false, name = "Q_ID")
	private Long ContestID;
	@NaturalId
	private String ContestEmail;
	@NaturalId(mutable = true)
	private String ContestTitle;
	@NaturalId(mutable = true)
	private String ContestStatus;
	@NaturalId(mutable = true)
	private String ContestTimeStamp;
	@NaturalId(mutable = true)
	private String ContestAbout;
	
	public ContestEntity() {
		super();
	}

	public ContestEntity(String contestEmail, String contestTitle, String contestStatus,
			String contestTimeStamp, String contestAbout) {
		super();
		ContestEmail = contestEmail;
		ContestTitle = contestTitle;
		ContestStatus = contestStatus;
		ContestTimeStamp = contestTimeStamp;
		ContestAbout = contestAbout;
	}

	public Long getContestID() {
		return ContestID;
	}

	public void setContestID(Long contestID) {
		ContestID = contestID;
	}

	public String getContestEmail() {
		return ContestEmail;
	}

	public void setContestEmail(String contestEmail) {
		ContestEmail = contestEmail;
	}

	public String getContestTitle() {
		return ContestTitle;
	}

	public void setContestTitle(String contestTitle) {
		ContestTitle = contestTitle;
	}

	public String getContestStatus() {
		return ContestStatus;
	}

	public void setContestStatus(String contestStatus) {
		ContestStatus = contestStatus;
	}

	public String getContestTimeStamp() {
		return ContestTimeStamp;
	}

	public void setContestTimeStamp(String contestTimeStamp) {
		ContestTimeStamp = contestTimeStamp;
	}

	public String getContestAbout() {
		return ContestAbout;
	}

	public void setContestAbout(String contestAbout) {
		ContestAbout = contestAbout;
	}

	@Override
	public String toString() {
		return "ContestEntity [ContestID=" + ContestID + ", ContestEmail=" + ContestEmail + ", ContestTitle="
				+ ContestTitle + ", ContestStatus=" + ContestStatus + ", ContestTimeStamp=" + ContestTimeStamp
				+ ", ContestAbout=" + ContestAbout + "]";
	}	
	
}
