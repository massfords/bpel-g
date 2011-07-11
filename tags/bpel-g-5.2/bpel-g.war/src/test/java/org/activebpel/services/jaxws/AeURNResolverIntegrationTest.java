package org.activebpel.services.jaxws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.junit.Before;
import org.junit.Test;

import bpelg.services.urnresolver.AeURNResolver;
import bpelg.services.urnresolver.types.AddMappingRequest;
import bpelg.services.urnresolver.types.GetMappingsRequest;
import bpelg.services.urnresolver.types.Mappings;
import bpelg.services.urnresolver.types.Mappings.Mapping;
import bpelg.services.urnresolver.types.Names;

public class AeURNResolverIntegrationTest {
    
	AeURNResolver resolver;

    @Before
	public void setUp() throws Exception {
    	String catalina_port = System.getProperty("CATALINA_PORT", "8080");
	    Service svc = Service.create(new URL("http://localhost:" + catalina_port + "/bpel-g/cxf/URNResolver?wsdl"), new QName("urn:bpel-g:services:urn-resolver", "URNResolver"));
	    resolver = svc.getPort(AeURNResolver.class);
	}
    
    @Test
    public void addMapping() throws Exception {
    	String urn = "urn:" + UUID.randomUUID().toString();
	    String expectedValue = "http://localhost:8080/HelloWorld";
		resolver.addMapping(new AddMappingRequest().withName(urn).withValue(expectedValue));
		
		assertTrue(getMappings().containsKey(urn));
		
	    assertEquals(expectedValue, resolver.getURL(expectedValue));
	    
	    resolver.removeMappings(new Names().withName(urn));
	    
	    Map<String, String> names = getMappings();
	    assertFalse(names.containsKey(urn));
    }

	protected Map<String, String> getMappings() {
		Mappings mappings = resolver.getMappings(new GetMappingsRequest());
	    Map<String,String> names = new HashMap<String,String>();
	    for(Mapping mapping : mappings.getMapping()) {
	    	names.put(mapping.getName(), mapping.getValue());
	    }
		return names;
	}

}
