/*
 * BevelRect.java
 *
 * Created on 2007/09/26, 15:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jgeom;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

/**
 *
 * @author i002060
 */
public class BevelRect extends RectangularShape{
    public double x,y,width,height,arcwidth,archeight;
    /** Creates a new instance of BevelRect */
    public BevelRect() {
        x=y=width=height=arcwidth=archeight=0;
    }
    public BevelRect(double x,double y,double width,double height,double arcwidth,double archeight){
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
        this.arcwidth=arcwidth;
        this.archeight=archeight;
    }
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public boolean isEmpty() {
        if (width==0 || height==0) return true;
        return false;
    }

    public void setFrame(double x, double y, double w, double h) {
        this.x=x;
        this.y=y;
        this.width=w;
        this.height=h;
    }

    public Rectangle2D getBounds2D() {
       return new Rectangle2D.Double(x,y,width,height);
    }

    public boolean contains(double x, double y) {
        return createGeneralPath().contains(x,y);
    }

    public boolean intersects(double x, double y, double w, double h) {
        return createGeneralPath().intersects(x,y,w,h);
    }

    public boolean contains(double x, double y, double w, double h) {
        return createGeneralPath().contains(x,y,w,h);
    }

    public PathIterator getPathIterator(AffineTransform at) {
        return createGeneralPath().getPathIterator(at);
    }
    private GeneralPath createGeneralPath(){
        GeneralPath ret=new GeneralPath();
        double aw=archeight;
        double ah=arcwidth;
        if (aw>width/2) aw=width/2;
        if (ah>height/2) ah=height/2;
        ret.moveTo(x+aw,y);
        ret.lineTo(x+width-aw,y);
        ret.lineTo(x+width,y+ah);
        ret.lineTo(x+width,y+height-ah);
        ret.lineTo(x+width-aw,y+height);
        ret.lineTo(x+aw,y+height);
        ret.lineTo(x,y+height-ah);
        ret.lineTo(x,y+ah);
        ret.lineTo(x+aw,y);
        ret.closePath();
        return ret;
    }
}
