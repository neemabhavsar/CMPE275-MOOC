/*
 * copyright 2012, gash
 * 
 * Gash licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package poke.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import poke.server.resources.Resource;
import poke.server.resources.ResourceUtil;
import eye.Comm.JobOperation;
import eye.Comm.JobOperation.JobAction;
import eye.Comm.NameValueSet;
import eye.Comm.JobDesc.JobCode;
import eye.Comm.NameValueSet.NodeType;
import eye.Comm.JobDesc;
import eye.Comm.Payload;
import eye.Comm.PokeStatus;
import eye.Comm.Request;
import eye.Comm.SignUp;

public class JobResource implements Resource {
	
	protected static Logger logger = LoggerFactory.getLogger("server");
	private Request.Builder rb;
	

	@Override
	public Request process(Request request) {

		// TODO add code to process the message/event received
		logger.info("Outside Request for ListCourses ::" + request.getBody().getJobOp().getData().getNameSpace()
				+ " :: " +  request.getBody().getJobOp().getData().getJobId());
		Request reply = null;
		if(request.getBody().getJobOp().getData().getNameSpace().equals("listcourses")){
			logger.info("Request for ListCourses ::" + request.getBody().getJobOp().getData().getOptions().getName()
					+ " :: " +  request.getBody().getJobOp().getData().getOptions().getValue());
			
			//DB Call to Fetch
			CourseResource courseResource = new CourseResource();
			reply = courseResource.courseList(request);
			
			
			
			/*
			SignUp.Builder sb = SignUp.newBuilder();
			sb.setEmail(request.getBody().getSignUp().getEmail());
			sb.setPassword(request.getBody().getSignUp().getPassword());
			sb.setFname(request.getBody().getSignUp().getFname());
			sb.setLname(request.getBody().getSignUp().getLname());
			pb.setSignUp(sb.build());
			*/
			
			// payload
			/*
			Payload.Builder pb = Payload.newBuilder();
			
			NameValueSet.Builder nb1 = NameValueSet.newBuilder();
			nb1.setName("coursename");
			nb1.setValue("cmpe277");
			nb1.setNodeType(NodeType.VALUE);
			
			
			NameValueSet.Builder nb2 = NameValueSet.newBuilder();
			nb2.setName("coursename");
			nb2.setValue("cmpe275");
			nb2.setNodeType(NodeType.VALUE);
			
			
			NameValueSet.Builder main = NameValueSet.newBuilder();
			main.setNodeType(NodeType.NODE);
			main.addNode(0, nb1.build());
			main.addNode(1, nb2.build());

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
*/				
		}
		else if(request.getBody().getJobOp().getData().getNameSpace().equals("coursedescription")){
			
			logger.info("coursedescription: " + request.getBody().getJobOp().getData().getOptions().getName()
					+ " " + request.getBody().getJobOp().getData().getOptions().getValue());
			/*String desc = "";
			if (request.getBody().getJobOp().getData().getOptions().getValue().equals("CMPE277"))
			{
				desc = "Architectures, technologies, and programming concepts for developing smartphone applications. " +
						"Covers current smartphone/tablet OSs, application development, and deployment environments. " +
						"Prerequisites: Classified graduate standing or instructor consent";
			}
			

			Payload.Builder pb = Payload.newBuilder();
			
			NameValueSet.Builder nb1 = NameValueSet.newBuilder();
			nb1.setName("coursedescription");
			nb1.setValue(desc);
			nb1.setNodeType(NodeType.VALUE);
			
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
					PokeStatus.SUCCESS, "Fetch desc Of Courses"));

			rb.setBody(pb.build());

			*/
			CourseResource courseResource = new CourseResource();
			reply = courseResource.courseDescription(request.getBody().getJobOp().getData().getOptions().getValue(), request);
		}else if(request.getBody().getJobOp().getData().getNameSpace().equals("signup")){
   		 	logger.info("signup: " + request.getBody().getJobOp().getData().getOptions().getName()
                	+ " " + request.getBody().getJobOp().getData().getOptions().getValue());
        String desc = "";

        
        String fname ="" , lname = "", email = "", password = "";
        if (request.getBody().getJobOp().getData().getOptions().getNode(0).getName().equals("fname"))
        	fname =  request.getBody().getJobOp().getData().getOptions().getNode(0).getValue();
        if (request.getBody().getJobOp().getData().getOptions().getNode(1).getName().equals("lname"))
        	lname = request.getBody().getJobOp().getData().getOptions().getNode(1).getValue();
        if (request.getBody().getJobOp().getData().getOptions().getNode(2).getName().equals("email"))
            email = request.getBody().getJobOp().getData().getOptions().getNode(2).getValue();
        if (request.getBody().getJobOp().getData().getOptions().getNode(3).getName().equals("password"))
        	password = request.getBody().getJobOp().getData().getOptions().getNode(3).getValue();
   	 
    	logger.info("User Details: "+fname + "  " + lname + " " + email + " " + password);
    	
    	UserResource userResource = new UserResource();
    	boolean result = userResource.signup(email, password, fname, lname);
   	 
    	Payload.Builder pb = Payload.newBuilder();
       	 
        	NameValueSet.Builder nb1 = NameValueSet.newBuilder();
        	nb1.setName("signup");
        	if(result){
        		nb1.setValue("signup successfull");
        	}else{
        		nb1.setValue("signup unsuccessfull");
        	}
        	nb1.setNodeType(NodeType.VALUE);
       	 
        	JobDesc.Builder jb = JobDesc.newBuilder();
        	jb.setNameSpace("signup");
        	jb.setOwnerId(request.getBody().getJobOp().getData().getOwnerId());
        	jb.setJobId("signup");
        	jb.setStatus(JobCode.JOBRECEIVED);
        	jb.setOptions(nb1.build());
       	 
        	JobOperation.Builder jo = JobOperation.newBuilder();
        	jo.setData(jb.build());
        	jo.setAction(JobAction.REMOVEJOB);
       	 
        	pb.setJobOp(jo.build());
        	rb = Request.newBuilder();

        	// metadata
        	rb.setHeader(ResourceUtil.buildHeaderFrom(request.getHeader(),
                	PokeStatus.SUCCESS, "signup done"));

        	rb.setBody(pb.build());    	 
    }
		else if(request.getBody().getJobOp().getData().getNameSpace().equals("signin")){
   		 	logger.info("signin: " + request.getBody().getJobOp().getData().getOptions().getName()
                	+ " " + request.getBody().getJobOp().getData().getOptions().getValue());
                
        String email = "", pwd = "";
        if (request.getBody().getJobOp().getData().getOptions().getNode(0).getName().equals("email"))
            email = request.getBody().getJobOp().getData().getOptions().getNode(0).getValue();
        if (request.getBody().getJobOp().getData().getOptions().getNode(1).getName().equals("password"))
            pwd = request.getBody().getJobOp().getData().getOptions().getNode(1).getValue();
   	 
    	logger.info(email + " " + pwd);

   	 
    	Payload.Builder pb = Payload.newBuilder();
       	 
        	NameValueSet.Builder nb1 = NameValueSet.newBuilder();
        	nb1.setName("signin");
        	nb1.setValue("signin successfull");
        	nb1.setNodeType(NodeType.VALUE);
       	 
        	JobDesc.Builder jb = JobDesc.newBuilder();
        	jb.setNameSpace("signin");
        	jb.setOwnerId(request.getBody().getJobOp().getData().getOwnerId());
        	jb.setJobId("signin");
        	jb.setStatus(JobCode.JOBRECEIVED);
        	jb.setOptions(nb1.build());
       	 
        	JobOperation.Builder jo = JobOperation.newBuilder();
        	jo.setData(jb.build());
        	jo.setAction(JobAction.REMOVEJOB);
       	 
        	pb.setJobOp(jo.build());
        	rb = Request.newBuilder();

        	// metadata
        	rb.setHeader(ResourceUtil.buildHeaderFrom(request.getHeader(),
                	PokeStatus.SUCCESS, "signin done"));

        	rb.setBody(pb.build());    	 
    }	

		//reply = rb.build();
		return reply;
	}

}

