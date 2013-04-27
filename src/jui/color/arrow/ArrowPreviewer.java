/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jui.color.arrow;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;

/**
 *
 * @author takashi
 */
public class ArrowPreviewer extends JComponent {

    public Shape start = null;
    public Shape end = null;
    public float startRatio = 1f;
    public float endRatio = 1f;
    public int direction = 0;
    public float strokeWidth = 4f;
    private static final float eOffset = 8f;

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform tx = new AffineTransform();
        tx.setToTranslation(20, 10);
        tx.scale(6, 6);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(Color.GRAY);
        g2.drawRect(0,0,getWidth()-1,getHeight()-1);
        g2.setColor(Color.BLACK);
        Rectangle2D rct1 = new Rectangle2D.Float();
        Rectangle2D rct2 =new Rectangle2D.Float();
        if (start !=null){
            rct1=start.getBounds2D();
        }
        if (end !=null){
            rct2=end.getBounds2D();
        }
        float ofs1=eOffset-(float)rct1.getX()*strokeWidth*startRatio;
        float ofs2=eOffset-(float)rct2.getX()*strokeWidth*endRatio;
        ArrowRenderer.renderArrow(g2, start, ofs1, getHeight()/2, getWidth()-ofs2, getHeight()/2, strokeWidth,Color.BLACK, startRatio, 1);
        ArrowRenderer.renderArrow(g2, end, ofs1, getHeight()/2, getWidth()-ofs2, getHeight()/2, strokeWidth,Color.BLACK, endRatio, 2);

    }
}
