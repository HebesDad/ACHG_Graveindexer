package org.achg.graveindex.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.achg.graveindex.views.components.IntegerUtils;

public class OutputRecordDetailExtractor {

	private static final String NN_MONTH_YYYY = "(\\d+)([a-z][a-z])?\\s+([A-Z][a-zA-Z]+)\\.?\\s+(\\d+)";
	private static final String YYYY = "(\\d+)";
	private static final String FORENAME_SURNAME = "(([A-Z\\(']\\S*\\s+)+)([A-Z\\(']\\S+)";
	private static final String FORENAME = "([A-Z\\(']\\S*)";

	private static Pattern FULLNAME_BIRTH_DEATH_FULLDATES = Pattern
			.compile(FORENAME_SURNAME + "\\s\\D*" + NN_MONTH_YYYY + "\\D*" + NN_MONTH_YYYY);
	private static Pattern FULLNAME_BIRTH_DEATH_YYYY = Pattern
			.compile(FORENAME_SURNAME + "\\s\\D*" + YYYY + "\\D*" + YYYY);
	private static Pattern FULLNAME_DEATH_AGED = Pattern
			.compile(FORENAME_SURNAME + "\\s\\D*" + NN_MONTH_YYYY + "\\s+[aA]ged\\s(\\d+)");
	private static Pattern FORENAME_BIRTH_DEATH_FULLDATES = Pattern
			.compile(FORENAME + "\\s\\D*" + NN_MONTH_YYYY + "\\D*" + NN_MONTH_YYYY);
	private static Pattern FORENAME_BIRTH_DEATH_YYYY = Pattern
			.compile(FORENAME + "\\s\\D*" + YYYY + "\\D*" + YYYY);
	private static Pattern FORENAME_DEATH_AGED = Pattern
			.compile(FORENAME + "\\s\\D*" + NN_MONTH_YYYY + "\\s+[aA]ged\\s(\\d+)");

	private static Pattern JANUARY = Pattern.compile("Jan(uary)?");
	private static Pattern FEBRUARY = Pattern.compile("Feb(ruary)?");
	private static Pattern MARCH = Pattern.compile("Mar(ch)?");
	private static Pattern APRIL = Pattern.compile("Apr(il)?");
	private static Pattern MAY = Pattern.compile("May");
	private static Pattern JUNE = Pattern.compile("Jun(e)?");
	private static Pattern JULY = Pattern.compile("Jul(y)?");
	private static Pattern AUGUST = Pattern.compile("Aug(ust)?");
	private static Pattern SEPTEMBER = Pattern.compile("Sep(tember)?");
	private static Pattern OCTOBER = Pattern.compile("Oct(ober)?");
	private static Pattern NOVEMBER = Pattern.compile("Nov(ember)?");
	private static Pattern DECEMBER = Pattern.compile("Dec(ember)?");

	public void extractDetails(OutputRecord outputRecord) {
		// the interesting it is outputRecord._scrubbedFullText;

		String txt = " " + outputRecord._scrubbedFullText;
		txt = txt.replace("born", "");
		txt = txt.replace("Born", "");
		txt = txt.replace("died", "");
		txt = txt.replace("Died", "");

		Matcher match = FULLNAME_DEATH_AGED.matcher(txt);
		if (match.find()) {
			outputRecord._forename = match.group(1).trim();
			outputRecord._surname = match.group(3).trim();
			outputRecord._bornCirca = true;
			outputRecord._diedDay = IntegerUtils.safeParseInt(match.group(4));
			outputRecord._diedMonth = convertMonth(match.group(6));
			outputRecord._diedYear = IntegerUtils.safeParseInt(match.group(7));
			int age = IntegerUtils.safeParseInt(match.group(8));
			outputRecord._bornYear = outputRecord._diedYear - age;
			outputRecord._bornDay = 0;
			outputRecord._bornMonth = 0;
			return;
		}
		match = FULLNAME_BIRTH_DEATH_FULLDATES.matcher(txt);
		if (match.find()) {
			outputRecord._forename = match.group(1).trim();
			outputRecord._surname = match.group(3).trim();
			outputRecord._bornCirca = false;
			outputRecord._bornDay = IntegerUtils.safeParseInt(match.group(4));
			outputRecord._bornMonth = convertMonth(match.group(6));
			outputRecord._bornYear = IntegerUtils.safeParseInt(match.group(7));

			outputRecord._diedDay = IntegerUtils.safeParseInt(match.group(8));
			outputRecord._diedMonth = convertMonth(match.group(10));
			outputRecord._diedYear = IntegerUtils.safeParseInt(match.group(11));

			return;
		}

		match = FULLNAME_BIRTH_DEATH_YYYY.matcher(txt);
		if (match.find()) {
			outputRecord._forename = match.group(1).trim();
			outputRecord._surname = match.group(3).trim();
			outputRecord._bornCirca = false;
			outputRecord._bornYear = IntegerUtils.safeParseInt(match.group(4));

			outputRecord._diedYear = IntegerUtils.safeParseInt(match.group(5));

			return;
		}
		
		match = FORENAME_DEATH_AGED.matcher(txt);
		if (match.find()) {
			outputRecord._forename = match.group(1).trim();
			outputRecord._bornCirca = true;
			outputRecord._diedDay = IntegerUtils.safeParseInt(match.group(2));
			outputRecord._diedMonth = convertMonth(match.group(4));
			outputRecord._diedYear = IntegerUtils.safeParseInt(match.group(5));
			int age = IntegerUtils.safeParseInt(match.group(6));
			outputRecord._bornYear = outputRecord._diedYear - age;
			outputRecord._bornDay = 0;
			outputRecord._bornMonth = 0;
			return;
		}
		match = FORENAME_BIRTH_DEATH_FULLDATES.matcher(txt);
		if (match.find()) {
			outputRecord._forename = match.group(1).trim();
			outputRecord._bornCirca = false;
			outputRecord._bornDay = IntegerUtils.safeParseInt(match.group(2));
			outputRecord._bornMonth = convertMonth(match.group(4));
			outputRecord._bornYear = IntegerUtils.safeParseInt(match.group(5));

			outputRecord._diedDay = IntegerUtils.safeParseInt(match.group(6));
			outputRecord._diedMonth = convertMonth(match.group(8));
			outputRecord._diedYear = IntegerUtils.safeParseInt(match.group(9));

			return;
		}

		match = FORENAME_BIRTH_DEATH_YYYY.matcher(txt);
		if (match.find()) {
			outputRecord._forename = match.group(1).trim();
			outputRecord._bornCirca = false;
			outputRecord._bornYear = IntegerUtils.safeParseInt(match.group(2));

			outputRecord._diedYear = IntegerUtils.safeParseInt(match.group(3));

			return;
		}

	}


	private int convertMonth(String group) {
		if (JANUARY.matcher(group).find())
			return 1;
		if (FEBRUARY.matcher(group).find())
			return 2;
		if (MARCH.matcher(group).find())
			return 3;
		if (APRIL.matcher(group).find())
			return 4;
		if (MAY.matcher(group).find())
			return 5;
		if (JUNE.matcher(group).find())
			return 6;
		if (JULY.matcher(group).find())
			return 7;
		if (AUGUST.matcher(group).find())
			return 8;
		if (SEPTEMBER.matcher(group).find())
			return 9;
		if (OCTOBER.matcher(group).find())
			return 10;
		if (NOVEMBER.matcher(group).find())
			return 11;
		if (DECEMBER.matcher(group).find())
			return 12;
		return 0;
	}

}
