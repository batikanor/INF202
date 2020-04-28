package implicaspection;

import javafx.fxml.FXML;

public class ControllerTemplate {
	
	protected int count = 0;
	
	@FXML
	public void initialize() {
		// Automatically called by FXMLLoader (see https://openjfx.io/javadoc/14/javafx.fxml/javafx/fxml/Initializable.html)
		count++;
		System.out.println(this.getClass().getSimpleName() + " ekranı yüklendi " + count);

	}

}