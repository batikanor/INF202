package implicaspection;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;


public class Model {
	// At first I didn't declare the functions here as static because I wanted the 'count' attribute to be special for every class that instances this class, but then I wanted to make the connectDB utility static and to enable usage of the functions here possible from there, I also turned this to static
	// This is more like a 'Helpers' class, that has all the needed functions for utils and controllers.
	
	//public static Vector<Stage> stages = new Vector<>();
	public static HashMap<String, Stage> stages = new HashMap<String, Stage>();
	public static String GetExcelColumnString(int columnNo){
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
			
			System.out.println("E000: Şifreleri saklamak için kullandığımız java standart kütüphanesindeki şifreleme algoritması bulunamadı...");
			e.printStackTrace();
		
		
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
			
			System.out.println("loading: " + Model.class.getResource("/implicaspection/" + toLoad + ".fxml"));
			root = FXMLLoader.load(Model.class.getResource("/implicaspection/" + toLoad + ".fxml"));
			Scene scene = new Scene(root, width, length);
			newStage.setScene(scene);
			scene.getStylesheets().add(Model.class.getResource("/styling/application.css").toExternalForm());
			newStage.setTitle(toLoad + " - Implicaspection");
			stages.put(toLoad, newStage);
			//stages.add(newStage);
			newStage.show();
		} catch (IOException e) {
			System.out.println("E001: Yüklenmeye çalışılan pencere bulunamadı");
			e.printStackTrace();
		}
		
	}
	public static void loadWindowNoCSS(String toLoad, int width, int length){
		Stage newStage = new Stage();
		Parent root;
		try {
			
			System.out.println("loading: " + Model.class.getResource("/implicaspection/" + toLoad + ".fxml"));
			root = FXMLLoader.load(Model.class.getResource("/implicaspection/" + toLoad + ".fxml"));
			Scene scene = new Scene(root, width, length);
			newStage.setScene(scene);

			newStage.setTitle(toLoad + " - Implicaspection");
			newStage.show();
			
		} catch (IOException e) {
			System.out.println("E001: Yüklenmeye çalışılan pencere bulunamadı");
			e.printStackTrace();
		}
		
	}
	
	public static void createPopup(String popupStr, Pane rootPane) {
		// TODO Auto-generated method stub
		Label lblStatus = new Label(popupStr);
		Button btnClose = new Button("Geri dön");
		btnClose.setPrefHeight(50);
		FlowPane fpPop = new FlowPane();
		fpPop.setPadding(new Insets(30,30,30,30));
		fpPop.setVgap(20);
		fpPop.setHgap(20);
		fpPop.autosize();
		fpPop.getChildren().addAll(btnClose, lblStatus);
		BoxBlur blur = new BoxBlur(4, 4, 4);
		rootPane.setEffect(blur);
		fpPop.setBlendMode(BlendMode.SRC_ATOP);
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
			}
			
		};
		btnClose.setOnAction(goBack);
		poppy.getContent().add(fpPop);
		poppy.show((Stage)(rootPane.getScene().getWindow()));
		

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
	
	public static String feedback(String fb, int count, String cName){
		// TODO
		// Log this action and put it to a file as final version, make the logging in a "try" block and just nevermind it if it fails
		// ^ so that a simple logging issue doesn't really affect the functionality of the program.
		String buff = (cName + " Klasındaki " + Integer.toString(count) + ". işlem şu idi: " + fb);
		System.out.println(buff);
		return (buff);
	}
	
	
	public static String generateRandom(){
		Random rand = new Random();
		String randNum = Integer.toString(rand.nextInt(101));
		feedback("Şu rastgele sayı üretildi: " + randNum, 0, "Model");
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
		System.out.println("Program tamamen sonlandırıldı!");
		System.exit(0);
		
	}
	public static void closeAllLoadLogin() {

		Main.mainStage.show();
		for(Stage st : stages.values()) {
			st.close();
		}

	}
	
}
