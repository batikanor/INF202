package utilities;

public final class SessionSingleton {
	// Singleton pattern: class can only have 1 instance
	private static SessionSingleton onlyInstance = null;
    
	private String username;
    private int adminID = -1; //-1 means not admin
    
    private SessionSingleton(String username, int adminID){
    	this.username = username;
    	this.adminID = adminID;
    }
    
    public static SessionSingleton getInstance(String username, int adminID){
    	if(onlyInstance == null){
    		onlyInstance = new SessionSingleton(username, adminID);
    		if(adminID != -1) {
    			System.out.println("Admin olarak giris yapildi");
    		}else {
    			System.out.println("Personnel olarak giris yapildi");
    		}
    	}else {
    		System.out.println("Halihazirda giris yapilmis");
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
        return "Session{" +
                "username='" + username + '\'' +
                ", adminID=" + adminID +
                '}';
    }

}
