import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.StringBuilder;

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
		HashMap<String,String> map = matchingMap("Parents(x,father(x),mother(Bill))", "Parents(Bill,father(y),z)");
		
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
		
		String[] tokenized1 = outerCommasToSpaces(expression1).split(" ");
		String[] tokenized2 = outerCommasToSpaces(expression2).split(" ");
		
		// under our rules the tokenized arrays will be the same length
		if (tokenized1.length != tokenized2.length) return null;
		
		for (int i = 0; i < tokenized1.length; i++)
		{
			//if there's a mismatch
			if (!tokenized1[i].equals(tokenized2[i]))
			{
				//if one of the mismatches is a variable, map it
				if (getType(tokenized1[i]) == EPartType.VARIABLE ||
					getType(tokenized2[i]) == EPartType.VARIABLE)
				{
					//if (!mapsToSelf(tokenized1[i], tokenized2[i]))
					if (!isDuplicatePair(tokenized1[i], tokenized2[i], map))
					{
						insertPair(tokenized1[i], tokenized2[i], map);
						System.out.println("Adding to map " + tokenized1[i] + ", " + tokenized2[i]);
					}
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
					if (m1.group(1).equals(m2.group(1)))
					{
						//recurse on the contents inside
						System.out.println(m1.group(2) + " / " + m2.group(2));
						HashMap<String,String> recurseResult = matchingMap(m1.group(2), m2.group(2));
						map = resolveUnion(map, recurseResult);
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
	
	static HashMap<String,String> resolveUnion(HashMap<String,String> mapA, HashMap<String,String> mapB)
	{
		HashMap<String,String> unionMap = new HashMap<String,String>(mapB);
		
		for (Map.Entry<String,String> entry : mapA.entrySet())
		{
			//resolve duplicates
			if (isDuplicatePair(entry.getValue(), entry.getKey(), unionMap)) continue;
			else insertPair(entry.getKey(), entry.getValue(), unionMap);
		}
		
		return unionMap;
	}
	
	static void insertPair(String a, String b, HashMap<String,String> map)
	{
		if (map.containsKey(a)) map.put(b, a);
		else map.put(a, b);
	}
	
	static boolean isDuplicatePair(String a, String b, HashMap<String,String> map)
	{
		if (map.containsValue(a) && map.containsKey(b)) return true;
		else if (map.containsKey(a) && map.containsValue(b)) return true;
		else return false;
	}
	
	//TODO
	static boolean mapsToSelf(String var, String funcOrPred)
	{
		return false;
	}
	
	// converts commas that aren't inside parentheses
	// because of recursive parsing reasons
	static String outerCommasToSpaces(String a)
	{
		StringBuilder newString = new StringBuilder(a);
		int openBracket = 0;
		for (int i = 0; i < newString.length(); i++)
		{
			char c = newString.charAt(i);
			if (c == '(') openBracket++;
			else if (c == ')') openBracket--;
			else if (c == ',' && openBracket == 0) newString.setCharAt(i,' ');
		}
		return newString.toString();
	}
}