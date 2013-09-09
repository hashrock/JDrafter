/*
 * JTextModifyEdit.java
 *
 * Created on 2007/11/06, 10:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jedit.textobjectedit;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import jedit.*;
import jscreen.JDocumentViewer;
import jobject.JText;

/**
 *
 * @author i002060
 */
public class JTextModifyEdit extends JAbstractEdit{
    private JText target;
    private DefaultStyledDocument newDoc;
    private DefaultStyledDocument savedDoc;
    /** Creates a new instance of JTextModifyEdit */
    public JTextModifyEdit(JDocumentViewer viewer,JText target,DefaultStyledDocument newDoc,DefaultStyledDocument savedDoc) {
        super(viewer);
        this.target=target;
        this.newDoc=newDoc;
        this.savedDoc=savedDoc;
        presentationName="テキスト編集";
        redo();
    }
    public void redo(){
        super.redo();
        target.setStyledDocument(newDoc);
        target.textUpdate(viewer.getEnvironment());
    }
    public void undo(){
        super.undo();
        target.setStyledDocument(savedDoc);
        target.textUpdate(viewer.getEnvironment());
    }
    
}
