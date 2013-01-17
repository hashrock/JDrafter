/*
 * JImageObject.java
 *
 * Created on 2007/11/09, 16:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jobject;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.ImageIcon;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import jedit.JDuplicateObjectEdit;
import jedit.JRotateImageObjectEdit;
import jedit.JTransformImageObjectEdit;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author i002060
 */

public class JImageObject extends JLeaf{
    protected Rectangle2D boundRect=null;
    protected ImageIcon imageIcon=null;
    protected AffineTransform totalTransform=null;
    protected float alpha;
    protected transient AffineTransform addingTransform=null;
    private static final long serialVersionUID=110l;
    /** Creates a new instance of JImageObject */
    public JImageObject(Image img,Point2D sp) {
        imageIcon=new ImageIcon(img);
        boundRect=new Rectangle2D.Double(sp.getX(),sp.getY(),imageIcon.getIconWidth(),imageIcon.getIconHeight());
        totalTransform=new AffineTransform();
        totalTransform.setToIdentity();
        addingTransform=null;
        alpha=1f;
        
    }
    public JImageObject(){
        imageIcon=null;
        boundRect=null;
        totalTransform=new AffineTransform();
        totalTransform.setToIdentity();
        alpha=1f;
        addingTransform=null;
    }
    @Override
    public void transform(AffineTransform tr, JRequest req, Point p) {
        addingTransform=tr;
    }
    @Override
    public void transform(AffineTransform tr){
        addTransform(tr);
    }
    @Override
    public int hitByPoint(JEnvironment env, JRequest req, Point2D point) {
        if (isLocked() || !isVisible()) return JRequest.HIT_NON;
        Shape s=getShape();
        if (s.contains(point)){
            req.hitObjects.add(this);
            return (req.hitResult=JRequest.HIT_OBJECT);
        }
        double radius=JEnvironment.PATH_SELECTOR_SIZE/2/env.getToScreenRatio();
        Rectangle2D.Double sr=new Rectangle2D.Double(0,0,radius*2,radius*2);
        PathIterator path=s.getPathIterator(null);
        double[] coords=new double[6];
        while(!path.isDone()){
            int type=path.currentSegment(coords);
            if (type==path.SEG_LINETO || type==path.SEG_MOVETO){
                sr.x=coords[0]-radius;
                sr.y=coords[1]-radius;
                if (sr.contains(point)){
                    req.hitObjects.add(this);
                    return (req.hitResult=JRequest.HIT_OBJECT);
                }
            }
            path.next();
        }
        return JRequest.HIT_NON;
    }
    
    @Override
    public void hitByRect(JEnvironment env, JRequest req, Rectangle2D rect) {
        if (isLocked() || !isVisible()) return;
        Shape s=getShape();
        if (s.intersects(rect)){
            req.hitObjects.add(this);
        }
        
    }
    @Override
    public UndoableEdit updateTransform(JEnvironment env) {
        JDocumentViewer viewer=getDocument().getViewer();
        JRequest req=viewer.getCurrentRequest();
        CompoundEdit cEdit=new CompoundEdit();
        if (req.isAltDown && req.getSelectionMode()==req.GROUP_MODE && addingTransform.getType()==AffineTransform.TYPE_TRANSLATION){
            cEdit.addEdit(new JDuplicateObjectEdit(viewer,this));
        }
        cEdit.addEdit(new JTransformImageObjectEdit(viewer,this,addingTransform));
        addingTransform =null;
        cEdit.end();
        return cEdit;
    }
    public void addTransform(AffineTransform af){
        totalTransform.preConcatenate(af);
    }
    @Override
    public UndoableEdit updateRotate(JEnvironment env, double rotation) {
        JDocumentViewer viewer=getDocument().getViewer();
        UndoableEdit ret=new JRotateImageObjectEdit(viewer,this,addingTransform,rotation);
        addingTransform=null;
        return ret;
    }
    
    @Override
    public void paintThis(Rectangle2D clip, Graphics2D g) {
        if (!clip.intersects(getBounds()))return;
        Graphics2D gc=(Graphics2D)g.create();
        Image img=imageIcon.getImage();
        gc.setColor(new Color(1f,1f,1f,alpha));
        double sx=boundRect.getWidth()/imageIcon.getIconWidth();
        double sy=boundRect.getHeight()/imageIcon.getIconHeight();
        AffineTransform af=new AffineTransform(totalTransform);
        af.translate(boundRect.getX(),boundRect.getY());
        af.scale(sx,sy);
        gc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,alpha));
        gc.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        gc.drawImage(img,af,imageIcon.getImageObserver());
        gc.dispose();
    }
    
    @Override
    public void paintPreview(JEnvironment env, JRequest req, Graphics2D g) {
        g.setColor(getPreviewColor());
        AffineTransform af=new AffineTransform(totalTransform);
        if (addingTransform !=null)
            af.preConcatenate(addingTransform);
        Shape s=getShape();
        if (addingTransform !=null)
            s=addingTransform.createTransformedShape(s);
        s=env.getToScreenTransform().createTransformedShape(s);
        g.draw(s);
        PathIterator path=s.getPathIterator(null);
        double radius=JEnvironment.PATH_SELECTOR_SIZE/2;
        Rectangle2D.Double sr=new Rectangle2D.Double(0,0,radius*2,radius*2);
        double[] coords=new double[6];
        while (!path.isDone()){
            int type=path.currentSegment(coords);
            if (type==path.SEG_MOVETO || type==path.SEG_LINETO){
                sr.x=coords[0]-radius;
                sr.y=coords[1]-radius;
                g.fill(sr);
            }else if(type==path.SEG_CUBICTO){
                sr.x=coords[4]-radius;
                sr.y=coords[5]-radius;
                g.fill(sr);
            }else if(type==path.SEG_QUADTO){
                sr.x=coords[2]-radius;
                sr.y=coords[3]-radius;
                g.fill(sr);
            }
            path.next();
        }
    }
    
    @Override
    public Rectangle2D getSelectionBounds() {
        return getShape().getBounds2D();
    }
    
    @Override
    public Rectangle2D getBounds() {
        return getSelectionBounds();
    }
    
    @Override
    public Rectangle2D getOriginalSelectionBounds(double x, double y) {
        Shape s=getShape();
        AffineTransform af=new AffineTransform();
        af.setToRotation(-totalRotation,x,y);
        Area a=new Area(af.createTransformedShape(s));
        return a.getBounds2D();
    }
    
    @Override
    public Object clone(){
        Point2D p=new Point2D.Double(boundRect.getX(),boundRect.getY());
        JImageObject ret=new JImageObject(imageIcon.getImage(),p);
        ret.alpha=alpha;
        ret.totalTransform=(AffineTransform)totalTransform.clone();
        ret.totalRotation=totalRotation;
        return ret;
    }
    
    @Override
    public String getPrefixer(){
        return "Image";
    }
    public Image getImage(){
        return imageIcon.getImage();
    }
    public float getAlpha(){
        return alpha;
    }
    public void setAlpha(float alpha){
        this.alpha=alpha;
    }
    public Rectangle2D getBoundRect(){
        return boundRect;
    }
    public Point2D getOriginalPoint(){
        return new Point2D.Double(boundRect.getX(),boundRect.getY());
    }
    @Override
    public Shape getShape() {
        return totalTransform.createTransformedShape(boundRect);
    }
}
