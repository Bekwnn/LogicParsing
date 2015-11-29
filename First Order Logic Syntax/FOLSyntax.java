import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.regex.Pattern;

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
	
	public static void main(String[] args)
	{
		matchingMap("Loves(x,y)", "Loves(Dog(Fred),Fred)");
	}
	
	// returns null if failure
	static HashMap<String,String> matchingMap(String expression1, String expression2)
	{
		HashMap<String,String> map = new HashMap<String,String>();
		
		String[] tokenized1 = expression1.split(" ");
		String[] tokenized2 = expression2.split(" ");
		
		for (int i = 0; i < tokenized1.length; i++)
		{
			//if there's a mismatch
			if (!tokenized1[i].equals(tokenized2[i]))
			{
				if (tokenized1[i].getType == EPartType.VARIABLE)
				{
					if (mapsToSelf(tokenized1[i], tokenized2[i]))
					map.put(tokenized1[i], tokenized2[i]);
					
				}
				else if (tokenized2[i].getType == EPartType.VARIABLE)
				{
					if (mapsToSelf(tokenized2[i], tokenized1[i]))
				}
				else return null;
			}
		}
		
		return null;
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
		Pattern.matches
	}
}