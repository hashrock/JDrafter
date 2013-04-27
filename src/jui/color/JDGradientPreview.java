/*
 * JDGradientPreview.java
 *
 * Created on 2007/02/19, 8:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jui.color;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.*;

/**
 *
 * @author i002060
 */
public class JDGradientPreview extends JComponent{
    MultipleGradientPaint grad;
    MultipleGradientPaint gradForDisplay;
    Paint cTexture;
    /** Creates a new instance of JDGradientPreview */
    public JDGradientPreview() {
         float w=Math.max(15f,this.getWidth());
        grad=new LinearGradientPaint(0f,0f,w,0f,new float[]{0f,1f},new Color[]{Color.WHITE,Color.BLACK},LinearGradientPaint.CycleMethod.NO_CYCLE);
        culcDisplay();
        if (JDPaintPreview.cTexture==null){
            JDPaintPreview.createCImage();            
        }
        cTexture=new TexturePaint(JDPaintPreview.cImage,new Rectangle(0,0,16,16));
        this.addComponentListener(new ComponentAdapter(){
            public void componentResized(ComponentEvent e){
                resized(e);
            }
        });
        
    }
    public void setPaint(MultipleGradientPaint g){
        this.grad=g;
        culcDisplay();
        repaint();
    }
    public MultipleGradientPaint getPaint(){
        return grad;
    }
    private void culcDisplay(){
        if(grad instanceof LinearGradientPaint){
            float w=Math.max(15f,this.getWidth());
            gradForDisplay=new LinearGradientPaint(0f,0f,w,0f,grad.getFractions(),grad.getColors(),grad.getCycleMethod());
        }else{
            gradForDisplay=new RadialGradientPaint(this.getWidth()/2f,this.getHeight()/2f,
                    Math.max((float)this.getWidth()/2f,1f),grad.getFractions(),grad.getColors(),grad.getCycleMethod());
        }
    }
    private void resized(ComponentEvent e){
        culcDisplay();
        repaint();
    }
    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2=(Graphics2D)g;
        g2.setPaint(cTexture);
        g2.fillRect(0,0,getWidth(),getHeight());
        g2.setPaint(gradForDisplay);
        g2.fillRect(0,0,getWidth(),getHeight());
    }
    
}
