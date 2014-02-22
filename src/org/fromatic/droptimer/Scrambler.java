package org.fromatic.droptimer;

import java.util.Random;

public class Scrambler {
	private static String[] turns = {"U", "D", "R", "L", "F", "B"};
	private static Random random = new Random();
	
	public static String scramble() {
		String scramble = "";
		for (int i = 0; i < 30; i++) {
			String nextTurn = "";
			if (random.nextInt(20) == 1) 
				nextTurn += "2";
			
			 nextTurn += turns[random.nextInt(turns.length)];
			
			 if (random.nextInt(2) == 1) 
				nextTurn += "\'";
			
			scramble += nextTurn + " ";
		}
		return scramble;
	}
}
