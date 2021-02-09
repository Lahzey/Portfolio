package graphics.swing;

import java.awt.Component;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollPane;
import javax.swing.JViewport;

public class AutoScrollPane extends JScrollPane{
	private static final long serialVersionUID = 1L;
	
	private boolean scrollToBottom = true;
	private int lastViewPosition;
	
	public AutoScrollPane(Component view) {
		super(view);
		getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
	        public void adjustmentValueChanged(AdjustmentEvent e) {
				JViewport vp = getViewport();
	        	int viewPosition = vp.getHeight() + vp.getViewPosition().y;
	        	int maxPosition = vp.getView().getHeight();
	            if(scrollToBottom){
	            	if(viewPosition >= lastViewPosition){
		            	e.getAdjustable().setValue(e.getAdjustable().getMaximum());
		            	viewPosition = maxPosition;
	            	}else{
	            		setScrollToBottom(false);
	            	}
	            }else{
	            	if(viewPosition >= maxPosition){
	            		setScrollToBottom(true);
	            	}
	            }
	            lastViewPosition = viewPosition;
	        }
	    });
	}
	
	public void setScrollToBottom(boolean scrollToBottom){
		this.scrollToBottom = scrollToBottom;
		repaint();
	}
	
	public boolean isScrollToBottom(){
		return scrollToBottom;
	}
}
