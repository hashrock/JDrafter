/*
 * JSegment.java
 *
 * Created on 2007/08/25, 8:33
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jgeom;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import jscreen.JEnvironment;

/**パスを構成する一つのセグメントを表します.
 * セグメントは、アンカーポイント、コントロールポイント１及びコントロールポイント
 *2から構成されます.
 *           ○---------□---------○
 *        control1     ancur      control2
 *               ------->path direction
 * @author TI
 */
public class JSegment implements Serializable,Cloneable{
    /**Indicate AncurPoint*/
    public static final int ANCUR=1;
    /**Indicate ControlPoint1*/
    public static final int CONTROL1=2;
    /**Indicate ControlPoint2*/
    public static final int CONTROL2=3;
    /**precision of join*/
    public static final double JOIN_PRECISION=0.0001d;
    
    private Point2D.Float ancur;
    private Point2D.Float control1;
    private Point2D.Float control2;
    private boolean joined;
    /** Creates a new instance of JSegment */
    public JSegment() {
        ancur=new Point2D.Float();
        control1= null;
        control2= null;
        joined=false;
    }
    public JSegment(Point2D a,Point2D c1,Point2D c2,boolean join ){
        ancur=new Point2D.Float();
        ancur.setLocation(a);
        control1=control2=null;
        if (c1!=null)
            control1=new Point2D.Float((float)c1.getX(),(float)c1.getY());
        if (c2 !=null)
            control2=new Point2D.Float((float)c2.getX(),(float)c2.getY());
        joined=false;
        if (join){
            joined=canJoin();
        }
    }
    public Point2D getAncur(){
        Point2D.Float ret=new Point2D.Float();
        ret.setLocation(ancur);
        return ret;
    }
    public Point2D getControl1(){
        if (control1==null)
            return null;
        Point2D ret=new Point2D.Float();
        ret.setLocation(control1);
        return ret;
    }
    public Point2D getControl2(){
        if (control2==null)
            return null;
        Point2D ret=new Point2D.Float();
        ret.setLocation(control2);
        return ret;
    }
    public Rectangle2D getSegmentRect(JEnvironment env){
        double minX=ancur.getX();
        double maxX=minX;
        double minY=ancur.getY();
        double maxY=minY;
        if (control1 !=null){
            minX=Math.min(minX,control1.getX());
            maxX=Math.max(maxX,control1.getX());
            minY=Math.min(minY,control1.getY());
            maxY=Math.max(maxY,control1.getY());
        }
        if (control2 !=null){
            minX=Math.min(minX,control2.getX());
            maxX=Math.max(maxX,control2.getX());
            minY=Math.min(minY,control2.getY());
            maxY=Math.max(maxY,control2.getY());
        }
        double radius=env.PATH_SELECTOR_SIZE/env.getToScreenRatio()/2;
        Rectangle2D ret=new Rectangle2D.Float();
        ret.setFrameFromDiagonal(minX-radius,minY-radius,maxX+radius,maxY+radius);
        return ret;
    }
    public void setAncur(Point2D p){
        ancur.x=(float)p.getX();
        ancur.y=(float)p.getY();
    }
    public void setAncur(double x,double y){
        ancur.x=(float)x;
        ancur.y=(float)y;
    }
    public void setControl1(Point2D p){
        if (p==null){
            control1=null;
            return;
        }
        if (control1==null){
            control1=new Point2D.Float();
        }
        control1.setLocation(p);
        setJoined(joined);
    }
    public void setControl1(double x,double y){
        if (control1==null){
            control1=new Point2D.Float();
        }
        control1.setLocation(x,y);
        setJoined(joined);
    }
    public void setControl2(Point2D p){
        if (p==null){
            control2=null;
            return;
        }
        if (control2==null)
            control2=new Point2D.Float();
        control2.setLocation(p);
        setJoined(joined);
    }
    public void setControl2(double x,double y){
        if (control2==null){
            control2=new Point2D.Float();
        }
        control2.setLocation(x,y);
        setJoined(joined);
    }
    public void transform(AffineTransform tr){
        tr.transform(ancur,ancur);
        if (control1 !=null)
            tr.transform(control1,control1);
        if (control2 !=null)
            tr.transform(control2,control2);
    }
    public void transformControl(AffineTransform tr,int place){
        if(tr.getType()!=AffineTransform.TYPE_TRANSLATION)
            throw new IllegalArgumentException("Only for Translation");
        Point2D.Float p1,p2,np;
        if (place ==CONTROL1){
            p1=control1;
            p2=control2;
        }else if (place==CONTROL2){
            p1=control2;
            p2=control1;
        }else {
            throw new IllegalArgumentException("Illeagl Place type");
        }
        if (p1==null) return;
        np=new Point2D.Float();
        tr.transform(p1,np);
        double theta=Math.atan2(p1.y-ancur.y,p1.x-ancur.x)-Math.atan2(np.y-ancur.y,np.x-ancur.x);
        p1.setLocation(np);
        if (p2!=null && joined){
            AffineTransform it=new AffineTransform();
            it.setToRotation(-theta,ancur.x,ancur.y);
            it.transform(p2,p2);
        }
    }
    public boolean isJoined(){
        return  joined && canJoin();
    }
    public void setJoined(boolean j){
        joined= j;
    }
    public JSegment clone(){
        JSegment cl=new JSegment();
        cl.setAncur(ancur);
        cl.setControl1(control1);
        cl.setControl2(control2);
        cl.setJoined(joined);
        return cl;
    }
    /*
    public boolean equals(Object o){
        if (!(o instanceof JSegment)) return false;
        JSegment js=(JSegment)o;
        if (!ancur.equals(js.ancur)) return false;
        if (control1 != null){
            if (js.control1 == null) return false;
            if (!control1.equals(js.control1)) return false;
        }else{
            if (js.control1 != null) return false;
        }
        if ( control2 !=null){
            if (js.control2==null) return false;
            if(!control2.equals(js.control2)) return false;
        } else{
            if (js.control2!=null) return false;
        }
        return (joined == js.joined);
    }
     */
    public boolean canJoin(){
        if (control1 == null || control2==null) return true;
        double theta1=Math.atan2(control1.y-ancur.y,control1.x-ancur.x);
        double theta2=Math.atan2(ancur.y-control2.y,ancur.x-control2.x);
        return (Math.abs(theta1-theta2)<JOIN_PRECISION);
    }
}
