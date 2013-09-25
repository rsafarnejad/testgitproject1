package gov.eeoc.complainantportal.model;

import java.io.InputStream;
import java.io.Serializable;

public class DocumentContent implements Serializable {	

	private static final long serialVersionUID = 1L;
	
	private InputStream content;
	private String mimeType;
	private String fileName;
	private long fileLength;
	private String title;

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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}	

}

