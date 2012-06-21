package abode.editing;

import java.util.Hashtable;

/**
 * Comment Scraper class.
 * Used for reading comments from a LAP file and keeping track
 * of which element number they are associated with.
 * 
 * @author Simon Davies 2012
 *
 */
public class CommentScraper {
	
	Hashtable <Integer, String> commentsMap;
	
	/**
	 * CommentScraper constructor.
	 * 
	 * @param stringContent The content of the LAP file which contains the comments
	 * @param commentDepth The depth of the comments that you are interested in.
	 * 						For example, for comments on action plans, drive collections
	 * 						and competences use Depth 1.
	 */
	public CommentScraper(String stringContent, int commentDepth){
		String planFileContents = stringContent;
		generateCommentsMap(planFileContents, commentDepth);
	}
	
	/** Generates the content of the comments map.
	 * This will go through the file and record any comments that are discovered, and will
	 * save them if they are at the correct depth.
	 * 
	 * @param strFileContents The content of the LAP file which contains the comments
	 * @param desiredCommentDepth The depth of comments that you are interested in.
	 */
	private void generateCommentsMap(String strFileContents, int desiredCommentDepth){
		commentsMap = new Hashtable<Integer, String>();
		
		int currentDepth = 0;
		int elementNumber = 0;
		
		// Are we reading a comment currently?
		boolean commenting = false;
		
		boolean newLine = false;
		
		String currentComment = "";
		
		for (int index = 0; index < strFileContents.length() - 1; index++) {
			// Get the character at this point
			char c = strFileContents.charAt(index);
			
			// increase the depth of the tree
			if(c == '('){
				currentDepth++;
				continue;
			}
			
			// decrease the depth of a tree
			else if(c == ')'){
				currentDepth--;
				// if we are back at the desired comment depth
				// then increment the number of the element we are currently looking at
				if(currentDepth == desiredCommentDepth){
					elementNumber++;
				}
				continue;
			}
			
			// Start of a comment block
			if(c == ';'){
				commenting = true;
				continue;
			}
			
			// If there is a new line, the next line is not a comment,
			// and we are at the desired depth, then save the comment
			if(c == '\n' && !isNextLineComment(strFileContents, index) && 
					commenting && currentDepth == desiredCommentDepth && currentComment.length() > 0){
				commenting = false;
				commentsMap.put(elementNumber, currentComment.trim());
				currentComment = "";
			}
			else if(c == '\n' && isNextLineComment(strFileContents, index)){
				newLine = true;
				// Add new line
				currentComment += '\n';
			}
			// Add character to the comment.
			else if(commenting){
				// If just turned to a new line
				// ignore whitespace
				if(newLine){
					if(!Character.isWhitespace(c)){
						// Encountered a character, no longer ignoring whitespace
						currentComment += c;
						newLine = false;
					}
				}
				else{
					// Everything OK, add the character to the comment
					currentComment += c;
				}
			}
		}
	}
	
	/** Returns the associated comment string value
	 * 
	 * @param elementNumber The index of the desired element.
	 * @return The desired comment string. If no element is present, an empty string is returned.
	 */
	public String getCommentString(int elementNumber){
		if(commentsMap.containsKey(elementNumber)){
			return commentsMap.get(elementNumber);
		}
		else{
			return "";
		}
	}
	
	/** Checks the next line (from the index point of the previous newline character)
	 * from a given index to see whether it contains a comment or not.
	 * 
	 * @param stringContents The entire file string
	 * @param index The index of the previous newline character
	 * @return true if the line is a comment, false otherwise
	 */
	private boolean isNextLineComment(String stringContents, int index){
		
		// First character is not a new line character as expected
		if(stringContents.charAt(index) != '\n'){
			System.out.println("Error : New line not prefixed with \n.");
			return false;
		}
		
		for (int i = index + 1; i < stringContents.length() - 1; i++) {
			char c = stringContents.charAt(i);
			// The character is not whitespace, and not a comment starting
			// character, so this line is not a comment
			if(!Character.isWhitespace(c) && c != ';'){
				return false;
			}
			// The first character to be found is a comment delimiter,
			// so this line is a comment
			if(c == ';'){
				return true;
			}
			
			// The line was found to not have anything in it, not a comment
			if(c == '\n'){
				return false;
			}
		}
		return false;
	}
}
