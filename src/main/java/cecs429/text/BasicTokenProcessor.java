package cecs429.text;

import java.util.List;

/**
 * A BasicTokenProcessor creates terms from tokens by removing all non-alphanumeric characters from the token, and
 * converting it to all lowercase.
 */
public class BasicTokenProcessor implements TokenProcessor {
	@Override
	public String processToken(String token) {
		return token.replaceAll("\\W", "").toLowerCase();
	}
        
	public List<String> processTokenTwo(String token) {
		return null;
	}
}
