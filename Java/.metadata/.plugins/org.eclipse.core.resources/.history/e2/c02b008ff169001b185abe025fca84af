package com.creditsuisse.graphics.swing;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.JLabel;

public class FormattedLabel extends JLabel{
	
	private String rawText;

	public FormattedLabel() {
		super();
	}

	public FormattedLabel(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
	}

	public FormattedLabel(Icon image) {
		super(image);
	}

	public FormattedLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
	}

	public FormattedLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
	}

	public FormattedLabel(String text) {
		super(text);
	}

	@Override
	public void setText(String text) {
		this.rawText = text;
		super.setText(generateFormattedString());
	}
	
	private String generateFormattedString(){
		return "";
	}
	
	
	
	
	//Formats
	
	public static interface Format{
		public String getOpenTag();
		public String getCloseTag();
	}
	
	private static class SimpleFormat implements Format{
		
		private String tagName;
		
		public SimpleFormat(String tagName){
			this.tagName = tagName;
		}

		@Override
		public String getOpenTag() {
			return "<" + tagName + ">";
		}

		@Override
		public String getCloseTag() {
			return "<" + tagName + "/>";
		}
		
	}
	
	public static Format BOLD = new SimpleFormat("b");
	public static Format ITALIC = new SimpleFormat("i");
	public static class COLOR implements Format{
		
		public COLOR(Color color, String type){
			
		}

		@Override
		public String getOpenTag() {
			return "<p>";
		}

		@Override
		public String getCloseTag() {
			return "</p>";
		}
		
	}

}
