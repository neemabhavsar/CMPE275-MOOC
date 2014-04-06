package poke.server.storage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;


import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class MongoDBOperations {

	private DB db; // database
	private DBCollection collection; // collection
	private MongoClient mongoClient; // MongoDB client
	private String Host_Name; // Host Name
	int Port_Number;
	String dbName;
	String dbUserName;
	String dbPassword;
	 
	public MongoDBOperations() throws FileNotFoundException, IOException {
		
		this.Host_Name = "localhost";
		this.Port_Number = 27017;
		this.dbName = "mooc";
		this.dbUserName = "";
		this.dbPassword = "";
	}

	// get MongoDB Connection
	public MongoClient getDBConnection() throws UnknownHostException {
		mongoClient = new MongoClient(Host_Name, Port_Number);
		return mongoClient;
	}

	// get MongoDB Database instance
	public DB getDB(String dbName) {
		db = mongoClient.getDB(dbName);
		return db;
	}

	// get MongoDB Collection
	public DBCollection getCollection(String input_collection,DB db) {
		collection = db.getCollection(input_collection);
		return collection;
	}

	// insert data into the collection
	public void insertData(BasicDBObject doc,DBCollection collection) {
		collection.insert(doc);
	}

	// Search the collection
	public DBCursor findData(BasicDBObject query,DBCollection collection) {
		return collection.find(query);
	}

	// Close database connection
	public void closeConnection() {
		mongoClient.close();
	}

	public String getHost_Name() {
		return Host_Name;
	}

	public void setHost_Name(String Host_Name) {
		this.Host_Name = Host_Name;
	}

	public int getPort_Number() {
		return Port_Number;
	}

	public void setPort_Number(int Port_Number) {
		this.Port_Number = Port_Number;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDbUserName() {
		return dbUserName;
	}

	public void setDbUserName(String dbUserName) {
		this.dbUserName = dbUserName;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

}