/*
 * PageSetupEdit.java
 *
 * Created on 2008/05/22, 17:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.layeredit;

import jedit.JAbstractEdit;
import jobject.JPage;
import jprinter.JPageFormat;
import jscreen.JDocumentViewer;

/**
 *
 * @author takashi
 */
public class PageSetupEdit extends JAbstractEdit{
    private JPage target;
    private JPageFormat savedFormat;
    private JPageFormat newFormat;
    /** Creates a new instance of PageSetupEdit */
    public PageSetupEdit(JDocumentViewer viewer,JPage target,JPageFormat newFormat) {
        super(viewer);
        this.target=target;
        savedFormat=target.getPageFormat();
        this.newFormat=newFormat;
        presentationName="ÉyÅ[ÉWê›íË";
        redo();
    }
    public void redo(){
        canUndo=true;
        canRedo=false;
        target.setPageFormat(newFormat);
        viewer.adjustSize();
        viewer.isDraftMode=false;
        viewer.repaint();   
    }
    public void undo(){
        canUndo=false;
        canRedo=true;
        target.setPageFormat(savedFormat);
        viewer.adjustSize();
        viewer.isDraftMode=false;
        viewer.repaint();
    }
    
}
