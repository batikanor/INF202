package utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import implicaspection.Model;

public class DatabaseAndSession {
	private static final String Driver = "org.hsqldb.jdbcDriver";
    private static final String user = "bati-202";
    private static final String pwd = "zoe833uwU";
    private static final String url = "jdbc:hsqldb:file:/home/inductiomori/Desktop/inf202/bati-hsqldb/fuer202/";
    
   
    
	public static Connection connect() {
		try {
			System.out.println("Veritabanı'na bağlanmaya çalışılınıyor");
			Class.forName(Driver);
			Connection con = DriverManager.getConnection(url, user, pwd);
			return con;
			
		}catch(ClassNotFoundException e) {
			System.out.println("E002: Veritabanı klası bulunamadı");
			e.printStackTrace();
			return null;
		}catch(SQLException e) {
			System.out.println("E002: Veritabanı klası bulundu, ama bağlantı kurulamadı");
			return null;
		}
			
	}
	// Not necessary, only there for testing
	public static void printAllPersonnel() {
		Connection con = connect();

		try {
			Statement stmt = con.createStatement();
			ResultSet rs;
			rs = stmt.executeQuery("SELECT * FROM PERSONNEL");
			while(rs.next()) {
				System.out.println(rs.getString("PERSONNELID") + " | " + rs.getString("USERNAME") + " | " + rs.getString("PASSWORDHASH"));
			}
		} catch (SQLException e) {
			System.out.println("E002: Veritabanı'na gönderilen komut hatalı");
			e.printStackTrace();
		}
		
	}
	
	// To logout, do SessionSingleton.resetInstance()
	public static void logout() {
		SessionSingleton.resetInstance();
	}
	public static boolean login(String username, String password) {
		// Hash password
		byte[] hash = Model.hashPassword(password);
		Connection con = connect();
		PreparedStatement ps;
		ResultSet rs2;
		//int rowCount = 0; // Not needed because it is already ensured there will only be two rows
		try {
			ps = con.prepareStatement("SELECT PERSONNELID FROM PERSONNEL WHERE USERNAME=? AND PASSWORDHASH=?;");
			ps.setString(1, username);
			ps.setBytes(2, hash);
			rs2 = ps.executeQuery();
			if(rs2.next()) {
				// If at least one row came (if a personnel or admin account was recognized)
				logout();
				int personnelID = Integer.parseInt(rs2.getString("PERSONNELID"));
				
			
				// Check if admin; if so, create an admin session
				ps = con.prepareStatement("SELECT ADMINID FROM ADMIN WHERE PERSONNELID=?;");
				ps.setInt(1, personnelID);
				rs2 = ps.executeQuery();
				
				if(rs2.next()) {
					// If it doesnt enter this block, it means that the result set only had one block, thus user isnt admin
					int adminID = Integer.parseInt(rs2.getString("ADMINID"));
					SessionSingleton.getInstance(username, adminID);
			
				}else {
					// User isn't admin
					SessionSingleton.getInstance(username, -1);
					
				}
				return true;
				
			}else {
				System.out.println("Giris yapilamadi, bilgileri kontrol edin");
				return false;
			}
			
		

			
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			System.out.println("SQL Hatasi sebebiyle giris yapilamadi");
		}
		
		return false;
	}

	public static boolean register(String username, String password) {
		// Hash password
		byte[] hash = Model.hashPassword(password);
		Connection con = connect();
		
		// Check if user exists, because it is faster if the code doesn't require a try-catch block 
		
		// Create user
		PreparedStatement ps;
		try {
			ps = con.prepareStatement("INSERT INTO PERSONNEL(USERNAME, PASSWORDHASH) VALUES(?, ?);");
			ps.setString(1, username);
			ps.setBytes(2, hash);
			
			int rs2 = ps.executeUpdate();
			con.commit();
			con.close();
			System.out.println("rowsUpdated: " + rs2);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return false;
	}
	
	
}

//PreparedStatement ps = con.prepareStatement("INSERT INTO ADMINS(USER, PASSHASH) VALUES(?, ?);");
/*
	try {
			System.out.println("Veritabanı'na bağlanmaya çalışılınıyor");
			Class.forName(Driver);
			String url = "jdbc:hsqldb:file:/home/inductiomori/Desktop/inf202/bati-hsqldb/fuer202/";
			Connection con = DriverManager.getConnection(url, user, pwd);
			Statement stmt = con.createStatement();
			ResultSet rs;
			rs = stmt.executeQuery("SELECT * FROM ADMINS;");
			while(rs.next()) {
				System.out.println(rs.getString("INT") + " | " + rs.getString("USER") + " | " + rs.getString("PASSHASH"));
			}
		}catch(ClassNotFoundException e) {
			System.out.println("E002: Veritabanı klası bulunamadı");
			e.printStackTrace();
		}catch(SQLException e) {
			System.out.println("E002: İsteğiniz veritabanınca gerçekleştirilemedi");
		}
			


*/
