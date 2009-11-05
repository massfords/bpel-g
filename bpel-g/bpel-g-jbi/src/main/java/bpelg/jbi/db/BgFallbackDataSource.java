package bpelg.jbi.db;

import java.io.File;
import java.util.Map;

import javax.sql.DataSource;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeJNDIDataSource;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLConfig;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.RunScript;

import bpelg.jbi.BgContext;

public class BgFallbackDataSource extends AeJNDIDataSource {

    public BgFallbackDataSource(Map aConfig, AeSQLConfig aSQLConfig) throws AeException {
        super(aConfig, aSQLConfig);
    }

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
        
        String installRoot = BgContext.getInstance().getComponentContext().getInstallRoot();
        File dbDir = new File(installRoot, "db");
        File scriptFile = new File(installRoot, "h2script/ActiveBPEL-H2.sql");
        String url = "jdbc:h2:" + dbDir.getAbsolutePath() + ";MVCC=TRUE";
        
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(url);
        ds.setUser("sa");
        ds.setPassword("sa");
        
        if (!dbDir.isDirectory()) {
            
            new RunScript().run("-url", url,
                                "-user", "sa",
                                "-password", "sa",
                                "-script", scriptFile.getAbsolutePath(),
                                "-showResults"
                                );
        }
        return ds;
    }
    
}
