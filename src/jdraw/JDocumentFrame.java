/*
 * JDocumentWindow.java
 *
 * Created on 2008/05/18, 22:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jdraw;

import jactions.JUndoRedoEvent;
import jactions.JUndoRedoListener;
import java.beans.PropertyVetoException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import jobject.JDocument;
import jscreen.JDocumentViewer;
import jscreen.JScroller;

/**
 *
 * @author takashi
 */
public class JDocumentFrame extends JInternalFrame implements JUndoRedoListener{
    private JScroller scroller;
    private String filePath;
    private boolean hasChanged;
    private Vector<JFrameStateListener> listeners;
    /** Creates a new instance of JDocumentWindow */
    public JDocumentFrame() {
        super("Untitled",true,true,true);
        initComponent();
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        scroller.getViewer().adjustSize();
        scroller.getViewer().getDocument().addUndoRedoListener(this);
        hasChanged=false;
        filePath="";
        listeners=new Vector<JFrameStateListener>();
        this.setFrameIcon(null);
    }

    public void addFrameStateListener(JFrameStateListener l){
        if (listeners.contains(l)) return;
        listeners.add(l);
    }
    public void removeFrameStateListener(JFrameStateListener l){
        listeners.remove(l);
    }
    private void initComponent(){
        scroller=new JScroller();
        scroller.setMinimumSize(new java.awt.Dimension(100, 100));
        scroller.setOpaque(false);        
        getContentPane().add(scroller, java.awt.BorderLayout.CENTER);
        add(scroller);
        pack();
    }
    public void setDocument(JDocument doc){
        scroller.getViewer().getDocument().removeUndoRedoListener(this);
        if (doc !=null)
            doc.addUndoRedoListener(this);
        scroller.getViewer().setDocument(doc);
        scroller.getViewer().adjustSize();
        this.setTitle(doc.getName());
        setChanged(false);
        filePath="";
    }
    public void setFilePath(String path){
        filePath=path;
    }
    public String getFilePath(){
        return filePath;
    }
    public boolean isChanged(){
        return hasChanged;
    }
    public void setChanged(boolean b){
        hasChanged=b;
        for (int i=0;i<listeners.size();i++){
            listeners.get(i).frameStateChanged(this);
        }
    }
    public JDocument getDocument(){
        return scroller.getViewer().getDocument();
    }
    public JDocumentViewer getViewer(){
        return scroller.getViewer();
    }

    public void undoRedoEventHappened(JUndoRedoEvent e) {
        setChanged(true);
    }
}
