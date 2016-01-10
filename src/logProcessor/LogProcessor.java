package logProcessor;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public  class LogProcessor {
	static String[] str_ArrayfileNames = null;
	static String inputDataDir="";
	static String outputDir="";
	static int numberOfThreads;
	//to be set
	static int bufferWriterSize=8192;
	//thread updates these two variables
	static volatile int fileNum=0;
	static volatile int cumulativeLength=0;
	
	static final Lock lock1 = new ReentrantLock();
	
	public static void main(String[] args) {
		
		//all configurable values initialized here
		inputDataDir = "src/data";
		outputDir="src/output";
		LogProcessor.numberOfThreads = 3;
		
		setBufferSize();
		
		File dir = new File(inputDataDir);
		String[] fileNamesArr = dir.list();
		str_ArrayfileNames = fileNamesArr;
		
		Arrays.sort(fileNamesArr, new Comparator<String>(){

			@Override
			public int compare(String arg0, String arg1) {
				//comparator for Strings of fixed format - logTest.2015-12-31.log
				String[] str1 = arg0.split("\\.");
				String[] str2 = arg1.split("\\.");
				
				return str1[2].compareTo(str2[1]);				
			}			
		});
		
		for(int i=0; i < numberOfThreads; i++)
		{
			ReaderWriter rw = new ReaderWriter(i, fileNamesArr.length);
			rw.start();
		}			
	}
	
	private static void setBufferSize()
	{
		bufferWriterSize=((70*1024*1024) / numberOfThreads)/2;
	}
}
