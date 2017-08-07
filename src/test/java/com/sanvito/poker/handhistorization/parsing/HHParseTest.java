package com.sanvito.poker.handhistorization.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import com.sanvito.pokereval.config.SpringConfig;
import com.sanvito.pokereval.controller.DecisionLogic;
import com.sanvito.pokereval.controller.DecisionLogicImpl;

public class HHParseTest {

	private DecisionLogic dLog = new DecisionLogicImpl();
	// (1) The estimated win probability, p,
	// (2) The ratio of expected winnings if CALL is selected,
	// (3) The ratio of expected winnings if RAISE is selected
	// (4) The current round,
	// (5) The size of the pot,
	// (6) The size of their own bankroll,
	// (7) The number of raises made by the opponent this round,
	// (8) The required cost of a bet.

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

	String hand2 = "Hand #2\r\n" + //
			"\r\n" + //
			"PokerStars Zoom Hand #131026193358:  Omaha Pot Limit ($0.25/$0.50) - 2015/02/23 16:27:58 ET\r\n" + //
			"Table 'Chi Sagittarii' 2-max Seat #1 is the button\r\n" + //
			"Seat 1: FreddyLele ($27.25 in chips) \r\n" + //
			"Seat 2: StartingSmall ($21.19 in chips) \r\n" + //
			"FreddyLele: posts small blind $0.25\r\n" + //
			"StartingSmall: posts big blind $0.50\r\n" + //
			"*** HOLE CARDS ***\r\n" + //
			"Dealt to StartingSmall [2h 5c 7c 4s]\r\n" + //
			"FreddyLele: calls $0.25\r\n" + //
			"StartingSmall: checks \r\n" + //
			"*** FLOP *** [5s 5d Kh]\r\n" + //
			"StartingSmall: checks \r\n" + //
			"FreddyLele: bets $0.96\r\n" + //
			"StartingSmall: calls $0.96\r\n" + //
			"*** TURN *** [5s 5d Kh] [7d]\r\n" + //
			"StartingSmall: checks \r\n" + //
			"FreddyLele: checks \r\n" + //
			"*** RIVER *** [5s 5d Kh 7d] [9c]\r\n" + //
			"StartingSmall: checks \r\n" + //
			"FreddyLele: checks \r\n" + //
			"*** SHOW DOWN ***\r\n" + //
			"StartingSmall: shows [2h 5c 7c 4s] (a full house, Fives full of Sevens)\r\n" + //
			"FreddyLele: mucks hand \r\n" + //
			"StartingSmall collected $2.79 from pot\r\n" + //
			"*** SUMMARY ***\r\n" + //
			"Total pot $2.92 | Rake $0.13 \r\n" + //
			"Board [5s 5d Kh 7d 9c]\r\n" + //
			"Seat 1: FreddyLele (button) (small blind) mucked [Td 3d 9s 3c]\r\n" + //
			"Seat 2: StartingSmall (big blind) showed [2h 5c 7c 4s] and won ($2.79) with a full house, Fives full of Sevens\r\n";

	@Test
	public void parse() throws IOException {
		String s1 = "2015-01-06 11:33:03 b.s.d.task [INFO] Emitting: adEventToRequestsBolt __ack_ack [-6722594615019711369 -1335723027906100557]";
		String s2 = "2015-01-06 11:33:03 b.s.d.executor [INFO] Processing received message source: eventToManageBolt:2, stream: __ack_ack, id: {}, [-6722594615019711369 -1335723027906100557]";
		String s3 = "2015-01-06 11:33:04 c.s.p.d.PackagesProvider [INFO] ===---> Loaded package com.foo.bar";
		String s4 = "2015-01-06 11:33:04 c.s.p.d.PackagesProvider [INFO] ===---> Loaded package co.il.boo";
		String s5 = "2015-01-06 11:33:04 c.s.p.d.PackagesProvider [INFO] ===---> Loaded package dot.org.biz";
		List<String> rows = Arrays.asList(s1, s2, s3, s4, s5);
		Stream<String> lines = rows.stream();
		new BasicParser().parse(lines, "D:/Delme/hh_output.txt");
	}

	@Test
	public void hand2Lines() {
		List<String> rows = Arrays.asList(hand1.split("\r\n"));
		assertTrue(30 == rows.size());
	}

	@Test
	public void getHoleCars() {
		String holeCards = HHParser.holeCards(hand1);
		assertEquals("8d Ts 7h 2c", holeCards);
	}

	@Test
	public void parse2Hands() {
		String hands = hand1 + hand2;
		Stream<String> scannedBlocks = HHParser.scan(hands);
		List<String> holdCards = scannedBlocks.map(h -> HHParser.holeCards(h))
				.collect(Collectors.toCollection(ArrayList::new));
		assertTrue("8d Ts 7h 2c".equals(holdCards.get(0)));
		assertTrue("2h 5c 7c 4s".equals(holdCards.get(1)));
	}

	private String formatHands(String[] hands) {
		String handsStr = "";
		for (String hand : hands) {
			if (!handsStr.isEmpty()) {
				handsStr += ":";
			}
			handsStr += hand;
		}
		return handsStr;
	}

	@BeforeClass
	public static void init() {
		SpringConfig c = new SpringConfig();
	}

	@Test
	public void testDecisionLogicImport() {
		Map<String, Double> eq = dLog.getEq("", "", formatHands(new String[] { "8d8h", "9h9d" }));
		assertTrue(eq.get("8d8h") < eq.get("9h9d"));
	}

	@Test
	public void hand2Object() {
		String hands = hand1 + hand2;
		Stream<String> handsStream = HHParser.scan(hands);
		List<Hand> handList = new ArrayList<>();
		handsStream.sequential().forEach(h -> handList.add(new Hand("", h)));
		System.out.println(handList.get(0).getFlopAction());

		// List<String> holdCards = scannedBlocks.map(h ->
		// HHParser.holeCards(h))
		// .collect(Collectors.toCollection(ArrayList::new));
		// assertTrue("8d Ts 7h 2c".equals(holdCards.get(0)));
		// assertTrue("2h 5c 7c 4s".equals(holdCards.get(1)));
		// Map<String, Double> eq = dLog.getEq("", "", formatHands(new String[]
		// { "8d8h", "9h9d" }));
		// assertTrue(eq.get("8d8h") < eq.get("9h9d"));
	}

	// @Test
	// public void parse2HandsWithStreamEx() {
	// String hands = hand1 + hand2;
	// List<String> allHands = HHParser.withStreamEx(hands);
	// assertEquals(2, allHands.size());
	// System.out.println(allHands);
	// assertTrue("8d Ts 7h 2c".equals(HHParser.holeCards(allHands.get(0))));
	// assertTrue("2h 5c 7c 4s".equals(HHParser.holeCards(allHands.get(1))));
	// }

}
