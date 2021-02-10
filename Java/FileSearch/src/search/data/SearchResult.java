package search.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.StringUtil;
import util.StringUtil.Insertion;

public class SearchResult {
	
	public final Search search;
	
	public final List<File> files = new ArrayList<>();
	public final Map<File, String> contents = new HashMap<>();
	public final Map<File, List<Match>> matches = new HashMap<>();
	
	public SearchResult(Search search){
		this.search = search;
	}
	
	/**
	 * Adds a new match for the given file at the given indexes.
	 * <br/>Initialization of content mapping etc will be done here if needed.
	 * @param file the file to add a match for
	 * @param content the content of the given file (keeping it loaded for performance)
	 * @param startIndex the starting index (inclusive) of the matched string
	 * @param endIndex the ending index (exclusive) of the matched string
	 * @return the index of the match inside the list of matches for the given file.
	 */
	public int addMatch(File file, String content, int startIndex, int endIndex){
		if(!files.contains(file)){
			files.add(file);
			contents.put(file, content);
			matches.put(file, new ArrayList<>());
		}
		List<Match> fileMatchList = matches.get(file);
		fileMatchList.add(new Match(file, startIndex, endIndex));
		return fileMatchList.size() - 1;
	}
	
	public class Match {
		public final File file;
		public final int start;
		public final int end;
		
		private int lineNr;
		private int indexAtLineStart;
		private int indexAtLineEnd;
		
		public Match(File file, int start, int end){
			this.file = file;
			this.start = start;
			this.end = end;
			
			lineNr = 0;
			int i = 0;
			boolean found = false;
			final String content = contents.get(file);
			indexAtLineEnd = content.length() - 1;
			for(char c : content.toCharArray()){
				if(i >= start && !found){
					found = true;
				}
				else if(c == '\n'){
					if(found){
						indexAtLineEnd = i;
						break;
					}else{
						lineNr++;
						indexAtLineStart = i + 1;
					}
				}
				i++;
			}
		}
		
		public int getLineNumber(){
			return lineNr;
		}
		
		/**
		 * @return the line the match was found in
		 */
		public String getMatchingLine(){
			return contents.get(file).substring(indexAtLineStart, indexAtLineEnd);
		}
		
		/**
		 * Creates a string with the line the match was found in, but formatted with HTML so the match is marked yellow.
		 * @return the created formatted string
		 */
		public String getFormattedMatchingLine(){
			return getFilteredFormattedMatchingLine(null);
		}
		
		/**
		 * Creates a string with the line the match was found in, but formatted with HTML so the match is marked yellow
		 * <br/>and any occurrences of the given filter are marked green.
		 * @param filter the filter to mark green, may be null or empty
		 * @return the created formatted string
		 */
		public String getFilteredFormattedMatchingLine(String filter){
			final StringBuilder formattedString = new StringBuilder("<html><p>");
			String line = getMatchingLine();
			int end = this.end;
			if(end > indexAtLineEnd){
				line += "...";
				end = indexAtLineEnd + 3;
			}
			
			List<Insertion> insertions = new ArrayList<>();
			insertions.add(new Insertion("<span bgcolor=\"yellow\">", start - indexAtLineStart));
			insertions.add(new Insertion("</span>", end - indexAtLineStart));

			if(filter != null && !filter.isEmpty()){
				int filterLength = filter.length();
				int index = -1;
				while((index = line.indexOf(filter, index + 1)) > -1){
					insertions.add(new Insertion("<span bgcolor=\"green\">", index));
					insertions.add(new Insertion("</span>", index + filterLength));
				}
			}
			
			formattedString.append(StringUtil.insertMultiple(line, insertions));
			
			formattedString.append("</p></html>");
			return formattedString.toString();
		}
	}
}
