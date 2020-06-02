package utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.Date;
import implicaspection.Model;
import implicaspection.ReportController;
import javafx.scene.control.ComboBox;

public class DatabaseAndSession {
	private static final String Driver = "org.hsqldb.jdbcDriver";
    private static final String user = "bati-202";
    private static final String pwd = "zoe833uwU";
    private static final String dbPath = "/home/inductiomori/Desktop/inf202/bati-hsqldb/fuer202/";
    //private static final String dbPath = "~/dbImplicaspection";
    private static final String url = "jdbc:hsqldb:file:" + dbPath;
    // Should I use relative path or define system variables ? see : 
    // http://hsqldb.org/doc/2.0/guide/dbproperties-chapt.html#dpc_variables_url
    // https://stackoverflow.com/questions/61484323/how-to-deploy-javafx-14-jdk11-hsqldb-java-desktop-application

    
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
	public static ComboBox<String> returnDependantValues(String dependantName) {
		Connection con = connect();
		try {
			
			// Get the name of the field that the dependant depends on (i.e. decisive field)
			String decisiveName;
			PreparedStatement ps = con.prepareStatement("SELECT TOP 1 DECISIVENAME FROM FIELDDEPEND WHERE DEPENDANTNAME = ?");
			ResultSet rs;
			ps.setString(1, dependantName);
			rs = ps.executeQuery();
			if (rs.next()) {
				decisiveName = rs.getString("DECISIVENAME");
				
				ArrayList<String> bufflist = ReportController.dependenceMap.get(decisiveName);
		
				if (bufflist == null) {
					bufflist = new ArrayList<String>();
					System.out.println("bufflist was null");
				} 
				if (!bufflist.contains(dependantName)) {
					bufflist.add(dependantName);
					ReportController.dependenceMap.put(decisiveName, bufflist);
				}

				// Get field content (from the map that updates every time an input changes)
				String decisiveContent = ReportController.contentsMap.get(decisiveName);
				if (decisiveContent == null) {
					System.out.println("Bu alan için mümkün bir değer bulunmamaktadır. Lütfen önce seçenek ekleyiniz veya " + decisiveName + "alanindaki seçiminizi değiştiriniz.");
				}
				
				PreparedStatement ps2 = con.prepareStatement("SELECT DEPENDANTCONTENT FROM FIELDDEPEND WHERE DECISIVENAME = ? AND DEPENDANTNAME = ? AND DECISIVECONTENT = ?");
				ResultSet rs2;
				ComboBox<String> buff = new ComboBox<String>();
				ps2.setString(1, decisiveName);
				ps2.setString(2, dependantName);
				ps2.setString(3, decisiveContent);
				rs2 = ps2.executeQuery();
				while (rs2.next()) {
					String content = rs2.getString("DEPENDANTCONTENT");
					buff.getItems().add(content);
				}
				return buff;
			} else {
				System.out.println(dependantName + " in bagli oldugu bir hucre yok, ama bagimli hucre olarak gosterilmis");
				// e.g. return null and control it on receiving side

				return null;
			}
			
			

		}catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Veritabanı hatası sebebiyle işlem gerçekleştirilemedi");
			return null;
			}
	}
	public static ComboBox<String> returnComboBoxValues(String fieldName) {
		Connection con = connect();
		try {
			PreparedStatement ps = con.prepareStatement("SELECT FIELDCONTENT FROM FIELDMULTI WHERE FIELDNAME=?");
			ResultSet rs;
			ComboBox<String> buff = new ComboBox<String>();
			ps.setString(1, fieldName);
			rs = ps.executeQuery();
			System.out.println("-----------------------------------" + fieldName + fieldName.length());
			while (rs.next()) {
				String content = rs.getString("FIELDCONTENT");
				buff.getItems().add(content);
			
			}
			return buff;
		}catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Veritabanı hatası sebebiyle işlem gerçekleştirilemedi");
			return null;
			}
	}
	public static ResultSet returnAllPersonnel() {
		Connection con = connect();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs;
			rs = stmt.executeQuery("SELECT * FROM PERSONNEL");
			/*
			while(rs.next()) {
				System.out.println(rs.getString("PERSONNELID") + " | " + rs.getString("USERNAME") + " | " + rs.getString("PASSWORDHASH"));
			}
			*/
			return rs;
		} catch (SQLException e) {
			System.out.println("E000: Veritabanı'na gönderilen sabit komut çalışmadı");
			e.printStackTrace();
		}
			return null;
		
	}
	public static ResultSet returnPersonnel(String username) {
		Connection con = connect();
		try {
			PreparedStatement ps = con.prepareStatement("SELECT * FROM PERSONNEL WHERE USERNAME=?");
			ResultSet rs;
			ps.setString(1, username);
			rs = ps.executeQuery();
			return rs;
		}catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Veritabanı hatası sebebiyle işlem gerçekleştirilemedi");
			return null;
			}
	}
	
	
	// To logout, do SessionSingleton.resetInstance()
	public static void logout() {
		UserSingleton.resetInstance();
	}
	public static boolean login(String username, String password, boolean justChecking) {
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
				if(justChecking) {
					// E.g. when changing password
					return true;
				}
				logout();
				int personnelID = Integer.parseInt(rs2.getString("PERSONNELID"));
				
			
				// Check if admin; if so, create an admin session
				ps = con.prepareStatement("SELECT ADMINID FROM ADMIN WHERE PERSONNELID=?;");
				ps.setInt(1, personnelID);
				rs2 = ps.executeQuery();
				
				if(rs2.next()) {
					// If it doesnt enter this block, it means that the result set only had one block, thus user isnt admin
					int adminID = Integer.parseInt(rs2.getString("ADMINID"));
					UserSingleton.getInstance(username, adminID);
					//Model.loadWindow("AdminPanel", 1205, 652);
			
				}else {
					// User isn't admin
					UserSingleton.getInstance(username, -1);
					
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
/*
 
CREATE TABLE PUBLIC.PERSONNEL
(PERSONNELID INTEGER NOT NULL IDENTITY,
USERNAME VARCHAR(100) NOT NULL,
PASSWORDHASH BINARY(16) NOT NULL,
LEVEL INTEGER,
CERTIFICATEDATE DATE,
NAME VARCHAR(100),
SURAME VARCHAR(100),
PRIMARY KEY (PERSONNELID),
UNIQUE (USERNAME))


 */
	
	
	public static boolean update(int personnelid, String username, int level, Date certificatedate, String name, String surname, boolean isAdmin) {
		Connection con = connect();
		PreparedStatement ps;
		try {
			ps = con.prepareStatement("UPDATE PERSONNEL SET USERNAME=?, LEVEL=?, CERTIFICATEDATE=?, NAME=?, SURNAME=? WHERE PERSONNELID=?");
			ps.setString(1, username);
			if(level == -1) {
				ps.setNull(2, java.sql.Types.INTEGER);
			}else {
				ps.setInt(2, level);
			}
			ps.setDate(3, certificatedate);
			ps.setString(4, name);
			ps.setString(5, surname);
			ps.setInt(6, personnelid);
			
			ps.executeUpdate();
			con.commit();
			System.out.println("Personel değiştirildi");
			ps = null;
			// Add to or remove from admin table
			ResultSet rs;
			ps = con.prepareStatement("SELECT ADMINID FROM ADMIN WHERE PERSONNELID=?;");
			ps.setInt(1, personnelid);
			rs = ps.executeQuery();
			ps = null; // Otherwise I get a resource leak warning...
			if(rs.next()) {
				if(isAdmin == false) {
					// Personnel was admin but shouldn't be anymore
					System.out.println("Personel'in elinden adminlik yetkisi alınıyor");
					ps = con.prepareStatement("DELETE FROM ADMIN WHERE PERSONNELID=?");
					ps.setInt(1, personnelid);
					ps.executeUpdate();
					con.commit();
				}
			}else if(isAdmin == true){
				// Personnel wasn`t admin but should be now
				System.out.println("Personel`e adminlik yetkisi veriliyor");
				ps = con.prepareStatement("INSERT INTO ADMIN(PERSONNELID) VALUES(?);");
				ps.setInt(1, personnelid);
				ps.executeUpdate();
				con.commit();
			}
			
			
			con.close();
			
		} catch (SQLException e) {
			System.out.println("Güncelleme işlemi veritabanında gerçekleştirilemedi");
			e.printStackTrace();
			return false;
		}
		

		return true;
	}
	
	
	public static boolean register(String username, String password, int level, Date certificatedate, String name, String surname, boolean isAdmin) {
		// Hash password
		byte[] hash = Model.hashPassword(password);
		
		Connection con = connect();
		
		// Check if user exists, because it is faster if the code doesn't require a try-catch block 
		
		// Create user
		PreparedStatement ps;
		try {
			ps = con.prepareStatement("INSERT INTO PERSONNEL(USERNAME, PASSWORDHASH, LEVEL, CERTIFICATEDATE, NAME, SURNAME) VALUES(?, ?, ?, ?, ?, ?);");
			ps.setString(1, username);
			ps.setBytes(2, hash);
			if(level == -1) {
				ps.setNull(3, java.sql.Types.INTEGER);
			}else {
				ps.setInt(3, level);
			}
			
			ps.setDate(4, certificatedate);
			ps.setString(5, name);
			ps.setString(6, surname);
			ps.executeUpdate();
			con.commit();
			System.out.println("Personel kaydedildi");
			if(isAdmin) {
				// Get PERSONNELID
				
				ResultSet rs2;
				ps = con.prepareStatement("SELECT PERSONNELID FROM PERSONNEL WHERE USERNAME=? AND PASSWORDHASH=?;");
				ps.setString(1, username);
				ps.setBytes(2, hash);
				rs2 = ps.executeQuery();
				if(rs2.next()) {
					int personnelID = Integer.parseInt(rs2.getString("PERSONNELID"));
					System.out.println("Yeni kaydedilen personelin ID si: " + personnelID);
					ps = con.prepareStatement("INSERT INTO ADMIN(PERSONNELID) VALUES(?);");
					ps.setInt(1, personnelID);
					ps.executeUpdate();
					con.commit();
					System.out.println("Admin kaydedildi");
				}else {
					System.out.println("Az önce oluşturulan kullanıcı, admin tablosuna eklenemedi... race conditions?");
				}
			}
			
			
			con.close();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			return false;
		}
		
		
		
		return true;
	}

	public static boolean remove(int personnelid) {
		Connection con = connect();
		PreparedStatement ps;
		ResultSet rs;
		try {
			ps = con.prepareStatement("SELECT PERSONNELID FROM ADMIN WHERE PERSONNELID=?");
			ps.setInt(1, personnelid);
			rs = ps.executeQuery();
			if(rs.next()) {
				// is admin
				// Maybe don't let people do this unless they have a specific admin id like 0...
				System.out.println("Admin yetkili bir personel hesabını siliyorsunuz.");
				ps = null;
				ps = con.prepareStatement("DELETE FROM ADMIN WHERE PERSONNELID=?");
				ps.setInt(1, personnelid);
				ps.executeUpdate();
				con.commit();
			}else {
				// isn`t admin
				System.out.println("Normal bir personel hesabını siliyorsunuz.");
			}
			
			// Personnel silme
			ps = null;
			ps = con.prepareStatement("DELETE FROM PERSONNEL WHERE PERSONNELID=?");
			ps.setInt(1, personnelid);
			ps.executeUpdate();
			con.commit();
		} catch (SQLException e) {
			System.out.println("");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	public static boolean changePassword(String username, String password) {
		Connection con = connect();
		byte[] hash = Model.hashPassword(password);
		PreparedStatement ps;
		try {
			ps = con.prepareStatement("UPDATE PERSONNEL SET PASSWORDHASH=? WHERE USERNAME=?;");
			ps.setBytes(1, hash);
			ps.setString(2, username);
			ps.executeUpdate();
			con.commit();
			con.close();
			return true;
		} catch (SQLException e) {
			System.out.println("Veritabanında şifre değiştirilemedi");
			e.printStackTrace();
			return false;
		}
	
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
