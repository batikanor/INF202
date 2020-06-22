package implicaspection;


import java.util.logging.Level;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import utilities.DatabaseAndSession;
import utilities.UserSingleton;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;

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

	

	//private String className = this.getClass().getSimpleName();


	@FXML Button buttonRandom;
	@FXML Button buttonAdmin;
	@FXML Label labelSession;
	@FXML Button buttonReport;
	@FXML Button buttonPassword;
	@FXML Button buttonLogout;

	@FXML AnchorPane mainAnchorPane;


	@Override
	public void initialize() {
		super.initialize();
		count++;
		labelSession.setText(UserSingleton.getSession().toString());
	}
	

	
	@FXML
	public void processInput(ActionEvent event) {
		count++;
		String input = queryInput.getText();
		queryOutput.setText(input);
		
	}

	public void generateRandom(ActionEvent event) throws Exception {
		
		labelRandom.setText(Model.generateRandom());

	}
	
	public void openAdminPanel() {
		if(UserSingleton.getSession().getAdminID() > -1) {
			Model.loadWindow("AdminPanel", 1300, 800);
			//System.out.println("Admin panel yükleniyor");
			Model.log.fine("Admin panel yükleniyor");
		}else {
			Model.createPopup(mainAnchorPane, "Yalnizca adminler buraya ulaşabilir", null, Level.WARNING);
			//System.out.println();
		}
		
	}
	
	public void createReport() {
		// TODO
		Model.loadWindow("Report", 1100, 700);
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
		BoxBlur blur = new BoxBlur(3, 3, 3);
		mainAnchorPane.setEffect(blur);
		fpPop.setBlendMode(BlendMode.SRC_ATOP);
		fpPop.setBackground(new Background(new BackgroundFill(Color.DARKSLATEGRAY, null, null)));
		fpPop.setBorder(new Border(new BorderStroke(Color.BLACK, 
	            BorderStrokeStyle.DASHED, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		
		
		Popup poppy = new Popup();
		
		poppy.centerOnScreen();

		EventHandler<ActionEvent> goBack = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				poppy.hide();
				mainAnchorPane.setEffect(null);
			}
			
		};
		btnClose.setOnAction(goBack);
		
		EventHandler<ActionEvent> submit = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e2) {
				if(!DatabaseAndSession.login(UserSingleton.getSession().getUsername(), passOld.getText(), true)) {
					// password isn`t correct
					lblStatus.setText("Yanlış şifre girdiniz!");
				}else {
					if(passPop.getText() != null && passPop.getText().length() > 2) {
						if(passPop.getText().contentEquals(confirmPop.getText())) {
							lblStatus.setText("Şifreniz değiştiriliyor");
							if(DatabaseAndSession.changePassword(UserSingleton.getSession().getUsername(), passPop.getText())) {
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
