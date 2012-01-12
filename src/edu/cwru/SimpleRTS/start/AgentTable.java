package edu.cwru.SimpleRTS.start;

import java.util.List;
import java.util.ArrayList;

import java.awt.Dimension;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableCellRenderer;

import edu.cwru.SimpleRTS.util.SwingUtils;

@SuppressWarnings("serial")
public class AgentTable extends JTable {

    AgentTableModel tableModel;

    public AgentTable() {
        tableModel = new AgentTableModel();
        setModel(tableModel);
        setPreferredScrollableViewportSize(new Dimension(324, 200));
        setFillsViewportHeight(true);
    }

    /**
     * Adds a new empty row to the AgentTable
     */
    public void addRow() {
        SwingUtils.invokeNowOrLater(new Runnable() {
            @Override
            public void run() {
                tableModel.addRow();
            }
        });
    }

    public void deleteSelectedRows() {
        SwingUtils.invokeNowOrLater(new Runnable() {
            @Override
            public void run() {
                int[] selection = getSelectedRows();
                for (int i = 0; i < selection.length; i++) {
                    selection[i] = convertRowIndexToModel(selection[i]);
                }
                tableModel.deleteRows(selection);
            }
        });
    }

    static class AgentTableModel extends AbstractTableModel {

        public AgentTableModel() {
            data.add(new AgentData());
        }

        static class AgentData {

            static final String[] COLUMN_NAMES =
                {"Player Number", "Class", "Arguments"};
            static final Class[] COLUMN_CLASSES =
                {Integer.class, String.class, String.class};

            private Integer playerNum = 0;
            private String clazz = "";
            private String argString = "";

            public Object get(int index) {
                switch(index) {
                    case 0:
                        return playerNum;
                    case 1:
                        return clazz;
                    case 2:
                        return argString;
                }
                return null;
            }

            public void set(int index, Object value) {
                switch(index) {
                    case 0:
                        playerNum = (Integer) value;
                        return;
                    case 1:
                        clazz = (String) value;
                        return;
                    case 2:
                        argString = (String) value;
                        return;
                }
            }

        }

        List<AgentData> data = new ArrayList<AgentData>();

        public void addRow() {
            AgentData agentData = new AgentData();
            int n = data.size();
            if (n > 0) {
                agentData.playerNum = data.get(n - 1).playerNum + 1;
            }
            data.add(agentData);
            fireTableDataChanged();
        }

        public void deleteRows(int[] rows) {
            // We need to be careful since indices
            // change as items are deleted..
            List<AgentData> toRemove = new ArrayList<AgentData>(rows.length);
            for (int row : rows) {
                toRemove.add(data.get(row));
            }
            for (AgentData deadAgent : toRemove) {
                int deadRow = data.indexOf(deadAgent);
                data.remove(deadAgent);
                fireTableRowsDeleted(deadRow, deadRow);
            }
        }

        @Override
        public int getColumnCount() {
            return AgentData.COLUMN_NAMES.length;
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public String getColumnName(int c) {
            return AgentData.COLUMN_NAMES[c];
        }

        @Override
        public Object getValueAt(int row, int col) {
            return data.get(row).get(col);
        }

        @Override
        public Class getColumnClass(int c) {
            return AgentData.COLUMN_CLASSES[c];
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return true;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            data.get(row).set(col, value);
            fireTableCellUpdated(row, col);
        }
    }

}
