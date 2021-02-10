package logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import util.StringComparator;
import com.sun.jna.platform.win32.Advapi32Util;

import logic.Variable.VariableScope;

public class Registry {
	
	public static final String VALUE_SPLITTER = ";";

	private static Map<VariableScope, BidiMap<String, Variable>> variables = new HashMap<>();
	
	
	
	public static BidiMap<String, Variable> getAllVars(VariableScope scope){
		BidiMap<String, Variable> scopeVars = variables.get(scope);
		if(scopeVars == null){
			scopeVars = new DualHashBidiMap<>();
			variables.put(scope, scopeVars);
			loadAllVars(scope);
		}
		return scopeVars;
	}
	
	public static List<String> getSortedVarNames(VariableScope scope){
		List<String> varNames = new ArrayList<>();
		varNames.addAll(getAllVars(scope).keySet());
		varNames.sort(new StringComparator());
		return varNames;
	}
	
	public static Variable get(VariableScope scope, String name){
		return getAllVars(scope).get(name);
	}
	
	public static boolean exists(VariableScope scope, String name){
		return scope != null && name != null && Advapi32Util.registryValueExists(scope.registryRoot, scope.registryPath, name);
	}
	
	public static void persist(Variable var) throws RegistryException{
		VariableScope scope = var.getScope();
		String name = var.getName();
		String originalName = var.getOriginalName();
		
		// Exceptions
		if(scope == null) throw new RegistryException("Scope is null.");
		if(name == null || name.isEmpty()) throw new RegistryException("Name is null or empty.");
		if(!originalName.equals(name) && exists(scope, name)){
			// New name already in use
			throw new RegistryException("The given variable name is already in use.");
		}
		
		// Build value
		StringBuilder valueBuilder = new StringBuilder();
		for(String value : var.getValues()){
			if(valueBuilder.length() > 0) valueBuilder.append(VALUE_SPLITTER);
			valueBuilder.append(value);
		}
		String valueString = valueBuilder.toString();
		
		// If renamed, delete old
		if(originalName != null && !name.equals(originalName) && exists(scope, originalName)){
			remove(var);
		}
		
		// If not existing, create
		if(!exists(scope, name)){
			Advapi32Util.registryCreateKey(scope.registryRoot, scope.registryPath, name);
			variables.get(var.getScope()).put(name, var);
		}
		
		// Store value
		if(valueString.contains("%")){
			Advapi32Util.registrySetExpandableStringValue(scope.registryRoot, scope.registryPath, name, valueString);
		}else{
			Advapi32Util.registrySetStringValue(scope.registryRoot, scope.registryPath, name, valueString);
		}
		
		// Set original name
		var.setOriginalName(name);
	}
	
	public static void remove(VariableScope scope, String name) throws RegistryException{
		Variable var = get(scope, name);
		if(var == null) throw new RegistryException("Variable does not exist.");
		else remove(var);
	}
	
	public static void remove(Variable var) throws RegistryException{
		if(var == null) throw new IllegalArgumentException("Variable may not be null.");
		VariableScope scope = var.getScope();
		String originalName = var.getOriginalName();
		if(exists(scope, originalName)){
			Advapi32Util.registryDeleteValue(scope.registryRoot, scope.registryPath, originalName);
			var.setOriginalName(null);
			variables.get(var.getScope()).remove(originalName);
		}else{
			throw new RegistryException("Variable does not exist.");
		}
	}
	
	
	public static void refresh(){
		for(VariableScope scope : variables.keySet()){
			loadAllVars(scope);
		}
	}
	
	
	private static void loadAllVars(VariableScope scope){
		BidiMap<String, Variable> scopeVars = getAllVars(scope);
		TreeMap<String, Object> varTreeMap = Advapi32Util.registryGetValues(scope.registryRoot, scope.registryPath);
		for(String varKey : varTreeMap.keySet()){
			Variable var = new Variable(varKey, scope);
			var.setOriginalName(varKey);
			var.setValues(varTreeMap.get(varKey).toString().split(VALUE_SPLITTER));
			scopeVars.put(varKey, var);
		}
	}
	
	
	
}
