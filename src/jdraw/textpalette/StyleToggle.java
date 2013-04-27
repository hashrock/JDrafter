/*
 * StyleToggle.java
 *
 * Created on 2008/06/08, 22:57
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jdraw.textpalette;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

/**
 *
 * @author takashi
 */
public class StyleToggle extends JToggleButton{
    private boolean mouseWithin=false;
    /** Creates a new instance of StyleToggle */
    public StyleToggle() {
        addMouseListener( new InnerMouseAdapter());
        this.setOpaque(false);
    }
    public void paintComponent(Graphics g){
        ImageIcon ic=(ImageIcon)getIcon();
        Graphics2D g2=(Graphics2D)g;
        Composite c=null;
        if (!isEnabled()){
            c=g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));
        }
        if (ic !=null){
            int x=(getWidth()-ic.getIconWidth())/2;
            int y=(getHeight()-ic.getIconHeight())/2;
            ic.paintIcon(this,g,x,y);
        }
        if (c!=null)
            g2.setComposite(c);
        if (!isEnabled()) return;
        //
        RoundRectangle2D r=new RoundRectangle2D.Float(0,0,getWidth()-1,getHeight()-1,4,4);
        if (isSelected())
            paintSelection(g,r);
        else if(mouseWithin)
            paintHover(g,r);
    }
    private void paintSelection(Graphics g,RoundRectangle2D r){
        Graphics2D g2=(Graphics2D)g;
        float w=getWidth();
        float h=getHeight();
        //
        float[] fractions={0f,0.6f,1f};
        Color c1=new Color(0.2f,0.2f,0.2f,0.4f);
        Color c2=new Color(0.6f,0.6f,0.6f,0.1f);
        Color c3=new Color(0.8f,0.8f,0.8f,0.1f);
        //
        Color[] colors=new Color[]{c3,c2,c1};
        float radius=(float)Math.sqrt(w*w+h*h);
        RadialGradientPaint gp1=new RadialGradientPaint(w,h,radius,fractions,colors);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(1f));
        g2.setPaint(gp1);
        g2.fill(r);
        g.setColor(java.awt.Color.GRAY);
        g2.draw(r);
        g2.dispose();
    }
    private void paintHover(Graphics g,RoundRectangle2D r){
        Graphics2D g2=(Graphics2D)g;
        float w=getWidth();
        float h=getHeight();
        //
        float[] fractions={0f,0.6f,1f};
        Color c1=new Color(0.2f,0.2f,0.2f,0.4f);
        Color c2=new Color(0.6f,0.6f,0.6f,0.1f);
        Color c3=new Color(1f,1f,1f,0.1f);
        //
        Color[] colors=new Color[]{c3,c2,c1};
        float radius=(float)Math.sqrt(w*w+h*h);
        RadialGradientPaint gp1=new RadialGradientPaint(0,0,radius,fractions,colors);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(1f));
        g2.setPaint(gp1);
        g2.fill(r);
        g.setColor(java.awt.Color.GRAY);
        g2.draw(r);
        g2.dispose();
    }
    public class InnerMouseAdapter extends MouseAdapter{
        public void mouseEntered(MouseEvent e){
            mouseWithin=true;
            repaint();
        }
        public void mouseExited(MouseEvent e){
            mouseWithin=false;
            repaint();
        }
        public void mouseReleased(MouseEvent e){
            repaint();
        }
    }
}
