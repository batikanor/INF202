package objects;

import javafx.beans.property.SimpleStringProperty;

public class CodedCell {
	private SimpleStringProperty cellLocation, cellCode;

	public CodedCell(String cellLocation, String cellCode) {
		super();
		this.cellLocation = new SimpleStringProperty(cellLocation);
		this.cellCode = new SimpleStringProperty(cellCode);
	}

	public String getCellLocation() {
		return cellLocation.get();
	}

	public void setCellLocation(SimpleStringProperty cellLocation) {
		this.cellLocation = cellLocation;
	}

	public String getCellCode() {
		return cellCode.get();
	}

	public void setCellCode(SimpleStringProperty cellCode) {
		this.cellCode = cellCode;
	}
	

}
