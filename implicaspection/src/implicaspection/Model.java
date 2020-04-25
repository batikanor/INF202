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
	public static void loadWindow(Class<?> cl, String toLoad, int width, int length)
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
	
	public static String feedback(String fb, int count, String cName)
	{
		// Log this action and put it to a file as final version, make the logging in a "try" block and just nevermind it if it fails
		// ^ so that a simple logging issue doesn't really affect the functionality of the program.
		String buff = ("The " + Integer.toString(count) + ". query in class " + cName + " is " + fb);
		System.out.println(buff);
		return (buff);
	}
	
	
	public static String generateRandom()
	{
		Random rand = new Random();
		String randNum = Integer.toString(rand.nextInt(101));
		feedback("random number was generated: " + randNum, 0, "Model");
		return randNum; //Generates a random number in range [0,100]
		
	}
	
}
