package graphics.swing.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import graphics.swing.Inspector;
import graphics.swing.TestFrame;
import util.ColorUtil;
import util.GetterSetterAccess;
import util.ReflectionUtil;
import util.StringUtil;

public class AutoTable<T> extends MigTable<T> {
	
	/**
	 * Directly accesses the attribute of the given class (and all super-classes).
	 */
	public static final int DIRECT_ACCESS_MODE = 1;
	
	/**
	 * Accesses the getter and setter methods of the given class (and all super-classes).
	 */
	public static final int GETTER_SETTER_MODE = 2;
	
	/**
	 * [Default Mode] Like {@link #GETTER_SETTER_MODE}, but requires a matching attribute for each getter-setter-pair to exist.
	 */
	public static final int COMBINED_MODE = 3;

	private final int mode;
	private final List<Field> fields;
	private final GetterSetterAccess getterSetterAccess;
	
	private final List<String> columns = new ArrayList<String>();
	private final Map<String, String> columnNames = new HashMap<String, String>();
	private final Map<String, Class<?>> columnTypes = new HashMap<String, Class<?>>();
	
	private final Map<Integer, Boolean> editable = new HashMap<Integer, Boolean>();
	private final Map<T, Map<Integer, TypeEditor<?>>> typeEditors = new HashMap<T, Map<Integer, TypeEditor<?>>>();
	
	public AutoTable(Class<T> clazz){
		this(clazz, COMBINED_MODE);
	}
	
	public AutoTable(Class<T> clazz, int mode) {
		this.mode = mode;
		fields = ReflectionUtil.getAllFieldsInHierarchy(clazz);
		getterSetterAccess = new GetterSetterAccess(clazz, mode == COMBINED_MODE);
		
		switch(mode){
		case DIRECT_ACCESS_MODE:
			initByFields();
			break;
		case GETTER_SETTER_MODE:
		case COMBINED_MODE:
			initByGetterSetterAccess();
			break;
		default:
			throw new IllegalArgumentException("Given mode (" + mode + ") is not one of [DIRECT_ACCESS_MODE: " + DIRECT_ACCESS_MODE
					+ ", GETTER_SETTER_MODE: " + GETTER_SETTER_MODE
					+ ", COMBINED_MODE: " + COMBINED_MODE + "]");
		}
		
		for(String column : columnTypes.keySet()){
			final Class<?> columnType = columnTypes.get(column);
			if(Comparable.class.isAssignableFrom(columnType) || columnType.isPrimitive()){
				final int columnIndex = columns.indexOf(column);
				setSort(columnIndex, new Comparator<T>() {
					@Override
					public int compare(T o1, T o2) {
						Object attr1 = getContentAt(o1, columnIndex);
						Object attr2 = getContentAt(o2, columnIndex);
						
						try {
							return (Integer) Comparable.class.getMethod("compareTo", Object.class).invoke(attr1, attr2);
						} catch (Throwable e) {
							e.printStackTrace();
							return 0;
						}
					}
				});
			}
		}
		
		// default constraints
		setColumnConstraints("[fill]");
		setRowConstraints("[fill]");
		
		// make MigTable generate header again
		clear();
	}
	
	private void initByFields(){
		for(Field field : fields){
			field.setAccessible(true);
			String name = field.getName();
			columns.add(name);
			columnNames.put(name, StringUtil.capitalizeAt(name, 0));
			columnTypes.put(name, field.getType());
		}
	}
	
	private void initByGetterSetterAccess(){
		for(String attribute : getterSetterAccess.getAttributes()){
			columns.add(attribute);
			columnNames.put(attribute, StringUtil.capitalizeAt(attribute, 0));
			columnTypes.put(attribute, getterSetterAccess.getAttributeType(attribute));
		}
	}
	
	private Object getContentAt(T element, int columnIndex){
		switch(mode){
		case DIRECT_ACCESS_MODE:
			try {
				return fields.get(columnIndex).get(element);
			} catch (Throwable e) {
				throw new RuntimeException("Failed to get " + columns.get(columnIndex) + " of " + element + " from field", e);
			}
		case GETTER_SETTER_MODE:
		case COMBINED_MODE:
			return getterSetterAccess.getValue(element, columns.get(columnIndex));
		default:
			throw new IllegalArgumentException("Given mode (" + mode + ") is not one of [DIRECT_ACCESS_MODE: " + DIRECT_ACCESS_MODE
					+ ", GETTER_SETTER_MODE: " + GETTER_SETTER_MODE
					+ ", COMBINED_MODE: " + COMBINED_MODE + "]");
		}
	}
	
	private void setContentAt(T element, int columnIndex, Object content){
		switch(mode){
		case DIRECT_ACCESS_MODE:
			try {
				fields.get(columnIndex).set(element, content);
			} catch (Throwable e) {
				throw new RuntimeException("Failed to get " + columns.get(columnIndex) + " of " + element + " from field", e);
			}
			break;
		case GETTER_SETTER_MODE:
		case COMBINED_MODE:
			getterSetterAccess.setValue(element, columns.get(columnIndex), content);
			break;
		default:
			throw new IllegalArgumentException("Given mode (" + mode + ") is not one of [DIRECT_ACCESS_MODE: " + DIRECT_ACCESS_MODE
					+ ", GETTER_SETTER_MODE: " + GETTER_SETTER_MODE
					+ ", COMBINED_MODE: " + COMBINED_MODE + "]");
		}
	}

	@Override
	protected Component getComponentAt(final T element, final int columnIndex) {
		final TypeEditor<?> typeEditor = TypeEditor.getEditor(columnTypes.get(columns.get(columnIndex)));
		if(typeEditor != null){
			final JAnimationPanel wrapper = new JAnimationPanel(new BorderLayout());
			wrapper.setOpaque(false);
			wrapper.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			wrapper.add(typeEditor);
			
			typeEditor.trySetInput(getContentAt(element, columnIndex));
			typeEditor.setEditable(isEditable(columnIndex));
			
			// store the editor
			Map<Integer, TypeEditor<?>> typeEditorMap = typeEditors.get(element);
			if(typeEditorMap == null){
				typeEditorMap = new HashMap<Integer, TypeEditor<?>>();
				typeEditors.put(element, typeEditorMap);
			}
			typeEditorMap.put(columnIndex, typeEditor);
			
			typeEditor.addSubmitListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(typeEditor.isInputValid()){
						setContentAt(element, columnIndex, typeEditor.getInput());
					}else{
						typeEditor.trySetInput(getContentAt(element, columnIndex));
						wrapper.setOpaque(true);
						wrapper.setBackground(getBackground());
						wrapper.blinkBackground(500, ColorUtil.ERROR_BORDER_COLOR).then(new Runnable() {
							
							@Override
							public void run() {
								wrapper.setOpaque(false);
							}
						});
					}
				}
			});
			
			return wrapper;
		}else{
			return new JLabel(getContentAt(element, columnIndex).toString());
		}
	}

	@Override
	protected Component getHeaderComponentAt(int columnIndex) {
		return new JLabel(columnNames.get(columns.get(columnIndex)));
	}

	@Override
	protected int getColumnCount() {
		return columns != null ? columns.size() : 0;
	}
	
	public boolean isEditable(int columnIndex){
		Boolean editable = this.editable.get(columnIndex);
		return editable == null ? false : editable;
	}
	
	public void setEditable(boolean editable){
		for(int columnIndex = 0; columnIndex < getColumnCount(); columnIndex++){
			setEditable(columnIndex, editable);
		}
	}
	
	public void setEditable(int columnIndex, boolean editable){
		this.editable.put(columnIndex, editable);
		for(T element : getData()){
			Map<Integer, TypeEditor<?>> typeEditorMap = typeEditors.get(element);
			if(typeEditorMap != null && typeEditorMap.containsKey(columnIndex)){
				typeEditorMap.get(columnIndex).setEditable(editable);
			}
		}
	}
	

	
	
	
	public static void main(String[] args) {
		int dataCount = 10;
		List<TestObject> data = new ArrayList<TestObject>();
		for(int i = 0; i < dataCount; i++){
			data.add(new TestObject());
		}
		
		AutoTable<TestObject> table = new AutoTable<AutoTable.TestObject>(TestObject.class, DIRECT_ACCESS_MODE);
		table.setData(data.toArray(new TestObject[dataCount]));
		table.setColumnConstraints("[fill]");
		table.setEditable(true);
		
		new TestFrame(new JScrollPane(new Inspector(table)));
	}


	@SuppressWarnings("unused")
	private static class TestObject {
		public String name = "";
		public Image image;
		public boolean bool;
		public int integer;
		public Color color;
		
		public TestObject(){
			Random r = new Random();
			int charCount = r.nextInt(15);
			for(int i = 0; i < charCount; i++){
				name += (char)(r.nextInt(26) + 'a');
			}
			
			image = FontIcon.of(FontAwesomeSolid.values()[r.nextInt(FontAwesomeSolid.values().length)]).toImage();
			
			bool = r.nextBoolean();
			
			integer = r.nextInt(1000);
			color = new Color(r.nextFloat(), r.nextFloat(), r.nextFloat());
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Image getImage() {
			return image;
		}

		public void setImage(Image image) {
			this.image = image;
		}

		public Rectangle getBounds() {
			return new Rectangle();
		}

		public void setBounds(Rectangle bounds) {
			
		}
		
		
	}
	
}
