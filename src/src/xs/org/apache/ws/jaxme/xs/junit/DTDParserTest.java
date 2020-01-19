package org.apache.ws.jaxme.xs.junit;

import java.io.File;
import java.io.FileReader;

import org.apache.ws.jaxme.xs.util.DTDParser;
import org.xml.sax.InputSource;

import junit.framework.TestCase;


/** A unit test for the
 *  {@link org.apache.ws.jaxme.xs.util.DTDParser}.
 */
public class DTDParserTest extends TestCase {
    /** Creates a new test case with the given name.
     */
	public DTDParserTest(String pName) { super(pName); }

    /** Parses the file XMLSchema.dtd.
     */
	public void testXMLSchemaDtd() throws Exception {
		String path = System.getProperty("path.xmlSchema.dtd", "examples/xs/XMLSchema.dtd");
        File f = new File(path);
        InputSource isource = new InputSource(new FileReader(f));
        isource.setSystemId(f.toURL().toString());
        new DTDParser().parse(isource);
    }
}
