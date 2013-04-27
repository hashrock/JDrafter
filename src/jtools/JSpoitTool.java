/*
 * JSpoitTool.java
 *
 * Created on 2007/12/03, 11:58
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jtools;

import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Vector;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import jobject.JColorable;
import jobject.JLeaf;
import jobject.JObject;
import jpaint.JPaint;
import jpaint.JStroke;
import jui.color.JColorChanger;
import jscreen.JDragPane;
import jscreen.JEnvironment;
import jscreen.JRequest;
import jui.JStrokeChanger;
/**
 *
 * @author i002060
 */
public class JSpoitTool extends JAbstractTool{
    JLeaf hoveringObject=null;
    JColorChanger changer=null;
    JStrokeChanger strokeChanger=null;
    /**
     * Creates a new instance of JSpoitTool
     */
    public JSpoitTool(JDragPane dragPane) {
        super(dragPane);
        presentationName="スポイトツール";
        
    }
    public void changeCursor() {
        JCursor jc=dragPane.getJCursor();
        if (hoveringObject != null){
            setCursor(jc.SPOIT_FILLED);
        }else{
            setCursor(jc.SPOIT);
        }
    }
    public void setColorChanger(JColorChanger c){
        this.changer=c;
    }
    public void setStrokeChanger(JStrokeChanger c){
        this.strokeChanger=c;
    }
    public void wakeup() {
        JRequest req=getRequest();
        req.setSelectionMode(req.GROUP_MODE);
        dragPane.setPaintRect(false);
    }
    public void mousePressed(MouseEvent e){
        hoveringObject=hit(e.getPoint());
        setChanger();
        
    }
    public void mouseDragged(MouseEvent e){
        hoveringObject=hit(e.getPoint());
        setChanger();
    }
    public void mouseReleased(MouseEvent e){
        if (hoveringObject!=null){
            JPaint fill=hoveringObject.getFillPaint();
            JPaint strokePaint=hoveringObject.getStrokePaint();
            JStroke stroke=hoveringObject.getStroke();
            if (fill !=null && fill.getPaintMode()!=JPaint.COLOR_MODE){
                fill=fill.clone();
            }
            CompoundEdit cEdit=null;
            if (changer !=null){
                UndoableEdit edt=changer.updatePaint(fill,changer.FILLMODE);
                if (edt !=null){
                    cEdit=new CompoundEdit();
                    cEdit.addEdit(edt);
                }
                edt=changer.updatePaint(strokePaint,changer.STROKEMODE);
                if (edt!=null){
                    if (cEdit==null) cEdit=new CompoundEdit();
                    cEdit.addEdit(edt);
                }
                
            }
            if (strokeChanger!=null){
                UndoableEdit edt=strokeChanger.updateStroke(stroke);
                if (edt !=null){
                    if (cEdit==null) cEdit=new CompoundEdit();
                    cEdit.addEdit(edt);
                }
            }
            if (cEdit !=null){
                cEdit.end();
                getViewer().getDocument().fireUndoEvent(cEdit);
            }
        }
        hoveringObject=null;
        changeCursor();
    }
    private JLeaf hit(Point p){
        Point2D mp=new Point2D.Double();
        getEnvironment().getToAbsoluteTransform().transform(p,mp);
        JEnvironment env=getViewer().getEnvironment();
        JRequest req=getViewer().getCurrentRequest();
        req.hitObjects.clear();
        req.hitResult=req.HIT_NON;
        getViewer().getAvailableLayer().hitByPoint(env,req,mp);
        JLeaf ret=null;
        if (req.hitResult!=req.HIT_NON){
            Vector<JLeaf> leafs=new Vector<JLeaf>(1);
            for (int i=0;i<req.hitObjects.size();i++){
                Object o=req.hitObjects.get(i);
                if (o instanceof JObject){
                    JColorChanger.getLeafObjects(leafs,(JObject)o);
                }else{
                    if ((o instanceof JLeaf) && (o instanceof JColorable)){
                        leafs.add((JLeaf)o);
                    }
                }
                
            }
            ret=null;
            req.hitObjects.clear();
            for (int i=leafs.size()-1;i>=0;i--){
                if (leafs.get(i).hitByPoint(env,req,mp)!=req.HIT_NON){
                    ret=leafs.get(i);
                    break;
                }
            }
        }
        return ret;
        
    }
    private void setChanger(){
        if (hoveringObject !=null){
            JPaint fill=hoveringObject.getFillPaint();
            JPaint strk=hoveringObject.getStrokePaint();
            JStroke stroke=hoveringObject.getStroke();
            if (fill !=null && fill.getPaintMode()!=JPaint.COLOR_MODE){
                fill=fill.clone();
            }
            if (changer !=null){
                changer.setFillPaint(fill);
                changer.setStrokePaint(strk);
            }
            if (strokeChanger !=null){
                strokeChanger.setStroke(stroke);
            }
        }else{
            getViewer().getCurrentRequest().fireChangeEvent(hoveringObject,ItemEvent.DESELECTED);
        }
        
    }
}
