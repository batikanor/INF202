package implicaspection;

import java.util.logging.Level;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.WindowEvent;
import utilities.DatabaseAndSession;
import utilities.UserSingleton;

public class LoginController extends ControllerTemplate{

	// About login
	@FXML
	private TextField txtUsername;
	@FXML
	private PasswordField txtPassword;
	@FXML
	private Label status;
	@FXML
	private AnchorPane rootPane;
	
	//private int count = 0;
	
	
	//private String className = this.getClass().getSimpleName();
	
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
			feedback = "Giriş başarılı";
			Model.createPopup(rootPane, feedback, null, Level.FINEST);
			// Load Main window

			Model.loadWindow("Main", 800, 600);
			// Hide previous window
			((Node)event.getSource()).getScene().getWindow().hide();
//			// Alternative code to hide windows		
			//Node thisNode = ((Node)event.getSource());
			//Stage thisStage = (Stage)(thisNode.getScene().getWindow());
			//thisStage.close();
			
			System.out.println(UserSingleton.getSession().toString());
		} else {
			feedback = "Giriş denemesi başarısız";
			Model.createPopup(rootPane, feedback, null, Level.WARNING);
			//Model.createPopup(rootPane, feedback, new TextArea("yooo!"));
			status.setText(feedback);
		}
		
	}
	
	
}
