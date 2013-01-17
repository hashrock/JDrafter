/*
 * NewJDialog.java
 *
 * Created on 2008/06/08, 17:34
 */

package jdraw.textpalette;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import jdraw.JDrawApplication;
import jdraw.typemenus.JTypeMenuItem;
import jedit.textobjectedit.JChangeTextEdit;
import jobject.JLeaf;
import jscreen.JDocumentViewer;
import jscreen.JRequest;
import jobject.JText;
import jobject.text.InlineTextPane;

/**
 *
 * @author  takashi
 */
public class TypePanel extends JPanel implements ActionListener,ItemListener,ComponentListener,CaretListener,UndoableEditListener {
    private JDocumentViewer viewer=null;
    /** Creates new form NewJDialog */
    public TypePanel() {
        initComponents();
        alignLeft.setAlignment(StyleConstants.ALIGN_LEFT);
        alignLeft.setToolTipText("左寄せ");
        alignCenter.setAlignment(StyleConstants.ALIGN_CENTER);
        alignCenter.setToolTipText("センタリング");
        alignRight.setAlignment(StyleConstants.ALIGN_RIGHT);
        alignRight.setToolTipText("右寄せ");
        bold.setMode(StyleButton.BOLD);
        bold.setToolTipText("ボールド");
        italic.setMode(StyleButton.ITALIC);
        italic.setToolTipText("斜体");
        underline.setMode(StyleButton.UNDERLINE);
        underline.setToolTipText("下線");
        strikethrough.setMode(StyleButton.STRIKETHROUGH);
        strikethrough.setToolTipText("字消し");
        fontFamily.addActionListener(this);
        sizeCombo.addActionListener(this);
        alignLeft.addActionListener(this);
        alignCenter.addActionListener(this);
        alignRight.addActionListener(this);
        bold.addActionListener(this);
        italic.addActionListener(this);
        underline.addActionListener(this);
        strikethrough.addActionListener(this);
        paragraphButton.addActionListener(this);
        setChildEnabled(false);
    }
    @Override
    public void actionPerformed(ActionEvent e){
        if (viewer==null) return;
        if (!(e.getSource() instanceof JTypeMenuItem)) return;
        JTypeMenuItem itm=(JTypeMenuItem)e.getSource();
        InlineTextPane pane=viewer.getTextPane();
        if (pane.isVisible()){
            if (itm instanceof AlignmentButton || itm==paragraphButton){
                pane.setParagraphAttributes(itm.getAttributes(),false);
            }else{
                pane.setCharacterAttributes(itm.getAttributes(),false);
            }
            if (itm==fontFamily)
                setResentFont(itm.getAttributes());
        }else{
            Vector<JText> selectedTexts=new Vector<JText>();
            JRequest req=viewer.getCurrentRequest();
            for (int i=0;i<req.size();i++){
                Object o=req.get(i);
                if (o instanceof JText)
                    selectedTexts.add((JText)o);
            }
            if (!selectedTexts.isEmpty()){
                startCompound(selectedTexts);
                for (int i=0;i<selectedTexts.size();i++){
                    StyledDocument doc=selectedTexts.get(i).getStyledDocument();
                    doc.addUndoableEditListener(this);
                    if (itm instanceof AlignmentButton || itm==paragraphButton){
                        doc.setParagraphAttributes(0,doc.getLength(),itm.getAttributes(),false);
                    }else{
                        doc.setCharacterAttributes(0,doc.getLength(),itm.getAttributes(),false);
                    }
                    doc.removeUndoableEditListener(this);
                }
               if (itm==fontFamily)
                   setResentFont(itm.getAttributes());
                endCompound();
            }
        }
    }
    private JDrawApplication getApplication(Component c){
        if (c instanceof JDrawApplication){
            return (JDrawApplication) c;
        }
        if (c==null) return null;
        return getApplication(c.getParent());
    }
    private void setResentFont(AttributeSet attr){
       JDrawApplication app=getApplication(this);
       if (app==null) return;
       app.addResentFont(attr);
    }
    public void setViewer(JDocumentViewer v){
        if (viewer !=null){
            viewer.getDocument().removeItemListener(this);
            viewer.getTextPane().removeComponentListener(this);
        }
        viewer=v;
        if (viewer !=null){
            viewer.getDocument().addItemListener(this);
            viewer.getTextPane().addComponentListener(this);
        }
        stateChanged();
    }
    private void stateChanged(){
        if (viewer==null){
            setChildEnabled(false);
            return;
        }
        if (viewer.getTextPane().isVisible()){
            setChildEnabled(true);
        }else{
            JRequest req=viewer.getCurrentRequest();
            boolean en=false;
            JText jt=null;
            for (int i=0;i<req.size();i++){
                if (req.get(i)instanceof JText){
                    en=true;
                    jt=(JText)req.get(i);
                    break;
                }
                
            }
            setChildEnabled(en);
            if (en){
                AttributeSet attr=jt.getStyledDocument().getCharacterElement(0).getAttributes();
                setAttributesToMenu(attr);
            }
        }
    }
    private void setAttributesToMenu(AttributeSet attr){
        fontFamily.setAttributes(attr);
        sizeCombo.setAttributes(attr);
        alignLeft.setAttributes(attr);
        alignCenter.setAttributes(attr);
        alignRight.setAttributes(attr);
        italic.setAttributes(attr);
        bold.setAttributes(attr);
        underline.setAttributes(attr);
        strikethrough.setAttributes(attr);
        paragraphButton.setAttributes(attr);
    }
    private void setChildEnabled(boolean en){
        fontFamily.setEnabled(en);
        sizeCombo.setEnabled(en);
        alignLeft.setEnabled(en);
        alignCenter.setEnabled(en);
        alignRight.setEnabled(en);
        bold.setEnabled(en);
        italic.setEnabled(en);
        underline.setEnabled(en);
        strikethrough.setEnabled(en);
        paragraphButton.setEnabled(en);
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup = new javax.swing.ButtonGroup();
        fontFamily = new jdraw.textpalette.FontFamilyCombo();
        sizeCombo = new jdraw.textpalette.SizeCombo();
        bold = new jdraw.textpalette.StyleButton();
        italic = new jdraw.textpalette.StyleButton();
        underline = new jdraw.textpalette.StyleButton();
        strikethrough = new jdraw.textpalette.StyleButton();
        alignLeft = new jdraw.textpalette.AlignmentButton();
        alignCenter = new jdraw.textpalette.AlignmentButton();
        alignRight = new jdraw.textpalette.AlignmentButton();
        paragraphButton = new jdraw.textpalette.ParagraphButton();

        fontFamily.setMaximumRowCount(30);
        fontFamily.setFocusable(false);
        fontFamily.setFont(new java.awt.Font("MS UI Gothic", 0, 14));

        bold.setText("styleButton1");
        bold.setBorderPainted(false);
        bold.setFocusPainted(false);
        bold.setFocusable(false);
        bold.setPreferredSize(new java.awt.Dimension(20, 20));

        italic.setText("styleButton1");
        italic.setBorderPainted(false);
        italic.setFocusPainted(false);
        italic.setFocusable(false);
        italic.setPreferredSize(new java.awt.Dimension(20, 20));

        underline.setText("styleButton1");
        underline.setBorderPainted(false);
        underline.setContentAreaFilled(false);
        underline.setFocusPainted(false);
        underline.setFocusable(false);
        underline.setPreferredSize(new java.awt.Dimension(20, 20));

        strikethrough.setText("styleButton1");
        strikethrough.setBorderPainted(false);
        strikethrough.setContentAreaFilled(false);
        strikethrough.setFocusPainted(false);
        strikethrough.setFocusable(false);
        strikethrough.setPreferredSize(new java.awt.Dimension(20, 20));

        alignLeft.setBorder(null);
        buttonGroup.add(alignLeft);
        alignLeft.setBorderPainted(false);
        alignLeft.setFocusPainted(false);
        alignLeft.setMargin(new java.awt.Insets(1, 14, 1, 14));
        alignLeft.setMinimumSize(new java.awt.Dimension(20, 20));
        alignLeft.setPreferredSize(new java.awt.Dimension(20, 20));
        alignLeft.setRequestFocusEnabled(false);

        alignCenter.setBorder(null);
        buttonGroup.add(alignCenter);
        alignCenter.setBorderPainted(false);
        alignCenter.setFocusPainted(false);
        alignCenter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        alignCenter.setMargin(new java.awt.Insets(1, 14, 1, 14));
        alignCenter.setMinimumSize(new java.awt.Dimension(20, 20));
        alignCenter.setPreferredSize(new java.awt.Dimension(20, 20));
        alignCenter.setRequestFocusEnabled(false);

        alignRight.setBorder(null);
        buttonGroup.add(alignRight);
        alignRight.setBorderPainted(false);
        alignRight.setFocusPainted(false);
        alignRight.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        alignRight.setMargin(new java.awt.Insets(1, 14, 1, 14));
        alignRight.setMinimumSize(new java.awt.Dimension(20, 20));
        alignRight.setPreferredSize(new java.awt.Dimension(20, 20));
        alignRight.setRequestFocusEnabled(false);

        paragraphButton.setBorder(null);
        paragraphButton.setText("paragraphButton1");
        paragraphButton.setFocusPainted(false);
        paragraphButton.setRequestFocusEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(fontFamily, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sizeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(bold, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(italic, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(underline, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(strikethrough, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(alignLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(alignCenter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(alignRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paragraphButton, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(bold, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(italic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(underline, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(strikethrough, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(sizeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(fontFamily, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(alignLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(alignCenter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                .addComponent(paragraphButton, javax.swing.GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE)
                .addComponent(alignRight, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void itemStateChanged(ItemEvent e) {
        stateChanged();
    }
    
    @Override
    public void componentResized(ComponentEvent e) {
    }
    
    @Override
    public void componentMoved(ComponentEvent e) {
    }
    
    @Override
    public void componentShown(ComponentEvent e) {
        InlineTextPane pane=(InlineTextPane)e.getSource();
        pane.addCaretListener(this);
        stateChanged();
    }
    
    @Override
    public void componentHidden(ComponentEvent e) {
        InlineTextPane pane=(InlineTextPane)e.getSource();
        pane.removeCaretListener(this);
        stateChanged();
    }
    
    @Override
    public void caretUpdate(CaretEvent e) {
        InlineTextPane pane=(InlineTextPane)e.getSource();
        setAttributesToMenu(pane.getInputAttributes());
    }
    private CompoundEdit cedit=null;
    private HashMap<StyledDocument,JText> textMap;
    private void startCompound(Vector<JText> vec){
        cedit=new CompoundEdit();
        textMap=new HashMap<StyledDocument,JText>();
        for (int i=0;i<vec.size();i++){
            JText jt=vec.get(i);
            textMap.put(jt.getStyledDocument(),jt);
        }
    }
    @Override
    public void undoableEditHappened(UndoableEditEvent e){
        StyledDocument doc=(StyledDocument)e.getSource();
        JText targetText=textMap.get(doc);
        UndoableEdit anEdit=new JChangeTextEdit(viewer,(JLeaf)targetText,e.getEdit(),true);
        cedit.addEdit(anEdit);
    }
    private void endCompound(){
        cedit.end();
        viewer.getDocument().fireUndoEvent(cedit);
        cedit=null;
        textMap=null;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private jdraw.textpalette.AlignmentButton alignCenter;
    private jdraw.textpalette.AlignmentButton alignLeft;
    private jdraw.textpalette.AlignmentButton alignRight;
    private jdraw.textpalette.StyleButton bold;
    private javax.swing.ButtonGroup buttonGroup;
    public jdraw.textpalette.FontFamilyCombo fontFamily;
    private jdraw.textpalette.StyleButton italic;
    private jdraw.textpalette.ParagraphButton paragraphButton;
    private jdraw.textpalette.SizeCombo sizeCombo;
    private jdraw.textpalette.StyleButton strikethrough;
    private jdraw.textpalette.StyleButton underline;
    // End of variables declaration//GEN-END:variables
    
}
