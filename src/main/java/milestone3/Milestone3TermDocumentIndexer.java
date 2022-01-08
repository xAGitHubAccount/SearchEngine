package milestone3;

import cecs429.documents.*;
import cecs429.text.*;
import cecs429.index.*;
import cecs429.query.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tartarus.snowball.SnowballStemmer;
/**
 *
 * @author Jesus
 */
public class Milestone3TermDocumentIndexer {
    public static void main(String[] args) throws IOException
    {      
        Scanner console = new Scanner(System.in);     
        DocumentCorpus corpus = new DirectoryCorpus(Paths.get("corpus/relevance_cranfield"));
        Index index = new DiskPositionalIndex(Paths.get("corpus/index/postings.bin"));  
        BooleanQueryParser bqp = new BooleanQueryParser();
        RankedQueryParser rqp = new RankedQueryParser();
        String input;
        int mode = 0;
        
        //Prompt user to build index or go straight to choosing a mode to begin querying
        while(true)
        {
            try 
            {
                System.out.println("Input 1) Build Index 2) Continue");
                mode = console.nextInt();
                if(mode == 1)
                {
                    console.nextLine();
                    System.out.println("Enter folder/directory to index");
                    input = console.nextLine();
                    Path p = Paths.get(input);
                    System.out.println("Starting to index: " + input);
                    corpus = new DirectoryCorpus(p);
                
                    DiskIndexWriter k = new DiskIndexWriter();
                    k.writeIndex(PositionalInvertedIndexCorpus(corpus), Paths.get("corpus/index/postings.bin"));
                    index = new DiskPositionalIndex(Paths.get("corpus/index/postings.bin"));
                    System.out.println("Done indexing");
                    System.out.println();
                    break;
                }
                else if(mode == 2)
                {
                    corpus.getDocuments();
                    break;
                }
                        
                console.nextLine();
                System.out.println("Incorrect input");
            }
            catch(Exception e ) 
            {
                System.out.println("Incorrect input");
                console.next();
            }
        }  
        
        HashMap <String, List<Integer>> queryRel = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader("corpus/relevance_cranfield/relevance/queries"));
        BufferedReader reader2 = new BufferedReader(new FileReader("corpus/relevance_cranfield/relevance/qrel"));
        String line = "";
        List<Integer> docList = new ArrayList<>();
        do
        {
            line = reader.readLine();
            if(line != null)
            {
                for (String s: reader2.readLine().split(" "))
                {
                    docList.add(Integer.parseInt(s));
                }
                queryRel.put(line.substring(0, line.length()- 1), docList);
            }
            
            docList = new ArrayList<>();
        }while(line != null);

        //Start of main loop of code
        while(true)
        {
            //Asks for query
            console.nextLine();
            System.out.println("Input a special search query or type 1 for single query or 2 to query the entire set");
            input = console.nextLine();
            
            //Checks for special query :q to quit/close the program      
            if (input.equals(":q"))
            {
                break;
            }
            
            //Outputs the first 1000 terms in the vocab of the positional index
            else if (input.equals(":vocab"))
            {
                List<String> vocab = index.getVocabulary();
                if(vocab.size() > 1000)
                {
                    for(int x = 0; x < 1000; x++)
                    {
                        System.out.println(vocab.get(x));
                    }
                }
                System.out.println(vocab.size() + " total vocabulary terms");
            }
            
            //check for special query :stem token to stem the token and display the result
            else if (input.contains(":stem"))
            {
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
   
                stemmer.setCurrent(input.substring(6, input.length()));
                stemmer.stem();
                System.out.println(stemmer.getCurrent());                
            }
            
            //check for special query :index folder/directory resetting the corpus
            else if (input.contains(":index"))
            {
                Path p = Paths.get(input.substring(7, input.length()));
                System.out.println("Starting to index: " + input.substring(7, input.length()));
                corpus = new DirectoryCorpus(p);
                
                DiskIndexWriter k = new DiskIndexWriter();
                k.writeIndex(PositionalInvertedIndexCorpus(corpus), Paths.get("corpus/index/postings.bin"));
                index = new DiskPositionalIndex(Paths.get("corpus/index/postings.bin"));
                System.out.println("Done indexing");
            }
        
            //Ranked retrieval mode
            else 
            {
                int def = 0;
                if(input.contains("1"))
                {
                    System.out.println("Input a search query");
                    input = console.nextLine();
                    mode = 1;
                    System.out.println("Input 1 for default or 2 for vocab elimination");
                    def = console.nextInt();
                    
                }
                else
                {
                    mode = 2;
                }
                float map = 0;
                float meanRTime = 0;
                int nQueries = 0;
                for(String query: queryRel.keySet())
                {   
                    long startTime = System.nanoTime();
                    
                    nQueries++;
                    if(mode == 2)
                    {
                        input = query;
                        
                    }
                //Ranked retrieval
                PriorityQueue K = new PriorityQueue(Collections.reverseOrder());
                HashMap <Integer, Float> Accumalators = new HashMap<>();
                for(String x: rqp.parseQuery(input))
                {
                    
                    List<Posting> pos = index.getPostingsNoPositions(x);
                    if(pos.size() > 0)
                    {
                        float wqt = (float)(Math.log(1 + (double)corpus.getCorpusSize()/(double)pos.size()));
                        if(def == 2)
                        {
                        if(wqt < (float) 1.35)
                        {
                        }
                        else
                        {
                            for(Posting y: pos)
                            {
                                if(Accumalators.containsKey(y.getDocumentId()))
                                {
                                    Accumalators.put(y.getDocumentId(), Accumalators.get(y.getDocumentId()) + (float)(wqt * y.getWdt()));
                                }
                                else
                                {
                                    Accumalators.put(y.getDocumentId(), (float)(wqt * y.getWdt()));
                                }
                            }
                        }
                        }
                        else
                        {
                            for(Posting y: pos)
                            {
                                if(Accumalators.containsKey(y.getDocumentId()))
                                {
                                    Accumalators.put(y.getDocumentId(), Accumalators.get(y.getDocumentId()) + (float)(wqt * y.getWdt()));
                                }
                                else
                                {
                                    Accumalators.put(y.getDocumentId(), (float)(wqt * y.getWdt()));
                                }
                            }
                        }
                    }
                }
                
                
                for(int x: Accumalators.keySet())
                {
                    Accumalators.put(x, Accumalators.get(x) / (float) index.getLd(x, Paths.get("corpus/index/docWeights.bin")));
                    K.offer(Accumalators.get(x));
                }
                
                //Output up to top K = 50 and their final Accumalators
                int relevant = 0;
                float patx = 0;
                int accIndex = 0;
                for(int x = 1; x < 51; x++)
                {
                    float f = (float) K.poll(); 
                    if(queryRel.containsKey(input) == true)
                    {
                        List<Integer> list = queryRel.get(input);
                        for(int z: Accumalators.keySet())
                        {
                            if(Accumalators.get(z) == f)
                            {
                                accIndex = z;
                                break;
                            }
                        }
                        if(list.contains(corpus.getDocument(accIndex).getId() + 1))
                        {
                            relevant++;
                            patx += (float) relevant/x;
                            System.out.println("Relevant: " + (corpus.getDocument(accIndex).getId() + 1) + ".json at index " + x);
                        }
                    }  
                    else
                    {
                        if (mode == 1)
                        {   
                            for(int z: Accumalators.keySet())
                            {
                                if(Accumalators.get(z) == f)
                                {
                                    accIndex = z;
                                    break;
                                }
                            }
                            
                            relevant++;
                            patx += (float) relevant/x;
                            System.out.println("Relevant: " + (corpus.getDocument(accIndex).getId() + 1) + ".json at index " + x);
                        }
                    }
                }
                long endTime = System.nanoTime();
                long duration = (endTime - startTime)/1000000000;
                meanRTime += (float) duration;
                if(relevant > 0)
                {
                    map += (float) patx/relevant;
                    System.out.println("Average Precision: " + (patx/relevant));
                }
                System.out.println();
                if(mode == 1 && nQueries == 30)
                {
                    break;
                }
                }
                System.out.println("Mean Average Precision: " + map/nQueries);
                System.out.println("Mean response time: " + meanRTime);
                System.out.println("Throughput: " + 1/meanRTime);
            }
        }
    }
    
    //Construct Index with given corpus
    private static Index PositionalInvertedIndexCorpus(DocumentCorpus corpus) throws IOException 
    {    
            PositionalInvertedIndex index = new PositionalInvertedIndex();
            TokenProcessor processor = new AdvTokenProcessor();
            
            for(Document d: corpus.getDocuments())
            {
                //wdt
                HashMap <String, Integer> wdt = new HashMap<>();
                TokenStream s = new EnglishTokenStream(d.getContent());
                
                int count = 0;
                for(String token: s.getTokens())
                {
                    List <String> terms = processor.processTokenTwo(token);
                    for(String term: terms)
                    {
                        if(wdt.containsKey(term))
                        {
                            wdt.put(term, wdt.get(term) + 1);
                        }
                        else
                        {
                            wdt.put(term, 1);
                        }
                        index.addTerm(term, d, count);
                    }
                    count++;
                }
                
                //wdt summation
                double sum = 0;
                for(int x : wdt.values())
                {
                    sum = sum + Math.pow(1 + Math.log(x), 2);
                }
                DiskIndexWriter.writeWeight(Math.sqrt(sum), Paths.get("corpus/index/docWeights.bin"));
                s.close();
            }       
	return index;
    } 
}