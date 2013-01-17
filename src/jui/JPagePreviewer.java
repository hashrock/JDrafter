/*
 * JPagePreviewer.java
 *
 * Created on 2007/12/07, 15:42
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.VolatileImage;
import jobject.JDocument;
import jobject.JPage;
import jpaint.JBlurPaint;

/**
 *
 * @author i002060
 */
public class JPagePreviewer {
    private JPage page =null;
    public boolean isDraftMode=false;
    private int width=110,height=110;
    private int x=0,y=0;
    private VolatileImage bufferedImage=null;
    private JPageSelecter selecter;
    /** Creates a new instance of JPagePreviewer */
    public JPagePreviewer(JPage page,JPageSelecter selecter) {
        setPage(page);
        this.selecter=selecter;
    }
    public int getWidth(){
        return width;
    }
    public void setWidth(int w){
        this.width=w;
    }
    public int getHeight(){
        return height;
    }
    public void setHeight(int h){
        this.height=h;
    }
    public int getX(){
        return x;
    }
    public void setX(int x){
        this.x=x;
    }
    public int getY(){
        return y;
    }
    public void setY(int y){
        this.y=y;
    }
    public Rectangle getBounds(){
        return new Rectangle(x,y,width,height);
    }
    private void setPage(JPage pg){
        if (page==pg) return;
        JDocument doc=pg.getDocument();
        page=pg;
    }
    public JPage getPage(){
        return page;
    }
    public Rectangle getPageRect(){
        Rectangle ret=new Rectangle();
        int w=this.getWidth()-8;
        int h=this.getHeight()-8;
        if (page==null || w<=0 || h<=0) return ret;
        Rectangle2D paper=page.getEnvironment().getPaperRect();
        double rat=Math.min(w/paper.getWidth(),h/paper.getHeight());
        double wt=paper.getWidth()*rat;
        double ht=paper.getHeight()*rat;
        double dx=(getWidth()-wt)/2;
        double dy=(getHeight()-ht)/2;
        ret.setFrame(getX()+dx,getY()+dy,wt,ht);
        return ret;
    }
    public AffineTransform getTransform(){
        int w=this.getWidth()-8;
        int h=this.getHeight()-20;
        if (page==null || w<=0 || h<=0) return null;
        Rectangle2D paper=page.getEnvironment().getPaperRect();
        double rat=Math.min(w/paper.getWidth(),h/paper.getHeight());
        double dx=(getWidth()-paper.getWidth()*rat)/2;
        double dy=(getHeight()-paper.getHeight()*rat)/2-6;
        AffineTransform tx=new AffineTransform();
        tx.setToTranslation(dx,dy);
        tx.scale(rat,rat);   
        return tx;        
    }
    public VolatileImage getImage(){
        return bufferedImage;
    }
    private void paintBuffer(Graphics2D g2){
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        AffineTransform tx=getTransform();
        if (tx==null) return;
        g2.transform(tx);
        Rectangle2D paper=page.getEnvironment().getPaperRect();
        //g2.setColor(Color.WHITE);
        Rectangle2D.Float shadow=new Rectangle2D.Float();
        shadow.setFrame(paper);
        shadow.x+=16;
        shadow.y+=16;
        g2.setPaint(new JBlurPaint(shadow,30,Color.BLACK,1f));
        g2.fill(shadow);
        g2.setColor(Color.WHITE);
        g2.fill(paper);
        double rat=tx.getScaleX();
        g2.setStroke(new BasicStroke((float)(0.8/rat)));
        g2.setColor(Color.BLACK);
        g2.draw(paper);    
        g2.setClip(paper);
        boolean vb=page.getGuidLayer().isVisible();
        page.getGuidLayer().setVisible(false);
        page.paint(paper,g2);
        page.getGuidLayer().setVisible(vb);
        
    }
    public void paint(Graphics2D g){
        if (bufferedImage ==null || bufferedImage.getWidth()<getWidth() || bufferedImage.getHeight()<getHeight() || bufferedImage.contentsLost()){
            bufferedImage =selecter.createVolatileImage(Math.max(100,getWidth()),Math.max(100,getHeight()));
            isDraftMode=false;
        }
        Graphics2D gc=bufferedImage.createGraphics();
        if (!isDraftMode){ 
            gc.setColor(selecter.getBackground());
            gc.fillRect(0,0,getWidth(),getHeight());
            paintBuffer(gc);
            isDraftMode=true;
        }
        g.drawImage(bufferedImage,0,0,selecter);
        g.setColor(Color.BLACK);
        String pg=String.valueOf(page.getParent().indexOf(page)+1);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawString(pg,getWidth()/2,getHeight()-2);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
        if (page.getDocument().getCurrentPage()==page){
            g.setColor(Color.ORANGE);
            g.drawRect(0,0,getWidth(),getHeight());

            Rectangle2D paper=page.getEnvironment().getPaperRect();
            Rectangle2D r=page.getEnvironment().getToAbsoluteTransform().createTransformedShape(page.getDocument().getViewer().getVisibleRect()).getBounds2D();
            r=r.createIntersection(paper);
            g.setStroke(new BasicStroke(2f));
            g.setColor(new Color(255,0,0,128));
            Shape s=getTransform().createTransformedShape(r);
            g.draw(s);       
        }else {
            g.setColor(Color.LIGHT_GRAY);
            g.setStroke(new BasicStroke(1f));
            g.drawRect(0,0,getWidth(),getHeight());
        }
        
    }
    
}