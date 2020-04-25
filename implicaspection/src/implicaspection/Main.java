package implicaspection;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utilities.DatabaseAndSession;
import utilities.SessionSingleton;



public class Main extends Application{
	@Override
	public void start(Stage primaryStage){
		try {
			
			Parent root = FXMLLoader.load(getClass().getResource("/implicaspection/Login.fxml"));
			//primaryStage.initStyle(StageStyle.TRANSPARENT);
			Scene scene = new Scene(root, 400, 500);
			
			//scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
			primaryStage.setScene(scene);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setTitle("Implicaspection - Login");
			primaryStage.show();
		} catch (Exception e){
			e.printStackTrace();
		}
		
		
	
	}

	public static void main(String[] args){
		
		//ConnectDB.printAllPersonnel();
		
		DatabaseAndSession.login("testPersonnel", "2turk2alman9uni");
		System.out.println(SessionSingleton.getSession().toString());
		SessionSingleton.resetInstance();

		
		DatabaseAndSession.login("testAdmin", "kurtulus0201savasi");
		launch(args);
		
		
		System.out.println("ENDE !");
	} 
	
	
}



/*
		try {
			//BorderPane root = new BorderPane();
			Parent root = FXMLLoader.load(getClass().getResource("/implicaspection/Main.fxml"));
			Scene scene = new Scene(root, 800, 600);
			primaryStage.setScene(scene);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setTitle("Implicaspection - Login");
			primaryStage.show();
		} catch (Exception e){
			e.printStackTrace();
		}


*/