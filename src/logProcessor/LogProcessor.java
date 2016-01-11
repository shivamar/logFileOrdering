package logProcessor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LogProcessor {
	static String[] fileNames_StringArr;
	static String inputDataDir = "";
	static String outputDir = "";
	static int numberOfThreads;
	static int defaultBufferSize = 8192;
	static int bufferSize = defaultBufferSize;
	// threads updates these two variables in the chronological order of files
	static volatile int fileNum = 0;
	static volatile int cumulativeLength = 0;
	static String errorMsg = "";

	static final Lock lock1 = new ReentrantLock();

	public static void main(String[] args) {

		// all configurable values initialized here
		inputDataDir = args[1];// "src/data";
		outputDir = args[3];// "src/output";
		numberOfThreads = Integer.valueOf(args[5]);// test val - 7;

		if (!handleUserArgumentsError()) {
			return;
		}

		setBufferSize();

		File dir = new File(inputDataDir);
		String[] fileNamesArr = dir.list();

		if (fileNamesArr.length == 0)
			return;
		else
			fileNames_StringArr = fileNamesArr;

		Arrays.sort(fileNamesArr, new Comparator<String>() {
			@Override
			public int compare(String arg0, String arg1) {
				// comparator for Strings of fixed format -
				// example <string>.yyyy-mm-dd.<string>: logTest.2015-12-31.log
				String[] str1 = arg0.split("\\.");
				String[] str2 = arg1.split("\\.");

				return str1[2].compareTo(str2[1]);
			}
		});

		ArrayList<ReaderWriter> listOfThreads = new ArrayList<>();
		
		for (int i = 0; i < numberOfThreads; i++) {
			ReaderWriter rw = new ReaderWriter(i, fileNamesArr.length);
			listOfThreads.add(rw);
			rw.start();
		}
		
		for(ReaderWriter rw : listOfThreads)
		{
			try {
				rw.join();
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
		}
		
		System.out.println("Completed");
	}

	/**
	 * Handles incorrect user arguments
	 * @return TRUE if there are no errors in input
	 */
	private static boolean handleUserArgumentsError() {
		if (numberOfThreads < 1) {
			errorMsg = ErrorMsg.THREADCOUNT_LESS_THAN_ONE.description;
			System.out.println(errorMsg);
			return false;
		}

		if (inputDataDir == null || inputDataDir.isEmpty()) {
			errorMsg = ErrorMsg.INPUTDIR_MISSING.description;
			System.out.println(errorMsg);
			return false;
		}

		if (outputDir == null || outputDir.isEmpty()) {
			errorMsg = ErrorMsg.INPUTDIR_MISSING.description;
			System.out.println(errorMsg);
			return false;
		}
		return true;
	}

	/**
	 * divides 70mb between the number of threads set as the buffer size being
	 * used by all threads
	 */
	private static void setBufferSize() {
		bufferSize = ((70 * 1024 * 1024) / numberOfThreads)/2;
	}
}
