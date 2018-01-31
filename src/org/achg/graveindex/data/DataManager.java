package org.achg.graveindex.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

public class DataManager {
	private static DataManager _instance = null;
	private InputFile _file = null;
	private List<IDataLoadListener> _loadListeners = new ArrayList<>();
	private List<IInputRecordListener> _inputListeners = new ArrayList<>();
	private List<IOutputRecordListener> _outputListeners = new ArrayList<>();

	private DataManager() {

	}

	public static synchronized DataManager getInstance() {
		if (_instance == null)
			_instance = new DataManager();
		return _instance;
	}

	public InputFile getInputFile() {
		return _file;
	}

	public void addLoadListener(IDataLoadListener listener) {
		_loadListeners.add(listener);
	}

	public void addInputRecordListener(IInputRecordListener listener) {
		_inputListeners.add(listener);
	}

	public void notifyListenersInputRecordIndexChanged() {
		for (IInputRecordListener listener : _inputListeners) {
			listener.currentIndexChanged();
		}
	}

	public void save() {
		if (_file != null) {
			List<String[]> data = _file.generateXlsxData();

			File myFile = new File(_file.getFileName().replace(".docx", ".xlsx"));
			if (!myFile.exists())
				try {
					myFile.createNewFile();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			XSSFWorkbook myWorkBook = new XSSFWorkbook();
			XSSFSheet mySheet = myWorkBook.createSheet();

			// Set to Iterate and add rows into XLS file

			// get the last row number to append new data
			int rownum = 1;

			for (String[] values : data) {

				// Creating a new Row in existing XLSX sheet
				Row row = mySheet.createRow(rownum++);
				int cellnum = 0;
				for (String obj : values) {
					Cell cell = row.createCell(cellnum++);

					cell.setCellValue((String) obj);
				}
			}

			// open an OutputStream to save written data into XLSX file
			try (FileOutputStream os = new FileOutputStream(myFile)) {
				myWorkBook.write(os);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				myWorkBook.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void loadFile(String file) {

		InputFile inputFile = new InputFile(file);

		File myFile = new File(file);
		try (FileInputStream fis = new FileInputStream(myFile)) {

			XWPFDocument document = new XWPFDocument(fis);

			for (XWPFTable table : document.getTables()) {
				for (XWPFTableRow row : table.getRows()) {
					InputRecord record = new InputRecord();

					for (XWPFTableCell cell : row.getTableCells()) {
						StringBuilder sb = new StringBuilder();
						for (XWPFParagraph para : cell.getParagraphs()) {
							sb.append(para.getParagraphText());
							sb.append(" / ");
						}
						record._cells.add(sb.toString());
					}

					inputFile._inputRecords.add(record);
				}
			}

			document.close();
		} catch (IOException ex) {

		}

		_file = inputFile;

		for (String columnHeader : _file._inputRecords.get(0)._cells) {
			_file._inputFields.add(new InputField(columnHeader));
		}

		for (IDataLoadListener listener : _loadListeners) {
			listener.notifyLoaded();
		}

		notifyListenersInputRecordIndexChanged();
	}

	public void addOutputRecordListener(IOutputRecordListener listener) {
		_outputListeners.add(listener);
	}

	public void notifyListenersOutputRecordsAvailable() {
		for (IOutputRecordListener listener : _outputListeners) {
			listener.notifyNewSetAvailable();
		}

	}

}
