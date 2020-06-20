package implicaspection;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import java.util.logging.Level;

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
import javafx.scene.layout.BorderPane;



public class ReportController extends ControllerTemplate{

	public boolean workbookTraversed = false;
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
	@FXML BorderPane rootPane;
	
	// Grid locations of dependants


	
	private int updateDependants(String decisiveName) {
		// WHAT HAPPENS IF THE DECISIVE FIELD IS NOT PRESENT ON THE REPORT?

		if (dependenceMap.keySet().contains(decisiveName)) {
			//System.out.println(decisiveName);
			ArrayList<String> dependantNames = dependenceMap.get(decisiveName);
			if (dependantNames != null) {
				
				for(String dependantName : dependantNames) {
					
					for (Node m : gridPane.getChildren()) {


						if (dependantName.equals(m.getUserData())) {
							
							int row = GridPane.getRowIndex(m);
							int col = GridPane.getColumnIndex(m);
							VBox cellToUpdate = (VBox) m;
							VBox newVBox = new VBox();
							newVBox.setUserData(dependantName);
			
							newVBox.getChildren().addAll(cellToUpdate.getChildren());
							gridPane.getChildren().remove(m);
							newVBox.getChildren().remove(2);
							
							ComboBox<String> newCombo = DatabaseAndSession.returnDependantValues(dependantName);
							if (newCombo == null) {
								// decisive doesnt exist
								return -1;
							}
							
							newVBox.getChildren().add(newCombo);
							gridPane.add(newVBox, col, row);
						
							contentsMap.put(dependantName, newCombo.getSelectionModel().getSelectedItem());
						
							
							
							newCombo.valueProperty().addListener( (v, oldValue, newValue) -> {
								Model.log.info("şu bölgede: " + dependantName + " şu değer: " + oldValue +  " şu değer oldu: " + newValue);
								contentsMap.put(dependantName, newValue);
							});
						
							break;
						}
					}
				}
			} else {
				System.out.println(decisiveName + " bolgesine bagli alan bulunamadi");
			}
			return 1;
		} else {
			// decisive (independant) field is not (yet) present on the workbook
			// if this happens before the whole workbook had been traversed, ok (dont handle the returned val).
			return -1;
		}
		

	}
	public void openTemplate(ActionEvent event) throws IOException {
	   //ColumnConstraints colConstraint = new ColumnConstraints(120);
	   //colConstraint.setHalignment(HPos.LEFT);

	   //RowConstraints rowConstraints = new RowConstraints(130);
	   //rowConstraints.setValignment(VPos.CENTER);
	    
	

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
								VBox celly = new VBox();
								
								final String fieldType = commentParts[0];
								final String meaningStr = commentParts[1] + " (" + commentParts[2] + ")";
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
								
								if (fieldType.contentEquals("default")) {
									//celly = new VBox();
									//celly.setUserData(fieldName);
									strContent = commentParts[3];
									textContent = new TextField(strContent);
									contentsMap.put(fieldName, strContent);
									celly.getChildren().addAll(cell, meaning, textContent);
									gridPane.add(celly, cellsUsedInRow , rowsUsed);
									
									textContent.textProperty().addListener( (v, oldValue, newValue) -> {
										Model.log.info("şu bölgede: " + fieldName + " şu değer: " + oldValue +  " şu değer oldu: " + newValue);
										contentsMap.put(fieldName, newValue);
										updateDependants(fieldName);
									});
									
								} else if (fieldType.contentEquals("percent")) {
									//celly = new VBox();
									meaning.setText(meaning.getText() + " (%)");
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
										Model.log.info("şu bölgede: " + fieldName + " şu değer: " + oldValue +  " şu değer oldu: " + newValue);
										contentsMap.put(fieldName, newValue.toString());
										// Assumption : Other fields do not depend on percent fields!!!!
									});
									
								} else if (commentParts[0].contentEquals("combo")) {
									//celly = new VBox();
									//celly.setUserData(fieldName);
									// Get data from DB and load them to combobox
									comboContent = DatabaseAndSession.returnComboBoxValues(fieldName);
									
									// You may need to set it to a default value in some occasions, an if check here could do it
									if (comboContent.getItems().size() == 1) { 
										comboContent.getSelectionModel().selectFirst();
										contentsMap.put(fieldName, comboContent.getSelectionModel().getSelectedItem().toString());
										// You also need to UPDATE THEM, if there are any dependant fields in this case!!!
										updateDependants(fieldName);
									} else {
										// put null into the hash table as val, so that it is forced to change before exporting
										contentsMap.put(fieldName, null); ///< put e.g. 'Deneme' instead of null if u want to be able to export it during development		
									}
									

									celly.getChildren().addAll(cell, meaning, comboContent);
									gridPane.add(celly, cellsUsedInRow , rowsUsed);
									
									comboContent.valueProperty().addListener( (v, oldValue, newValue) -> {
										Model.log.info("şu bölgede: " + fieldName + " şu değer: " + oldValue +  " şu değer oldu: " + newValue);
										contentsMap.put(fieldName, newValue);
										updateDependants(fieldName);
									});
					
									
									
								} else if (fieldType.contentEquals("depend")) {
									// Assumption : fields do not depend on percent or special fields!!!!
									//celly = new VBox();
									celly.setUserData(fieldName);
									
									// Get data from DB and load them to combobox, user data of newCombo is also set to the decisiveName with the line below
									comboContent = DatabaseAndSession.returnDependantValues(fieldName); 
									// You may need to set it to a default value in some occasions, an if check here could do it
									
									if (comboContent == null) {
										System.out.println("alan" + fieldName + " doldurulamadığından export alınamayacaktı, onun yerine alan hiç eklenmedi");
										continue;
									}
									
									meaning.setText(meaningStr + " --(baglidir)--> " + comboContent.getUserData() );
									// Maybe gray it out and say 'once su alani doldurun' until theres a value on that place (u can check from hashmap)
									
									celly.getChildren().addAll(cell, meaning, comboContent);
									gridPane.add(celly, cellsUsedInRow , rowsUsed);

									comboContent.valueProperty().addListener( (v, oldValue, newValue) -> {
										Model.log.info("şu bölgede: " + fieldName + " şu değer: " + oldValue +  " şu değer oldu: " + newValue);
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
								} else if (fieldType.contentEquals("special")) {
									// Assumption : Other fields do not depend on special fields!!!!
									// Special fields will be handled here, e.g. the client wants the report no. in a certain format.
									
									
									
									if (fieldName.contentEquals("rapor-no")) {
										meaning.setText(meaning.getText() + " (yyyyMMdd??)");
										final int reportNoLength = 10;
										SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");  
									    Date date = new Date();
									    String dateStr = formatter.format(date);
									    // 'rapor isterleri nin ne oldugunu bilmedigimden son 2 haneyi manuel istiyorum'
									    strContent = dateStr + "??";
									    textContent = new TextField(strContent);
										
									    // cuz ?? is not a number
									    contentsMap.put(fieldName, null);
										
									    celly.getChildren().addAll(cell, meaning, textContent);
										gridPane.add(celly, cellsUsedInRow , rowsUsed);
										textContent.focusedProperty().addListener( (v, oldValue, newValue) -> {
											if (newValue == false) {
												String newContent = textContent.getText();
												
												String changeProblem;
												if (newContent.length() != reportNoLength) {
													contentsMap.put(fieldName, null);
													changeProblem = fieldName + " bölgesindeki " + newContent + " 10 haneli bir pozitif sayı olmalıdır";
													Model.createPopup(rootPane, changeProblem, null, Level.WARNING);
												} else {
													try {
														
														Integer.parseUnsignedInt(newContent);
														
														contentsMap.put(fieldName, newContent);
														Model.log.info("şu bölgede: " + fieldName + " yeni değer şu değer oldu: " + newContent);
													} catch (NumberFormatException nfe) {
														contentsMap.put(fieldName, null);
														changeProblem = fieldName + " bölgesindeki " + newContent + " pozitif bir sayı olmalıdır";
														Model.createErrorPopup(rootPane, changeProblem, null, nfe);
													} 
												}
											}
										});
									}
									
									else if (fieldName.contentEquals("rapor-tarihi")) {
										SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/YYYY");  
									    Date date = new Date();
									    String dateStr = formatter.format(date);
									    System.out.println(dateStr);
									    Label dateLabel = new Label(dateStr);
									    celly.getChildren().addAll(cell, meaning, dateLabel);
										gridPane.add(celly, cellsUsedInRow , rowsUsed);
										contentsMap.put(fieldName, dateStr);
									}
									//else if ()
									
								}
								
								cellsUsedInRow++;
								rowUsed = true;
										
							} else {
								// this cell has a comment that starts with "???"
							}
						} else {
							// this cell doesn't have a comment
						}
					} else {
						// this cell is somehow null
					}
				}
				if (rowUsed) {
					rowsUsed++;
				}

			

				
			}
			// All the fields had been traversed.. now try to update the dependant fields, they will update if the field they depend on had somehow been filled.
			for (String decisiveName : dependenceMap.keySet()) {
				if (!contentsMap.containsKey(decisiveName)) {
					// Decisive field doesnt exist on the workbook, therefore it is impossible to complete
					String errStr = "En az bir bölgenin bağlı olduğu alan " + decisiveName + " rapor şablonunda bulunamadı. Dolayısıyla export alınamaz.";
					Model.createErrorPopup(rootPane, errStr , null, null);
					wb.close();
					rootPane.getScene().getWindow().hide();
				} else if (updateDependants(decisiveName) == -1) {
					// on DatabaseAndSession.java >//System.out.println(dependantName + " in bagli oldugu bir hucre yok, ama bagimli hucre olarak gosterilmis");
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
			System.out.println(fieldContent);
			if(fieldContent == null) {
				//String warnPop = "Doldurulmasi gereken seçmeli değerlerden " + fieldName + " doldurulamadı";
				//Model.createPopup(rootPane, warnPop, null, Level.WARNING);
				//return false;
				String fieldLocation[] = locationsMap.get(fieldName).split("\\?\\?\\?");
				int i = Integer.parseInt(fieldLocation[0]);
				int j = Integer.parseInt(fieldLocation[1]);
				// put the content to the cell at given location
				sheet.getRow(i).getCell(j).setCellValue("buveonceki3satirisil");
				continue;
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
		String infoPop = "Doldurma işlemi başarıyla tamamlandı";
		Model.log.info(infoPop);
		

		return false;
	}
	public void closeEverythıng(ActionEvent event) throws IOException {
		//exportXLSX();
		if(wb != null) {
			wb.close();
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

