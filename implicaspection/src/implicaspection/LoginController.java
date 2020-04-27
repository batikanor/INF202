package implicaspection;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.WindowEvent;
import utilities.DatabaseAndSession;
import utilities.SessionSingleton;

public class LoginController extends ControllerTemplate{

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
	
@Override
public void initialize() {
	
	super.initialize();
	
	// This event is fired whenever the window is shown
	Main.mainStage.setOnShowing(new EventHandler<WindowEvent>() {
	    @Override
	    public void handle(WindowEvent event) {
	        txtUsername.setText("");
	        txtPassword.setText("");
	    }
	});
	}

	public void login(ActionEvent event) throws Exception{
		count++;
		String feedback;
		
		if(DatabaseAndSession.login(txtUsername.getText(), txtPassword.getText(), false)) {
			feedback = "Statü: giriş başarılı";
			status.setText(Model.feedback(feedback, count, className));
			// Load Main window

			Model.loadWindow("Main", 800, 600);
			// Hide previous window
			((Node)event.getSource()).getScene().getWindow().hide();
//			// Alternative code to hide windows		
//			Node thisNode = ((Node)event.getSource());
//			Stage thisStage = (Stage)(thisNode.getScene().getWindow());
//			thisStage.close();
			
			System.out.println(SessionSingleton.getSession().toString());
		} else {
			feedback = "Statü: giriş başarısız";
			status.setText(Model.feedback(feedback, count, className));
		}
		
	}
	
	
}
