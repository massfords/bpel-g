package org.activebpel.rt.bpel.server.engine.storage.sql;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.activebpel.rt.bpel.server.deploy.scanner.AeDeploymentFileInfo;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.RunScript;
import org.h2.util.IOUtils;

public class AeFallbackDataSource extends AeJNDIDataSource {
    
    private static final Log sLog = LogFactory.getLog(AeFallbackDataSource.class);

    /**
     * @see org.activebpel.rt.bpel.server.engine.storage.sql.AeDataSource#createDelegate()
     */
    public DataSource createDelegate() throws AeStorageException {
        try {
            DataSource ds = super.createDelegate();
            Connection c = ds.getConnection();
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery("select * from AeMetaInfo");
            boolean b = rs.next();
            rs.close();
            s.close();
            c.close();
            if (!b) {
                throw new Exception("db is empty");
            }
            return ds;
        } catch (Exception e) {
            try {
                return createFallbackDataSource();
            } catch (Exception e1) {
                throw new AeStorageException(e1);
            }
        }
    }

    private DataSource createFallbackDataSource() throws Exception {
        
        sLog.debug("No DataSource loaded from:" + getJNDIName() + " using embedded db");
        
        File dbDir = new File(AeDeploymentFileInfo.getDeploymentDirectory(), "db");
        File dbFile = new File(dbDir, "bpelg-embedded");
        File scriptFile = new File(AeDeploymentFileInfo.getDeploymentDirectory(), "h2script/ActiveBPEL-H2.sql");
        scriptFile.getParentFile().mkdirs();
        String url = "jdbc:h2:" + dbFile.getAbsolutePath() + ";MVCC=TRUE";
        
        IOUtils.copyAndClose(getClass().getResourceAsStream("/ActiveBPEL-H2.sql"), new FileOutputStream(scriptFile));
        
//        JdbcDataSource ds = new JdbcDataSource();
//        ds.setURL(url);
//        ds.setUser("sa");
//        ds.setPassword("sa");
        
        if (!dbDir.isDirectory()) {
            
            sLog.debug("Creating embedded db: " + dbFile.getAbsolutePath());

            new RunScript().run("-url", url,
                                "-user", "sa",
                                "-password", "sa",
                                "-script", scriptFile.getAbsolutePath(),
                                "-showResults"
                                );
        } else {
            sLog.debug("Using embedded db at: " + dbDir.getAbsolutePath());
        }
        
        final JdbcConnectionPool cp = JdbcConnectionPool.create(url, "sa", "sa");
        
        DataSource ds = new DataSource() {

        	private PrintWriter pw = null;
        	private int loginTimeout;
        	
			@Override
			public PrintWriter getLogWriter() {
				return pw;
			}

			@Override
			public void setLogWriter(PrintWriter aOut) {
				this.pw = aOut;
			}

			@Override
			public void setLoginTimeout(int aSeconds) {
				this.loginTimeout = aSeconds;
			}

			@Override
			public int getLoginTimeout() {
				return loginTimeout;
			}

			@Override
			public <T> T unwrap(Class<T> aIface) throws SQLException {
				throw new SQLException("not a wrapper for anything");
			}

			@Override
			public boolean isWrapperFor(Class<?> aIface) {
				return false;
			}

			@Override
			public Connection getConnection() throws SQLException {
				return cp.getConnection();
			}

			@Override
			public Connection getConnection(String aUsername, String aPassword)
					throws SQLException {
				return getConnection();
			}
        };
        return ds;
    }
}
