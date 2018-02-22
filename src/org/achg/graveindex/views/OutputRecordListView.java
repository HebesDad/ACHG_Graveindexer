package org.achg.graveindex.views;

import javax.annotation.PostConstruct;

import org.achg.graveindex.data.DataManager;
import org.achg.graveindex.data.IOutputRecordListener;
import org.achg.graveindex.data.OutputRecord;
import org.achg.graveindex.views.components.IntegerUtils;
import org.achg.graveindex.views.components.OutputRecordLabelProvider;
import org.achg.graveindex.views.components.OutputRecordResultsLabelProvider;
import org.achg.graveindex.views.components.OutputRecordsContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

public class OutputRecordListView implements IOutputRecordListener {
	private TableViewer _inputTableViewer;
	private TableViewer _outputTableViewer;
	private OutputRecord _currentOutputRecord = null;
	private Text _forenameTxt;
	private Text _surnameTxt;
	private Button _circaCheckbox;
	
	private Text _bornYearTxt;

	
	private Text _diedYearTxt;

	@PostConstruct
	public void create(Composite viewParent) {
		GridLayout gl = new GridLayout(2, false);
		viewParent.setLayout(gl);

		_inputTableViewer = new TableViewer(viewParent,
				SWT.FILL | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		_inputTableViewer.getControl().setLayoutData(gd);
		_inputTableViewer.setContentProvider(new OutputRecordsContentProvider());
		_inputTableViewer.setLabelProvider(new OutputRecordLabelProvider());
		Table table = (Table) _inputTableViewer.getControl();
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				processTableSelection(((IStructuredSelection) _inputTableViewer.getSelection()).getFirstElement());
			}

		});

		Button deleteButton = new Button(viewParent, SWT.NONE);
		deleteButton.setText("Delete");
		deleteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) _inputTableViewer.getSelection();
				for (Object obj : selection.toArray()) {
					if (obj instanceof OutputRecord) {
						DataManager.getInstance().getInputFile()._inputRecords
								.get(DataManager.getInstance().getInputFile().getCurrentRecordNumber())._outputRecords
										.remove(obj);
					}
				}
				notifyNewSetAvailable();
			}
		});

		_outputTableViewer = new TableViewer(viewParent,
				SWT.FILL | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		_outputTableViewer.getControl().setLayoutData(gd);
		_outputTableViewer.setContentProvider(new OutputRecordsContentProvider());
		_outputTableViewer.setLabelProvider(new OutputRecordResultsLabelProvider());
		table = (Table) _outputTableViewer.getControl();
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				processTableSelection(((IStructuredSelection) _outputTableViewer.getSelection()).getFirstElement());
			}

		});

		Label lab = new Label(viewParent, SWT.NONE);
		lab.setText("Forename:");

		_forenameTxt = new Text(viewParent, SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		_forenameTxt.setLayoutData(gd);
		_forenameTxt.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (_currentOutputRecord != null) {
					_currentOutputRecord._forename = _forenameTxt.getText().trim();
					refreshTables();
				}
			}
		});

		lab = new Label(viewParent, SWT.NONE);
		lab.setText("Surname:");

		_surnameTxt = new Text(viewParent, SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		_surnameTxt.setLayoutData(gd);
		_surnameTxt.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (_currentOutputRecord != null) {
					_currentOutputRecord._surname = _surnameTxt.getText().trim();
					refreshTables();
				}
			}
		});

		lab = new Label(viewParent, SWT.NONE);
		lab.setText("Birth year calculated from age:");
		_circaCheckbox = new Button(viewParent, SWT.CHECK);
		_circaCheckbox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				refreshTables();
			}
		});

		

		

		lab = new Label(viewParent, SWT.NONE);
		lab.setText("Birth year:");

		_bornYearTxt = new Text(viewParent, SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		_bornYearTxt.setLayoutData(gd);
		_bornYearTxt.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (_currentOutputRecord != null) {
					_currentOutputRecord._bornYear = IntegerUtils.safeParseInt(_bornYearTxt.getText());
					refreshTables();
				}
			}
		});

	

		lab = new Label(viewParent, SWT.NONE);
		lab.setText("Death year:");

		_diedYearTxt = new Text(viewParent, SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		_diedYearTxt.setLayoutData(gd);
		_diedYearTxt.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (_currentOutputRecord != null) {
					_currentOutputRecord._diedYear = IntegerUtils.safeParseInt(_diedYearTxt.getText());
					refreshTables();
				}
			}
		});

		DataManager.getInstance().addOutputRecordListener(this);
	}

	private void refreshTables() {
		_inputTableViewer.refresh();
		_outputTableViewer.refresh();
	}

	public void processTableSelection(Object object) {
		if (object instanceof OutputRecord) {
			_currentOutputRecord = (OutputRecord) object;
			_forenameTxt.setText(_currentOutputRecord._forename);
			_surnameTxt.setText(_currentOutputRecord._surname);
			_circaCheckbox.setSelection(_currentOutputRecord._bornCirca);
		
			
			if (_currentOutputRecord._bornYear > 0)
				_bornYearTxt.setText(String.format("%d", _currentOutputRecord._bornYear));
			else
				_bornYearTxt.setText("");

		
			if (_currentOutputRecord._diedYear > 0)
				_diedYearTxt.setText(String.format("%d", _currentOutputRecord._diedYear));
			else
				_diedYearTxt.setText("");

		}
	}

	@Override
	public void notifyNewSetAvailable() {

		_inputTableViewer.setInput(DataManager.getInstance().getInputFile()._inputRecords
				.get(DataManager.getInstance().getInputFile().getCurrentRecordNumber()));
		_inputTableViewer.refresh();
		_inputTableViewer.setSelection(new StructuredSelection(DataManager.getInstance().getInputFile()._inputRecords
				.get(DataManager.getInstance().getInputFile().getCurrentRecordNumber())._outputRecords.get(0)));
		processTableSelection(DataManager.getInstance().getInputFile()._inputRecords
				.get(DataManager.getInstance().getInputFile().getCurrentRecordNumber())._outputRecords.get(0));

		_outputTableViewer.setInput(DataManager.getInstance().getInputFile()._inputRecords
				.get(DataManager.getInstance().getInputFile().getCurrentRecordNumber()));
		_outputTableViewer.refresh();
	}

}
