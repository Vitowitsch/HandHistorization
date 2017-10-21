package com.sanvito.poker.handhistorization.parsing;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

public class ParseHandTest {

	HHParser parser = new HHParser("StartingSmall");

	static String hand1 = // "Hand #1\r\n" + "\r\n" //
			"PokerStars Zoom Hand #131026229495:  Omaha Pot Limit ($0.25/$0.50) - 2015/02/23 16:28:31 ET\r\n" //
					+ "Table 'Chi Sagittarii' 2-max Seat #1 is the button\r\n" //
					+ "Seat 1: StartingSmall ($22.52 in chips) \r\n" //
					+ "Seat 2: FreddyLele ($25.79 in chips) \r\n" //
					+ "StartingSmall: posts small blind $0.25\r\n" //
					+ "FreddyLele: posts big blind $0.50\r\n" //
					+ "*** HOLE CARDS ***\r\n" //
					+ "Dealt to StartingSmall [8d Ts 7h 2c]\r\n" //
					+ "StartingSmall: raises $1 to $1.50\r\n" //
					+ "FreddyLele: raises $3 to $4.50\r\n" //
					+ "StartingSmall: calls $3\r\n" //
					+ "*** FLOP *** [9h 3s Jd]\r\n" //
					+ "FreddyLele: checks \r\n" //
					+ "StartingSmall: bets $8.60\r\n"//
					+ "FreddyLele: raises $12.69 to $21.29 and is all-in\r\n"//
					+ "StartingSmall: calls $9.42 and is all-in\r\n" //
					+ "Uncalled bet ($3.27) returned to FreddyLele\r\n"//
					+ "*** TURN *** [9h 3s Jd] [Ad]\r\n" //
					+ "*** RIVER *** [9h 3s Jd Ad] [6d]\r\n"//
					+ "*** SHOW DOWN ***\r\n" //
					+ "FreddyLele: shows [Qc 8h Ah Jc] (two pair, Aces and Jacks)\r\n"//
					+ "StartingSmall: shows [8d Ts 7h 2c] (high card Ace)\r\n" //
					+ "FreddyLele collected $44.54 from pot\r\n" //
					+ "*** SUMMARY ***\r\n" //
					+ "Total pot $45.04 | Rake $0.50 \r\n"//
					+ "Board [9h 3s Jd Ad 6d]\r\n" //
					+ "Seat 1: StartingSmall (button) (small blind) showed [8d Ts 7h 2c] and lost with high card Ace\r\n"//
					+ "Seat 2: FreddyLele (big blind) showed [Qc 8h Ah Jc] and won ($44.54) with two pair, Aces and Jacks\r\n";

	String hand2 = "PokerStars Zoom Hand #129708318936:  Omaha Pot Limit ($1/$2) - 2015/02/01 5:22:33 ET\r\n" + //
			"Table 'Iota Apodis' 2-max Seat #1 is the button\r\n" + //
			"Seat 1: TGSM89 ($229.04 in chips) \r\n" + //
			"Seat 2: StartingSmall ($200 in chips) \r\n" + //
			"TGSM89: posts small blind $1\r\n" + //
			"StartingSmall: posts big blind $2\r\n" + //
			"*** HOLE CARDS ***\r\n" + //
			"Dealt to StartingSmall [Jc Kh 5s 3c]\r\n" + //
			"TGSM89: raises $3 to $5\r\n" + //
			"StartingSmall: folds \r\n" + //
			"Uncalled bet ($3) returned to TGSM89\r\n" + //
			"TGSM89 collected $4 from pot\r\n" + //
			"TGSM89: doesn't show hand \r\n" + //
			"*** SUMMARY ***\r\n" + //
			"Total pot $4 | Rake $0 \r\n" + //
			"Seat 1: TGSM89 (button) (small blind) collected ($4)\r\n" + //
			"Seat 2: StartingSmall (big blind) folded before Flop";

	@Test
	public void getStartingStack() {
		String startingStack = parser.getStartingStack(hand1);
		assertEquals("22.52", startingStack);
	}

//	@Test
	public void getWinnings() {
		String winnings = parser.getWinnings(hand1);
		assertEquals("44.54", winnings);
		winnings = parser.getWinnings(hand2);
		assertEquals("4", winnings);
	}

	@Test
	public void getFlopAction() {
		String flopAction = parser.getFlopAction(hand1);
		Assert.assertTrue(flopAction.startsWith("FreddyLele: checks"));
		Assert.assertTrue(flopAction.endsWith("returned to FreddyLele\r\n"));
	}

	@Test
	public void getPreFlopAction() {
		String preflopAction = parser.getPreFlopAction(hand1);
		Assert.assertTrue(preflopAction.startsWith("StartingSmall: raises $1 to $1.50"));
		Assert.assertTrue(preflopAction.endsWith("StartingSmall: calls $3\r\n"));
	}

	@Test
	public void getWinner() {
		String winner = parser.getWinner(hand1);
		Assert.assertEquals("get winner test failed: calculated winner was: " + winner, "FreddyLele", winner);
	}

	@Test
	public void getBB() {
		Assert.assertTrue(0.50 == parser.getBB(hand1));
		Assert.assertTrue(2 == parser.getBB(hand2));
	}

	@Test
	public void getPreflopPotMultiple() {
		Assert.assertTrue(9 == parser.getPreflopPotMultiple(hand1));
	}

	@Test
	public void getPreflopPotMultipleDistribution() {
		Assert.assertTrue(0.33 == parser.getPreflopPotMultipleDistribution(hand1));
	}

}
