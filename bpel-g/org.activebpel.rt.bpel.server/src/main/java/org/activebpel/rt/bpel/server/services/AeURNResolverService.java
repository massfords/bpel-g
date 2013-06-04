package org.activebpel.rt.bpel.server.services;

import bpelg.services.urnresolver.AeURNResolver;
import bpelg.services.urnresolver.types.AddMappingRequest;
import bpelg.services.urnresolver.types.GetMappingsRequest;
import bpelg.services.urnresolver.types.Mappings;
import bpelg.services.urnresolver.types.Mappings.Mapping;
import bpelg.services.urnresolver.types.Names;
import org.activebpel.rt.bpel.urn.IAeURNResolver;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AeURNResolverService implements AeURNResolver {

    @Inject
	IAeURNResolver mResolver;

	@Override
	public void addMapping(AddMappingRequest aBody) {
		getResolver().addMapping(aBody.getName(), aBody.getValue());
	}

	@Override
	public String getURL(String aBody) {
		return getResolver().getURL(aBody);
	}

	@Override
	public void removeMappings(Names aBody) {
		getResolver().removeMappings(aBody.getName().toArray(new String[aBody.getName().size()]));
	}

	@Override
	public Mappings getMappings(GetMappingsRequest aBody) {
		Map<String,String> map = getResolver().getMappings();
		List<Mapping> mappings = new LinkedList<>();
		for(Map.Entry<String, String> entry : map.entrySet()) {
			mappings.add(new Mapping().withName(entry.getKey()).withValue(entry.getValue()));
		}
		return new Mappings().withMapping(mappings);
	}

	public IAeURNResolver getResolver() {
		return mResolver;
	}

	public void setResolver(IAeURNResolver aResolver) {
		mResolver = aResolver;
	}
}
