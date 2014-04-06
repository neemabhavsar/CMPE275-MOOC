package poke.resources;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;


import eye.Comm.JobDesc;
import eye.Comm.JobDesc.JobCode;
import eye.Comm.JobOperation;
import eye.Comm.JobOperation.JobAction;
import eye.Comm.NameValueSet;
import eye.Comm.NameValueSet.NodeType;
import eye.Comm.Payload;
import eye.Comm.PokeStatus;
import eye.Comm.Request;
import poke.server.resources.ResourceUtil;
import poke.server.storage.MongoDBOperations;

public class CourseResource {
	MongoDBOperations dbOperations;
	private DB db; // database
	private DBCollection collection; // collection
	private MongoClient mongoClient; // MongoDB client
	private Request.Builder rb;
	
	protected static Logger logger = LoggerFactory.getLogger("server");
	public CourseResource() {
		// TODO Auto-generated constructor stub
		try{
			dbOperations = new MongoDBOperations();
			mongoClient = dbOperations.getDBConnection();
			db = mongoClient.getDB(dbOperations.getDbName());
			logger.info("DB is:: "+db);
			collection = dbOperations.getCollection("Course",db);
		}catch(IOException e){
			e.printStackTrace();
		}		
	}
	
	public Request courseList(Request request){
		
		NameValueSet.Builder nb1 = NameValueSet.newBuilder();
		NameValueSet.Builder main = NameValueSet.newBuilder();
		main.setNodeType(NodeType.NODE);
		Payload.Builder pb = Payload.newBuilder();
		int i=0;
		
		BasicDBObject doc = new BasicDBObject();									
		
		DBCursor cursor = dbOperations.findData(doc, collection);
		
		while (cursor.hasNext()) {

			nb1.setName("coursename");
			nb1.setValue((String) cursor.next().get("course_name"));
			nb1.setNodeType(NodeType.VALUE);
			main.addNode(i++, nb1.build());
		}
		cursor.close();
		
		JobDesc.Builder jb = JobDesc.newBuilder();
		jb.setNameSpace("listcourses");
		jb.setOwnerId(request.getBody().getJobOp().getData().getOwnerId());
		jb.setJobId("listcourses");
		jb.setStatus(JobCode.JOBRECEIVED);
		jb.setOptions(main.build());
		
		JobOperation.Builder jo = JobOperation.newBuilder();
		jo.setData(jb.build());
		jo.setAction(JobAction.REMOVEJOB);
		
		pb.setJobOp(jo.build());
		rb = Request.newBuilder();

		// metadata
		rb.setHeader(ResourceUtil.buildHeaderFrom(request.getHeader(),
				PokeStatus.SUCCESS, "Fetch List Of Courses"));

		rb.setBody(pb.build());

		
		Request reply = rb.build();
		return reply;
	}

public Request courseDescription(String course_name,Request request){
		
		NameValueSet.Builder nb1 = NameValueSet.newBuilder();
		//.Builder main = NameValueSet.newBuilder();
		//main.setNodeType(NodeType.NODE);
		Payload.Builder pb = Payload.newBuilder();
		int i=0;
		
		logger.info("Course Name Received: "+course_name);
		BasicDBObject doc = new BasicDBObject("course_name",course_name);									
		
		DBCursor cursor = dbOperations.findData(doc, collection);
		
		while (cursor.hasNext()) {

			nb1.setName("coursedescription");
			//logger.info("Cursor Value is: "+cursor.next());
			//logger.info("Cursor Desc is: "+cursor.next().get("course_desc"));
			nb1.setValue((String) cursor.next().get("course_desc"));
			nb1.setNodeType(NodeType.VALUE);
			//main.addNode(i++, nb1.build());
		}
		cursor.close();
		
		JobDesc.Builder jb = JobDesc.newBuilder();
		jb.setNameSpace("coursedescription");
		jb.setOwnerId(request.getBody().getJobOp().getData().getOwnerId());
		jb.setJobId("coursedescription");
		jb.setStatus(JobCode.JOBRECEIVED);
		jb.setOptions(nb1.build());
		
		JobOperation.Builder jo = JobOperation.newBuilder();
		jo.setData(jb.build());
		jo.setAction(JobAction.REMOVEJOB);
		
		pb.setJobOp(jo.build());
		rb = Request.newBuilder();

		// metadata
		rb.setHeader(ResourceUtil.buildHeaderFrom(request.getHeader(),
				PokeStatus.SUCCESS, "Fetch List Of Courses"));

		rb.setBody(pb.build());

		
		Request reply = rb.build();
		return reply;
	}

	
}
