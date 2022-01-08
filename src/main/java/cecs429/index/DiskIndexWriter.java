/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cecs429.index;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import org.mapdb.*;


/**
 *
 * @author iN0va
 */
public class DiskIndexWriter
{
    public DiskIndexWriter()
    { 
    }
    
    //write weights of docs of a index to disk
    public static void writeWeight(double Ld, Path path)throws IOException 
    {
        DataOutputStream output = new DataOutputStream(new FileOutputStream(path.toFile(), true));
        output.writeDouble(Ld);
    }
    
    //write index to disk
    public List<Long> writeIndex(Index index, Path path)throws IOException 
    {
        DB db = DBMaker.fileDB("file.db").make();
        ConcurrentMap<String, Long> map = db
        .hashMap("map", Serializer.STRING, Serializer.LONG)
        .counterEnable()
        .createOrOpen();
        
        List<Long> TermByteIndex = new ArrayList<>();
        
        DataOutputStream output = new DataOutputStream(new FileOutputStream(path.toFile()));
        
        long outputSize = 0;
        for(String term: index.getVocabulary())
        {
            
            TermByteIndex.add((outputSize));
            map.put(term, (outputSize));
            List<Posting> pos = index.getPostings(term);
            //Number of DocumentIDs of term
            output.writeInt(pos.size());
            outputSize += 4;
            int prevDoc = 0;
            for(Posting x : pos)
            {
                output.writeInt(x.getDocumentId() - prevDoc);
                outputSize += 4;
                prevDoc = x.getDocumentId();
                output.writeDouble(1 + Math.log(x.getPositions().size()));
                outputSize += 8;
                output.writeInt(x.getPositions().size());
                outputSize += 4;
                int prevPos = 0;
                for(int y : x.getPositions())
                {
                    output.writeInt(y - prevPos);
                    outputSize += 4;
                    prevPos = y;
                }
            }
        }
        db.commit();
        db.close();
        return TermByteIndex;
    }
}