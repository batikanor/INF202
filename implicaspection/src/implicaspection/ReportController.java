package implicaspection;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;



public class ReportController extends ControllerTemplate{
	
	private File selectedFile;
	private XSSFWorkbook wb;
	private XSSFSheet sheet;

	public void openTemplate(ActionEvent event) throws IOException {
		FileChooser fc = new FileChooser();
		// User should only be able to import xlsx files (maybe add possibility to convert xls to xlsx aswell)
		// maybe let them add multiple files in the future.
		fc.getExtensionFilters().add( new ExtensionFilter("XLSX dosyaları", "*.xlsx"));
		fc.setInitialDirectory(new File("./report-templates"));
		selectedFile = fc.showOpenDialog(null);
		if (selectedFile != null) {
			FileInputStream fis = new FileInputStream(selectedFile);
			wb = new XSSFWorkbook(fis);
			// We only get the first sheet, If the file has multiple sheets only the first will be imported!!!
			sheet = wb.getSheetAt(0);

			
		} else {
			System.out.println("Dosya açılamadı");
		}

	}

	public void closeEverythıng(ActionEvent event) {
		count++;
		Model.closeAll();
	}
}
