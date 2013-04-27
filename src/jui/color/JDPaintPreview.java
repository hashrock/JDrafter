/*
 * JDPaintPreview.java
 *
 * Created on 2007/02/15, 18:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jui.color;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import javax.swing.*;
/**
 *
 * @author TK
 */
public class JDPaintPreview extends JComponent{
    public static BufferedImage cImage=null;
    public static TexturePaint cTexture=null;
    private Color color;
    /** Creates a new instance of JDPaintPreview */
    public static BufferedImage  createCImage(){
        if (cImage!=null) return cImage;
        cImage=new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g=(Graphics2D)cImage.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0,0,16,16);
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0,0,8,8);
        g.fillRect(8,8,8,8);
        g.dispose();
        cTexture=new TexturePaint(cImage,new Rectangle(0,0,16,16));
        return cImage;
    }
    public JDPaintPreview() {
        color=null;
        createCImage();
        
    }
    public void setColor(Color c){
        this.color=c;
        repaint();
    }
    public Color getColor(){
        return color;
    }
    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2=(Graphics2D)g.create();
        g2.setPaint(cTexture);
        g2.fillRect(0,0,getWidth(),getHeight());
        if (color !=null){
            g2.setColor(color);
            g2.fillRect(0,0,getWidth(),getHeight());
        }else{
            g2.setColor(Color.WHITE);
            g2.fillRect(0,0,getWidth(),getHeight());
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(3f));
            g2.drawLine(getWidth(),0,0,getHeight());
        }
    }
    
}
