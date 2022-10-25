package ca.alberta.services.sithdfca;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

public class ContrivedTest {

	@Test
	public void checkJavaVersion() {
		String currentVersion = System.getProperty("java.version");
		boolean tooModern = currentVersion.compareTo("17.0.5") > 0;
		assertFalse(tooModern, String.format("java version %s is too modern!", currentVersion));
	}
}
