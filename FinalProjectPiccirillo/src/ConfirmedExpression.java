public class ConfirmedExpression 
{

	//returns if the string is valid or not
	public static boolean confirmed(DFA dfa, String s) {
		
		State state = dfa.getDfa().getFirst();
		
		//This is a special case for when the string is empty
		if (s.compareTo("e") == 0) 
		{
			//if the first state is an accept then the empty string is value
			if (state.isAcceptState()) 	{	return true; 	}
			else						{	return false; 	}
		//if its not empty like it should be
		} else if (dfa.getDfa().size() > 0)
		{	

			for (int i = 0 ; i < s.length(); i++) 
			{
				//if there's no transitions we have to break
				if (state == null) { break; }
				
				//this gets the transitions without any input
				state = state.getNextState().get(s.charAt(i)).get(0);
			}
			
			//checks if the string is valid
			if (state != null && state.isAcceptState()) {	return true;	} 
			//if the string is invalid
			else 										{	return false;	}
		
		} else 
		{
			return false;
		}
		
	}
}