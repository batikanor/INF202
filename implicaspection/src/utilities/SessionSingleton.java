package utilities;

// ALTERNATIVE SINGLETON IMPLEMENTATIONS : STATIC HOLDER PATTERN / ENUM
public final class SessionSingleton {
	// Singleton pattern: class can only have 1 instance
	// Volatile. becuase I don't want the JVM to re-order the instructions that happen as a part of "onlyInstance = new SessionSingleton(username, adminID);'
	private static volatile SessionSingleton onlyInstance = null;
    
	private String username;
    private int adminID = -1; //-1 means not admin
    
    private SessionSingleton(String username, int adminID){
    	this.username = username;
    	this.adminID = adminID;
    }
    
    public static SessionSingleton getInstance(String username, int adminID){
    	// Double check locking
    	if(onlyInstance == null) { // Check
        	synchronized (SessionSingleton.class) { // Lock
            	if(onlyInstance == null){ // Check
            		onlyInstance = new SessionSingleton(username, adminID);
            		if(adminID != -1) {
            			System.out.println("Admin olarak giris yapildi");
            		}else {
            			System.out.println("Personnel olarak giris yapildi");
            		}
            	}else {
            		System.out.println("Halihazirda giris yapilmis");
            	}
    		}

    	}

    	return onlyInstance;
    }
    
    public String getUsername() {
        return username;
    }
    public int getAdminID(){
    	return adminID;
    }

		
    public void cleanSession() {
        username = "";// or null
        adminID = -1;// or null
    }

    public static void resetInstance() {
    	onlyInstance = null;
    }
    
    public static SessionSingleton getSession() {
    	return onlyInstance;
    }

    @Override
    public String toString() {
        return "Kullanıcı bilgileriniz:        KULLANICI ADI:"+ this.username + "    ADMIN NO:" + this.adminID;
    }
}