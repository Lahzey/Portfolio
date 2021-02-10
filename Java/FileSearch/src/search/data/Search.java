package search.data;

import java.io.File;

public class Search {

	public String containingText;
	public boolean containingCase;
	public boolean containingRegex;
	
	public String namePattern;
	public boolean nameCase;
	public boolean nameRegex;
	
	public File scope;
	public boolean storeIndex = true;
	public boolean searchArchives = false;
	
	public SearchResult result = new SearchResult(this);
	
	public Search(){
		
	}
	
	public Search(Search previousSearch){
		containingText = previousSearch.containingText;
		containingCase = previousSearch.containingCase;
		containingRegex = previousSearch.containingRegex;
		
		namePattern = previousSearch.namePattern;
		nameCase = previousSearch.nameCase;
		nameRegex = previousSearch.nameRegex;
		
		scope = previousSearch.scope;
		storeIndex = previousSearch.storeIndex;;
		searchArchives = previousSearch.searchArchives;
	}
	
}
