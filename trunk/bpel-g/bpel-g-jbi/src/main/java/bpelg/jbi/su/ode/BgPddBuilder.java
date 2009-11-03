package bpelg.jbi.su.ode;

import java.io.File;
import java.io.FileFilter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.util.AeXPathUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BgPddBuilder {
    
    private static final Map NAMESPACES = Collections.singletonMap("ode", "http://www.apache.org/ode/schemas/dd/2007/03");
    
    private File mServiceUnitRoot;
    
    private Map<QName, BgPddInfo> mDeployments = new HashMap();
    
    public BgPddBuilder(File aServiceUnitRoot) {
        assert aServiceUnitRoot.isDirectory();
        assert new File(aServiceUnitRoot, "deploy.xml").isFile();
        
        mServiceUnitRoot = aServiceUnitRoot;
    }
    
    public void build() throws Exception {
        Map<QName,String> processNameToLoc = buildLocationMap();

        Document deploy = AeXmlUtil.toDoc(new File(mServiceUnitRoot, "deploy.xml"), null);
        
        List<Element> processes = AeXPathUtil.selectNodes(deploy, "/ode:deploy/ode:process", NAMESPACES);
        for(Element process : processes) {
            
            QName processName = AeXmlUtil.getAttributeQName(process, "name");
            String location = processNameToLoc.get(processName);
            
            BgPddInfo pddInfo = new BgPddInfo(processName, location);
            mDeployments.put(processName, pddInfo);
            
            // add provides
            List<Element> providesService = AeXPathUtil.selectNodes(process, "ode:provide/ode:service", NAMESPACES);
            for(Element provideService : providesService) {
                String plinkName = (String) AeXPathUtil.selectSingleObject(provideService, "string(../@partnerLink)", NAMESPACES);
                QName myService = AeXmlUtil.getAttributeQName(provideService, "name");
                String myEndpoint = provideService.getAttribute("port");
                pddInfo.addProvide(plinkName, myService, myEndpoint);
            }

            // add invokes
            List<Element> invokesService = AeXPathUtil.selectNodes(process, "ode:invoke/ode:service", NAMESPACES);
            for(Element invokeService : invokesService) {
                String plinkName = (String) AeXPathUtil.selectSingleObject(invokeService, "string(../@partnerLink)", NAMESPACES);
                QName partnerService = AeXmlUtil.getAttributeQName(invokeService, "name");
                String partnerEndpoint = invokeService.getAttribute("port");
                pddInfo.addInvoke(plinkName, partnerService, partnerEndpoint);
            }
        }
    }
    
    protected Map<QName,String> buildLocationMap() throws AeException {
        
        Map<QName,String> locationMap = new HashMap();
        
        File[] files = mServiceUnitRoot.listFiles(new FileFilter() {

            @Override
            public boolean accept(File aFile) {
                return aFile.getName().endsWith(".bpel");
            }
        });
        
        for(File file : files) {
            Document doc = AeXmlUtil.toDoc(file, null);
            QName processName = new QName(doc.getDocumentElement().getAttribute("targetNamespace"), doc.getDocumentElement().getAttribute("name"));
            String location = file.getName();
            locationMap.put(processName, location);
        }
        return locationMap;
    }
    
    protected Map<QName,BgPddInfo> getDeployments() {
        return mDeployments;
    }
}
