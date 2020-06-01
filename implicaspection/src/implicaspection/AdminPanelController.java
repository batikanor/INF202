package implicaspection;

import java.io.File;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import objects.CodedCell;
import utilities.DatabaseAndSession;
import javafx.scene.control.Button;
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

import org.apache.poi.hssf.usermodel.examples.CellComments;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableView;

public class AdminPanelController extends ControllerTemplate {

	private int personnelid = -1; // id of the personnel we`re working with

	@FXML
	private Pane paneJobFirma;
	@FXML
	private Pane paneTemplate;
	@FXML
	private Pane paneDB;
	@FXML
	private Pane paneRegister;
	@FXML
	private Button buttonRegister;
	@FXML
	private Button buttonJobFirma;
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
	private XSSFWorkbook wb;
	private XSSFSheet sheet;

	@FXML
	ToggleGroup fieldType;

	// Configuring the table
	@FXML
	private TableView<CodedCell> fieldsTable;
	@FXML
	private TableColumn<CodedCell, String> locationColumn;
	@FXML
	private TableColumn<CodedCell, String> codeColumn;

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		super.initialize();
		locationColumn.setCellValueFactory(new PropertyValueFactory<CodedCell, String>("cellLocation"));
		codeColumn.setCellValueFactory(new PropertyValueFactory<CodedCell, String>("cellCode"));

	}

	@FXML
	private void updateTable() {
		fieldsTable.setItems(getCodedCells());
	}

	private ObservableList<CodedCell> getCodedCells() {
		// ObservableList is like ArrayList, but designed specifically for gui
		ObservableList<CodedCell> codedCellsList = FXCollections.observableArrayList();
		
		
		  //Iterator it = sheet.getCellComments().entrySet().iterator();
		  Map<CellAddress, XSSFComment> map = sheet.getCellComments();
		  int uncodedCommentCount = 0;
		  for (Map.Entry<CellAddress, XSSFComment> entry : map.entrySet()) {
			    String key = entry.getKey().toString();
			    String val = entry.getValue().getString().toString();
			    System.out.println("key = " + key + " val = " + val);
			    
	
			    if (val.startsWith("???")){
			    	codedCellsList.add(new CodedCell(key, val));	
			    } else {
			    	uncodedCommentCount++;
			    }
			}
		  System.out.println(uncodedCommentCount + " adet kodlanmamış yorum mevcut");
		  
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
			System.out.println("Dosya secilemedi");
		}

	}

	public void saveTemplate(ActionEvent event) throws IOException {
		if (lblSelectedFile.isVisible() == false) {

			int lastRowIndex = sheet.getLastRowNum();
			int noRows = lastRowIndex + 1;
			System.out.println(noRows + " tane satır içeren dosya kaydediliyor.");
			// sheet.getRow(0).getCell(0).setCellValue("şablondur");
			// Maybe add something before the file name to show that its a template
			FileOutputStream fos = new FileOutputStream("./report-templates/" + selectedFile.getName());
			wb.write(fos);
			fos.flush();
			fos.close();
			wb.close();
			buttonSaveTemplate.setDisable(true);

			System.out.println("Seçili dosya kaydedildi");
		} else {
			System.out.println("Önce bir dosya seçiniz");
		}

	}

	public void modeRegister() {
		rbRegister.setSelected(true);
		System.out.println("Yeni kullanıcı kaydetme moduna geçiliyor.");
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
			System.out.println("Hesap başarıyla silindi");
			areaOutput.appendText("Hesap başarıyla silindi");
		} else {
			System.out.println("Hesap silinirken hatayla karşılaşıldı ve silinemedi.");
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
			System.out.println("Veritabanından alınan elemanlar sıralanamadı");
			e.printStackTrace();
		}
	}

	public void getPersonnelInfo(ActionEvent event) {
		count++;

		areaOutput.setText("");
		areaOutput.appendText("Personel bilgileri getiriliyor\n");
		System.out.println("Personel bilgileri getiriliyor");
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
			System.out.println("Veritabanından alınan elemanlar sıralanamadı");
			e.printStackTrace();
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

		// System.out.println(event.getSource().toString());
		// Button[id=buttonRegister, styleClass=button]'Personel / Admin kaydet'
		if (event.getSource() == buttonRegister) {
			paneRegister.toFront();

			paneJobFirma.setVisible(false);
			paneDB.setVisible(false);
			paneTemplate.setVisible(false);
			paneRegister.setVisible(true);
		} else if (event.getSource() == buttonJobFirma) {
			paneJobFirma.toFront();

			paneDB.setVisible(false);
			paneTemplate.setVisible(false);
			paneRegister.setVisible(false);
			paneJobFirma.setVisible(true);
		} else if (event.getSource() == buttonDB) {
			paneDB.toFront();

			paneTemplate.setVisible(false);
			paneRegister.setVisible(false);
			paneJobFirma.setVisible(false);
			paneDB.setVisible(true);
		} else if (event.getSource() == buttonTemplate) {
			paneTemplate.toFront();

			paneRegister.setVisible(false);
			paneJobFirma.setVisible(false);
			paneDB.setVisible(false);
			paneTemplate.setVisible(true);
		}
	}

}
