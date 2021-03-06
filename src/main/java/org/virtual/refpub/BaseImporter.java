/**
 * 
 */
package org.virtual.refpub;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtualrepository.Asset;
import org.virtualrepository.tabular.Table;


/**
 * Imports codelists in an internal model.
 * <p>
 * It is wrapped to convert the model into specific asset types.
 */
public class BaseImporter {
	private static final Logger log = LoggerFactory.getLogger(BaseImporter.class);
	
	@Inject RefPubConnector connector;
	
	public Table retrieve(final Asset asset) throws Exception {
		log.info("retrieving asset {}", asset.id());
		
		return connector.retrieve(asset);
	}
}
