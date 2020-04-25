package implicaspection;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;

public class AdminPanelController {

	@FXML private Pane paneField;
	@FXML private Pane paneTemplate;
	@FXML private Pane paneDB;
	@FXML private Pane paneRegister;
	@FXML private Button buttonRegister;
	@FXML private Button buttonField;
	@FXML private Button buttonTemplate;
	@FXML private Button buttonDB;
	
	private int count = 0;
	
	@FXML
	public void changePane(ActionEvent event) {
		count++;
		
		//System.out.println(event.getSource().toString());
		//Button[id=buttonRegister, styleClass=button]'Personel / Admin kaydet'
		if(event.getSource() == buttonRegister) {
			paneRegister.toFront();
			
			paneField.setVisible(false);
			paneDB.setVisible(false);
			paneTemplate.setVisible(false);
			paneRegister.setVisible(true);
		}
		else if(event.getSource() == buttonField) {
			paneField.toFront();
			
			paneDB.setVisible(false);
			paneTemplate.setVisible(false);
			paneRegister.setVisible(false);
			paneField.setVisible(true);
		}
		else if(event.getSource() == buttonDB) {
			paneDB.toFront();
			
			paneTemplate.setVisible(false);
			paneRegister.setVisible(false);
			paneField.setVisible(false);
			paneDB.setVisible(true);
		}
		else if(event.getSource() == buttonTemplate) {
			paneTemplate.toFront();
			
			paneRegister.setVisible(false);
			paneField.setVisible(false);
			paneDB.setVisible(false);
			paneTemplate.setVisible(true);
		}
	}
	
	
	
	
}
