package org.achg.graveindex.views.components;

import org.achg.graveindex.data.OutputRecord;
import org.eclipse.jface.viewers.LabelProvider;

public class OutputRecordLabelProvider extends LabelProvider {
	@Override
	public String getText(Object element)
	{
		if (element instanceof OutputRecord)
		{
			return ((OutputRecord) element)._fullText.replaceAll("\r\n", " ");
		}
		return null;
	}
}
