package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.text.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A TermLiteral represents a single term in a subquery.
 */
public class NearLiteral implements Query {
	private String first = "";
        private String second = "";
        private int k = 0;
        private TokenProcessor processor = new AdvQueryTokenProcessor();
	
	public NearLiteral(List<String> children) {
		first = children.get(0);
                second = children.get(2);
                k = Character.getNumericValue(children.get(1).charAt(children.get(1).length() - 1));
	}
       
        @Override
	public List<Posting> getPostings(Index index) {
            List<Posting> result = new ArrayList<>();
            List<Posting> l1 = new ArrayList<>();
            List<Posting> l2 = new ArrayList<>();
            
            for(String j: processor.processTokenTwo(first))
            {
                
                l1.addAll(index.getPostings(j));
            }
            
            for(String j: processor.processTokenTwo(second))
            {
                
                l2.addAll(index.getPostings(j));
            }
            
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
                        int position = Collections.binarySearch(p2, ((p1.get(a))));
                        position = Math.abs(position + 1);
                        if((position) < p2.size())
                        {
                            int check = p2.get(position) - p1.get(a);
                            if(check <= k && check >= 0)
                            {
                                n.setPosition(p1.get(a));
                            }
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
            return result;
        }
	
        public void setProcessor(TokenProcessor tp)
        {
            processor = tp;
        }
        
	@Override
	public String toString() {
		return "[" + first + " " + k + " " + second + "]";
	}
}
