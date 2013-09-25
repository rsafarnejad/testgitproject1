package gov.eeoc.complainantportal.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.lang3.RandomStringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.eeoc.complainantportal.entity.ComplainantData;
import gov.eeoc.complainantportal.model.DocumentContent;
import gov.eeoc.complainantportal.model.DocumentDetails;
import gov.eeoc.complainantportal.util.AlfrescoAccessManager;

/**
 * Session Bean implementation class ComplaintantDataService
 */
@Stateless
public class ComplainantDataService {

	@PersistenceContext(unitName = "SystemicWatchList")
	private EntityManager em;

	private String alfrescoTempFolderId;
	
	@Inject
	private RandomQuestionAnswerBuilder randomQuestionBuilder;
	
	@Inject
	private AlfrescoAccessManager accessManager;

	private final Logger log = LoggerFactory.getLogger(ComplainantDataService.class);

	private final String completeFolderPath = "/EEOC/Applications/TempComplaintFolder";
	
	private final int TOKEN_LENGTH = 6;
	
	@PostConstruct
	private void init() {
		alfrescoTempFolderId = accessManager.getFolderIdByCompleteName(completeFolderPath);
	}
	
	public String generateToken() {
		return RandomStringUtils.randomAlphanumeric(TOKEN_LENGTH);
	}

	public long addComplaintantData(ComplainantData data) {
		ComplainantData insertedObject = em.merge(data);
		return insertedObject.getComplainantDataId();
	}

	public ComplainantData getComplainantDataByPk(Long pk) {
		return em.find(ComplainantData.class, pk);
	}

	public boolean addDocument(Long complaintDataId, String documentType,
			DocumentDetails documentDetail) {
		String alfrescoId = null;
		alfrescoId = accessManager.addDocument(alfrescoTempFolderId,documentDetail);
		ComplainantData data = em.find(ComplainantData.class, complaintDataId);
		if (data != null) {
			if (data.getAlfrescoId() == null) {
				data.setAlfrescoId(alfrescoId);
				data.setFileName(documentDetail.getFileName());
				data.setDocumentType(documentType);
			} else {
				ComplainantData newRecord = generateNewComplaintData(data);
				newRecord.setAlfrescoId(alfrescoId);
				newRecord.setFileName(documentDetail.getFileName());
				newRecord.setDocumentType(documentType);
				em.persist(newRecord);
			}
		}
		return alfrescoId == null ? false : true;
	}

	public List<ComplainantData> getAllDocuments() {
		Query query = em.createNamedQuery("ComplainantData.findAll");
		return query.getResultList();
	}
	
	public boolean delete(Long complaintDataId){
		if(complaintDataId == null){
			throw new IllegalArgumentException("Null parameter was passed");
		}
		ComplainantData data = em.find(ComplainantData.class, complaintDataId);
		if(data == null){
			throw new RuntimeException("No record found with id :" + complaintDataId);
		}
		// delete documents from Alfresco
		 List<DocumentDetails> documents  = getDocumentsByComplaintId(complaintDataId);
		 for(DocumentDetails document : documents){
			 deleteDocument(document.getDocumentNodeId());
		 }
		 //delete documents for database
		 Query query = em.createNamedQuery("ComplainantData.deleteByComplaintIdOrRefId");
		 query.setParameter("complainantDataId", complaintDataId);
		 int numOfUpdate = query.executeUpdate();
		 return numOfUpdate > 0 ? true : false;
	}

	public Map<String, String> generateChallengeQuestionAnswer(){
		return randomQuestionBuilder.generateChallengeQuestionAnswer();
	}

	public List<DocumentDetails> getDocumentsByComplaintId(Long complaintId) {
		Query query = em.createNamedQuery("ComplainantData.findByPkOrRefId");
		query.setParameter("complainantDataId", complaintId);
		query.setParameter("refrenceId", complaintId);
		List<ComplainantData> list = query.getResultList();
		List<DocumentDetails> result = new ArrayList<DocumentDetails> (list.size());
		for(ComplainantData data : list){
			DocumentDetails docDetail = new DocumentDetails();
			docDetail.setDocumentNodeId(data.getAlfrescoId());
			docDetail.setFileName(data.getFileName());
			result.add(docDetail);
		}	
		return result;
	}
		
	public DocumentContent getDocumentContentByAlfrecoId(String alfrescoId) {
		return accessManager.getDocumentContent(alfrescoId);
	}

	public boolean deleteDocument(String documentId) {
		if (documentId == null) {
			throw new IllegalArgumentException("Null document Id passed");
		}
		log.info("Deleting object with alfresco id : {}", documentId);
		Query query = em.createNamedQuery("ComplainantData.deleteByAlfrescoId");
		query.setParameter("alfrescoId", documentId);
		int numOfDelete = query.executeUpdate();
		// numOfDelete would be zero if the document is the 1st document. In that case, nullyfy alfrescoId, fileName, fileType 
		// instead of delete an entire row.
		if(numOfDelete == 0){
			query = em.createNamedQuery("ComplainantData.resetAlfrescoId");
			query.setParameter("alfrescoId", documentId);
			int numOfUpdates = query.executeUpdate();
			log.info("Resetting AlfrescoId : {}", numOfUpdates);
		}	
		return accessManager.deleteDocument(documentId);
	}	
	
	public StreamedContent downloadDocument(String alfrescoId){
		DocumentContent dc = accessManager.getDocumentContent(alfrescoId);
		if(dc.getContent() != null){
			InputStream ins = dc.getContent();
			String fileName = dc.getFileName();
			String fileType = dc.getMimeType();
			return new DefaultStreamedContent(ins, fileType, fileName);
		}
		return null;
	}
	
	private ComplainantData generateNewComplaintData(ComplainantData data) {
		ComplainantData record = new ComplainantData();
		record.setRefrenceId(data.getComplainantDataId());
		record.setAgencyName(data.getAgencyName());
		record.setAgencyNumber(data.getAgencyNumber());
		if (data.getAppealNumber() != null) {
			record.setAppealNumber(data.getAppealNumber());
		}
		record.setComplainantFirstName(data.getComplainantFirstName());
		record.setComplainantLastName(data.getComplainantLastName());
		record.setDocumentSubmitterFirstName(data
				.getDocumentSubmitterFirstName());
		record.setDocumentSubmitterLastName(data.getDocumentSubmitterLastName());
		record.setDocumentType(data.getDocumentType());
		
		return record;
	}
}
