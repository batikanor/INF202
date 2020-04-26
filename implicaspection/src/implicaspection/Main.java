package implicaspection;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application{
	@Override
	public void start(Stage primaryStage){
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/implicaspection/Login.fxml"));
			//primaryStage.initStyle(StageStyle.TRANSPARENT);
			Scene scene = new Scene(root, 400, 500);
			//scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
			primaryStage.setScene(scene);
			scene.getStylesheets().add(getClass().getResource("/styling/application.css").toExternalForm());
			
			primaryStage.setTitle("Implicaspection - Login");
			primaryStage.show();
		} catch (Exception e){
			e.printStackTrace();
		}
		
		
	
	}

	public static void main(String[] args){
	
		//DatabaseAndSession.login("testPersonnel", "2turk2alman9uni");
		//System.out.println(SessionSingleton.getSession().toString());
		//DatabaseAndSession.login("testAdmin", "kurtulus0201savasi");
		launch(args);
		
		
		System.out.println("Program tamamen sonlandırıldı!");
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