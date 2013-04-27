package minimum;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;


import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


import uk.co.geolib.geolib.C2DCircle;
import uk.co.geolib.geolib.C2DLine;
import uk.co.geolib.geolib.C2DPoint;
import uk.co.geolib.geolib.C2DRect;
import uk.co.geolib.geopolygons.C2DPolygon;
import uk.co.geolib.geoview.GeoDraw;

/**
 *
 * @author STANEV
 */
public class Panel extends JPanel{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int size = 10;//size of points
    private C2DPolygon polygon;//points polygon
    private GeoDraw drawer;//drawer class
    private ArrayList<C2DPoint> pointList;//pointSet
    private C2DCircle minimumCircle;//minimum circle instance
    private C2DRect minimumBoundingBox;//minimum abb rect instance
    @SuppressWarnings("unused")
	private Graphics2D g2d;//2d graphics
    private C2DPoint obb1, obb2, obb3, obb4;//four corners of obb
    private boolean minCFlag = true, minABBFlag = true, minOBBFlag = true;
    
    /**
     * Constructor, initializes variables and run the algorithms..
     * 
     * @param width of window
     * @param height of window
     */
    public Panel(int width, int height){
        
    	//chose window
    	JCheckBox minCCheckBox = new JCheckBox("Minimum circle");
    	JCheckBox minABBCheckBox = new JCheckBox("Minimum axis bounding box");
    	JCheckBox minOBBCheckBox = new JCheckBox("Minimum oriented bounding box");
    	JTextField sizeTextField = new JTextField("100",10);
    	String message = "Chose!";  
    	Object[] params = {message, sizeTextField, minCCheckBox,
    			minABBCheckBox, minOBBCheckBox};  
    	JOptionPane.showConfirmDialog(null, params,
    			"Setting", JOptionPane.YES_NO_OPTION);  
    	minCFlag = minCCheckBox.isSelected();
    	minABBFlag = minABBCheckBox.isSelected();
    	minOBBFlag = minOBBCheckBox.isSelected();
    	size = Integer.parseInt(sizeTextField.getText());
    	
    	
        //init
        polygon = new C2DPolygon();
        drawer = new GeoDraw();
        g2d = (Graphics2D) this.getGraphics();
        
        //set rectangle
        C2DRect rect = new C2DRect(100,100,width-100,height-100);
        
        //generate random points
        polygon.CreateRandom(rect, size, size);
        //poly.CreateRegular(new C2DPoint(width/2, width/2), width/2-30, size);
        
        //copy point to point set
        pointList = new ArrayList<C2DPoint>();
        polygon.GetPointsCopy(pointList);
        
        
        long startTime = System.currentTimeMillis();
        
        //minimum circle
        minimumCircle = minimumCircle(pointList);
        
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("MC: "+elapsedTime);
        
        startTime = System.currentTimeMillis();
        
        //minimum axis bounding box
        minimumBoundingBox = axisBoundingBox(pointList);
        
        stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;
        System.out.println("ABB: "+elapsedTime);
        
        startTime = System.currentTimeMillis();
        
        //minimum oriented bounding box
        minimumOrientedBoundingBox(polygon);
        
        stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;
        System.out.println("OBB: "+elapsedTime);
        
    }
    
    /**
     * Render method
     */
    public void paintComponent(Graphics g){
    	Graphics2D g2d = (Graphics2D) g;
    	
        //draw points
    	g2d.setColor(Color.red);
        for(int i = 0;i<pointList.size();i++){
        	g2d.fillRect((int)pointList.get(i).x, 
        			(int)pointList.get(i).y, 3, 3);
        }
        
        
        //minimum circle
        if(minCFlag){
        	g2d.setColor(Color.blue);
            drawer.Draw(minimumCircle, g2d);
        }
        
        //minimum axis bounding box
        if(minABBFlag){
        	g2d.setColor(Color.green);
            drawer.Draw(minimumBoundingBox, g2d);
        }
        
        //oriented bounding box
        if(minOBBFlag){
        	g2d.setColor(Color.yellow);
            drawer.Draw(new C2DLine(obb1, obb2), g2d);
            drawer.Draw(new C2DLine(obb2, obb3), g2d);
            drawer.Draw(new C2DLine(obb3, obb4), g2d);
            drawer.Draw(new C2DLine(obb4, obb1), g2d);
        }
    
    }
    
    /**
     * Minimum circle method, finds the minimum circle containing
     * all points. Its an algorithm witch uses two sub methods.
     * 
     * @param pointSet points
     * @return the minimum circle containing the points
     */
    private C2DCircle minimumCircle(ArrayList<C2DPoint> pointSet){
    	//variables
    	C2DPoint p;
    	C2DCircle d = new C2DCircle();
    	ArrayList<C2DPoint> newPointSet = new ArrayList<C2DPoint>();
    	
    	//shuffle point
    	Collections.shuffle(pointSet);
    	
    	//init newPointSet, circle
    	newPointSet.add(pointSet.get(0));
    	newPointSet.add(pointSet.get(1));
    	d.SetMinimum(newPointSet.get(0), newPointSet.get(1));
    	
    	for(int i = 2;i<pointSet.size();i++){
    		p = pointSet.get(i);
    		if(!d.Contains(p)){
    			d = minimumCircleWithOnePoint(newPointSet, p);;
    		}
    		newPointSet.add(p);
    	}
    	return d;
    }
    
    /**
     * Minimum circle sub method. Given a set of points and a point. Returns
     * the minimum circle containing all points and passes through q.
     * 
     * @param oldPointSet subset of points
     * @param q a bias point of the circle
     * @return the minimum circle of the subset 
     */
    private C2DCircle minimumCircleWithOnePoint(ArrayList<C2DPoint> oldPointSet,
    		C2DPoint q){
    	//variables
    	C2DPoint p;
    	C2DCircle d = new C2DCircle();
    	ArrayList<C2DPoint> newPointSet = new ArrayList<C2DPoint>();
    	
    	//shuffle point
    	Collections.shuffle(oldPointSet);
    	
    	//init newPointSet, circle;
    	newPointSet.add(oldPointSet.get(0));
    	d.SetMinimum(q, newPointSet.get(0));
    	
    	for(int i = 1;i<oldPointSet.size();i++){
    		p = oldPointSet.get(i);
    		if(!d.Contains(p)){
    			d = minimumCircleWithTwoPoint(newPointSet, p, q);
    		}
    		newPointSet.add(p);
    	}
    	return d;
    }
    
    /**
     * This method defines the minimum circle of a given point set
     * with two bias points.
     * 
     * @param oldPointSet a set of points
     * @param q1 the first bias point
     * @param q2 the second bias point
     * @return the minimum circle of the subset
     */
    private C2DCircle minimumCircleWithTwoPoint(ArrayList<C2DPoint> oldPointSet,
    		C2DPoint q1, C2DPoint q2){
    	//variables
    	C2DPoint p;
    	C2DCircle d = new C2DCircle();
    	
    	//inti circle
    	d.SetMinimum(q1, q2);
    	
    	for(int i = 1;i<oldPointSet.size();i++){
    		p = oldPointSet.get(i);
    		if(!d.Contains(p)){
    			d.SetMinimum(q1, q2, oldPointSet.get(i));
    		}
    	}
    	return d;
    }
    
    /**
     * This method finds the minimum axis bounding rectangle
     * of a given set of points.
     * 
     * @param pointSet a set of points
     * @return minimum axis rectangle
     */
    private C2DRect axisBoundingBox(ArrayList<C2DPoint> pointSet){
    	//variables
    	C2DPoint pLeft, pRight, pUp, pDown, p;
    	
    	//init
    	pLeft = pRight = pUp = pDown = pointSet.get(0);
    	
    	for(int i = 1;i<pointSet.size();i++){
    		p = pointSet.get(i);
    		if(!onRight(pLeft, p)){
    			pLeft = p;
    		}else{
    			if(onRight(pRight, p)){
    				pRight = p;
    			}
    		}
    		
    		if(!onUp(pDown, p)){
    			pDown = p;
    		}else{
    			if(onUp(pUp, p)){
    				pUp = p;
    			}
    		}
    	}
    	
    	return new C2DRect(new C2DPoint(pLeft.x, pUp.y),
    			new C2DPoint(pRight.x, pDown.y));
    }
    
    /**
     * This method checks of a points q is on the rigth side
     * of point p
     * 
     * @param p the first point
     * @param q the second point
     * @return a boolean
     */
    private boolean onRight(C2DPoint p, C2DPoint q){
    	if(p.x<=q.x){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    /**
     * This method checks if q(y)<p(y)
     * 
     * @param p the first point
     * @param q the second point
     * @return a boolean
     */
    private boolean onUp(C2DPoint p, C2DPoint q){
    	if(p.y<=q.y){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    /**
     * This method finds the minimum oriented bounding box of
     * a given set of points. For a given set of points, rotate
     * the points, find the axis bounding box. Rotation in [0-90]
     * keep the rectangle corners witch minimize the area.
     * 
     * @param polygon of points
     */
    private void minimumOrientedBoundingBox(C2DPolygon polygon){
    	//variables
    	C2DPoint center = new C2DPoint(0,0);
    	double angle;

    	//find angle
    	angle = findTheta(polygon, center, 1);
    	
    	//oriented bounding box points
    	obb1.RotateToRight(-angle, center);
    	obb2.RotateToRight(-angle, center);
    	obb3.RotateToRight(-angle, center);
    	obb4.RotateToRight(-angle, center);
    }
    
    /**
     * The method to find the minimum angle.
     * 
     * @param polygon a set of point
     * @param center the center of rotation
     * @param step of the angle
     * @return the minimum angle
     */
    private double findTheta(C2DPolygon polygon, C2DPoint center, double step){
    	//variables
    	C2DRect rect = new C2DRect();
    	ArrayList<C2DPoint> pointSet =  new ArrayList<C2DPoint>();
    	double area = Double.MAX_VALUE;
    	double minAngle = step;
	
    	//get points copy
    	polygon.GetPointsCopy(pointSet);
    	
    	for(double angle = step;angle<90.0;angle+=step){
    		//rotate the points
    		for(int i = 0;i<pointSet.size();i++){
    			pointSet.get(i).RotateToRight(step, center);
    		}
    		
    		//find minimum axis box
    		rect = axisBoundingBox(pointSet);
    		
    		//check for min
    		if(area>rect.GetArea()){
    			area = rect.GetArea();
    			minAngle = angle;
    			obbBoundingPoints(rect);
    		}
    	}
    	
    	System.out.println("Area: "+area+"Angle: "+minAngle);
    	return minAngle;
    }
    
    /**
     * This method gets a rectangle and defines the for corners
     * 
     * @param rect a rectangle to define
     */
    private void obbBoundingPoints(C2DRect rect){
    	obb1 = rect.getTopLeft();
    	obb2 = rect.GetTopRight();
    	obb3 = rect.getBottomRight();
    	obb4 = rect.GetBottomLeft();
    }
    
}
