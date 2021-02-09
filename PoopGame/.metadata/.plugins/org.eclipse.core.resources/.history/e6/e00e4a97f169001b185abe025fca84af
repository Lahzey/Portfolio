package com.creditsuisse.util;

import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.List;

/**
 * A class with some static rules.
 * Those rules can be used to validate files in different ways.
 * @author A469627
 *
 */
public class FileValidator {
	
	/** Checks if the file can be created (by creating it and then deleting again). */
	public static final Rule CAN_CREATE;
	/** Checks if the file can be written in (for directories this means adding, changing and removing children). */
	public static final Rule CAN_WRITE;
	/** Checks if the file exists. */
	public static final Rule EXISTS;
	/** Checks if the file is a directory. It does not have to exist for this rule to return true. */
	public static final Rule IS_DIRECTORY;
	/** Checks if the file is a file. It does not have to exist for this rule to return true. */
	public static final Rule IS_FILE;
	
	static{
		CAN_CREATE = new Rule(){

			@Override
			public boolean check(File file) {
				List<File> createdFiles = new ArrayList<File>();
				createdFiles.add(file);
				File parent = file.getParentFile();
				while(parent != null && !parent.exists()){
					createdFiles.add(parent);
					parent = parent.getParentFile();
				}
				if(IS_DIRECTORY.check(file)){
					if(!file.mkdirs()){
						return false;
					}
				}else{
					try {
						if((file.getParentFile() != null && !file.getParentFile().mkdirs()) || !file.createNewFile()){
							return false;
						}
					} catch (IOException e) {
						return false;
					}
				}
				
				for(File createdFile : createdFiles) createdFile.delete();
				return true;
			}
			
		};
		CAN_WRITE = new Rule(){

			@Override
			public boolean check(File file) {
				try{
					AccessController.checkPermission(new FilePermission(file.getAbsolutePath(), "read,write"));
					return true;
				}catch(Exception e){
					return false;
				}
			}
			
		};
		
		EXISTS = new Rule(){

			@Override
			public boolean check(File file) {
				return file.exists();
			}
			
		};
		
		IS_DIRECTORY = new Rule(){

			@Override
			public boolean check(File file) {
				return file.getName().indexOf(".") < 0;
			}
			
		};
		
		IS_FILE = new Rule(){

			@Override
			public boolean check(File file) {
				return !IS_DIRECTORY.check(file);
			}
			
		};
	}
	
	
	/**
	 * A Rule that can be checked on files.
	 * Rules can be connected with {@link #and(Rule)} and {@link #or(Rule)}.
	 * @author A469627
	 *
	 */
	public static abstract class Rule{
		
		
		public abstract boolean check(File file);
		
		public Rule and(final Rule rule){
			final Rule thisRule = this;
			return new Rule(){

				@Override
				public boolean check(File file) {
					return thisRule.check(file) && rule.check(file);
				}
			};
		}
		
		public Rule or(final Rule rule){
			final Rule thisRule = this;
			return new Rule(){

				@Override
				public boolean check(File file) {
					return thisRule.check(file) || rule.check(file);
				}
			};
		}
	}
}
