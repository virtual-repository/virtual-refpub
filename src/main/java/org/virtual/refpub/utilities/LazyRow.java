/**
 * (c) 2016 FAO / UN (project: virtual-refpub)
 */
package org.virtual.refpub.utilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.virtual.refpub.Configuration;
import org.virtual.refpub.RefPubParser;
import org.virtualrepository.tabular.Column;
import org.virtualrepository.tabular.Row;

import lombok.NonNull;

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
public class LazyRow extends Row {
	private Map<QName,String> data;
	
	private String id;
	private List<String> attributes;
	private RefPubParser parser;
	private String conceptName;
	private String codeSystem;
	private Configuration configuration;

	/**
	 * Class constructor
	 *
	 * @param id The row id
	 * @param attributes The concept attributes
	 * @param conceptName The concept name
	 * @param codeSystem The code systemm
	 * @param configuration The RefPub access configuration
	 */
	public LazyRow(@NonNull String id, @NonNull List<String> attributes, @NonNull String conceptName, @NonNull String codeSystem, @NonNull Configuration configuration) {
		super(new HashMap<QName, String>());
		
		this.id = id;
		this.attributes = attributes;
		this.parser = new RefPubParser();
		this.conceptName = conceptName;
		this.codeSystem = codeSystem;
		this.configuration = configuration;
	}
	
	private  Map<QName,String> lazyInitializeData() {
		if(data == null || data.isEmpty()) {
			data = parser.attributesForEntry(configuration.endpoint(), conceptName, codeSystem, id, attributes);
			data.put(new QName(codeSystem + "-code"), id);
		}
		
		return data;
	}
			
	/* (non-Javadoc)
	 * @see org.virtualrepository.tabular.Row#get(org.virtualrepository.tabular.Column)
	 */
	@Override
	public String get(Column column) {
		return lazyInitializeData().get(column.name());
	}
	
	/* (non-Javadoc)
	 * @see org.virtualrepository.tabular.Row#get(javax.xml.namespace.QName)
	 */
	@Override
	public String get(QName name) {
		return lazyInitializeData().get(name);
	}
	
	/* (non-Javadoc)
	 * @see org.virtualrepository.tabular.Row#get(java.lang.String)
	 */
	@Override
	public String get(String name) {
		return lazyInitializeData().get(new QName(name));
	}
	
	/* (non-Javadoc)
	 * @see org.virtualrepository.tabular.Row#toString()
	 */
	@Override
	public String toString() {
		return lazyInitializeData().toString();
	}
}