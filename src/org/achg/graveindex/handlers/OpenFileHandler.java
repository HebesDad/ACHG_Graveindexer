package org.achg.graveindex.handlers;

import org.achg.graveindex.data.DataManager;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class OpenFileHandler {
	@Execute
	public void handle(IWorkbench workbench, Shell shell) {
		FileDialog dialog = new FileDialog(shell);
		String file = dialog.open();
		if (file != null)
			DataManager.getInstance().loadFile(file);
	}
}
