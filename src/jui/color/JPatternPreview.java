/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jui.color;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.TexturePaint;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JComponent;
import jpaint.JPatternPaint;

/**
 *
 * @author takashi
 */
public class JPatternPreview extends JComponent {

    Paint paint = null;
    TexturePaint texture = null;

   public JPatternPreview() {
       this(null);
    }
   public JPatternPreview(Paint p){
       paint=p;
        texture = new TexturePaint(JDPaintPreview.createCImage(), new java.awt.Rectangle(0, 0, 16, 16));
        this.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                resized(e);
            }
        });
       
   }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(texture);
        g2.fillRect(0, 0, getWidth(), getHeight());
        if (paint == null) {
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(3f));
            g2.drawLine(getWidth(), 0, 0, getHeight());
        }else if ( paint instanceof JPatternPaint){
            JPatternPaint jpp=(JPatternPaint)paint;
            double ratX=getWidth()/jpp.getClip().getWidth();
            double ratY=getHeight()/jpp.getClip().getHeight();
            //AffineTransform tx=g2.getTransform();
            //g2.scale(ratX, ratY);
            g2.setPaint(paint);
            g2.fillRect(0,0,getWidth(),getHeight());
            //g2.fill(jpp.getClip());
            //g2.setTransform(tx);
        }else {
            g2.setPaint(paint);
            g2.fill(getBounds());
        }
    }
    public void setPaint(Paint p){
        this.paint=p;
    }
    private void culcDisplay() {
        if (paint instanceof LinearGradientPaint) {
            LinearGradientPaint lg = (LinearGradientPaint) paint;
            float w = Math.max(15f, this.getWidth());
            paint = new LinearGradientPaint(0f, 0f, w, 0f, lg.getFractions(), lg.getColors(), lg.getCycleMethod());
        } else if (paint instanceof RadialGradientPaint) {
            RadialGradientPaint gl = (RadialGradientPaint) paint;
            paint = new RadialGradientPaint(this.getWidth() / 2f, this.getHeight() / 2f,
                    Math.max((float) this.getWidth() / 2f, 1f), gl.getFractions(), gl.getColors(), gl.getCycleMethod());
        }
    }

    private void resized(ComponentEvent e) {
        culcDisplay();
        repaint();
    }
}