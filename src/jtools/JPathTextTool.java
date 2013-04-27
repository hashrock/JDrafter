/*
 * JPathTextTool.java
 *
 * Created on 2007/11/06, 15:43
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
import jobject.JLeaf;
import jobject.JPathObject;
import jobject.JPathTextObject;
import jscreen.JDragPane;
import jscreen.JEnvironment;
import jscreen.JRequest;
import jobject.JText;
import jobject.text.InlineTextPane;

/**
 *
 * @author i002060
 */
public class JPathTextTool extends JLayoutTextTool {

    /** Creates a new instance of JPathTextTool */
    public JPathTextTool(JDragPane dragPane) {
        super(dragPane);
        presentationName = "パステキスト";
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (startPoint != null && endPoint != null && !startPoint.equals(endPoint)) {
            startPoint = endPoint = null;
            return;
        }
        JEnvironment env = getEnvironment();
        JRequest req = getRequest();
        req.hitResult = req.HIT_NON;
        req.hitObjects.clear();
        Point2D p = new Point2D.Double();
        env.getToAbsoluteTransform().transform(e.getPoint(), p);
        getViewer().getCurrentPage().hitByPoint(env, req, p);
        if (req.hitResult == req.HIT_PATH || req.hitResult == req.HIT_ANCUR) {
            for (int i = 0; i < req.hitObjects.size(); i++) {
                Object o = req.hitObjects.get(i);
                if (o instanceof JText) {
                    break;
                }
                if (o instanceof JPathObject) {
                    JPathObject jj = (JPathObject) o;
                    if (jj.getPath().size() == 1) {
                        createObject(jj, p);
                    }
                    req.hitResult = req.HIT_NON;
                    req.hitObjects.clear();
                    break;
                }
            }
        }
        if (req.hitResult != req.HIT_NON) {
            for (int i = 0; i < req.hitObjects.size(); i++) {
                Object o = req.hitObjects.get(i);
                if (o instanceof JText) {
                    req.clear();
                    req.add(o);
                    dragPane.repaint();
                    modifyObject((JText) o, e.getPoint());
                    req.hitObjects.clear();
                    req.hitResult = req.HIT_NON;
                    break;
                }
            }
        }
        startPoint = endPoint = null;
    }

    @Override
    public void wakeup() {
        JRequest req = getRequest();
        req.setSelectionMode(JRequest.DIRECT_MODE);
    }

    private void hit(Point2D p) {
        JRequest req = getRequest();
        JEnvironment env = getEnvironment();
        req.hitResult = req.HIT_NON;
        req.hitObjects.clear();
        for (int i = 0; i < req.size(); i++) {
            Object o = req.get(i);
            if (o instanceof JLeaf) {
                JLeaf jl = (JLeaf) o;
                int rs = jl.hitByPoint(env, req, p);
                if (rs == req.HIT_PATH || rs == req.HIT_ANCUR || rs == req.HIT_L_CONTROL || rs == req.HIT_R_CONTROL) {
                    break;
                }
            }
            req.hitObjects.clear();
            req.hitResult = req.HIT_NON;
        }
        //選択中のオブジェクトにヒットしなかった場合
        if (req.hitResult == req.HIT_NON) {
            getViewer().getCurrentPage().hitByPoint(env, req, p);
        }
    }

    public void mouseMoved(MouseEvent e) {
        JRequest req = getRequest();
        JEnvironment env = getEnvironment();
        Point2D p = new Point2D.Double();
        env.getToAbsoluteTransform().transform(e.getPoint(), p);
        hit(p);
    }

    @Override
    public void changeCursor() {
        textChanged = true;
        JRequest req = getRequest();
        JCursor jc = dragPane.getJCursor();

        if (req.hitResult == JRequest.HIT_PATH) {
            setCursor(JCursor.PATH_TEXT_ON_PATH);
        } else {
            setCursor(JCursor.PATH_TEXT);
        }
    }

    private void createObject(JPathObject jp, Point2D p) {
        JRequest req = getRequest();
        JEnvironment env = getEnvironment();

        JPathTextObject creatingObject = new JPathTextObject();
        creatingObject.setPath(jp.getPath().clone());
        creatingObject.setTotalRotation(jp.getTotalRotation());
        creatingObject.setStartPosition(creatingObject.dividePathPt(creatingObject.getPath().get(0),
                (float) p.getX(), (float) p.getY(), 0.01f));
        DefaultStyledDocument doc = creatingObject.getStyledDocument();
        //replaceObject
        getViewer().getDocument().fireUndoEvent(new JReplaceObjectEdit(getViewer(), creatingObject, jp, "レイアウトテキスト"));
        //        
        InlineTextPane tPane = getViewer().getTextPane();
        AffineTransform tx = getEnvironment().getToScreenTransform();
        tx.concatenate(creatingObject.getTotalTransform());
        tPane.setTransform(tx);
        tPane.setJText(creatingObject);
        getViewer().getCurrentRequest().clear();
        doc.addDocumentListener(this);
        targetText = creatingObject;
        targetText.getStyledDocument().addUndoableEditListener(uEListener);
        tPane.setVisible(true);
    /*
    JRequest req=getRequest();
    JEnvironment env=getEnvironment();
    //
    JPathTextObject target=new JPathTextObject();
    target.setPath(jp.getPath().clone());
    //
    getViewer().getDocument().fireUndoEvent(new JReplaceObjectEdit(getViewer(),target,jp,"パステキスト"));
    //
    DefaultStyledDocument doc=target.getStyledDocument();
    target.setStartPosition(target.dividePathPt(target.getPath().get(0),
    (float)p.getX(),(float)p.getY(),0.01f));
    if (textPane==null)
    textPane=new JTextDialog((JFrame)getViewer().getRootPane().getParent(),true);
    textPane.setDocument(doc);
    doc.addDocumentListener(this);
    targetText=target;
    textPane.setVisible(true);
    doc.removeDocumentListener(this);
    targetText.textUpdate(getViewer().getEnvironment());
    getViewer().repaint();
    targetText=null;
     */
    }
}
