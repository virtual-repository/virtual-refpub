package org.virtual.refpub;

import static java.lang.System.currentTimeMillis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtual.refpub.model.Codelist;
import org.virtual.refpub.model.Concept;
import org.virtual.refpub.utilities.LazyRow;
import org.virtualrepository.Asset;
import org.virtualrepository.tabular.Column;
import org.virtualrepository.tabular.DefaultTable;
import org.virtualrepository.tabular.Row;
import org.virtualrepository.tabular.Table;

/**
 * Single point of integration with repository.
 * 
 * The real work at read-time takes place here.
 * 
 */
public class RefPubConnector {
	static final private Logger log = LoggerFactory.getLogger(RefPubConnector.class);
	
	private Configuration configuration;
	private RefPubParser xmlParser = new RefPubParser();

	/**
	 * Class constructor
	 *
	 * @param configuration
	 */
	@Inject public RefPubConnector(Configuration configuration) {
		this.configuration = configuration;
	}
	
	public Collection<Codelist> codelists() throws Exception {
		Collection<Codelist> fromConcepts = new ArrayList<Codelist>();
		
		for(Concept in : xmlParser.concepts(configuration.endpoint())) {
			if(in != null && in.getCodeSystems() != null && !in.getCodeSystems().isEmpty()) {
				for(String code : in.getCodeSystems()) {
					fromConcepts.add(new Codelist(in, code));
				}
			}
		}
		
		return fromConcepts;
	}
	
	public Table retrieve(Asset asset) throws Exception {
		Codelist codelist = codelistFor(asset);
		
		String concept = (String)codelist.conceptName().value();
		String codeSystem = (String)codelist.codeSystem().value();
		
		long time = currentTimeMillis();
		
		log.trace("retrieving data for {} ({} @ {})", codelist.name(), concept, codeSystem);
		
		List<String> attributes = xmlParser.attributesFor(configuration.endpoint(), concept);
		
		List<Column> columns = fromAttributes(attributes, codeSystem);
		
		Collection<String> ids = xmlParser.idsForConcept(configuration.endpoint(), concept, codeSystem);
		
		log.trace("retrieved {} entries for {} in {} ms", ids.size(), codelist.name(), currentTimeMillis() - time);
		
		List<Row> rows = new ArrayList<>();
		
		for(String id : ids) {
			rows.add(new LazyRow(id, attributes, concept, codeSystem, configuration));
		}
		
		return new DefaultTable(columns, rows);
	}
	
	private List<Column> fromAttributes(List<String> attributes, String codeSystem) {
		List<Column> columns = new ArrayList<Column>();
		
		columns.add(new Column(new QName(codeSystem + "-code"), new QName("code"), String.class));
		
		for(String attribute : attributes) {
			columns.add(new Column(new QName(attribute), String.class));
		}
		
		return columns;
	}
	
	private Codelist codelistFor(Asset asset) {
		Codelist codelist = new Codelist(asset);
		
		if(codelist.conceptName() == null || codelist.codeSystem() == null)
			throw new IllegalArgumentException("malformed asset: properties required to retrieve codelist are missing");

		return codelist;
	}
}
