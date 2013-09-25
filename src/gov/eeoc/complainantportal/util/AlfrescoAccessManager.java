package gov.eeoc.complainantportal.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.eeoc.complainantportal.common.DocumentExistException;
import gov.eeoc.complainantportal.model.DocumentContent;
import gov.eeoc.complainantportal.model.DocumentDetails;
import gov.eeoc.complainantportal.model.NotesDetails;

@Singleton
public class AlfrescoAccessManager {

	private final Logger log = LoggerFactory.getLogger(AlfrescoAccessManager.class);

	private final static String FORUM_NAME_APPENDER = " case forum";
	private final static String TOPIC_NAME_APPENDER = " case topic";
	private final static String PROP_TAB_TYPE = "my:tabType";
	private final static String PROP_SUB_CAT_TYPE = "my:subCategory";
	private final static String PROP_REL_DATE = "my:relatedDate";
	private final static String PROP_TITLE = "my:title";

	// added for complaintanat portal
	private final static String PROP_APPEAL_NUMBER = "my:appealNumber";
	private final static String PROP_COMPLAINT_ID = "my:complaintId";
	private final static String PROP_APPLICATION_NAME = "my:applicationName";

	@Inject
	private CacheManager cacheManager;

	private SessionFactory sessionFactory;

	@Inject
	private SystemUtil sysUtil;

	private Properties properties;
	private String url;

	@PostConstruct
	public void init() {
		sessionFactory = SessionFactoryImpl.newInstance();
		url = cacheManager.getAlfrescoServiceURL();
		if (sysUtil != null) {
			properties = sysUtil.getPropertiesFromClasspath(SystemUtil.CONFIG_PROPERTY_FILE);
		}
	}

	public Session createSession(String userName, String password) {
		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put(SessionParameter.ATOMPUB_URL, url);
		parameter.put(SessionParameter.BINDING_TYPE,
				BindingType.ATOMPUB.value());
		parameter.put(SessionParameter.USER, userName);
		parameter.put(SessionParameter.PASSWORD, password);
		List<Repository> repositories = sessionFactory
				.getRepositories(parameter);
		return repositories.get(0).createSession();
	}

	/*
	 * Returns folder id
	 */
	public String createFolder(Session session, String parentFolderId,
			String folderName) {
		Map<String, String> props = new HashMap<String, String>();
		props.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
		props.put(PropertyIds.NAME, folderName);
		Folder appBaseFolder = null;
		if (parentFolderId == null) {
			appBaseFolder = getApplicationBaseFolder();
		} else {
			appBaseFolder = (Folder) session.getObject(parentFolderId);
		}
		if (appBaseFolder == null)
			return null;
		Folder caseFolder = appBaseFolder.createFolder(props);
		return caseFolder.getId();
	}

	public String createFolder(Session session, String folderName,boolean isCaseFolder) {

		String baseFolderPath = cacheManager.getValue(Const.ALFRESCO_FOLDER_PATH);
		String systemicCaseFolder = baseFolderPath + "/Systemic/";
		String chargeFolder = baseFolderPath + "/Charges/";
		String completeFolderName = null;
		Folder baseFolder = null;
		if (isCaseFolder) {
			baseFolder = getFolderByCompleteName(session, systemicCaseFolder);
			completeFolderName = systemicCaseFolder + folderName;
		} else {
			baseFolder = getFolderByCompleteName(session, chargeFolder);
			completeFolderName = chargeFolder + folderName;
		}
		if (baseFolder == null)
			return null;
		Folder folder = getFolderByCompleteName(session, completeFolderName);
		if (folder != null) {
			// folder is already existed, no need to create new one!
			return folder.getId();
		}
		Map<String, String> props = new HashMap<String, String>();
		props.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
		props.put(PropertyIds.NAME, folderName);
		Folder targetFolder = baseFolder.createFolder(props);
		return targetFolder.getId();
	}

	public boolean updateFolderName(String origName, String newName) {
		if (origName != null && newName != null) {
			String folderId = getFolderIdByName(origName);
			if (folderId != null) {
				CmisObject obj = getAdminSession().getObject(folderId);
				if (obj != null) {
					Map<String, String> props = new HashMap<String, String>();
					props.put(PropertyIds.NAME, newName);
					ObjectId oid = obj.updateProperties(props, true);
					return oid == null ? false : true;
				}
			}
		}
		return false;
	}

	public Folder getFolderByCompleteName(Session session, String name) {
		Folder folder = null;
		try {
			folder = (Folder) session.getObjectByPath(name);
		} catch (CmisObjectNotFoundException e) {
			// ignore
		}
		return folder;
	}

	public String getFolderIdByCompleteName(String name) {
		String id = null;
		try {
			Folder folder = (Folder) getAdminSession().getObjectByPath(name);
			if (folder != null) {
				id = folder.getId();
			}
		} catch (CmisObjectNotFoundException e) {
			// ignore
		}
		return id;
	}

	private Document getExistingDocument(Session session, Folder target,String newDocName) {
		// SELECT cmis:objectId from cmis:document where
		// in_folder('workspace://SpacesStore/3d1e69fb-ca98-4112-a27d-578e1b2f3cd7')
		// and cmis:name = 'Tesdt.txt'
		List<DocumentDetails> documents = this.getDocumentsByFolderId(target
				.getId());
		String existingDocId = null;
		for (DocumentDetails detail : documents) {
			String fileName = detail.getDocumentName();
			if (newDocName.equals(fileName)) {
				existingDocId = detail.getDocumentNodeId();
				break;
			}
		}
		if (existingDocId != null) {
			return (Document) session.getObject(existingDocId);
		}

		return null;
	}

	private Document getExistingDocument2(Session session, Folder target,
			String newDocName) {
		String folderId = target.getId();
		String query = "SELECT cmis:objectId  from cmis:document where in_folder('"
				+ folderId + "') and cmis:name = '" + newDocName + "'";
		ItemIterable<QueryResult> rows = session.query(query, false);
		String objectId = null;
		for (QueryResult row : rows) {
			List<PropertyData<?>> properties = row.getProperties();
			for (PropertyData<?> property : properties) {
				objectId = (String) property.getFirstValue();
				if (objectId != null) {
					break;
				}
			}
		}
		if (objectId != null) {
			return (Document) session.getObject(objectId);
		}
		return null;
	}

	public boolean addDocument(Session session, String folderNodeId,
			DocumentDetails dc) throws DocumentExistException {
		if (session == null) {
			session = getAdminSession();
		}
		boolean isSucessful = false;
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
		props.put(PropertyIds.OBJECT_TYPE_ID, "D:my:sop");
		props.put(PROP_TAB_TYPE, dc.getTabType());
		props.put(PROP_SUB_CAT_TYPE, dc.getSubCategory());
		props.put(PROP_REL_DATE, dc.getRelatedDate());
		props.put(PROP_TITLE, dc.getTitle());
		props.put(PropertyIds.NAME, dc.getFileName());

		props.put(PROP_APPEAL_NUMBER, dc.getAppealNumber());
		props.put(PROP_COMPLAINT_ID, dc.getComplaintId());

		ContentStream contentStream = session.getObjectFactory()
				.createContentStream(dc.getFileName(), dc.getFileLength(),
						dc.getMimeType(), dc.getContent());
		Folder target = (Folder) session.getObject(folderNodeId);
		Document existingDoc = getExistingDocument2(session, target,
				dc.getFileName());
		if (existingDoc == null) {
			Document newDocument = target.createDocument(props, contentStream,
					VersioningState.MAJOR);
			if (newDocument != null) {
				isSucessful = true;
			}
		} else {
			throw new DocumentExistException("File :" + dc.getFileName()
					+ " existed in this folder");
		}

		return isSucessful;

	}

	public String addDocument(String folderNodeId, DocumentDetails dc) {

		Session session = getAdminSession();
		String documentId = null;
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
		props.put(PropertyIds.OBJECT_TYPE_ID, "D:my:sop");
		props.put(PROP_TAB_TYPE, dc.getTabType());
		props.put(PROP_SUB_CAT_TYPE, dc.getSubCategory());
		props.put(PROP_REL_DATE, dc.getRelatedDate());
		props.put(PROP_TITLE, dc.getTitle());
		props.put(PropertyIds.NAME, dc.getFileName());

		props.put(PROP_APPEAL_NUMBER, dc.getAppealNumber());
		props.put(PROP_COMPLAINT_ID, dc.getComplaintId());

		ContentStream contentStream = session.getObjectFactory()
				.createContentStream(dc.getFileName(), dc.getFileLength(),
						dc.getMimeType(), dc.getContent());
		Folder target = (Folder) session.getObject(folderNodeId);
		Document existingDoc = getExistingDocument2(session, target,
				dc.getFileName());
		if (existingDoc == null) {
			Document newDocument = target.createDocument(props, contentStream,
					VersioningState.MAJOR);
			if (newDocument != null) {
				documentId = newDocument.getId();
			}
		}
		if (session != null) {
			session.clear();
		}
		return documentId;

	}

	public List<DocumentDetails> getDocumentIdByMetaData(String folderId,
			String metaDataType, String value) {

		StringBuilder builder = new StringBuilder(
				"select cmis:objectId from my:sop where ").append(metaDataType)
				.append(" = '").append(value).append("'")
				.append(" and in_tree('").append(folderId).append("')");
		log.info("CMIS Query : {} ", builder.toString());
		Session session = getAdminSession();
		session.clear();
		ItemIterable<QueryResult> rows = session.query(builder.toString(),
				false);
		log.info("Total Number of results = {} ", rows.getTotalNumItems());
		List<DocumentDetails> details = new ArrayList<DocumentDetails>();
		for (QueryResult row : rows) {
			List<PropertyData<?>> properties = row.getProperties();
			for (PropertyData<?> property : properties) {
				String objectId = (String) property.getFirstValue();
				Document document = (Document) session.getObject(objectId);
				DocumentDetails detail = new DocumentDetails();
				detail.setFileName(document.getContentStreamFileName());
				detail.setDocumentName(document.getContentStreamFileName());
				detail.setCreatedDateStr(DateFormat.getDateTimeInstance(
						DateFormat.MEDIUM, DateFormat.SHORT).format(
						document.getCreationDate().getTime()));
				detail.setFileLength(document.getContentStreamLength());
				detail.setMimeType(document.getContentStreamMimeType());
				detail.setDocumentNodeId(document.getId());
				details.add(detail);
			}

		}
		session.clear();
		return details;
	}

	public boolean updateDocument(Session session, String folderNodeId,
			DocumentDetails dc) {

		Map<String, Object> props = new HashMap<String, Object>();
		props.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
		props.put(PropertyIds.OBJECT_TYPE_ID, "D:my:sop");
		props.put(PROP_TAB_TYPE, dc.getTabType());
		props.put(PROP_SUB_CAT_TYPE, dc.getSubCategory());
		props.put(PROP_REL_DATE, dc.getRelatedDate());
		props.put(PROP_TITLE, dc.getTitle());
		props.put(PropertyIds.NAME, dc.getFileName());
		ContentStream contentStream = session.getObjectFactory()
				.createContentStream(dc.getFileName(), dc.getFileLength(),
						dc.getMimeType(), dc.getContent());
		Folder target = (Folder) session.getObject(folderNodeId);
		Document existingDoc = getExistingDocument2(session, target,
				dc.getFileName());
		if (existingDoc == null) {
			return false;
		}
		// update document
		contentStream = session.getObjectFactory().createContentStream(
				dc.getFileName(), dc.getFileLength(), dc.getMimeType(),
				dc.getContent());
		existingDoc.setContentStream(contentStream, true);
		existingDoc.updateProperties(props);
		return true;

	}

	public String getFolderIdByName(String folderName) {
		Session session = getAdminSession();
		// String baseFolderPath =
		// cacheManager.getValue("alfresco.private.folder.path"); -- commented
		// and will be removed
		String baseFolderPath = cacheManager
				.getValue(Const.ALFRESCO_FOLDER_PATH);
		String systemicCaseFolder = baseFolderPath + "/Systemic/" + folderName;
		String chargeFolder = baseFolderPath + "/Charges/" + folderName;
		Folder folder = getFolderByCompleteName(session, chargeFolder);
		if (folder != null)
			return folder.getId();
		// try to find case folder
		folder = getFolderByCompleteName(session, systemicCaseFolder);
		String folderId = null;
		if (folder != null) {
			folderId = folder.getId();
		} else {
			// create new charge folder
			// we assume charge folder creation because case folder is created
			// at the time of creating a new case
			folderId = this.createFolder(session, folderName, false);
		}

		session.clear();
		return folderId;
	}

	public DocumentContent getDocumentContent(String documentId) {
		Session session = getAdminSession();
		Document document = (Document) session.getObject(documentId);
		ContentStream content = document.getContentStream();
		DocumentContent docContent = new DocumentContent();
		docContent.setContent(content.getStream());
		docContent.setFileLength(content.getLength());
		docContent.setFileName(content.getFileName());
		docContent.setMimeType(content.getMimeType());
		session.clear();
		return docContent;
	}

	public boolean deleteDocument(Session session, String documentNodeId,
			String userName) {
		if (session == null) {
			session = getAdminSession();
		}
		boolean isSucessful = false;
		try {
			CmisObject object = session.getObject(documentNodeId);
			String createdBy = object.getCreatedBy();
			if (!createdBy.equalsIgnoreCase(userName)) {
				return false;
			}
			Document delDoc = (Document) object;
			delDoc.delete(true);
			isSucessful = true;
		} catch (CmisObjectNotFoundException e) {
			// ignore
		}
		session.clear();
		return isSucessful;

	}

	public boolean deleteDocument(String documentNodeId) {
		boolean isSucessful = false;
		Session session = getAdminSession();
		try {
			CmisObject object = session.getObject(documentNodeId);
			Document delDoc = (Document) object;
			delDoc.delete(true);
			isSucessful = true;
		} catch (CmisObjectNotFoundException e) {
			// ignore
		}
		session.clear();
		return isSucessful;
	}

	public List<DocumentDetails> getDocumentsByFolderName(String folderName) {
		String folderId = this.getFolderIdByFolderName(folderName);
		if (folderId != null) {
			return this.getDocumentsByFolderId(folderId);
		}
		return Collections.emptyList();
	}

	public List<DocumentDetails> getDocumentsByFolderId(String folderId) {
		if (folderId == null)
			return Collections.emptyList();

		Session session = getAdminSession();
		CmisObject caseFolder = session.getObject(folderId);
		if (caseFolder == null)
			return Collections.emptyList();
		Folder folder = (Folder) caseFolder;
		ItemIterable<CmisObject> children = folder.getChildren();
		List<DocumentDetails> list = new ArrayList<DocumentDetails>();
		for (CmisObject child : children) {
			// if (child.getType().getId().equals("cmis:document")) {
			if (child.getType().getId().equals("D:my:sop")) {
				DocumentDetails detail = new DocumentDetails();
				detail.setCreatedBy(child.getCreatedBy());
				detail.setDocumentName(child.getName());
				detail.setDocumentNodeId(child.getId());
				Calendar creationDate = child.getLastModificationDate();
				// Calendar creationDate = child.getCreationDate();
				// detail.setCreatedDateStr(dateFormat.format(creationDate
				// .getTime()));
				detail.setCreatedDateStr(DateFormat.getDateTimeInstance(
						DateFormat.MEDIUM, DateFormat.SHORT).format(
						creationDate.getTime()));
				GregorianCalendar relDate = (GregorianCalendar) child
						.getPropertyValue(PROP_REL_DATE);
				if (relDate != null) {
					detail.setRelatedDate(relDate.getTime());
				}
				detail.setTitle((String) child.getPropertyValue(PROP_TITLE));
				detail.setSubCategory((String) child
						.getPropertyValue(PROP_SUB_CAT_TYPE));
				detail.setTabType((String) child
						.getPropertyValue(PROP_TAB_TYPE));
				detail.setComplaintId((String) child
						.getPropertyValue(PROP_COMPLAINT_ID));
				Long loggedInUser = (Long) WebUtil
						.getSessionVariable(Const.STAFF_SEQ_ID);
				String strUser = "";
				if (loggedInUser != null) {
					strUser = String.valueOf(loggedInUser);
				}
				if (strUser.trim().equals(
						cacheManager.getStaffIdFromAlfrescoUser(
								child.getCreatedBy()).trim())) {
					detail.setDocumentOwner(true);
				} else {
					detail.setDocumentOwner(false);
				}

				list.add(detail);
			}
		}
		session.clear();
		return list;
	}

	public boolean updateProperties(Session session, DocumentDetails dc) {
		java.util.Map<java.lang.String, Object> properties = new HashMap<java.lang.String, Object>();
		if (dc.getRelatedDate() != null) {
			properties.put(PROP_REL_DATE, dc.getRelatedDate());
		}
		if (dc.getSubCategory() != null) {
			properties.put(PROP_SUB_CAT_TYPE, dc.getSubCategory());
		}
		if (dc.getTabType() != null) {
			properties.put(PROP_TAB_TYPE, dc.getTabType());
		}
		if (dc.getTitle() != null) {
			properties.put(PROP_TITLE, dc.getTitle());
		}
		CmisObject obj = session.getObject(dc.getDocumentNodeId());
		if (obj == null) {
			return false;
		}
		obj.updateProperties(properties, true);
		return true;
	}

	public Folder getBaseFolderForForums(Session session) {

		String query = "SELECT cmis:objectId FROM cmis:folder  WHERE cmis:name  = 'Cases Discussion'";
		ItemIterable<QueryResult> rows = session.query(query, false);
		String objectId = null;
		for (QueryResult row : rows) {
			List<PropertyData<?>> properties = row.getProperties();
			for (PropertyData<?> property : properties) {
				objectId = (String) property.getFirstValue();
				if (objectId != null) {
					break;
				}
			}
		}
		return (Folder) session.getObject(objectId);

	}

	private Folder createForumFolder(Folder target, String newFolderName) {
		Map<String, String> props = new HashMap<String, String>();
		props.put(PropertyIds.OBJECT_TYPE_ID, "F:fm:forum");
		props.put(PropertyIds.NAME, newFolderName);
		Folder newFolder = target.createFolder(props);
		return newFolder;
	}

	private Folder createTopic(Folder forumFolder, String topicName) {
		Map<String, String> props = new HashMap<String, String>();
		props.put(PropertyIds.OBJECT_TYPE_ID, "F:fm:topic");
		props.put(PropertyIds.NAME, topicName);
		Folder newFolder = forumFolder.createFolder(props);
		return newFolder;
	}

	public String getFolderIdByFolderName(String folderName) {
		Session session = getAdminSession();
		Folder folder = this.getFolderByName(session, folderName);
		if (folder != null) {
			return folder.getId();
		}
		return null;
	}

	public Folder getFolderByName(Session session, String folderName) {
		try {
			if (StringUtils.contains(folderName, "'")) {
				folderName = StringUtils.replace(folderName, "'", "\\'");
			}
			String query = "SELECT cmis:objectId FROM cmis:folder  WHERE cmis:name  = '"
					+ folderName + "'";
			ItemIterable<QueryResult> rows = session.query(query, false);
			String objectId = null;
			for (QueryResult row : rows) {
				List<PropertyData<?>> properties = row.getProperties();
				for (PropertyData<?> property : properties) {
					objectId = (String) property.getFirstValue();
					if (objectId != null) {
						break;
					}
				}
			}
			if (objectId != null) {
				return (Folder) session.getObject(objectId);
			}
		} catch (Exception e) {
			// ignore
		}
		return null;
	}

	public void updateForumAndTopicName(String currentName, String newName) {
		String currentForumFolderName = currentName + FORUM_NAME_APPENDER;
		String currentTopicFolderName = currentName + TOPIC_NAME_APPENDER;
		Folder forumFolder = this.getFolderByName(getAdminSession(),
				currentForumFolderName);
		// if folder is empty which means it is not created. So, no need to
		// update name
		if (forumFolder == null) {
			return;
		} else {
			String newForumFolderName = newName + FORUM_NAME_APPENDER;
			String newTopicFolderName = newName + TOPIC_NAME_APPENDER;

			CmisObject obj = getAdminSession().getObject(forumFolder.getId());
			if (obj != null) {
				Map<String, String> props = new HashMap<String, String>();
				props.put(PropertyIds.NAME, newForumFolderName);
				ObjectId oid = obj.updateProperties(props, true);
			}
			Folder topicFolder = this.getFolderByName(getAdminSession(),
					currentTopicFolderName);
			if (topicFolder != null) {
				obj = getAdminSession().getObject(topicFolder.getId());
				if (obj != null) {
					Map<String, String> props = new HashMap<String, String>();
					props.put(PropertyIds.NAME, newTopicFolderName);
					obj.updateProperties(props, true);
				}
			}
		}
	}

	public void addNotesToTopic(Session session, String folderName,
			String content) {
		String forumFolderName = folderName + FORUM_NAME_APPENDER;
		String topicFolderName = folderName + TOPIC_NAME_APPENDER;
		Folder folder = this.getFolderByName(session, forumFolderName);
		if (folder == null) {
			// create forum folder and topic
			Folder baseForumFolder = getBaseFolderForForums(session);
			Folder forumFolder = createForumFolder(baseForumFolder,
					forumFolderName);
			Folder topic = this.createTopic(forumFolder, topicFolderName);
			this.createPost(session, topic, content);
		} else {
			// get topic
			Folder topic = this.getFolderByName(session, topicFolderName);
			if (topic != null) {
				this.createPost(session, topic, content);
			}
		}
	}

	public List<NotesDetails> getNotesDetailsByFolderName(String folderName) {
		folderName = folderName + TOPIC_NAME_APPENDER;
		Session session = this.getAdminSession();
		Folder topicFolder = this.getFolderByName(session, folderName);
		if (topicFolder == null)
			return Collections.emptyList();
		ItemIterable<CmisObject> children = topicFolder.getChildren();
		List<NotesDetails> list = new ArrayList<NotesDetails>();
		for (CmisObject child : children) {
			Document post = (Document) child;
			NotesDetails notesDetail = new NotesDetails();
			ContentStream contentStream = post.getContentStream();
			InputStream ins = contentStream.getStream();
			String notes = null;
			try {
				notes = IOUtils.toString(ins);
				notesDetail.setNotes(notes);
			} catch (IOException e) {
				// ignore
			}
			Calendar creationDate = child.getCreationDate();
			// notesDetail.setCreatedDateStr(dateFormat.format(creationDate.getTime()));
			notesDetail.setCreatedDateStr(DateFormat.getDateTimeInstance(
					DateFormat.MEDIUM, DateFormat.SHORT).format(
					creationDate.getTime()));
			notesDetail.setCreatedBy(post.getLastModifiedBy());
			list.add(notesDetail);
		}
		session.clear();
		Collections.reverse(list);
		return list;
	}

	public Document createPost(Session session, Folder topic, String content) {

		Map<String, String> props = new HashMap<String, String>();
		props.put(PropertyIds.OBJECT_TYPE_ID, "D:fm:post");
		props.put(PropertyIds.NAME, "test+" + System.currentTimeMillis());
		props.put(PropertyIds.CONTENT_STREAM_MIME_TYPE, "text/plain");
		props.put(PropertyIds.OBJECT_TYPE_ID, "D:fm:post");
		ContentStream contentStream = session.getObjectFactory()
				.createContentStream("forum-posting.txt", content.length(),
						"text/plain",
						new ByteArrayInputStream(content.getBytes()));
		Document newDocument = topic.createDocument(props, contentStream,
				VersioningState.MAJOR);
		return newDocument;
	}

	private Folder getApplicationBaseFolder() {
		Session session = getAdminSession();
		String objectId = this.getNodeIdOfBaseApplicationFolder(session);
		if (objectId == null)
			return null;
		session.clear();
		return (Folder) session.getObject(objectId);
	}

	private String getNodeIdOfBaseApplicationFolder(Session session) {
		String nodeId = null;
		ItemIterable<QueryResult> rows = session
				.query("SELECT cmis:objectId FROM cmis:folder where cmis:name = 'Systemic'",
						false);
		for (QueryResult row : rows) {
			List<PropertyData<?>> properties = row.getProperties();
			for (PropertyData<?> property : properties) {
				nodeId = (String) property.getValues().get(0);
				break;
			}
		}
		return nodeId;
	}

	private Session getAdminSession() {
	
		String adminUserId = sysUtil.getValueByProperyKey(Const.ALFRESCO_USER);
		String adminPassword = sysUtil.getValueByProperyKey(Const.ALFRESCO_ADMIN_PWD);
		return this.createSession(adminUserId, adminPassword);

	}

}

