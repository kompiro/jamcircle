package org.kompiro.jamcircle;

import java.util.UUID;

import net.java.ao.EntityManager;
import net.java.ao.ValueGenerator;

public class UUIDValueGenerator implements ValueGenerator<String> {

	public String generateValue(EntityManager manager) {
		return UUID.randomUUID().toString();
	}

}
