package org.jogre.go.client;

import java.util.Vector;

import javax.swing.JComboBox;

import org.jogre.client.ClientConnectionThread;
import org.jogre.client.awt.JogreLabel;
import org.jogre.client.awt.JogrePropertyDialog;
import org.jogre.common.comm.CommNewTable;
import org.jogre.common.util.GameLabels;
import org.jogre.common.util.GameProperties;

/**
 * Go property dialog.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class GoPropertyDialog extends JogrePropertyDialog {

	/**
	 * Constructor to a go property dialog.
	 * 
	 * @param frame   Frame this dialog belongs to.
	 * @param string  
	 * @param modal
	 * @param conn
	 */
	public GoPropertyDialog (GoClientFrame owner, 
			                 String title, 
			                 boolean modal, 
			                 ClientConnectionThread conn) {
		super (owner, title, modal, conn);
	}

	/* (non-Javadoc)
	 * @see org.jogre.client.awt.JogrePropertyDialog#addCustomPropertiesToTable(java.util.Vector, java.util.Vector, org.jogre.common.util.GameProperties, org.jogre.common.comm.CommNewTable)
	 */
	public void addCustomPropertiesToTable(Vector labels, Vector components, CommNewTable newTable) {
		GameProperties gameProperties = GameProperties.getInstance();
		
		// Add properties to table object
		
		// 1) Board sizes
		int size = GoModel.DEFAULT_BOARD_SIZE;
		int index = this.getJogreLabelIndexByName(labels, "board_size");
		if (index > -1) {
			JComboBox boardSize = (JComboBox) components.get(index);
			if (boardSize.getSelectedIndex() == 0)
				size = gameProperties.getInt("board.size.small");
			else if (boardSize.getSelectedIndex() == 1)
				size = gameProperties.getInt("board.size.medium");
			else
				size = gameProperties.getInt("board.size.large");
		}
		newTable.addProperty ("size", String.valueOf(size));
		
		// 2) Score type
		index = this.getJogreLabelIndexByName(labels, "score_type");
		if (index > -1) {
			JComboBox scoreType = (JComboBox) components.get(index);
			newTable.addProperty ("score_type", String.valueOf (scoreType.getSelectedIndex()));
		}
		
		// 3) komi 
		index = this.getJogreLabelIndexByName(labels, "komi");
		if (index > -1) {
			JComboBox komiCB = (JComboBox) components.get(index);
			newTable.addProperty ("komi", String.valueOf (komiCB.getSelectedIndex()));
		}
	}

	/* (non-Javadoc)
	 * @see org.jogre.client.awt.JogrePropertyDialog#addCustomProperties(java.util.Vector, java.util.Vector, org.jogre.common.util.GameProperties)
	 */
	public void addCustomProperties(Vector labels, Vector components) {
		// Add Board Size
		GameProperties gameProperties = GameProperties.getInstance();
		GameLabels gameLabels = GameLabels.getInstance();
		
		// 1) Add board sizes
		JogreLabel boardSizeLabel = new JogreLabel(gameLabels.get("boardSize") + ":");
		boardSizeLabel.setName("board_size");
		labels.add(boardSizeLabel);		
	    String[] boardSizes = new String[3];
	    boardSizes[0] = gameLabels.get("small") + " - " + gameProperties.getInt("board.size.small") + " x " + gameProperties.getInt("board.size.small");
	    boardSizes[1] = gameLabels.get("medium") + " - " + gameProperties.getInt("board.size.medium") + " x " + gameProperties.getInt("board.size.medium");
	    boardSizes[2] = gameLabels.get("large") + " - " + gameProperties.getInt("board.size.large") + " x " + gameProperties.getInt("board.size.large");
	    components.add(new JComboBox(boardSizes));
	    
	    // 2) Add score type
	    JogreLabel scoreTypeLabel = new JogreLabel(gameLabels.get("scoreType") + ":");
	    scoreTypeLabel.setName("score_type");
	    labels.add(scoreTypeLabel);
	    String [] scoreTypes = {gameLabels.get("chinese.area"), gameLabels.get("jananese.territory")};
	    components.add(new JComboBox(scoreTypes));
	    
	    // 3) Add komi
	    JogreLabel komiLabel = new JogreLabel(gameLabels.get("komi") + ":");
	    komiLabel.setName("komi");
	    labels.add(komiLabel);
	    String [] komi = {"2.5", "3.5", "4.5", "5.5", "6.5", "7.5", "8.5"};
	    JComboBox komiCB = new JComboBox(komi); komiCB.setSelectedIndex(4);
	    components.add(komiCB);
	}

	/* (non-Javadoc)
	 * @see org.jogre.client.awt.JogrePropertyDialog#addCustomListeners()
	 */
	public void addCustomListeners() {
	}
}
