/*
 * JLayoutTextTool.java
 *
 * Created on 2007/11/06, 11:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jtools;

import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import javax.swing.text.DefaultStyledDocument;
import jedit.JReplaceObjectEdit;
import jobject.JLayoutTextObject;
import jobject.JPathObject;
import jscreen.JDragPane;
import jscreen.JEnvironment;
import jscreen.JRequest;
import jobject.JText;
import jobject.text.InlineTextPane;

/**
 *
 * @author i002060
 */
public class JLayoutTextTool extends JTextTool{
    
    /** Creates a new instance of JLayoutTextTool */
    public JLayoutTextTool(JDragPane dragPane) {
        super(dragPane);
        presentationName="レイアウトテキスト";
    }
    public void wakeup(){
        JRequest req=getRequest();
        req.setSelectionMode(req.DIRECT_MODE);
        dragPane.setPaintRect(false);
        startPoint=endPoint=null;
    }
    public void mouseReleased(MouseEvent e){
        if (startPoint !=null && endPoint !=null && !startPoint.equals(endPoint)){
            startPoint=endPoint=null;
            return;
        }
        JEnvironment env=getEnvironment();
        JRequest req=getRequest();
        req.hitResult=req.HIT_NON;
        req.hitObjects.clear();
        Point2D p=new Point2D.Double();
        env.getToAbsoluteTransform().transform(e.getPoint(),p);
        getViewer().getCurrentPage().hitByPoint(env,req,p);
        if (req.hitResult != req.HIT_NON){
            for (int i=0;i<req.hitObjects.size();i++){
                Object o=req.hitObjects.get(i);
                if (o instanceof JText){
                    req.clear();
                    req.add(o);
                    dragPane.repaint();
                    modifyObject((JText)o,e.getPoint());
                    req.hitObjects.clear();
                    req.hitResult=req.HIT_NON;
                    break;
                }
                if (o instanceof JPathObject ){
                    createObject((JPathObject)o);
                    dragPane.repaint();
                    break;
                }
            }
        }
        startPoint=endPoint=null;
    }
    private void createObject(JPathObject jp){
        JRequest req=getRequest();
        JEnvironment env=getEnvironment();
        
        JLayoutTextObject creatingObject=new JLayoutTextObject();
        creatingObject.setPath(jp.getPath().clone());
        creatingObject.setTotalRotation(jp.getTotalRotation());
        //replaceObject
        getViewer().getDocument().fireUndoEvent(new JReplaceObjectEdit(getViewer(),creatingObject,jp,"レイアウトテキスト"));
        //
        DefaultStyledDocument doc=creatingObject.getStyledDocument();
        InlineTextPane tPane=getViewer().getTextPane();
        AffineTransform tx=getEnvironment().getToScreenTransform();
        tx.concatenate(creatingObject.getTotalTransform());
        tPane.setTransform(tx);
        tPane.setJText(creatingObject);
        getViewer().getCurrentRequest().clear();
        doc.addDocumentListener(this);
        targetText=creatingObject;
        targetText.getStyledDocument().addUndoableEditListener(uEListener);
        tPane.setVisible(true);
        /*
        DefaultStyledDocument doc=jto.getStyledDocument();
         
        if (textPane==null)
            textPane=new JTextDialog((JFrame)getViewer().getRootPane().getParent(),true);
        textPane.setDocument(doc);
        doc.addDocumentListener(this);
        targetText=jto;
        textPane.setVisible(true);
        doc.removeDocumentListener(this);
        targetText.textUpdate(getViewer().getEnvironment());
        getViewer().repaint();
        targetText=null;
         */
        
    }
    public void changeCursor() {
        textChanged=true;
        JCursor jc=dragPane.getJCursor();
        setCursor(jc.LAYOUT_TEXT);
    }
}
