package bpelg.jbi.db;

import java.io.File;

import javax.sql.DataSource;

import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeJNDIDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.RunScript;

import bpelg.jbi.BgContext;

public class BgFallbackDataSource extends AeJNDIDataSource {
    
    private static final Log sLog = LogFactory.getLog(BgFallbackDataSource.class);

    /**
     * @see org.activebpel.rt.bpel.server.engine.storage.sql.AeDataSource#createDelegate()
     */
    public DataSource createDelegate() throws AeStorageException {
        try {
            return super.createDelegate();
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
        
        String installRoot = BgContext.getInstance().getComponentContext().getInstallRoot();
        File dbDir = new File(installRoot, "db");
        File dbFile = new File(dbDir, "bpelg-embedded");
        File scriptFile = new File(installRoot, "h2script/ActiveBPEL-H2.sql");
        String url = "jdbc:h2:" + dbFile.getAbsolutePath() + ";MVCC=TRUE";
        
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(url);
        ds.setUser("sa");
        ds.setPassword("sa");
        
        if (!dbDir.isDirectory()) {
            
            sLog.debug("Creating embedding db: " + dbFile.getAbsolutePath());

            new RunScript().run("-url", url,
                                "-user", "sa",
                                "-password", "sa",
                                "-script", scriptFile.getAbsolutePath(),
                                "-showResults"
                                );
        } else {
            sLog.debug("Using embedded db at: " + dbDir.getAbsolutePath());
        }
        return ds;
    }
    
}
