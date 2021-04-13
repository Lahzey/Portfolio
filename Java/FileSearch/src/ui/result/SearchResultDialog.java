package ui.result;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JDialog;

import graphics.swing.components.TextProgressBar;
import util.ExtendedThread;

import net.miginfocom.swing.MigLayout;
import search.SearchEngine;
import search.SearchEngine.SearchCallback.Status;
import search.data.SearchResult;

public class SearchResultDialog extends JDialog{
	private static final long serialVersionUID = 1L;

	private TextProgressBar progressBar = new TextProgressBar();
	private ResultList resultList;
	
	public SearchResultDialog(SearchResult results, Component parent){
		//Setup
		setLayout(new MigLayout("fill"));
		setModal(true);
		setSize(getPreferredSize());
		setLocationRelativeTo(parent);
		
		//Title
		String title = "Search for ";
		if(!results.search.containingText.isEmpty()) title += "'" + results.search.containingText + "' in ";
		if(!results.search.namePattern.isEmpty()) title += "files called '" + results.search.namePattern + "' in ";
		title += "'" + results.search.scope.getAbsolutePath() + "'";
		setTitle(title);
		
		//Progress
		add(progressBar, "wrap, growx");
		
		//Results
		resultList = new ResultList(results);
		add(resultList, "wrap, growx, growy, pushy");
		
		progressBar.addMouseListener(new MouseAdapter() {
			
			private long minimizeAt = System.currentTimeMillis();
			private ExtendedThread minimizeThread;
			
			{
				mouseExited(null); //init
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				minimizeAt = System.currentTimeMillis() + 1000;
				if(minimizeThread == null){
					minimizeThread = new ExtendedThread(){
						public void run(){
							while(minimizeAt > 0){
								long sleepTime = minimizeAt - System.currentTimeMillis();
								sleepSilent(sleepTime);
								if(minimizeAt > 0 && minimizeAt <= System.currentTimeMillis()){
									minimizeAt = 0;
									progressBar.setMinimized(true);
								}
							}
							minimizeThread = null;
						}
					};
					minimizeThread.start();
				}
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				minimizeAt = 0;
				progressBar.setMinimized(false);
			}
		});
		
		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosed(WindowEvent e) {
				SearchEngine.cancelSearch();
			}
		});
	}
	
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(700, 300);
	}
	
	public void setStatus(Status status){
		progressBar.setComment(status.toString());
	}
	
	public void setProgress(int progress){
		progressBar.setValue(progress);
	}
	
	public void setMaxProgress(int maxProgress){
		progressBar.setMaximum(maxProgress);
	}
	
	public void add(File file, int matchIndex){
		resultList.add(file, matchIndex);
		revalidate();
		repaint();
	}
}
