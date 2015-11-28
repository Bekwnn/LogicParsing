import java.util.Stack;
import java.util.Map;

public class PropositionalSyntax
{
	enum EOperType {
		DIMPLY, //double imply
		IMPLY,
		AND,
		OR,
		NOT,
		ISNTOPER //isn't an operator
	}
	public static final String DIMPLY = '=';
	public static final String IMPLY = '>';
	public static final String AND = '&';
	public static final String OR = '|';
	public static final String NOT = '!';
	
	public static void main(String[] args)
	{
		Map<String, boolean> myMap = new Map<String, boolean>();
		
	}
	
	static boolean evalExpression(String expression, Map<String, boolean> values)
	{
		Stack<boolean> valueStack = new Stack<boolean>();
		String[] tokenized = expression.split();
		for (int i = tokenized.length-1; i > 0; i--;)
		{
			EOperType operation = operType(tokenized[i]);
			//if it's not an operation, get value from map and push onto stack
			if (operation == EOperType.ISNTOPER)
			{
				boolean temp = values.get(tokenized[i]);
				valueStack.push(temp);
			}
			else
			{
				boolean temp; //store our result
				switch(operation):
				{
					case DIMPLY:
						temp = operDIMPLY(valueStack.pop(), valueStack.pop());
						break;
					case IMPLY:
						temp = operIMPLY(valueStack.pop(), valueStack.pop());
						break;
					case AND:
						temp = operAND(valueStack.pop(), valueStack.pop());
						break;
					case OR:
						temp = operOR(valueStack.pop(), valueStack.pop());
						break;
					case NOT:
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
	}
	
	static EOperType operType(String token)
	{
		if      (token.equals(DIMPLY)) return EOperType.DIMPLY;
		else if (token.equals(IMPLY))  return EOperType.IMPLY;
		else if (token.equals(AND))    return EOperType.AND;
		else if (token.equals(OR))     return EOperType.OR;
		else if (token.equals(NOT))    return EOperType.NOT;
		
		else return ISNTOPER;
	}
	
	static boolean operAND(boolean a, boolean b)
	{
		return a && b;
	}
	
	static boolean operOR(boolean a, boolean b)
	{
		return a || b;
	}
	
	static boolean operNOT(boolean a)
	{
		return !a;
	}
	
	static boolean operIMPLY(boolean a, boolean b)
	{
		return operOR(operNOT(a), b);
	}
	
	static boolean operDIMPLY(boolean a, boolean b)
	{
		return operAND(operIMPLY(a, b), operIMPLY(b, a));
	}
}