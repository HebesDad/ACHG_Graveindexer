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

	public int getCurrentRecordNumber() {
		return _currentRecordNumber;
	}

	public void incrementCurrentRecordNumber() {
		_currentRecordNumber++;
		DataManager.getInstance().notifyListenersInputRecordIndexChanged();
	}

	public void decrementCurrentRecordNumber() {
		_currentRecordNumber--;
		DataManager.getInstance().notifyListenersInputRecordIndexChanged();
	}

	public int getMainCellNumber() {
		if (_mainCellNumber == -1) {
			_mainCellNumber = calculateMainCellNumber();
		}
		return _mainCellNumber;
	}

	private int calculateMainCellNumber() {
		List<Integer> fieldSizes = new ArrayList<>();
		for (int i = 0; i < _inputRecords.get(0)._cells.size(); i++) {
			fieldSizes.add(Integer.valueOf(0));
		}

		for (InputRecord record : _inputRecords) {
			for (int i = 0; i < record._cells.size(); i++) {

				fieldSizes.set(i, fieldSizes.get(i) + record._cells.get(i).length());
			}
		}

		int cellNumber = -1;
		int averageSize = 0;

		for (int i = 0; i < _inputRecords.get(0)._cells.size(); i++) {
			int currentAverage = fieldSizes.get(i) / _inputRecords.size();
			if (currentAverage > averageSize) {
				cellNumber = i;
				averageSize = currentAverage;
			}
		}
		return cellNumber;
	}

	public List<String[]> generateXlsxData() {
		

		List<String[]> map = new ArrayList<>();
		for (InputRecord inputRecord : _inputRecords) {

			List<String> cells = new ArrayList<>();
			if (inputRecord._outputRecords.isEmpty()) {
				addPrefixCells(cells, inputRecord);
				map.add(cells.toArray(new String[cells.size()]));
				
			} else

				for (OutputRecord outputRecord : inputRecord._outputRecords) {
					cells = new ArrayList<>();
					addPrefixCells(cells, inputRecord);
					// and the output we generated
					cells.add(outputRecord._forename);
					cells.add(outputRecord._surname);
					cells.add(Boolean.valueOf(outputRecord._bornCirca).toString());
					cells.add(String.format("%d", outputRecord._bornDay));
					cells.add(String.format("%d", outputRecord._bornMonth));
					cells.add(String.format("%d", outputRecord._bornYear));
					cells.add(String.format("%d", outputRecord._diedDay));
					cells.add(String.format("%d", outputRecord._diedMonth));
					cells.add(String.format("%d", outputRecord._diedYear));
					map.add(cells.toArray(new String[cells.size()]));
					
				}

		}
		return map;
	}

	private void addPrefixCells(List<String> destination, InputRecord inputRecord) {
		// add the extras
		for (String extra : _extraFields) {
			destination.add(extra);
		}

		// then the originals
		for (int i = 0; i < _inputFields.size(); i++) {
			if (_inputFields.get(i)._outputField) {
				destination.add(inputRecord._cells.get(i));
			}
		}
	}

}
