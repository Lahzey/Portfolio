package bankcity.util;

import java.util.Arrays;
import java.util.Collection;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;

public class InputCatcher implements EventListener{
	
	public final Collection<Type> toCatch;
	
	public InputCatcher(Type... types){
		toCatch = Arrays.asList(types);
	}

	@Override
	public boolean handle(Event event) {
		if(event instanceof InputEvent){
			InputEvent inputEvent =  (InputEvent) event;
			Type type = inputEvent.getType();
			return toCatch.contains(type);
		}else return false;
	}
	
	
	public static class TouchInputCatcher extends InputCatcher{
		
		public TouchInputCatcher(){
			super(Type.touchDown, Type.touchDragged, Type.touchUp);
		}
		
	}
	
}
