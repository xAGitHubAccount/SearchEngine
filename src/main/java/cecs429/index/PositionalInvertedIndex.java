package cecs429.index;

import cecs429.documents.Document;
import java.nio.file.Path;
import java.util.*;


public class PositionalInvertedIndex implements Index {
	private HashMap <String, List<Posting>> dictionary = new HashMap<>();
        
	public PositionalInvertedIndex() 
        {}
	
	/**
	 * Associates the given documentId with the given term in the index.
     * @param term
     * @param d
     * @param position
	 */
	public void addTerm(String term, Document d, int position) 
        {
		if (dictionary.containsKey(term) == false) 
                {
                    List<Posting> list = new ArrayList<>();
                    Posting p = new Posting(d.getId());
                    p.setPosition(position);
                    list.add(p);
                    dictionary.put(term, list);  
                }
                else
                {
                    List<Posting> list = dictionary.get(term);

                    if(list.get(list.size() - 1).getDocumentId() != d.getId())
                    {
                        Posting p = new Posting(d.getId());
                        p.setPosition(position);
                        list.add(p);
                    } 
                    else
                    {
                        list.get(list.size() - 1).setPosition(position);
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
