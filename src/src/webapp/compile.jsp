<%--
 Copyright 2003, 2004  The Apache Software Foundation
  
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
  
 http://www.apache.org/licenses/LICENSE-2.0
  
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%
	java.util.List errors = new java.util.ArrayList();
	boolean isValidating = Boolean.valueOf(request.getParameter("isValidating")).booleanValue();
	String url = request.getParameter("url");
	if (url == null  ||  url.length() == 0) {
		errors.add("The schema URL must not be empty.");
	} else {
		try {
			new java.net.URL(url);
		} catch (java.net.MalformedURLException e) {
			errors.add("The schema URL " + url + " is invalid.");
		}
	}
	String what = request.getParameter("what");
	if (!"compile".equals(what)  &&  !"validate".equals(what)) {
		errors.add("You must choose a proper action: Either 'compile' or 'validate'");
	}
		

	if (errors.size() == 0) {
		request.getRequestDispatcher("jaxme").forward(request, response);
	} else {
		%>
			<html><head><title>JaxMe Online - Error Message</title></head>
				<body><h1>JaxMe Online - Error Message</h1>
					<p>Sorry, but we are unable to process your request, due to the
						following problems:<ul>
						<% for (java.util.Iterator iter = errors.iterator();  iter.hasNext();  ) { %>
							<li><%= iter.next() %></li>
						<% } %></ul>
					<p>Please return to the <a href="javascript:history.back()">calling page</a>
						and fix your input.</p>
				</body>
			</html>
		<%
	}
%>
