package implicaspection;

import java.sql.Date;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import utilities.DatabaseAndSession;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;


public class AdminPanelController extends ControllerTemplate{

	@FXML private Pane paneField;
	@FXML private Pane paneTemplate;
	@FXML private Pane paneDB;
	@FXML private Pane paneRegister;
	@FXML private Button buttonRegister;
	@FXML private Button buttonField;
	@FXML private Button buttonTemplate;
	@FXML private Button buttonDB;
	
	
	
	@FXML RadioButton rbPersonnel;
	@FXML RadioButton rbAdmin;
	//@FXML ToggleGroup userType;
	@FXML PasswordField passwordInput;
	@FXML TextField usernameInput;
	@FXML PasswordField password2Input;
	@FXML TextField levelInput;
	@FXML DatePicker dateInput;
	@FXML TextField nameInput;
	@FXML TextField surnameInput;
	@FXML TextArea areaOutput;
	
	
	
	public void saveRegister(ActionEvent event) {
		count++;
		boolean inputOK = true;
		areaOutput.setText("");
		
		boolean isAdmin = false;
		Date certificatedate = null;
		String username = usernameInput.getText();
		String password = passwordInput.getText();
		String levelstr = levelInput.getText();
		int level = -1; // Integer isnt an object, it is a primitive data type and it cant be null.
		String name = nameInput.getText();
		String surname = surnameInput.getText();
		if(name.equals("")) {
			name = null;
		}
		if(surname.equals("")) {
			surname = null;
		}
		

		
		
		if(rbAdmin.isSelected()) {
			isAdmin = true;
		}
		if(password.length() < 2) {
			// They may want to have easier passwords, its a small company anyways...
			areaOutput.appendText("Lütfen 2 karakterden daha uzun bir şifre giriniz.\n");
			inputOK = false;
		}
		if(!password.equals(password2Input.getText())) {
			areaOutput.appendText("Şifreleriniz birbiriyle eşleşmiyor\n");
			inputOK = false;
		}
		if(username.length() < 2) {
			areaOutput.appendText("Lütfen 2 karakterden daha uzun bir kullanıcı adı giriniz.\n");
			inputOK = false;
		}
		if(!Model.isPositiveIntegerOrNull(levelstr)) {
			areaOutput.appendText("Lütfen seviye bölümüne bir sayı giriniz veya bölümü boş bırakınız.\n");
			inputOK = false;
		}else if(levelstr != null && !levelstr.equals("")){
			level = Integer.parseInt(levelstr);
		}
			
		
		
		if(dateInput.getValue() != null) {
			certificatedate = Date.valueOf(dateInput.getValue());
		}
		
		
		if(inputOK){
			DatabaseAndSession.register(username, password, level, certificatedate, name, surname, isAdmin);
			
			
		}
		
	}
	
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
