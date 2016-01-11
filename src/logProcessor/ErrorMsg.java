package logProcessor;
/**
 * Stores error messages
 * @author Shiva
 *
 */
public enum ErrorMsg {
	THREADCOUNT_LESS_THAN_ONE("Thread count should be set to greater than zero"),
	INPUTDIR_MISSING("Input Directory Missing"),
	OUTPUTDIR_MISSING("Output Directory Missing"),
	Incorrect_Arguments_Detected("example arguments: -input <input directory path> -output <output directory path> -threads <integer value greater than 0>");
	
	String description;
	
	ErrorMsg(){}
	ErrorMsg(String desc){
		description = desc;
	}
}
