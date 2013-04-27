/*
 * JDStrokePreview.java
 *
 * Created on 2007/02/20, 9:57
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jui;
import java.awt.*;
import java.awt.geom.GeneralPath;
import javax.swing.*;
/**
 *
 * @author i002060
 */
public class JDStrokePreview extends JComponent{
    Stroke stroke;
    /** Creates a new instance of JDStrokePreview */
    public JDStrokePreview() {
        stroke=new BasicStroke(1);
    }
    public void setStroke(Stroke s){
        this.stroke=s;
        repaint();
    }
    public Stroke getStroke(){
        return stroke;
    }
    public void paintComponent(Graphics g){
        GeneralPath gPath=new GeneralPath();
        gPath.moveTo(25,getHeight());
        gPath.lineTo(25,getHeight()/2);
        gPath.lineTo(getWidth()-10,getHeight()/2);    
        Graphics2D g2=(Graphics2D)g.create();
        g2.setColor(Color.WHITE);
        g2.fillRect(0,0,getWidth(),getHeight());
        g2.setStroke(stroke);
        g2.setColor(Color.BLACK);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2.draw(gPath);     
    }
}
