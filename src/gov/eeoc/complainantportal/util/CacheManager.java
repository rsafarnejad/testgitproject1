package gov.eeoc.complainantportal.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.eeoc.complainantportal.model.AlfrescoUser;

/**
 * Session Bean implementation class CacheManager
 */

@Singleton
public class CacheManager {
	
	@PersistenceContext(unitName = "SystemicWatchList")
	private EntityManager em;
	
	private Map<String, String> codeDescriptionMap = null;
	
	Logger log = LoggerFactory.getLogger(CacheManager.class);
	
	@Inject
	private SystemUtil util;
	
	private Properties configProp;
	@PostConstruct
	public void init(){
		configProp = util.getPropertiesFromClasspath(SystemUtil.CONFIG_PROPERTY_FILE);
	}
	
	

    public CacheManager() {
        // TODO Auto-generated constructor stub
    }
    
    
    
    public String getValue(String key){
    //	return configProp.getProperty(key); commented and will be removed
    	return util.getValueByProperyKey(key);
    }
    
    
    public String getDescriptionByCode(String code){
    	if(codeDescriptionMap == null){
    		populateCodeDescriptionMap();
    	}
    	String description = codeDescriptionMap.get(code);
    	if(description == null){
    		Map<String, String> updatedMap = getCodeAndDescription(code);
    		description = updatedMap.get(code);
    		if(description != null){
    			codeDescriptionMap.put(code, description);
    		}
    	}
    	return description;
    }
    
    public String getUserIdByStaffSeq(Long staffSeq){
		Query query = em.createNativeQuery("select oracle_user_id from shared_access where ss_staff_seq = ?");
		try{
			query.setParameter(1, staffSeq);
			return (String) query.getSingleResult();
		}catch (Exception e){
			log.error("Unable to get user id for {}" , staffSeq.longValue());
		}
		return null;
	}
    
    public AlfrescoUser getUserDetailByStaffId(Long staffSeq){
    	String sql = "select a.oracle_user_id, b.first_name, b.last_name, b.middle_initial, b.email_address from shared_access a, shared_staff b where b.staff_seq = ? and  a.ss_staff_seq = b.staff_seq";
    	Query query = em.createNativeQuery(sql);
    	query.setParameter(1, staffSeq);
    	List<Object[]> results = query.getResultList();
    	AlfrescoUser user = new AlfrescoUser();
    	for(Object[] columns : results){
    		user.setUserName((String) columns[0]);
    		user.setFirstName((String) columns[1]);
    		user.setLastName((String) columns[2]);
    		user.setMiddleName((String) columns[3]);
    		user.setEmail((String) columns[4]);
    	}
    	
    	return user;
    }
    
    public String getStaffIdFromAlfrescoUser(String userId){
    	String sql = "select a.ss_staff_seq from shared_access a where a.oracle_user_id = ?";
    	Query query = em.createNativeQuery(sql);
    	query.setParameter(1, userId);
    	Object results = query.getSingleResult();
    	String staffSeqId="";
    	if(results != null){
    		staffSeqId = results+"";
    	}
    	return staffSeqId;
    }
    
    public String getAlfrescoServiceURL(){
    	SystemUtil util = new SystemUtil();
    	try {
		/* commented and will be removed -- reading from config properties
    		Properties prop = util.getPropertiesFromClasspath(SystemUtil.CONFIG_PROPERTY_FILE);
			return prop.getProperty(SystemUtil.ALFRESCO_URL_PROP);
			*/
    		return util.getValueByProperyKey(Const.ALFRESCO_SERVICE_URL);
		} catch (Exception e) {
			log.error("Error retrieving propery for Alfresco URL {} ", e.toString());
		}
    	return null;
    }
    
    private void populateCodeDescriptionMap(){
    	codeDescriptionMap = new HashMap <String, String> ();
    	Query query = em.createNativeQuery("SELECT code, DESCRIPTION FROM SHARED_CODE WHERE DOMAIN = 'ACO' ");
    	List<Object[]> objs = query.getResultList();
    	for(Object [] obj : objs){
    		String code = (String) obj[0];
    		String description = (String) obj[1];
    		codeDescriptionMap.put(code, description);
    	}
    	
    }
    private Map<String, String> getCodeAndDescription(String code){
	    Query query = em.createNativeQuery("SELECT CODE, DESCRIPTION FROM SHARED_CODE WHERE DOMAIN = 'ACO' and CODE = ? ").setParameter(1, code);
	    Map<String, String> map = new HashMap <String, String> ();
		List<Object[]> objs = query.getResultList();
		for(Object [] obj : objs){
			String codeName = (String) obj[0];
			String description = (String) obj[1];
			map.put(codeName, description);
		}
		return map;
    }
}
