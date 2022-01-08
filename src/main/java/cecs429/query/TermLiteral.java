package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.text.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A TermLiteral represents a single term in a subquery.
 */
public class TermLiteral implements Query {
	private String mTerm = "";
        private TokenProcessor processor = new AdvQueryTokenProcessor();
	
	public TermLiteral(String term) {
		mTerm = term;
	}
	
	public String getTerm() {
		return mTerm;
	}
	
       
        @Override
	public List<Posting> getPostings(Index index) {
            List <Posting> r = new ArrayList(); 
                for(String j: processor.processTokenTwo(mTerm))
                {
                    
                    r.addAll(index.getPostings(j));
                }
                
		return r;
	}
	
        public void setProcessor(TokenProcessor tp)
        {
            processor = tp;
        }
        
	@Override
	public String toString() {
		return mTerm;
	}
}
