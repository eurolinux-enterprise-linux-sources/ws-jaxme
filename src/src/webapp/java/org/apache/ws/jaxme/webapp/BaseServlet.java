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
package org.apache.ws.jaxme.webapp;

import java.io.File;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;


public abstract class BaseServlet extends HttpServlet {
	private static File workDir;
	
	public File getWorkDir() {
		return workDir;
	}

	public void cleanDirectory(File pDirectory) throws ServletException {
		File[] files = pDirectory.listFiles();
		for (int i = 0;  i < files.length;  i++) {
			File f = files[i];
			if (f.isFile()) {
				if (!f.delete()) {
					throw new UnavailableException("Unable to delete file " + f.getAbsolutePath());
				}
			} else if (f.isDirectory()) {
				cleanDirectory(f);
				if (!f.delete()) {
					throw new UnavailableException("Unable to delete directory " + f.getAbsolutePath());
				}
			} else {
				throw new UnavailableException("Unable to determine how to remove " + f.getAbsolutePath());
			}
		}
	}

	public void createDirectory(File pDirectory) throws ServletException {
		if (!pDirectory.mkdir()) {
			throw new UnavailableException("Unable to create working directory " + pDirectory);
		}
	}

	public void init() throws ServletException {
		synchronized (BaseServlet.class) {
			if (workDir == null) {
				String p = getServletContext().getInitParameter("http.proxyHost");
				if (p != null  &&  p.length() > 0) {
					String v = getServletContext().getInitParameter("http.proxyPort");
					if (v != null  &&  v.length() > 0) {
						log("http.proxyHost parameter detected, setting host=" + p + ", port=" + v);
						System.setProperty("http.proxyHost", p);
						System.setProperty("http.proxyPort", v);
					} else {
						throw new UnavailableException("The http.proxyHost parameter is set, but the http.proxyPort parameter is not set.");
					}
				} else {
					log("http.proxyHost parameter is not set");
				}
				String s = getServletContext().getInitParameter("work.dir");
				File f;
				if (s == null  ||  s.length() == 0) {
					s = System.getProperty("java.io.tmpdir");
					if (s == null  ||  s.length() == 0) {
						throw new UnavailableException("Neither the servlet context parameter work.dir nor the system property java.io.tmpdir are set.");
					}
					f = new File(s);
					if (!f.isDirectory()) {
						throw new UnavailableException("The directory " + s + " (specified by the system property java.io.tmpdir) does not exist.");
					}
				} else {
					f = new File(s);
					if (!f.isDirectory()) {
						throw new UnavailableException("The directory " + s + " (specified by the servlet context parameter work.dir) does not exist.");
					}
				}

				File g = new File(f, "jaxme");
				if (g.isDirectory()) {
					cleanDirectory(g);
				} else {
					createDirectory(g);
				}
				workDir = g;
			}
		}
	}
}
