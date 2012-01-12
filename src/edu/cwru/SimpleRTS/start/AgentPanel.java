package edu.cwru.SimpleRTS.start;

import java.awt.Color;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import edu.cwru.SimpleRTS.start.StartWindow;
import edu.cwru.SimpleRTS.start.AgentTable;

@SuppressWarnings("serial")
public class AgentPanel extends JPanel {

    AgentTable agentTable;

    public AgentPanel() {
        super(new GridBagLayout());
        TitledBorder border = new TitledBorder("Agents");
        border.setBorder(new LineBorder(Color.BLACK, 2));
        border.setTitleFont(border.getTitleFont().deriveFont(StartWindow.TITLE_FONT));
        this.setBorder(border);

        agentTable = new AgentTable();
        JButton addButton = new JButton("Add");
        JButton deleteButton = new JButton("Delete");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        add(new JScrollPane(agentTable), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        add(Box.createHorizontalGlue(), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.0;
        add(addButton, gbc);

        gbc.gridx = 2;
        add(deleteButton, gbc);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agentTable.addRow();
            }
        });

    }

}
