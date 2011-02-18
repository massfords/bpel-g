package org.activebpel.services.jaxws;

import java.io.FileInputStream;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.activebpel.rt.base64.BASE64Encoder;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import active_endpoints.docs.wsdl.activebpeladmin._2007._01.activebpeladmin_wsdl.IAeAxisActiveBpelAdmin;

import com.active_endpoints.schemas.activebpeladmin._2007._01.activebpeladmin.AesDeployBprType;
import com.active_endpoints.schemas.activebpeladmin._2007._01.activebpeladmin.AesStringResponseType;


public class AeDeployBPRTest {

	@Test
	public void deployBPR() throws Exception {
        Service service = Service.create(new URL("http://localhost:8888/bpel-g/services/ActiveBpelAdmin"), new QName("http://docs.active-endpoints/wsdl/activebpeladmin/2007/01/activebpeladmin.wsdl", "ActiveBpelAdmin"));
        
		byte[] raw = IOUtils.toByteArray(new FileInputStream("src/test/resources/loanProcessCompleted.bpr"));
		String filedata = new BASE64Encoder().encode(raw);
		
		IAeAxisActiveBpelAdmin activeBpelAdmin = service.getPort(new QName("http://docs.active-endpoints/wsdl/activebpeladmin/2007/01/activebpeladmin.wsdl", "ActiveBpelAdminPort"), IAeAxisActiveBpelAdmin.class);
		AesDeployBprType withBase64File = new AesDeployBprType().withBprFilename("loanProcessCompleted.bpr").withBase64File(filedata);
		AesStringResponseType response = activeBpelAdmin.deployBpr(withBase64File);
		
		System.out.println(response.getResponse());
	}
}
