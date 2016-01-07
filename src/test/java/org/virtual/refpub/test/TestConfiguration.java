package org.virtual.refpub.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.virtual.refpub.Configuration.ENDPOINT;
import static org.virtual.refpub.Configuration.NO_REFRESH;
import static org.virtual.refpub.Configuration.PUBLISH_ENDPOINT;
import static org.virtual.refpub.Configuration.PUBLISH_PWD;
import static org.virtual.refpub.Configuration.PUBLISH_TIMEOUT;
import static org.virtual.refpub.Configuration.PUBLISH_USER;

import java.util.Properties;

import org.junit.Test;
import org.virtual.refpub.Configuration;
import org.virtual.refpub.dependencies.DependencyProvider;

public class TestConfiguration {
	@Test(expected=Exception.class)
	public void detectsInvalidConfiguration() {
		new Configuration(new Properties());
	}
		
	@Test
	public void readsValidConfiguration() {
		Properties properties = new Properties();
		
		properties.put(ENDPOINT, "ENDPOINT");
		properties.put(NO_REFRESH, false);
		properties.put(PUBLISH_ENDPOINT, "PUBLISH_ENDPOINT");
		properties.put(PUBLISH_USER, "PUBLISH_USER");
		properties.put(PUBLISH_PWD, "PUBLISH_PWD");
		properties.put(PUBLISH_TIMEOUT, "666");
		
		Configuration c = new Configuration(properties);
		
		assertEquals(c.endpoint(), properties.get(ENDPOINT));
		assertEquals(c.noRefresh(), properties.get(NO_REFRESH));
		assertEquals(c.publishEndpoint(), properties.get(PUBLISH_ENDPOINT));
		assertEquals(c.publishUser(), properties.get(PUBLISH_USER));
		assertEquals(c.publishPwd(), properties.get(PUBLISH_PWD));
		assertEquals(c.publishTimeout(), Integer.valueOf((String)properties.get(PUBLISH_TIMEOUT)).intValue());
	}
	
	@Test
	public void parsesConfigurationFromClasspath() {
		Configuration c = new DependencyProvider().configuration();

		assertNotNull(c.endpoint());
		assertNotNull(c.noRefresh());
		assertNotNull(c.publishEndpoint());
		assertNotNull(c.publishUser());
		assertNotNull(c.publishPwd());
		assertNotNull(c.publishTimeout());
	}
}
