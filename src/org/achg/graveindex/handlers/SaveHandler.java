package org.achg.graveindex.handlers;

import org.achg.graveindex.data.DataManager;
import org.eclipse.e4.core.di.annotations.Execute;

public class SaveHandler {
	@Execute
	public void handle() {
		DataManager.getInstance().save();
	}
}
