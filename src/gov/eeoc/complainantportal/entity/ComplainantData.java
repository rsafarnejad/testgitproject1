package gov.eeoc.complainantportal.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the COMPLAINANT_DATA database table.
 * 
 */
@Entity
@Table(name = "COMPLAINANT_DATA")
@NamedQueries({ @NamedQuery(name = "ComplainantData.findAll", query = "SELECT s FROM ComplainantData s order by complainantDataId desc"),
				@NamedQuery(name = "ComplainantData.deleteByAlfrescoId", query = "DELETE FROM ComplainantData where alfrescoId = :alfrescoId and refrenceId is not null"),
				@NamedQuery(name = "ComplainantData.deleteByComplaintIdOrRefId", query = "DELETE FROM ComplainantData where complainantDataId = :complainantDataId or refrenceId = :complainantDataId"),
				@NamedQuery(name = "ComplainantData.resetAlfrescoId", query = "UPDATE ComplainantData set alfrescoId = null, fileName = null, documentType = null  where alfrescoId = :alfrescoId"),
				@NamedQuery(name = "ComplainantData.findByPkOrRefId", query = "SELECT s FROM ComplainantData s where (complainantDataId = :complainantDataId and alfrescoId is not null) or (refrenceId = :refrenceId and alfrescoId is not null)")
	})
public class ComplainantData implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "COMPLAINANT_DATA_SEQ_GENERATOR", sequenceName = "COMPLAINANT_DATA_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMPLAINANT_DATA_SEQ_GENERATOR")
	@Column(name = "COMPLAINANT_DATA_ID")
	private long complainantDataId;

	@Column(name = "AGENCY_NAME")
	private String agencyName;

	@Column(name = "AGENCY_NUMBER")
	private String agencyNumber;

	@Column(name = "APPEAL_NUMBER")
	private String appealNumber;

	@Column(name = "COMPLAINANT_FIRST_NAME")
	private String complainantFirstName;

	@Column(name = "COMPLAINANT_LAST_NAME")
	private String complainantLastName;

	@Column(name = "DOCUMENT_SUBMITTER_FIRST_NAME")
	private String documentSubmitterFirstName;

	@Column(name = "DOCUMENT_SUBMITTER_LAST_NAME")
	private String documentSubmitterLastName;

	@Column(name = "DOCUMENT_TYPE")
	private String documentType;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_ON")
	private Date createdOn;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "LAST_MODIFIED_BY")
	private Long lastModifiedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_MODIFIED_ON")
	private Date lastModifiedOn;

	@Column(name = "ALFRESCO_NODE_ID")
	private String alfrescoId;

	@Column(name = "FILE_NAME")
	private String fileName;

	@Column(name = "REFRENCE_ID")
	private Long refrenceId;
	
	@Column(name = "COMPLAINANT_EMAIL")
	private String complainantEmail;

	public ComplainantData() {
		createdOn = new Date(System.currentTimeMillis());
	}

	public long getComplainantDataId() {
		return this.complainantDataId;
	}

	public void setComplainantDataId(long complainantDataId) {
		this.complainantDataId = complainantDataId;
	}

	public String getAgencyName() {
		return this.agencyName;
	}

	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}

	public String getAgencyNumber() {
		return this.agencyNumber;
	}

	public void setAgencyNumber(String agencyNumber) {
		this.agencyNumber = agencyNumber;
	}

	public String getAppealNumber() {
		return this.appealNumber;
	}

	public void setAppealNumber(String appealNumber) {
		this.appealNumber = appealNumber;
	}

	public String getComplainantFirstName() {
		return this.complainantFirstName;
	}

	public void setComplainantFirstName(String complainantFirstName) {
		this.complainantFirstName = complainantFirstName;
	}

	public String getComplainantLastName() {
		return this.complainantLastName;
	}

	public void setComplainantLastName(String complainantLastName) {
		this.complainantLastName = complainantLastName;
	}

	public String getDocumentSubmitterFirstName() {
		return this.documentSubmitterFirstName;
	}

	public void setDocumentSubmitterFirstName(String documentSubmitterFirstName) {
		this.documentSubmitterFirstName = documentSubmitterFirstName;
	}

	public String getDocumentSubmitterLastName() {
		return this.documentSubmitterLastName;
	}

	public void setDocumentSubmitterLastName(String documentSubmitterLastName) {
		this.documentSubmitterLastName = documentSubmitterLastName;
	}

	public String getDocumentType() {
		return this.documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(Long lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public Date getLastModifiedOn() {
		return lastModifiedOn;
	}

	public void setLastModifiedOn(Date lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}

	public String getAlfrescoId() {
		return alfrescoId;
	}

	public Long getRefrenceId() {
		return refrenceId;
	}

	public void setRefrenceId(Long refrenceId) {
		this.refrenceId = refrenceId;
	}

	public void setAlfrescoId(String alfrescoId) {
		this.alfrescoId = alfrescoId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getComplainantEmail() {
		return complainantEmail;
	}

	public void setComplainantEmail(String complainantEmail) {
		this.complainantEmail = complainantEmail;
	}

	

}

