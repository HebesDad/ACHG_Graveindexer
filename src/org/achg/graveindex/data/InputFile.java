package org.achg.graveindex.data;

import java.util.ArrayList;
import java.util.List;

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
	
	
}
