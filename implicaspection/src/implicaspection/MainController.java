package implicaspection;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import utilities.DatabaseAndSession;
import utilities.SessionSingleton;
import javafx.scene.Scene;
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
		super.initialize();
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
		Model.loadWindow("Menu", 800, 600);
	}
	
	public void openAdminPanel() {
		if(SessionSingleton.getSession().getAdminID() > -1) {
			Model.loadWindow("AdminPanel", 1000, 510);
			System.out.println("Admin panel yükleniyor");
		}else {
			System.out.println("Yalnizca adminler buraya ulaşabilir");
		}
		
	}
	
	public void createReport() {
		// TODO
		Model.loadWindow("Report", 1000, 700);
	}
	
	public void closeAll() {
		Model.closeAll();
	}
	
	public void logout(){
		DatabaseAndSession.logout();
		Model.closeAllLoadLogin();
	}
	
	public void changePassword() {
		Label lblStatus = new Label();
		lblStatus.setText("Henüz deneme yapılmadı");
		PasswordField passOld = new PasswordField();
		passOld.setPromptText("Eski şifre");
		PasswordField passPop = new PasswordField();
		passPop.setPromptText("Yeni şifre");
		PasswordField confirmPop = new PasswordField();
		confirmPop.setPromptText("Yeni şifre tekrar");
		Button btnPop = new Button("Şifreyi Kaydet");
		Button btnClose = new Button("Geri dön");
		FlowPane fpPop = new FlowPane();
		fpPop.setPadding(new Insets(20));
		fpPop.setVgap(50);
		fpPop.getChildren().addAll(passOld, passPop, confirmPop, btnPop, btnClose, lblStatus);

		Popup poppy = new Popup();
		
		
		EventHandler<ActionEvent> goBack = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				poppy.hide();
			}
			
		};
		btnClose.setOnAction(goBack);
		
		EventHandler<ActionEvent> submit = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e2) {
				if(!DatabaseAndSession.login(SessionSingleton.getSession().getUsername(), passOld.getText(), true)) {
					// password isn`t correct
					lblStatus.setText("Yanlış şifre girdiniz!");
				}else {
					if(passPop.getText() != null && passPop.getText().length() > 2) {
						if(passPop.getText().contentEquals(confirmPop.getText())) {
							lblStatus.setText("Şifreniz değiştiriliyor");
							if(DatabaseAndSession.changePassword(SessionSingleton.getSession().getUsername(), passPop.getText())) {
								lblStatus.setText("Şifreniz değiştirildi");
							}else {
								lblStatus.setText("Şifreniz değiştirilemedi");
							}
							
						}else {
							lblStatus.setText("Girdiğiniz şifreler uyuşmuyor");
						}
					}else {
						lblStatus.setText("Şifreniz 2 karakterden uzun olmalı");
					}
					
				}
				

				
			}
			
		};
		btnPop.setOnAction(submit);
		
		poppy.getContent().add(fpPop);
		poppy.show((Stage)(buttonPassword.getScene().getWindow()));
		
		
	}


}
