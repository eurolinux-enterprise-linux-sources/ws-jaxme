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
package javax.xml.bind;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Properties;
import java.util.StringTokenizer;


/** <p>The <code>JAXBContext</code> provides the JAXB users anchor to
 * the implmentation and hos generated classes. A JAXBContext is used to
 * obtain instances of {@link javax.xml.bind.Marshaller},
 * {@link javax.xml.bind.Unmarshaller}, and
 * {@link javax.xml.bind.Validator}. To obtain a JAXBContext, the
 * application invokes
 * <pre>
 *   JAXBContext context = JAXBContext.newInstance("com.mycompany:com.mycompany.xml");
 * </pre>
 * The list of colon separated package names matches the list in the
 * schemas used to generate classes. In other words: If you have a
 * schema using package name "com.mycompany.xml", then this package
 * name has to be part of the list.</p>
 * <p>The <code>JAXBContext</code> class will scan the given list of packages
 * for a file called <samp>jaxb.properties</samp>. This file contains the
 * name of an instantiation class in the property
 * {@link #JAXB_CONTEXT_FACTORY}. (See {@link #newInstance(String)} for
 * details on how the class name is obtained.) Once such a file is found, the
 * given class is loaded via {@link ClassLoader#loadClass(java.lang.String)}.
 * The <code>JAXBContext</code> class demands, that the created object
 * has a method
 * <pre>
 *   public static JAXBContext createContext(String pPath, ClassLoader pClassLoader)
 *     throws JAXBException;
 * </pre>
 * This method is invoked with the same path and {@link ClassLoader} than
 * above. See {@link #newInstance(String,ClassLoader)}} for details on the choice
 * of the {@link ClassLoader}.</p>
 * <p>The created context will scan the same package path for implementation
 * specific configuration details (in the case of the <code>JaxMe</code>
 * application a file called <samp>Configuration.xml</samp> in any of the
 * packages) and do whatever else is required to initialize the runtime.
 * In particular it will invoke
 * {@link DatatypeConverter#setDatatypeConverter(DatatypeConverterInterface)}.</p>
 *
 * @author JSR-31
 * @since JAXB1.0
 * @see Marshaller
 * @see Unmarshaller
 * @see Validator
 */
public abstract class JAXBContext {
  private static final Class[] CONTEXT_CLASSES = new Class[]{String.class, ClassLoader.class};

  /** <p>This is the name of the property used to determine the name
   * of the initialization class: "javax.xml.bind.context.factory".
   * The name is used by {@link #newInstance(String)} and
   * {@link #newInstance(String,ClassLoader)}. It contains a class
   * name. The class must contain a static method
   * <pre>
   *   public static JAXBContext createContext(String, ClassLoader) throws JAXBException;
   * </pre>
   * which is invoked to create the actual instance of JAXBContext.</p>
   */
  public static final java.lang.String JAXB_CONTEXT_FACTORY = "javax.xml.bind.context.factory";

  /** <p>Creates a new instance of <code>JAXBContext</code> by applying
   * the following algorithm:
   * <ol>
   *   <li>The first step is to determine the name of an initialization class.
   *     For any of the package names in the list given by
   *     <code>pPath</code> the <code>JAXBContext</code> class will try to find a file
   *     called <samp>jaxb.properties</samp>. This file's got to be in
   *     standard property file format. The <code>JAXBContext</code> class
   *     will load the property file.</li>
   *   <li>A property called "javax.xml.bind.context.factory" (see
   *     {@link #JAXB_CONTEXT_FACTORY}) is evaluated. It must contain the
   *     name of an initialization class. The initialization class is
   *     loaded via
   *     <code>Thread.currentThread().getContextClassLoader().loadClass(String)</code>.</li>
   *   <li>The initialization class must contain a method
   *     <pre>
   *       public static JAXBContext createContext(String, ClassLoader) throws JAXBException;
   *     </pre>
   *     which is invoked with the <code>pPath</code> argument and the
   *     {@link ClassLoader} of the <code>JAXBContext</class> class as
   *     parameters. The result of this method is also used as the
   *     result of the <code>newInstance(String)</code> method.</li>
   * </ol>
   * @param pPath A colon separated path of package names where to look for
   *   <samp>jaxb.properties</samp> files. The package names must match the
   *   generated classes which you are going to use in your application.
   * @return An initialized instance of <code>JAXBContext</code>.
   * @throws JAXBException An error occurred while creating the JAXBContext instance. 
   */  
  public static JAXBContext newInstance(java.lang.String pPath) throws JAXBException {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    if (cl == null) {
      cl = JAXBContext.class.getClassLoader();
    }
    return newInstance(pPath, cl);
  }

  /** <p>Creates a new instance of <code>JAXBContext</code> by applying
   * the following algorithm:
   * <ol>
   *   <li>The first step is to determine the name of an initialization class.
   *     For any of the package names in the list given by
   *     <code>pPath</code> the <code>JAXBContext</code> class will try to find a file
   *     called <samp>jaxb.properties</samp>. This file's got to be in
   *     standard property file format. The <code>JAXBContext</code> class
   *     will load the property file.</li>
   *   <li>A property called "javax.xml.bind.context.factory" (see
   *     {@link #JAXB_CONTEXT_FACTORY}) is evaluated. It must contain the
   *     name of an initialization class. The initialization class is
   *     loaded via
   *     <code>pClassLoader.loadClass(String)</code>.</li>
   *   <li>The initialization class must contain a method
   *     <pre>
   *       public static JAXBContext createContext(String, ClassLoader) throws JAXBException;
   *     </pre>
   *     which is invoked with the parameters <code>pPath</code> and
   *     <code>pClassLoader</code>. The result of this method is also
   *     used as the result of the <code>newInstance(String)</code>
   *     method.</li>
   * </ol>
   * @param pPath A colon separated path of package names where to look for
   *   <samp>jaxb.properties</samp> files. The package names must match the
   *   generated classes which you are going to use in your application.
   * @return An initialized instance of <code>JAXBContext</code>.
   * @throws JAXBException An error occurred while creating the JAXBContext instance.
   */
  public static JAXBContext newInstance(String pPath, ClassLoader pClassLoader) throws JAXBException {
    if (pPath == null) {
      throw new JAXBException("The context path must not be null.");
    }
    if (pClassLoader == null) {
      throw new JAXBException("The classloader must not be null.");
    }
    for (StringTokenizer st = new StringTokenizer(pPath, ":");  st.hasMoreTokens();  ) {
      String packageName = st.nextToken();
      String resourceName = packageName.replace('.', '/') + "/jaxb.properties";
      URL resource = pClassLoader.getResource(resourceName);
      if (resource == null) {
        continue;
      }
      Properties props = new Properties();
      InputStream istream = null;
      try {
        istream = resource.openStream();
        props.load(istream);
        istream.close();
        istream = null;
      } catch (IOException e) {
        throw new JAXBException("Failed to load property file " + resource, e);
      } finally {
        if (istream != null) { try { istream.close(); } catch (Throwable ignore) {} }
      }
      String className = props.getProperty(JAXB_CONTEXT_FACTORY);
      if (className == null) {
        throw new JAXBException("The property " + JAXB_CONTEXT_FACTORY + " is not set in " + resource);
      }
      Class c;
      try {
        c = pClassLoader.loadClass(className);
      } catch (ClassNotFoundException e) {
        throw new JAXBException("The class " + className + ", referenced by property " +
                                 JAXB_CONTEXT_FACTORY + " in " + resource + ", could not be loaded.");
      }
      Method m;
      try {
        m = c.getMethod("createContext", CONTEXT_CLASSES);
      } catch (NoSuchMethodException e) {
        throw new JAXBException("The class " + c + " does not have a method 'public static createContext(String, ClassLoader) throws JAXBException'");
      }
      int modifiers = m.getModifiers();
      if (m == null  ||  !Modifier.isStatic(modifiers)  ||  !Modifier.isPublic(modifiers)) {
        throw new JAXBException("The class " + c + " does not have a method 'public static createContext(String, ClassLoader) throws JAXBException'");
      }
      Object o;
      try {
        o = m.invoke(null, new Object[]{pPath, pClassLoader});
      } catch (IllegalAccessException e) {
        throw new JAXBException("Illegal access to method " + c.getName() +
                                 ".createContext(String,ClassLoader).", e);
      } catch (InvocationTargetException e) {
        Throwable t = e.getTargetException();
        throw new JAXBException(t.getClass().getName() + " in method " + c.getName() +
                                 ".createContext(String,ClassLoader): " + t.getMessage(), t);
      }
      if (o == null) {
        throw new JAXBException("The method " + c.getName() +
                                 ".createContext(String,ClassLoader) returned null.");
      }
      try {
        return (JAXBContext) o;
      } catch (ClassCastException e) {
        throw new JAXBException("The object created by " + c.getName() +
                                 ".createContext(String,ClassLoader) cannot be casted to " +
                                 JAXBContext.class.getName() + ".", e);
      }
    }
    throw new JAXBException("Failed to resolve resource jaxb.properties via path " + pPath +
                             " and ClassLoader " + pClassLoader);
  }

  /** <p>Creates a new instance of {@link Marshaller}. The
   * {@link Marshaller} can be used
   * to convert JAXB objects into XML data.</p>
   * <p><em>Note</em>: Marshallers are reusable, but not reentrant (thread safe).</p>
   */
  public abstract Marshaller createMarshaller() throws JAXBException;

  /** <p>Creates a new instance of {@link Unmarshaller}. The
   * {@link Unmarshaller} can be used
   * to convert XML data into JAXB objects.</p>
   * <p><em>Note</em>: Unmarshallers are reusable, but not reentrant (thread safe).</p>
   */
  public abstract Unmarshaller createUnmarshaller() throws JAXBException;

  /** <p>Creates a new instance of {@link Validator}. The
   * {@link Validator} can be used to validate JAXB objects.</p>
   */
  public abstract Validator createValidator() throws JAXBException;
}
