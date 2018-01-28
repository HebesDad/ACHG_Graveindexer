package org.achg.graveindex.views.components;


import org.achg.graveindex.data.InputRecord;
import org.eclipse.jface.viewers.IStructuredContentProvider;

public class OutputRecordsContentProvider implements IStructuredContentProvider
{

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof InputRecord)
		{
			return ((InputRecord) inputElement)._outputRecords.toArray();
		}
		return null;
	}

}
