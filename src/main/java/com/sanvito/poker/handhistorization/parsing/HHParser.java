package com.sanvito.poker.handhistorization.parsing;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import one.util.streamex.StreamEx;

public class HHParser {

	private String ownPlayerName;

	public HHParser(String ownPlayerName) {
		this.ownPlayerName = ownPlayerName;
	}

	public static void parse(Stream<String> lines, String output) throws IOException {
		final FileWriter fw = new FileWriter(output);

		//@formatter:off
		lines.filter(line -> line.contains("===---> Loaded package")).map(line -> line.split(" "))
				.map(arr -> arr[arr.length - 1]).forEach(packageName -> writeToFile(fw, packageName));
		//@formatter:on
		fw.close();
		lines.close();
	}

	private static void writeToFile(FileWriter fw, String packageName) {
		try {
			fw.write(String.format("%s%n", packageName));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static Stream<String> scan(String s) {
		Scanner scanner = new Scanner(new StringReader(s));
		Pattern pattern = Pattern.compile("Hand #.\r\n");
		scanner.useDelimiter(pattern);
		return StreamSupport
				.stream(Spliterators.spliteratorUnknownSize(scanner, Spliterator.ORDERED | Spliterator.NONNULL), false)
				.onClose(scanner::close);
	}

	public static Stream<String> string2StringStream(String s) {
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

	public void writeCSVLine(String... values) {
		try (FileWriter writer = new FileWriter("D:/Delme/hands.csv", true);) {
			for (int i = 0; i < values.length; i++) {
				writer.append(values[i]);
				if (i < values.length - 1) {
					writer.append(',');
				}
			}
			writer.append('\n');
			writer.flush();
		} catch (IOException e) {
			System.out.println("could not write csv file: " + e.getMessage());
		}
	}

	public static String holeCards(String hand) {
		try (Stream<String> lines = string2StringStream(hand);) {
			String holeCards = lines.filter(line -> line.contains("Dealt to StartingSmall"))
					.map(line -> line.split("\\[")).map(arr -> arr[arr.length - 1])
					.map(s -> s.substring(0, s.length() - 1)).findAny().orElse("HOLE_CARDS_FOUND");
			return holeCards;
		}
	}

	public static String flopAction(String hand) {
		try (Stream<String> lines = string2StringStream(hand);) {
			String holeCards = lines.filter(line -> line.contains("*** FLOP ***")).map(line -> line.split("\\["))
					.map(arr -> arr[arr.length - 1]).map(s -> s.substring(0, s.length() - 1)).findAny()
					.orElse("HOLE_CARDS_FOUND");

			return holeCards;
		}
	}

}
