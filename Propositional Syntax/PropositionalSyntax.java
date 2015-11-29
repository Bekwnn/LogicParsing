import java.util.Stack;
import java.util.HashMap;

public class PropositionalSyntax
{
	enum EOperType {
		eDIMPLY, //double imply
		eIMPLY,
		eAND,
		eOR,
		eNOT,
		eISNTOPER //isn't an operator
	}
	public static final String DIMPLY = "=";
	public static final String IMPLY = ">";
	public static final String AND = "&";
	public static final String OR = "|";
	public static final String NOT = "!";
	
	public static void main(String[] args)
	{
		HashMap<String, Boolean> I1 = new HashMap<String, Boolean>();
		
		I1.put("p1", false);
		I1.put("p2", true);
		I1.put("p3", false);
		I1.put("p4", true);
		I1.put("p5", false);
		I1.put("p6", true);
		
		calcPart3Expression(I1);
		
		HashMap<String, Boolean> I2 = new HashMap<String, Boolean>();
		
		I2.put("p1", true);
		I2.put("p2", false);
		I2.put("p3", true);
		I2.put("p4", false);
		I2.put("p5", true);
		I2.put("p6", false);
		
		calcPart3Expression(I2);
	}
	
	static void calcPart3Expression(HashMap<String, Boolean> myMap)
	{
		// calculate A
		boolean resultA = evalExpression("& > p1 & p2 p3 > ! p1 & p3 p4", myMap);
		System.out.println("Result A: " + resultA);
		myMap.put("A", resultA);
		// calculate B
		boolean resultB = evalExpression("& > p3 ! p6 > ! p3 > p4 p1", myMap);
		System.out.println("Result B: " + resultB);
		myMap.put("B", resultB);
		// calculate C
		boolean resultC = evalExpression("& ! & p2 p5 > p2 p5", myMap);
		System.out.println("Result C: " + resultC);
		myMap.put("C", resultC);
		// calculate D
		boolean resultD = evalExpression("! > p3 p6", myMap);
		System.out.println("Result D: " + resultD);
		myMap.put("D", resultD);
		
		// calculate final formula E
		boolean resultE = evalExpression("> & A & B C D", myMap);
		System.out.println("Result Final E: " + resultE + "\n");
	}
	
	static boolean evalExpression(String expression, HashMap<String, Boolean> values)
	{
		Stack<Boolean> valueStack = new Stack<Boolean>();
		String[] tokenized = expression.split(" ");
		for (int i = tokenized.length-1; i >= 0; i--)
		{
			EOperType operation = operType(tokenized[i]);
			//if it's not an operation, get value from map and push onto stack
			if (operation == EOperType.eISNTOPER)
			{
				boolean temp = values.get(tokenized[i]);
				valueStack.push(temp);
				System.out.println("Pushed " + tokenized[i] + " onto stack.");
			}
			else
			{
				boolean temp; //store our result
				switch(operation)
				{
					case eDIMPLY:
						temp = operDIMPLY(valueStack.pop(), valueStack.pop());
						break;
					case eIMPLY:
						temp = operIMPLY(valueStack.pop(), valueStack.pop());
						break;
					case eAND:
						temp = operAND(valueStack.pop(), valueStack.pop());
						break;
					case eOR:
						temp = operOR(valueStack.pop(), valueStack.pop());
						break;
					case eNOT:
						temp = operNOT(valueStack.pop());
						break;
					default:
						System.out.println("Default hit in oper switch! something went wrong!");
						temp = false;
						break;
				}
				valueStack.push(temp);
			}
		}
		return valueStack.pop();
	}
	
	static EOperType operType(String token)
	{
		if      (token.equals(DIMPLY)) return EOperType.eDIMPLY;
		else if (token.equals(IMPLY))  return EOperType.eIMPLY;
		else if (token.equals(AND))    return EOperType.eAND;
		else if (token.equals(OR))     return EOperType.eOR;
		else if (token.equals(NOT))    return EOperType.eNOT;
		
		else return EOperType.eISNTOPER;
	}
	
	static boolean operAND(boolean a, boolean b)
	{
		System.out.println("Performing AND");
		return a && b;
	}
	
	static boolean operOR(boolean a, boolean b)
	{
		System.out.println("Performing OR");
		return a || b;
	}
	
	static boolean operNOT(boolean a)
	{
		System.out.println("Performing NOT");
		return !a;
	}
	
	static boolean operIMPLY(boolean a, boolean b)
	{
		System.out.println("Performing IMPLY");
		return operOR(operNOT(a), b);
	}
	
	static boolean operDIMPLY(boolean a, boolean b)
	{
		System.out.println("Performing DIMPLY");
		return operAND(operIMPLY(a, b), operIMPLY(b, a));
	}
}