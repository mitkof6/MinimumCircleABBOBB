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
	private static final int height = 500, width = 500;
    
    public View(){
        super("View");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setBounds(10, 10, width, height);
        this.setBackground(Color.black);
        
        Panel panel = new Panel(width, height);
        
        this.add(panel);
        this.setVisible(true);
    }
}
