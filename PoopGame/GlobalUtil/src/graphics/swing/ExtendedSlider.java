package graphics.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import graphics.ImageUtil;
import util.ColorUtil;

/**
 * A JSlider with custom extended functionality. This does not extend JSlider as it supports double values.
 * <br>Therefore, it will always have the same look, Look and Feel does not matter.
 * @author A469627
 *
 */

public class ExtendedSlider extends JComponent{

	
	private final Slider slider = new Slider();
	private final NumericTextField minInput = new NumericTextField(slider.getMin() + "");
	private final NumericTextField maxInput = new NumericTextField(slider.getMax() + "");
	
	private boolean minEditable = true;
	private boolean maxEditable = true;
	
	/**
	 * Creates a new slider with the default values (min: 0, max: 100, value: 50);
	 */
	public ExtendedSlider(){
		this(Slider.DEFAULT_MIN, Slider.DEFAULT_MAX, Slider.DEFAULT_VALUE);
	}
	
	/**
	 * Creates a new slider with the defined values
	 * @param min
	 * @param max
	 * @param value
	 */
	public ExtendedSlider(double min, double max, double value){
		setLayout(new BorderLayout());
		add(slider, BorderLayout.CENTER);
		add(minInput, BorderLayout.WEST);
		add(maxInput, BorderLayout.EAST);

		slider.setMin(min);
		slider.setMax(max);
		slider.setValue(value);
		
		DocumentListener docListener = new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				update();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				update();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				update();
			}
			
			private void update(){
				minInput.setColumns(minInput.getText().length());
				maxInput.setColumns(maxInput.getText().length());
				
				String minInputText = minInput.getText();
				if(minInputText != null && !minInputText.equals("")){
					slider.setMin(Float.parseFloat(minInputText));
				}
				
				String maxInputText = maxInput.getText();
				if(maxInputText != null && !maxInputText.equals("")){
					slider.setMax(Float.parseFloat(maxInputText));
				}
				
				revalidate();
				repaint();
			}
		};
		
		minInput.allowFloats = true;
		maxInput.allowFloats = true;
		
		minInput.getDocument().addDocumentListener(docListener);
		maxInput.getDocument().addDocumentListener(docListener);
		
		minInput.setHorizontalAlignment(SwingConstants.CENTER);
		maxInput.setHorizontalAlignment(SwingConstants.CENTER);
		
		minInput.setText(slider.getMin() + "");
		maxInput.setText(slider.getMax() + "");
	}

	/**
	 * See setMinEditable
	 * @return if the minimum value is editable
	 */
	public boolean isMinEditable() {
		return minEditable;
	}
	
	/**
	 * Defines if the minimum value should be editable.
	 * <br/>If not, the input field on the left will disappear.
	 * @param minEditable true if it should be editable, false otherwise
	 */
	public void setMinEditable(boolean minEditable) {
		this.minEditable = minEditable;
		if(minEditable){
			add(minInput, BorderLayout.WEST);
		}else{
			remove(minInput);
		}
	}

	/**
	 * See setMaxEditable
	 * @return if the maximum value is editable
	 */
	public boolean isMaxEditable() {
		return maxEditable;
	}

	/**
	 * Defines if the maximum value should be editable.
	 * <br/>If not, the input field on the right will disappear.
	 * @param maxEditable true if it should be editable, false otherwise
	 */
	public void setMaxEditable(boolean maxEditable) {
		this.maxEditable = maxEditable;
		if(maxEditable){
			add(maxInput, BorderLayout.EAST);
		}else{
			remove(maxInput);
		}
	}
	
	/**
	 * @return the slider itself which contains the value and can be customized
	 */
	public Slider getSlider(){
		return slider;
	}
	
	public void setEnabled(boolean enabled){
		super.setEnabled(enabled);
		slider.setEnabled(enabled);
		minInput.setEnabled(enabled);
		maxInput.setEnabled(enabled);
	}
	
	
	
	public class Slider extends JComponent{
		
		//Default Values
		private static final double DEFAULT_MIN = 0;
		private static final double DEFAULT_MAX = 100;
		private static final double DEFAULT_VALUE = 50;
		
		//Values
		private double min;
		private double max;
		private double value;
		
		//Design: Colors
		private Color sliderColor = new Color(50, 115, 255);
		private Color buttonColor = new Color(165, 165, 165);
		private Color buttonBorderColor = new Color(81, 81, 81);
		private Color buttonPressedColor = new Color(63, 63, 63);
		
		//Design: Icons (will be used when not null)
		private Image sliderIcon = null;
		private Image buttonIcon = null;
		private Image buttonPressedIcon = null;
		
		//Mouse Info
		private boolean pressed = false;
		
		//Listener
		private final List<ChangeListener> changeListener = new ArrayList<ChangeListener>();
		
		
		
		public Slider(){
			this(DEFAULT_MIN, DEFAULT_MAX, DEFAULT_VALUE);
		}
		
		public Slider(double min, double max, double value){
			setMin(min);
			setMax(max);
			setValue(value);
			initListener();
			setToolTipText(value + "");
		}
		
		
		private void initListener(){
			addMouseListener(new MouseAdapter() {
				
				@Override
				public void mousePressed(MouseEvent e) {
					if(!isEnabled()) return;
					setValueByX(e.getX());
					pressed = true;
					repaint();
				}
				
				@Override
				public void mouseReleased(MouseEvent e) {
					if(!isEnabled()) return;
					pressed = false;
					repaint();
				}
			});
			
			addMouseMotionListener(new MouseMotionAdapter() {
				
				@Override
				public void mouseDragged(MouseEvent e) {
					if(!isEnabled()) return;
					setValueByX(e.getX());
				}
			});
		}
		
		private void setValueByX(int x){
			setValue(getValueAtX(x));
		}
		
		private double getValueAtX(int x){
			double value =  min + ((double)x / getWidth() * (max - min));
			if(value > max) value = max;
			else if(value < min) value = min;
			return value;
		}
		
		private int getXByValue(){
			double maxDist = max - min;
			if(maxDist == 0) return getWidth();
			double currDist = value - min;
			int x = (int) ((currDist / maxDist) * getWidth());
			if(x > getWidth()) x = getWidth();
			else if(x < 0) x = 0;
			return x;
		}
		
		@Override
		public String getToolTipText(MouseEvent e){
			return getValueAtX(e.getX()) + "";
		}
		
		@Override
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			
			int buttonHeight = getHeight();
			int buttonWidth = buttonHeight / 4;
			int buttonX = getXByValue() - buttonWidth / 2;
			int buttonY = 0;
			
			int sliderHeight = getHeight() / 4;
			int sliderWidth = getWidth();
			int sliderX = 0;
			int sliderY = getHeight() / 2 - sliderHeight / 2;
			

			Image sliderImage = sliderIcon;
			Color sliderColor = this.sliderColor;
			//Draw Slider
			if(sliderImage != null){
				if(!isEnabled()) sliderImage = ImageUtil.grayScale(sliderImage);
				g.drawImage(sliderImage, sliderX, sliderY, sliderWidth, sliderHeight, null);
			}else if(sliderColor != null){
				if(!isEnabled()) sliderColor = ColorUtil.grayScale(sliderColor);
				g.setColor(sliderColor);
				g.fillRect(sliderX, sliderY, sliderWidth, sliderHeight);
			}
			
			//Draw button
			Color buttonBorderColor = this.buttonBorderColor;
			Color buttonFillColor = null;
			Image buttonImage = null;
			if(pressed){
				if(buttonPressedIcon != null) buttonImage = buttonPressedIcon;
				else buttonFillColor = buttonPressedColor;
			}else{
				if(buttonIcon != null) buttonImage = buttonIcon;
				else buttonFillColor = buttonColor;
			}
			
			if(buttonImage != null){
				if(!isEnabled()) buttonImage = ImageUtil.grayScale(buttonImage);
				g.drawImage(buttonImage, buttonX, buttonY, buttonWidth, buttonHeight, null);
			}else if(buttonFillColor != null){
				if(!isEnabled()) buttonFillColor = ColorUtil.grayScale(buttonFillColor);
				g.setColor(buttonFillColor);
				g.fillRect(buttonX, buttonY, buttonWidth, buttonHeight);
				if(buttonBorderColor != null){
					if(!isEnabled()) buttonBorderColor = ColorUtil.grayScale(buttonBorderColor);
					g.setColor(buttonBorderColor);
					g.drawRect(buttonX, buttonY, buttonWidth, buttonHeight);
				}
			}
			
		}
		
		
		//Listener

		public void addChangeListener(ChangeListener changeListener) {
			this.changeListener.add(changeListener);
		}

		public void removeChangeListener(ChangeListener changeListener) {
			this.changeListener.remove(changeListener);
			
		}
		
		
		
		//Getter & Setter

		public double getMin() {
			return min;
		}

		public void setMin(double min) {
			this.min = min;
		}

		public double getMax() {
			return max;
		}

		public void setMax(double max) {
			this.max = max;
		}

		public double getValue() {
			return value;
		}

		public void setValue(double value) {
			this.value = value;
			for(ChangeListener listener : changeListener) listener.stateChanged(new ChangeEvent(this));
		}

		public Color getSliderColor() {
			return sliderColor;
		}

		public void setSliderColor(Color sliderColor) {
			this.sliderColor = sliderColor;
		}

		public Color getButtonColor() {
			return buttonColor;
		}

		public void setButtonColor(Color buttonColor) {
			this.buttonColor = buttonColor;
		}

		public Color getButtonBorderColor() {
			return buttonBorderColor;
		}

		public void setButtonBorderColor(Color buttonBorderColor) {
			this.buttonBorderColor = buttonBorderColor;
		}

		public Color getButtonPressedColor() {
			return buttonPressedColor;
		}

		public void setButtonPressedColor(Color buttonPressedColor) {
			this.buttonPressedColor = buttonPressedColor;
		}

		public Image getSliderIcon() {
			return sliderIcon;
		}

		public void setSliderIcon(Image sliderIcon) {
			this.sliderIcon = sliderIcon;
		}

		public Image getButtonIcon() {
			return buttonIcon;
		}

		public void setButtonIcon(Image buttonIcon) {
			this.buttonIcon = buttonIcon;
		}

		public Image getButtonPressedIcon() {
			return buttonPressedIcon;
		}

		public void setButtonPressedIcon(Image buttonPressedIcon) {
			this.buttonPressedIcon = buttonPressedIcon;
		}
		
		
	}
}
