package implicaspection;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



public class Main extends Application{
	
	public static Stage mainStage = new Stage();
	
	@Override
	public void start(Stage primaryStage){
	
	try {
		mainStage = primaryStage;
		Parent root = FXMLLoader.load(getClass().getResource("/implicaspection/Login.fxml"));
		//primaryStage.initStyle(StageStyle.TRANSPARENT);
		Scene scene = new Scene(root, 500, 400);
		
		//scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
		mainStage.setScene(scene);
		scene.getStylesheets().add(getClass().getResource("/styling/application.css").toExternalForm());
		mainStage.setTitle("Login - Implicaspection");
		mainStage.show();
	} catch (Exception e){
		e.printStackTrace();
	}
		/*
		Model.loadWindow("Login", 500, 400);
		mainStage = primaryStage;
		mainStage.show();
		*/
	}

	public static void main(String[] args){
; 
		String yo = "istanbul";
		String yopart[] = yo.split("/");
		System.out.println(yopart[0]);
		launch(args);
		
		System.out.println("E003: Program optimal şekilde sonlandırılamadı");
		
	} 
	
	
}



//DatabaseAndSession.login("testPersonnel", "2turk2alman9uni");
//System.out.println(UserSingleton.getSession().toString());
//DatabaseAndSession.login("testAdmin", "kurtulus0201savasi");

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


try {
	
	Parent root = FXMLLoader.load(getClass().getResource("/implicaspection/Login.fxml"));
	//primaryStage.initStyle(StageStyle.TRANSPARENT);
	Scene scene = new Scene(root, 500, 400);
	//scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
	primaryStage.setScene(scene);
	scene.getStylesheets().add(getClass().getResource("/styling/application.css").toExternalForm());
	primaryStage.setTitle("Implicaspection - Login");
	primaryStage.show();
} catch (Exception e){
	e.printStackTrace();
}

*/