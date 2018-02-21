package custom.utils.time; 

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;

/**
 * @jar	commons-collections.jar (to use BidiMap)
 */
public class NextSendCalculator {
	
	public static String formatDate(Date date, String pattern) {
		if(date == null)
			return "";
		
		return (new SimpleDateFormat(pattern)).format(date);
	}
	
	public static void main(String[] args) throws Exception {
		Calendar date1 = Calendar.getInstance();
		date1.set(112+1900, 9-1, 24, 14, 0, 0);
		System.out.println("IN DATE: "+formatDate(date1.getTime(),"yyyy-MM-dd HH:mm"));
		Calendar date2 = Calendar.getInstance();
		date2.set(112+1900, 10-1, 23, 22, 0, 0);
		System.out.println("IN DATE: "+formatDate(date2.getTime(),"yyyy-MM-dd HH:mm"));
		
		StringBuffer errorLog = new StringBuffer();
		try {
			//System.out.println(" OUT DATE: 2H : "+formatDate(calculateNextSend(date2.getTime(), "2H", errorLog),"yyyy-MM-dd HH:mm"));
			//System.out.println(" OUT DATE: 9D : "+formatDate(calculateNextSend(date2.getTime(), "9D", errorLog),"yyyy-MM-dd HH:mm"));
			//System.out.println(" OUT DATE: 15Mi : "+formatDate(calculateNextSend(date2.getTime(), "15Mi", errorLog),"yyyy-MM-dd HH:mm"));
			//System.out.println(" OUT DATE: 1Mo : "+formatDate(calculateNextSend(date2.getTime(), "1Mo", errorLog),"yyyy-MM-dd HH:mm"));
			//System.out.println(" OUT DATE: 1D{21D} : "+formatDate(calculateNextSend(date2.getTime(), "1D{21D}", errorLog),"yyyy-MM-dd HH:mm"));
			
			//System.out.println(" OUT DATE: 7D : "+formatDate(calculateNextSend(date2.getTime(), "7D"),"yyyy-MM-dd HH:mm"));
			
			System.out.println(" OUT DATE: 1Mi{15-45Mi} : "+formatDate(calculateNextSend(date2.getTime(), "1Mi{15-45Mi}", errorLog),"yyyy-MM-dd HH:mm"));
			System.out.println(" OUT DATE: 1Mo{4-6,8Mo} : "+formatDate(calculateNextSend(date2.getTime(), "1Mo{4-6,8Mo}", errorLog),"yyyy-MM-dd HH:mm"));
			System.out.println(" OUT DATE: 1H{9-19H;3-5W} : "+formatDate(calculateNextSend(date2.getTime(), "1H{9-19H;3-5W}", errorLog),"yyyy-MM-dd HH:mm"));
			System.out.println(" OUT DATE: 1H{0-8,11-23H;1-5W} : "+formatDate(calculateNextSend(date2.getTime(), "1H{0-8,11-23H;1-5W}", errorLog),"yyyy-MM-dd HH:mm"));
			System.out.println(" OUT DATE: 1D{2-5D;5-8H} : "+formatDate(calculateNextSend(date2.getTime(), "1D{2-5D;5-8H}", errorLog),"yyyy-MM-dd HH:mm"));// !!!
			System.out.println(" OUT DATE: 1Mo{4-6,8Mo;1W} : "+formatDate(calculateNextSend(date2.getTime(), "1Mo{4-6,8Mo;1W}", errorLog),"yyyy-MM-dd HH:mm"));// !!!
			System.out.println(" OUT DATE: 1W : "+formatDate(calculateNextSend(date2.getTime(), "1W", errorLog),"yyyy-MM-dd HH:mm"));// !!!
			System.out.println(" OUT DATE: 1W : "+formatDate(calculateNextSend(date2.getTime(), "1Mo{3-11M0}", errorLog),"yyyy-MM-dd HH:mm"));// !!!
			
			System.out.println(errorLog.toString());
		} catch (Exception e) {
			
		}
	}
	
	private static BidiMap dateCodeRegister = null;
	private static BidiMap dateCodePriority = null;
	static {
		dateCodeRegister = new DualHashBidiMap();
		dateCodeRegister.put("MI", Calendar.MINUTE);
		dateCodeRegister.put("H", Calendar.HOUR_OF_DAY);
		dateCodeRegister.put("D", Calendar.DAY_OF_MONTH);
		dateCodeRegister.put("W", Calendar.DAY_OF_WEEK);
		dateCodeRegister.put("MO", Calendar.MONTH);
		
		dateCodePriority = new DualHashBidiMap();
		dateCodePriority.put("MI", 1);
		dateCodePriority.put("H", 2);
		dateCodePriority.put("D", 3);
		dateCodePriority.put("W", 4);
		dateCodePriority.put("MO", 5);
	}
	
	public static Date calculateNextSend(Date date, String periodicity, StringBuffer errorLog) {
		Date result = null;
		
		try {
			String[] p = periodicity.replaceAll(" ","").toUpperCase().split("\\{");
			String increment = p[0];
			String conditions = "";
			if (p.length > 1)
				conditions = p[1].substring(0, p[1].lastIndexOf("}"));
			
			String incNumber = increment.split("[a-zA-Z]")[0];
			String incType = increment.split(incNumber)[1];
			
			if(!"".equals(conditions)) {
				if (!hasLegalConditionPriority(incType, conditions))
					throw new Exception("The increment type: "+incType+" with priority "+dateCodePriority.get(incType)+",\r\ndisallows to have in the condition {"+conditions+"} an increment type with lower priority.\r\n"+dateCodePriority.keySet().toString()+"\r\n"+dateCodePriority.values().toString());
				
				result = incrementDate(date, Integer.parseInt(incNumber), incType);
				int whileLimit = 0;
				while(!conformsToConditions(result, conditions) && whileLimit++ < 501)
					result = incrementDate(result, Integer.parseInt(incNumber), incType);
				
				if(whileLimit >=500)
					throw new Exception("While loop limit of "+whileLimit+" was reached.\r\nThe NEXTSEND date cannot be determined.\r\nConsider changing the PERIODICITY. ");
			} else {
				result = incrementDate(date, Integer.parseInt(incNumber), incType);
			}
		} catch (Exception e) {
			errorLog.append(e.getMessage());
			return null;
		}
		
		return result;
	}
	
	public static Date incrementDate (Date date, int increment, String incrementType) {
		if (date == null) 
			throw new IllegalArgumentException("Unable to generate a new Date, as the supplied date is NULL. ");
		else if (!dateCodeRegister.containsKey(incrementType))
			throw new IllegalArgumentException("The increment type: "+incrementType+" is not in the list of allowed values.\r\n"+dateCodeRegister.keySet().toString());
		else if ("W".equalsIgnoreCase(incrementType))
			throw new IllegalArgumentException("The increment type: W, can be used only in the conditoin {...}.\r\nUse 7D to indicate weekly sending.");
		
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add((Integer)dateCodeRegister.get(incrementType), increment);
		
		return cal.getTime();
	}
	
	private static boolean hasLegalConditionPriority (String incrementType, String conditions) {
		boolean result = true;
		
		Integer incrementPriority = (Integer)dateCodePriority.get(incrementType);
		while(--incrementPriority > 0)
			if(conditions.contains((String)dateCodePriority.getKey(incrementPriority)))
				result = false;
		
		return result;
	}
	
	private static boolean conformsToConditions (Date date, String conditions) throws Exception {
		boolean result = true;
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		String[] conds = conditions.split(";");
		OUT:for(String cond : conds) {
			String condType = cond.split("[\\d,-]+")[1];
			String condRanges = cond.split(condType)[0];
			
			int current = cal.get((Integer)dateCodeRegister.get(condType));
			if("Mo".equalsIgnoreCase(condType))
				current = current + 1;//adjust for Calendar.MONTH [0 - 11]
			else if("W".equalsIgnoreCase(condType))
				current = (current == 1) ? 7 : (current - 1);//adjust for Calendar.DAY_OF_WEEK [1=SUNDAY, ..... , 7 = SATURDAY]
			
			String[] ranges = condRanges.split(",");
			IN:for(String range : ranges) {
				String[] rangeMinMax = range.split("-");
				int rangeMin = Integer.parseInt(rangeMinMax[0]);
				if (rangeMinMax.length > 1) {
					int rangeMax = Integer.parseInt(rangeMinMax[1]);
					result = (current >= rangeMin && current <= rangeMax) ? true : false;
				} else
					result = (rangeMin == current) ? true : false;
				
				if(result)
					break IN;
			}
			
			if(!result)
				break OUT;
		}
		
		return result;
	}
	
}
