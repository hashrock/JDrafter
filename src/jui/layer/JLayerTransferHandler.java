/*
 * JLayerTransferHandler.java
 *
 * Created on 2008/05/08, 18:32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jui.layer;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import jedit.layeredit.BringToIndexEdit;
import jobject.JDocument;
import jobject.JLayer;
import jobject.JLeaf;
import jobject.JObject;
import jobject.JPage;

/**
 *
 * @author takashi
 */
public class JLayerTransferHandler extends TransferHandler{
    public static final DataFlavor JLEAF_FLAVOR=new DataFlavor(JLeaf.class,"JLeafClass");
    private JLeaf data=null;
    /** Creates a new instance of JLayerTransferHandler */
    public JLayerTransferHandler() {
    }
    public boolean canImport(JComponent comp,DataFlavor[] flavors){
        for (int i=0;i<flavors.length;i++){
            if (flavors[i].equals(JLEAF_FLAVOR))
                return true;
        }
        return false;
    }
    public boolean canImport(TransferHandler.TransferSupport support){
        JTree.DropLocation loc=(JTree.DropLocation)support.getDropLocation();
        int childIndex=loc.getChildIndex();
        JLeaf dropTarget=(JObject)loc.getPath().getLastPathComponent();
        JLeaf dropObj=null;
        try{
            support.getDataFlavors();
            dropObj=data;
            if (dropObj==null){
                throw new Exception();
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        if (dropTarget.isAnscester(dropObj) || dropObj==dropTarget){
            return false;
        }
        if ((dropTarget instanceof JPage) && !(dropObj instanceof JLayer)) return false;
        return true;
    }
    public boolean importData(TransferHandler.TransferSupport support){
        if (!canImport(support)) return false;
        JTree.DropLocation location=(JTree.DropLocation)support.getDropLocation();
        int childIndex=location.getChildIndex();
        JObject parent=(JObject)location.getPath().getLastPathComponent();
        if (childIndex !=-1)
            childIndex=parent.size()-childIndex;
        int oldIndex=data.getParent().indexOf(data);
        if (data.getParent()==parent){
           if (childIndex==oldIndex) return false;
           if (childIndex==-1 && oldIndex==data.getParent().size()-1) return false;
           if (oldIndex<childIndex) childIndex--; 
        }
        JDocument doc=data.getDocument();
        UndoableEdit edt=new BringToIndexEdit(doc.getViewer(),data,parent,childIndex);
        doc.fireUndoEvent(edt);
        doc.getViewer().repaint();
        return true;
    }
    protected Transferable createTransferable(JComponent c){
        data=null;
        if (!(c instanceof JTree)) return null;
        JTree tree=(JTree)c;
        Point p=c.getMousePosition();
        if (p==null) return null;
        int row=tree.getRowForLocation(p.x,p.y);
        tree.clearSelection();
        tree.addSelectionRow(row);
        TreePath tPath=tree.getPathForRow(row);
        if (tPath==null ) return null;
        JLeaf jl=(JLeaf)tPath.getLastPathComponent();
        if (jl instanceof JPage)
            return null;
        return new JLayerTransfer(jl);
    }
    public int getSourceActions(JComponent c){
        return COPY_OR_MOVE;
    }
    public class JLayerTransfer implements Transferable{
        
        public JLayerTransfer(JLeaf d){
            data=d;
        }
        public DataFlavor[] getTransferDataFlavors() {
            DataFlavor[] ret=new DataFlavor[1];
            ret[0]=JLEAF_FLAVOR;
            return ret;
        }
        
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return (JLEAF_FLAVOR.equals(flavor));
        }
        
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (JLEAF_FLAVOR.equals(flavor)){
                return data;
            }
            throw new UnsupportedFlavorException(flavor);
        }
        
    }
}
