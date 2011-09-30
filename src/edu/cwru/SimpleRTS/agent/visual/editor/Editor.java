package edu.cwru.SimpleRTS.agent.visual.editor;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ListIterator;

import javax.swing.AbstractListModel;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListDataListener;

import org.json.JSONException;

import edu.cwru.SimpleRTS.agent.visual.GameScreen;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.environment.State.StateBuilder;
import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.model.upgrade.UpgradeTemplate;
import edu.cwru.SimpleRTS.util.GameMap;
import edu.cwru.SimpleRTS.util.TypeLoader;

public class Editor extends JFrame {
	
	GameScreen screen;
	State state;
	JComboBox templateSelector;
	JComboBox playerSelector;
	JButton addPlayer;
	ButtonGroup cursorGroup;
	JTextField resourceAmount;
	JRadioButton selectPointer;
	JRadioButton selectUnit;
	JRadioButton selectTree;
	JRadioButton selectMine;
	JButton save;
	JTextArea error;
	
	public Editor(GameScreen screen, State state, String templatefilename) {
		this.screen = screen;
		this.state = state;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(screen.getLocation().x+screen.getWidth()+1, screen.getLocation().y);
		setSize(100,screen.getHeight());
		setTitle("Editor");
		setLayout(new FlowLayout());
		
		screen.addMouseListener(this.new EditorMouseListener());
		
		DefaultComboBoxModel model = new DefaultComboBoxModel(new Object[]{});
		playerSelector = new JComboBox(model);
		addPlayer = new JButton("Add Player");
		addPlayer.addActionListener(new ActionListener() {
			DefaultComboBoxModel model;
			String templatefilename;
			State state;
			public ActionListener setOutsideInformationAndClick(DefaultComboBoxModel model, String templatefilename, State state) {
				this.model = model;
				this.templatefilename = templatefilename;
				this.state = state;
				actionPerformed(null); //click it once so it starts with one
				return this;
			}
			@Override
			public void actionPerformed(ActionEvent e) {
				if(model.getSize() < 8)
				{
					int newPlayerNum = model.getSize();
					try {
						List<Template> newPlayerTemplates = TypeLoader.loadFromFile(templatefilename, newPlayerNum);
						for (Template t : newPlayerTemplates) {
							state.addTemplate(t, newPlayerNum);
						}
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					model.addElement("Player "+newPlayerNum);
				}
			}			
		}.setOutsideInformationAndClick(model,templatefilename,state));
		cursorGroup = new ButtonGroup();
		selectPointer = new JRadioButton("Pointer");
		selectPointer.setSelected(true);
		cursorGroup.add(selectPointer);
		selectUnit = new JRadioButton("Unit");
		cursorGroup.add(selectUnit);
		selectTree = new JRadioButton("Tree");
		cursorGroup.add(selectTree);
		selectMine = new JRadioButton("Mine");
		cursorGroup.add(selectMine);
		String[] unitnames;
		List<UnitTemplate> tempunittemplates=null;
		try {
			tempunittemplates = TypeLoader.loadUnitsFromFile(templatefilename, -3);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		unitnames = new String[tempunittemplates.size()];
		for (int i = 0; i<unitnames.length;i++) {
			unitnames[i] = tempunittemplates.get(i).getName();
		}
		templateSelector = new JComboBox(unitnames);		
		templateSelector.addActionListener(new ActionListener() {
			JRadioButton button;
			public ActionListener setButton(JRadioButton button) {
				this.button = button;
				return this;
			}
			@Override
			public void actionPerformed(ActionEvent e) {
				button.setSelected(true);
			}			
		}.setButton(selectUnit));
		error = new JTextArea(4,10);
		error.setForeground(Color.RED);
		error.setEditable(false);
		error.setLineWrap(true);
		error.setWrapStyleWord(true);
		resourceAmount = new JTextField(4);
		resourceAmount.setText("100");
		save = new JButton("Save");
		save.addActionListener(new ActionListener() {
			GameMap map;
			public ActionListener setState(State state) {
				map = new GameMap(state);
				return this;
			}
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				int result = jfc.showSaveDialog(Editor.this);
				if(result != JFileChooser.APPROVE_OPTION)
					return;
				GameMap.storeMap(jfc.getSelectedFile().toString(), map);
			}
			
		}.setState(state));
		
		add(templateSelector);
		add(playerSelector);
		add(addPlayer);
		add(selectPointer);
		add(selectUnit);
		add(selectTree);
		add(selectMine);
		add(resourceAmount);
		add(save);
		add(error);
		
		setVisible(true);
	}
	
	
	private class EditorMouseListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			int x = screen.unscaleX(e.getX());
			int y = screen.unscaleY(e.getY());
			System.out.println(x+","+y);
			int player = playerSelector.getSelectedIndex();
			if(!selectPointer.isSelected() && (state.unitAt(x, y) != null || state.resourceAt(x, y) != null))
			{
				error.setText("Cannot place on top of existing object.");
				return;
			}
			if(selectUnit.isSelected())
			{
				String name = (String)templateSelector.getSelectedItem();
				Unit u = ((UnitTemplate)state.getTemplate(player,name)).produceInstance();
				u.setxPosition(x);
				u.setyPosition(y);
				state.addUnit(u);
			}
			else if(selectTree.isSelected())
			{
				int amount;
				try {
					amount = Integer.parseInt(resourceAmount.getText());
				}
				catch(Exception ex) {
					error.setText("Invalid resource quantity.");
					return;
				}
				ResourceNode r = new ResourceNode(ResourceNode.Type.TREE, x, y, amount);
				state.addResource(r);
			}
			else if(selectMine.isSelected())
			{
				int amount;
				try {
					amount = Integer.parseInt(resourceAmount.getText());
				}
				catch(Exception ex) {
					error.setText("Invalid resource quantity.");
					return;
				}
				ResourceNode r = new ResourceNode(ResourceNode.Type.GOLD_MINE, x, y, amount);
				state.addResource(r);
			}
			screen.updateState(state.getView());
		}

	}

	public static void main(String[] args) throws FileNotFoundException, JSONException {
		State state = null;
		if(args.length > 0)
		{
			try {
			state = GameMap.loadMap(args[0]).getState();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		if(state == null)
		{
			StateBuilder builder = new StateBuilder();
			builder.setSize(32, 32);
			state = builder.build();
		}
		GameScreen screen = new GameScreen(null);
		screen.updateState(state.getView());
		Editor editor = new Editor(screen,state,"data/unit_templates");
	}
}
