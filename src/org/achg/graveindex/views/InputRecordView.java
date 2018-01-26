package org.achg.graveindex.views;

import javax.annotation.PostConstruct;

import org.achg.graveindex.data.DataManager;
import org.achg.graveindex.data.IInputRecordListener;
import org.achg.graveindex.data.InputRecord;
import org.achg.graveindex.data.OutputRecord;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class InputRecordView implements IInputRecordListener {
private final String SPLIT_MARKER = "\r\n<SPLIT>\r\n";
	private Label _recordNumberLabel;
	private Text _inputRecordText;

	@PostConstruct
	public void create(Composite viewParent) {
		GridLayout layout = new GridLayout(4, false);
		viewParent.setLayout(layout);

		Label lab = new Label(viewParent, SWT.NONE);
		GridData gd = new GridData(SWT.LEFT, SWT.TOP, false, false);
		gd.horizontalSpan = 1;
		lab.setLayoutData(gd);
		lab.setText("Record Number:");

		_recordNumberLabel = new Label(viewParent, SWT.FILL);
		gd = new GridData(SWT.LEFT, SWT.TOP, true, false);
		_recordNumberLabel.setLayoutData(gd);

		Button prevButton = new Button(viewParent, SWT.NONE);
		gd = new GridData(SWT.RIGHT, SWT.TOP, false, false);
		prevButton.setLayoutData(gd);
		prevButton.setText("Previous");
		prevButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DataManager.getInstance().getInputFile().decrementCurrentRecordNumber();
			}
		});

		Button nextButton = new Button(viewParent, SWT.NONE);
		gd = new GridData(SWT.RIGHT, SWT.TOP, false, false);
		nextButton.setLayoutData(gd);
		nextButton.setText("Next");
		nextButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DataManager.getInstance().getInputFile().incrementCurrentRecordNumber();
			}
		});

		_inputRecordText = new Text(viewParent, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		_inputRecordText.setEditable(false);

		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 4;
		_inputRecordText.setLayoutData(gd);

		Button insertSplitButton = new Button(viewParent, SWT.NONE);
		insertSplitButton.setText("Insert a Split");
		insertSplitButton.setToolTipText("inserts a split at cursor position");
		insertSplitButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				insertSplit();
			}
		});

		Button recordButton = new Button(viewParent, SWT.NONE);
		recordButton.setText("Create Output Records");
		recordButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				createOutputRecords();
			}
		});

		DataManager.getInstance().addInputRecordListener(this);
	}
	
	private void createOutputRecords()
	{
		int currentIndex = DataManager.getInstance().getInputFile().getCurrentRecordNumber();
		String workingCopy = DataManager.getInstance().getInputFile()._inputRecords
				.get(currentIndex)._manipulatedTranscription;
		
		String[] parts = workingCopy.split(SPLIT_MARKER);
		String[] scrubbedParts = scrubOutputRecordDross(parts);
		InputRecord inputRecord = DataManager.getInstance().getInputFile()._inputRecords
		.get(currentIndex);
		for (int i = 0; i<parts.length;i++)
		{
			OutputRecord outputRecord = new OutputRecord();
			outputRecord._fullText=parts[i];
			outputRecord._scrbbedFullText = scrubbedParts[i];
			inputRecord._outputRecords.add(outputRecord);
		}
		DataManager.getInstance().notifyListenersOutputRecordsAvailable();
	}

	private String[] scrubOutputRecordDross(String[] parts) {
		String outputs[] = new String[parts.length];
		return outputs;
	}

	private void insertSplit() {
		int currentIndex = DataManager.getInstance().getInputFile().getCurrentRecordNumber();
		String workingCopy = DataManager.getInstance().getInputFile()._inputRecords
				.get(currentIndex)._manipulatedTranscription;

		int offset = _inputRecordText.getCaretPosition();
		workingCopy = workingCopy.substring(0, offset) + SPLIT_MARKER + workingCopy.substring(offset);

		DataManager.getInstance().getInputFile()._inputRecords
				.get(currentIndex)._manipulatedTranscription = workingCopy;
		_inputRecordText.setText(workingCopy);
	}

	@Override
	public void currentIndexChanged() {
		int currentIndex = DataManager.getInstance().getInputFile().getCurrentRecordNumber();
		_recordNumberLabel.setText(String.format("%d", currentIndex));

		String workingCopy = DataManager.getInstance().getInputFile()._inputRecords
						.get(currentIndex)._cells.get(DataManager.getInstance().getInputFile().getMainCellNumber())
								.replaceAll(" / ", "\r\n");
		
		workingCopy = automaticSplit(workingCopy);

		DataManager.getInstance().getInputFile()._inputRecords
		.get(currentIndex)._manipulatedTranscription = workingCopy;
		
		_inputRecordText.setText(
				DataManager.getInstance().getInputFile()._inputRecords.get(currentIndex)._manipulatedTranscription);

	}

	private String automaticSplit(String workingCopyParam) {
		String workingCopy = workingCopyParam.replaceAll("And their", SPLIT_MARKER + "And their");
		workingCopy = workingCopy.replaceAll("And his", SPLIT_MARKER + "And his");
		workingCopy = workingCopy.replaceAll("And her", SPLIT_MARKER + "And her");
		workingCopy = workingCopy.replaceAll("And", SPLIT_MARKER + "And");
		workingCopy = workingCopy.replaceAll("His", SPLIT_MARKER + "His");
		workingCopy = workingCopy.replaceAll("Her", SPLIT_MARKER + "Her");
		workingCopy = workingCopy.replaceAll("Their", SPLIT_MARKER + "Their");
		workingCopy = workingCopy.replaceAll("Also", SPLIT_MARKER + "Also");
		return workingCopy;
	}
}
