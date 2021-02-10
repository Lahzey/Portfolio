package logic;

import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.WinReg.HKEY;

public class Variable implements Comparable<Variable>{

	
	private String name;
	private VariableScope scope;
	private String[] values;
	
	private String originalName;
	
	public Variable(String name, VariableScope scope){
		this.name = name;
		this.scope = scope;
		this.values = new String[0];
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public VariableScope getScope(){
		return scope;
	}

	public String[] getValues() {
		return values;
	}

	public void setValues(String[] values) {
		this.values = values;
	}
	
	public String getOriginalName(){
		return originalName;
	}
	
	void setOriginalName(String originalName){
		this.originalName = originalName;
	}
	
	public static enum VariableScope{
		SYSTEM(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment"), USER(WinReg.HKEY_CURRENT_USER, "Environment");
		
		public final HKEY registryRoot;
		public final String registryPath;
		
		private VariableScope(HKEY registryRoot, String registryPath){
			this.registryRoot = registryRoot;
			this.registryPath = registryPath;
		}
	}

	@Override
	public int compareTo(Variable o) {
		return getName().compareTo(o.getName());
	}
}
