package custom.tools;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;


public class jvuWorkDaysCalculator {
	
	
	
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
	
	/*
	 * Every 40th or so calculations of calculateOnce() method gives a wrong result.
	 * Example:
	 * 			if correct calculation gives 4.77 working days, then once and a while the method will return 5.77 working days
	 * 
	 * This is why this method exists, to execute calculateOnce() 2 times, and if results are not matching - then it will execute a 3rd time.
	 * 
	 */
	public static String calculate (Timestamp start, Timestamp end, String outputUnits) {
		String result1 = calculateOnce(start, end, outputUnits);
		String result2 = calculateOnce(start, end, outputUnits);
		
		if (result1.equals(result2))
			return result1;
		else
			return calculateOnce(start, end, outputUnits);
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
	public static String calculateOnce (Timestamp start, Timestamp end, String outputUnits) {
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
