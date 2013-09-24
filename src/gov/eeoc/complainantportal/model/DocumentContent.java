package gov.eeoc.complainantportal.model;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;

public class DocumentContent implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private InputStream content;
	private String mimeType;
	private String fileName;
	private long fileLength;
	
//	private String tabType;
//	private String subCategory;
//	private Date relatedDate;
	
	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
/*
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

	public DocumentContent(InputStream content, String mimeType,
			String fileName, long fileLength, String tabType,String subCategory,
			Date relatedDate, String title) {
		super();
		this.content = content;
		this.mimeType = mimeType;
		this.fileName = fileName;
		this.fileLength = fileLength;
		this.tabType = tabType;
		this.subCategory = subCategory;
		this.relatedDate = relatedDate;
		this.title = title;
	}
*/	
	public DocumentContent() {
		super();
	}

	public DocumentContent(InputStream content, String mimeType,
			String fileName, long fileLength) {
		this.content = content;
		this.mimeType = mimeType;
		this.fileName = fileName;
		this.fileLength = fileLength;
	}

	public InputStream getContent() {
		return content;
	}
	public void setContent(InputStream content) {
		this.content = content;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public long getFileLength() {
		return fileLength;
	}
	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}
	
	

}

