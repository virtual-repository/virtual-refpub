/**
 * (c) 2016 FAO / UN (project: virtual-refpub)
 */
package org.virtual.refpub.model;

import static org.virtualrepository.Utils.notNull;

import org.virtualrepository.Asset;
import org.virtualrepository.Properties;
import org.virtualrepository.Property;
import org.virtualrepository.csv.CsvCodelist;
import org.virtualrepository.sdmx.SdmxCodelist;

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
public class Codelist {
	static final private String ID_PROP = "id";
	static final private String NAME_PROP = "name";
	
	static final private String CONCEPT_NAME_PROP = "concept";
	static final private String CODE_SYSTEM_PROP = "codeSystem";
	
	private final Properties properties;
	
	public Codelist() {
		this(new Properties());
	}
	
	public Codelist(Properties properties) {
		this.properties = properties;
	}
	
	public Codelist(Asset asset) {
		this(asset.properties());
	}
	
	public Codelist(Concept concept, String codeSystem) {
		this();
		
		notNull(concept);
		notNull(concept.getName());
		notNull(concept.getCodeSystems());
		notNull(codeSystem);

		if(!concept.getCodeSystems().contains(codeSystem))
			throw new IllegalArgumentException("Unknown code system '" + codeSystem + "' for concept " + concept.getName());
		
		properties.add(
			new Property(CONCEPT_NAME_PROP, concept.getName()),
			new Property(CODE_SYSTEM_PROP, codeSystem)
		);
		
		properties.add(
			new Property(ID_PROP, computeId()),
			new Property(NAME_PROP, computeName())
		);
	}
	
	public CsvCodelist toCsvAsset() {
		return new CsvCodelist(computeId(), computeName(), 0, properties.toArray());
	}
	
	public SdmxCodelist toSdmxAsset() {
		return new SdmxCodelist(id() + "-sdmx", computeId().replaceAll("\\-", "_"), "unknown", computeId().replaceAll("\\-", "_"), properties.toArray());
	}

	private String computeId() {
		return "RefPub-" + ((String)properties.lookup(CONCEPT_NAME_PROP).value()).replaceAll("\\s", "_") + "_by_" + properties.lookup(CODE_SYSTEM_PROP).value();
	}
	
	private String computeName() {
		return "RefPub-" + properties.lookup(CONCEPT_NAME_PROP).value() + "-" + properties.lookup(CODE_SYSTEM_PROP).value() + " code";
	}
	
	public Property id() {
		return properties.lookup(ID_PROP);
	}
	
	public Property name() {
		return properties.lookup(NAME_PROP);
	}
	
	public Property conceptName() {
		return properties.lookup(CONCEPT_NAME_PROP);
	}
	
	public Property codeSystem() {
		return properties.lookup(CODE_SYSTEM_PROP);
	}
}