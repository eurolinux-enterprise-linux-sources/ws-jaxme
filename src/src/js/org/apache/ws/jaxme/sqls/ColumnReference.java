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
package org.apache.ws.jaxme.sqls;

/** <p>Interface of a column reference.</p>
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 */
public interface ColumnReference {
   /** <p>Returns the {@link TableReference} that created the
    * column reference.</p>
    */
   public TableReference getTableReference();

   /** <p>Returns the referenced {@link Column}.</p>
    */
   public Column getColumn();

   /** <p>Sets the references alias name, if any. Null indicates,
    * that an alias name may be choosen.</p>
    */
   public void setAlias(String pName);

   /** <p>Sets the references alias name, if any. Null indicates,
    * that an alias name may be choosen.</p>
    */
   public void setAlias(Column.Name pName);

   /** <p>Returns the references alias name, if any. Null indicates,
    * that an alias name may be choosen.</p>
    */
   public Column.Name getAlias();
}
