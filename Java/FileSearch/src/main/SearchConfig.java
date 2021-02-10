package main;

import java.io.File;

import util.Config;

import search.data.Search;

public class SearchConfig extends Config{
	
	private static final long serialVersionUID = 1L;
	
	private static final String CONFIG_FILE = "config.properties";
	private static final SearchConfig INSTANCE = new SearchConfig();
	private static Search search;
	
	//Prop names
	private static final String CONTAINING_TEXT = "containg_text";
	private static final String CONTAINING_CASE = "containg_case";
	private static final String CONTAINING_REGEX = "containg_regex";
	
	private static final String NAME_PATTERN = "name_pattern";
	private static final String NAME_CASE = "name_case";
	private static final String NAME_REGEX = "name_regex";

	private static final String SCOPE = "scope";
	private static final String STORE_INDEX = "store_index";
	private static final String SEARCH_ARCHIVES = "search_archives";
	
	private SearchConfig() {
		super(new File(CONFIG_FILE));
	}
	
	public static Search getSearch() {
		if(search == null) load();
		return search;
	}

	public static void setSearch(Search search) {
		SearchConfig.search = search;
	}
	
	public static void load(){
		SearchConfig.search = INSTANCE.loadSearch();
	}
	
	public static void store(){
		INSTANCE.storeSearch(search);
	}



	private Search loadSearch(){
		Search search = new Search();
		
		search.containingText = getProperty(CONTAINING_TEXT, search.containingText);
		search.containingCase = Boolean.parseBoolean(getProperty(CONTAINING_CASE, search.containingCase + ""));
		search.containingRegex = Boolean.parseBoolean(getProperty(CONTAINING_REGEX, search.containingRegex + ""));
		
		search.namePattern = getProperty(NAME_PATTERN, search.namePattern);
		search.nameCase = Boolean.parseBoolean(getProperty(NAME_CASE, search.nameCase + ""));
		search.nameRegex = Boolean.parseBoolean(getProperty(NAME_REGEX, search.nameRegex + ""));
		
		String filePath = getProperty(SCOPE, search.scope == null ? "" : search.scope.getAbsolutePath());
		search.scope = filePath.isEmpty() ? null : new File(filePath);
		search.storeIndex = Boolean.parseBoolean(getProperty(STORE_INDEX, search.storeIndex + ""));
		search.searchArchives = Boolean.parseBoolean(getProperty(SEARCH_ARCHIVES, search.searchArchives + ""));
		
		return search;
	}
	
	private void storeSearch(Search search){
		if(search == null) search = new Search();
		
		setProperty(CONTAINING_TEXT, search.containingText);
		setProperty(CONTAINING_CASE, search.containingCase + "");
		setProperty(CONTAINING_REGEX, search.containingRegex + "");

		setProperty(NAME_PATTERN, search.namePattern);
		setProperty(NAME_CASE, search.nameCase + "");
		setProperty(NAME_REGEX, search.nameRegex + "");

		setProperty(SCOPE, search.scope == null ? "" : search.scope.getAbsolutePath());
		setProperty(STORE_INDEX, search.storeIndex + "");
		setProperty(SEARCH_ARCHIVES, search.searchArchives + "");
	}

}
