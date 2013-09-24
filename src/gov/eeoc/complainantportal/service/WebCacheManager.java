package gov.eeoc.complainantportal.service;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class WebCacheManager {

	private Cache<String, String> cache;

	private EmbeddedCacheManager manager;
	
	private final Logger log = LoggerFactory.getLogger(WebCacheManager.class);
	
	@PostConstruct
	private void init() {
		log.info("WebCacheManager::init()");
		Configuration config = new ConfigurationBuilder()
        .eviction()
        .strategy(EvictionStrategy.LRU)
        .maxEntries(2000).build();
		manager = new DefaultCacheManager();
		manager.defineConfiguration("complainanat-email-token-cache-config", config);
		cache = manager.getCache();

	}
	
	@PreDestroy
	private void closeResources(){
		log.info("WebCacheManager::closeResources()");
		cache.stop();
		manager.stop();
	}

	/*
	 * This method generates six characters random alapha-numeric token
	 */
	
	public void addToken(String email, String token) {
		validateParameters(email, token);
		cache.put(email, token, 60, TimeUnit.MINUTES);
	}

	public boolean isValidToken(String email, String token) {
		validateParameters(email, token);
		String value = (String) cache.get(email);
		if (value != null) {
			return value.equals(token);
		}
		return false;
	}

	private void validateParameters(String email, String token) {
		if (!StringUtils.isNotBlank(email) || !StringUtils.isNotBlank(token)) {
			throw new IllegalArgumentException(
					"Null value passed for either email or token parameters");
		}
	}

}

