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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;
import org.apache.ws.jaxme.logging.Logger;
import org.apache.ws.jaxme.logging.LoggerAccess;
import org.apache.ws.jaxme.sqls.BinaryColumn;
import org.apache.ws.jaxme.sqls.Column;
import org.apache.ws.jaxme.sqls.ForeignKey;
import org.apache.ws.jaxme.sqls.Index;
import org.apache.ws.jaxme.sqls.SQLFactory;
import org.apache.ws.jaxme.sqls.Schema;
import org.apache.ws.jaxme.sqls.StringColumn;
import org.apache.ws.jaxme.sqls.Table;
import org.apache.ws.jaxme.sqls.hsqldb.HsqlDbSQLFactoryImpl;


/**
 * @author <a href="mailto:jwi@softwareag.com">Jochen Wiedmann</a>
 */
public class VersionTest extends TestCase {
	private static final Logger logger = LoggerAccess.getLogger(VersionTest.class);
	public VersionTest(String pName) { super(pName); }
	private Connection connection;

	private SQLFactory factory;
	private Schema schema;
	private Table mainTable, subTable, subsubTable;
	
	protected SQLFactory getSQLFactory() {
		if (factory == null) {
			factory = new HsqlDbSQLFactoryImpl();
		}
		return factory;
	}
	
	protected Table getMainTable() {
		if (mainTable == null) {
			Table mt = getSchema().newTable("MAIN");
			Column mtId = mt.newColumn("ID", Column.Type.BIGINT);
			Column mtVer = mt.newColumn("VER", Column.Type.INTEGER);
			StringColumn mtName = (StringColumn) mt.newColumn("NAME", Column.Type.VARCHAR);
			mtName.setLength(60);
			BinaryColumn mtSig = (BinaryColumn) mt.newColumn("SIG", Column.Type.BINARY);
			mtSig.setLength(16);
			mt.newColumn("DATE", Column.Type.DATE);
			
			Index primaryKey = mt.newPrimaryKey();
			primaryKey.addColumn(mtId);
			primaryKey.addColumn(mtVer);
			mainTable = mt;
		}
		return mainTable;
	}
	
	protected Table getSubTable() {
		if (subTable == null) {
			Table st = getSchema().newTable("SUB");
			StringColumn stId = (StringColumn) st.newColumn("ID", Column.Type.VARCHAR);
			stId.setLength(32);
			
			Column stMtId = st.newColumn("MTID", Column.Type.BIGINT);
			Column stMtVer = st.newColumn("MTVER", Column.Type.INTEGER);
			StringColumn stAddress = (StringColumn) st.newColumn("ADDRESS", Column.Type.VARCHAR);
			stAddress.setLength(60);
			StringColumn stEmail = (StringColumn) st.newColumn("EMAIL", Column.Type.VARCHAR);
			stEmail.setLength(60);
			stEmail.setNullable(true);
			
			Index primaryKey = st.newPrimaryKey();
			primaryKey.addColumn(stId);
			
			ForeignKey foreignKey = st.newForeignKey(getMainTable());
			foreignKey.addColumnLink(stMtId, getMainTable().getColumn("ID"));
			foreignKey.addColumnLink(stMtVer, getMainTable().getColumn("VER"));
			
			subTable = st;
		}
		return subTable;
	}
	
	protected Table getSubSubTable() {
		if (subsubTable == null) {
			Table sst = getSchema().newTable("SUBSUB");
			StringColumn sstId = (StringColumn) sst.newColumn("ID", Column.Type.VARCHAR);
			sstId.setLength(32);
			
			Column sstMtId = sst.newColumn("MTID", Column.Type.BIGINT);
			Column sstMtVer = sst.newColumn("MTVER", Column.Type.INTEGER);
			ForeignKey foreignKeySt = sst.newForeignKey(getMainTable());
			foreignKeySt.addColumnLink(sstMtId, getMainTable().getColumn("ID"));
			foreignKeySt.addColumnLink(sstMtVer, getMainTable().getColumn("VER"));
			
			StringColumn sstStId = (StringColumn) sst.newColumn("SSTID", Column.Type.VARCHAR);
			sstStId.setLength(32);
			ForeignKey foreignKeySst = sst.newForeignKey(getSubTable());
			foreignKeySst.addColumnLink(sstStId, getSubTable().getColumn("ID"));
			
			sst.newColumn("MTTS", Column.Type.TIMESTAMP);
		}
		return subsubTable;
	}
	
	protected Schema getSchema() {
		if (schema == null) {
			schema = getSQLFactory().getDefaultSchema();
			getMainTable();
			getSubTable();
			getSubSubTable();
		}
		return schema;
	}
	
	protected Connection getConnection() throws ClassNotFoundException, SQLException {
		if (connection == null) {
			final String mName = "getConnection";
			String driver = System.getProperty("jdbc.driver");
			if (driver == null) {
				driver = "org.hsqldb.jdbcDriver";
				logger.fine(mName, "System property 'jdbc.driver' not set, using default JDBC driver: " + driver);
			} else {
				logger.fine(mName, "Using JDBC driver: " + driver);
			}
			try {
				Class.forName(driver);
			} catch (ClassNotFoundException e) {
				try {
                    ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    if (cl == null) {
                        throw new ClassNotFoundException(driver);
                    }
                    cl.loadClass(driver);
				} catch (ClassNotFoundException ex) {
					throw e;
				}
			}
			
			String url = System.getProperty("jdbc.url");
			if (url == null) {
				url = "jdbc:hsqldb:build/db/db";
				logger.fine(mName, "System property 'jdbc.url' not set, using default JDBC url: " + url);
			} else {
				logger.fine(mName, "Using JDBC url: " + url);
			}
			
			String user = System.getProperty("jdbc.user");
			String password;
			if (user == null) {
				user = "sa";
				password = "";
				logger.fine(mName, "System property 'jdbc.user' not set, using default JDBC user: " + user);
			} else {
				password = System.getProperty("jdbc.password");
				logger.fine(mName, "Using JDBC user: " + user);
			}
			
			connection = DriverManager.getConnection(url, user, password);
		}
		return connection;
	}
	
	private String[] stmts = new String[] {
		"DELETE FROM SUBSUB",
		"DELETE FROM SUB",
		"DELETE FROM MAIN",
		"INSERT INTO MAIN VALUES (456, 'foo Main', 1, '000102030405060708090a0b0c0d0e0f', '2003-06-09')",
		"INSERT INTO MAIN VALUES (456, 'foo Main 2', 22, 'a0a1a2a3a4a5a6a7a8a9aaabacadaeaf', '2003-06-10')",
		"INSERT INTO MAIN VALUES (457, 'bar Main', 3, 'f0f1f2f3f4f5f6f7f8f9fafbfcfdfeff', '2003-06-10')",
		"INSERT INTO SUB VALUES (456, 1, 'Somewhere', '23423423', 'some@where')",
		"INSERT INTO SUB VALUES (456, 22, 'Somewhere', '23423434', 'some@where')",
		"INSERT INTO SUB VALUES (456, 22, 'Someone', '23421425', 'some@one')",
		"INSERT INTO SUB VALUES (457, 3, 'Somewhere', '23423426', 'some@where')",
		"INSERT INTO SUB VALUES (457, 3, 'Someone', '23421427', 'some@one')",
		"INSERT INTO SUBSUB VALUES ('324987', 456, 1, '23423423', '2003-06-10 12:00:00')",
		"INSERT INTO SUBSUB VALUES ('124987', 456, 22, '23423434', '2003-06-10 12:00:01')",
		"INSERT INTO SUBSUB VALUES ('124988', 456, 22, '23423434', '2003-06-10 12:00:02')",
		"INSERT INTO SUBSUB VALUES ('124989', 456, 22, '23421425', '2003-06-10 12:00:03')",
		"INSERT INTO SUBSUB VALUES ('124990', 457, 3, '23423426', '2003-06-10 12:00:04')",
		"INSERT INTO SUBSUB VALUES ('124991', 457, 3, '23421427', '2003-06-10 12:00:04')"
	};
	
	public void testCreate() throws ClassNotFoundException, SQLException {
		Connection conn = getConnection();
		for (int i = 0;  i < stmts.length;  i++) {
			PreparedStatement stmt = conn.prepareStatement(stmts[i]);
			try {
				stmt.executeUpdate();
			} catch (SQLException e) {
				if (stmts[i].startsWith("DROP TABLE ")  &&  "S0002".equals(e.getSQLState())) {
					continue;
				}
				throw e;
			}
		}
	}
	
	public void testClone() throws SQLException, ClassNotFoundException {
		Connection conn = getConnection();
		Object[] values = new Object[]{new Long(456), new Integer(1)};
		Object[] updatedValues = (new MAINCloner()).clone(conn, values);
		assertEquals(456, ((Long) updatedValues[0]).longValue());
		assertEquals(2, ((Integer) updatedValues[2]).intValue());
	}
}
