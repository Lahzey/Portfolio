package util;

import java.awt.datatransfer.Transferable;

public interface Selectable{
	public void setSelected(boolean selected);
	public boolean isSelected();
	public Transferable getTransferableContent();
}
