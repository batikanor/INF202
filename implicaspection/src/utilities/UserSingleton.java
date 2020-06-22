package utilities;

import implicaspection.Model;

// ALTERNATIVE SINGLETON IMPLEMENTATIONS : STATIC HOLDER PATTERN / ENUM
public final class UserSingleton {
	// Singleton pattern: class can only have 1 instance
	// Volatile. becuase I don't want the JVM to re-order the instructions that happen as a part of "onlyInstance = new SessionSingleton(username, adminID);'
	private static volatile UserSingleton onlyInstance = null;
    
	private String username;
    private int adminID = -1; //-1 means not admin
    //sertifikatdatum speichern
    
    /*
 
 	___________            __   .__              .__     
\__    ___/_ _________|  | _|__| ______ ____ |  |__  
  |    | |  |  \_  __ \  |/ /  |/  ___// ___\|  |  \ 
  |    | |  |  /|  | \/    <|  |\___ \\  \___|   Y  \
  |____| |____/ |__|  |__|_ \__/____  >\___  >___|  /
                           \/       \/     \/     \/ 
 ________                 __                .__            
\______ \   ____  __ ___/  |_  ______ ____ |  |__   ____  
 |    |  \_/ __ \|  |  \   __\/  ___// ___\|  |  \_/ __ \ 
 |    `   \  ___/|  |  /|  |  \___ \\  \___|   Y  \  ___/ 
/_______  /\___  >____/ |__| /____  >\___  >___|  /\___  >
        \/     \/                 \/     \/     \/     \/

 ____ ___      .__                          .__  __                 __   
|    |   \____ |__|__  __ ___________  _____|__|/  |______    _____/  |_ 
|    |   /    \|  \  \/ // __ \_  __ \/  ___/  \   __\__  \ _/ __ \   __\
|    |  /   |  \  |\   /\  ___/|  | \/\___ \|  ||  |  / __ \\  ___/|  |  
|______/|___|  /__| \_/  \___  >__|  /____  >__||__| (____  /\___  >__|  
             \/              \/           \/              \/     \/    

     */
    
    
    private UserSingleton(String username, int adminID){
    	this.username = username;
    	this.adminID = adminID;
    }
    
    public static UserSingleton getInstance(String username, int adminID){
    	// Double check locking
    	if(onlyInstance == null) { // Check
        	synchronized (UserSingleton.class) { // Lock
            	if(onlyInstance == null){ // Check
            		onlyInstance = new UserSingleton(username, adminID);
            		if(adminID != -1) {
            			Model.log.finest("Admin olarak giris yapildi");
  
            		}else {
            			Model.log.finest("Personnel olarak giris yapildi");
            		}
            	}else {
            		Model.log.warning("Halihazirda giris yapilmis");
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

	/*
    public void cleanSession() {
        username = "";// or null
        adminID = -1;// or null
    }
    */

    public static void resetInstance() {
    	onlyInstance = null;
    }
    
    public static UserSingleton getSession() {
    	return onlyInstance;
    }

    @Override
    public String toString() {
        return "Kullanıcı bilgileriniz:        KULLANICI ADI:"+ this.username + "    ADMIN NO:" + this.adminID;
    }
}