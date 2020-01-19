package org.apache.ws.jaxme.generator.sg.impl.ccsg;

import org.apache.ws.jaxme.generator.sg.ComplexTypeSG;
import org.apache.ws.jaxme.generator.sg.GroupSG;
import org.apache.ws.jaxme.generator.sg.ParticleSG;
import org.xml.sax.SAXException;


/** Default implementation of a
 * {@link org.apache.ws.jaxme.generator.sg.impl.ccsg.ParticleVisitor},
 * with methods doing nothing.
 */
public class ParticleVisitorImpl implements ParticleVisitor {
	public void emptyType(ComplexTypeSG type) throws SAXException {
	}

	public void simpleContent(ComplexTypeSG type) throws SAXException {
	}

	public void startSequence(GroupSG group) throws SAXException {
	}

	public void endSequence(GroupSG group) {
	}

	public void startChoice(GroupSG group) {
	}

	public void endChoice(GroupSG group) {
	}

	public void startAll(GroupSG group) {
	}

	public void endAll(GroupSG group) {
	}

	public void startComplexContent(ComplexTypeSG type) throws SAXException {
	}

	public void endComplexContent(ComplexTypeSG type) throws SAXException {
	}

	public void simpleElementParticle(GroupSG pGroup, ParticleSG pParticle) throws SAXException {
	}

	public void complexElementParticle(GroupSG pGroup, ParticleSG particle) throws SAXException {
	}

	public void wildcardParticle(ParticleSG particle) {
	}
}
