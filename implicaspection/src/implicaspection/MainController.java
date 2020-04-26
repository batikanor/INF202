package implicaspection;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utilities.SessionSingleton;
import javafx.scene.control.Button;

public class MainController extends ControllerTemplate{
	
	// Trivia
	@FXML
	private Label labelRandom;
	
	
	// About commands
	@FXML
	private TextField queryInput;
	@FXML
	private Label queryOutput;
	@FXML
	private int count = 0;
	
	// About administration
	@FXML
	
	// About MVC
	private String className = this.getClass().getSimpleName();


	@FXML Button buttonRandom;


	@FXML Button buttonAdmin;


	@FXML Label labelSession;


	@FXML Button buttonReport;


	@FXML Button buttonPassword;


	@FXML Button buttonLogout;


	@Override
	public void initialize() {
		count++;
		labelSession.setText(SessionSingleton.getSession().toString());
	}
	
	@FXML
	public void processInput(ActionEvent event) {
		count++;
		String input = queryInput.getText();
		queryOutput.setText(Model.feedback(input, count, className));
		
	}

	public void generateRandom(ActionEvent event) throws Exception {
		
		labelRandom.setText(Model.generateRandom());
		// Load Menu window
		Stage menuStage = new Stage();
		Parent rootMenu = FXMLLoader.load(getClass().getResource("/implicaspection/Menu.fxml"));
		Scene scene = new Scene(rootMenu, 800, 600);
		menuStage.setScene(scene);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		menuStage.setTitle("Implicaspection - Menu");
		menuStage.show();
	}
	
	public void openAdminPanel() {
		if(SessionSingleton.getSession().getAdminID() > -1) {
			Model.loadWindow("AdminPanel", 1000, 510);
			System.out.println("Admin panel yükleniyor");
		}else {
			System.out.println("Yalnizca adminler buraya ulaşabilir");
		}
		
	}


}
