package implicaspection;


import javafx.application.Platform;
import javafx.event.ActionEvent;


public class MenuController extends ControllerTemplate{



	public void closeMenu(ActionEvent event) {
		count++;
		System.out.println("Program kapatılıyor");
		Platform.exit(); //exit javafx
		System.exit(0); //close app
	}
}
