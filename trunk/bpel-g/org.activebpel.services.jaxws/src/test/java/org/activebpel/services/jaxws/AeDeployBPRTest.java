package org.activebpel.services.jaxws;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.activebpel.rt.base64.BASE64Encoder;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import active_endpoints.docs.wsdl.activebpeladmin._2007._01.activebpeladmin_wsdl.IAeAxisActiveBpelAdmin;

import com.active_endpoints.schemas.activebpeladmin._2007._01.activebpeladmin.AesDeployBprType;
import com.active_endpoints.schemas.activebpeladmin._2007._01.activebpeladmin.AesStringResponseType;

@Ignore
public class AeDeployBPRTest {

    @Test
    public void deployBPR() throws Exception {
        AesStringResponseType response = deploy("loanProcessCompleted.bpr");
        System.out.println(response.getResponse());
    }

    @Test
    public void deployODE() throws Exception {
        AesStringResponseType response = deploy("ode-project.zip");
        System.out.println(response.getResponse());
    }

    protected AesStringResponseType deploy(String aName) throws MalformedURLException, IOException,
            FileNotFoundException {
        Service service = Service.create(new URL(
                "http://localhost:8080/bpel-g/services/ActiveBpelAdmin"), new QName(
                "http://docs.active-endpoints/wsdl/activebpeladmin/2007/01/activebpeladmin.wsdl",
                "ActiveBpelAdmin"));

        byte[] raw = IOUtils.toByteArray(new FileInputStream("src/test/resources/" + aName));
        String filedata = new BASE64Encoder().encode(raw);

        IAeAxisActiveBpelAdmin activeBpelAdmin = service.getPort(new QName(
                "http://docs.active-endpoints/wsdl/activebpeladmin/2007/01/activebpeladmin.wsdl",
                "ActiveBpelAdminPort"), IAeAxisActiveBpelAdmin.class);
        AesDeployBprType withBase64File = new AesDeployBprType().withBprFilename(
                aName).withBase64File(filedata);
        AesStringResponseType response = activeBpelAdmin.deployBpr(withBase64File);
        return response;
    }
}
