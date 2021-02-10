package poopgame.ui;

public class MainMenu extends MenuPanel {
	private static final long serialVersionUID = 1L;
	
	public MainMenu() {
		super("wrap 1, fill", "[grow, fill]", "[grow, bottom][grow, top]");
		
		MenuButton createButton = new MenuButton("CREATE GAME");
		createButton.addActionListener(e -> SwingFrame.goTo(new ChampSelection(false)));
		add(createButton);
		MenuButton joinButton = new MenuButton("JOIN GAME");
		joinButton.addActionListener(e -> SwingFrame.goTo(new ChampSelection(true)));
		add(joinButton);
	}
}
