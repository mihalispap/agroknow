package gr.agroknow.client;



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
	
	
}
