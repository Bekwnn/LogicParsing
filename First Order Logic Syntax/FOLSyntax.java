import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class FOLSyntax
{
	enum EPartType {
		CONNECTIVE,
		QUANTIFIER,
		FUNCORPRED, //function or predicate
		VARIABLE
	}
	
	public static List<String> connectives = Arrays.asList("&", "|", ">", "=", "!"); // and, or, not, imply
	public static List<String> quantifiers = Arrays.asList("A", "E", ":");	         // for all, there exists, such that
	
	// captures a function/predicate name and its contents 
	public static Pattern captureFuncOrPred = Pattern.compile("(\\w+?)\\((.+)\\)");
	
	public static void main(String[] args)
	{
		HashMap<String,String> map = matchingMap("Loves(x,y)", "Loves(Dog(Fred),Fred)");
		
		System.out.println("Resulting map: ");
		ArrayList<String> keys = new ArrayList<String>(map.keySet());
		for (String key: keys) {
			System.out.println(key + ": " + map.get(key));
		}
	}
	
	// returns null if failure
	static HashMap<String,String> matchingMap(String expression1, String expression2)
	{
		HashMap<String,String> map = new HashMap<String,String>();
		
		String[] tokenized1 = expression1.split(" ");
		String[] tokenized2 = expression2.split(" ");
		
		// under our rules the tokenized arrays will be the same length
		if (tokenized1.length != tokenized2.length) return null;
		
		for (int i = 0; i < tokenized1.length; i++)
		{
			//if there's a mismatch
			if (!tokenized1[i].equals(tokenized2[i]))
			{
				//if one of the mismatches is a variable, just 
				if (getType(tokenized1[i]) == EPartType.VARIABLE)
				{
					//if (!mapsToSelf(tokenized1[i], tokenized2[i]))
					map.put(tokenized1[i], tokenized2[i]);
					System.out.println("Adding to map 1");
				}
				else if (getType(tokenized2[i]) == EPartType.VARIABLE)
				{
					//if (!mapsToSelf(tokenized2[i], tokenized1[i]))
					map.put(tokenized1[i], tokenized2[i]);
					System.out.println("Adding to map 2");
				}
				//if both mismatching things are functions or predicates...
				else if (getType(tokenized1[i]) == EPartType.FUNCORPRED &&
						 getType(tokenized2[i]) == EPartType.FUNCORPRED)
				{
					//then their function handles must match
					Matcher m1 = captureFuncOrPred.matcher(tokenized1[i]);
					Matcher m2 = captureFuncOrPred.matcher(tokenized2[i]);
					m1.find();
					m2.find();
					
					System.out.println(tokenized1[i] + " / " + tokenized2[i]);
					System.out.println(m1.group(1) + " / " + m1.group(2));
					if (m1.group(1).equals(m2.group(1)))
					{
						//recurse on the contents inside
						System.out.println(m1.group(2) + " / " + m2.group(2));
						HashMap recurseResult = matchingMap(m1.group(2), m2.group(2));
						if (recurseResult != null) map.putAll(recurseResult); //unsafe, compiler complaining
					}
					else return null;	//function handles don't match
					
				}
				else return null;
			}
		}
		
		return map;
	}
	
	static EPartType getType(String a)
	{
		if      (connectives.contains(a))           return EPartType.CONNECTIVE;
		else if (quantifiers.contains(a))           return EPartType.QUANTIFIER;
		else if (Pattern.matches(".+\\(.+\\)", a))  return EPartType.FUNCORPRED;
		else                                        return EPartType.VARIABLE;
	}
	
	static boolean mapsToSelf(String variable, String funcorpred)
	{
		return false;
	}
}