package com.sanvito.poker.handhistorization.parsing;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class BasicParser {

	public static void main(String[] args) throws IOException {
		Stream<String> lines = null;
		if (args.length == 2) {
			lines = Files.lines(Paths.get(args[1]));
		} else {
			String s1 = "2015-01-06 11:33:03 b.s.d.task [INFO] Emitting: adEventToRequestsBolt __ack_ack [-6722594615019711369 -1335723027906100557]";
			String s2 = "2015-01-06 11:33:03 b.s.d.executor [INFO] Processing received message source: eventToManageBolt:2, stream: __ack_ack, id: {}, [-6722594615019711369 -1335723027906100557]";
			String s3 = "2015-01-06 11:33:04 c.s.p.d.PackagesProvider [INFO] ===---> Loaded package com.foo.bar";
			String s4 = "2015-01-06 11:33:04 c.s.p.d.PackagesProvider [INFO] ===---> Loaded package co.il.boo";
			String s5 = "2015-01-06 11:33:04 c.s.p.d.PackagesProvider [INFO] ===---> Loaded package dot.org.biz";
			List<String> rows = Arrays.asList(s1, s2, s3, s4, s5);
			lines = rows.stream();
		}
		new BasicParser().parse(lines, args[0]);
	}

	public void parse(Stream<String> lines, String output) throws IOException {
		final FileWriter fw = new FileWriter(output);

		// @formatter:off
		lines.filter(line -> line.contains("===---> Loaded package")).map(line -> line.split(" "))
				.map(arr -> arr[arr.length - 1]).forEach(packageName -> writeToFile(fw, packageName));
		// @formatter:on
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

}
