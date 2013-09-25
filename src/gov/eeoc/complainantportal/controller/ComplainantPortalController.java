package gov.eeoc.complainantportal.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.apache.commons.io.FilenameUtils;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.UploadedFile;

import gov.eeoc.complainantportal.entity.ComplainantData;
import gov.eeoc.complainantportal.service.ComplainantDataService;
import gov.eeoc.complainantportal.service.WebCacheManager;
import gov.eeoc.complainantportal.common.DocumentExistException;
import gov.eeoc.complainantportal.model.DocumentDetails;
import gov.eeoc.complainantportal.util.WebUtil;

@ManagedBean(name = "complainantPortalController")
@SessionScoped
public class ComplainantPortalController implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private ComplainantDataService complainantDataService;
	@Inject
	private WebCacheManager webCacheManager;
	private ComplainantData complainantdata;
	private UploadedFile file;
	private String title;
	private long id;
	private String documentType;
	private boolean showUpload = false;
	private List<DocumentDetails> compDataList = new ArrayList<DocumentDetails>();
	private static String COMP_DATA_LIST = "compDataList";
	private DocumentDetails selectedDocForDelete;
	private boolean disable = true;
	private String token;

	public ComplainantData getComplainantdata() {
		if (complainantdata == null) {
			complainantdata = new ComplainantData();
		}
		return complainantdata;
	}

	public void setComplainantdata(ComplainantData complainantdata) {
		this.complainantdata = complainantdata;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public boolean isShowUpload() {
		return showUpload;
	}

	public void setShowUpload(boolean showUpload) {
		this.showUpload = showUpload;
	}

	public List<DocumentDetails> getCompDataList() {
		return compDataList;
	}

	public void setCompDataList(List<DocumentDetails> compDataList) {
		this.compDataList = compDataList;
	}

	public DocumentDetails getSelectedDocForDelete() {
		return selectedDocForDelete;
	}

	public void setSelectedDocForDelete(DocumentDetails selectedDocForDelete) {
		this.selectedDocForDelete = selectedDocForDelete;
	}

	public boolean isDisable() {
		return disable;
	}

	public void setDisable(boolean disable) {
		this.disable = disable;
	}
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public ComplainantPortalController() {

	}

	/**
	 * Submit Process
	 * 
	 */
	public void submit() {

		boolean isValidToken = false;
		 isValidToken = webCacheManager.isValidToken(this.complainantdata.getComplainantEmail().toLowerCase(), token);
		if (isValidToken) {
			setDisable(false);
			System.out.println("Submission in Progress");
			System.out.println("Trying to insert record");
			id = complainantDataService.addComplaintantData(this.complainantdata);
			System.out.println("The long value is id:" + id);
			if (id == 0) {
				FacesMessage msg = new FacesMessage("Please try after some time.");
				FacesContext.getCurrentInstance().addMessage(null, msg);
			} else {
				setShowUpload(true);
				FacesMessage msg = new FacesMessage("Success:Please upload the  file/files ");
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
		} 
		else {
			FacesMessage msg = new FacesMessage("The security token is not matching/expired ");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}

	/**
	 * Document Upload Process
	 * 
	 */
	public void upload() throws DocumentExistException {
		String fileName = "";
		boolean uploaded = false;
		if (this.file == null || this.documentType == null) {
			FacesMessage msg = new FacesMessage("Failed : Please select a file to upload. ");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		} else {
			//File size should be less than 25MB
			if(this.file.getSize()<26214400){
			try {
				fileName = FilenameUtils.getName(this.file.getFileName());
				this.title = FilenameUtils.removeExtension(fileName);
				DocumentDetails dc = new DocumentDetails();
				dc.setContent(file.getInputstream());
				dc.setMimeType(file.getContentType());
				dc.setFileName(fileName);
				dc.setFileLength(file.getSize());
				dc.setTitle(title);
				dc.setAppealNumber(complainantdata.getAppealNumber());
				dc.setComplaintId(String.valueOf(id));
			
				this.complainantdata.setDocumentType(getDocumentType());

				uploaded = complainantDataService.addDocument(id,this.complainantdata.getDocumentType(), dc);
				System.out.println("Id value to Alfresco :" + id);

				if (uploaded) {
					this.compDataList = complainantDataService.getDocumentsByComplaintId(id);
					System.out.println("size of the list:"+ compDataList.size());
					WebUtil.SetSessionVariable(COMP_DATA_LIST, compDataList);
					FacesMessage msg = new FacesMessage("Success! "+ this.file.getFileName() + " is uploaded.");
					FacesContext.getCurrentInstance().addMessage(null, msg);
				} else {
					FacesMessage msg = new FacesMessage("Failed! "+ this.file.getFileName() + " is not uploaded.");
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			}else{
				FacesMessage msg = new FacesMessage("Failed!"+ this.file.getFileName() + " is not uploaded as the file exceeds the maximum file size of 25MB.");
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
		}
	}

	/**
	 * this method is for editing/saving in metadata for caselistdoc
	 * 
	 * @param event
	 */
	public void onCaseDocEdit(RowEditEvent event) {
		DocumentDetails dc = (DocumentDetails) event.getObject();
		this.compDataList = complainantDataService.getDocumentsByComplaintId(id);
		WebUtil.SetSessionVariable(COMP_DATA_LIST, compDataList);
	
	}

	@PostConstruct
	public void init() {
	
	}

	/**
	 * Selected Document Delete Process
	 * 
	 */

	public void deleteDocument() throws IOException {
		System.out.println("deleteDocument()..start");
		DocumentDetails docDetail = this.selectedDocForDelete;
		boolean deleted = false;
		deleted = complainantDataService.deleteDocument(docDetail.getDocumentNodeId());

		if (deleted) {
			System.out.println("File deletion Success");
			this.compDataList = complainantDataService
					.getDocumentsByComplaintId(id);
			//FacesMessage msg = new FacesMessage("Success!"+ docDetail.getDocumentName() + " is deleted.");
			FacesMessage msg = new FacesMessage("Success! The selected file is deleted.");
			FacesContext.getCurrentInstance().addMessage(null, msg);

		} else {
			System.out.println("File deletion Failed");
			// FacesMessage msg = new FacesMessage("Failed "+
			// docDetail.getDocumentName() + " is not deleted.");
			FacesMessage msg = new FacesMessage("Failed! The selected file is not deleted.");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}

	/**
	 * Confirmation Process
	 * 
	 */
	public void confirm() throws IOException {
		System.out.println("confirm()..start");
		System.out.println("Inside the confirmation button");
		FacesContext.getCurrentInstance().getExternalContext()
				.redirect("confirmation.jsf");

	}

	/**
	 * Cancel Process
	 * 
	 */
	public void cancel() {
		System.out.println("cancel()..start");
		boolean deleted=false;
		deleted=complainantDataService.delete(id);
		if(deleted){
			System.out.println("Delete success");
			setComplainantdata(null);
			//setDocumentSubmitterEmail("");
			setCompDataList(null);
			setToken("");
			setDocumentType("");
			setDisable(true);
			setShowUpload(false);
		}else
			System.out.println("Delete failed");
	}
	

		 
}   



