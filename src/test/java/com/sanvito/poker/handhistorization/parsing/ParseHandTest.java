package com.sanvito.poker.handhistorization.parsing;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ParseHandTest {

	static String hand1 = "Hand #1\r\n" + "\r\n" //
			+ "PokerStars Zoom Hand #131026229495:  Omaha Pot Limit ($0.25/$0.50) - 2015/02/23 16:28:31 ET\r\n" //
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

//	@Test
//	public void getHoleCars() {
//		Double startingStack = HHParser.getStartingStack(hand1);
//		assertEquals("8d Ts 7h 2c", holeCards);
//	}

}
