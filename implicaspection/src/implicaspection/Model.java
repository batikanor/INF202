package implicaspection;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Model {
	// At first I didn't declare the functions here as static because I wanted the 'count' attribute to be special for every class that instances this class, but then I wanted to make the connectDB utility static and to enable usage of the functions here possible from there, I also turned this to static
	// This is more like a 'Helpers' class, that has all the needed functions for utils and controllers.
	
	
	public static byte[] hashPassword(String password)
	{
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
	public static void loadWindow(String toLoad, int width, int length)
	{
		Stage newStage = new Stage();
		Parent root;
		try {
			
			System.out.println("loading: " + Model.class.getResource("/implicaspection/" + toLoad + ".fxml"));
			root = FXMLLoader.load(Model.class.getResource("/implicaspection/" + toLoad + ".fxml"));
			Scene scene = new Scene(root, width, length);
			newStage.setScene(scene);
			scene.getStylesheets().add(Model.class.getResource("/styling/application.css").toExternalForm());
			newStage.setTitle(toLoad + " - Implicaspection");
			newStage.show();
			
		} catch (IOException e) {
			System.out.println("E001: Yüklenmeye çalışılan pencere bulunamadı");
			e.printStackTrace();
		}
		
	}
	public static void loadWindowNoCSS(String toLoad, int width, int length)
	{
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
	
	public static String feedback(String fb, int count, String cName)
	{
		// Log this action and put it to a file as final version, make the logging in a "try" block and just nevermind it if it fails
		// ^ so that a simple logging issue doesn't really affect the functionality of the program.
		String buff = (cName + " Klasındaki " + Integer.toString(count) + ". işlem şu idi: " + fb);
		System.out.println(buff);
		return (buff);
	}
	
	
	public static String generateRandom()
	{
		Random rand = new Random();
		String randNum = Integer.toString(rand.nextInt(101));
		feedback("Şu rastgele sayı üretildi: " + randNum, 0, "Model");
		return randNum; //Generates a random number in range [0,100]
		
	}
	public static boolean isPositiveIntegerOrNull(String text) {
		// negative isnt accepted, value can be zero or null!
		for(int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if(ch < '0' || ch > '9') {
				return false;
			}
		}
		return true;
	}
	
}
