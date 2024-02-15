package org.beatific.bot.selftest.tester;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProxyTesterTests {

    private static final String PERCENTAGE_SIGN = "%";
 
    @Test
    public void testURI() {
        try {
            URI uri = reconstructURI("34.160.111.145", "https", 443, new URI("https://ifconfig.me"));
            log.info(uri.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private URI reconstructURI(String host, String scheme, int port, URI original) {
		boolean encoded = containsEncodedParts(original);
		return UriComponentsBuilder.fromUri(original).scheme(scheme).host(host).port(port).build(encoded).toUri();
	}

    private static boolean containsEncodedParts(URI uri) {
		boolean encoded = (uri.getRawQuery() != null && uri.getRawQuery().contains(PERCENTAGE_SIGN))
				|| (uri.getRawPath() != null && uri.getRawPath().contains(PERCENTAGE_SIGN))
				|| (uri.getRawFragment() != null && uri.getRawFragment().contains(PERCENTAGE_SIGN));
		// Verify if it is really fully encoded. Treat partial encoded as unencoded.
		if (encoded) {
			try {
				UriComponentsBuilder.fromUri(uri).build(true);
				return true;
			}
			catch (IllegalArgumentException ignore) {
			}
			return false;
		}
		return false;
	}
}
