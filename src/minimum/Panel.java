/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package minimum;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;


import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


import uk.co.geolib.geolib.C2DCircle;
import uk.co.geolib.geolib.C2DLine;
import uk.co.geolib.geolib.C2DLineBaseSet;
import uk.co.geolib.geolib.C2DPoint;
import uk.co.geolib.geolib.C2DPointSet;
import uk.co.geolib.geolib.C2DRect;
import uk.co.geolib.geolib.C2DSegment;

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
	private int size = 30;
    private C2DPolygon polygon;
    private GeoDraw drawer;
    private ArrayList<C2DPoint> pointList;
    private C2DCircle minimumCircle;
    private C2DRect minimumBoundingBox;
    
    public Panel(int width, int height){
        
    	/*
    	JCheckBox chCheckBox = new JCheckBox("Convex Hull");
    	JCheckBox filledCheckBox = new JCheckBox("Non Delaunay Filled");
    	JCheckBox circlesCheckBox = new JCheckBox("Delaunay Circle");
    	JTextField sizeTextField = new JTextField("100",10);
    	String message = "Chose!";  
    	Object[] params = {message, sizeTextField, chCheckBox, filledCheckBox, circlesCheckBox};  
    	int n = JOptionPane.showConfirmDialog(null, params, "Setting", JOptionPane.YES_NO_OPTION);  
    	convexHullFlag = chCheckBox.isSelected();
    	filledFlag = filledCheckBox.isSelected();
    	circleFlag = circlesCheckBox.isSelected();
    	size = Integer.parseInt(sizeTextField.getText());
    	
    	*/
        //init
        polygon = new C2DPolygon();
        drawer = new GeoDraw();
        
        //set rectangle
        C2DRect rect = new C2DRect(150,150,width-150,height-150);
        
        //generate random points
        polygon.CreateRandom(rect, size, size);
        //poly.CreateRegular(new C2DPoint(width/2, width/2), width/2-30, size);
        
        //copy point to point set
        pointList = new ArrayList<C2DPoint>();
        polygon.GetPointsCopy(pointList);
        
        //minimum circle
        minimumCircle = minimumCircle(pointList);
        
        //minimum axis bounding box
        minimumBoundingBox = axisBoundingBox(pointList);
        
        
        
    }
    
    public void paint(Graphics g){

        //draw points
        g.setColor(Color.red);
        for(int i = 0;i<pointList.size();i++){
        	g.fillRect((int)pointList.get(i).x, 
        			(int)pointList.get(i).y, 3, 3);
        }
        
        
        //minimum circle
        g.setColor(Color.blue);
        //drawer.Draw(minimumCircle, g);
        
        //minimum axis bounding box
        g.setColor(Color.green);
        drawer.Draw(minimumBoundingBox, g);
        
      
    }
    
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
    
    private boolean onRight(C2DPoint p, C2DPoint q){
    	if(p.x<=q.x){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    private boolean onUp(C2DPoint p, C2DPoint q){
    	if(p.y<=q.y){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    private void minimumOrientedBoundingBox(C2DPolygon polygon){
    	//variables
    	C2DPolygon convexHull = new C2DPolygon();
    	C2DLineBaseSet convexLines;
    	C2DPointSet convexPointSet = new C2DPointSet();
    	C2DLine l, minConvexLine;
    	C2DPoint maxPoin;
    	double distance = 0, minDistance = Double.MAX_VALUE;
    	double maxDistance = Double.MIN_VALUE;
    	
    	//convex hull
    	convexHull.CreateConvexHull(polygon);
    	convexHull.GetPointsCopy(convexPointSet);
    	convexLines = convexHull.getLines();

    	//min distance from convex line and max point
    	double tempDistance;
    	for(int i = 0;i<convexLines.size();i++){
    		l = (C2DLine) convexLines.get(i);
    		if(i == 0){
    			minDistance = distance;
    			minConvexLine = l;
    			continue;
    		}
    		maxDistance = Double.MIN_VALUE;
    		for(int j = 0;j<convexPointSet.size();j++){//sos
    			tempDistance = l.Distance(convexPointSet.get(j));
    			distance += tempDistance;
    			if(tempDistance>maxDistance){
    				maxDistance = tempDistance;
    				maxPoin = convexPointSet.get(j);
    			}
    		}
    		if(distance<minDistance){
    			minDistance = distance;
    			minConvexLine = l;
    		}
    	}
    	

    }
}
