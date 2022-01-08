package cecs429.index;

import java.util.ArrayList;
import java.util.List;

/**
 * A Posting encapulates a document ID associated with a search query component.
 */
public class Posting {
	private int mDocumentId;
        private double wdt ;
        private int positionsCount;
        private List<Integer> positions = new ArrayList();
	
	public Posting(int documentId) {
		mDocumentId = documentId;
                positionsCount = 0;
	}
	
        public void setPosition(int x)
        {
            positions.add(x);
            positionsCount++;
        }
        
        public void setWdt(double x)
        {
            wdt = x;
        }
        
        public double getWdt()
        {
            return wdt;
        }
        
        public List<Integer> getPositions()
        {
            return positions;
        }
        
	public int getDocumentId() {
		return mDocumentId;
	}
}
