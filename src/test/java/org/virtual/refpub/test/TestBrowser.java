/**
 * (c) 2016 FAO / UN (project: virtual-refpub)
 */
package org.virtual.refpub.test;

import static org.virtual.refpub.test.Utils.inject;

import java.util.Arrays;
import java.util.Iterator;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.virtual.refpub.RefPubBrowser;
import org.virtual.refpub.dependencies.DependencyProvider;
import org.virtualrepository.Asset;
import org.virtualrepository.AssetType;
import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.spi.MutableAsset;

import dagger.Module;

/**
 * Place your class / interface description here.
 *
 * History:
 *
 * ------------- --------------- -----------------------
 * Date			 Author			 Comment
 * ------------- --------------- -----------------------
 * Jan 5, 2016   Fabio     Creation.
 *
 * @version 1.0
 * @since Jan 5, 2016
 */
@Module(injects=TestBrowser.class, includes={ DependencyProvider.class })
public class TestBrowser {
	@Inject RefPubBrowser toTest;
	
	@BeforeClass
	public static void setup() {
	}
	
	@Test
	public void testDiscovery() throws Exception {
		inject(this);
		
		Iterable<? extends MutableAsset> assets = toTest.discover(Arrays.asList(new AssetType[] { CsvCodelist.type }));
		
		Assert.assertNotNull(assets);
		
		int count = 0;
		Iterator<? extends MutableAsset> iterator = assets.iterator();
				
		for(;iterator.hasNext();count++)
			iterator.next();

		Assert.assertTrue(count > 0);
		
		System.out.println(count + " assets discovered. Listing follows:");
		
		iterator = assets.iterator();
		
		Asset current;
		while(iterator.hasNext()) {
			System.out.println(( current = iterator.next()).id() + " (" + current.name() + ") : " + current.properties());
		}
	}
}
