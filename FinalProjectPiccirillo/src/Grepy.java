import java.util.ArrayList;
import java.util.Scanner;

public class Grepy {
	private static Scanner s;
	private static String regular;
	
	//reads all the expressions from the arrayList
	private static ArrayList<String> expressions = new ArrayList<String>();
	//stores our NFA
	private static NFA nfa;
	//stores our DFA
	private static DFA dfa;

	public static void main(String[] args) 
	{
		//creates a scanner object
		s = new Scanner (System.in);

		//reads the reg expression
		regular = s.next();

		//reads the expressions to the apply the regular expression
		while(s.hasNext()) {	expressions.add (s.next());		}
		
		//generates the NFA using thompsons algorithms with the reg expressions
		setNfa (RegularExpression.generateNFA (regular));		
		
		//generates the DFA using the previous NFA and subsets the construction algorithm
		setDfa (RegularExpression.generateDFA (getNfa()));
		
		//validates all the strings with the DFA
		//yes means that it is valid, no means it is invalid
		for (String str : expressions) 
		{
			if (ConfirmedExpression.confirmed(getDfa(), str)) {	System.out.println ("yes");	}
			else 											{	System.out.println ("no");	}
		}
			
	}

	// Getters and Setters
	public static NFA getNfa() 
	{
		return nfa;
	}

	public static void setNfa(NFA nfa) 
	{
		Grepy.nfa = nfa;
	}

	public static DFA getDfa() 
	{
		return dfa;
	}

	public static void setDfa(DFA dfa) 
	{
		Grepy.dfa = dfa;
	}
}