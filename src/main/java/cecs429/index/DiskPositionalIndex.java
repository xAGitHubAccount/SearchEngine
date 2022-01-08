package cecs429.index;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mapdb.*;


public class DiskPositionalIndex implements Index {
	
        private Path p;
	public DiskPositionalIndex(Path input) 
        {
            p = input;
        }
	
        //Get postings with positions
	@Override
	public List<Posting> getPostings(String term) 
        {
            DB db = DBMaker.fileDB("file.db").make();
            ConcurrentMap<String, Long> map = db
            .hashMap("map", Serializer.STRING, Serializer.LONG)
            .counterEnable()
            .createOrOpen();
            
            List<Posting> postings = new ArrayList<>();
            
            try {
                DataInputStream input = new DataInputStream(new FileInputStream(p.toFile()));
                Long start = map.get(term);
                
                input.skipNBytes(start);
                
                int prevDoc = 0;
                int docs = input.readInt();
                //Loops for Number of DocumentIDs of term
                for(int x = 0; x < docs; x++)
                {
                    //Create posting with DocumentID
                    prevDoc = prevDoc + input.readInt();
                    Posting p = new Posting(prevDoc);
                    p.setWdt(input.readDouble());
                    
                    int prevPos = 0;
                    int pos = input.readInt();
                    //Loop for number of positions
                    for(int y = 0; y < pos; y++)
                    {
                         prevPos = prevPos + input.readInt();
                         p.setPosition(prevPos);
                    }
                    postings.add(p);
                }
            } catch (FileNotFoundException ex) {
                ;
            } catch (IOException ex) {
                Logger.getLogger(DiskPositionalIndex.class.getName()).log(Level.SEVERE, null, ex);
            }
            db.close();
            return postings;
	}
	
        //Get postings without positions
        @Override
	public List<Posting> getPostingsNoPositions(String term) 
        {
            
            DB db = DBMaker.fileDB("file.db").make();
            ConcurrentMap<String, Long> map = db
            .hashMap("map", Serializer.STRING, Serializer.LONG)
            .counterEnable()
            .createOrOpen();
            
            List<Posting> postings = new ArrayList<>();
            
            try {
                DataInputStream input = new DataInputStream(new FileInputStream(p.toFile()));
                Long start = map.get(term);
                
                input.skipNBytes(start);
                
                int prevDoc = 0;
                //Loops for Number of DocumentIDs of term
                int z = input.readInt();
                for(int x = 0; x < z; x++)
                { 
                    //Create posting with DocumentID
                    prevDoc = prevDoc + input.readInt();                 
                    Posting p = new Posting(prevDoc);
                    p.setWdt(input.readDouble());
                    //Skip past y number of positions
                    int y = input.readInt();
                    input.skipNBytes(y * 4);
                    postings.add(p);
                }
            }
            catch (NullPointerException ex) 
            {
                Logger.getLogger(DiskPositionalIndex.class.getName()).log(Level.SEVERE, null, ex);
            } 
            catch (FileNotFoundException ex) 
            {
                Logger.getLogger(DiskPositionalIndex.class.getName()).log(Level.SEVERE, null, ex);
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(DiskPositionalIndex.class.getName()).log(Level.SEVERE, null, ex);
            }
            db.close();
            return postings;
	}
        
        //Get weights from file
        @Override
        public double getLd(int docID, Path path) 
        {
            DataInputStream input;
            try {
                input = new DataInputStream(new FileInputStream(path.toFile()));
                input.skipNBytes(docID * 8);
                return input.readDouble();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(DiskPositionalIndex.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(DiskPositionalIndex.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return 0;
        }
        
        @Override
	public List<String> getVocabulary() 
        {
            DB db = DBMaker.fileDB("file.db").make();
            ConcurrentMap<String, Long> map = db
            .hashMap("map", Serializer.STRING, Serializer.LONG)
            .counterEnable()
            .createOrOpen();    
        
            List<String> mVocabulary = new ArrayList<>(map.keySet());
            Collections.sort(mVocabulary);
            db.close();
            return Collections.unmodifiableList(mVocabulary);
	}
}
