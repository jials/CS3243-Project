
public class Game extends State implements Cloneable {
	
	public Game() {
		super();
	}
	
	public Object clone(){  
	    try{  
	        return super.clone();  
	    }catch(Exception e){ 
	        return null; 
	    }
	}
	
}