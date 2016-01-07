/**
 * (c) 2016 FAO / UN (project: virtual-refpub)
 */
package org.virtual.refpub.test;

import static org.virtual.refpub.test.Utils.inject;

import java.io.IOException;
import java.util.Collection;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.virtual.refpub.Configuration;
import org.virtual.refpub.RefPubParser;
import org.virtual.refpub.dependencies.DependencyProvider;
import org.virtual.refpub.model.Concept;

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
@Module(injects=TestParser.class, includes=DependencyProvider.class)
public class TestParser {
	@Inject Configuration configuration;
	RefPubParser toTest = new RefPubParser();
	
	@BeforeClass
	public static void setup() {
	}
	
	@Test
	public void testConceptRetrievment() throws IOException {
		inject(this);
		
		Collection<Concept> concepts = toTest.concepts(configuration.endpoint());
		
		Assert.assertNotNull(concepts);
		Assert.assertTrue(!concepts.isEmpty());
		
		for(Concept in : concepts) {
			System.out.println(in.toString());
		};
	}
}
