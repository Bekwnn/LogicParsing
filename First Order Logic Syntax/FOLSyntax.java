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
		if (args.length != 1)
		{
			System.out.println("Usage: java PropositionalSyntax sample.txt");
			return;
		}
		
		ArrayList<FOLUtility.Problem> problems = FOLUtility.readProblemFile(args[0]);
		int count = 0;
		
		for (FOLUtility.Problem prob : problems)
		{
			HashMap<String,String> map = matchingMap(prob.expression1, prob.expression2);
			if (map == null) System.out.println("Result map " + (++count) " failed due to mismatch.");
			else if (hasInfRecursion(map)) System.out.println("Mapping " + (++count) + " failed due to infinite recursion mapping.");
			else printMap("Resulting map " + (++count) + ": ", map);
			System.out.println();
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
				// if one of the mistmatches is an operation, fail
				if (getType(tokenized1[i]) == EPartType.CONNECTIVE ||
					getType(tokenized1[i]) == EPartType.QUANTIFIER ||
					getType(tokenized2[i]) == EPartType.CONNECTIVE ||
					getType(tokenized2[i]) == EPartType.QUANTIFIER)
					{return null;}
				
				//if one of the mismatches is a variable, map it
				else if (getType(tokenized1[i]) == EPartType.VARIABLE ||
					getType(tokenized2[i]) == EPartType.VARIABLE)
				{
					if (!isDuplicatePair(tokenized1[i], tokenized2[i], map))
					{
						insertPair(tokenized1[i], tokenized2[i], map);
						System.out.println("Adding to map " + tokenized1[i] + " : " + tokenized2[i]);
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
	
	//returns true if a map has some mapping x : func(x)
	static boolean hasInfRecursion(HashMap<String,String> map)
	{
		// splits on parenthesis and commas
		// ex func(stuff1,stuff2) => [func, stuff1, stuff2]
		for (Map.Entry<String,String> entry : map.entrySet())
		{
			if (getType(entry.getKey()) == EPartType.FUNCORPRED)
			{
				if (isRecursion(entry.getValue(), entry.getKey(), map)) return true;
			}
			else if (getType(entry.getValue()) == EPartType.FUNCORPRED)
			{
				if (isRecursion(entry.getKey(), entry.getValue(), map)) return true;
			}
		}
		return false;
	}
	
	// recursive call to check if mapping is recursive
	static boolean isRecursion(String var, String func, HashMap<String,String> map)
	{
		System.out.println("Checking recursion on var:"+var+" and func:"+func);
		// for each term check if mapping from var to term exists
		// ex. func(term1,term2(term3)) => does var map to term1, term2(term3), or term3?
		Matcher m = captureFuncOrPred.matcher(func);
		m.find();
		String[] terms = outerCommasToSpaces(m.group(2)).split(" ");
		
		for (String term : terms)
		{
			// if the term is a func or pred, recursive call
			// to check for things like x: a(b(c)), x: c
			if (getType(term) == EPartType.FUNCORPRED)
			{
				if (isRecursion(var, term, map)) return true;
			}
			
			// check for mapping from var to term or vice versa
			for (Map.Entry<String,String> entry : map.entrySet())
			{
				if (term.equals(entry.getKey()) && var.equals(entry.getValue())) return true;
				else if (term.equals(entry.getValue()) && var.equals(entry.getKey())) return true;
			}
		}
		
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
	
	static void printMap(String introLine, HashMap<String,String> map)
	{
		System.out.println(introLine);
		ArrayList<String> keys = new ArrayList<String>(map.keySet());
		for (String key: keys) {
			System.out.println(key + ": " + map.get(key));
		}
	}
}