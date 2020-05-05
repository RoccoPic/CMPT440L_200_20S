import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;

public class RegularExpression 
{
	private static int stateID = 0;
	
	private static Stack<NFA> stackNfa = new Stack<NFA> ();
	private static Stack<Character> operator = new Stack<Character> ();	

	private static Set<State> set1 = new HashSet <State> ();
	private static Set<State> set2 = new HashSet <State> ();
	
	//this is the input set
	private static Set <Character> input = new HashSet <Character> ();
	
	//makes the NFA with regular expression
	public static NFA generateNFA(String regular) 
	{
		//concatted regular expression
		regular = AddConcat (regular);
		
		//added the only available inputs
		input.add('a');
		input.add('b');
		
		//emptied the stacks
		stackNfa.clear();
		operator.clear();

		for (int i = 0 ; i < regular.length(); i++) 
		{	

			if (isInputCharacter (regular.charAt(i))) 
			{
				pushStack(regular.charAt(i));
				
			} else if (operator.isEmpty()) 
			{
				operator.push(regular.charAt(i));
				
			} else if (regular.charAt(i) == '(') 
			{
				operator.push(regular.charAt(i));
				
			} else if (regular.charAt(i) == ')') 
			{
				while (operator.get(operator.size()-1) != '(') 
				{
					doOperation();
				}				
		
				//pops the '('
				operator.pop();
				
			} else 
			{
				while (!operator.isEmpty() && 
						Priority (regular.charAt(i), operator.get(operator.size() - 1)) )
				{
					doOperation ();
				}
				operator.push(regular.charAt(i));
			}		
		}		
		
		//empties the rest of the stack
		while (!operator.isEmpty()) {	doOperation(); }
		
		//pulls the complete NFA
		NFA completeNfa = stackNfa.pop();
		
		//sets an accepting state for the end of the NFA
		completeNfa.getNfa().get(completeNfa.getNfa().size() - 1).setAcceptState(true);
		
		//returns the NFA
		return completeNfa;
	}
	
	//prioritizes commands
	private static boolean Priority (char first, Character second) 
	{
		if(first == second) {	return true;	}
		if(first == '*') 	{	return false;	}
		if(second == '*')  	{	return true;	}
		if(first == '.') 	{	return false;	}
		if(second == '.') 	{	return true;	}
		if(first == '|') 	{	return false;	} 
		else 				{	return true;	}
	}

	//does the operations based on the top of the stackedNFA
	private static void doOperation () 
	{
		if (RegularExpression.operator.size() > 0) 
		{
			char charAt = operator.pop();

			//depending on the current character we act upon it
			switch (charAt) 
			{
				case ('|'):
					union ();
					break;
	
				case ('.'):
					concatenation ();
					break;
	
				case ('*'):
					star ();
					break;
	
				//just in case there is a typo or an unknown symbol
				default :
					System.out.println("Typo?");
					System.exit(1);
					break;			
			}
		}
	}
		
	//star operation
	private static void star() 
	{
		//retrieves the NFA from the stack
		NFA nfa = stackNfa.pop();
		
		//states are made for the beginning and end state
		State start = new State (stateID++);
		State end	= new State (stateID++);
		
		//set transitions for the states
		start.addTransition(end, 'e');
		start.addTransition(nfa.getNfa().getFirst(), 'e');
		
		nfa.getNfa().getLast().addTransition(end, 'e');
		nfa.getNfa().getLast().addTransition(nfa.getNfa().getFirst(), 'e');
		
		nfa.getNfa().addFirst(start);
		nfa.getNfa().addLast(end);
		
		//the NFA gets put in the back of the stackNFA
		stackNfa.push(nfa);
	}

	//concat operation to merge the states
	private static void concatenation() 
	{
		//retrieves both NFA's
		NFA nfa2 = stackNfa.pop();
		NFA nfa1 = stackNfa.pop();
		
		//adds the end state of NFA1 to the beginning of NFA2
		nfa1.getNfa().getLast().addTransition(nfa2.getNfa().getFirst(), 'e');
		
		//adds all the states to the end of NFA1 from NFA2
		for (State s : nfa2.getNfa()) {	nfa1.getNfa().addLast(s); }

		//pushes the NFA1 back to StackNFA
		stackNfa.push(nfa1);
	}
	
	//unions NFA1 and 2
	private static void union() 
	{
		//loads two NFAs in the stack as variables
		NFA nfa2 = stackNfa.pop();
		NFA nfa1 = stackNfa.pop();
		
		//creates states for the union op
		State start = new State (stateID++);
		State end	= new State (stateID++);

		//sets transitions to the beginning of each NFA with an empty string
		start.addTransition(nfa1.getNfa().getFirst(), 'e');
		start.addTransition(nfa2.getNfa().getFirst(), 'e');

		//sets the transitions to the end of each NFA with another empty string
		nfa1.getNfa().getLast().addTransition(end, 'e');
		nfa2.getNfa().getLast().addTransition(end, 'e');

		//adds the start to the end of each NFA
		nfa1.getNfa().addFirst(start);
		nfa2.getNfa().addLast(end);
		
		//adds all the states in NFA2 to the end of NFA1
		for (State s : nfa2.getNfa()) 
		{
			nfa1.getNfa().addLast(s);
		}
		//puts the NFA back to the stack
		stackNfa.push(nfa1);		
	}
	
	//push the input symbol into the NFAstack
	private static void pushStack(char symbol) 
	{
		State s0 = new State (stateID++);
		State s1 = new State (stateID++);
		
		//add the transition from 0 to 1 with the current symbol
		s0.addTransition(s1, symbol);
		
		// new temporary NFA
		//new temp NFA
		NFA tempNfa = new NFA ();
		
		//adds states to the NFA
		tempNfa.getNfa().addLast(s0);
		tempNfa.getNfa().addLast(s1);		
		
		//puts the NFA back to the stackedNFA
		stackNfa.push(tempNfa);
	}

	//adds the "." when there is a concatenation between the symbols and concats them
	private static String AddConcat(String regular) 
	{
		String newRegular = new String ("");

		for (int i = 0; i < regular.length() - 1; i++) 
		{
			if ( isInputCharacter(regular.charAt(i))  && isInputCharacter(regular.charAt(i+1)) ) 
			{
				newRegular += regular.charAt(i) + ".";
				
			} else if ( isInputCharacter(regular.charAt(i)) && regular.charAt(i+1) == '(' ) 
			{
				newRegular += regular.charAt(i) + ".";
				
			} else if ( regular.charAt(i) == ')' && isInputCharacter(regular.charAt(i+1)) )
			{
				newRegular += regular.charAt(i) + ".";
				
			} else if (regular.charAt(i) == '*'  && isInputCharacter(regular.charAt(i+1)) )
			{
				newRegular += regular.charAt(i) + ".";
				
			} else if ( regular.charAt(i) == '*' && regular.charAt(i+1) == '(' ) 
			{
				newRegular += regular.charAt(i) + ".";
				
			} else if ( regular.charAt(i) == ')' && regular.charAt(i+1) == '(') 
			{
				newRegular += regular.charAt(i) + ".";			
				
			} else 
			{
				newRegular += regular.charAt(i);
			}
		}
		newRegular += regular.charAt(regular.length() - 1);
		return newRegular;
	}

	
	//returns true if the any of the language is false
	private static boolean isInputCharacter(char charAt) 
	{
		if 		(charAt == 'a')	return true;
		else if (charAt == 'b')	return true;
		else if (charAt == 'e')	return true;
		else					return false;
	}

	
	//uses the NFA to produce a DFA
	public static DFA generateDFA(NFA nfa) 
	{
		//generates the DFA
		DFA dfa = new DFA ();

		//clears the ID's to 0
		stateID = 0;

		//sets an arrayList of unprocessed states
		LinkedList <State> unprocessed = new LinkedList<State> ();
		
		//created two sets
		set1 = new HashSet <State> ();
		set2 = new HashSet <State> ();

		//adds the first state to the first set
		set1.add(nfa.getNfa().getFirst());

		//removes the epsilon state from the NFA to make an actual DFA
		removeEpsilonTransition();

		//creates the DFA's start date
		State dfaStart = new State (set2, stateID++);
		
		dfa.getDfa().addLast(dfaStart);
		unprocessed.addLast(dfaStart);
		
		//while the stack isn't empty
		while (!unprocessed.isEmpty()) 
		{
			//removes the last state in the stack
			State state = unprocessed.removeLast();

			//checks the inputted symbol
			for (Character symbol : input) 
			{
				set1 = new HashSet<State> ();
				set2 = new HashSet<State> ();
				moveStates (symbol, state.getStates(), set1);
				removeEpsilonTransition ();
				boolean found = false;
				State states = null;

				for (int i = 0 ; i < dfa.getDfa().size(); i++) 
				{
					states = dfa.getDfa().get(i);

					if (states.getStates().containsAll(set2)) 
					{
						found = true;
						break;
					}
				}

				//if it is not in the DFA
				if (!found) 
				{
					State s = new State (set2, stateID++);
					unprocessed.addLast(s);
					dfa.getDfa().addLast(s);
					state.addTransition(s, symbol);

				//if it is already in 
				} else 
				{
					state.addTransition(states, symbol);
				}
			}			
		}
		//returns the dfa
		return dfa;
	}

	//removes the epsilon transition
	private static void removeEpsilonTransition() 
	{
		Stack <State> stack = new Stack <State> ();
		set2 = set1;

		for (State st : set1) 
		{ 
			stack.push(st);	
		}

		while (!stack.isEmpty()) 
		{
			State state = stack.pop();
			ArrayList <State> epsilonStates = state.getAllTransitions ('e');

			for (State s : epsilonStates) 
			{
				//if s is not in the s then we add
				if (!set2.contains(s)) 
				{
					set2.add(s);
					stack.push(s);
				}				
			}
		}		
	}

	//states move based on the input symbol
	private static void moveStates(Character c, Set<State> states,	Set<State> set) 
	{
		ArrayList <State> temp = new ArrayList<State> ();

		for (State st : states) 
		{	
			temp.add(st);	
		}
		for (State st : temp) 
		{			
			ArrayList<State> allStates = st.getAllTransitions(c);

			for (State p : allStates)
			{	
				set.add(p);	
			}
		}
	}	
}