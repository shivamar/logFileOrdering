package logProcessor;

public enum ErrorMsg {
	THREADCOUNT_LESS_THAN_ONE("Thread count should be set to greater than zero"),
	INPUTDIR_MISSING("Input Directory Missing"),
	OUTPUTDIR_MISSING("Output Directory Missing");
	
	String description;
	
	ErrorMsg(){}
	ErrorMsg(String desc){
		description = desc;
	}
}
