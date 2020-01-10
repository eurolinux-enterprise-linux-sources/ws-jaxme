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
package javax.xml.bind.helpers;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;


/** <p>Default implementation of a Marshaller. The JAXB provider needs
 * to implement only
 * {@link javax.xml.bind.Marshaller#marshal(Object, javax.xml.transform.Result)}.</p>
 *
 * @author JSR-31
 * @since JAXB1.0
 * @see javax.xml.bind.Marshaller 
 */
public abstract class AbstractMarshallerImpl implements Marshaller {
  private String encoding = "UTF-8";
  private String schemaLocation, noNSSchemaLocation;
  private boolean isFormattedOutput = true;
  private ValidationEventHandler eventHandler = DefaultValidationEventHandler.theInstance;

  /** <p>Creates a new instance of <code>AbstractMarshallerImpl</code>.</p>
   */
  public AbstractMarshallerImpl() {
  }

  /* @see javax.xml.bind.Marshaller#setValidationEventHandler(javax.xml.bind.ValidationEventHandler)}
   */
  public void setEventHandler(ValidationEventHandler pHandler) throws JAXBException {
    eventHandler = pHandler;
  }

  /* @see javax.xml.bind.Marshaller#getValidationEventHandler()}
   */
  public ValidationEventHandler getEventHandler() throws JAXBException {
    return eventHandler;
  }

  /** <p>Public interface to set the properties defined
   * by the {@link javax.xml.bind.Marshaller} interface.
   * Works by invocation of {@link #setEncoding(String)},
   * {@link #setFormattedOutput(boolean)},
   * {@link #setNoNSSchemaLocation(String)}, and
   * {@link #setSchemaLocation(String)} internally.</p>
   * <p>If you want to support additional properties,
   * you have to override this method in a subclass.</p>
   * @throws PropertyException Unknown property name
   */
  public void setProperty(String pName, Object pValue) throws PropertyException {
    if (pName == null) {
      throw new IllegalArgumentException("The property name must not be null.");
    }
    if (Marshaller.JAXB_ENCODING.equals(pName)) {
      setEncoding((String) pValue);
    } else if (Marshaller.JAXB_FORMATTED_OUTPUT.equals(pName)) {
      if (pValue == null) {
        setFormattedOutput(true);
      } else {
        setFormattedOutput(((Boolean) pValue).booleanValue());
      }
    } else if (Marshaller.JAXB_SCHEMA_LOCATION.equals(pName)) {
      setSchemaLocation((String) pValue);
    } else if (Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION.equals(pName)) {
      setNoNSSchemaLocation((String) pValue);
    } else {
      throw new PropertyException("Unknown property name: " + pName);
    }
  }

  /** <p>Public interface to get the properties defined
   * by the {@link javax.xml.bind.Marshaller} interface.
   * Works by invocation of {@link #getEncoding()},
   * {@link #isFormattedOutput()},
   * {@link #getNoNSSchemaLocation()}, and
   * {@link #getSchemaLocation()} internally.</p>
   * <p>If you want to support additional properties,
   * you have to override this method in a subclass.</p>
   * @throws PropertyException Unknown property name
   */
  public Object getProperty(String pName) throws PropertyException {
    if (pName == null) {
      throw new IllegalArgumentException("The property name must not be null.");
    }
    if (Marshaller.JAXB_ENCODING.equals(pName)) {
      return getEncoding();
    } else if (Marshaller.JAXB_FORMATTED_OUTPUT.equals(pName)) {
      return isFormattedOutput() ? Boolean.TRUE : Boolean.FALSE;
    } else if (Marshaller.JAXB_SCHEMA_LOCATION.equals(pName)) {
      return getSchemaLocation();
    } else if (Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION.equals(pName)) {
      return getNoNSSchemaLocation();
    } else {
      throw new PropertyException("Unknown property name: " + pName);
    }
  }

  /** <p>Returns the current output encoding.</p>
   * @see javax.xml.bind.Marshaller#JAXB_ENCODING
   * @return The current encoding, by default "UTF-8".
   */
  protected String getEncoding() {
    return encoding;
  }

  /** <p>Converts the given IANA encoding name into a Java encoding
   * name. This is a helper method for derived subclasses.</p>
   */
  protected String getJavaEncoding(String pEncoding)
        throws UnsupportedEncodingException {
     "".getBytes(pEncoding);  // Throws an UnsupportedEncodingException,
                              // if the encoding is unknown.
     return pEncoding;
  }

  /** <p>Sets the current output encoding.</p>
   * @see javax.xml.bind.Marshaller#JAXB_ENCODING
   */
  protected void setEncoding(String pEncoding) {
    encoding = pEncoding == null ? "UTF-8" : pEncoding;
  }

  /** <p>Sets the marshallers schema location.
   * Defaults to null.</p>
   * @see javax.xml.bind.Marshaller#JAXB_SCHEMA_LOCATION
   */
  protected void setSchemaLocation(String pSchemaLocation) {
    schemaLocation = pSchemaLocation;
  }

  /** <p>Returns the marshallers schema location.
   * Defaults to null.</p>
   * @see javax.xml.bind.Marshaller#JAXB_SCHEMA_LOCATION
   */
  protected String getSchemaLocation() {
    return schemaLocation;
  }
  
  /** <p>Sets the marshallers "no namespace" schema location.
   * Defaults to null.</p>
   * @see javax.xml.bind.Marshaller#JAXB_NO_NAMESPACE_SCHEMA_LOCATION
   */
  protected void setNoNSSchemaLocation(String pNoNSSchemaLocation) {
    noNSSchemaLocation = pNoNSSchemaLocation;
  }

  /** <p>Returns the marshallers "no namespace" schema location.
   * Defaults to null.</p>
   * @see javax.xml.bind.Marshaller#JAXB_NO_NAMESPACE_SCHEMA_LOCATION
   */
  protected String getNoNSSchemaLocation() {
    return noNSSchemaLocation;
  }

  /** <p>Sets whether the marshaller will create formatted
   * output or not. By default it does.</p>
   * @see javax.xml.bind.Marshaller#JAXB_FORMATTED_OUTPUT
   */
  protected void setFormattedOutput(boolean pFormattedOutput) {
    isFormattedOutput = pFormattedOutput;
  }

  /** <p>Returns whether the marshaller will create formatted
   * output or not. By default it does.</p>
   * @see javax.xml.bind.Marshaller#JAXB_FORMATTED_OUTPUT
   */
  protected boolean isFormattedOutput() {
    return isFormattedOutput;
  }

  /* @see javax.xml.bind.Marshaller#marshal(Object, java.io.OutputStream)
   */
  public final void marshal(Object pObject, OutputStream pStream)
        throws JAXBException {
    StreamResult sr = new StreamResult();
    sr.setOutputStream(pStream);
    marshal(pObject, sr);
  }

  /* @see javax.xml.bind.Marshaller#marshal(Object, java.io.Writer)
   */
  public final void marshal(Object pObject, Writer pWriter)
        throws JAXBException {
    StreamResult sr = new StreamResult();
    sr.setWriter(pWriter);
    marshal(pObject, sr);
  }

  /* @see javax.xml.bind.Marshaller#marshal(Object, org.xml.sax.ContentHandler)
   */
  public final void marshal(Object pObject, ContentHandler pHandler)
        throws JAXBException {
    SAXResult sr = new SAXResult();
    sr.setHandler(pHandler);
    marshal(pObject, sr);
  }

  /* @see javax.xml.bind.Marshaller#marshal(Object, org.w3c.dom.Node)
   */
  public final void marshal(Object pObject, Node pNode)
        throws JAXBException {
    DOMResult dr = new DOMResult();
    dr.setNode(pNode);
    marshal(pObject, dr);
  }

  /** <p>This method is unsupported in the default implementation
   * and throws an {@link UnsupportedOperationException}.</p>
   * @throws UnsupportedOperationException This method is not available in the
   *   default implementation.
   */
  public Node getNode(Object obj) throws JAXBException {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }
}

