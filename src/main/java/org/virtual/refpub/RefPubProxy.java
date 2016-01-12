package org.virtual.refpub;

import static org.virtualrepository.spi.ImportAdapter.adapt;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.csv.Table2CsvStream;
import org.virtualrepository.spi.Browser;
import org.virtualrepository.spi.Importer;
import org.virtualrepository.spi.Lifecycle;
import org.virtualrepository.spi.Publisher;
import org.virtualrepository.spi.ServiceProxy;


/**
 * Configures and provides access components.
 *  
 * @author Fabio Simeoni
 *
 */
public class RefPubProxy implements ServiceProxy, Lifecycle {
	private static Logger log = LoggerFactory.getLogger(RefPubBrowser.class);

	@Inject Configuration configuration;
	@Inject RefPubBrowser browser;
	@Inject CsvImporter csvImporter;
	@Inject SdmxImporter sdmxImporter;
	
	private final List<Publisher<?,?>> publishers = new ArrayList<Publisher<?,?>>();
	private final List<Importer<?,?>> importers = new ArrayList<Importer<?,?>>();

	@Override
	public void init() throws Exception {
		log.info("connecting to RefPub @ {}", configuration.endpoint());

		importers.add(csvImporter);
		importers.add(sdmxImporter);
		
		importers.add(adapt(csvImporter, new Table2CsvStream<CsvCodelist>()));
	}
	
	@Override
	public Browser browser() {
		return browser;
	}

	@Override
	public List<? extends Importer<?, ?>> importers() {
		return importers;
	}

	@Override
	public List<? extends Publisher<?, ?>> publishers() {
		return publishers;
	}
}