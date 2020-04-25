package implicaspection;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

public class MenuController implements Initializable{

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}

	public void closeMenu(ActionEvent event) {
		Platform.exit(); //exit javafx
		System.exit(0); //close app
	}
}
