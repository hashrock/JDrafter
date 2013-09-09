/*
 * JLayer.java
 *
 * Created on 2007/08/27, 16:33
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jobject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.undo.UndoableEdit;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 * Document上の描画階層
 * @author i002060
 */
public class JLayer extends JObject<JPage,JLeaf> {
    private static final long serialVersionUID=110l;
    private Color previewColor=JEnvironment.PREVIEW_COLOR;
    /** Creates a new instance of JLayer */
    public JLayer() {
    }
    
    @Override
    public void transform(AffineTransform tr,JRequest req,Point p) {
        //Do Nothing;
    }
    @Override
    public void transform(AffineTransform tr){
       // Do nothing;
    }
    
    @Override
    public int hitByPoint(JEnvironment env, JRequest req, Point2D point) {
        //Send to Children;
        if (isLocked() || !isVisible()) return JRequest.HIT_NON;
        int ret=JRequest.HIT_NON;
        if (! isEnabled() || !isVisible()) return ret;
        for (int i=size()-1;i>=0;i--){
            if((ret=get(i).hitByPoint(env,req,point))!=JRequest.HIT_NON)
                break;
        }
        return ret;
    }
    
    @Override
    public void hitByRect(JEnvironment env, JRequest req, Rectangle2D rect) {
        //Sent to Children
        if (isLocked() || !isVisible()) return;
        for(int i=0;i<size();i++){
            get(i).hitByRect(env,req,rect);
        }
    }
    
    @Override
    public UndoableEdit updateTransform(JEnvironment env) {
        //Do nothing;
        return null;
    }
    
    @Override
    public UndoableEdit updateRotate(JEnvironment env,double rotation){
        return null;
    }
    
    @Override
    public void paintThis(Rectangle2D clip, Graphics2D g) {
        //Do nothing;
    }
    
    @Override
    public void paintPreview(JEnvironment env, JRequest req, Graphics2D g) {
        //Do Nothing
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        JLayer ret=new JLayer();
        for (int i=0;i<size();i++){
            ret.add((JLeaf)get(i).clone());
        }
        return ret;
    }
    
    @Override
    public Rectangle2D getSelectionBounds() {
        return null;
    }
    
    @Override
    public Rectangle2D getOriginalSelectionBounds(double x,double y) {
        return null;
    }
    
    @Override
    public Rectangle2D getBounds() {
        Rectangle2D ret=null;
        for (int i=0;i<size();i++){
            if (get(i).getBounds()==null) continue;
            if (ret==null) ret=get(i).getBounds();
            else ret.add(get(i).getBounds());
        }
        return ret;
    }
    
    @Override
    public Shape getShape() {
        return null;
    }
    @Override
    public JLayer getLayer(){
        return this;
    }
    @Override
    public String getPrefixer(){
        return "Layer";
    }
    @Override
    public Color getPreviewColor(){
        return previewColor;
    }
    public void setPreviewColor(Color c){
        previewColor=c;
    }
    
}
