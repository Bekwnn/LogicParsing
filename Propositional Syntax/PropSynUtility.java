import java.util.HashMap;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;

public class PropSynUtility
{
	static class Problem
	{
		public Problem() {values = new HashMap<String,Boolean>();}
		
		public String expression;
		public HashMap<String,Boolean> values;
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
			String expr     = null;
			String[] valTokens = null;
			
			while ((expr = reader.readLine()) != null) {
				Problem newProblem = new Problem();
				
				newProblem.expression = expr;
				
				String tempLine = reader.readLine();
				valTokens = tempLine.split(" ");
				
				for (String token : valTokens)
				{
					Matcher m = Pattern.compile("(.+)=([t|T|f|F])").matcher(token);
					m.find();
					
					boolean bTemp = (m.group(2).equals("t") || m.group(2).equals("T"));
					
					newProblem.values.put(m.group(1), bTemp);
				}
				
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