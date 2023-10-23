package com.alibaba.cloud.sentinel.feign;

import feign.Contract;
import feign.MethodMetadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SentinelContractHolder implements Contract {
	private final Contract delegate;

	public static final Map<String, MethodMetadata> METADATA_MAP = new HashMap();

	public SentinelContractHolder(Contract delegate) {
		this.delegate = delegate;
	}

	@Override
	public List<MethodMetadata> parseAndValidateMetadata(Class<?> aClass) {
		List<MethodMetadata> metadatas = this.delegate.parseAndValidateMetadata(aClass);
		metadatas.forEach((metadata) ->
			METADATA_MAP.put(aClass.getName() + metadata.configKey(), metadata)
		);
		return metadatas;
	}
}
