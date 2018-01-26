package org.achg.graveindex.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
	
	private DataManager() {

	}
	


	public static synchronized DataManager getInstance() {
		if (_instance == null)
			_instance = new DataManager();
		return _instance;
	}
	
	public InputFile getInputFile ()
	{
		return _file;
	}

	public void addLoadListener(IDataLoadListener listener)
	{
		_loadListeners.add(listener);
	}
	
	public void addInputRecordListener(IInputRecordListener listener)
	{
		_inputListeners.add(listener);
	}
	
	public void notifyListenersInputRecordIndexChanged()
	{
		for (IInputRecordListener listener: _inputListeners)
		{
			listener.currentIndexChanged();
		}
	}
	
	public void save() {
		// TODO Auto-generated method stub

	}

	public void loadFile(String file) {

		InputFile inputFile = new InputFile(file);

		File myFile = new File(file);
		try (FileInputStream fis = new FileInputStream(myFile)) {

			XWPFDocument document = new  XWPFDocument(fis);
			
			for (XWPFTable table: document.getTables())
			{
				for (XWPFTableRow row:table.getRows())
				{
					InputRecord record = new InputRecord();
					
					
					
					for (XWPFTableCell cell:row.getTableCells())
					{
						StringBuilder sb = new  StringBuilder();
						for (XWPFParagraph para : cell.getParagraphs())
						{
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
		
		for (String columnHeader : _file._inputRecords.get(0)._cells)
		{
			_file._inputFields.add(new InputField(columnHeader));
		}
		
		for (IDataLoadListener listener:_loadListeners)
		{
			listener.notifyLoaded();
		}
		
		notifyListenersInputRecordIndexChanged();
	}

}