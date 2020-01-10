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
package org.apache.ws.jaxme.pm.ino.api4j;

import javax.xml.bind.Element;
import javax.xml.bind.JAXBException;

import org.apache.ws.jaxme.JMManager;
import org.apache.ws.jaxme.Observer;
import org.apache.ws.jaxme.PMException;
import org.apache.ws.jaxme.PMParams;
import org.apache.ws.jaxme.impl.JAXBContextImpl;
import org.apache.ws.jaxme.impl.JMUnmarshallerHandlerImpl;
import org.apache.ws.jaxme.pm.impl.PMImpl;
import org.apache.ws.jaxme.pm.ino.InoObject;

import com.softwareag.tamino.db.api.accessor.TAccessLocation;
import com.softwareag.tamino.db.api.accessor.TDeleteException;
import com.softwareag.tamino.db.api.accessor.TInsertException;
import com.softwareag.tamino.db.api.accessor.TQuery;
import com.softwareag.tamino.db.api.accessor.TQueryException;
import com.softwareag.tamino.db.api.accessor.TUpdateException;
import com.softwareag.tamino.db.api.accessor.TXMLObjectAccessor;
import com.softwareag.tamino.db.api.connection.TConnection;
import com.softwareag.tamino.db.api.objectModel.TIteratorException;
import com.softwareag.tamino.db.api.objectModel.TNoSuchXMLObjectException;
import com.softwareag.tamino.db.api.objectModel.TXMLObject;
import com.softwareag.tamino.db.api.objectModel.TXMLObjectIterator;
import com.softwareag.tamino.db.api.objectModel.TXMLObjectModel;
import com.softwareag.tamino.db.api.objectModel.sax.TSAXObjectModel;
import com.softwareag.tamino.db.api.response.TResponse;


/** Common base class for
 * {@link org.apache.ws.jaxme.pm.ino.api4j.TaminoAPI4JRaPm}, and
 * {@link org.apache.ws.jaxme.pm.ino.api4j.TaminoAPI4JDbPm}.
 */
public abstract class TaminoAPI4JPm extends PMImpl {
    private static final ThreadLocal context = new ThreadLocal();
	private String collection;
    private static boolean isInitialized;
    private static TSAXObjectModel model;

    static JAXBContextImpl getJAXBContext() {
        return (JAXBContextImpl) context.get();
    }

    static void setJAXBContext(JAXBContextImpl pContext) {
        context.set(pContext);
    }

    public void init(JMManager pManager) throws JAXBException {
        super.init(pManager);
        if (!isInitialized) {
            synchronized(TaminoAPI4JRaPm.class) {
                if (!isInitialized) {
                    TSAXObjectModel m = new TSAXObjectModel(TaminoAPI4JRaPm.class.getName(),
                            								TJMElement.class, TJMElement.class,
                            								new DocumentDefaultHandler(),
															new ElementDefaultHandler());
                    TXMLObjectModel.register(m);
                    model = m;
                    isInitialized = true;
                }
            }
        }
        collection = pManager.getProperty("collection");
        if (collection == null  ||  collection.length() == 0) {
            throw new JAXBException("The property 'collection' must be set.");
        }
    }

	/** Returns the Tamino database collection.
	 */
	public String getCollection() {
	    return collection;
	}

	/** Sets the Tamino database collection.
	 */
	public void setCollection(String pCollection) {
	    collection = pCollection;
	}

    protected TXMLObject getTXMLObject(InoObject pElement) {
        return TXMLObject.newInstance(new TJMElement(pElement));
    }

    protected TSAXObjectModel getTSAXObjectModel() throws JAXBException {
        return model;
    }

    protected abstract TConnection getTConnection() throws PMException;

    public void select(Observer pObserver, String pQuery, PMParams pParams) throws PMException {
        TConnection conn = null;
        try {
        	JAXBContextImpl c = getManager().getFactory();
            setJAXBContext(c);
            JMUnmarshallerHandlerImpl h = (JMUnmarshallerHandlerImpl) c.createUnmarshaller().getUnmarshallerHandler();
            DocumentDefaultHandler.setUnmarshallerHandler(h);
            ElementDefaultHandler.initData();
            conn = getTConnection();
            TXMLObjectAccessor accessor = conn.newXMLObjectAccessor(TAccessLocation.newInstance(collection), model);
            TResponse response = accessor.query(TQuery.newInstance(pQuery));
            for (TXMLObjectIterator iter = response.getXMLObjectIterator();
                 iter.hasNext();  ) {
                TXMLObject object = iter.next();
                pObserver.notify(((TJMElement) object.getElement()).getJMElement());
            }
        } catch (TQueryException e) {
            throw new PMException(e);
        } catch (TNoSuchXMLObjectException e) {
            throw new PMException(e);
        } catch (TIteratorException e) {
            throw new PMException(e);
        } catch (JAXBException e) {
            throw new PMException(e);
        } finally {
            if (conn != null) { try { conn.close(); } catch (Throwable ignore) {} }
            setJAXBContext(null);
            DocumentDefaultHandler.setUnmarshallerHandler(null);
            ElementDefaultHandler.initData();
        }
    }

    public void insert(Element pElement) throws PMException {
        TConnection conn = null;
        try {
            setJAXBContext(getManager().getFactory());
            conn = getTConnection();
            TXMLObjectAccessor accessor = conn.newXMLObjectAccessor(TAccessLocation.newInstance(collection), model);
            accessor.insert(getTXMLObject((InoObject) pElement));
        } catch (TInsertException e) {
            throw new PMException(e);
        } finally {
            if (conn != null) { try { conn.close(); } catch (Throwable ignore) {} }
            setJAXBContext(null);
        }
    }

    public void update(Element pElement) throws PMException {
        TConnection conn = null;
        try {
            setJAXBContext(getManager().getFactory());
            conn = getTConnection();
            TXMLObjectAccessor accessor = conn.newXMLObjectAccessor(TAccessLocation.newInstance(collection), model);
            accessor.update(getTXMLObject((InoObject) pElement));
        } catch (TUpdateException e) {
            throw new PMException(e);
        } finally {
            if (conn != null) { try { conn.close(); } catch (Throwable ignore) {} }
            setJAXBContext(null);
        }
    }

    public void delete(Element pElement) throws PMException {
        TConnection conn = null;
        try {
            setJAXBContext(getManager().getFactory());
            conn = getTConnection();
            TXMLObjectAccessor accessor = conn.newXMLObjectAccessor(TAccessLocation.newInstance(collection), model);
            accessor.delete(getTXMLObject((InoObject) pElement));
        } catch (TDeleteException e) {
            throw new PMException(e);
        } finally {
            if (conn != null) { try { conn.close(); } catch (Throwable ignore) {} }
            setJAXBContext(null);
        }
    }
}
