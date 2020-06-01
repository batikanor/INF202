package implicaspection;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import utilities.DatabaseAndSession;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;



public class ReportController extends ControllerTemplate{
	
	private File selectedFile;
	private XSSFWorkbook wb;
	private XSSFSheet sheet;
	@FXML GridPane gridPane;
	// String fieldName, String fieldContent
	public static HashMap<String, String> contentsMap = new HashMap<String, String>();
	// As soon as a field has been updated, update the contentsMap with listeners aswell!!! 
	// String fieldName, int fieldLocation
	public static HashMap<String, Integer> locationsMap = new HashMap<String, Integer>();
	
	
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
			
			String fileName = selectedFile.getName();
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
								comment = comment.substring(3);
								String commentParts[] = comment.split("\\?\\?\\?");
								// commentsParts[0] should be type of entry
								VBox celly;
								String fieldName;
								String meaningStr = commentParts[1];
								Label meaning = new Label(meaningStr);
								TextField textContent;
								String strContent;
								int intContent;
								Label cell = new Label("Sütun: " + Model.GetExcelColumnString(j + 1) + " Satır: " + (i + 1));
								ComboBox<String> comboContent;
								Spinner<Integer> percentage;
								
								if (commentParts[0].contentEquals("default")) {
									celly = new VBox();
								
									
									fieldName= commentParts[2];
									strContent = commentParts[3];
									textContent = new TextField(strContent);
									contentsMap.put(fieldName, strContent);
									textContent.textProperty().addListener( (v, oldValue, newValue) -> {
										System.out.println("on " + meaningStr + " " + oldValue + " is now " + newValue);
										contentsMap.put(fieldName, newValue);
									});
									celly.getChildren().addAll(cell, meaning, textContent);
									gridPane.add(celly, cellsUsedInRow , rowsUsed);
									
								} else if (commentParts[0].contentEquals("percent")) {
									celly = new VBox();
							
									fieldName= commentParts[2];
									strContent = commentParts[3];
									intContent = Integer.parseInt(strContent);
									SpinnerValueFactory<Integer> percVals = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, intContent);
									percentage = new Spinner<Integer>();
									percentage.setValueFactory(percVals);
									contentsMap.put(fieldName, strContent);
									percentage.valueProperty().addListener( (v, oldValue, newValue) -> {
										System.out.println("on " + meaningStr + " " + oldValue + " is now " + newValue);
										contentsMap.put(fieldName, newValue.toString());
										// Assumption : Other fields do not depend on percent fields!!!!
									});
									celly.getChildren().addAll(cell, meaning, percentage);
									gridPane.add(celly, cellsUsedInRow , rowsUsed);
									
									
								} else if (commentParts[0].contentEquals("combo")) {
									celly = new VBox();
									// Get data from DB and load them to combobox
									comboContent = DatabaseAndSession.returnComboBoxValues(commentParts[2]);
									// You may need to set it to a default value in some occasions, an if check here could do it
									celly.getChildren().addAll(cell, meaning, comboContent);
									gridPane.add(celly, cellsUsedInRow , rowsUsed);
									
								} else if (commentParts[0].contentEquals("depend")) {
									celly = new VBox();
									meaning = new Label(commentParts[1]);
									
									// Get data from DB and load them to combobox
									comboContent = DatabaseAndSession.returnComboBoxValues(commentParts[2]);
									// You may need to set it to a default value in some occasions, an if check here could do it
									
									
									celly.getChildren().addAll(cell, meaning, comboContent);
									gridPane.add(celly, cellsUsedInRow , rowsUsed);
									
									// If combobox is opened again. refresh these !
								}
								
								cellsUsedInRow++;
								rowUsed = true;
										
							} else {
								
								// this cell has a comment that isnt coded for this programm
							}
						} else {
							// this cel doesnt have a comment
						}
					} else {
						// this cell is somehow null
					}
				}
				if (rowUsed) {
					rowsUsed++;
				}

			

				
			}
			//XSSFComment commentExported = sheet.getRow(0).getCell(0).getCellComment();
			//commentExported.setString("exported");
			//^doesnt work if null


			//sheet.getRow(0).getCell(0).setCellValue("exported");
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");  
		    Date date = new Date();
		    String dateStr = formatter.format(date);
					FileOutputStream fos = new FileOutputStream("./report-exports/" + fileName.substring(0, fileName.lastIndexOf(".")) + dateStr + ".xlsx");
					wb.write(fos);
					fos.flush();
					fos.close();
					wb.close();
					
		} else {
			System.out.println("Dosya açılamadı");
		}

		
		// Traverse all rows on sheet, for every row make a slidable list of items, draw the items depending on what TYPE of field they are...
	}

	public void closeEverythıng(ActionEvent event) {
		for(Map.Entry<String, String> entry : contentsMap.entrySet()) {
			System.out.println("field name: " + entry.getKey() + " __ field content: " + entry.getValue());
		}
		count++;
		Model.closeAll();
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

