/*
 * JPageFormat.java
 *
 * Created on 2007/08/20, 20:16
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jprinter;

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author TI
 */
public class JPageFormat extends PageFormat implements Serializable,Cloneable {
    double width,height,imageableX,imageableY,imageableWidth,imageableHeight;
    int orientation;
    /** Creates a new instance of JPageFormat */
    public JPageFormat() {
        super();
        Paper p=super.getPaper();
        width=p.getWidth();
        height=p.getHeight();
        imageableX=p.getImageableX();
        imageableY=p.getImageableY();
        imageableWidth=p.getImageableWidth();
        imageableHeight=p.getImageableHeight();
        orientation=super.getOrientation();
    }
    public JPageFormat(PageFormat p){
        super();
        setPaper(p.getPaper());
        setOrientation(p.getOrientation());
    }
    @Override
    public void setPaper(Paper p){
        super.setPaper(p);
        width=p.getWidth();
        height=p.getHeight();
        imageableX=p.getImageableX();
        imageableY=p.getImageableY();
        imageableWidth=p.getImageableWidth();
        imageableHeight=p.getImageableHeight();       
    }
    @Override
    public void setOrientation(int p){
        super.setOrientation(p);
        orientation=p;
    }
    @Override
    public JPageFormat clone() {
        JPageFormat ret=new JPageFormat();
        ret.setOrientation(orientation);
        ret.setPaper(getPaper());
        return ret;
    }
     private void readObject(java.io.ObjectInputStream in)
     throws IOException, ClassNotFoundException
     {
         in.defaultReadObject();
         Paper p=new Paper();
         p.setImageableArea(imageableX,imageableY,imageableWidth,imageableHeight);
         p.setSize(width,height);
         super.setPaper(p);
         super.setOrientation(orientation);
     }
}
