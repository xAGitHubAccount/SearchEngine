package cecs429.query;

import cecs429.index.*;
import cecs429.text.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a phrase literal consisting of one or more terms that must occur in sequence.
 */
public class PhraseLiteral implements Query {
	// The list of individual terms in the phrase.
	private List<String> mChildren = null;
	private TokenProcessor processor = new AdvQueryTokenProcessor();
	/**
	 * Constructs a PhraseLiteral with the given individual phrase terms.
     * @param children
	 */
	public PhraseLiteral(List<String> children) 
        {
            mChildren = children;
	}
	
	@Override
	public List<Posting> getPostings(Index index){
               
                
                List<String> proc = processor.processTokenTwo(mChildren.get(0));
                // TODO: program this method. Retrieve the postings for the individual terms in the phrase,
		// and positional merge them together.
                
                List<Posting> l1;
            
                l1 = index.getPostings(proc.get(0));
                
                //x = k 
                for(int x = 1; x < mChildren.size(); x++)
                {
                    List<String> proc2 = processor.processTokenTwo(mChildren.get(x));
                    List<Posting> result = new ArrayList<>();
                    List<Posting> l2;
                    
                    l2 = index.getPostings(proc2.get(0));
                    
                    int i = 0;
                    int j = 0;
                    while(i < l1.size() && j < l2.size())
                    {
                        if(l1.get(i).getDocumentId() == l2.get(j).getDocumentId())
                        {
                            List<Integer> p1 = l1.get(i).getPositions();
                            List<Integer> p2 = l2.get(j).getPositions();
                            int a = 0;
                            
                            Posting n = new Posting(l1.get(i).getDocumentId());
                            
                            while(a < p1.size())
                            {
                                int position = Collections.binarySearch(p2, (p1.get(a)+x));
                                if(position >= 0)
                                {
                                    n.setPosition(p1.get(a));
                                }
                                a++;
                                
                            }
                            if(n.getPositions().size() > 0)
                            {
                                result.add(n);
                            }
                            i++;
                            j++;
                        }
                        else if(l1.get(i).getDocumentId() < l2.get(j).getDocumentId())
                        {
                            i++;
                        }
                        else if(l1.get(i).getDocumentId() > l2.get(j).getDocumentId())
                        {
                            j++;
                        }
                    }
                    l1 = result;
                }
                
                return l1;
	}
	
        public void setProcessor(TokenProcessor tp)
        {
            processor = tp;
        }
        
	@Override
	public String toString() {
		return "\"" + String.join(" ", mChildren.stream().map(c -> c.toString()).collect(Collectors.toList())) + "\"";
	}
}
