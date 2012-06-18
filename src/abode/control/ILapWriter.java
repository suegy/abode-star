package abode.control;

import model.posh.LearnableActionPattern;

/**
 * Interface for classes which write out .LAP files
 * 
 * @author James Nugent
 * @version 1.0, 6/3/2006
 */
public interface ILapWriter {

	public void save(String fileName, LearnableActionPattern data);
	
	public String generateLispFromLAP(LearnableActionPattern data);
	
}
