package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;
import java.util.ArrayList;
import java.util.Collection;

import java.util.List;
import java.util.stream.Collectors;

/**
 * An OrQuery composes other Query objects and merges their postings with a union-type operation.
 */
public class OrQuery implements Query {
	// The components of the Or query.
	private List<Query> mChildren;
	
	public OrQuery(Collection<Query> children) {
		mChildren = new ArrayList<>(children);
	}
	
	@Override
	public List<Posting> getPostings(Index index) {
		// TODO: program the merge for an OrQuery, by gathering the postings of the composed Query children and
		// unioning the resulting postings.
		Query q1 = mChildren.get(0);
                List<Posting> l1 = q1.getPostings(index);
                
                for(int x = 1; x < mChildren.size(); x++)
                {
                    List<Posting> result = new ArrayList<>();
                    Query q2 = mChildren.get(x);
                    List<Posting> l2 = q2.getPostings(index);
                    
                    int i = 0;
                    int j = 0;
                    while(i < l1.size() && j < l2.size())
                    {
                        if(l1.get(i).getDocumentId() == l2.get(j).getDocumentId())
                        {
                            result.add(l1.get(i));
                            i++;
                            j++;
                        }
                        else if(l1.get(i).getDocumentId() < l2.get(j).getDocumentId())
                        {
                            result.add(l1.get(i));
                            i++;
                        }
                        else if(l1.get(i).getDocumentId() > l2.get(j).getDocumentId())
                        {
                            result.add(l2.get(j));
                            j++;
                        }
                    }
                    
                    if(i < l1.size())
                    {
                        while(i < l1.size())
                        {
                            result.add(l1.get(i));
                            i++;
                        }
                    }
                    else
                    {
                        while(j < l2.size())
                        {
                            result.add(l2.get(j));
                            j++;
                        }
                    }
                    
                    l1 = result;
                }
		return l1;
	}
	
	@Override
	public String toString() {
		// Returns a string of the form "[SUBQUERY] + [SUBQUERY] + [SUBQUERY]"
		return "(" +
		 String.join(" + ", mChildren.stream().map(c -> c.toString()).collect(Collectors.toList()))
		 + " )";
	}
}
