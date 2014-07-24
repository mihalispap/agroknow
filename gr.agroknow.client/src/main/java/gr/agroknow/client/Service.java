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
	    public Response addHarvest(Harvest harvest ) throws IOException, InterruptedException {
		
		   logger.info("Post.Inside addHarvest");

		
		
		   /*harvest.setId(++currentId);
    	System.out.println("---- Harvester id is: " + currentId);
    	harvests.put(harvest.getId(), harvest);
    	harvest.addToHarvestList(harvest.getId(),harvest);*/
		 	
	        	
		    	//add to list 
		    	
		    	harvest.setId(++currentId);//
		    	System.out.println("---- Harvester id is: " + currentId);
		    	//list.addToHarvestList(harvest.getId(),harvest);
		    	harvests.put(currentId, harvest);
		    	harvest.addToHarvestList(harvest.getId(),harvest);
		    	FileWriter fstream = new FileWriter(harvest.getPrefix()+".txt");//C:\\harvest\\
		    	logger.info("---- File : " + harvest.getPrefix()+".txt created");
    	        BufferedWriter out = new BufferedWriter(fstream);
		    	
				try {
					
					Process	ps = Runtime.getRuntime().exec(new String[]{"java","-jar","harvester.jar",harvest.getUrl(),harvest.getDirectory(),harvest.getPrefix()});
					ps.waitFor();
					System.out.println("process created");
					java.io.InputStream is = ps.getInputStream();
			    	// byte b[] = new byte[is.available()];
			    	 
					 logger.info("----------------harvest pending .....--------------------");
			         for (int i = 0; i < is.available(); i++) {
			        	harvest.setStatus("pending");
			            System.out.println("" + is.read());
			            out.write(is.read());
			            
			         }
			         
			         out.close();
			    	// wait for 10 seconds and then destroy the process
			         Thread.sleep(10000);
			         harvest.setStatus("completed");
			         logger.info("-------------------harvest completed--------------------");
			         ps.destroy();
			    	 
			       //  return  Response.ok().type("application/xml").entity(harvest).build();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					  harvest.setStatus("broken");
					  logger.info("-------------------thete is a broken link---------------");
					//return Response.notModified().build();	
				}
		    	
		    	 
		    //	return  Response.ok().type("application/xml").entity(harvest).build();
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

	       // h = h.getHarvest(idNumber);//list.getHarvest(idNumber);
	        	
	        
	        return h;
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
