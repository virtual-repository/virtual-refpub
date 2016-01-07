package org.virtual.refpub.test;

import static java.lang.System.setProperty;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.virtual.refpub.test.Utils.inject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtual.refpub.BaseImporter;
import org.virtual.refpub.RefPubBrowser;
import org.virtual.refpub.RefPubConnector;
import org.virtual.refpub.RefPubPlugin;
import org.virtual.refpub.RefPubProxy;
import org.virtual.refpub.model.Codelist;
import org.virtualrepository.Asset;
import org.virtualrepository.VirtualRepository;
import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.csv.CsvTable;
import org.virtualrepository.impl.Repository;
import org.virtualrepository.sdmx.SdmxCodelist;
import org.virtualrepository.tabular.Row;
import org.virtualrepository.tabular.Table;

import dagger.Module;

@Module(injects = IntegrationTests.class, includes = RefPubPlugin.class)
public class IntegrationTests {
	private static Logger log;

	@Inject RefPubConnector connector;
	@Inject RefPubBrowser browser;
	@Inject RefPubProxy proxy;
	@Inject BaseImporter importer;
	
	@BeforeClass
	public static void setup() {
		setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
		setProperty("org.slf4j.simpleLogger.log.org.springframework", "warn");
		log = LoggerFactory.getLogger("test");
	}

	@Test
	public void findsCodelists() throws Exception {
		inject(this);

		Collection<Codelist> result = connector.codelists();

		log.info("test found {} codelists", result.size());

		for (Codelist list : result)
			log.debug(list.toString());
	}

	@Test
	public void discoverCsvCodelists() {
		VirtualRepository repo = new Repository();

		repo.discover(CsvCodelist.type);

		for (Asset asset : repo)
			log.debug(asset.toString());
	}

	@Test
	public void prefersCsvCodelists() {
		VirtualRepository repo = new Repository();

		repo.discover(CsvCodelist.type, SdmxCodelist.type);

		for (Asset asset : repo)
			assertEquals(CsvCodelist.type, asset.type());
	}

	@Test
	public void retrieveFirstCodelist() throws Exception {
		inject(this);

		Iterable<? extends Asset> codelists = browser.discover(asList(CsvCodelist.type));

		Iterator<? extends Asset> it = codelists.iterator();

		Asset codelist = it.next();

		Table table = importer.retrieve(codelist);

		int counter = 0;
		for (Row row : table) {
			log.debug(row.toString());
			
			if(++counter > 100) break;
		}
	}
	
	@Test
	public void retrieveGivenCodelist() throws Exception {
		inject(this);

		String name = "RefPub-Gear-ISSCFG-CODE code";

		Iterable<? extends Asset> codelists = browser.discover(asList(CsvCodelist.type));

		for (Asset list  : codelists) 
			if (list.name().equals(name)) {
				Table table = importer.retrieve(list);
				for (Row row : table)
					log.debug(row.toString());
			}
	}

	@Test
	public void retrieveFirstCodelistAsTable() throws Exception {
		VirtualRepository repo = new Repository();

		repo.discover(CsvCodelist.type);

		Asset list = repo.iterator().next();
		
		Table table = repo.retrieve(list,Table.class);

		log.debug(list.toString());
		
		for (Row row : table)
			log.debug(row.toString());
	}
	
	@Test
	public void retrieveFirstCodelistAsStream() throws Exception {
		VirtualRepository repo = new Repository();

		repo.discover(CsvCodelist.type);

		Asset list = repo.iterator().next();
		
		InputStream stream = repo.retrieve(list,InputStream.class);

		Table table = new CsvTable((CsvCodelist)list, stream);
		
		for (Row row : table)
			System.out.println(row);
	}

	@Test
	public void retrieveAllCodelists() throws Exception {
		inject(this);

		Iterable<? extends Asset> codelists = browser.discover(asList(CsvCodelist.type));

		Iterator<? extends Asset> it = codelists.iterator();

		List<String> empties = new ArrayList<>();
		Map<String, String> errors = new HashMap<>();

		while (it.hasNext()) {
			Asset asset = it.next();
			try {
				Table table = importer.retrieve(asset);

				if (!table.iterator().hasNext())
					empties.add(asset.id());
			} catch (Exception e) {
				errors.put(asset.id(), e.getMessage());
			}
		}

		log.debug(empties.size() + " empties\n:" + empties);

		assertEquals(errors.toString(), 0, errors.size());
	}
}
