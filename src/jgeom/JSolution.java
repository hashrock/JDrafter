/*
 * JSolution.java
 *
 * Created on 2007/09/24, 8:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jgeom;

import java.awt.geom.Point2D;

/**
 *
 * @author TI
 */
public class JSolution {
    public Point2D p;
    public double t;
    /**
     * Creates a new instance of JDSolution
     */
        
    public JSolution(double x,double y,double t) {
        p=new Point2D.Double(x,y);
        this.t=t;
    }
    public JSolution(Point2D p,double t){
        this(p.getX(),p.getY(),t);
    }
}