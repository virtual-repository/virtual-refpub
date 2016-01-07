/**
 * (c) 2016 FAO / UN (project: virtual-refpub)
 */
package org.virtual.refpub.test;

import static org.virtual.refpub.test.Utils.inject;

import java.util.Iterator;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.virtual.refpub.Configuration;
import org.virtual.refpub.RefPubConnector;
import org.virtual.refpub.dependencies.DependencyProvider;
import org.virtual.refpub.model.Codelist;
import org.virtualrepository.Asset;
import org.virtualrepository.tabular.Row;
import org.virtualrepository.tabular.Table;

import dagger.Module;

/**
 * Place your class / interface description here.
 *
 * History:
 *
 * ------------- --------------- -----------------------
 * Date			 Author			 Comment
 * ------------- --------------- -----------------------
 * Jan 4, 2016   Fabio     Creation.
 *
 * @version 1.0
 * @since Jan 4, 2016
 */
@Module(injects=TestConnector.class, includes=DependencyProvider.class)
public class TestConnector {
	@Inject Configuration configuration;
	@Inject RefPubConnector toTest;
	
	@BeforeClass
	public static void setup() {
	}
	
	@Test
	public void testCodelistListing() throws Exception {
		inject(this);
		
		for(Codelist in : toTest.codelists())
			System.out.println(in.id() + " " + in.name() + " [ " + in.conceptName() + " - " + in.codeSystem() + " ]");
	}
	
	@Test
	public void testCodelistRetrievement() throws Exception {
		inject(this);
		
		Asset toRetrieve = null;
		for(Codelist in : toTest.codelists())
			if("RefPub-Species_by_ASFIS".equals(in.id().value()))
				toRetrieve = in.toCsvAsset();
		
		Assert.assertNotNull(toRetrieve);
		
		long end, start = System.currentTimeMillis();
		
		Table data = toTest.retrieve(toRetrieve);
		
		end = System.currentTimeMillis();
		
		System.out.println("Retrieving asset " + toRetrieve.id() + " took " + ( end - start ) + " mSec.");
		
		Iterator<Row> rows = data.iterator();
		Row current;
		int counter = 0;
		
		while(rows.hasNext() && counter < 100) {
			current = rows.next();
			counter++;
			
			System.out.println(current);
		}
	}
	
	@Test
	public void testNonStandardCodelistRetrievement() throws Exception {
		inject(this);
		
		Asset toRetrieve = null;
		for(Codelist in : toTest.codelists())
			if("RefPub-TimeUnit_by_ID".equals(in.id().value()))
				toRetrieve = in.toCsvAsset();
		
		Assert.assertNotNull(toRetrieve);
		
		long end, start = System.currentTimeMillis();
		
		Table data = toTest.retrieve(toRetrieve);
		
		end = System.currentTimeMillis();
		
		System.out.println("Retrieving asset " + toRetrieve.id() + " took " + ( end - start ) + " mSec.");
		
		Iterator<Row> rows = data.iterator();
		Row current;
		int counter = 0;
		
		while(rows.hasNext() && counter < 100) {
			current = rows.next();
			counter++;
			
			System.out.println(current);
		}
	}
}
