package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;

import logProcessor.LogProcessor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LogProcessorTest {
	String expectedOutputDir;
	String testOutputDir;

	@Before
	public void setUp() throws Exception {
		expectedOutputDir = "src/expectedOutput";
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMain() throws IOException {

		String testOutputDir = "src/testOutput";
		String testInputDir = "src/testData";
		String threadCount = "5";

		String[] args = { "-input", testInputDir, "-output", testOutputDir,
				"-threadCount", threadCount };
		LogProcessor.main(args);

		checkAllFilesExists(testOutputDir);
		checkLineNumberMatches(expectedOutputDir, testOutputDir);
	}

	/**
	 * Checks all the files in expected files folder has been generated in
	 * testOutput directory
	 * 
	 * @param testOutputDir
	 *            Directory where the files were written into by logProcessor
	 * @return true if perfect match ; false otherwise
	 */
	public boolean checkAllFilesExists(String testOutputDir) {
		String[] expectedFileNames = (new File(expectedOutputDir)).list();
		String[] testFilesNames = (new File(testOutputDir)).list();

		HashSet<String> hs = new HashSet<>();

		for (String testFile : testFilesNames) {
			hs.add(testFile);
		}

		for (String expectedFileName : expectedFileNames) {
			if (!hs.contains(expectedFileName)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks if file content matches between expected and test output taking
	 * inn a random file from expected files folder
	 * 
	 * @param expectedOutputDir
	 * @param testOutputDir
	 * @return true if the match is perfect ; false otherwise
	 * @throws IOException
	 */
	@SuppressWarnings({ "resource", "unused" })
	public boolean checkLineNumberMatches(String expectedOutputDir,
			String testOutputDir) throws IOException {

		String[] expectedFileNames = (new File(expectedOutputDir)).list();

		Random ran = new Random();
		int index = ran.nextInt(expectedFileNames.length);
		String fileNameToBeChecked = expectedFileNames[index];

		File fileExpec = new File(expectedOutputDir + "/" + fileNameToBeChecked);
		FileInputStream fStream_expected = new FileInputStream(fileExpec);

		File fileTest = new File(testOutputDir + "/" + fileNameToBeChecked);
		FileInputStream fStream_test = new FileInputStream(fileTest);

		int readChars = 0, readChars2 = 0;
		byte[] b = new byte[1024];
		byte[] testFile_bytes = new byte[1024];

		while ((readChars = fStream_expected.read(b, 0, b.length)) != -1) {
			if (((readChars2 = fStream_test.read(testFile_bytes, 0,
					testFile_bytes.length)) != -1)) {
				if (!b.equals(testFile_bytes))
					return false;
			} else
				return false;
		}

		fStream_expected.close();
		fStream_test.close();

		return true;
	}

}
