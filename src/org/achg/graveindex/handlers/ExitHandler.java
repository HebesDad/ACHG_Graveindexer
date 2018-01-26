package org.achg.graveindex.handlers;

import org.achg.graveindex.data.DataManager;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.IWorkbench;

public class ExitHandler {
	@Execute
	public void handle(IWorkbench workbench) {
		DataManager.getInstance().save();
		workbench.close();
	}
}
