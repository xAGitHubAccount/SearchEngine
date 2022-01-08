/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cecs429.documents;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 *
 * @author iN0va
 */
public class JsonFileDocument implements FileDocument {
	private int mDocumentId;
	private Path mFilePath;
        private String title;
	
	/**
	 * Constructs a TextFileDocument with the given document ID representing the file at the given
	 * absolute file path.
     * @param id
	 */
	public JsonFileDocument(int id, Path absoluteFilePath) {
		mDocumentId = id;
		mFilePath = absoluteFilePath;
	}
	
	@Override
	public Path getFilePath() {
		return mFilePath;
	}
	
	@Override
	public int getId() {
		return mDocumentId;
	}
	
	@Override
	public Reader getContent() {
		try {
                    JsonReader jsonReader = new JsonReader(Files.newBufferedReader(mFilePath));
                    JsonElement jsonElement = new JsonParser().parse(jsonReader);
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    
                    Reader stringReader = new StringReader(jsonObject.get("body").getAsString());
                    return stringReader;
                    
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
        
	@Override
	public String getTitle() {
                    JsonReader jsonReader;
            try {
                jsonReader = new JsonReader(Files.newBufferedReader(mFilePath));
                JsonElement jsonElement = new JsonParser().parse(jsonReader);
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                title = jsonObject.get("title").getAsString();
		return title;
                
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
	}
	
	public static FileDocument loadJsonFileDocument(Path absolutePath, int documentId) {
		return new JsonFileDocument(documentId, absolutePath);
	}
}
