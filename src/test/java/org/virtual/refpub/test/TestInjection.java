package org.virtual.refpub.test;


import static java.lang.System.setProperty;
import static org.junit.Assert.assertNotNull;
import static org.virtual.refpub.test.Utils.*;

import javax.inject.Inject;

import org.junit.BeforeClass;
import org.junit.Test;
import org.virtual.refpub.Configuration;
import org.virtual.refpub.RefPubPlugin;
import org.virtual.refpub.RefPubProxy;
import org.virtualrepository.RepositoryService;
import org.virtualrepository.VirtualRepository;
import org.virtualrepository.impl.Repository;

import dagger.Module;

import static org.virtual.refpub.RefPubPlugin.*;

@Module(injects=TestInjection.class, includes=RefPubPlugin.class)
public class TestInjection {
	@Inject Configuration config;
	
	@Inject RefPubProxy proxy;
	
	@BeforeClass
	public static void setup() {
		setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
		setProperty("org.slf4j.simpleLogger.log.org.springframework", "warn");
	}
	
	@Test
	public void injectionsWork() throws Exception {
		inject(this);
		
		assertNotNull(config);
		assertNotNull(proxy);
	}
	
	@Test
	public void pluginStarts() throws Exception {
		VirtualRepository repo = new Repository();
		
		RepositoryService service = repo.services().lookup(name);
		
		System.out.println(service.properties());
	}
}
