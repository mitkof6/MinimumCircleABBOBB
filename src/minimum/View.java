/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minimum;

import java.awt.Color;

import javax.swing.JFrame;

/**
 *
 * @author STANEV
 */
public class View extends JFrame{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int height = 800, width = 800;
    
    public View(){
        super("View");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setBounds(10, 10, width, height);
        this.setBackground(Color.black);
        
        Panel panel = new Panel(width-30, height-30);
        
        this.add(panel);
        this.setVisible(true);
    }
}
