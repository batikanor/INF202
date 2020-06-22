package implicaspection;

import java.io.File;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import objects.CodedCell;
import utilities.DatabaseAndSession;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

public class AdminPanelController extends ControllerTemplate {

	private int personnelid = -1; // id of the personnel we`re working with

	@FXML
	private Pane paneFieldVal;
	@FXML
	private Pane paneTemplate;
	@FXML
	private Pane paneDB;
	@FXML
	private Pane paneRegister;
	@FXML
	private Button buttonRegister;
	@FXML
	private Button buttonFieldVal;
	@FXML
	private Button buttonTemplate;
	@FXML
	private Button buttonDB;

	@FXML
	RadioButton rbPersonnel;
	@FXML
	RadioButton rbAdmin;
	// @FXML ToggleGroup userType;
	@FXML
	PasswordField passwordInput;
	@FXML
	TextField usernameInput;
	@FXML
	PasswordField password2Input;
	@FXML
	TextField levelInput;
	@FXML
	DatePicker dateInput;
	@FXML
	TextField nameInput;
	@FXML
	TextField surnameInput;
	@FXML
	TextArea areaOutput;
	@FXML
	RadioButton rbRegister;
	@FXML
	RadioButton rbChange;
	@FXML
	TextField changeInput;

	@FXML
	Button buttonRemove;

	@FXML
	Button buttonSelectTemplate;
	@FXML
	Button buttonSaveTemplate;
	@FXML
	Label lblFileName;
	@FXML
	Label lblSelectedFile;
	@FXML
	Label lblFilePath;
	@FXML
	Label lblFileSize;
	@FXML
	Label lblSheet;

	private File selectedFile;
	static XSSFWorkbook wb;
	static XSSFSheet sheet;

	private	String chosenFieldType;
	private String chosenFieldMeaning;
	private String chosenFieldName;
	private String chosenDecisiveName;
	private String chosenDecisiveContent;
	//private String chosenFieldContent;
	
	@FXML
	ToggleGroup fieldType;

	// Configuring the table
	@FXML
	private TableView<CodedCell> fieldsTable;
	@FXML
	private TableColumn<CodedCell, String> locationColumn;
	@FXML
	private TableColumn<CodedCell, String> codeColumn;

	@FXML Label lblDBOutput;

	@FXML TextArea dbInput;

	@FXML RadioButton stQuery;

	@FXML RadioButton stUpdate;

	@FXML RadioButton stOther;

	@FXML ListView<String> listChosen;

	@FXML AnchorPane rootPane;

	@FXML Label lblFieldInfo;

	@FXML ToggleGroup valueType;

	@FXML TextField ctFieldName;

	@FXML TextField ctFieldContent;

	@FXML TextField ctDecisiveName;

	@FXML TextField ctDecisiveContent;

	@FXML RadioButton contentCombo;

	@FXML RadioButton contentDepend;

	@FXML Button buttonAdd;

	@Override
	public void initialize() {
	
		super.initialize();
		//Model.createPopup(null, "denemee", null, null);
		locationColumn.setCellValueFactory(new PropertyValueFactory<CodedCell, String>("cellLocation"));
		codeColumn.setCellValueFactory(new PropertyValueFactory<CodedCell, String>("cellCode"));

	}
	

	public void updateFields() {

			
		String chosenItem;
		if (listChosen.getSelectionModel().getSelectedItem() != null) {
			
			ctFieldName.setText(chosenFieldName);
			
			chosenItem = listChosen.getSelectionModel().getSelectedItem();
		
		
			
			// Update the fields that add values
			if (chosenFieldType.contentEquals("combo")) {

				//contentCombo.setSelected(true);
			
				
				
			} else if (chosenFieldType.contentEquals("depend")) {
	
				//contentDepend.setSelected(true);
				ctDecisiveName.setText(chosenDecisiveName);
				
				String decisiveContentAndDependantContent[] = chosenItem.split(Main.delimiterRegex);
				chosenDecisiveContent = decisiveContentAndDependantContent[0];
				//chosenFieldContent = decisiveContentAndDependantContent[1];
				
				ctDecisiveContent.setText(chosenDecisiveContent);
				
			} // else do not change
		}
		
		ctFieldName.setDisable(false);
		ctFieldContent.setDisable(false);
		
		if (contentDepend.isSelected()) {
		
			ctDecisiveName.setDisable(false);
			ctDecisiveContent.setDisable(false);
		
		
		} else {
	
			ctDecisiveName.setDisable(true);
			ctDecisiveContent.setDisable(true);
	
		}
		buttonAdd.setDisable(false);
		
	
	}
	
	public void couldntAdd() {
		Model.createPopup(rootPane, "Ekleme yapılamadı!!", null, Level.WARNING);
	}
	
	public void addContent() {
		String fn = ctFieldName.getText();
		String fc = ctFieldContent.getText();
		String dn = ctDecisiveName.getText();
		String dc = ctDecisiveContent.getText();
		if (fn == null || fc == null) {
			couldntAdd();
			return;
		}
		
		if (contentDepend.isSelected()) {
			
			if (dn == null || dc == null) {
				couldntAdd();
				return;
			}
			DatabaseAndSession.addDependantValue(fn, fc, dn, dc);
			
			
			
		} else {
			// Combobox value
			DatabaseAndSession.addComboboxValue(fn, fc);
			
		}
	}
	


	public void removeContent() {

		if (listChosen.getSelectionModel().getSelectedItem() != null) {
			Model.log.info(listChosen.getSelectionModel().getSelectedItem().toString() + " değeri siliniyor");
			String chosenItem =  listChosen.getSelectionModel().getSelectedItem();
			//System.out.println(chosenFieldType);
			if (chosenFieldType.contentEquals("combo")) {
				// Remove data from combobox
				
				DatabaseAndSession.removeComboBoxValue(chosenFieldName, chosenItem);
				
				
			} else if (chosenFieldType.contentEquals("depend")) {
				
				// String decisiveName, String decisiveContent, String dependantName, String dependantContent
				String decisiveContentAndDependantContent[] = chosenItem.split(Main.delimiterRegex);
				String decCon = decisiveContentAndDependantContent[0];
				String depCon = decisiveContentAndDependantContent[1];
				
				DatabaseAndSession.removeDependantValue(chosenDecisiveName, decCon, chosenFieldName, depCon);
			}
		}
	}

	

	/*
	
	public void editContent() {
	
		if (listChosen.getSelectionModel().getSelectedItem() != null) {
			
			Model.log.info(listChosen.getSelectionModel().getSelectedItem().toString() + " değeri seçildi");
			
			if (chosenFieldType.contentEquals("combo")) {
				
			} else if (chosenFieldType.contentEquals("depend")) {
				
			}
		}
		
	}
	*/
	
	public void getCodeOfSelectedCell() {
		lblFieldInfo.setText("-");
		try {
			listChosen.getItems().clear();
			
			

			int row = fieldsTable.getFocusModel().getFocusedCell().getRow();
			CodedCell cc = fieldsTable.getItems().get(row);
			String strCode = codeColumn.getCellData(cc);
			String strS[] = strCode.split(Main.delimiterRegex);
			chosenFieldType = strS[1];
			chosenFieldMeaning = strS[2];
			chosenFieldName = strS[3];
			lblFieldInfo.setText("En son seçilen bölge: " + chosenFieldMeaning + " (" + chosenFieldName + ")\n");
			if (chosenFieldType.contentEquals("combo")) {
				ComboBox<String> cb = DatabaseAndSession.returnComboBoxValues(chosenFieldName);
				listChosen.getItems().addAll(cb.getItems());
				
			} else if (chosenFieldType.contentEquals("depend")) {
				ComboBox<String> cb = DatabaseAndSession.returnDependantOptions(chosenFieldName);
				chosenDecisiveName = cb.getItems().get(0);
				cb.getItems().remove(0);
				lblFieldInfo.setText(lblFieldInfo.getText() + "Bağlı olunan alan: " + chosenDecisiveName + "\n" + "(Soldaki bağlı olunan değer, sağdaki ise bağlı değerdir.)");
				listChosen.getItems().addAll(cb.getItems());
				
			} else if (chosenFieldType.contentEquals("default")) {
				if (strS[4] == null) {
					//Not possible!
				}
				String popupStr = "Bu bölgedeki güncel default değer: " + strS[4] + " :Değiştirmek istiyorsanız yeni değeri yazınız";
				TextField tf = new TextField(strS[4]);
				tf.setUserData(Main.delimiter + "AdminPanelDefault" + Main.delimiter + locationColumn.getCellData(row));
				Model.createPopup(rootPane, popupStr, (Node) tf, Level.FINE);

				//while (Model.popupFired == false) {
					// Do nothing, i.e. wait
					//Thread.sleep(200);
				
				//}
				
				//System.out.println(tf.getText());
			} else if (chosenFieldType.contentEquals("percent")) {
				if (strS[4] == null) {
					//Not possible!
				}
				String popupStr = "Bu bölgedeki güncel percent değer: " + strS[4] + " :Değiştirmek istiyorsanız yeni değeri yazınız. [0-100] arası sayı girmezseniz değişim olmaz.";
				TextField tf = new TextField(strS[4]);
				tf.setUserData(Main.delimiter + "AdminPanelPercent" + Main.delimiter + locationColumn.getCellData(row));
				Model.createPopup(rootPane, popupStr, (Node) tf, Level.FINE);

			}
		} catch (Exception e) {
			Model.createPopup(rootPane, "Seçilen hücre değeri değiştirilemez", null, Level.WARNING);
		}

		
	}
	
	@FXML
	private void updateTable() {
		try {
			fieldsTable.setItems(getCodedCells());
		} catch (Exception e) {
			Model.createPopup(rootPane, "Henüz bir dosya seçilmemiş", null, Level.WARNING);
		}
		
	
	}
	
	@FXML
	private void executeStatement() {
		// executes any kind of statement. So not just query or update...
		int mode;
		if (stQuery.isSelected()) {
			mode = 0;
		} else if (stUpdate.isSelected()) {
			mode = 1;
		} else {
			mode = 2;
		}
		String st = dbInput.getText(); 
		lblDBOutput.setText(DatabaseAndSession.executeStatementDirectly(st, mode));
	}

	private ObservableList<CodedCell> getCodedCells() {
		// ObservableList is like ArrayList, but designed specifically for GUI
		ObservableList<CodedCell> codedCellsList = FXCollections.observableArrayList();
		
		
		  //Iterator it = sheet.getCellComments().entrySet().iterator();
		  Map<CellAddress, XSSFComment> map = sheet.getCellComments();
		  int uncodedCommentCount = 0;
		  for (Map.Entry<CellAddress, XSSFComment> entry : map.entrySet()) {
			    String key = entry.getKey().toString();
			    String val = entry.getValue().getString().toString();

			    Model.log.info("key = " + key + " val = " + val);
	
			    if (val.startsWith(Main.delimiter)){
			    	codedCellsList.add(new CodedCell(key, val));	
			    } else {
			    	uncodedCommentCount++;
			    }
			}
		  Model.log.info(uncodedCommentCount + " adet kodlanmamış yorum mevcut");
	
		  
		/*
		 * = sheet.getCellComments(); while (it.hasNext()) { Map.Entry pair = } for
		 * (Iterator iterator = codedCellsList.iterator(); iterator.hasNext();) {
		 * CodedCell codedCell = (CodedCell) iterator.next();
		 * 
		 * })
		 */
		 

		//codedCellsList.add(new CodedCell("C2", "???deneme???deneme1"));
		//codedCellsList.add(new CodedCell("C3", "???deneme???deneme2"));
		return codedCellsList;
	}

	public void selectTemplate(ActionEvent event) throws IOException {
		FileChooser fc = new FileChooser();
		// User should only be able to import xlsx files (maybe add possibility to
		// convert xls to xlsx aswell)
		// maybe let them add multiple files in the future.
		fc.getExtensionFilters().add(new ExtensionFilter("XLSX dosyaları", "*.xlsx"));
		selectedFile = fc.showOpenDialog(null);
		if (selectedFile != null) {

			lblSelectedFile.setVisible(false);
			lblFileName.setText(selectedFile.getName());
			lblFilePath.setText(selectedFile.getAbsolutePath());
			// lblFileSize.setText(String.valueOf(selectedFile.getTotalSpace()));

			lblFileSize.setText(Model.getFileSizeKiloByteString(selectedFile));
			FileInputStream fis = new FileInputStream(selectedFile);
			wb = new XSSFWorkbook(fis);

			// We only get the first sheet, If the file has multiple sheets only the first
			// will be imported!!!
			int sheetNo = wb.getNumberOfSheets();
			lblSheet.setText(String.valueOf(sheetNo));
			sheet = wb.getSheetAt(0);
			buttonSaveTemplate.setDisable(false);
			updateTable();

		} else {
			Model.createPopup(rootPane, "Dosya seçilemedi!", null, Level.WARNING);
			
		}

	}

	public void saveTemplate(ActionEvent event) throws IOException {
		if (lblSelectedFile.isVisible() == false) {

			int lastRowIndex = sheet.getLastRowNum();
			int noRows = lastRowIndex + 1;
			Model.log.info(noRows + " tane satır içeren dosya kaydediliyor.");
			// sheet.getRow(0).getCell(0).setCellValue("şablondur");
			// Maybe add something before the file name to show that its a template
			FileOutputStream fos = new FileOutputStream("./report-templates/" + selectedFile.getName());
			wb.write(fos);
			fos.flush();
			fos.close();
			wb.close();
			buttonSaveTemplate.setDisable(true);

			Model.log.fine("Seçili dosya kaydedildi");
		} else {
			Model.createPopup(rootPane, "Önce bir dosya seçiniz", null, Level.WARNING);
		}

	}

	public void modeRegister() {
		rbRegister.setSelected(true);
	
		Model.log.info("Yeni kullanıcı kaydetme moduna geçiliyor.");
		passwordInput.setDisable(false);
		password2Input.setDisable(false);
		buttonRemove.setVisible(false);
		passwordInput.setText(null);
		password2Input.setText(null);
		usernameInput.setText(null);
		levelInput.setText(null);
		dateInput.setValue(null);
		nameInput.setText(null);
		surnameInput.setText(null);

	}

	public void removePersonnel(ActionEvent event) {
		if (DatabaseAndSession.remove(personnelid)) {
			Model.createPopup(rootPane, "Hesap başarıyla silindi", null, Level.FINE);
			areaOutput.appendText("Hesap başarıyla silindi");
		} else {
	
			Model.createPopup(rootPane, "Hesap silinirken hatayla karşılaşıldı ve silinemedi.", null, Level.WARNING);
			areaOutput.appendText("Hesap silinirken hatayla karşılaşıldı ve silinemedi.");

		}
		modeRegister();
	}

	public void listPersonnel(ActionEvent event) {
		ResultSet rs = DatabaseAndSession.returnAllPersonnel();
		areaOutput.setText("");
		try {
			while (rs.next()) {
				for (int i = 1; i < 8; i++) {
					areaOutput.appendText(rs.getString(i) + " | ");

				}
				areaOutput.appendText("\n");
			}
		} catch (SQLException e) {
	
			Model.createErrorPopup(rootPane, "Veritabanından alınan elemanlar sıralanamadı", null, e);
			//e.printStackTrace();
		}
	}

	public void getPersonnelInfo(ActionEvent event) {
		count++;

		areaOutput.setText("");
		areaOutput.appendText("Personel bilgileri getiriliyor\n");
		//System.out.println("Personel bilgileri getiriliyor");
		rbChange.setSelected(true);
		passwordInput.setDisable(true);
		password2Input.setDisable(true);
		passwordInput.setText("Değiştirilemez");
		password2Input.setText("Değiştirilemez");

		ResultSet rs = DatabaseAndSession.returnPersonnel(changeInput.getText());
		try {
			if (rs.next()) {
				personnelid = rs.getInt(1);
				usernameInput.setText(rs.getString(2));
				levelInput.setText(rs.getString(4));
				Date ld = rs.getDate(5);
				if (ld != null) {
					dateInput.setValue(ld.toLocalDate());
				} else {
					dateInput.setValue(null);
				}

				nameInput.setText(rs.getString(6));
				surnameInput.setText(rs.getString(7));

			} else {
				areaOutput.appendText("Bu kullanıcı adına sahip kişi (personel veya admin) bulunamadı\n");
			}

		} catch (SQLException e) {
			Model.createErrorPopup(rootPane, "Veritabanından alınan elemanlar sıralanamadı", null, e);
			//e.printStackTrace();
		}
		buttonRemove.setVisible(true);
	}

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
		if (name != null) {
			if (name.contentEquals("")) {
				name = null;
			}
		}
		if (surname != null) {
			if (surname.contentEquals("")) {
				surname = null;
			}

		}

		if (rbAdmin.isSelected()) {
			isAdmin = true;
		}
		if (password.length() < 2) {
			// They may want to have easier passwords, its a small company anyways...
			areaOutput.appendText("Lütfen 2 karakterden daha uzun bir şifre giriniz.\n");
			inputOK = false;
		}
		if (!password.equals(password2Input.getText())) {
			areaOutput.appendText("Şifreleriniz birbiriyle eşleşmiyor\n");
			inputOK = false;
		}
		if (username.length() < 2) {
			areaOutput.appendText("Lütfen 2 karakterden daha uzun bir kullanıcı adı giriniz.\n");
			inputOK = false;
		}
		if (!Model.isPositiveIntegerOrNull(levelstr)) {
			areaOutput.appendText("Lütfen seviye bölümüne bir sayı giriniz veya bölümü boş bırakınız.\n");
			inputOK = false;
		} else if (levelstr != null && !levelstr.equals("")) {
			level = Integer.parseInt(levelstr);
		}

		if (dateInput.getValue() != null) {
			certificatedate = Date.valueOf(dateInput.getValue());
		}

		if (inputOK) {
			if (rbRegister.isSelected()) {
				if (DatabaseAndSession.register(username, password, level, certificatedate, name, surname, isAdmin)) {
					areaOutput.appendText("Hesap başarıyla oluşturuldu.\n");
				} else {
					areaOutput.appendText("Hesap oluşturulamadı. (Kullanıcı adı kişiye hastır)\n");
				}

			} else {
				if (DatabaseAndSession.update(personnelid, username, level, certificatedate, name, surname, isAdmin)) {
					areaOutput.appendText("Hesap başarıyla güncellendi.\n");
				} else {
					areaOutput.appendText(
							"Hesap güncellenemedi (Kullanıcı adı başkası tarafından kullanılıyor olabilir)\n");
				}
			}
		}

	}

	public void changePane(ActionEvent event) {
		count++;

		//System.out.println(event.getSource().toString());
		// Button[id=buttonRegister, styleClass=button]'Personel / Admin kaydet'
		if (event.getSource() == buttonRegister) {
			paneRegister.toFront();

			paneFieldVal.setVisible(false);
			paneDB.setVisible(false);
			paneTemplate.setVisible(false);
			paneRegister.setVisible(true);
		} else if (event.getSource() == buttonFieldVal) {
			paneFieldVal.toFront();

			paneDB.setVisible(false);
			paneTemplate.setVisible(false);
			paneRegister.setVisible(false);
			paneFieldVal.setVisible(true);
		} else if (event.getSource() == buttonDB) {
			paneDB.toFront();

			paneTemplate.setVisible(false);
			paneRegister.setVisible(false);
			paneFieldVal.setVisible(false);
			paneDB.setVisible(true);
		} else if (event.getSource() == buttonTemplate) {
			paneTemplate.toFront();

			paneRegister.setVisible(false);
			paneFieldVal.setVisible(false);
			paneDB.setVisible(false);
			paneTemplate.setVisible(true);
		}
	}
	public void closeAll() {
		Model.closeAll();
	}

}
