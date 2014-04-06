package poke.resources;

import java.io.IOException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import poke.server.storage.MongoDBOperations;

public class UserResource {
	MongoDBOperations dbOperations;
	private DB db; // database
	private DBCollection collection; // collection
	private MongoClient mongoClient; // MongoDB client

	public UserResource() {
		// TODO Auto-generated constructor stub
		try{
			dbOperations = new MongoDBOperations();
			mongoClient = dbOperations.getDBConnection();
			db = mongoClient.getDB(dbOperations.getDbName());
			collection = dbOperations.getCollection("User",db);
		}catch(IOException e){
			e.printStackTrace();
		}		
	}
	
	public boolean signup(String email,String password, String fname,String lname){
		try{
			BasicDBObject doc = new BasicDBObject("email",email).
										append("password", password).
										append("fname", fname).
										append("lname",lname);
			dbOperations.insertData(doc,collection);
		
		}catch(Exception e){
			return false;
		}
		return true;
	}
	
	

	
	
}
