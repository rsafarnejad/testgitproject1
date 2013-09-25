package gov.eeoc.complainantportal.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

public class DocumentDetails extends DocumentContent implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String documentName;
	private String createdBy;
	private String createdDateStr;
	private String documentNodeId;
	private String tabType;
	private String subCategory;
	private Date relatedDate;
	private String relatedDateStr;
	private String title;
	private boolean documentOwner;
	
	// following attributes added for public Complainant portal
	private String appealNumber;
	private String complaintId;
	private final static DateFormat defaultDf = DateFormat.getDateInstance();
	
	public String getDocumentName() {
		return documentName;
	}
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
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
	public String getDocumentNodeId() {
		return documentNodeId;
	}
	public void setDocumentNodeId(String documentNode) {
		this.documentNodeId = documentNode;
	}
	public String getTabType() {
		return tabType;
	}
	public void setTabType(String tabType) {
		this.tabType = tabType;
	}
	public String getSubCategory() {
		return subCategory;
	}
	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}
	public Date getRelatedDate() {
		return relatedDate;
	}
	public void setRelatedDate(Date relatedDate) {
		this.relatedDate = relatedDate;
	}
	public String getRelatedDateStr() {
		if(relatedDate != null){
			return defaultDf.format(relatedDate);
		}
		return relatedDateStr;
	}
	public void setRelatedDateStr(String relatedDateStr) {
		this.relatedDateStr = relatedDateStr;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public boolean isDocumentOwner() {
		return documentOwner;
	}
	public void setDocumentOwner(boolean documentOwner) {
		this.documentOwner = documentOwner;
	}
	public String getAppealNumber() {
		return appealNumber;
	}
	public void setAppealNumber(String appealNumber) {
		this.appealNumber = appealNumber;
	}
	public String getComplaintId() {
		return complaintId;
	}
	public void setComplaintId(String complaintId) {
		this.complaintId = complaintId;
	}
	@Override
	public String toString() {
		return "DocumentDetails [documentName=" + documentName + ", createdBy="
				+ createdBy + ", createdDateStr=" + createdDateStr
				+ ", documentNodeId=" + documentNodeId + ", tabType=" + tabType
				+ ", subCategory=" + subCategory + ", relatedDate="
				+ relatedDate + ", title=" + title + "]";
	}
	

}

