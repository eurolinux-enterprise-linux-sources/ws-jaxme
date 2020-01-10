/*
 * Copyright 2003, 2004  The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ws.jaxme.generator.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.ws.jaxme.util.DOMSerializer;
import org.apache.ws.jaxme.xs.SchemaTransformer;
import org.apache.ws.jaxme.xs.XSParser;
import org.apache.ws.jaxme.xs.jaxb.impl.JAXBParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;


/** Performs the modifications on an XML schema file, as
 * specified by an external binding file. This is based
 * on a suggestion from Ortwin Glück, see
 * <a href="http://wiki.apache.org/ws/JaxMe/ExternalSchemaBindings">
 * http://wiki.apache.org/ws/JaxMe/ExternalSchemaBindings</a>.
 */
public class Inliner implements SchemaTransformer {
	private static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	static {
		dbf.setNamespaceAware(true);
		dbf.setValidating(false);
	}

	private final Document[] bindings;
	private XMLReader transformedXMLReader;
	private InputSource transformedInputSource;

	/** Creates a new instance with the given bindings.
	 */
	public Inliner(Document[] pBindings) {
		bindings = pBindings;
	}

	private Document read(InputSource pSource, XMLReader pReader)
			throws SAXException, ParserConfigurationException, IOException {
		DocumentBuilder db = dbf.newDocumentBuilder();
		db.setEntityResolver(pReader.getEntityResolver());
		db.setErrorHandler(pReader.getErrorHandler());
		return db.parse(pSource);
	}

	private void apply(Document pSchema, Document pBindings, String pURI) throws XPathExpressionException {
		for (Iterator iter = getBindingElements(pBindings, pURI);  iter.hasNext();  ) {
			Element e = (Element) iter.next();
			String xpathQuery = e.getAttribute("node");
			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpath = xpathFactory.newXPath();
			NodeList nodes = (NodeList) xpath.evaluate(xpathQuery, e, XPathConstants.NODESET);
			for (int i = 0;  i < nodes.getLength();  i++) {
				Node node = nodes.item(i);
				if (node.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				Element match = (Element) node;
				if (!XSParser.XML_SCHEMA_URI.equals(match.getNamespaceURI())) {
					continue;
				}
				String prefix = match.getPrefix();
				Element annotationElement = getChild(match, prefix, "annotation");
				Element appInfoElement = getChild(annotationElement, prefix, "appInfo");
				for (Node child = e.getFirstChild();  child != null;  child = child.getNextSibling()) {
					appInfoElement.appendChild(pSchema.importNode(child, true));
				}
			}
		}
	}

	private Iterator getBindingElements(Document pBindings, String pURI) {
		Element root = pBindings.getDocumentElement();
		List result = new ArrayList();
		for (Node child = root.getFirstChild();  child != null;  child = child.getNextSibling()) {
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) child;
				if (JAXBParser.JAXB_SCHEMA_URI.equals(e.getNamespaceURI())  &&
					"bindings".equals(e.getLocalName())  &&
					pURI.equals(e.getAttribute("schemaLocation"))) {
					result.add(e);
				}
			}
		}
		return result.iterator();
	}

	private Element getChild(Element pParent, String pPrefix, String pName) {
		for (Node child = pParent.getFirstChild();  child != null;  child = child.getNextSibling()) {
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) child;
				if (pName.equals(e.getLocalName())  &&  XSParser.XML_SCHEMA_URI.equals(e.getNamespaceURI())) {
					return e;
				}
			}
		}

		String qName = (pPrefix == null || pPrefix.length() == 0) ? pName : pPrefix + ":" + pName;
		Element e = pParent.getOwnerDocument().createElementNS(XSParser.XML_SCHEMA_URI, qName);
		pParent.insertBefore(e, pParent.getFirstChild());
		return e;
	}

	public void parse(InputSource pSource, XMLReader pReader) throws ParserConfigurationException, SAXException, IOException {
		final Document schema = read(pSource, pReader);
		String uri = pSource.getSystemId();
		if (uri != null) {
			try {
				for (int i = 0;  i < bindings.length;  i++) {
					apply(schema, bindings[i], uri);
				}
			} catch (XPathExpressionException e) {
				throw new SAXException(e);
			}
		}
		transformedInputSource = new InputSource();
		transformedXMLReader = new XMLFilterImpl(){
			public void parse(InputSource pInput) throws SAXException, IOException {
				new DOMSerializer().serialize(schema, this);
			}

			public void parse(String pSystemId) throws SAXException, IOException {
				throw new IllegalStateException("Not implemented");
			}
		};
	}

	public InputSource getTransformedInputSource() {
		return transformedInputSource;
	}

	public XMLReader getTransformedXMLReader() {
		return transformedXMLReader;
	}
}
