package antipasto.GUI.ImageListView;

import javax.swing.BoxLayout;

import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.io.*;

import antipasto.GUI.GadgetListView.GadgetPanel;
import antipasto.GUI.GadgetListView.GadgetPanelEvents.ActiveGadgetObject;
import antipasto.GUI.GadgetListView.GadgetPanelEvents.IActiveGadgetChangedEventListener;
import antipasto.Interfaces.IModule;

import processing.app.Base;
import processing.app.Serial;
import processing.app.SerialException;
import processing.app.FlashTransfer;
import javax.swing.TransferHandler;

import javax.swing.*;
import java.awt.datatransfer.*;
import java.util.List;
import java.util.Iterator;
import java.net.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.math.*;

import java.awt.BorderLayout;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ImageListPanel extends JPanel implements IActiveGadgetChangedEventListener, ComponentListener {
	private ImageListView list;
	final FlashTransfer imageTransfer;
	Serial mySerial = null;
	private JButton removeButton;
	private JButton transferButton;
	private GadgetPanel panel;
	private IModule _module;
	private JProgressBar progressBar;
	boolean isTransfering = false; 
	private JLabel infoLabel;
	private JLabel progressLabel;
	private long totalSize = 0;
	private long totalFileCount = 0;
	private JScrollPane scrollPane;
	
	public ImageListPanel(GadgetPanel panel, FlashTransfer imageTransfer){
		this.imageTransfer = imageTransfer;	
		this.init();
	}
	
	private void init(){
		this.createTransferButton();
		this.transferButton.setVisible(true);
		
		this.setLayout(new BorderLayout());
		
		this.createRemoveButton();
		
		/* The north panel */
		JPanel northPanel = new JPanel();
		northPanel.setBackground(new Color(0x04, 0x4F, 0x6F));
		northPanel.setLayout(new BorderLayout());
		
		/* I'm the transfer buttons area */
		JPanel northButtonPanel = new JPanel();
		northButtonPanel.setLayout(new BorderLayout());
		northButtonPanel.setBackground(new Color(0x04, 0x4F, 0x6F));
		northButtonPanel.add(transferButton, BorderLayout.WEST);
		northButtonPanel.add(removeButton, BorderLayout.EAST);
		
		/* I'm the transfer label area */
		JPanel northLabelPanel = new JPanel();
		northLabelPanel.setBackground(new Color(0x04, 0x4F, 0x6F));	
		JLabel infoLabel = new JLabel(" Drop files below. ");
		infoLabel.setForeground(Color.white);
		northLabelPanel.add(infoLabel);
		
		/* Add everything that's North */
		northPanel.add(northButtonPanel,BorderLayout.EAST);
		northPanel.add(northLabelPanel, BorderLayout.WEST);
		this.add(northPanel, BorderLayout.NORTH);
		
		/* The south label */
		JPanel southProgressPanel = new JPanel();
		southProgressPanel.setLayout(new BorderLayout());
		southProgressPanel.setBackground(new Color(0x04, 0x4f, 0x6f));
		
		/* A progress bar for the transfer */
		progressBar = new JProgressBar();
		progressBar.setVisible(false);
		
		/* A label to describe stuff */
		progressLabel = new JLabel("  Files: " + totalFileCount + 
	   					   	       " | Total Size: " + totalSize / 1000 + " KB");
		progressLabel.setForeground(Color.WHITE);
		
		/* Add everything that's South */
		southProgressPanel.add(progressBar,BorderLayout.EAST);
		southProgressPanel.add(progressLabel, BorderLayout.WEST);
		
		this.add(southProgressPanel, BorderLayout.SOUTH);
		
		this.transferButton.setVisible(true);

		this.removeButton.setVisible(true);
		this.progressBar.setVisible(true);
		this.progressLabel.setVisible(true);
		
	}
	
	public void setModule(IModule module){
		if(module != null){
			/* Build the center */
			File[] files = module.getData();
			list = new ImageListView(module);
			if(scrollPane != null){
				this.remove(scrollPane);
				scrollPane.setVisible(false);
				scrollPane = null;				//let's take out the trash!
			}
			scrollPane = new JScrollPane(list);
			this.add(scrollPane, BorderLayout.CENTER);
			scrollPane.setSize(this.getWidth(), this.getHeight() - transferButton.getHeight());
			scrollPane.setVisible(true);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			list.setVisible(true);
			
		}
		this.transferButton.setVisible(true);
		this.setSizesOfComponents();
		
		_module = module;
	}
	
	public IModule getModule(){
		return this._module;
	}
	
	public void paint(java.awt.Graphics g){
		//this.setSizesOfComponents();
		super.paint(g);
	}
	
	private void setSizesOfComponents(){
		Dimension parentSize = this.getParent().getSize();
		Dimension btnSize = this.transferButton.getSize();
		double height = this.getParent().getSize().getHeight() - this.transferButton.getSize().getHeight();
		int heightI = (int)height;
		Dimension listViewSize = new Dimension(this.getParent().getWidth(), heightI);
		list.setSize(listViewSize);
		//this.repaint();
	}
	
	private JButton createRemoveButton(){
		this.removeButton = new JButton("Remove File");
		this.removeButton.setBackground(new Color(0x04, 0x4F, 0x6F));
		this.removeButton.addMouseListener(
			new MouseListener(){
										   
			public void mouseClicked(MouseEvent arg0) {
				if(list.getSelectedValue() != null){
					list.removeSelected();
				}
			}

			public void mouseEntered(MouseEvent arg0) {
			}

			public void mouseExited(MouseEvent arg0) {	
			}

			public void mousePressed(MouseEvent arg0) {
			}

			public void mouseReleased(MouseEvent arg0) {
			}
										   
			});
		return this.removeButton;
	}
	
	private void createTransferButton(){
		this.transferButton = new JButton("Transfer");
		this.transferButton.setBackground(new Color(0x04, 0x4F, 0x6F));
		this.transferButton.addMouseListener(new MouseListener(){
			
			public void mouseClicked(MouseEvent arg0) {
				if (isTransfering == false) {
					transfer();
				} else {
					reset();		//already transfering, reset
				}
			}

			public void mouseEntered(MouseEvent arg0) {
			}

			public void mouseExited(MouseEvent arg0) {
			}

			public void mousePressed(MouseEvent arg0) {	
			}

			public void mouseReleased(MouseEvent arg0) {
			}
			
		});
	}
	
	/*
	 *Transfers the files
	 */
	private void transfer(){
		try {
			final File fileList[] = this._module.getData();
			final FlashTransfer transfer = imageTransfer;
			isTransfering = true;
			
			transferButton.setText("Sending...");
			progressBar.setVisible(true);
			progressBar.setMaximum(fileList.length+1);
			progressBar.setValue(1);	//bump the progress bar
										//up one for visual friendliness
			new Thread(
					   new Runnable() {
					   public void run() {
						   				   
						   try {
							   mySerial = new Serial();
							   transfer.setSerial(mySerial);
							   
							   /* For each file... */
							   for(int i = 0; i < fileList.length; i++){
								   System.out.println("sending: " + fileList[i].getName());
								   transfer.sendFile(fileList[i]);
								   progressBar.setValue(progressBar.getValue()+1);
								   progressBar.repaint();
							   }
							   
							   /* Exit the image transfer */
							   transfer.close();
							   
							   /* Reset UI after transfer */
							   reset();
							   
						   } catch (SerialException e) {
							  
							   e.printStackTrace();
						   }
				        
					   }}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	/*
	 * Reset the UI after transfer.
	 */
	private void reset() {
		
		   mySerial.dispose();
		   transferButton.setText("Transfer");
		   progressBar.setValue(0);
		   progressBar.setVisible(false);
		   isTransfering = false;
		
	}
	
	public void onActiveGadgetChanged(ActiveGadgetObject obj) {
	}

	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
