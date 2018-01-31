package org.achg.graveindex.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputFile {
	private String _filename;
	private String[] _extraFields = {};
	private int _currentRecordNumber = 0;
	private int _mainCellNumber = -1;

	public InputFile(String file) {
		_filename = file;
	}

	public List<InputField> _inputFields = new ArrayList<>();
	public List<InputRecord> _inputRecords = new ArrayList<>();

	public String getFileName() {

		return _filename;
	}

	public void setExtraFields(String[] fields) {
		_extraFields = fields;
	}

	public int getCurrentRecordNumber()
	{
		return _currentRecordNumber;
	}
	
	public void incrementCurrentRecordNumber()
	{
		_currentRecordNumber++;
		DataManager.getInstance().notifyListenersInputRecordIndexChanged();
	}
	
	public void decrementCurrentRecordNumber()
	{
		_currentRecordNumber--;
		DataManager.getInstance().notifyListenersInputRecordIndexChanged();
	}

	public int getMainCellNumber() {
		if (_mainCellNumber ==-1)
		{
			_mainCellNumber = calculateMainCellNumber();
		}
		return _mainCellNumber;
	}

	private int calculateMainCellNumber() {
		List<Integer> fieldSizes = new ArrayList<>();
		for (int i = 0 ; i< _inputRecords.get(0)._cells.size(); i++)
		{
			fieldSizes.add(Integer.valueOf(0));
		}
		
		for (InputRecord record:_inputRecords)
		{
			for(int i =0; i<record._cells.size();i++)
			{
				
				fieldSizes.set(i,  fieldSizes.get(i)+ record._cells.get(i).length());
			}
		}
		
		int cellNumber =-1;
		int averageSize = 0;
		
		for (int i = 0 ; i< _inputRecords.get(0)._cells.size(); i++)
		{
			int currentAverage = fieldSizes.get(i)/_inputRecords.size();
			if (currentAverage>averageSize)
			{
				cellNumber=i;
				averageSize=currentAverage;
			}
		}
		return cellNumber;
	}

	public Map<String, String[]> generateXlsxData() {
		int rowNum = 1;
		
		Map<String,String[]> map = new HashMap<>();
		for (InputRecord inputRecord:_inputRecords)
		{
			for (OutputRecord outputField:inputRecord._outputRecords)
			{
				List<String> cells = new ArrayList<>();
				
				if (!inputRecord._outputRecords.isEmpty())
				{for(String extra:_extraFields)
				{
					cells.add(extra);
				}
				
				for (int i = 0 ; i<_inputFields.size(); i++)
				{
					if (_inputFields.get(i)._outputField)
					{
						cells.add(inputRecord._cells.get(i));
					}
				}
				 
				for (OutputRecord record:inputRecord._outputRecords)
				{
					cells.add(record._forename);
					cells.add(record._surname)
					;
					cells.add(Boolean.valueOf(record._bornCirca).toString());
					cells.add(String.format("%d", record._bornDay));
					cells.add(String.format("%d", record._bornMonth));
					cells.add(String.format("%d", record._bornYear));
					cells.add(String.format("%d", record._diedDay));
					cells.add(String.format("%d", record._diedMonth));
					cells.add(String.format("%d", record._diedYear));
				}
				
				
				
				map.put(String.format("%d", rowNum), cells.toArray(new String[cells.size()]));
				rowNum++;}
			}
		}
		return map;
	}
	
	
}
