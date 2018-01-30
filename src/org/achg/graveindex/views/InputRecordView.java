package org.achg.graveindex.views;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.achg.graveindex.data.DataManager;
import org.achg.graveindex.data.IInputRecordListener;
import org.achg.graveindex.data.InputRecord;
import org.achg.graveindex.data.OutputRecord;
import org.achg.graveindex.data.OutputRecordDetailExtractor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class InputRecordView implements IInputRecordListener {

	private final Pattern[] DROSS = { Pattern.compile("R\\.I\\.P\\.?", Pattern.CASE_INSENSITIVE),
			Pattern.compile("Rest\\s+in\\s+peace", Pattern.CASE_INSENSITIVE),
			Pattern.compile("(In)?(\\s+ever)?\\s+loving\\s+remembrance", Pattern.CASE_INSENSITIVE),
			Pattern.compile("(In)?(\\s+ever)?(\\s+loving)?\\s+memory\\s+of", Pattern.CASE_INSENSITIVE),
			Pattern.compile("(In)?(\\s+ever)?\\s+(loving\\s)?+memory\\s+", Pattern.CASE_INSENSITIVE),
			Pattern.compile("(In)?(\\s+ever)?\\s+loving\\s+memories\\s+", Pattern.CASE_INSENSITIVE),
			Pattern.compile("At\\s+peace", Pattern.CASE_INSENSITIVE),
			Pattern.compile("And\\s+(devoted\\s+)?their", Pattern.CASE_INSENSITIVE),
			Pattern.compile("And\\s+(devoted\\s+)?his", Pattern.CASE_INSENSITIVE),
			Pattern.compile("And\\s+(devoted\\s+)?her", Pattern.CASE_INSENSITIVE),
			Pattern.compile("\\sHis(\\s+devoted)?", Pattern.CASE_INSENSITIVE),
			
			Pattern.compile("Their(\\s+devoted)?", Pattern.CASE_INSENSITIVE),
			Pattern.compile("Also", Pattern.CASE_INSENSITIVE),
			Pattern.compile("(devoted\\s+)?Son", Pattern.CASE_INSENSITIVE),
			Pattern.compile("(devoted\\s+)?Daughter", Pattern.CASE_INSENSITIVE),
			Pattern.compile("(devoted\\s+)?Husband", Pattern.CASE_INSENSITIVE),
			Pattern.compile("(devoted\\s+)?Father", Pattern.CASE_INSENSITIVE),
			Pattern.compile("(devoted\\s+)?Wife", Pattern.CASE_INSENSITIVE),Pattern.compile("\\sHer(\\s+devoted)?", Pattern.CASE_INSENSITIVE),
			Pattern.compile("\\sAnd", Pattern.CASE_INSENSITIVE) };

	private final String SPLIT_MARKER = "\r\n<SPLIT>\r\n";
	private Label _recordNumberLabel;
	private Text _inputRecordText;
	private OutputRecordDetailExtractor _extractor = new OutputRecordDetailExtractor();

	@PostConstruct
	public void create(Composite viewParent) {
		GridLayout layout = new GridLayout(6, false);
		viewParent.setLayout(layout);

		Label lab = new Label(viewParent, SWT.NONE);
		GridData gd = new GridData(SWT.LEFT, SWT.TOP, false, false);
		gd.horizontalSpan = 1;
		lab.setLayoutData(gd);
		lab.setText("Record Number:");

		_recordNumberLabel = new Label(viewParent, SWT.NONE);
		gd = new GridData(SWT.LEFT, SWT.TOP, true, false);
		_recordNumberLabel.setLayoutData(gd);
		_recordNumberLabel.setText("---------------");

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

		DataManager.getInstance().addInputRecordListener(this);
	}

	private void createOutputRecords() {
		int currentIndex = DataManager.getInstance().getInputFile().getCurrentRecordNumber();
		String workingCopy = DataManager.getInstance().getInputFile()._inputRecords
				.get(currentIndex)._manipulatedTranscription;

		String[] parts = workingCopy.split(SPLIT_MARKER);
		String[] scrubbedParts = scrubOutputRecordDross(parts);
		InputRecord inputRecord = DataManager.getInstance().getInputFile()._inputRecords.get(currentIndex);
		inputRecord._outputRecords.clear();
		for (int i = 0; i < parts.length; i++) {
			if (!parts[i].isEmpty()) {
				OutputRecord outputRecord = new OutputRecord();
				outputRecord._fullText = parts[i];
				outputRecord._scrubbedFullText = scrubbedParts[i];
				_extractor.extractDetails(outputRecord);
				inputRecord._outputRecords.add(outputRecord);
			}
		}
		DataManager.getInstance().notifyListenersOutputRecordsAvailable();
	}

	private String[] scrubOutputRecordDross(String[] parts) {
		String outputs[] = new String[parts.length];

		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];
			part = part.replaceFirst("Who\\s+died", "died");
			for (Pattern dross : DROSS) {
				Matcher match = dross.matcher(part);
				part = match.replaceAll("").trim();
			}
			outputs[i] = part;
		}
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
		// _recordNumberLabel.requestLayout();

		String workingCopy = DataManager.getInstance().getInputFile()._inputRecords.get(currentIndex)._cells
				.get(DataManager.getInstance().getInputFile().getMainCellNumber()).replaceAll(" / ", "\r\n");

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
