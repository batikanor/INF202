package implicaspection;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;



public class ReportController extends ControllerTemplate{
	
	String fileName;
	private File selectedFile;
	private XSSFWorkbook wb;
	private XSSFSheet sheet;
	private FileOutputStream fos;
	@FXML GridPane gridPane;
	// String fieldName, String fieldContent
	public static HashMap<String, String> contentsMap = new HashMap<String, String>();
	// As soon as a field has been updated, update the contentsMap with listeners aswell!!! 
	// String fieldName, String fieldLocation (rowNo???cellNo)
	public static HashMap<String, String> locationsMap = new HashMap<String, String>();
	
	// String decisiveName, String dependantName
	// There may be multiple dependants to a decisive field.
	public static HashMap<String, ArrayList<String>> dependenceMap = new HashMap<String, ArrayList<String>>();
	
	// Grid locations of dependants


	
	private void updateDependants(String decisiveName) {
		System.out.println(1);
		if (dependenceMap.keySet().contains(decisiveName)) {
			System.out.println(decisiveName);
			ArrayList<String> dependantNames = dependenceMap.get(decisiveName);
			if (dependantNames != null) {
				
				for(String dependantName : dependantNames) {
					System.out.println(dependantName);
					
					for (Node m : gridPane.getChildren()) {

						
						System.out.println(dependantName);
						System.out.println(m.getUserData());
						if (dependantName.equals(m.getUserData())) {
							
							int row = GridPane.getRowIndex(m);
							int col = gridPane.getColumnIndex(m);
							VBox cellToUpdate = (VBox) m;
							VBox newVBox = new VBox();
							newVBox.setUserData(dependantName);
							System.out.println(9);
							newVBox.getChildren().addAll(cellToUpdate.getChildren());
							gridPane.getChildren().remove(m);
							newVBox.getChildren().remove(2);
							ComboBox<String> newCombo = DatabaseAndSession.returnDependantValues(dependantName);
							System.out.println(4);
							newVBox.getChildren().add(newCombo);
							gridPane.add(newVBox, col, row);
							contentsMap.put(dependantName, null);
							
							newCombo.valueProperty().addListener( (v, oldValue, newValue) -> {
								System.out.println("şu bölgede: " + dependantName + " şu değer: " + oldValue +  " şu değer oldu: " + newValue);
								contentsMap.put(dependantName, newValue);
							});
							System.out.println(99);
							break;
						}
					}
				}
			}
			
		}
	}
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
			
			fileName = selectedFile.getName();
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
								
								String meaningStr = commentParts[1];
								String fieldName = commentParts[2];
								String fieldLocation = i + "???" + j;
							
								int len = commentParts.length;
								commentParts[len - 1] = commentParts[len - 1].stripTrailing();
								
								
								locationsMap.put(fieldName, fieldLocation);
								Label meaning = new Label(meaningStr);
								TextField textContent;
								String strContent;
								int intContent;
								Label cell = new Label("Sütun: " + Model.GetExcelColumnString(j + 1) + " Satır: " + (i + 1));
								ComboBox<String> comboContent;
								Spinner<Integer> percentage;
								
								if (commentParts[0].contentEquals("default")) {
									celly = new VBox();
									//celly.setUserData(fieldName);
									strContent = commentParts[3];
									textContent = new TextField(strContent);
									contentsMap.put(fieldName, strContent);
									celly.getChildren().addAll(cell, meaning, textContent);
									gridPane.add(celly, cellsUsedInRow , rowsUsed);
									
									textContent.textProperty().addListener( (v, oldValue, newValue) -> {
										System.out.println("şu bölgede: " + meaningStr + " şu değer: " + oldValue +  " şu değer oldu: " + newValue);
										contentsMap.put(fieldName, newValue);
										updateDependants(fieldName);
									});
									
								} else if (commentParts[0].contentEquals("percent")) {
									celly = new VBox();
							
									//celly.setUserData(fieldName);
									strContent = commentParts[3];
									intContent = Integer.parseInt(strContent);
									SpinnerValueFactory<Integer> percVals = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, intContent);
									percentage = new Spinner<Integer>();
									percentage.setValueFactory(percVals);
									contentsMap.put(fieldName, strContent + "%");
									celly.getChildren().addAll(cell, meaning, percentage);
									gridPane.add(celly, cellsUsedInRow , rowsUsed);
									
									percentage.valueProperty().addListener( (v, oldValue, newValue) -> {
										System.out.println("şu bölgede: " + meaningStr + " şu değer: " + oldValue +  " şu değer oldu: " + newValue);
										contentsMap.put(fieldName, newValue.toString());
										// Assumption : Other fields do not depend on percent fields!!!!
									});
									
								} else if (commentParts[0].contentEquals("combo")) {
									celly = new VBox();
									//celly.setUserData(fieldName);
									// Get data from DB and load them to combobox
									comboContent = DatabaseAndSession.returnComboBoxValues(fieldName);
									// You may need to set it to a default value in some occasions, an if check here could do it
									boolean comboWithDefault = false;
									if(comboWithDefault) {
										// set initial val with default val
									} else {
										// put null into the hash table as val, so that it is forced to change before exporting
										contentsMap.put(fieldName, "Deneme");
									}
									celly.getChildren().addAll(cell, meaning, comboContent);
									gridPane.add(celly, cellsUsedInRow , rowsUsed);
									
									comboContent.valueProperty().addListener( (v, oldValue, newValue) -> {
										System.out.println("şu bölgede: " + meaningStr + " şu değer: " + oldValue +  " şu değer oldu: " + newValue);
										contentsMap.put(fieldName, newValue);
										updateDependants(fieldName);
									});
					
									
									
								} else if (commentParts[0].contentEquals("depend")) {
									celly = new VBox();
									celly.setUserData(fieldName);
									
									// Get data from DB and load them to combobox
									comboContent = DatabaseAndSession.returnDependantValues(fieldName);
									// You may need to set it to a default value in some occasions, an if check here could do it
									
									if (comboContent == null) {
										System.out.println("alan doldurulamadığından export alınamayacaktı, onun yerine alan hiç eklenmedi");
										continue;
									}
									//comboContent.setUserData("combo");
									
									celly.getChildren().addAll(cell, meaning, comboContent);
									gridPane.add(celly, cellsUsedInRow , rowsUsed);

									comboContent.valueProperty().addListener( (v, oldValue, newValue) -> {
										System.out.println("şu bölgede: " + meaningStr + " şu değer: " + oldValue +  " şu değer oldu: " + newValue);
										contentsMap.put(fieldName, newValue);
									});
									/*
									 * contentsMap.values(). // If a decisive field's value is entered again,
									 * refresh these ! somehowAnEventListener(){ comboContent =
									 * DatabaseAndSession.returnDependantValues(fieldName); if (comboContent ==
									 * null) { System.out.
									 * println("alan doldurulamadığından export alınamayacaktı, onun yerine alan hiç eklenmedi"
									 * ); continue; } }
									 */
								}
								
								cellsUsedInRow++;
								rowUsed = true;
										
							} else {
								// this cell has a comment that starts with "???"
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

					
		} else {
			System.out.println("Dosya açılamadı");
		}

		
		// Traverse all rows on sheet, for every row make a slideable list of items, draw the items depending on what TYPE of field they are...
	}

	@FXML
	public boolean exportXLSX() throws IOException {
		// require import  
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");  
	    Date date = new Date();
	    String dateStr = formatter.format(date);

				
		for(Map.Entry<String, String> entry : contentsMap.entrySet()) {
			String fieldName =  entry.getKey();
			String fieldContent = entry.getValue();
			
			if(fieldContent == null) {
				System.out.println("Doldurulmasi gereken seçmeli değerlerden " + fieldName + " doldurulamadı");
				return false;
			}
			
			//System.out.println("field name: " + myKey + " __ field content: " + myVal);
			
			// Get location of the field
			String fieldLocation[] = locationsMap.get(fieldName).split("\\?\\?\\?");
			int i = Integer.parseInt(fieldLocation[0]);
			int j = Integer.parseInt(fieldLocation[1]);
			// put the content to the cell at given location
			sheet.getRow(i).getCell(j).setCellValue(fieldContent);
			

		}
		
		
		fos = new FileOutputStream("./report-exports/" + fileName.substring(0, fileName.lastIndexOf(".")) + dateStr + ".xlsx");
		wb.write(fos);
		fos.flush();
		fos.close();
		
		System.out.println("Doldurma işlemi başarıyla tamamlandı.");
		return false;
	}
	public void closeEverythıng(ActionEvent event) throws IOException {
		//exportXLSX();

		wb.close();
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

