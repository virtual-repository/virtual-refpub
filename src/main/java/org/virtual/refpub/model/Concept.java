/**
 * (c) 2016 FAO / UN (project: virtual-refpub)
 */
package org.virtual.refpub.model;

import java.io.Serializable;
import java.util.List;

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
public class Concept implements Serializable {
	/** Field serialVersionUID */
	private static final long serialVersionUID = 8655613047227224697L;
	
	private String name;
	private List<String> codeSystems;
	private List<String> attributes;

	/**
	 * @return the 'name' value
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the 'name' value to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the 'codeSystems' value
	 */
	public List<String> getCodeSystems() {
		return codeSystems;
	}

	/**
	 * @param codeSystems the 'codeSystems' value to set
	 */
	public void setCodeSystems(List<String> codeSystems) {
		this.codeSystems = codeSystems;
	}

	/**
	 * @return the 'attributes' value
	 */
	public List<String> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes the 'attributes' value to set
	 */
	public void setAttributes(List<String> attributes) {
		this.attributes = attributes;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( attributes == null ) ? 0 : attributes.hashCode() );
		result = prime * result + ( ( codeSystems == null ) ? 0 : codeSystems.hashCode() );
		result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		Concept other = (Concept) obj;
		if(attributes == null) {
			if(other.attributes != null) return false;
		} else if(!attributes.equals(other.attributes)) return false;
		if(codeSystems == null) {
			if(other.codeSystems != null) return false;
		} else if(!codeSystems.equals(other.codeSystems)) return false;
		if(name == null) {
			if(other.name != null) return false;
		} else if(!name.equals(other.name)) return false;
		return true;
	}
	
	public String toString() {
		return "CONCEPT [ " + name + " ] : CODE SYSTEMS [ " + codeSystems + " ] : ATTRIBUTES [ " + attributes + " ]";
	}
}
