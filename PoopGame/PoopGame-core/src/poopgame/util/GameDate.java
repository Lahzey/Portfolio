package poopgame.util;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple class to represent simple dates (without time).
 * <br/>Records the amount of days passed since 01.01.0001 and is able to represent this amount as a nicely formatted String (days.moths.years).
 * <br/>For simplicity, this does not consider leap-years.
 * @author A469627
 *
 */
public class GameDate {

	public static final int DAYS_PER_YEAR = 365;
	public static final int MONTHS_PER_YEAR = 12;
	public static final Map<Integer, Integer> DAYS_PER_MONTH = new HashMap<>();
	
	static{
		DAYS_PER_MONTH.put(1, 31);
		DAYS_PER_MONTH.put(2, 28);
		DAYS_PER_MONTH.put(3, 31);
		DAYS_PER_MONTH.put(4, 30);
		DAYS_PER_MONTH.put(5, 31);
		DAYS_PER_MONTH.put(6, 30);
		DAYS_PER_MONTH.put(7, 31);
		DAYS_PER_MONTH.put(8, 31);
		DAYS_PER_MONTH.put(9, 30);
		DAYS_PER_MONTH.put(10, 31);
		DAYS_PER_MONTH.put(11, 30);
		DAYS_PER_MONTH.put(12, 31);
	}

	/**
	 * Number of days since 01.01.0001
	 */
	public int days;
	
	public GameDate(){
		this(0);
	}
	
	public GameDate(int days){
		this.days = days;
	}
	
	public GameDate(int day, int month, int year){
		days = day - 1;
		days += getMonthInDays(month);
		days += getYearInDays(year);
	}
	
	private static int getMonthInDays(int month){
		int days = 0;
		while(month > 1){
			month--;
			days += DAYS_PER_MONTH.get(month);
		}
		return days;
	}
	
	private static int getYearInDays(int year){
		return (year - 1) * DAYS_PER_YEAR;
	}
	
	public String toString(){
		StringBuilder string = new StringBuilder();
		
		int day = days + 1;
		int month = 1;
		int year = 1;

		while(day > DAYS_PER_MONTH.get(month)){
			day -= DAYS_PER_MONTH.get(month);
			month++;
			if(month > MONTHS_PER_YEAR){
				year++;
				month -= MONTHS_PER_YEAR;
			}
		}
		
		if(day < 10) string.append("0");
		string.append(day);
		
		string.append(".");
		
		if(month < 10) string.append("0");
		string.append(month);
		
		string.append(".");

		if(year < 1000) string.append("0");
		if(year < 100) string.append("0");
		if(year < 10) string.append("0");
		string.append(year);
		return string.toString();
	}
	
	public GameDate add(GameDate gameDate){
		days += gameDate.days;
		return this;
	}
	
	public GameDate add(int days){
		this.days += days;
		return this;
	}
	
	public GameDate sub(GameDate gameDate){
		days -= gameDate.days;
		return this;
	}
	
	public GameDate sub(int days){
		this.days -= days;
		return this;
	}
	
	public GameDate cpy(){
		return new GameDate(days);
	}
}
