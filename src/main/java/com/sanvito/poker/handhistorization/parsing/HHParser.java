package com.sanvito.poker.handhistorization.parsing;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.sanvito.poker.handhistorization.equity.Equity;
import com.sanvito.poker.handhistorization.generate.CSVWriter;
import com.sanvito.pokereval.config.SpringConfig;
import com.sanvito.pokereval.controller.DecisionLogic;
import com.sanvito.pokereval.controller.DecisionLogicImpl;

public class HHParser {

	private String ownPlayerName;
	private static final String FLOPSTART = "### FLOP ###";
	private static final String FLOPEND = "### TURN";
	private static final String PREFLOPSTART = "### HOLE CARDS ###";
	private static final String PREFLOPEND = FLOPSTART;

	public HHParser(String ownPlayerName) {
		this.ownPlayerName = ownPlayerName;
	}

	public void parse(Stream<String> lines, String output) throws IOException {
		final FileWriter fw = new FileWriter(output);

		//@formatter:off
		lines.filter(line -> line.contains("===---> Loaded package")).map(line -> line.split(" "))
				.map(arr -> arr[arr.length - 1]).forEach(packageName -> writeToFile(fw, packageName));
		//@formatter:on
		fw.close();
		lines.close();
	}

	private void writeToFile(FileWriter fw, String packageName) {
		try {
			fw.write(String.format("%s%n", packageName));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Stream<String> scan(InputStream s) {
		Scanner scanner = new Scanner(s);
		Pattern pattern = Pattern.compile("Hand #[0-9]*\r\n");
		scanner.useDelimiter(pattern);
		return StreamSupport
				.stream(Spliterators.spliteratorUnknownSize(scanner, Spliterator.ORDERED | Spliterator.NONNULL), false)
				.onClose(scanner::close);
	}

	public Stream<String> string2StringStream(String s) {
		return Arrays.asList(s.split("\r\n")).stream();
	}

	// public static List<String> withStreamEx(String s) {
	// List<String> allHands = new ArrayList<>();
	// StreamEx.ofLines(new StringReader(s)).groupRuns((a, b) ->
	// !b.startsWith("Hand #"))
	// .forEachOrdered(list -> list.subList(1, list.size()).addAll(allHands));
	// return allHands;
	// }

	// public static List<String> withStreamEx(String s) {
	// List<String> allHands = new ArrayList<>();
	// List<String> lines = Arrays.asList(s.split("\r\n"));
	// for (String x : lines) {
	// System.out.println(x);
	// }
	// Stream<ObjectDefinedByStrings> result =
	// StreamEx.of(strings).groupRuns((a, b) -> !b.contains("<start mark>"))
	// .map(stringList -> constructObjectDefinedByStrings());
	// StreamEx.of(lines).groupRuns((a, b) -> !b.startsWith("Hand #")).map(l ->
	// l.get(0))
	// .forEach(ss -> allHands.add(ss));
	// return allHands;
	// }

	// private static Stream<String>
	// constructObjectDefinedByStrings(List<String> strs) {
	// Stream<String> s;
	// for (String x : strs) {
	// s = string2StringStream(x);
	// }
	// return s;
	// }

	public String holeCards(String hand) {
		try (Stream<String> lines = string2StringStream(hand);) {
			String holeCards = lines.filter(line -> line.contains("Dealt to " + ownPlayerName))
					.map(line -> line.split("\\[")).map(arr -> arr[arr.length - 1])
					.map(s -> s.substring(0, s.length() - 1)).findAny().orElse("HOLE_CARDS_FOUND");
			return holeCards;
		}
	}

	public String getStartingStack(String hand) {
		try (Stream<String> lines = string2StringStream(hand);) {
			return lines.filter(line -> line.contains(ownPlayerName + " (")).map(line -> line.split("\\$"))
					.map(arr -> arr[arr.length - 1]).map(s -> s.substring(0, s.indexOf(" "))).findFirst()
					.orElse("STARTINGSTACK_NOT_FOUND");
		}
	}

	public String getFlopAction(String hand) {
		return getAction(FLOPSTART, FLOPEND, hand, 1);
	}

	public String getPreFlopAction(String hand) {
		return getAction(PREFLOPSTART, PREFLOPEND, hand, 2);
	}

	private String getAction(String startPattern, String endPattern, String hand, int lineIdx) {
		hand = hand.replace("*", "#");
		String PATTERN = startPattern + ".*" + endPattern;
		Pattern pattern = Pattern.compile(PATTERN, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(hand);
		List<String> result = new ArrayList<>();
		while (matcher.find()) {
			result.add(matcher.group(0));
		}
		String streetStr = "";
		if (!result.isEmpty()) {
			String[] flopLines = result.get(0).split("\\r?\\n");

			for (int i = lineIdx; i < flopLines.length - 1; i++) {
				streetStr += flopLines[i] + "\r\n";
			}
		}
		return streetStr;
	}

	public String getWinner(String hand) {
		String[] lines = hand.split("\r\n");
		String lastLine = lines[lines.length - 1];
		String[] winnerContainingPart = lastLine.split(": ");
		String[] winner = winnerContainingPart[1].split(" ");
		return winner[0];
	}

	public Double getBB(String hand) {
		String PATTERN = "(\\$\\d+(.\\d+)?)";
		Pattern pattern = Pattern.compile(PATTERN);
		Matcher matcher = pattern.matcher(hand);
		matcher.find();
		if (matcher.find()) {
			return Double.parseDouble(matcher.group().substring(1));
		} else {
			System.out.println("NO BB found in hand: " + hand);
			return -1d;
		}

	}

	public double getPreflopPotMultiple(String hand) {
		String PATTERN = "to \\$([0-9]+[.][0-9]+)";
		Pattern pattern = Pattern.compile(PATTERN, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(getPreFlopAction(hand));
		String raiseWord = null;
		while (matcher.find()) {
			raiseWord = matcher.group();
		}
		if (null != raiseWord) {
			raiseWord = raiseWord.split("\\$")[1];
			return (Double.parseDouble(raiseWord) * 2) / (getBB(hand) * 2);
		} else {
			return 1;
		}

	}

	public double getPreflopPotMultipleDistribution(String hand) {
		String PATTERN = ".* to \\$([0-9]+.*)";
		Pattern pattern = Pattern.compile(PATTERN);
		Matcher matcher = pattern.matcher(getPreFlopAction(hand));
		String raiseLine = null;
		double initialPot = getBB(hand) * 2;
		double myRaises = 0;
		double opponentRaises = 0;
		while (matcher.find()) {
			raiseLine = matcher.group();
			String raiser = raiseLine.split(":")[0];
			double raiseAmount = Double.parseDouble(raiseLine.split("\\$")[1].split(" ")[0]);
			if (ownPlayerName.equals(raiser)) {
				myRaises += raiseAmount * 2;
			} else {
				opponentRaises += raiseAmount * 2;
			}
		}
		double result = 0.5;
		if (null != raiseLine) {
			if (0 != myRaises || 0 != opponentRaises) {
				result = (myRaises + initialPot) / (opponentRaises + myRaises + initialPot);
				result = Math.round(result * 100d) / 100d;
			}
		}
		return result;
	}


	public void hand2CSV(String hand) {
		if (hand.startsWith("\r\nPokerStars")) {
			String holeCards = holeCards(hand);
			String myEq = Equity.getHoleCardEq(holeCards.replaceAll(" ", "")) + "";
			String preflopPotMultiple = getPreflopPotMultiple(hand) + "";
			String preflopAggressionDistr = getPreflopPotMultipleDistribution(hand) + "";
			String winnings = getWinnings(hand);
			Double bb = getBB(hand);
			int out = 0;
			if (Double.parseDouble(myEq) > 0.5)
				out = 1;
			System.out.println(myEq + "," + bb + "," + preflopPotMultiple + "," + preflopAggressionDistr + ","
					+ (Double.parseDouble(winnings) / bb) + "");
			CSVWriter.writeCSVLine(myEq, preflopPotMultiple, preflopAggressionDistr,
					(Double.parseDouble(winnings) / bb) + "");
		}
	}

	public String getWinnings(String hand) {
		Double bb = getBB(hand);
		String PATTERN;
		if (hand.contains("collected (")) {
			PATTERN = ".* collected \\(.*";
		} else {
			PATTERN = ".* and won \\(.*";
		}
		Pattern pattern = Pattern.compile(PATTERN);
		Matcher matcher = pattern.matcher(hand);
		if (matcher.find()) {
			String line = matcher.group();
			String finalPot = line.substring(line.lastIndexOf("(") + 2, line.lastIndexOf(")"));
			Double bbWon = Double.parseDouble(finalPot) / bb;
			String winner = line.split(" ")[2];
			if (!winner.equals(ownPlayerName)) {
				bbWon = -1 * bbWon;
			}
			return bbWon.toString();
		} else {
			return null;
		}
	}

}
