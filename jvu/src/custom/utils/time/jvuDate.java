package custom.utils.time;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import custom.utils.CustomUtilsException;

public final class jvuDate {
	
	private static final String DEFAULT_FORMAT_PATTERN = "yyyy/MM/dd";
	
	private jvuDate () {}
	
	public static boolean isFutureDate (Date date) {
		return isDateAfter(date, new Date());
	}
	
	public static boolean isPastDate (Date date) {
		return isDateAfter(new Date(), date);
	}
	
	private static boolean isDateAfter (Date date1, Date date2) {
		if (date1 == null || date2 == null)
			return false;
		else
			return date1.after(date2);
	}
	
	/**
	 * Formats given date using the default pattern.
	 */
	public static String formatDate (Date date) {
		return formatDate(date, DEFAULT_FORMAT_PATTERN);
	}
	
	/**
	 * Formats given date using given pattern. Pattern is not cached.
	 */
	public static String formatDate (Date date, String pattern) {
		return (new SimpleDateFormat(pattern)).format(date);
	}
	
	/**
	 * Parses String to date with the given pattern. If pattern is null, the default pattern is used.
	 */
	public static Date parseDate (String date, String pattern) throws CustomUtilsException {
		if (date == null || date.isEmpty())
			return null;
		
		if (pattern == null || pattern.isEmpty())
			pattern = DEFAULT_FORMAT_PATTERN;
		
		try {
			return (new SimpleDateFormat(pattern)).parse(date);
		} catch (ParseException pe) {
			throw new CustomUtilsException("Exception during method execution.", pe);
		}
	}
	
	/**
	 * Parses String to date using default pattern.
	 */
	public static Date parseDate (String dateString) throws CustomUtilsException {
		return parseDate(dateString, DEFAULT_FORMAT_PATTERN);
	}
	
	/**
	 * Decrements months by the supplied number.
	 */
	public static Date subtractMonths (Date date, int numMonths) throws CustomUtilsException {
		if (date == null) 
			throw new CustomUtilsException("Unable to generate a new Date as the supplied date is NULL");
		
		numMonths = Math.abs(numMonths);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, -1 * numMonths);// TODO : solve problem with dates 02.29 etc.
		return cal.getTime();
	}
	
	/**
	 * Decrements days by the supplied number.
	 */
	public static Date subtractDays (Date date, int days) throws CustomUtilsException {
		if (date == null) 
			throw new CustomUtilsException("Unable to generate a new Date as the supplied date is NULL");
		
		days = Math.abs(days);
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, -1 * days);
		return cal.getTime();
	}
	
	/**
	 * Provides date for next day.
	 */
	public static Date getNextDay (Date date) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.roll(Calendar.DATE, true);
		return cal.getTime();
	}
	
	public static Calendar getCalendar (int year, int month, int day, int hr, int min, int sec) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month-1, day, hr, min, sec);
		return cal;
	}
	
	public static long cal2mlsec (Calendar cal) {
		return cal.getTimeInMillis();
	}
	
	public static long cal2mlsec_V2 (Calendar cal) {
		return cal.getTime().getTime();
	}
	
	public static long getDiffInMlsecs (Calendar cal, Date date) {
		return Math.abs(cal.getTimeInMillis() - date.getTime());
	}
	
	public static long getDiffInHrs (Calendar cal, Date date) {
		return getDiffInMlsecs(cal, date)/1000/60/60;
	}
	
	public static final boolean isWeekend (Calendar cal) {
		if (cal == null)
			throw new IllegalArgumentException(" Unable to decide, as the supplied date is NULL. ");
		
		if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
			return false;
		
		return true;
	}
	
	/**
	 * TODO : Working time calculation START
	 */
	private static int nbrWorkHours = 9;
	private static int workStartTimeHr = 9;
	private static int resultPrecision = 3;
	private static ArrayList<String> holidays = new ArrayList<String>();
	private static ArrayList<String> easter = new ArrayList<String>();
	private static ArrayList<String> ascension = new ArrayList<String>();
	//private static final String outputUnits_Days = "WORK_DAYS";
	private static final String outputUnits_Hours = "WORK_HOURS";
	static {
		holidays.add("1.1");
		holidays.add("5.1");
		holidays.add("5.8");
		holidays.add("7.14");
		holidays.add("8.15");
		holidays.add("11.1");
		holidays.add("11.11");
		holidays.add("12.25");
		
		easter.add("2009.4.13");
		easter.add("2010.4.5");
		easter.add("2011.4.25");
		easter.add("2012.4.9");
		easter.add("2013.4.1");
		easter.add("2014.4.21");
		easter.add("2015.4.6");
		easter.add("2016.3.28");
		easter.add("2017.4.17");
		easter.add("2018.4.2");
		easter.add("2019.4.22");
		easter.add("2020.4.13");
		
		ascension.add("2009.5.21");
		ascension.add("2010.5.13");
		ascension.add("2011.6.2");
		ascension.add("2012.5.17");
		ascension.add("2013.5.9");
		ascension.add("2014.5.29");
		ascension.add("2015.5.14");
		ascension.add("2016.5.5");
		ascension.add("2017.5.25");
		ascension.add("2018.5.10");
		ascension.add("2019.5.30");
		ascension.add("2020.5.21");
		
	}
	
	/**
	 * Calculates working time (hours/days) between two date-s, taking into account French holidays.
	 * The working time is governed by static variables.
	 * The French holidays are also hardcoded in static ArrayList-s.
	 * 
	 * For 100% correct calculation, the start and end dates should be during the working time (date not holiday/weekend, time within working time).
	 * 
	 * The output precision is also controlled by a static variable.
	 * 
	 * @param start
	 * @param end
	 * @param outputType	- the output units ; valid values are "WORK_HOURS", "WORK_DAYS"(default) ;
	 * @return
	 */
	public static double calculateWorkTime (Date start, Date end, String outputType) {
		double result = 0.00;
		
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(start);
		Calendar endDate = Calendar.getInstance();
		endDate.setTime(end);
		
		if (startDate.getTimeInMillis() > endDate.getTimeInMillis()) {
			startDate.setTime(end);
			endDate.setTime(start);
		}
		
		Calendar firstDayStart = Calendar.getInstance();
		firstDayStart.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH), workStartTimeHr, 0, 0);
		Calendar lastDayStart = Calendar.getInstance();
		lastDayStart.set(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH), workStartTimeHr, 0, 0);		
		
		if (firstDayStart.getTimeInMillis() == lastDayStart.getTimeInMillis()) {
			result = (endDate.getTimeInMillis() - startDate.getTimeInMillis())/1000.00/60.00/60.00/nbrWorkHours; // calculated in working days
			
		} else {
			// Calculate hours of first day
			Calendar firstDayEnd = Calendar.getInstance();
			firstDayEnd.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH), workStartTimeHr+nbrWorkHours, 0, 0);
			result += (firstDayEnd.getTimeInMillis() - startDate.getTimeInMillis())/1000.00/60.00/60.00/nbrWorkHours; // calculated in working days
			
			firstDayStart.add(Calendar.DAY_OF_MONTH, 1);
			
			while (firstDayStart.before(lastDayStart)) {
				String month_day = (firstDayStart.get(Calendar.MONTH)+1)+"."+firstDayStart.get(Calendar.DAY_OF_MONTH);
				String year_month_day = firstDayStart.get(Calendar.YEAR)+"."+month_day;
				if (	firstDayStart.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY 
				    		&& firstDayStart.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
				    			&& !holidays.contains(month_day)
				    				&& !easter.contains(year_month_day)
				    					&& !ascension.contains(year_month_day) 			) {
					result++; // calculated in working days
				}
				
				firstDayStart.add(Calendar.DAY_OF_MONTH, 1);
			}
			
			// Calculate hours of last day
			Calendar endDayStart = Calendar.getInstance();
			endDayStart.set(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH), workStartTimeHr, 0, 0);
			result += (endDate.getTimeInMillis() - endDayStart.getTimeInMillis())/1000.00/60.00/60.00/nbrWorkHours; // calculated in working days
			
		}
		
		if(outputType != null
				&& !"".equalsIgnoreCase(outputType)
					&& outputUnits_Hours.equalsIgnoreCase(outputType)) {
			result *= nbrWorkHours;
		}
		
		result *= Math.pow(10,resultPrecision);
		int r = (int) result;
		result = (double) r;
		result /= Math.pow(10,resultPrecision);
		
		return result;
	}
	/**
	 * Working time calculation END
	 */
	
	
	
}


class WorkDaysCalculator {
	
	private static int workStartTimeHr = 9;
	private static int nbrWorkHours = 9;
	private static int resultPrecision = 3;
	private static int resultRounding = BigDecimal.ROUND_DOWN;
	private static String dateDelimiter = ".";
	
	private static final String outputUnits_Days = "WORK_DAYS";
	private static final String outputUnits_Hours = "WORK_HOURS";
	
	private static BigDecimal bd1000 = new BigDecimal("1000.00");
	private static BigDecimal bd60 = new BigDecimal("60.00");
	private static BigDecimal bdWorkHours = null;
	private static BigDecimal bd1 = new BigDecimal("1.00");
	
	private static ArrayList<String> holidays = new ArrayList<String>();	// holds fixed-date holidays
	private static ArrayList<String> easter = new ArrayList<String>();		// holds floating-date holiday
	private static ArrayList<String> ascension = new ArrayList<String>();	// holds floating-date holiday
	
	static {
		holidays.add("1"+dateDelimiter+"1");
		holidays.add("5"+dateDelimiter+"1");
		holidays.add("5"+dateDelimiter+"8");
		holidays.add("7"+dateDelimiter+"14");
		holidays.add("8"+dateDelimiter+"15");
		holidays.add("11"+dateDelimiter+"1");
		holidays.add("11"+dateDelimiter+"11");
		holidays.add("12"+dateDelimiter+"25");
		
		easter.add("2009"+dateDelimiter+"4"+dateDelimiter+"13");
		easter.add("2010"+dateDelimiter+"4"+dateDelimiter+"5");
		easter.add("2011"+dateDelimiter+"4"+dateDelimiter+"25");
		easter.add("2012"+dateDelimiter+"4"+dateDelimiter+"9");
		easter.add("2013"+dateDelimiter+"4"+dateDelimiter+"1");
		easter.add("2014"+dateDelimiter+"4"+dateDelimiter+"21");
		easter.add("2015"+dateDelimiter+"4"+dateDelimiter+"6");
		easter.add("2016"+dateDelimiter+"3"+dateDelimiter+"28");
		easter.add("2017"+dateDelimiter+"4"+dateDelimiter+"17");
		easter.add("2018"+dateDelimiter+"4"+dateDelimiter+"2");
		easter.add("2019"+dateDelimiter+"4"+dateDelimiter+"22");
		easter.add("2020"+dateDelimiter+"4"+dateDelimiter+"13");
		
		ascension.add("2009"+dateDelimiter+"5"+dateDelimiter+"21");
		ascension.add("2010"+dateDelimiter+"5"+dateDelimiter+"13");
		ascension.add("2011"+dateDelimiter+"6"+dateDelimiter+"2");
		ascension.add("2012"+dateDelimiter+"5"+dateDelimiter+"17");
		ascension.add("2013"+dateDelimiter+"5"+dateDelimiter+"9");
		ascension.add("2014"+dateDelimiter+"5"+dateDelimiter+"29");
		ascension.add("2015"+dateDelimiter+"5"+dateDelimiter+"14");
		ascension.add("2016"+dateDelimiter+"5"+dateDelimiter+"5");
		ascension.add("2017"+dateDelimiter+"5"+dateDelimiter+"25");
		ascension.add("2018"+dateDelimiter+"5"+dateDelimiter+"10");
		ascension.add("2019"+dateDelimiter+"5"+dateDelimiter+"30");
		ascension.add("2020"+dateDelimiter+"5"+dateDelimiter+"21");
		
		bd1000 = bd1000.setScale(3, resultRounding);
		bd60 = bd60.setScale(3, resultRounding);
		bd1 = bd1.setScale(3, resultRounding);
		bdWorkHours = new BigDecimal(nbrWorkHours);
		bdWorkHours = bdWorkHours.setScale(3, resultRounding);
		
	}
	
	/**
	 * Calculates working hours/days between two date-s, taking into account French holidays.
	 * The working time is governed by static variables.
	 * The output precision is also controlled by a static variable.
	 * 
	 * Due to the floating holiday dates (ascension, easter), the method will work well for dates between 2009/01/01 and 2020/12/31.
	 * 
	 * @param start
	 * @param end
	 * @param outputUnits	- valid values are "WORK_HOURS", "WORK_DAYS"(default) ;
	 * @return
	 */
	public static String calculate (Timestamp start, Timestamp end, String outputUnits) {
		BigDecimal result = new BigDecimal("0.00");
		result = result.setScale(3, resultRounding);
		
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(start);
		Calendar endDate = Calendar.getInstance();
		endDate.setTime(end);
		
		if (startDate.getTimeInMillis() > endDate.getTimeInMillis()) {
			startDate.setTime(end);
			endDate.setTime(start);
		}
		
		Calendar firstDayStart = Calendar.getInstance();
		firstDayStart.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH), workStartTimeHr, 0, 0);
		Calendar firstDayEnd = Calendar.getInstance();
		firstDayEnd.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH), workStartTimeHr+nbrWorkHours, 0, 0);
		if (startDate.before(firstDayStart))
			startDate = firstDayStart;
		else if (startDate.after(firstDayEnd))
			startDate = firstDayEnd;
		
		Calendar lastDayStart = Calendar.getInstance();
		lastDayStart.set(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH), workStartTimeHr, 0, 0);
		Calendar lastDayEnd = Calendar.getInstance();
		lastDayEnd.set(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH), workStartTimeHr+nbrWorkHours, 0, 0);
		if (endDate.before(lastDayStart))
			endDate = lastDayStart;
		else if (endDate.after(lastDayEnd))
			endDate = lastDayEnd;
		
		
		// START calculation
		if (firstDayStart.getTimeInMillis() == lastDayStart.getTimeInMillis()) {
			if (!isWeekend(firstDayStart) && !isHolidayFR(firstDayStart))
				result = addDifferenceInDays(result, endDate, startDate);
		} else {
			// Calculate hours of first day
			if (!isWeekend(startDate) && !isHolidayFR(startDate))
				result = addDifferenceInDays(result, firstDayEnd, startDate); // calculated in working days
			
			// Calculate days after first and before last
			firstDayStart.add(Calendar.DAY_OF_MONTH, 1);
			while (firstDayStart.before(lastDayStart)) {
				if (!isWeekend(firstDayStart) && !isHolidayFR(firstDayStart))
					result = result.add(bd1); // calculated in working days
				
				firstDayStart.add(Calendar.DAY_OF_MONTH, 1);
			}
			
			// Calculate hours of last day
			if (!isWeekend(endDate) && !isHolidayFR(endDate))
				result = addDifferenceInDays(result, endDate, lastDayStart); // calculated in working days
		}
		// END calculation
		
		
		if(outputUnits != null && !"".equalsIgnoreCase(outputUnits) && outputUnits_Hours.equalsIgnoreCase(outputUnits))
			result = result.multiply(bdWorkHours);
		
		
		String s = result.toString();
		if (s.length() > s.indexOf(".")+1+resultPrecision)
			s = s.substring(0, s.indexOf(".")+1+resultPrecision);
		while (s.length() < s.indexOf(".")+1+resultPrecision)
			s += "0";
		
		
		return s;
	}
	
	private static final BigDecimal addDifferenceInDays (BigDecimal oldValue, Calendar end, Calendar start) {
		BigDecimal bdEnd = new BigDecimal(Long.toString(end.getTimeInMillis()));
		BigDecimal bdStart = new BigDecimal(Long.toString(start.getTimeInMillis()));
		BigDecimal tmp = bdEnd.subtract(bdStart);
		tmp = tmp.setScale(resultPrecision, resultRounding);
		tmp = tmp.divide(bd1000, resultPrecision, resultRounding);
		tmp = tmp.divide(bd60, resultPrecision, resultRounding);
		tmp = tmp.divide(bd60, resultPrecision, resultRounding);
		tmp = tmp.divide(bdWorkHours, resultPrecision, resultRounding);
		tmp = oldValue.add(tmp);
		
		return tmp;
	}
	
	private static final boolean isWeekend (Calendar cal) {
		if (cal == null)
			return true;
		
		boolean result = true;
		if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
			result = false;
		
		return result;
	}
	
	private static final boolean isHolidayFR (Calendar cal) {
		if (cal == null)
			return false;
		
		boolean result = false;
		String month_day = (cal.get(Calendar.MONTH)+1)+dateDelimiter+cal.get(Calendar.DAY_OF_MONTH);
		String year_month_day = cal.get(Calendar.YEAR)+dateDelimiter+month_day;
		
		if (holidays.contains(month_day) || easter.contains(year_month_day) || ascension.contains(year_month_day))
			result = true;
		
		return result;
	}
	
	
	/**
	 * For TESTING purposes only
	 */
	public static void main(String[] args) {
		Calendar start = Calendar.getInstance();
		//start.set(112+1900, 9-1, 9, 14, 0, 0);
		start.set(111+1900, 7-1, 11, 1, 0, 0);
		
		Calendar end = Calendar.getInstance();
		//end.set(112+1900, 9-1, 9, 16, 30, 0);
		end.set(111+1900, 7-1, 18, 16, 0, 0);
		
		System.out.println(calculate(new Timestamp(start.getTime().getTime()), new Timestamp(end.getTime().getTime()), outputUnits_Days));
	}
	
}

