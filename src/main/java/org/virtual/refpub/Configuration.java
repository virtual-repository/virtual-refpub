package org.virtual.refpub;

import static org.virtualrepository.Utils.*;

import java.util.Properties;

public class Configuration {
	public static final String ENDPOINT = "refpub.endpoint";
	public static final String NO_REFRESH = "refpub.norefresh";
	public static final String PUBLISH_ENDPOINT = "refpub.publish.endpoint";
	public static final String PUBLISH_USER = "refpub.publish.user";
	public static final String PUBLISH_PWD = "refpub.publish.pwd";
	public static final String PUBLISH_TIMEOUT = "refpub.publish.timeout";

	private final Properties properties;

	public Configuration(Properties properties) {
		validate(properties);

		this.properties = properties;
	}

	public String endpoint() {
		return properties.getProperty(ENDPOINT);
	}
	public boolean noRefresh() {
		return Boolean.valueOf(properties.getProperty(NO_REFRESH));
	}
	
	public String publishEndpoint() {
		return properties.getProperty(PUBLISH_ENDPOINT);
	}
	
	public String publishUser() {
		return properties.getProperty(PUBLISH_USER);
	}
	
	public String publishPwd() {
		return properties.getProperty(PUBLISH_PWD);
	}
	
	public int publishTimeout() {
		String value = properties.getProperty(PUBLISH_TIMEOUT);
		return Integer.valueOf(value);
	}

	private void validate(Properties properties) {
		notNull(ENDPOINT, properties.getProperty(ENDPOINT));
		
		notNull(PUBLISH_ENDPOINT, properties.getProperty(PUBLISH_ENDPOINT));
		notNull(PUBLISH_USER, properties.getProperty(PUBLISH_USER));
		notNull(PUBLISH_PWD, properties.getProperty(PUBLISH_PWD));
		
		String value = properties.getProperty(PUBLISH_TIMEOUT);
		
		notNull(PUBLISH_TIMEOUT, value);
	}
}
