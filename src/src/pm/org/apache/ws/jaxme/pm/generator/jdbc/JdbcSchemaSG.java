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
 
package org.apache.ws.jaxme.pm.generator.jdbc;

import java.util.Iterator;
import java.util.List;

import org.apache.ws.jaxme.generator.sg.Context;
import org.apache.ws.jaxme.generator.sg.ObjectSG;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.generator.sg.SchemaSGChain;
import org.apache.ws.jaxme.generator.sg.TypeSG;
import org.apache.ws.jaxme.generator.sg.impl.SchemaSGChainImpl;
import org.apache.ws.jaxme.impl.JAXBContextImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class JdbcSchemaSG extends SchemaSGChainImpl {
  final JaxMeJdbcSG jdbcSG;

  /** <p>Creates a new instance of JdbcSchemaSG.</p>
   */
  protected JdbcSchemaSG(JaxMeJdbcSG pJdbcSG, SchemaSGChain pBackingObject) {
    super(pBackingObject);
    jdbcSG = pJdbcSG;
  }

  private Element createProperty(Element pParent, String pName, String pValue) {
    Element element = pParent.getOwnerDocument().createElementNS(JAXBContextImpl.CONFIGURATION_URI, "Property");
    pParent.appendChild(element);
    element.setAttributeNS(null, "name", pName);
    element.setAttributeNS(null, "value", pValue);
    return element;
  }

  public Document getConfigFile(SchemaSG pController, String pPackageName, List pContextList)
      throws SAXException {
    final String uri = JAXBContextImpl.CONFIGURATION_URI;
    final Document doc = super.getConfigFile(pController, pPackageName, pContextList);
    final Element root = doc.getDocumentElement();
    final Iterator iter = pContextList.iterator();
    for (Node node = root.getFirstChild();  node != null;  node = node.getNextSibling()) {
      if (node.getNodeType() == Node.ELEMENT_NODE
          &&  JAXBContextImpl.CONFIGURATION_URI.equals(node.getNamespaceURI())
          &&  "Manager".equals(node.getLocalName())) {
        final ObjectSG objectSG = (ObjectSG) iter.next();
        final TypeSG typeSG = objectSG.getTypeSG();
        if (typeSG.isComplex()) {
          final CustomTableData customTableData = (CustomTableData) typeSG.getProperty(jdbcSG.getKey());
          if (customTableData != null) {
            Element manager = (Element) node;
            final Context ctx = typeSG.getComplexTypeSG().getClassContext();
            manager.setAttributeNS(uri, "pmClass", ctx.getPMName().toString());

            final TableDetails tableDetails = customTableData.getTableDetails();
            final String driver = tableDetails.getDriver();
            if (driver != null) {
              createProperty(manager, "jdbc.driver", driver);
            }
            final String url = tableDetails.getUrl();
            if (url != null) {
              createProperty(manager, "jdbc.url", url);
            }
            final String user = tableDetails.getUser();
            if (user != null) {
              createProperty(manager, "jdbc.user", user);
            }
            final String password = tableDetails.getPassword();
            if (password != null) {
              createProperty(manager, "jdbc.password", password);
            }
          }
        }
      }
    }
    if (iter.hasNext()) {
      throw new IllegalStateException("More managers expected than found");
    }
    return doc;
  }
}
