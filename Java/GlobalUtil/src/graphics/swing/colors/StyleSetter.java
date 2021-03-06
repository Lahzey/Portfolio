package graphics.swing.colors;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import graphics.swing.SwingUtil;
import util.GeneralListener;

public abstract class StyleSetter<T> extends MouseAdapter implements FocusListener {

		private JComponent component;

		private T normalStyle, hoveredStyle, clickedStyle, selectedStyle;

		private GeneralListener changeListener;

		private boolean isHovered;
		private boolean isSelected;
		private boolean isClicked;

		public StyleSetter(JComponent component, T normalStyle, T hoveredStyle, T clickedStyle, T selectedStyle, GeneralListener changeListener) {
			this.component = component;
			this.normalStyle = normalStyle;
			this.hoveredStyle = hoveredStyle;
			this.clickedStyle = clickedStyle;
			this.selectedStyle = selectedStyle;
			this.changeListener = changeListener;

			SwingUtil.addRecursiveMouseListener(component, this);
			component.addFocusListener(this);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			isClicked = false;
			updateStyle();
		}

		@Override
		public void mousePressed(MouseEvent e) {
			isClicked = true;
			updateStyle();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			isHovered = false;
			updateStyle();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			isHovered = true;
			updateStyle();
		}

		@Override
		public void focusGained(FocusEvent e) {
			isSelected = true;
			updateStyle();
		}

		@Override
		public void focusLost(FocusEvent e) {
			isSelected = false;
			updateStyle();
		}

		private void updateStyle() {
			if (isClicked && clickedStyle != null) {
				setStyle(component, clickedStyle);
			} else if (isSelected && selectedStyle != null) {
				setStyle(component, selectedStyle);
			} else if (isHovered && hoveredStyle != null) {
				setStyle(component, hoveredStyle);
			} else {
				setStyle(component, normalStyle);
			}
			if (changeListener != null) changeListener.actionPerformed();
		}
		
		protected abstract void setStyle(JComponent component, T style);

	}