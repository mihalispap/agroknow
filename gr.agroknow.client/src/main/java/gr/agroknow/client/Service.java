package gr.agroknow.client;




import javax.ws.rs.Path;

import java.io.BufferedWriter;
import java.io.Console;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;






@Path("/service/")
@Produces("text/xml")
public class Service {
	
	
	private static final transient Logger logger = LoggerFactory.getLogger(Service.class.getName());

	
	
	 static long currentId = 123;
	// HarvestList list;
	static Map<Long, Harvest> harvests = new HashMap<Long, Harvest>();
	// private List<Harvest> harvests = new ArrayList<Harvest>();
	 
	
	 public Service(){
			logger.info("initialize:before Service init() method ");

		 init();
	 }
	 

	@POST
	    @Path("/harvest/")
	    @Produces("text/xml")
	    @Consumes(value={"text/xml"})
	    public Response addHarvest(Harvest harvest ) {
		
		   logger.info("Post.Inside addHarvest");

		    	//add to list 
		    	
		    	harvest.setId(++currentId);//
		    	System.out.println("---- Harvester id is: " + currentId);
		    	//list.addToHarvestList(harvest.getId(),harvest);
		    	harvests.put(currentId, harvest);
		    	harvest.addToHarvestList(harvest.getId(),harvest);
		    	harvest.startHarvest(harvest);
		    	
		    	 
	        // return Response.notModified().build();	
		    	
	    
		    return  Response.ok().type("application/xml").entity(harvest).build();
	       
	    }
	 	
	 
	 
	   @GET
	    @Path("/harvest/{id}/")
	    @Produces(value={"text/xml"})
	    @Consumes(value={"text/xml"})
	    public Harvest getHarvest(@PathParam("id") String id) {
		   logger.info("inside getHarvest,harvest with id: "+id);

	        long idNumber = Long.parseLong(id);	        
	        
	        Harvest h = harvests.get(idNumber);
	    	System.out.println("---- Harveste with id : " + id);
	    	
	    	if(h == null){
	    		Response.status(Response.Status.NOT_FOUND).entity("Service Not Found").build();
	    		
	    	}
	       // h = h.getHarvest(idNumber);//list.getHarvest(idNumber);
	        	
	        
	        return h;
	    }
	   
	   
	   @GET
	    @Path("/harvest/{id}.zip/")	    
	    @Produces("application/zip")
	    public Response getZip(@PathParam("id") String id) {
		   logger.info("inside getZip Method,harvest with id: "+id);
		   System.out.println("Start");
	
		   
	        long idNumber = Long.parseLong(id);	        
	        
	        Harvest h = harvests.get(idNumber);
	    	System.out.println("---- Harveste with id : " + id);
	    	
	    	if(h == null){
	    		Response.status(Response.Status.NOT_FOUND).entity("File Not Found").build();
	    		
	    	}
	       // h = h.getHarvest(idNumber);//list.getHarvest(idNumber);
	        	
	        
	    	 return  Response.ok().type("application/zip").entity(h).build();
	    	
	    }
	   	
	   
	    final void init(){
		   logger.info("inside Service init() method ");

	       long idNumber = Long.parseLong("102");	
		   Harvest h = new Harvest();
		   h.setId(idNumber);
		   //h.addToHarvestList(idNumber, h);
		   harvests.put(idNumber, h);
		   
	   }
	   
	   
	
	
}
