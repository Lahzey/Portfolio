package com.creditsuisse.graphics.swing;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class DocumentChangeListener implements DocumentListener {

	@Override
	public void insertUpdate(DocumentEvent e) {
		onChange(e);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		onChange(e);
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		onChange(e);
	}

	protected abstract void onChange(DocumentEvent e);
}
