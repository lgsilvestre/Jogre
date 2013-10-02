/*
 * JOGRE (Java Online Gaming Real-time Engine) - Server
 * Copyright (C) 2004  Bob Marks (marksie531@yahoo.com)
 * http://jogre.sourceforge.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.jogre.server.administrator;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import nanoxml.XMLElement;

import org.jogre.client.IClient;
import org.jogre.common.comm.Comm;
import org.jogre.common.comm.CommAdminClientData;
import org.jogre.server.ServerLabels;
import org.jogre.server.ServerProperties;
import org.jogre.server.data.IServerData;
import org.jogre.server.data.ServerDataFactory;

/**
 * Panel for the administrator data for the server.
 *
 * @author Bob Marks
 * @version Beta 0.3
 */
public class AdminDataPanel extends JPanel implements ChangeListener, IClient {
		
	// Layout constants
	private static final double PAD = 5;
	private static final double PREF = TableLayout.PREFERRED;
	private static final double FILL = TableLayout.FILL;
	
	private static final List DATA_ORDER = Arrays.asList(IServerData.DATA_ORDER);
		
	private static ServerLabels labels = ServerLabels.getInstance();
	
	// GUI fields
	private JTabbedPane dataTabbedPane;
	private static final int NUM_OF_DATA_PANES = 3;
	private DataTableModel [] dataModels;
	private DataTablePanel [] dataPanels;	
	
	// Other fields
	private JFrame owner;		// link to admin frame
	private AdminClientConnectionThread conn;
    
    /**
     * Constructor to an admin data panel.
     * 
     * @param conn   Connection to server.
     */
    public AdminDataPanel (JFrame owner, AdminClientConnectionThread conn) {   
    	this.conn = conn;
    	this.owner = owner;
    	
    	createGUI();
    }
    
    /**
     * Create GUI.
     */
    private void createGUI() {
    	this.dataTabbedPane = new JTabbedPane ();
    	
    	// Create column names
    	String [][] columnNames = 
    		{{"User", "Password"},
    	     {"Game", "Players", "Results", "Start Time", "End Time", "Score", "History"},
    	     {"Game", "Username", "Rating", "Wins", "Loses", "Draws"}};
    	String [] tabNames = {labels.get("users"), labels.get("game.info"), labels.get("game.summary")};     	
    	
    	// Declare models
    	this.dataModels = new DataTableModel [NUM_OF_DATA_PANES];
    	this.dataPanels = new DataTablePanel [NUM_OF_DATA_PANES];
    	for (int i = 0; i < NUM_OF_DATA_PANES; i++) {
    		this.dataModels[i] = new DataTableModel (columnNames[i]);			// create model
    		this.dataPanels[i] = new DataTablePanel (owner, dataModels[i], 0);	// and panel
    		dataTabbedPane.add(dataPanels[i], tabNames[i]);						// and add panel to tab pane
    		dataPanels[i].setName(tabNames[i]);
    		if (i > 0)
    			dataPanels[i].setPermissions(false, false, false);
    	}
		
		dataTabbedPane.setSelectedIndex(-1);
		dataTabbedPane.addChangeListener(this);
		
		setLayout(new BorderLayout ());
		add (getInfoPanel(), BorderLayout.NORTH);
		add (dataTabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel getInfoPanel() {
    	double [][] sizes = {{PAD, FILL, PAD}, {PAD, PREF, PAD}};
        JPanel panel = new JPanel (new TableLayout (sizes));
        
        labels = ServerLabels.getInstance(); 
        String dataType = ServerDataFactory.getInstance ().getType();
        if (dataType.equals (IServerData.DATABASE))
            dataType += " (" + ServerProperties.getInstance().getCurrentDatabaseConnection() + ")";
        String label = labels.get("persistent.data") + ":  " + dataType;
        panel.add(new JLabel (label), "1,1");
        
    	return panel;
    }
    
    /**
	 * Called when a tab has been selected.
	 * 
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged (ChangeEvent e) {
		int tab = dataTabbedPane.getSelectedIndex();
		if (tab != -1) {
			dataPanels[tab].requestData ();
		}
	}
	
	/**
	 * User table model.
	 */
	private class DataTableModel extends AbstractTableModel {
		
		private String [] columns;
		private String [][] data;
		
		/**
		 * Constructor.
		 * 
		 * @param columns
		 * @param data
		 */
		public DataTableModel (String [] columns, String [][] data) {
			this.columns = columns;
			if (data == null) {
				this.data = new String [0][0];
			} else {
				this.data = data;
			}
		}
		
		public DataTableModel (String [] columns) {
			this.columns = columns;
			this.data = new String [0][0];
		}
			
		public void setData (String [][] data) {
			if (data == null) {
				this.data = new String [0][0];
			} else {
				this.data = data;
			}
		}

		public Object getValueAt(int rowIndex, int columnIndex) { return data[rowIndex][columnIndex]; }		
		public int getColumnCount()  { return columns.length; }	
		public String getColumnName (int column) { return columns [column]; }
		public boolean isCellEditable (int row, int column) { return true; }
		public int getRowCount() { return data.length; }

		public String [] getColumns() { return this.columns; }
	}
	
	/**
	 * Abstract table panel.
	 */
	private class DataTablePanel extends JPanel {
		
		// Declare GUI items
		private JButton newButton, updateButton, deleteButton, refreshButton;		
		private JTable table;
		
		private DataTableModel model;
		private String dateType;
		private JFrame owner;
		
		private boolean newPermission = true, updatePermission = true, deletePermission = true;
		
		public DataTablePanel (JFrame owner, DataTableModel model, int tabOrder) {	        
	        // Set fields
	        this.owner = owner;
	        this.model = model;
	        this.dateType = IServerData.DATA_ORDER[tabOrder];
	        
	        // Create layout
	        double [][] sizes = {{PAD, FILL}, {PAD, FILL, PAD, PREF, PAD}};
	        setLayout(new TableLayout (sizes));
	
	        // Create GUI
	        add (getDataPanel (), "1,1");
	        add (getButtonPanel (), "1,3,l,c");			
	        
	        addListeners ();
	        
	        refresh ();
		}
		
		/**
		 * Set permission for a data pane - i.e. can they create new items, update an item or delete an item.
		 * 
		 * @param newPermission
		 * @param updatePermission
		 * @param deletePermission
		 */
		public void setPermissions (boolean newPermission, boolean updatePermission, boolean deletePermission) {
			this.newPermission = newPermission;
			this.updatePermission = updatePermission;
			this.deletePermission = deletePermission;
			refresh ();
		}
		
		/**
		 * Receive data from server.
		 * 
		 * @param clientData
		 */
		public void receiveMessage (CommAdminClientData clientData) {
			model.setData(clientData.getData());
			model.fireTableDataChanged();
		}
		
		/**
		 * Return button panel;
		 * 
		 * @return
		 */
		private JPanel getButtonPanel () {
			this.newButton = new JButton (labels.get("new"));
			this.updateButton = new JButton (labels.get("update"));
			this.deleteButton = new JButton (labels.get("delete"));
			this.refreshButton = new JButton (labels.get("refresh"));
			
			// Create left panel
			double [][] sizes = {{PAD, PREF, PAD, PREF, PAD, PREF, PAD, PREF, PAD}, {PAD, PREF, PAD}};
	        JPanel panel = new JPanel (new TableLayout (sizes));
	        
	        panel.add(newButton, "1,1");
	        panel.add(updateButton, "3,1");
	        panel.add(deleteButton, "5,1");
	        panel.add(refreshButton, "7,1");
	        panel.setBorder(BorderFactory.createLoweredBevelBorder());
	        
	        return panel;
		}
		
		private JPanel getDataPanel () {
			double [][] sizes = {{PAD, FILL, PAD}, {PAD, FILL, PAD}};
	        JPanel panel = new JPanel (new TableLayout (sizes));
	        
	        table = new JTable (model);
			JScrollPane scrolledTable = new JScrollPane (table);		
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
	        panel.add (scrolledTable, "1,1");
	        
			return panel;
		}
		
		private void addListeners () {
			// Add selection listener
			table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener () {
					public void valueChanged(ListSelectionEvent e) {
						if (e.getValueIsAdjusting()) 
							return;
				        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
				        if (!lsm.isSelectionEmpty()) {
				            refresh ();
				        }			
					}					
				}
			);
			
			// New button
			newButton.addActionListener(new ActionListener() {
				public void actionPerformed (ActionEvent e) {
					newItem ();
				}		
			});
			
			updateButton.addActionListener(new ActionListener() {
				public void actionPerformed (ActionEvent e) {
					updateItem ();
				}		
			});
			
			// Delete button
			deleteButton.addActionListener(new ActionListener() {
				public void actionPerformed (ActionEvent e) {
					deleteItem ();
				}		
			});
			
			// Refresh button
			refreshButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					requestData ();
				}				
			});
		}
		
		/**
		 * Request new data - only adding users is supported at minute.
		 */
		private void newItem () {
			
			String title = getName() + ": " + labels.get("new");
			DataEditDialog editDialog = new DataEditDialog (owner, title, dateType, model.getColumns());
			String [] params = editDialog.getValues();
			
			if (!params[0].equals("")) {	// cancel button hit
				String dataType = IServerData.DATA_ORDER [dataTabbedPane.getSelectedIndex()];
				CommAdminClientData message = 
					new CommAdminClientData (params, dataType, CommAdminClientData.NEW);
				conn.send(message);
			}
		}
		
		/**
		 * Update item. 
		 */
		private void updateItem () {			
			int row = table.getSelectedRow(); 			
			if (row != -1) {
				String title = getName() + ": " + labels.get("update");
				String [] initialValues = new String [model.getColumnCount()];
				for (int i = 0; i < model.getColumnCount(); i++) 
					initialValues[i] = (String)model.getValueAt(row, i);
				
				// Show dialog and get parameters
				DataEditDialog editDialog = new DataEditDialog (owner, title, dateType, model.getColumns(), initialValues, new boolean [] {false, true});
				String [] params = editDialog.getValues();
				
				if (!params[0].equals("")) {	// cancel button hit
					String dataType = IServerData.DATA_ORDER [dataTabbedPane.getSelectedIndex()];
					CommAdminClientData message = 
						new CommAdminClientData (params, dataType, CommAdminClientData.UPDATE);
					conn.send(message);
				}
			}
		}
		
		/**
		 * Delete item - only deleting users is supported at minute.
		 */
		private void deleteItem () {
			int row = table.getSelectedRow(); 			
			if (row != -1) {
			
				String title = getName() + ": " + labels.get("delete");
				String selectedStr = (String)table.getValueAt(row, 0);
				
				int reply = 
					JOptionPane.showConfirmDialog(owner,
						                          labels.get("are.you.sure.you.want.to.delete.this.item") + ": " + selectedStr,
						                          title, 
						                          JOptionPane.YES_NO_OPTION,
						                          JOptionPane.QUESTION_MESSAGE);
				if (reply == JOptionPane.YES_OPTION) {
					String dataType = IServerData.DATA_ORDER [dataTabbedPane.getSelectedIndex()];
					CommAdminClientData message = 
						new CommAdminClientData (new String [] {selectedStr}, dataType, CommAdminClientData.DELETE);
					conn.send(message);
				}
			}
		}
		
		/**
		 * Request data from server.
		 */
		private void requestData () {
			String dataType = IServerData.DATA_ORDER [dataTabbedPane.getSelectedIndex()];
			CommAdminClientData requestData = 
				new CommAdminClientData (dataType);
			if (conn != null)
				conn.send(requestData);
		}
		
		/**
		 * Refresh states of buttons etc.
		 */
		private void refresh () {
			int row = table.getSelectedRow();
			
			newButton.setEnabled(newPermission);
			updateButton.setEnabled(updatePermission && row != -1);
			deleteButton.setEnabled(deletePermission && row != -1);
		}
	}

	/**
	 * Little dialog class for getting user input.
	 * 
	 * Currently this is only supported for n
	 */
	private class DataEditDialog extends JDialog {
		
		// Fields		
		private String dataType;
		private String [] columns;
		
		// GUI items
		private JButton okButton, cancelButton;
		private JTextField [] inputTF;		
		
		/**
		 * Constructor which doesn't take initial values - used for new items usually.
		 * 
		 * @param owner
		 * @param title
		 * @param dataType
		 * @param columns
		 */
		public DataEditDialog (JFrame owner, String title, String dataType, String [] columns) {
			this (owner, title, dataType, columns, null, null);
		}
		
		/**
		 * Constructor which takes initial values - used for updating items usually.
		 * 
		 * @param owner
		 * @param title
		 * @param dataType
		 * @param columns
		 * @param initialValues
		 * @param inputEditable
		 */
		public DataEditDialog (JFrame owner, String title, String dataType, String [] columns, String [] initialValues, boolean [] inputEditable) {
			super (owner, title, true);
			
			// Set fields
			this.dataType = dataType;
			this.columns = columns;
			
			// Create panel
			add(getPanel (initialValues, inputEditable), BorderLayout.CENTER);
			
			// Add listeners
			addListeners ();
			
			pack ();
			Dimension screenSize = getToolkit().getScreenSize();
			this.setLocation ((int)screenSize.getWidth() / 2 - this.getWidth() / 2, (int)screenSize.getHeight()/2 - this.getHeight()/2);
			setVisible (true);
		}
		
		/**
		 * Return panel
		 * 
		 * @param initialValues  Initial values of text fields. 
		 * @param inputEditable  Indicates if a text box is editable or not.
		 * 
		 * @return
		 */
		private JPanel getPanel (String[] initialValues, boolean[] inputEditable) {
			double [][] sizes = {{PAD, 0.5, PAD, 0.5, PAD}, {PAD, PREF, PAD, PREF, PAD}};
			JPanel panel = new JPanel (new TableLayout (sizes));
			JPanel dataPanel = null;
			
			// Create data panel
			if (dataType.equals (IServerData.DATA_USERS)) {	// only support Users at min but can be easily changed in future.
				TableLayout dataLayout = new TableLayout ();
				dataPanel = new JPanel (dataLayout);
				dataLayout.setColumn(new double [] {PAD, 0.5, PAD, 0.5, PAD});
				dataLayout.insertRow(0, PAD);
				
				this.inputTF = new JTextField [columns.length];
				for (int i = 0; i < columns.length; i++) {
					// Add row to layout
					dataLayout.insertRow((i * 2) + 1, PREF);					
					dataLayout.insertRow((i * 2) + 2, PAD);
					
					// Add GUI items
					inputTF[i] = new JTextField (20);
					dataPanel.add(new JLabel (columns[i]), "1," + ((i * 2) + 1));
					dataPanel.add(inputTF[i], "3," + ((i * 2) + 1));
					if (initialValues !=  null)
						inputTF[i].setText(initialValues[i]);
					if (inputEditable != null)
						inputTF[i].setEditable(inputEditable[i]);
				}
				dataLayout.layoutContainer(dataPanel);
				dataPanel.repaint();
			}
			
			// Create buttons
			this.okButton = new JButton (labels.get("ok"));
			this.cancelButton = new JButton (labels.get("cancel"));
			
			// Add items to main panel
			panel.add (dataPanel,    "1,1,3,1");
			panel.add (okButton,     "1,3,r,c");
			panel.add (cancelButton, "3,3,l,c");
			
			return panel;
		}
		
		/**
		 * Add listeners.
		 */
		private void addListeners() {
			this.okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// Validation ...
					boolean valid = true;
					
					for (int i = 0; i < inputTF.length; i++) {
						if (inputTF[i].getText().trim().equals("")) 
							valid = false;
					}
					
					if (!valid) {
						String errFillIn = labels.get("please.fill.in.all.the.textboxes");
						String errWarning = labels.get ("warning");
						JOptionPane.showMessageDialog(owner, errFillIn, errWarning, JOptionPane.WARNING_MESSAGE);
					}
					else
						dispose();
				}				
			});
			this.cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for (int i = 0; i < inputTF.length; i++)
						inputTF[i].setText("");
					
				    dispose();
				}				
			});
		}
		
		/**
		 * Return values to user.
		 * 
		 * @return
		 */
		public String [] getValues () {
			String [] values = new String [inputTF.length];
			for (int i = 0; i < inputTF.length; i++) {
				values[i] = inputTF[i].getText().trim();
			}
			return values;
		}
	}
	
	/**
	 * Receive admin client data message.
	 * 
	 * @see org.jogre.client.IClient#receiveGameMessage(nanoxml.XMLElement)
	 */
	public void receiveGameMessage (XMLElement message) {
		// Retrieve the type of the message
		String type = message.getName();
		
		if (type.equals(Comm.ADMIN_CLIENT_DATA)) {
			CommAdminClientData clientData = new CommAdminClientData (message);
			int dataTypeInt = DATA_ORDER.indexOf(clientData.getDataType());
			dataPanels[dataTypeInt].receiveMessage (clientData);
		}		
	}
	public void receiveTableMessage(XMLElement message, int tableNum) {}
	
	/**
	 * Little main method to test GUI.
	 * 
	 * @param args
	 */
	public static void main (String [] args) {
		ServerProperties.setUpFromFile();
    	JFrame frame = new JFrame ();
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit (0);
            }
        });
        frame.getContentPane().add(new AdminDataPanel (frame, null), BorderLayout.CENTER);
        frame.setLocation(400, 400);
        frame.setSize(600, 400);
        frame.setVisible(true);
    }
}
