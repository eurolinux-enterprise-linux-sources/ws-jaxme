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
package org.apache.ws.jaxme.xs.junit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;
import org.apache.ws.jaxme.xs.XSParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public class SyntaxTest extends TestCase {
  public SyntaxTest(String pName) {
    super(pName);
  }

  protected void parseSyntax(File f) throws IOException, SAXException, ParserConfigurationException {
    XSParser xsp = new XSParser();
    InputSource isource = new InputSource(new FileInputStream(f));
    isource.setSystemId(f.toURL().toString());
    xsp.parseSyntax(isource);
  }

  protected void parseLogical(File f) throws IOException, SAXException, ParserConfigurationException {
    XSParser xsp = new XSParser();
    InputSource isource = new InputSource(new FileInputStream(f));
    isource.setSystemId(f.toURL().toString());
    xsp.parse(isource);
  }

  protected void parseSyntax(String s) throws IOException, SAXException, ParserConfigurationException {
    parseSyntax(new File(s));
  }

  protected void parseLogical(String s) throws IOException, SAXException, ParserConfigurationException {
    parseLogical(new File(s));
  }

  public void testStructureSyntax() throws Exception {
    parseSyntax("examples/xs/structures.xsd");
  }

  public void testDatatypesSyntax() throws Exception {
    parseSyntax("examples/xs/datatypes.xsd");
  }

  public void testXmlSyntax() throws Exception {
    parseSyntax("examples/xs/xml.xsd");
  }

  public void testStructureLogical() throws Exception {
    parseLogical("examples/xs/structures.xsd");
  }
}
