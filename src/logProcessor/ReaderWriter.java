import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ReaderWriter extends Thread {
	int totalFiles;
	int totalNoThreads; 
	int currFileNum;
	
	public ReaderWriter(int currFileNum, int totalFiles) {		
		this.currFileNum = currFileNum;
		this.totalFiles = totalFiles;
		this.totalNoThreads = LogProcessor.numberOfThreads;
	}

	public void run()
	{
		int lineCount=0;
		int prevFilesLastLineCount=0;
		boolean updateFlag=false;
		
		while(currFileNum < totalFiles)
		{
			String fileName = LogProcessor.str_ArrayfileNames[currFileNum];		
			
			File file = new File(LogProcessor.dataDir+"/"+fileName);
			try
			{
				FileInputStream fStream = new FileInputStream(file);
				InputStream inputStream = new BufferedInputStream(fStream);
				
				byte[] c = new byte[1000];
				int readChars = 0;
				boolean endsWithoutNewLine=false;
				
				while((readChars = inputStream.read(c)) != -1)
				{
					for(int i=0;i<readChars;i++){
						if(c[i] == '\n')
						 lineCount++;
					}
					if(readChars > 0) endsWithoutNewLine = c[readChars-1] != '\n';
				}
				
				if(endsWithoutNewLine) lineCount++;
				
				inputStream.close();
				fStream.close();							
		} 
		catch(Exception e)
		{ 
			if(e instanceof FileNotFoundException || e instanceof IOException)			
				e.printStackTrace();		
		}	
			
		updateFlag=false;		
		
		while(!updateFlag)
		{
			synchronized(LogProcessor.lock1){
				if(currFileNum == LogProcessor.fileNum){
					
					//TODO : to comment out: for test purposes only
					System.out.println("LineCount ::"+lineCount+"## in  currFileNumber::"+currFileNum);
					
					//update prev files last line count so u can start writing from this number in ur current file
					prevFilesLastLineCount = LogProcessor.cumulativeLength;
					
					//update the filenum for next thread to go forward and set the cumulative line count - line count
					LogProcessor.fileNum = LogProcessor.fileNum+1;
					LogProcessor.cumulativeLength += lineCount;
					
					LogProcessor.lock1.notifyAll();
					updateFlag = !updateFlag;
				}
				else {
					try {
						LogProcessor.lock1.wait();
					} catch (InterruptedException e) {						
						e.printStackTrace();
					}
				}			
			}
		}
		
		//write to the file: use 70 mb only
		FileOutputStream fOutStream = new FileOutputStream(file);
		OutputStream outStream = new BufferedOutputStream(fOutStream, 1024);
		
		
		//update currFileNum to next thread and reset lineCount to zero
			currFileNum += LogProcessor.numberOfThreads;	
			lineCount=0;
		}
	
	}
	
}