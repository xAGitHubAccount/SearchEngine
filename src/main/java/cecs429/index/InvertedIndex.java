package cecs429.index;

import cecs429.documents.Document;
import java.nio.file.Path;
import java.util.*;


public class InvertedIndex implements Index {
	private HashMap <String, List<Posting>> dictionary = new HashMap<>();
        
	public InvertedIndex() 
        {}
	
	/**
	 * Associates the given documentId with the given term in the index.
     * @param term
     * @param d
	 */
	public void addTerm(String term, Document d) 
        {
        
		if (dictionary.containsKey(term) == false) 
                {
                    List<Posting> list = new ArrayList<>();  
                    Posting p = new Posting(d.getId());
                    list.add(p);
                    dictionary.put(term, list);
                }
                else
                {
                    List<Posting> list = dictionary.get(term);
                    Posting p = new Posting(d.getId());
                    
                    if(list.get(list.size() - 1).getDocumentId() != d.getId())
                    {
                        list.add(p);
                    } 
                       
                }
        }
	
	@Override
	public List<Posting> getPostings(String term) {
		List<Posting> result = new ArrayList();
            result = dictionary.get(term);
            if(dictionary.containsKey(term) == false)
            {
                return result = new ArrayList();
            }
            else
            {
                return result;
            }
	}
	
        @Override
	public List<Posting> getPostingsNoPositions(String term) 
        {
            return null;
        }
        
        @Override
        public double getLd(int docID, Path path) 
        {
            return 0;
        }
        
        @Override
	public List<String> getVocabulary() {
                List<String> mVocabulary = new ArrayList<>(dictionary.keySet());
                Collections.sort(mVocabulary);
		return Collections.unmodifiableList(mVocabulary);
	}
}
