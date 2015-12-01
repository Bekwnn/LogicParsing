import java.util.HashMap;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;

public class FOLUtility
{
	static class Problem
	{
		public String expression1;
		public String expression2;
	}
	
	// parses file and returns a list of problems as
	// expression + value map
	public static ArrayList<Problem> readProblemFile(String filename)
	{
		ArrayList<Problem> problems = new ArrayList<Problem>();
		
		File file = new File(filename);
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(file));
			String line     = null;
			String[] valTokens = null;
			
			while ((line = reader.readLine()) != null) {
				Problem newProblem = new Problem();
				
				Matcher m = Pattern.compile("\\[(.+)\\]\\[(.+)\\]").matcher(line);
				m.find();
				
				newProblem.expression1 = m.group(1);
				newProblem.expression2 = m.group(2);
				
				problems.add(newProblem);
			}
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: File not found.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("ERROR: IOException.");
			e.printStackTrace();
		}
		
		return problems;
	}
}