package screens;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import framework.*;

public class StartUpScreen extends Screen {
	
	JPanel pnlMain;
	JPanel pnlIO;
	JButton btnConfirm;
	JTextField txtWorkspace;
	JButton btnWorkspace;
	JLabel lblWorkspace;

	public StartUpScreen(Game game) {
		super(game);
	}

	@Override
	public void update(float deltaTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void present(float deltaTime) {
		// TODO Auto-generated method stub
//        game.getGraphics().clear(0xff000000);
//		
//		game.getGraphics().drawText("test", 50, 50, 0xffffffff);
	}

	public void init(){
				
		pnlMain = new JPanel();
		pnlMain.setLayout(new BorderLayout());
		
		pnlIO = new JPanel();
		pnlIO.setLayout(new FlowLayout());
		
		btnConfirm = new JButton("OK");
		btnConfirm.addActionListener(new StartUpScreen.MyButtonListener());
		
		txtWorkspace = new JTextField(game.getGlobalVars().workspace);
		txtWorkspace.setPreferredSize(new Dimension(350,25));
		btnWorkspace = new JButton("...");
		btnWorkspace.addActionListener(new StartUpScreen.MyButtonListener());
		btnWorkspace.setPreferredSize(new Dimension(20,25));
		lblWorkspace = new JLabel("workspace location:");
		
		pnlIO.add(lblWorkspace);
		pnlIO.add(txtWorkspace);
		pnlIO.add(btnWorkspace);
		pnlIO.setMinimumSize(new Dimension(400,50));
		
		//Add to pnlMain
		pnlMain.add(pnlIO,BorderLayout.CENTER);
		pnlMain.add(btnConfirm,BorderLayout.PAGE_END);
		
		game.setWindowContainer(pnlMain);
		game.setWindowSize(400, 150);
		game.setWindowCentered(true);
	}
	
	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	private class MyButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			
			if(src==btnConfirm){
				game.getGlobalVars().workspace = txtWorkspace.getText();
				game.saveSettingsXML();
				game.setScreen(new EditorScreen(game));
			}
			if(src==btnWorkspace){
				//Open a fileIO dialog
				//TODO: IODialog
			}
			
		}
		
	
	}
	
}
