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
package org.apache.ws.jaxme.xs.xml;


/** <p>Implementation of <code>xs:qName</code>.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class XsQName {
  private final String namespaceURI;
  private final String localName;
  private final String prefix;

  public XsQName(XsAnyURI pNamespaceURI, String pLocalName) {
    this(pNamespaceURI == null ? null : pNamespaceURI.getURI(), pLocalName, null);
  }

  public XsQName(XsAnyURI pNamespaceURI, String pLocalName, String pPrefix) {
    this(pNamespaceURI == null ? null : pNamespaceURI.getURI(), pLocalName, pPrefix);
  }

  public XsQName(String pNamespaceURI, String pLocalName) {
    this(pNamespaceURI, pLocalName, null);
  }

  public XsQName(String pNamespaceURI, String pLocalName, String pPrefix) {
    namespaceURI = (pNamespaceURI == null) ? "" : pNamespaceURI;
    if (pLocalName == null) {
      throw new NullPointerException("The localName must not be null.");
    }
    int offset = pLocalName.indexOf(':');
    if (offset >= 0) {
    	throw new IllegalArgumentException("The localName " + pLocalName +
                                           " is invalid, because it contains a namespace prefix.");
    }
    localName = pLocalName;
    prefix = pPrefix;
  }

  public String getNamespaceURI() { return namespaceURI; }
  public String getLocalName() { return localName; }
  public String getPrefix() { return prefix; }

  public boolean equals(Object pObject) {
    if (pObject == null  ||  !(pObject instanceof XsQName)) {
      return false;
    }
    XsQName name = (XsQName) pObject;
    return name.getNamespaceURI().equals(getNamespaceURI())
    	&&  name.getLocalName().equals(getLocalName());
  }

  public int hashCode() {
      return getNamespaceURI().hashCode() + getLocalName().hashCode();
  }

  public String toString() {
    if ("".equals(namespaceURI)) { return localName; }
    return "{" + namespaceURI + "}" + localName;
  }

  public static String prefixOf(String pQName) {
    int offset = pQName.indexOf(':');
    if (offset > 0) {
      return pQName.substring(0, offset);
    } else {
      return "";
    }
  }
}
