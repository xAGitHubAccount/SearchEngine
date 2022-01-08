package cecs429.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tartarus.snowball.SnowballStemmer;

/**
 * A BasicTokenProcessor creates terms from tokens by removing all non-alphanumeric characters from the token, and
 * converting it to all lowercase.
 */
public class AdvTokenProcessor implements TokenProcessor {
        @Override
	public String processToken(String token) {
		return token.replaceAll("\\W", "").toLowerCase();
	}
        @Override
	public List<String> processTokenTwo(String token) {
                List<String> tokens = new ArrayList<>();
		String mToken = token.replaceAll("^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+$", "");
                mToken = mToken.replace("\"", "");
                mToken = mToken.replace("'", "");
                if(mToken.contains("-"))
                {
                    String s = mToken.replace("-", "");
                    tokens.add(s);
                    tokens.addAll(Arrays.asList(mToken.split("-")));
                }
                else
                {
                    tokens.add(mToken);
                }
                
                ListIterator<String> iterator = tokens.listIterator();
                while (iterator.hasNext())
                {
                    iterator.set(iterator.next().toLowerCase());
                }
                
                Class stemClass = null;
                try {
                stemClass = Class.forName("org.tartarus.snowball.ext.englishStemmer");
                } catch (ClassNotFoundException ex) {
                Logger.getLogger(AdvTokenProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            
                SnowballStemmer stemmer = null;
                try {
                stemmer = (SnowballStemmer) stemClass.newInstance();
                } catch (InstantiationException ex) {
                Logger.getLogger(AdvTokenProcessor.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                Logger.getLogger(AdvTokenProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                List<String> results = new ArrayList<>();
                for(String x: tokens)
                {
                    stemmer.setCurrent(x);
                    stemmer.stem();

                        results.add(stemmer.getCurrent());

                }
     
                return results;
	}
}
