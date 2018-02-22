package org.achg.graveindex.views.components;

import org.achg.graveindex.data.OutputRecord;
import org.eclipse.jface.viewers.LabelProvider;

public class OutputRecordResultsLabelProvider extends LabelProvider {
	@Override
	public String getText(Object element)
	{
		if (element instanceof OutputRecord)
		{
			OutputRecord record = (OutputRecord) element;
			StringBuilder sb = new StringBuilder();
			
			sb.append("fn="); sb.append(record._forename); sb.append(", ");
			sb.append("sn="); sb.append(record._surname); sb.append(", ");
			if (record._bornCirca)
			{
				sb.append("born circa "); sb.append(record._bornYear); sb.append(", ");
			}
			else
			{
				sb.append(String.format("born %d, ", record._bornYear));
			}
			sb.append(String.format("died %d", record._diedYear));
			
			
			return sb.toString();
		}
		return null;
	}
}
