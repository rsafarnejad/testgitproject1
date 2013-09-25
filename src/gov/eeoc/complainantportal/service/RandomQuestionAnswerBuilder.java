package gov.eeoc.complainantportal.service;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.lang3.RandomStringUtils;

public class RandomQuestionAnswerBuilder {
	
	public Map<String, String> generateChallengeQuestionAnswer(){
		int questionNumberPosition = pickRandomQuestionNumber();
		if(questionNumberPosition == 0){
			return question0();
		}else if (questionNumberPosition == 1){
			return question1();
		}else  if (questionNumberPosition == 2){
			return question2();
		} else  if (questionNumberPosition == 3) {
			return question3();
		} else if (questionNumberPosition == 4) {
			return question4();
		}  else if (questionNumberPosition == 5) {
			return question5();
		} else if (questionNumberPosition == 6) {
			return question6();
		}  else if (questionNumberPosition == 7) {
			return question7();
		} else if (questionNumberPosition == 8) {
			return question8();
		}  else if (questionNumberPosition == 9) {
			return question9();
		}else {
			return question10();
		}		
	}
	
	private Map<String, String> question10() {
		String question = "What date is tomorrow?";
		Calendar cal = new GregorianCalendar();
		int day = cal.get(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.DAY_OF_MONTH, day + 1);
		Map<String, String> map = new HashMap<String, String> (1);
		map.put(question, String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
		return map;
	}

	private Map<String, String> question9() {
		int digit1 = generateSingleDigit();
		int digit2 = digit1 + 2;
		String question = "What's the  number between " + digit1 + " and " +  digit2 + "?";
		int answer = digit1  + 1;
		Map<String, String> map = new HashMap<String, String> (1);
		map.put(question, String.valueOf(answer));
		return map;
	}

	private Map<String, String> question8() {
		int digit1 = generateSingleDigit();
		int digit2 = generateSingleDigit();
		while(digit1 < digit2){
			digit1 = generateSingleDigit();
		}
		String question = "What's the difference between " + digit1 + " and " +  digit2 + "?";
		int answer = digit1 -  digit2;
		Map<String, String> map = new HashMap<String, String> (1);
		map.put(question, String.valueOf(answer));
		return map;
	}

	private Map<String, String> question7() {
		String question = "What day is tomorrow?";
		Calendar cal = new GregorianCalendar();
		int day = cal.get(Calendar.DAY_OF_WEEK);
		cal.set(Calendar.DAY_OF_WEEK, day + 1);
		Map<String, String> map = new HashMap<String, String> (1);
		map.put(question, cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US));
		return map;
	}
    
	private Map<String, String> question6() {	
		String question = "What is the current year?";
		Calendar cal = new GregorianCalendar();
		Map<String, String> map = new HashMap<String, String> (1);
		map.put(question,String.valueOf(cal.get(Calendar.YEAR)));
		return map;
	}

    private Map<String, String> question5() {	
	String question = "What date is today?";
	Calendar cal = new GregorianCalendar();
	Map<String, String> map = new HashMap<String, String> (1);
	map.put(question,String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
	return map;
}
	
	private Map<String, String> question4() {	
		String question = "What day is today?";
		Calendar cal = new GregorianCalendar();
		Map<String, String> map = new HashMap<String, String> (1);
		map.put(question,cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US));
		return map;
	}

	private Map<String, String> question2() {
		int digit1 = generateSingleDigit();
		int digit2 = digit1 + 2;
		String question = "What's the  number between " + digit1 + " and " +  digit2 + "?";
		int answer = digit1  + 1;
		Map<String, String> map = new HashMap<String, String> (1);
		map.put(question, String.valueOf(answer));
		return map;
	}

	private Map<String, String> question1() {
		int digit1 = generateSingleDigit();
		int digit2 = generateSingleDigit();
		while(digit1 < digit2){
			digit1 = generateSingleDigit();
		}
		String question = "What's the difference between " + digit1 + " and " +  digit2 + "?";
		int answer = digit1 -  digit2;
		Map<String, String> map = new HashMap<String, String> (1);
		map.put(question, String.valueOf(answer));
		return map;
	}
	
	private int generateSingleDigit(){
		return Integer.valueOf(RandomStringUtils.randomNumeric(1));
	}

	private Map<String, String> question0() {
		int digit1 = generateSingleDigit();
		int digit2 = generateSingleDigit();
		String question = "What is the sum of " + digit1 + " and " +  digit2 + "?";
		int answer = digit1 +  digit2;
		Map<String, String> map = new HashMap<String, String> (1);
		map.put(question, String.valueOf(answer));
		return map;
	}
	
	private Map<String, String> question3() {
		String word = RandomStringUtils.randomAlphanumeric(7);
		String question = "Please type the word as it appears between the quote " + "'" + word + "' :";
		
		Map<String, String> map = new HashMap<String, String> (1);
		map.put(question, String.valueOf(word));
		return map;
	}

	private int pickRandomQuestionNumber() {
		String randomNumberStr = RandomStringUtils.randomNumeric(4);
		int randomNumber = Integer.valueOf(randomNumberStr);
		if(randomNumber >= 0 && randomNumber <1000){
			return 0;
		}else if (randomNumber >=1000 && randomNumber < 2000) {
			return 1;
		}else if (randomNumber >=2000 && randomNumber < 3000) {
			return 2;
		}else if (randomNumber >=3000 && randomNumber < 4000) {
			return 3;
		}else if (randomNumber >=4000 && randomNumber < 5000) {
			return 4;
		}else if (randomNumber >=5000 && randomNumber < 6000) {
			return 5;
		}else if (randomNumber >=6000 && randomNumber < 7000) {
			return 6;
		}else if (randomNumber >=7000 && randomNumber < 8000) {
			return 7;
		}else if (randomNumber >=8000 && randomNumber < 9000) {
			return 8;
		}else  {
			return 0;
		}
	}

	public static void main(String[] args) {
		RandomQuestionAnswerBuilder builder = new RandomQuestionAnswerBuilder();	
		System.out.println(builder.question0());
		System.out.println(builder.question1());
		System.out.println(builder.question2());
		System.out.println(builder.question3());
		System.out.println(builder.question4());
		System.out.println(builder.question5());
		System.out.println(builder.question6());
		System.out.println(builder.question7());
		System.out.println(builder.question8());
		System.out.println(builder.question9());
		System.out.println(builder.question10());

	}

}
