package implicaspection;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;



public class ReportController extends ControllerTemplate{
	
	private File selectedFile;
	private XSSFWorkbook wb;
	private XSSFSheet sheet;
	@FXML GridPane gridPane;

	
	public void openTemplate(ActionEvent event) throws IOException {
	   // ColumnConstraints colConstraint = new ColumnConstraints(120);
	    //colConstraint.setHalignment(HPos.LEFT);

	   // RowConstraints rowConstraints = new RowConstraints(130);
	   // rowConstraints.setValignment(VPos.CENTER);
	    
	

	    // add constraints for columns
	    //gridPane.getColumnConstraints().addAll(colConstraint, colConstraint, colConstraint);

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
			
			int rowCount = sheet.getLastRowNum();
			int rowsUsed = 0;
			
			for (int i = 0; i - 1< rowCount; i++) {
				boolean rowUsed = false;
				int cellCount = sheet.getRow(i).getLastCellNum();
				int cellsUsedInRow = 0;
				for (int j = 0; j - 1< cellCount; j++) {
					if (sheet.getRow(i).getCell(j) != null) {
						
						if ((sheet.getRow(i).getCell(j).getCellComment() != null)){
							String comment = sheet.getRow(i).getCell(j).getCellComment().getString().toString();
							//System.out.println(comment);
							if (comment.startsWith("???")){
								gridPane.add(new TextField("buraDolacak"), cellsUsedInRow , rowsUsed);
								cellsUsedInRow++;
								rowUsed = true;
								
								
							}
						}
						
						//System.out.println((sheet.getRow(i).getCell(j).getCellType().toString()));
						/*
						if(sheet.getRow(i).getCell(j).getCellType() == CellType.STRING ) {
							//System.out.println(sheet.getRow(i).getCell(j).getStringCellValue());
							System.out.println(sheet.getRow(i).getCell(j).getRichStringCellValue());
							if(sheet.getRow(i).getCell(j).getRichStringCellValue().getString().equals("HAHA")){
								System.out.println("hey");
								gridPane.add(new TextArea("99999999999"), j, i);
							} else {
								gridPane.add(new TextArea("888"), j, i);
				
							}
						} else {
							gridPane.add(new TextArea("Bu alanda metinden baska birsey var"), j, i, 2, 2);
						}
						*/
		
					}
					// Cell is null

					
				}
				if (rowUsed) {
					rowsUsed++;
				}
				
			}
			
		} else {
			System.out.println("Dosya açılamadı");
		}

		
		// Traverse all rows on sheet, for every row make a slidable list of items, draw the items depending on what TYPE of field they are...
	}

	public void closeEverythıng(ActionEvent event) {
		count++;
		Model.closeAll();
	}
}
