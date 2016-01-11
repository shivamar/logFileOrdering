package logProcessor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ReaderWriter extends Thread {
	int totalFiles;
	int currFileNum;	

	public ReaderWriter(int currFileNum, int totalFiles) {
		this.currFileNum = currFileNum;
		this.totalFiles = totalFiles;
	}

	public void run() {
		int lineCount = 0;
		int prevFilesLastLineCount = 0;
		boolean updateFlag = false;

		while (currFileNum < totalFiles) {
			String fileName = LogProcessor.fileNames_StringArr[currFileNum];
			File file = new File(LogProcessor.inputDataDir + "/" + fileName);

			//gets the line count of the file
			lineCount = getLineCount(file);

			updateFlag = false;

			//synchronized block takes care of updating the shared variables of fileNum , cumulative line count
			while (!updateFlag) {
				
				synchronized (LogProcessor.lock1) {
					if (currFileNum == LogProcessor.fileNum) {						
						// System.out.println("LineCount ::" + lineCount + "## in  currFileNumber::" + currFileNum);
						
						// update previous files last line count in thread local
						// variable to enable writing in current file from
						// prevFilesLastLineCount+1
						prevFilesLastLineCount = LogProcessor.cumulativeLength;

						// update the file-number for next thread to go forward
						// and
						// update the cumulative line count -for numbering in next chronological file
						LogProcessor.fileNum = LogProcessor.fileNum + 1;
						LogProcessor.cumulativeLength += lineCount;

						//notifies all waiting threads
						LogProcessor.lock1.notifyAll();
						updateFlag = !updateFlag;
					} else {
						try {
							//waits until notified by other thread
							LogProcessor.lock1.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}					
				}
			}

			//opens the file and writes the line numbers
			writeLineNumbers(fileName, prevFilesLastLineCount);
			
			// update currFileNum to next thread and reset lineCount to zero
			currFileNum += LogProcessor.numberOfThreads;
			lineCount = 0;
		}
	}
private void writeLineNumbers(String fileName, int prevFilesLastLineCount) {
	int currLineNumberToBeWritten;
	
		try {
			File fileToWrite = new File(LogProcessor.outputDir + "/" + fileName);
			if (!fileToWrite.exists())
				fileToWrite.createNewFile();

			FileOutputStream fOutStream = new FileOutputStream(fileToWrite);
			OutputStream outStream = new BufferedOutputStream(fOutStream,
					LogProcessor.bufferSize);

			FileReader fReader = new FileReader(LogProcessor.inputDataDir + "/"
					+ fileName);
			BufferedReader buffReader = new BufferedReader(fReader,
					LogProcessor.bufferSize);
			
			String lineBeingRead;
			currLineNumberToBeWritten = prevFilesLastLineCount + 1;

			while ((lineBeingRead = buffReader.readLine()) != null) {
				lineBeingRead = currLineNumberToBeWritten + ". "+ lineBeingRead + "\n";
				byte[] byt = lineBeingRead.getBytes();
				outStream.write(byt);
				currLineNumberToBeWritten++;
			}

			outStream.close();
			fOutStream.close();

			buffReader.close();
			fReader.close();
		} catch (Exception e) {
			if (e instanceof FileNotFoundException || e instanceof IOException)
				e.printStackTrace();
		}
}

/**
 * Returns the line count given a file
 * @param file input file object
 * @return line-count of the file 
 */
	private int getLineCount(File file) {
		int lineCount = 0;
		FileInputStream fStream = null;
		InputStream inputStream = null;

		try {
			fStream = new FileInputStream(file);
			inputStream = new BufferedInputStream(fStream);

			byte[] c = new byte[1000];
			int readChars = 0;
			boolean endsWithoutNewLine = false;

			while ((readChars = inputStream.read(c)) != -1) {
				for (int i = 0; i < readChars; i++) {
					if (c[i] == '\n')
						lineCount++;
				}
				if (readChars > 0)
					endsWithoutNewLine = c[readChars - 1] != '\n';
			}

			if (endsWithoutNewLine)
				lineCount++;

			inputStream.close();
			fStream.close();
		} catch (Exception e) {
			if (e instanceof FileNotFoundException || e instanceof IOException)
				e.printStackTrace();
		}

		return lineCount;
	}
}