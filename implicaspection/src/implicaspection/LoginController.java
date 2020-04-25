package implicaspection;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import utilities.DatabaseAndSession;
import utilities.SessionSingleton;

public class LoginController {

	// About login
	@FXML
	private TextField txtUsername;
	@FXML
	private PasswordField txtPassword;
	@FXML
	private Label status;
	
	
	private int count = 0;
	
	// About MVC
	private String className = this.getClass().getSimpleName();
	
	@FXML
	public void login(ActionEvent event) throws Exception{
		count++;
		String feedback;
		if(DatabaseAndSession.login(txtUsername.getText(), txtPassword.getText())) {
			feedback = "Statü: giriş başarılı";
			status.setText(Model.feedback(feedback, count, className));
			// Load Main window

			Model.loadWindow(this.getClass(), "Main", 800, 600);
			
			// Hide previous window
			((Node)event.getSource()).getScene().getWindow().hide();
			
			System.out.println(SessionSingleton.getSession().toString());
		} else {
			feedback = "Statü: giriş başarısız";
			status.setText(Model.feedback(feedback, count, className));
		}
		
	}
	
	
}
