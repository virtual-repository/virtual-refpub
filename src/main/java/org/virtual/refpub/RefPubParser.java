/**
 * (c) 2016 FAO / UN (project: virtual-refpub)
 */
package org.virtual.refpub;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtual.refpub.model.Concept;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
public class RefPubParser {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	private DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY;
	private DocumentBuilder DOCUMENT_BUILDER;
	private XPathFactory XPATH_FACTORY;
	
	public RefPubParser() {
		try {
			DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
			DOCUMENT_BUILDER = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
			XPATH_FACTORY = XPathFactory.newInstance();
		} catch (Throwable t) {
			throw new RuntimeException("Unable to initialize XML document builder", t);
		}
	}
	
	private Document parse(URI URI) {
		long end, start = System.currentTimeMillis();

		try {
			log.info("Building document from XML @ {}...", URI);
			
			return DOCUMENT_BUILDER.parse(URI.toString());
		} catch(Throwable t) {
			throw new RuntimeException("Unable to parse XML from " + URI + ": " + t.getMessage(), t);
		} finally {
			end = System.currentTimeMillis();
			
			log.info("Document from XML @ {} has been built in {} mSec.", URI, end - start);
		}
	}
	
	private Document parse(String URL) {
		try {
			return parse(new URI(URL));
		} catch(URISyntaxException USe) {
			throw new RuntimeException(USe);
		}
	}
	
	public Collection<Concept> concepts(String endpoint) {
		log.info("Retrieving available concepts from {}...", endpoint);
		
		Document response = parse(endpoint + "/rest/concept/xml");
		
		Collection<Concept> concepts = new ArrayList<Concept>();
		
		NodeList conceptNodes = response.getElementsByTagName("concept");
		NodeList codeNodes;
		
		Concept current;
		Node link, conceptNode, codeList, code;
		for(int n=0; n<conceptNodes.getLength(); n++) {
			current = new Concept();
			
			conceptNode = conceptNodes.item(n);
			
			current.setName((link = conceptNode.getFirstChild()).getAttributes().getNamedItem("rel").getTextContent());
			current.setCodeSystems(new ArrayList<String>());
			current.setAttributes(attributesFor(endpoint, current.getName()));
			
			codeList = link.getNextSibling();
			
			codeNodes = ((Element)codeList).getElementsByTagName("code");
			
			for(int m=0; m<codeNodes.getLength(); m++) {
				code = codeNodes.item(m);
				
				current.getCodeSystems().add(code.getAttributes().getNamedItem("name").getTextContent());
			}
			
			concepts.add(current);
		}
		
		log.info("{} concepts have been retrieved from {}", concepts.size(), endpoint);
		
		return concepts;
	}
	
	public Collection<String> idsForConcept(String endpoint, String conceptName, String codeSystem) {
		long end, start = System.currentTimeMillis();
		
		try {
			return idsForConcept(endpoint, new ArrayList<String>(), conceptName, codeSystem, 1, 1000);
		} finally {
			end = System.currentTimeMillis();
			
			log.info("Retrieving IDs for concept {}#{} from {} took {} mSec", conceptName, codeSystem, endpoint, end - start);
		}
	}
	
	public Collection<String> idsForConcept(String endpoint, Collection<String> current, String conceptName, String codeSystem, int page, int size) {
		Document currentPage = parse(endpoint + "/rest/concept/" + sanitize(conceptName) + "/codesystem/" + codeSystem + "/xml?page=" + page + "&count=" + size);
		
		NodeList concepts = currentPage.getElementsByTagName("concept");
		
		if(concepts.getLength() == 0) return current;
		
		String id;
		
		for(int n=0; n<concepts.getLength(); n++) {
			id = idsForCodeSystem((Element)concepts.item(n), codeSystem);
			
			if(id != null)
				current.add(id);
		}
		
		return idsForConcept(endpoint, current, conceptName, codeSystem, page + 1, size);
	}
	
	public Map<QName, String> attributesForEntry(String endpoint, String conceptName, String codeSystem, String code, List<String> attributes) {
		Map<QName, String> attributesMap = new HashMap<QName, String>();
		
		Document document = parse(endpoint + "/rest/concept/" + sanitize(conceptName) + "/codesystem/" + codeSystem + "/code/" + code + "/xml");
		
		String value;
		for(String attribute : attributes) {
			try {
				value = getAttribute((Element)document.getFirstChild(), attribute);
				
				if(value != null)
					attributesMap.put(new QName(attribute), value);
			} catch(Throwable t) {
				throw new RuntimeException(t);
			}
		}
		
		return attributesMap;
	}
	
	private String idsForCodeSystem(Element concept, String codeSystem) {
		NodeList codelistNodes = concept.getElementsByTagName("codeList");
		
		if(codelistNodes.getLength() == 0) return null;
		if(codelistNodes.getLength() > 1) throw new IllegalArgumentException("Multiple 'codelist' nodes for entry");
		
		NodeList codes = ((Element)codelistNodes.item(0)).getElementsByTagName("code");
		
		if(codes.getLength() == 0) return null;
		
		Element current;
		
		for(int n=0; n<codes.getLength(); n++) {
			current = (Element)codes.item(n);
			
			if(codeSystem.equals(current.getAttribute("name")))
				return current.getAttribute("concept");
		}
		
		return null;
	}
	
	public List<String> attributesFor(String endpoint, String conceptName) {
		Document response = parse(endpoint + "/rest/concept/" + sanitize(conceptName) + "/attribute/xml");
		
		List<String> attributes = new ArrayList<String>();
		
		NodeList attributeNodes = response.getElementsByTagName("attribute");
		
		Node attributeNode;
		for(int n=0; n<attributeNodes.getLength(); n++) {
			attributeNode = attributeNodes.item(n);
			
			attributes.add(attributeNode.getAttributes().getNamedItem("value").getTextContent());
		}
		
		return attributes;
	}
	
	private String getAttribute(Element entry, String name) throws Exception {
		XPath xPath = XPATH_FACTORY.newXPath();
		
		NodeList elements = (NodeList)xPath.evaluate("/concept/" + name, 
													 entry, 
													 XPathConstants.NODESET);
		
		if(elements.getLength() > 1) throw new IllegalArgumentException("Multiple elements named " + name);
		
		if(elements.getLength() == 1) return elements.item(0).getTextContent();
		
		if(isName(name)) {
			String replacement = getElementTag(name);
			
			if(replacement != null) {
//				NodeList candidates = entry.getElementsByTagName(replacement);
				
				xPath = XPATH_FACTORY.newXPath();
				
				NodeList candidates = (NodeList)xPath.evaluate("/concept/" + replacement, 
															   entry, 
															   XPathConstants.NODESET);
				
				if(candidates.getLength() == 1) {
					Element current = (Element)candidates.item(0);
					
					String language = getLanguage(name);
					
					if(language == null) return null;
					
					NodeList localized = current.getElementsByTagName(language);
					
					if(localized.getLength() == 0) return null;
					if(localized.getLength() > 1) throw new IllegalArgumentException("Multiple localized values for " + name);
					
					return localized.item(0).getTextContent();
				} else if(candidates.getLength() > 1) {
					System.err.println("Multiple elements found for " + name + " (" + candidates.getLength() + ")");
					//throw new IllegalArgumentException("Multiple elements found for " + name);
				}
			}
		}
		
		String asAttr = name.toUpperCase().replaceAll("\\-", "_");
		
		NodeList attrs = entry.getElementsByTagName("attr");
		
		if(attrs.getLength() == 0) return null;
		if(attrs.getLength() > 1) throw new IllegalArgumentException("Multiple <attr> elements");
		
		NodeList values = ((Element)attrs.item(0)).getElementsByTagName("value");
		
		Element current;
		String value = null;
		for(int n=0; n<values.getLength(); n++) {
			current = (Element)values.item(n);
			
			if(asAttr.equals(current.getAttribute("name"))) {
				value = current.getTextContent(); 
				
				return "".equals(value) ? null : value;
			}
		}
		
		return null;
	}
	
	private boolean isName(String attrName) {
		return attrName.endsWith("name_a") ||
			   attrName.endsWith("name_c") ||
			   attrName.endsWith("name_e") ||
			   attrName.endsWith("name_f") ||
			   attrName.endsWith("name_r") ||
			   attrName.endsWith("name_s") ||
			   attrName.startsWith("short_desc");
	}
	
	private boolean isLocalized(String attrName) {
		return isName(attrName);
	}
	
	private String getLanguage(String attrName) {
		if(!isLocalized(attrName)) return null;
		
		String lang = attrName.substring(attrName.lastIndexOf("_") + 1, attrName.length());
		
		switch(lang) {
			case "a": return "AR";
			case "c": return "ZH";
			case "e": return "EN";
			case "f": return "FR";
			case "r": return "RU";
			case "s": return "ES";
			default: return null;
		}
	}
	
	private String getElementTag(String attrName) {
		if(!isName(attrName)) return null;
		
		if(attrName.startsWith("full_name"))
			return "multilingualFullName";
		
		if(attrName.startsWith("long_name"))
			return "multilingualLongName";
		
		if(attrName.startsWith("official_name"))
			return "multilingualOfficialName";
		
		if(attrName.startsWith("name"))
			return "multilingualName";
		
		if(attrName.startsWith("short_desc"))
			return "multilingualShortDescription";
		
		return null;
	}
	
	private String sanitize(String conceptName) {
		try {
			return conceptName.replaceAll("\\s", "%20");
		} catch(Throwable t) {
			throw new RuntimeException(t);
		}
	}
}
