package gui;

import java.awt.Font;
import java.time.Instant;
import java.time.ZoneId;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import graphics.swing.DurationInputField;
import graphics.swing.NestedCheckBox;
import util.ColorUtil;
import com.github.lgooddatepicker.components.DateTimePicker;

import logic.RobotController;
import net.miginfocom.swing.MigLayout;

public class ControllerPanel extends JPanel implements RobotController.ChangeListener {
	
	private NestedCheckBox<DateTimePicker> autoDeactivateCheck;
	private DateTimePicker autoDeactivateTimePicker;
	
	private NestedCheckBox<DurationInputField> interruptCheck;
	private DurationInputField interruptInput;
	
	private JButton startButton;
	
	private RobotController controller = new RobotController();
	
	public ControllerPanel(){
		super(new MigLayout("wrap 1", "[grow]", "[][]50px[]"));
		autoDeactivateTimePicker = new DateTimePicker();
		autoDeactivateTimePicker.setDateTimePermissive(Instant.ofEpochMilli(System.currentTimeMillis() + (1000 * 60 * 60 * 12)).atZone(ZoneId.systemDefault()).toLocalDateTime());
		autoDeactivateCheck = new NestedCheckBox<>("turn off automatically at", true, autoDeactivateTimePicker);
		add(autoDeactivateCheck, "grow");
		
		interruptInput = new DurationInputField("30 seconds");
		interruptCheck = new NestedCheckBox<>("interrupt on mouse drag for", true, interruptInput);
		add(interruptCheck, "grow");
		
		startButton = new JButton("Start ", FontIcon.of(FontAwesomeSolid.PLAY, 20, ColorUtil.SUCCESS_FOREGROUND_COLOR));
		startButton.setFont(startButton.getFont().deriveFont(Font.BOLD, 20));
		startButton.setHorizontalTextPosition(SwingConstants.LEFT);
		startButton.addActionListener(e -> toggle());
		add(startButton, "center");
	}
	
	
	private void toggle() {
		if(controller.isActive()){
			controller.stopMouseMoving();
			startButton.setText("Start ");
			startButton.setIcon(FontIcon.of(FontAwesomeSolid.PLAY, 20, ColorUtil.SUCCESS_FOREGROUND_COLOR));
		}else{
			controller.startMouseMoving();
			startButton.setText("Stop ");
			startButton.setIcon(FontIcon.of(FontAwesomeSolid.SQUARE_FULL, 20, ColorUtil.ERROR_FOREGROUND_COLOR));
		}
	}


	private void render(){
		
	}


	@Override
	public void onChange() {
		render();
	}
	
}
