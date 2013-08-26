package org.activebpel.camel;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.camel.util.EndpointHelper;

public class AeBpelComponent extends DefaultComponent {

    @Override
    protected Endpoint createEndpoint(String aUri, String aRemaining,
                                      Map<String, Object> aParameters) throws Exception {
        AeBpelEndpoint endpoint = new AeBpelEndpoint(aUri, this);
        // use the helper class to set all of the properties
        EndpointHelper.setReferenceProperties(getCamelContext(), endpoint,
                aParameters);
        EndpointHelper.setProperties(getCamelContext(), endpoint, aParameters);
        return endpoint;
    }

}
