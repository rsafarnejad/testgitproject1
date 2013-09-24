package gov.eeoc.complainantportal.model;

import java.io.Serializable;

public class NotesDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String createdBy;
	private String createdDateStr;
	private String notes;
	
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedDateStr() {
		return createdDateStr;
	}
	public void setCreatedDateStr(String createdDateStr) {
		this.createdDateStr = createdDateStr;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	@Override
	public String toString() {
		return "NotesDetails [createdBy=" + createdBy + ", createdDateStr="
				+ createdDateStr + ", notes=" + notes + "]";
	}
	
	
}

