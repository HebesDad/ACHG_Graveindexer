package org.achg.graveindex.views;

import javax.annotation.PostConstruct;

import org.achg.graveindex.data.DataManager;
import org.achg.graveindex.data.IDataLoadListener;
import org.achg.graveindex.data.InputField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class InputFileInfoView implements IDataLoadListener {

	private Label _fileLabel;
	Tree _fieldsList;

	@PostConstruct
	public void create(Composite viewParent) {

		GridLayout layout = new GridLayout(2, false);
		viewParent.setLayout(layout);

		Label label = new Label(viewParent, SWT.NONE);
		label.setText("Input File: ");

		_fileLabel = new Label(viewParent, SWT.FILL);

		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		_fileLabel.setLayoutData(gd);

		Label lab = new Label(viewParent, SWT.NONE);
		lab.setText("Fields to use:");
		gd = new GridData(SWT.LEFT, SWT.TOP, false, false);
		lab.setLayoutData(gd);

		_fieldsList = new Tree(viewParent, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.FILL);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		_fieldsList.setLayoutData(gd);
		_fieldsList.addListener(SWT.CHECK, new Listener() {

			@Override
			public void handleEvent(Event event) {

				for (int i = 0; i < _fieldsList.getItemCount(); i++) {
					DataManager.getInstance().getInputFile()._inputFields.get(i)._outputField = _fieldsList.getItem(i)
							.getChecked();
				}

			}
		});

		new Label(viewParent, SWT.NONE).setText("Extra field values:");

		Text extraFieldText = new Text(viewParent, SWT.FILL | SWT.BORDER);
		extraFieldText.setMessage("comma separate list");
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		extraFieldText.setLayoutData(gd);
		extraFieldText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				DataManager.getInstance().getInputFile().setExtraFields(extraFieldText.getText().split(","));

			}
		});

		DataManager.getInstance().addLoadListener(this);
	}

	@Override
	public void notifyLoaded() {
		_fileLabel.setText(DataManager.getInstance().getInputFile().getFileName());
		_fileLabel.requestLayout();

		_fieldsList.setItemCount(0);
		for (InputField field : DataManager.getInstance().getInputFile()._inputFields) {

			TreeItem item = new TreeItem(_fieldsList, SWT.CHECK);
			item.setText(field._value.replace(" /", "").trim());
			item.setChecked(true);
		}
		_fieldsList.requestLayout();

	}
}
