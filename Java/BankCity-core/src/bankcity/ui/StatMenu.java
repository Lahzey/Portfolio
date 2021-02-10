package bankcity.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import bankcity.BankCity;
import bankcity.data.FileLocations;
import bankcity.ui.stages.InGameStage;
import bankcity.util.ColorDrawable;
import bankcity.util.DynamicLabel;
import bankcity.util.DynamicLabel.DynamicString;
import bankcity.util.Scene2DUtil;

public class StatMenu extends Table{

	private InGameStage inGameStage;
	
	public static final int WIDTH = 600;
	public static final int HEIGHT = 100;
	public static final int INNER_PADDING = 5;
	public static final int OUTER_PADDING = 15 + INNER_PADDING;
	public static final int ELEMENT_WIDTH = WIDTH / 3;
	public static final int LABEL_HEIGHT = HEIGHT / 4;

	public StatMenu(InGameStage inGameStage){
		this.inGameStage = inGameStage;
		init();
	}
	
	private void init(){
		top();
		setBackground(new TextureRegionDrawable(BankCity.getImage(FileLocations.STAT_MENU + "/background.png")));
		
		Table dateArea = new Table();
		Image dateIcon = new Image(new TextureRegionDrawable(BankCity.getImage(FileLocations.STAT_MENU + "/date.png")), Scaling.fit);
		dateArea.add(dateIcon).width(LABEL_HEIGHT).height(LABEL_HEIGHT).left().pad(OUTER_PADDING);
		Label dateLabel = new Label("", BankCity.SKIN){
			@Override
			public void draw(Batch batch, float parentAlpha) {
				setText(inGameStage.game.timeSystem.date.toString());
				super.draw(batch, parentAlpha);
			}
		};
		dateLabel.setAlignment(Align.right);
		dateArea.add(dateLabel).grow().right().pad(INNER_PADDING);
		add(dateArea).top().width(ELEMENT_WIDTH).height(LABEL_HEIGHT);
		
		ImageButton bcButton = Scene2DUtil.createImageButton(BankCity.TEXTURE_MANAGER, FileLocations.BC_BUTTON + "/normal.png", FileLocations.BC_BUTTON + "/hover.png", FileLocations.BC_BUTTON + "/pressed.png", FileLocations.BC_BUTTON + "/disabled.png");
		add(bcButton).width(ELEMENT_WIDTH).height(HEIGHT);
		bcButton.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				InternalBrowser.INSTANCE.open(inGameStage.game);
			}
		});
		
		Table moneyArea = new Table();
		Image moneyIcon = new Image(new TextureRegionDrawable(BankCity.getImage(FileLocations.STAT_MENU + "/money.png")), Scaling.fit);
		moneyArea.add(moneyIcon).width(LABEL_HEIGHT).height(LABEL_HEIGHT).left().pad(INNER_PADDING);
		Label moneyLabel = new Label("", BankCity.SKIN){
			@Override
			public void draw(Batch batch, float parentAlpha) {
				setText(inGameStage.game.getMoneyString());
				super.draw(batch, parentAlpha);
			}
		};
		moneyLabel.setAlignment(Align.right);
		moneyArea.add(moneyLabel).grow().right().pad(OUTER_PADDING);
		add(moneyArea).top().width(ELEMENT_WIDTH).height(LABEL_HEIGHT);
		
		Table moneyTooltip = new Table();
		moneyTooltip.setBackground(new ColorDrawable(Color.WHITE, Color.BLACK, 3));
		moneyTooltip.pad(5);
		DynamicLabel tooltipText = new DynamicLabel(new DynamicString(){
			@Override
			public String toString() {
				double yield = inGameStage.game.economySystem.getTotalYield();
				String yieldText = inGameStage.game.formatMoney(yield);
				if(yield > 0) yieldText = "[GREEN]+" + yieldText;
				else if(yield < 0) yieldText = "[RED]" + yieldText;
				else yieldText = "[BLACK]+" + yieldText;
				return yieldText + "[BLACK] / year";
			}
		});
		tooltipText.getStyle().font.getData().markupEnabled = true;
		moneyTooltip.add(tooltipText);
		moneyArea.addListener(new Tooltip<Table>(moneyTooltip));
	}
	
}
