package gr.agroknow.client;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

//import java.io.Serializable;


@XmlRootElement(name = "harvest")
public class Harvest {
	
	private String url;
	private String directory;
	private String prefix;
	private long id;
	private String status;
	
	
 
	
	
	public void setUrl(String url){
		this.url = url;
		
		
	}
	
	public String getUrl(){
		return url;
		
	}
	
	public void setDirectory(String directory){
		this.directory = directory;
		
		
	}
	
	public String getDirectory(){
		return directory;
		
	}
	
	public void setPrefix(String prefix){
		this.prefix = prefix;
		
		
	}
	
	public String getPrefix(){
		return prefix;
		
	}
	
	
	public void setId(long id){
		this.id = id;
		
		
	}
	
	public long getId(){
		return id;
		
	}
	
	public void setStatus(String status){
		this.status = status;
		
		
	}
	
	public String getStatus(){
		
		return status;
		
	}
	
	
	  static Map<Long, Harvest> harvests = new HashMap<Long, Harvest>();
		public void addToHarvestList(Long id,Harvest harvest){
			if(!harvests.containsKey(id));
				harvests.put(id,harvest);
			
		}
		
		public static List<Harvest> getListHarvests(){
			
			return (List<Harvest>) harvests;
			
		} 
		
		public Harvest getHarvest(long id){
			
			Harvest h = harvests.get(id);
			
			return h;
		} 
		
		
		
		public int startHarvest(Harvest harvest){
			
			
	    	
	    	
			try {
				
				//FileWriter fstream = new FileWriter(harvest.getPrefix()+".txt");//C:\\harvest\\
		//    	logger.info("---- File : " + harvest.getPrefix()+".txt created");
		       // BufferedWriter out = new BufferedWriter(fstream);
		        
				Process	ps = Runtime.getRuntime().exec(new String[]{"java "," -jar","harvester.jar ",harvest.getUrl()+" ",harvest.getDirectory()+" ",harvest.getPrefix()});
				
					System.out.println("process created");
					try {
						ps.waitFor();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}				
					
				
				java.io.InputStream is = ps.getInputStream();
		    	// byte b[] = new byte[is.available()];
				harvest.setStatus("pending"); 
				// logger.info("----------------harvest pending .....--------------------");
		         for (int i = 0; i < is.available(); i++) {		        	
		            System.out.println("" + is.read());
		           // out.write(is.read());		            
		         }
		         
		        // out.close();
		    	// wait for 10 seconds and then destroy the process
		         try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		         harvest.setStatus("completed");
		         //logger.info("-------------------harvest completed--------------------");
		         ps.destroy();
		         
		         //zipping the data folder to {id}.zip
		         String fileName = Long.toString(harvest.getId());
		         ZipUtils appZip = new  ZipUtils(fileName, harvest.getDirectory());//ZipUtils();
	             appZip.generateFileList(new File(appZip.getSrcFolder()));
	             appZip.zipIt(appZip.getOutputFile());
		         
		         
		         
		    	 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				  harvest.setStatus("broken");
				  //logger.info("-------------------thete is a broken link---------------");
			}
			
			
			
			
			return 0;
		}
		
		
	
	
}
