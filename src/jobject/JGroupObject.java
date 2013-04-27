/*
 * JGroupObject.java
 *
 * Created on 2007/09/16, 9:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jobject;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import jedit.JDuplicateObjectEdit;
import jpaint.JPaint;
import jpaint.JStroke;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author TI
 */
public class JGroupObject extends JObject<JLayer,JLeaf> implements JColorable{
    private AffineTransform transform;
    private static final long serialVersionUID=110l;
    /** Creates a new instance of JGroupObject */
    public JGroupObject() {
        transform=null;
    }
    
    @Override
    public void transform(AffineTransform tr, JRequest req,Point p) {
        for (int i=0;i<size();i++){
            if (get(i).isLocked() || !get(i).isVisible()) continue;
            get(i).transform(tr,req,p);
        }
    }
    @Override
    public void transform(AffineTransform tr){
        for(int i=0;i<size();i++){
            get(i).transform(tr);
        }
    }
    @Override
    public int hitByPoint(JEnvironment env, JRequest req, Point2D point) {
        if (isLocked() || !isVisible()) return req.HIT_NON;
        int ret=req.HIT_NON;
        for (int i=size()-1;i>=0;i--){
            if (get(i).isLocked() || !get(i).isVisible()) continue;
            ret=get(i).hitByPoint(env,req,point);
            if (ret !=req.HIT_NON) break;
        }
        if (ret!=req.HIT_NON && req.getSelectionMode()==req.GROUP_MODE){
            req.hitObjects.clear();
            req.hitObjects.add(this);
        }
        return ret;
    }
    
    @Override
    public void hitByRect(JEnvironment env, JRequest req, Rectangle2D rect) {
        if (isLocked() || !isVisible()) return;
        for(int i=0;i<size();i++){
            if (get(i).isLocked() || !get(i).isVisible()) continue;
            get(i).hitByRect(env,req,rect);
        }
        boolean hitMe=false;
        if (req.getSelectionMode()==req.GROUP_MODE){
            for (int i=0;i<req.hitObjects.size();i++){                
                Object o=req.hitObjects.get(i);
                if (o instanceof JLeaf){
                    JLeaf jl=(JLeaf)o;
                    if (this.contains(jl)){
                        req.hitObjects.remove(i--);
                        hitMe=true;
                    }
                }else{
                    req.hitObjects.remove(i--);
                }
            }
        }
        if (hitMe){
            req.hitObjects.add(this);
        }
    }
    
    @Override
    public UndoableEdit updateTransform(JEnvironment env) {
        JDocumentViewer viewer=getDocument().getViewer();
        JRequest req=viewer.getCurrentRequest();
        boolean savedAlt=req.isAltDown;
        req.isAltDown=false;
        CompoundEdit cEdit=null;
        if (savedAlt){
            cEdit=new CompoundEdit();
            cEdit.addEdit(new JDuplicateObjectEdit(viewer,this));
        }
        for (int i=0;i<size();i++){
            if (get(i).isLocked() || !get(i).isVisible()) continue;
            if (cEdit==null) cEdit=new CompoundEdit();
            cEdit.addEdit(get(i).updateTransform(env));
        }
        if (cEdit !=null){
            cEdit.end();
        }
        req.isAltDown=savedAlt;
        return cEdit;
    }
    
    @Override
    public UndoableEdit updateRotate(JEnvironment env, double rotation) {
        JDocumentViewer viewer=getDocument().getViewer();
        JRequest req=viewer.getCurrentRequest();
        boolean savedAlt=req.isAltDown;
        req.isAltDown=false;
        CompoundEdit cEdit=null;
        if (savedAlt){
            cEdit=new CompoundEdit();
            cEdit.addEdit(new JDuplicateObjectEdit(viewer,this));
        }
        
        for (int i=0;i<size();i++){
            if (get(i).isLocked() || !get(i).isVisible()) continue;
            if (cEdit==null) cEdit=new CompoundEdit();
            cEdit.addEdit(get(i).updateRotate(env,rotation));
        }
        if (cEdit !=null){
            cEdit.end();
        }
        req.isAltDown=savedAlt;
        return cEdit;
    }
    
    @Override
    public void paintThis(Rectangle2D clip, Graphics2D g) {
        
        for (int i=0;i<size();i++){
            get(i).paint(clip,g);
        }
    }
    
    @Override
    public void paintPreview(JEnvironment env, JRequest req, Graphics2D g) {
        Vector savedObject;
        savedObject=(Vector)req.getSelectedVector().clone();
        req.getSelectedVector().clear();
        for (int i=0;i<size();i++){
           if(get(i).isLocked() || !get(i).isVisible()) continue;
            req.getSelectedVector().add(get(i));
            get(i).paintPreview(env,req,g);
            req.getSelectedVector().remove(get(i));
        }
        req.setSelectedVector(savedObject);
    }
    
    @Override
    public Rectangle2D getSelectionBounds() {
        
        Rectangle2D ret=null;
        for (int i=0;i<size();i++){
            if (get(i).isLocked() || !get(i).isVisible()) continue;
            if (ret==null)
                ret=get(i).getSelectionBounds();
            else{
                Rectangle2D r=get(i).getSelectionBounds();
                if (r!=null)
                    ret.add(r);
            }
            
        }
        return ret;
    }
    @Override
    public double getTotalRotation(){
        double ret=-9999;
        for (int i=0;i<size();i++){
            if (get(i).isLocked() || !get(i).isVisible()) continue;
            if (ret==-9999){
                ret=get(i).getTotalRotation();
            }else{
                if (ret != get(i).getTotalRotation()){
                    ret=0;
                    break;
                }
            }
        }
        if (ret==-9999) ret=0;
        return ret;
    }
    @Override
    public Rectangle2D getOriginalSelectionBounds(double x, double y) {
        double tr=getTotalRotation();
        if (tr==0) return getSelectionBounds();
        Rectangle2D ret=null;
        for (int i=0;i<size();i++){
            if (get(i).isLocked() || !get(i).isVisible()) continue;
            if (ret==null)
                ret=get(i).getOriginalSelectionBounds(x,y);
            else
                ret.add(get(i).getOriginalSelectionBounds(x,y));
            
        }
        return ret;
    }
    
    @Override
    public JGroupObject clone() throws CloneNotSupportedException {
        JGroupObject ret=new JGroupObject();
        for (int i=0;i<size();i++){
            ret.add((JLeaf)get(i).clone());
        }
        return ret;
    }
    
    @Override
    public Rectangle2D getBounds() {
        Rectangle2D ret=null;
        for (int i=0;i<size();i++){
            //if (get(i).isLocked() || !get(i).isVisible()) continue;
            if (get(i).getBounds()==null) continue;
            if (ret==null) ret=get(i).getBounds();
            else ret.add(get(i).getBounds());
        }
        return ret;
    }
    
    @Override
    public String getPrefixer(){
        return "Group";
    }
    @Override
    public void setFillPaint(JPaint p){
        for (int i=0;i< size();i++){
            Object o=get(i);
            if (o instanceof JColorable){
                get(i).setFillPaint(p);
            }
        }
    }
    @Override
    public void setStrokePaint(JPaint p){
        for (int i=0;i< size();i++){
            Object o=get(i);
            if (o instanceof JColorable){
                get(i).setStrokePaint(p);
            }
        }
    }
    @Override
    public void setStroke(JStroke s){
        for (int i=0;i< size();i++){
            Object o=get(i);
            if (o instanceof JColorable){
                get(i).setStroke(s);
            }
        }
    }    
    @Override
    public Shape getShape() {
        return null;
    }
    @Override
    public boolean canBeGuide(){
        for (int i=0;i<size();i++){
            if (!get(i).canBeGuide())
                return false;
        }
        return true;
    }
}
