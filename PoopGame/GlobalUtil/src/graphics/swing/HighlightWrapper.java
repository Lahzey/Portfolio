package graphics.swing;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import util.ColorUtil;

public class HighlightWrapper {
	
	public static final Color DEFAULT_COLOR = ColorUtil.INFO_BACKGROUND_COLOR;

	private Highlighter highlighter;
	private Object reference;
	
	private int start = -1;
	private int end = -1;
	private Color color = DEFAULT_COLOR;
	
	private final Invoker invoker = new Invoker();
	
	public HighlightWrapper(Highlighter highlighter){
		this.highlighter = highlighter;
	}
	
	public HighlightWrapper(Highlighter highlighter, int start, int end){
		this(highlighter, start, end, DEFAULT_COLOR);
	}
	
	public HighlightWrapper(Highlighter highlighter, int start, int end, Color color){
		this.highlighter = highlighter;
		this.start = start;
		this.end = end;
		this.color = color;
		invoker.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				add();
			}
		});
	}
	
	public int getStart() {
		return start;
	}
	
	public void setStart(final int start) {
		invoker.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				HighlightWrapper.this.start = start;
				update();
			}
		});
	}
	
	public int getEnd() {
		return end;
	}
	
	public void setEnd(final int end) {
		invoker.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				HighlightWrapper.this.end = end;
				update();
			}
		});
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(final Color color) {
		invoker.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				HighlightWrapper.this.color = color;
				update();
			}
		});
	}
	
	public Highlighter getHighlighter() {
		return highlighter;
	}

	public void setHighlighter(final Highlighter highlighter) {
		invoker.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				remove();
				HighlightWrapper.this.highlighter = highlighter;
				add();
			}
		});
	}

	private void update(){
		remove();
		add();
	}
	
	private void remove(){
		if(highlighter != null && reference != null){
			highlighter.removeHighlight(reference);
			reference = null;
		}
	}
	
	private void add(){
		if(highlighter != null && start >= 0 && end >= 0 && color != null){
			try {
				reference = highlighter.addHighlight(start, end, new DefaultHighlighter.DefaultHighlightPainter(color));
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class Invoker implements Runnable {
		
		private final List<Runnable> invokeQueue = new ArrayList<Runnable>();
		private boolean isInvoking = false;

		@Override
		public void run() {
			isInvoking = true;
			while(!invokeQueue.isEmpty()){
				invokeQueue.get(0).run();
				invokeQueue.remove(0);
			}
			isInvoking = false;
		}
		
		public void invokeLater(Runnable runnable){
			invokeQueue.add(runnable);
			if(!isInvoking){
				SwingUtilities.invokeLater(this);
			}
		}
		
	}
	
}
