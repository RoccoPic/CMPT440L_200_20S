import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//state class to map the transitions for each input
//needed to make my NFA and DFA classes work
public class State {
	private int stateID;
	private Map<Character, ArrayList<State>> nextState;
	private Set <State> states;
	private boolean acceptState;
	
	//constructor for my NFA class
	public State (int ID) 
	{
		this.setStateID(ID);
		this.setNextState(new HashMap <Character, ArrayList<State>> ());
		this.setAcceptState(false);
	}
	
	//constructor for my DFA class
	public State(Set<State> states, int ID) 
	{
		this.setStates(states);
		this.setStateID(ID);
		this.setNextState(new HashMap <Character, ArrayList<State>> ());
		
		//checking if the current state is accepting or not
		for (State p : states) 
		{
			if (p.isAcceptState()) 
			{
				this.setAcceptState(true);
				break;
			}
		}
	}

	//adding transitions between states and moving them into the arrayList
	//also can create an arrayList based on the key
	public void addTransition (State next, char key)
	{
		ArrayList <State> list = this.nextState.get(key);		
		if (list == null) 
		{
			list = new ArrayList<State> ();
			this.nextState.put(key, list);
		}	
		list.add(next);
	}

	//Depending on the symbol we get the transition states
	public ArrayList<State> getAllTransitions(char c) 
	{	
		if (this.nextState.get(c) == null)	
		{	
			return new ArrayList<State> ();	
		}
		else 								
		{	
			return this.nextState.get(c);	
		}		
	}
	
	//Automatically generated Getter n Setter List
	public Map<Character, ArrayList<State>> getNextState() 
	{
		return nextState;
	}

	public void setNextState(HashMap<Character, ArrayList<State>> hashMap) 
	{
		this.nextState = hashMap;
	}
	
	public int getStateID() 
	{
		return stateID;
	}

	public void setStateID(int stateID) 
	{
		this.stateID = stateID;
	}

	public boolean isAcceptState() 
	{
		return acceptState;
	}

	public void setAcceptState(boolean acceptState) 
	{
		this.acceptState = acceptState;
	}

	public Set <State> getStates() 
	{
		return states;
	}

	public void setStates(Set <State> states) 
	{
		this.states = states;
	}
}