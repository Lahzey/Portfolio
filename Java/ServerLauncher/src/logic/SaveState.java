package logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.GeneralListener;
import com.google.gson.annotations.Expose;

import logic.FileLaunchable.Type;
import ui.LauncherPanel.Position;

public class SaveState {

	public List<Tab> tabs = new ArrayList<Tab>();
	public int currentTabIndex = 0;
	
	
//	public Tab getCurrentTab(){
//		if(tabs.isEmpty()){
//			tabs.add(new Tab("Tab 1"));
//		}
//		if(currentTabIndex < 0) currentTabIndex = 0;
//		else if(currentTabIndex >= tabs.size()){
//			currentTabIndex = tabs.size() - 1;
//		}
//		return tabs.get(currentTabIndex);
//	}
	
	public static class Tab {
		private String name;
		private Map<Position, Item> items = new HashMap<Position, Item>();
		
		@Expose
		public final List<GeneralListener> changeListeners = new ArrayList<GeneralListener>();
		
		public Tab(String name){
			this.name = name;
		}
		
		public void setName(String name){
			this.name = name;
			for(GeneralListener listener : changeListeners) listener.actionPerformed();
		}
		
		public String getName(){
			return name;
		}
		
		public void put(Position position, Item item){
			items.put(position, item);
			for(GeneralListener listener : changeListeners) listener.actionPerformed();
		}
		
		public Item get(Position position){
			return items.get(position);
		}
		
		public Item remove(Position position){
			return items.remove(position);
		}
		
	}
	
	public static class Item {
		public final String absolutePath;
		public final Type type;
		
		public Item(String absolutePath, Type type){
			this.absolutePath = absolutePath;
			this.type = type;
		}
	}
	
}
