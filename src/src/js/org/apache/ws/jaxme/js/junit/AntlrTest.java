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
package org.apache.ws.jaxme.js.junit;

import junit.framework.TestCase;

/**
 * Test for JavaParser.
 */
public class AntlrTest extends TestCase {

	/**
	 * Tests the JavaParser.
	 */
	public void test() throws Throwable {
		final String path = System.getProperty("js.src")
			+ "/org/apache/ws/jaxme/sqls/impl/SQLGeneratorImpl.java";
		org.apache.ws.jaxme.js.util.JavaParser.main(new String[]{path});
	}
	
}
