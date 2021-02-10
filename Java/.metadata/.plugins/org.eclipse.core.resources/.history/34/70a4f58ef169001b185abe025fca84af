package com.creditsuisse.graphics.swing;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicSpinnerUI;

public class HorizontalSpinnerUI extends BasicSpinnerUI {

  public static ComponentUI createUI(JComponent c) {
    return new HorizontalSpinnerUI();
  }

  @Override
  protected Component createNextButton() {
    Component c = createArrowButton(SwingConstants.EAST);
    c.setName("Spinner.nextButton");
    installNextButtonListeners(c);
    return c;
  }

  @Override
  protected Component createPreviousButton() {
    Component c = createArrowButton(SwingConstants.WEST);
    c.setName("Spinner.previousButton");
    installPreviousButtonListeners(c);
    return c;
  }

  // copied from BasicSpinnerUI
  private Component createArrowButton(int direction) {
    JButton b = new BasicArrowButton(direction);
    Border buttonBorder = UIManager.getBorder("Spinner.arrowButtonBorder");
    if (buttonBorder instanceof UIResource) {
      b.setBorder(new CompoundBorder(buttonBorder, null));
    } else {
      b.setBorder(buttonBorder);
    }
    b.setInheritsPopupMenu(true);
    return b;
  }

  @Override
  public void installUI(JComponent c) {
    super.installUI(c);
    c.removeAll();
    c.setLayout(new BorderLayout());
    c.add(createNextButton(), BorderLayout.EAST);
    c.add(createPreviousButton(), BorderLayout.WEST);
    c.add(createEditor(), BorderLayout.CENTER);
  }
}