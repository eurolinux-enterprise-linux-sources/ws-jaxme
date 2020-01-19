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
	String url = request.getParameter("url");
	if (url == null) {
		url = "";
	}

	Boolean success = (Boolean) request.getAttribute("success");
	boolean isValidating = Boolean.valueOf(request.getParameter("isValidating")).booleanValue();
	ServletException e = (ServletException) request.getAttribute("error");

	String what = request.getParameter("what");
	boolean compiling = what == null  ||  "compile".equals(what);
%>
<html><head><title>JaxMe Online</title></head>
	<body><h1>JaxMe Online</h1>
		<p>This small web application allows you to run JaxMe online.
			By entering a schema URL, the JaxMe compiler is invoked
			and returns a ZIP file with the generated sources.</p>
	    <form method="get" action="compile.jsp">
	    	<table>
	    		<tr><th align="right">URL:</th>
	    	        <td><input type="text" name="url" value="<%= url %>"></input></td></tr>
				<tr><th align="right">Action:</th>
					<td><select name="what">
						  <option value="compile">Compile using JaxMe</option>
						  <option value="validate" <%= compiling ? "" : "\"selected\"" %>>Validate using JaxMeXS</option>
						</select></td></tr>
				<tr><th align="right">Validating parser:</th>
					<td><select name="isValidating">
						  <option value="false">No</option>
						  <option value="true" <%= isValidating ? "\"selected\"" : ""%>>Yes</option>
						</select></td></tr>
	    	    <tr><td></td>
	    	    	<td><input type="submit" value="Compile"></input></td></tr>
	    	</table>
	    </form>
<%
	if (success != null) {
		if (success.booleanValue()) {
			%><h3>Schema validation result</h3><p>The schema was validated successful.</p><%
		} else {
			%><h3>Schema validation result</h3><p>The schema was found to contain errors.</p><%
		}
	}
	if (e != null) {
		%><h3>Error Details</h3><pre><%
		    Throwable t = e.getRootCause() == null ? e : e.getRootCause();
		    t.printStackTrace(new java.io.PrintWriter(out));
		%></pre><%
	}
%>
	</body>
</html>
