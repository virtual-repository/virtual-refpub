/**
 * (c) 2016 FAO / UN (project: virtual-refpub)
 */
package org.virtual.refpub;

import static java.util.Collections.singleton;

import java.util.Collection;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.sdmxsource.sdmx.api.manager.output.StructureWriterManager;
import org.virtual.refpub.dependencies.DependencyProvider;
import org.virtualrepository.Property;
import org.virtualrepository.RepositoryService;
import org.virtualrepository.spi.Plugin;

import dagger.Module;
import dagger.ObjectGraph;

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
@Module(includes=DependencyProvider.class)
public class RefPubPlugin implements Plugin {
	public static QName name = new QName("refpub");

	public static String about = 
		"The Reference data Publisher (RefPub) is a proposal for a main reference database " +
		"within the Fisheries and Acquaculture Department of the Food and Agriculture Organisation (FAO) " + 
		"of the United Nations.";
	
	@Inject RefPubProxy proxy;
	@Inject Configuration configuration;
	@Inject StructureWriterManager m;	
	
	@Override
	public Collection<RepositoryService> services() {
		ObjectGraph.create(this).inject(this);
		
		RepositoryService service = new RepositoryService(name, proxy, properties());
		 
		return singleton(service);
	}
	
	private Property[] properties() {
		Property blurb = new Property("REFPUB",about);
		Property location = new Property("location",configuration.endpoint());
		
		return new Property[] {
			blurb,
			location
		};
	}
}