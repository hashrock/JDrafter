/*
 * JAbstractTool.java
 *
 * Created on 2007/08/29, 16:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jtools;

import jactions.JUndoRedoEvent;
import jactions.JUndoRedoListener;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import jgeom.JSimplePath;
import jscreen.JDocumentViewer;
import jscreen.JDragPane;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *オブジェクトの編集を行う全てのドラッガーのスーパークラス
 * @author i002060
 */
public abstract class JAbstractTool implements JUndoRedoListener {
    //owner
    protected JDragPane dragPane;
    protected String presentationName = "";
    private int snapPlace = JRequest.HIT_NON;
    private Point2D snapPoint = null;
    private JSimplePath snapPath = null;

    /** Creates a new instance of JAbstractTool */
    public JAbstractTool(JDragPane dragPane) {
        this.dragPane = dragPane;
        dragPane.getViewer().getDocument().addUndoRedoListener(this);
    }

    public JDocumentViewer getViewer() {
        return dragPane.getViewer();
    }

    public JRequest getRequest() {
        return dragPane.getCurrentRequest();
    }

    public JEnvironment getEnvironment() {
        return dragPane.getViewer().getEnvironment();
    }

    protected void setCursor(Cursor c) {
        if (dragPane.getCursor() != c) {
            dragPane.setCursor(c);
        }
    }

    @Override
    public void undoRedoEventHappened(JUndoRedoEvent e) {
    }

    public String presentationName() {
        return presentationName;
    }

    public abstract void changeCursor();

    public abstract void wakeup();

    public void sleep() {
    }

    public void paint(Graphics2D g) {
        if (snapPlace != JRequest.HIT_NON) {
            if (snapPoint !=null){
                Point2D.Float p=new Point2D.Float();
                getEnvironment().getToScreenTransform().transform(snapPoint, p);
                if (snapPlace==JRequest.HIT_ANCUR)
                    g.drawString("アンカー", p.x, p.y);
                else if (snapPlace==JRequest.HIT_PATH)
                    g.drawString("パス", p.x,p.y);
            }
            snapPlace=JRequest.HIT_NON;
            snapPoint=null;
            snapPath=null;
        }
    }
    public void setSnapPlace(int place,Point2D point,JSimplePath path){
        snapPlace=place;
        snapPoint=point;
        snapPath=path;
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void documentChanged() {
    }

    public boolean controlChangeTool() {
        return true;
    }
}
