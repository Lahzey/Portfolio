package graphics.swing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import graphics.swing.TableManager.TableObject;

/**
 * A class that allows easier use of the {@link JTable} by providing a simple table model working with reflection.
 * <br/>You can simply give it a type and then add and remove items like in a list with some extra functionality.
 * <br/>To add this to a component, just create a new {@link JTable} and pass this model in the constructor.
 * @author A469627
 *
 * @param <T> the type of the objects contained in the table
 */
public class TableManager<T extends TableObject> implements TableModel{
	
	private List<T> data = new ArrayList<T>();
	private Comparator<T> sort;
	
	public void add(T element){
		add(element, -1);
	}
	
	public void add(T element, int index){
		if(index < 0){
			data.add(element);
			if(sort != null) Collections.sort(data, sort);
		}else data.add(index, element);
	}
	
	public void remove(T item){
		data.remove(item);
	}
	
	public void remove(int index){
		data.remove(index);
	}
	
	public int size(){
		return data.size();
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getColumnName(int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub
		
	}
	
	
	public static interface TableObject{
		
		/**
		 * @return all the columns in the correct order
		 */
		public String[] getColumns();
		
		/**
		 * Gets the value for the given column.
		 * @param column the name of the column
		 * @return the value for this column
		 */
		public String getValueAt(String column);
	}

}
