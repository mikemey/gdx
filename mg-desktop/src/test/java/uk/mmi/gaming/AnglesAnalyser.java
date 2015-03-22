package uk.mmi.gaming;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class AnglesAnalyser {
	@Test
	public void printDistance() throws Exception {
		List<Object> readLines = FileUtils.readLines(new File("angles.txt"));

		boolean first = true;
		double lastDouble = 0.0;
		double dist;
		double maxD = 0.0;
		int index = 0;

		for (Object obj : readLines) {
			double parseDouble = Double.parseDouble(obj.toString());

			if (first) {
				first = false;
			} else {
				dist = Math.abs(parseDouble - lastDouble);
				System.out.print(++index + ": ");
				if (dist > 2) System.out.print("\t");
				System.out.println(dist);
				if (dist < 10 && dist > maxD) maxD = dist;
			}
			lastDouble = parseDouble;
		}
		System.out.println("max distance: " + maxD);
	}
}
