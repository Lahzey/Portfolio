package com.creditsuisse.graphics.swing;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
public class AdvancedFileChooser extends JFileChooser{
	
	private boolean forceFileType;
	private List<String> fileTypes;

	@Override
    public void approveSelection(){
        File f = getSelectedFile();
        
        
        if(getDialogType() == SAVE_DIALOG){
        	//Force file type
        	if(forceFileType && checkFileTypes()){
        		String fileType;
        		if(f.getName().lastIndexOf(".") != -1) fileType = f.getName().substring(f.getName().lastIndexOf(".") + 1).toLowerCase();
        		else fileType = "";
        		if (!fileTypes.contains(fileType)) f = new File(f.getAbsolutePath() + "." + fileTypes.get(0));
        	}
        	
        	//Check if file exists and if it does, ask the user if he wants to overwrite
            if(f.exists()){
                int result = JOptionPane.showConfirmDialog(this,"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
                switch(result){
                    case JOptionPane.YES_OPTION:
                        super.approveSelection();
                        return;
                    case JOptionPane.NO_OPTION:
                        return;
                    case JOptionPane.CLOSED_OPTION:
                        return;
                    case JOptionPane.CANCEL_OPTION:
                        cancelSelection();
                        return;
                }
            }
        }
        
        //Save any changes made to the selected file (like append .txt).
        setSelectedFile(f);
        
        
        super.approveSelection();
    }

	/**
	 * Returns true if the file chooser should automatically make any saved file a file with one of the given file types (see {@link #setFileTypes(List) setFileTypes})
	 * <br/>If the file types are not set or set to an invalid value (null or list or null), the file type will not be forced.
	 * @return true if it automatically converts to file type, false otherwise
	 */
	public boolean isForceFileType() {
		return forceFileType;
	}

	/**
	 * Defines if the file chooser should automatically make any saved file a file with one of the given file types (see {@link #setFileTypes(List) setFileTypes})
	 * <br/>If the file types are not set or set to an invalid value (null or list or null), the file type will not be forced.
	 * @param forceFileType true if it should automatically convert to file type, false otherwise
	 */
	public void setForceFileType(boolean forceFileType) {
		this.forceFileType = forceFileType;
		if(forceFileType) setFileFilterFromFileTypes();
	}

	/**
	 * Returns the allowed file types for this file chooser.
	 * <br/>Those file types are used when forcing the file type. (see {@link #setForceFileType(boolean) setForceFileType})
	 * @return the file types in use
	 */
	public List<String> getFileTypes() {
		return fileTypes;
	}

	/**
	 * Sets the allowed file types for this file chooser. All file types will be converted to lower case.
	 * <br/>Those file types will be used when forcing the file type. (see {@link #setForceFileType(boolean) setForceFileType})
	 * @param fileTypes a list of the allowed file types. Only include the file ending, not the dot.<br/>(example:  "txt" -> "file.txt" | ".txt" -> "file..txt")
	 */
	public void setFileTypes(List<String> fileTypes) {
		for(int i = 0; i < fileTypes.size(); i++) fileTypes.set(i, fileTypes.get(i).toLowerCase());
		this.fileTypes = fileTypes;
		if(forceFileType) setFileFilterFromFileTypes();
	}
	
	/**
	 * Sets the allowed file types for this file chooser. All file types will be converted to lower case.
	 * <br/>Those file types will be used when forcing the file type. (see {@link #setForceFileType(boolean) setForceFileType})
	 * @param fileTypes a list of the allowed file types. Only include the file ending, not the dot.<br/>(example:  "txt" -> "file.txt" | ".txt" -> "file..txt")
	 */
	public void setFileTypes(String... fileTypes) {
		setFileTypes(Arrays.asList(fileTypes));
	}
	
	/**
	 * Checks whether the given file type list is valid and therefore can be used to force the file type.
	 * @return true if the list is valid, false otherwise
	 */
	private boolean checkFileTypes(){
		boolean valid = true;
		if(fileTypes == null || fileTypes.size() == 0 || fileTypes.get(0) == null) valid = false;
		return valid;
	}
	
	/**
	 * Sets the file filter to a new FileNameExtensionFilter created from the file types.
	 * @return true if it worked, false if not (due to an invalid file type list).
	 */
	private boolean setFileFilterFromFileTypes(){
		boolean listValid = checkFileTypes();
		if(listValid) setFileFilter(new FileNameExtensionFilter("", (String[]) fileTypes.toArray()));
		return listValid;
	}
}
