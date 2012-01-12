package edu.cwru.SimpleRTS.agent.visual;

import java.util.List;
import java.util.LinkedList;
import java.util.Hashtable;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class ControlPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

    public static final int MAX_DELAY = 1000;
    public static final float TITLE_FONT = 20f;
    public static final float CONTROL_FONT = 16f;

    private List<ActionListener> listeners = new LinkedList<ActionListener>();
    private Timer timer = new Timer(100, this);
	private JButton stepButton;
    private JToggleButton playButton;
    private JSlider slider;
    private boolean playing = false;

    public ControlPanel() {
        super(new GridBagLayout());
        TitledBorder border = new TitledBorder("Controls");
        border.setBorder(new LineBorder(Color.BLACK, 2));
        border.setTitleFont(border.getTitleFont().deriveFont(TITLE_FONT));
        this.setBorder(border);

        stepButton = new JButton("Step");
        playButton = new JToggleButton("Play");
        stepButton.setFont(stepButton.getFont().deriveFont(CONTROL_FONT));
        playButton.setFont(playButton.getFont().deriveFont(CONTROL_FONT));

        stepButton.addActionListener(this);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                togglePlay();
            }
        });

        JLabel sliderLabel = new JLabel("Play Speed:");
        sliderLabel.setFont(sliderLabel.getFont().deriveFont(CONTROL_FONT));
        
        slider = new JSlider(SwingConstants.HORIZONTAL, 0, MAX_DELAY, MAX_DELAY / 2);
        slider.setMajorTickSpacing(1);
        slider.setPaintTrack(true);
        slider.setOpaque(false);
        Hashtable<Integer,JLabel> labelTable = new Hashtable<Integer,JLabel>();
        JLabel fast = new JLabel("Fast"),
               slow = new JLabel("Slow");
        fast.setFont(fast.getFont().deriveFont(CONTROL_FONT));
        slow.setFont(slow.getFont().deriveFont(CONTROL_FONT));
        labelTable.put(new Integer((int)(0.1*MAX_DELAY)), slow);
        labelTable.put(new Integer((int)(0.9*MAX_DELAY)), fast);
        slider.setLabelTable( labelTable );
        slider.setPaintLabels(true);

        // Add components to panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        this.add(playButton, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        this.add(stepButton, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add(sliderLabel, gbc);
        
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        this.add(slider, gbc);

        gbc.gridy = 3;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        this.add(Box.createVerticalGlue(), gbc);
    }

    private void togglePlay() {
        if (playing) {
            playButton.setText("Play");
            stepButton.setEnabled(true);
            timer.stop();
        } else {
            playButton.setText("Pause");
            stepButton.setEnabled(false);
            timer.start();
        }
        playing = !playing;
        playButton.invalidate();
    }
    
    protected void stopPlay() {
    	playing = true;
    	togglePlay();
    	playButton.setSelected(false);
    }

	public void addStepperListener(ActionListener l) {
        listeners.add(l);
	}
	
	public void removeStepperListener(ActionListener l) {
        listeners.remove(l);
	}

    @Override
    public void actionPerformed(ActionEvent e) {
        timer.setDelay(MAX_DELAY - slider.getValue());
        for (ActionListener listener : listeners) {
            listener.actionPerformed(e);
        }
    }
}
