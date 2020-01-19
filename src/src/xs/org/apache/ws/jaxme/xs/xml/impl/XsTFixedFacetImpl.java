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
package org.apache.ws.jaxme.xs.xml.impl;

import org.apache.ws.jaxme.xs.xml.XsObject;
import org.apache.ws.jaxme.xs.xml.XsTFixedFacet;


/** <p>A common base class for {@link org.apache.ws.jaxme.xs.xml.XsTFacet} and
 * {@link org.apache.ws.jaxme.xs.xml.XsTNumFacet}.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public abstract class XsTFixedFacetImpl extends XsTFacetBaseImpl
    implements XsTFixedFacet {
  private boolean isFixed;

  protected XsTFixedFacetImpl(XsObject pParent) {
    super(pParent);
  }

  public void setFixed(boolean pFixed) {
    isFixed = pFixed;
  }

  public boolean isFixed() {
    return isFixed;
  }
}
