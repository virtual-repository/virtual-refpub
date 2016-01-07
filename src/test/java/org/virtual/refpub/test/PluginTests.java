package org.virtual.refpub.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.virtualrepository.impl.Services;


public class PluginTests {
	
	@Test
	public void pluginLoads() {
		Services repos = new Services();
		repos.load();
		assertTrue(repos.size()>0);
	}
	
}
