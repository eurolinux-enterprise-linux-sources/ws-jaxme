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
package org.apache.ws.jaxme.sqls.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.ws.jaxme.sqls.Case;
import org.apache.ws.jaxme.sqls.Column;
import org.apache.ws.jaxme.sqls.Column.Type;


/** Implementation of a
 * {@link org.apache.ws.jaxme.sqls.Case} clause.
 */
public class CaseImpl implements Case {
	/** Implementation of a
     * {@link org.apache.ws.jaxme.sqls.Case.When} clause.
	 */
    public static class WhenImpl implements When {
        private final Object condition, value;
        /** Creates a new instance with the given condition
         * and value.
         */
        public WhenImpl(Object pCondition, Object pValue) {
        	condition = ValueImpl.asValue(pCondition);
            value = ValueImpl.asValue(pValue);
        }
        public Object getCondition() { return condition; }
        public Object getValue() { return value; }
    }

    private final Column.Type type;
    private Object checkedValue, elseValue;
    private List whens = new ArrayList();

    /** Creates a new instance with the given type.
	 */
	protected CaseImpl(Column.Type pType) {
        type = pType;
	}

	public void setCheckedValue(Object pValue) {
        checkedValue = ValueImpl.asValue(pValue);
    }
	public Object getCheckedValue() {
        return checkedValue;
    }
	public void addWhen(Object pCondition, Object pValue) {
        addWhen(new WhenImpl(pCondition, pValue));
    }
	public void addWhen(When pWhen) { whens.add(pWhen); }
	public void setElseValue(Object pValue) {
        elseValue = ValueImpl.asValue(pValue);
    }
	public Object getElseValue() { return elseValue; }
	public Type getType() { return type; }
	public When[] getWhens() {
        return (When[]) whens.toArray(new When[whens.size()]);
    }
}
