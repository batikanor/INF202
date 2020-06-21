package implicaspection;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.PlatformLoggingMXBean;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import javafx.stage.Stage;



public class Model {
	// At first I didn't declare the functions here as static because I wanted the 'count' attribute to be special for every class that instances this class, but then I wanted to make the connectDB utility static and to enable usage of the functions here possible from there, I also turned this to static
	// This is more like a 'Helpers' class, that has all the needed functions for utils and controllers.
	
	//public static Vector<Stage> stages = new Vector<>();
	public static HashMap<String, Stage> stages = new HashMap<String, Stage>();
	
	public static Logger log = Logger.getLogger(Model.class.getPackageName());


	
	public static String joinCode(String[] s, String delimiter) {
		String ret = "";
	    if (s == null) {
	    	return "";
	    }
	    for (String st : s) {
	    	ret += delimiter;
	    	ret += st;
	    	
	    }
	    return ret;
	}

    public static int getExcelColumnInt(String col) {
        int res = 0;
        int len = col.length();
        
        for (int i = 0; i < len; i++) {
        
        	res *= 26;
            res += col.charAt(i) - 'A' + 1;
        }
        return res;
    }
	
	public static String getExcelColumnString(int columnNo){
	    int dividend = columnNo;
	    String columnStr = "";
	    int mod;

	    while (dividend > 0)
	    {
	        mod = (dividend - 1) % 26;
	        columnStr += (char)(65 + mod);
	        dividend = (int)((dividend - mod) / 26);
	    } 

	    return columnStr;
	}
	public static String getFileSizeKiloByteString(File file) {
		double len = (double)file.length() / 1024;

		return String.valueOf(len) + " KB";
	}
	
	public static byte[] hashPassword(String password){
		// Creating the salt for passwordHash creation (you need to import java.security.SecureRandom)
		// SecureRandom random = new SecureRandom();
		// byte[] salt = new byte[16]; //e.g: 101 | 40 | 21 | 17 | -99 | -111 | -9 | -7 | -115 | 4 | -24 | -123 | 37 | 116 | 113 | 21 | 
		// random.nextBytes(salt);
		
		// Also an option: do not hard-code the salt, but make it a separate file, which can be changed if they wish to disable access to all previous logins.
		byte[] salt = {101, 40, 21, 17, -99, -11, -9, -7, -115, 4, -24, -123, 37, 116, 113, 21};
		
		// Creation of a PBEKeySpec and a SecretKeyFactory and instantiation with PBKDF2WithHmacSHA1 algorithm
		// Third argument of the function below (65536 : binary 10000000000000000) is  the strength parameter. It indicates how many iterations this algorithm runs for, increasing the time it takes to produce hash.
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
		SecretKeyFactory factory = null;
		try {
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		} catch (NoSuchAlgorithmException e) {
			String eStr = "E000: Şifreleri saklamak için kullandığımız java standart kütüphanesindeki şifreleme algoritması bulunamadı...";
			
			createErrorPopup(null, eStr, null, e);
			
			//e.printStackTrace();
		
		
		}
		byte[] hash = null;
		try {
			hash = factory.generateSecret(spec).getEncoded();
		} catch (InvalidKeySpecException e) {
			System.out.println("E001: Şifreniz hash'lenemedi.");
			e.printStackTrace();
		}
		return hash;
		
	}
	

	public static void loadWindow(String toLoad, int width, int length){
		Stage newStage = new Stage();
		Parent root;
		try {
			
			log.info("loading: " + Model.class.getResource("/implicaspection/" + toLoad + ".fxml"));
			root = FXMLLoader.load(Model.class.getResource("/implicaspection/" + toLoad + ".fxml"));
			Scene scene = new Scene(root, width, length);
			newStage.setScene(scene);
			scene.getStylesheets().add(Model.class.getResource("/styling/application.css").toExternalForm());
			newStage.setTitle(toLoad + " - Implicaspection");
			stages.put(toLoad, newStage);
			//stages.add(newStage);
			newStage.show();
		} catch (IOException e) {
		
			log.warning("E001: Yüklenmeye çalışılan pencere bulunamadı");
			//e.printStackTrace();
		}
		
	}
	public static void loadWindowNoCSS(String toLoad, int width, int length){
		Stage newStage = new Stage();
		Parent root;
		try {
			Model.log.info("loading: " + Model.class.getResource("/implicaspection/" + toLoad + ".fxml"));
	
			root = FXMLLoader.load(Model.class.getResource("/implicaspection/" + toLoad + ".fxml"));
			Scene scene = new Scene(root, width, length);
			newStage.setScene(scene);

			newStage.setTitle(toLoad + " - Implicaspection");
			newStage.show();
			
		} catch (IOException e) {
			createErrorPopup(null, "E001: Yüklenmeye çalışılaan pencere bulunamadı", null, e);
		}
		
	}
	public static void createErrorPopup(Pane rootPane, String popupStr, Node otherNode, Exception e ) {

		
		StringWriter errors = new StringWriter();
		if (e != null) {
			e.printStackTrace(new PrintWriter(errors));
		} else {
			errors.append(popupStr);
		}
		Model.log.log(Level.SEVERE,popupStr + " tam sebep: \r\n\r\n <hata_detayi> \n\r" + errors.toString() + "\r\n</hata_detayi>\r\n\r\n", e);
		
		Label lblStatus = new Label(popupStr);
		Button btnClose = new Button("Geri dön");
		btnClose.setPrefHeight(50);
		FlowPane fpPop = new FlowPane();
		fpPop.setPadding(new Insets(30,30,30,30));
		fpPop.setVgap(20);
		fpPop.setHgap(20);
		fpPop.autosize();
		fpPop.getChildren().addAll(btnClose, lblStatus);
		if (otherNode != null) {
		fpPop.getChildren().add(otherNode);	
		}
		BoxBlur blur = new BoxBlur(4, 4, 4);
		rootPane.setEffect(blur);
		fpPop.setBlendMode(BlendMode.RED);
		//fpPop.setBackground(new Background(new BackgroundFill(Color.DARKGREY, null, null)));
		//fpPop.setBorder(new Border(new BorderStroke(Color.BLACK, 
	          //  BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THICK)));
		
		
		Popup poppy = new Popup();
		
		poppy.centerOnScreen();

		EventHandler<ActionEvent> goBack = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				poppy.hide();
				rootPane.setEffect(null);
				
				
				// Maybe do something with the now maybe altered otherNode here
			}
			
		};
		btnClose.setOnAction(goBack);
		poppy.getContent().add(fpPop);
		poppy.show((Stage)(rootPane.getScene().getWindow()));
		
		

	}
	public static boolean createPopup(Pane rootPane, String popupStr, Node otherNode, Level logLevel){

		//final Object lock = new Object();
			
		if (logLevel != null) {
			Model.log.log(logLevel, popupStr);
		}
		
		Label lblStatus = new Label(popupStr);
		Button btnClose = new Button("O.K.");
		btnClose.setPrefHeight(50);
		FlowPane fpPop = new FlowPane();
		fpPop.setPadding(new Insets(30,30,30,30));
		fpPop.setVgap(20);
		fpPop.setHgap(20);
		fpPop.autosize();
		fpPop.getChildren().addAll(btnClose, lblStatus);
		if (otherNode != null) {

		fpPop.getChildren().add(otherNode);	
		}
		BoxBlur blur = new BoxBlur(4, 4, 4);
		
		if (rootPane != null) {
			rootPane.setEffect(blur);
		}
		
		fpPop.setBlendMode(BlendMode.SRC_ATOP);
		//fpPop.setBackground(new Background(new BackgroundFill(Color.DARKGREY, null, null)));
		//fpPop.setBorder(new Border(new BorderStroke(Color.BLACK, 
	          //  BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THICK)));
		
		
		Popup poppy = new Popup();
		
		poppy.centerOnScreen();

		EventHandler<ActionEvent> goBack = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				// Maybe do something with the now maybe altered otherNode here

				if (otherNode != null) {
					//fpPop.getChildren().get(2);
					//synchronized(lock) {
						//popupFired = true;
						//lock.notifyAll();
					//}
					String ud = otherNode.getUserData().toString();
					
					if (ud.startsWith("???")) {
						ud = ud.substring(3);
						
						String ss[] = ud.split("\\?\\?\\?");
						if (ss[0].startsWith("AdminPanel")) {
							String cellLocationExcel = ss[1]; // T7, AA5 ..."AdminPanelDefault"
							// Substract the string from int
							int rowNo = 0, colNo;
							String col = "";
							for (int i = 0; i < cellLocationExcel.length(); i++) {
								
								try {
								
									rowNo = Integer.valueOf(cellLocationExcel.substring(i));
								
								} catch (NumberFormatException ex) {
									
									col += cellLocationExcel.charAt(i);
									continue;
								
								}
								
								// If the catch block wasn't entered
								break; ///< Cuz the rowNo is already done
								
							}
							colNo = getExcelColumnInt(col);
							rowNo--;
							colNo--;
							//System.out.println("rowno : " + String.valueOf(rowNo) + "colNo = " + String.valueOf(colNo));
				
							//System.out.println(AdminPanelController.sheet.getRow(rowNo).getCell(colNo).getCellComment().getString());
							String oldComment = AdminPanelController.sheet.getRow(rowNo).getCell(colNo).getCellComment().getString().toString();
							//System.out.println("AAACS" + oldComment);
							oldComment = oldComment.substring(3);
							
							String oldParts[] = oldComment.split(Main.delimiterRegex);
							//oldParts[0] = default, ...
							oldParts[3] = ((TextField)otherNode).getText();
							if (ss[0].contentEquals("AdminPanelPercent")) {
								int inta;
								try{
									inta = Integer.parseInt(oldParts[3]);
								} catch (Exception exx) {
									createPopup(rootPane, "Girdi bir sayı değil!", null, Level.WARNING);
									return;
								}
								if (inta < 0 || inta > 100) {
									createPopup(rootPane, "Girdi 0 - 100 arasi değil!", null, Level.WARNING);
									return;
								}
								
							} 
							String newComment = Model.joinCode(oldParts, Main.delimiter);

							AdminPanelController.wb.getSheetAt(0).getRow(rowNo).getCell(colNo).getCellComment().setString(newComment);
							
						}
						
						
						
					}

					
				}
				poppy.hide();
				if (rootPane != null) {
					rootPane.setEffect(null);
				}
				
	
				

				

			}
			
		};
		btnClose.setOnAction(goBack);
		poppy.getContent().add(fpPop);
		if (rootPane != null) {
			poppy.show((Stage)(rootPane.getScene().getWindow()));
		} else {
			for (Stage s : Model.stages.values()) {
				try {
					poppy.show(s);
					return true;
				} catch (Exception e) {
					// That stage wasnt being shown
					
				}
			}
			
			
		}
		
		//synchronized(lock) {
			//while (!Model.popupFired) {
				//try {
				//	lock.wait();
				//} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
				//	e1.printStackTrace();
				//}
			//}
			
		//}
		return false;

		
		

	}
	/*
	 * 	public static void loadWindow(Class<?> cl, String toLoad, int width, int length)
	{
		Stage primaryStage = new Stage();
		Parent root;
		try {
			System.out.println("loading: " + cl.getResource("/implicaspection/" + toLoad + ".fxml"));
			root = FXMLLoader.load(cl.getResource("/implicaspection/" + toLoad + ".fxml"));
			Scene scene = new Scene(root, width, length);
			primaryStage.setScene(scene);
			scene.getStylesheets().add(cl.getResource("application.css").toExternalForm());
			primaryStage.setTitle(toLoad + " - Implicaspection");
			primaryStage.show();
		} catch (IOException e) {
			System.out.println("E001: Yüklenmeye çalışılan pencere bulunamadı");
			e.printStackTrace();
		}
		
	}
	 *]]]]]]]]]]]] 
	 *            StackPane secondaryLayout = new StackPane();
                secondaryLayout.getChildren().add(secondLabel);
 
                Scene secondScene = new Scene(secondaryLayout, 230, 100);
 
                // New window (Stage)
                Stage newWindow = new Stage();
                newWindow.setTitle("Second Stage");
                newWindow.setScene(secondScene);
 
                // Set position of second window, related to primary window.
                newWindow.setX(primaryStage.getX() + 200);
                newWindow.setY(primaryStage.getY() + 100);
 
                newWindow.show();
	 */
	/*
	 * public static String feedback(String fb, int count, String cName){ // TODO //
	 * Log this action and put it to a file as final version, make the logging in a
	 * "try" block and just nevermind it if it fails // ^ so that a simple logging
	 * issue doesn't really affect the functionality of the program. String buff =
	 * (cName + " Klasındaki " + Integer.toString(count) + ". işlem şu idi: " + fb);
	 * System.out.println(buff); return (buff); }
	 */
	
	
	public static String generateRandom(){
		Random rand = new Random();
		String randNum = Integer.toString(rand.nextInt(101));

		Model.log.finest("Şu rastgele sayı üretildi: " + randNum);
		return randNum; //Generates a random number in range [0,100]
		
	}
	public static boolean isPositiveIntegerOrNull(String text) {
		// negative isn't accepted, value can be zero or null!
		if(text == null) {
			return true;
		}
		for(int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if(ch < '0' || ch > '9') {
				return false;
			}
		}
		return true;
	}
	
	public static void closeAll() {

		
		Platform.exit();
		Model.log.finest("Program düzgün şekilde sonlandırıldı\n");
		System.exit(0);
		
	}
	public static void closeAllLoadLogin() {

		Main.mainStage.show();
		for(Stage st : stages.values()) {
			st.close();
		}

	}
	/*
	static Level	ALL
	ALL indicates that all messages should be logged.
	static Level	CONFIG
	CONFIG is a message level for static configuration messages.
	static Level	FINE
	FINE is a message level providing tracing information.
	static Level	FINER
	FINER indicates a fairly detailed tracing message.
	static Level	FINEST
	FINEST indicates a highly detailed tracing message.
	static Level	INFO
	INFO is a message level for informational messages.
	static Level	OFF
	OFF is a special level that can be used to turn off logging.
	static Level	SEVERE
	SEVERE is a message level indicating a serious failure.
	static Level	WARNING
	WARNING is a message level indicating a potential problem.
	*/
	public static void initLogger() {
		Logger root = Logger.getLogger(Model.class.getPackageName());
		//root = Logger.getLogger(ReportController.class.getName());
		FileHandler logFile = null;
		//BasicConfigurator.configure();
		
		try {
			// Change to Logs.txt after actual deployment
			logFile = new FileHandler("logs/developmentLogs.txt", true);
			logFile.setLevel(Level.ALL);
			
		} catch (IOException | SecurityException e) {
			e.printStackTrace();
			// Couldn't initialize logger, then what?
		}
		root.setLevel(Level.ALL);
		//com.sun.javafx.Logging.getCSSLogger().setLevel(Level.OFF);
		//System.out.println(Logging.getCSSLogger().toString());
	  
		List<String> loggerNames = ManagementFactory.getPlatformMXBean(PlatformLoggingMXBean.class).getLoggerNames();
		for (String logged : loggerNames) {
			System.out.println("loggerlerden biri : " + logged);
		}
	
		//ManagementFactory.getPlatformMXBean(PlatformLoggingMXBean.class).setLoggerLevel("styling/application.css", "OFF");
		logFile.setFormatter(new Formatter() {
			@Override
			public String format(LogRecord rec) {
				// TODO Auto-generated method stub
				String ret = "";
				if (rec.getLevel().intValue() >= Level.WARNING.intValue()) {
					ret += "\r\n";
					ret += "ÖNEMLİ: ";
				}
				ret += rec.getLevel() + " ";
				SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				Date d = new Date(rec.getMillis());
				ret += df.format(d) + " ";
				ret += this.formatMessage(rec);
				
				
				ret += " -> ";
				
				return ret;
				
				
			}
		});
		root.addHandler(logFile);
	}
	

	
}
