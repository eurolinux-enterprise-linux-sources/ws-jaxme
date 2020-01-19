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

/** <p>Interface of the element <code>xs:complexContent</code>,
 * as specified by:
 * <pre>
 *  &lt;xs:element name="complexContent" id="complexContent"&gt;
 *    &lt;xs:annotation&gt;
 *      &lt;xs:documentation
 *          source="http://www.w3.org/TR/xmlschema-1/#element-complexContent"/&gt;
 *    &lt;/xs:annotation&gt;
 *    &lt;xs:complexType&gt;
 *      &lt;xs:complexContent&gt;
 *        &lt;xs:extension base="xs:annotated"&gt;
 *          &lt;xs:choice&gt;
 *            &lt;xs:element name="restriction" type="xs:complexRestrictionType"/&gt;
 *            &lt;xs:element name="extension" type="xs:extensionType"/&gt;
 *          &lt;/xs:choice&gt;
 *          &lt;xs:attribute name="mixed" type="xs:boolean"&gt;
 *            &lt;xs:annotation&gt;
 *              &lt;xs:documentation&gt;
 *                Overrides any setting on complexType parent.
 *              &lt;/xs:documentation&gt;
 *            &lt;/xs:annotation&gt;
 *          &lt;/xs:attribute&gt;
 *        &lt;/xs:extension&gt;
 *      &lt;/xs:complexContent&gt;
 *    &lt;/xs:complexType&gt;
 *  &lt;/xs:element&gt;
 * </pre></p>
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface XsEComplexContent extends XsTAnnotated {
  public void setMixed(boolean pMixed);
  public Boolean isMixed();

  public XsTComplexRestrictionType createRestriction();
  public XsTComplexRestrictionType getRestriction();

  public XsTExtensionType createExtension();
  public XsTExtensionType getExtension();
}
