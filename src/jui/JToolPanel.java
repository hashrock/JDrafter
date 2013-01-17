/*
 * JToolPanel.java
 *
 * Created on 2007/10/01, 15:59
 */
package jui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JSeparator;
import jtools.JSpoitTool;
import jui.color.JColorChanger;
import jscreen.JDragPane;
import jui.JStrokeChanger;

/**
 *
 * @author  i002060
 */
public class JToolPanel extends javax.swing.JPanel {

    JDragPane dragPane;
    JToolWindow parentWindow;
    JColorChanger cChanger;
    JStrokeChanger sChanger;
    Vector<AbstractButton> buttons;

    /** Creates new form JToolPanel */
    public JToolPanel(Frame owner) {
        dragPane = null;
        parentWindow = new JToolWindow(owner, false);
        parentWindow.setCloseBox(false);
        parentWindow.setContentPane(this);
        parentWindow.setResizable(false);
        initComponents();
        this.setPreferredSize(new Dimension(52, 290));
        parentWindow.pack();
        parentWindow.setVisible(true);
        buttons = new Vector<AbstractButton>();
        setButtons();
    }

    public JToolPanel(Frame owner, JDragPane dp) {
        this(owner);
        setDragPane(dp);
    }

    private void setButtons() {
        addButton(JDragPane.JICON.RESHAPE);
        addButton(JDragPane.JICON.DIRECT);
        JSeparator jsp = new JSeparator(JSeparator.HORIZONTAL);
        jsp.setPreferredSize(new Dimension(50, 2));
        this.add(jsp);
        JSelectToggle jst = new JSelectToggle();
        addSelectItem(jst, JDragPane.JICON.PEN);
        addSelectItem(jst, JDragPane.JICON.PEN_PLUS);
        addSelectItem(jst, JDragPane.JICON.PEN_MINUS);
        addSelectItem(jst, JDragPane.JICON.ANCUR);
        buttonGroup.add(jst);
        this.add(jst);
        buttons.add(jst);

        jst = new JSelectToggle();
        addSelectItem(jst, JDragPane.JICON.PENCIL_ICON);
        addSelectItem(jst, JDragPane.JICON.LINE);

        buttonGroup.add(jst);
        this.add(jst);
        buttons.add(jst);
        //addButton(JDragPane.JICON.LINE);

        jst = new JSelectToggle();
        addSelectItem(jst, JDragPane.JICON.RECTANGLE);
        addSelectItem(jst, JDragPane.JICON.ROUNDRECT);
        addSelectItem(jst, JDragPane.JICON.BEVEL);
        addSelectItem(jst, JDragPane.JICON.ELLIPSE);
        addSelectItem(jst, JDragPane.JICON.POLYGON);
        addSelectItem(jst, JDragPane.JICON.STAR);
        buttonGroup.add(jst);
        this.add(jst);
        buttons.add(jst);
        //
        jst = new JSelectToggle();
        addSelectItem(jst, JDragPane.JICON.TEXT);
        addSelectItem(jst, JDragPane.JICON.LAYOUT_TEXT);
        addSelectItem(jst, JDragPane.JICON.PATH_TEXT);
        buttonGroup.add(jst);
        this.add(jst);
        buttons.add(jst);
        //
        jsp = new JSeparator(JSeparator.HORIZONTAL);
        jsp.setPreferredSize(new Dimension(50, 2));
        this.add(jsp);
        //
        jst = new JSelectToggle();
        addSelectItem(jst, JDragPane.JICON.RESIZE);
        addSelectItem(jst, JDragPane.JICON.SHEAR);
        buttonGroup.add(jst);
        this.add(jst);
        buttons.add(jst);

        jst = new JSelectToggle();
        addSelectItem(jst, JDragPane.JICON.ROTATE);
        addSelectItem(jst, JDragPane.JICON.SYMMETRIC);
        buttonGroup.add(jst);
        this.add(jst);
        buttons.add(jst);

        addButton(JDragPane.JICON.CUTTER);
        jst = new JSelectToggle();
        addSelectItem(jst, JDragPane.JICON.ROUND_CORNER);
        addSelectItem(jst, JDragPane.JICON.CUTOFF_CORNER);
        buttonGroup.add(jst);
        this.add(jst);
        buttons.add(jst);
        //
        jsp = new JSeparator(JSeparator.HORIZONTAL);
        jsp.setPreferredSize(new Dimension(50, 2));
        this.add(jsp);
        addButton(JDragPane.JICON.SPOIT);
        addButton(JDragPane.JICON.GRADIENT);
        //
        jsp = new JSeparator(JSeparator.HORIZONTAL);
        jsp.setPreferredSize(new Dimension(50, 2));
        this.add(jsp);
        addButton(JDragPane.JICON.MAGNIFY);
        addButton(JDragPane.JICON.HAND);
        //
        jsp = new JSeparator(JSeparator.HORIZONTAL);
        jsp.setPreferredSize(new Dimension(50, 2));
        this.add(jsp);
        cChanger = new JColorChanger();
        this.add(cChanger);
        sChanger = new JStrokeChanger();
        this.add(sChanger);
        //
        /*
        JSpoitTool spoit=(JSpoitTool)JDragPane.getAction(JDragPane.JICON.SPOIT).getDragger();
        JColorChanger cChanger=new JColorChanger(JDragPane.getViewer());
        spoit.setColorChanger(cChanger);
        this.add(cChanger);
        //       jsp=new JSeparator(JSeparator.HORIZONTAL);
        //       jsp.setPreferredSize(new Dimension(50,2));
        //       this.add(jsp);
        JStrokeChanger stc=new JStrokeChanger();
        stc.setViewer(JDragPane.getViewer());
        spoit.setStrokeChanger(stc);
        this.add(stc);
        //
         **/
        parentWindow.pack();
    }

    public void setDragPane(JDragPane dp) {
        if (dragPane == dp) {
            return;
        }
        if (dp != null) {
            for (int i = 0; i < buttons.size(); i++) {
                ImageIcon ic = (ImageIcon) buttons.get(i).getIcon();
                buttons.get(i).setAction(dp.getAction(ic));
                buttons.get(i).setToolTipText(dp.getAction(ic).getDragger().presentationName());
            }
            JSpoitTool spoit = (JSpoitTool) dp.getAction(JDragPane.JICON.SPOIT).getDragger();
            cChanger.setViewer(dp.getViewer());
            spoit.setColorChanger(cChanger);
            sChanger.setViewer(dp.getViewer());
            spoit.setStrokeChanger(sChanger);
        } else {
            for (int i = 0; i < buttons.size(); i++) {
                ImageIcon ic = (ImageIcon) buttons.get(i).getAction().getValue(Action.LARGE_ICON_KEY);
                buttons.get(i).setAction(new EmptyAction(ic));
            }
        }
        this.dragPane = dp;
    }
    /*
    public void setDragPane(JDragPane dp){
    dragPane=dp;
    addButton(dp.JICON.RESHAPE);
    addButton(dp.JICON.DIRECT);
    JSeparator jsp=new JSeparator(JSeparator.HORIZONTAL);
    jsp.setPreferredSize(new Dimension(50,2));
    this.add(jsp);
    JSelectToggle jst=new JSelectToggle();
    addSelectItem(jst,dp.JICON.PEN);
    addSelectItem(jst,dp.JICON.PEN_PLUS);
    addSelectItem(jst,dp.JICON.PEN_MINUS);
    addSelectItem(jst,dp.JICON.ANCUR);
    buttonGroup.add(jst);
    this.add(jst);
    
    addButton(dp.JICON.LINE);
    
    jst=new JSelectToggle();
    addSelectItem(jst,dp.JICON.RECTANGLE);
    addSelectItem(jst,dp.JICON.ROUNDRECT);
    addSelectItem(jst,dp.JICON.BEVEL);
    addSelectItem(jst,dp.JICON.ELLIPSE);
    addSelectItem(jst,dp.JICON.POLYGON);
    addSelectItem(jst,dp.JICON.STAR);
    buttonGroup.add(jst);
    this.add(jst);
    //
    jst=new JSelectToggle();
    addSelectItem(jst,dp.JICON.TEXT);
    addSelectItem(jst,dp.JICON.LAYOUT_TEXT);
    addSelectItem(jst,dp.JICON.PATH_TEXT);
    buttonGroup.add(jst);
    this.add(jst);
    //
    jsp=new JSeparator(JSeparator.HORIZONTAL);
    jsp.setPreferredSize(new Dimension(50,2));
    this.add(jsp);
    //
    jst=new JSelectToggle();
    addSelectItem(jst,dp.JICON.RESIZE);
    addSelectItem(jst,dp.JICON.SHEAR);
    buttonGroup.add(jst);
    this.add(jst);
    jst=new JSelectToggle();
    addSelectItem(jst,dp.JICON.ROTATE);
    addSelectItem(jst,dp.JICON.SYMMETRIC);
    buttonGroup.add(jst);
    this.add(jst);
    addButton(dp.JICON.CUTTER);
    jst=new JSelectToggle();
    addSelectItem(jst,dp.JICON.ROUND_CORNER);
    addSelectItem(jst,dp.JICON.CUTOFF_CORNER);
    buttonGroup.add(jst);
    this.add(jst);
    //
    jsp=new JSeparator(JSeparator.HORIZONTAL);
    jsp.setPreferredSize(new Dimension(50,2));
    this.add(jsp);
    addButton(dp.JICON.SPOIT);
    addButton(dp.JICON.GRADIENT);
    //
    jsp=new JSeparator(JSeparator.HORIZONTAL);
    jsp.setPreferredSize(new Dimension(50,2));
    this.add(jsp);
    addButton(dp.JICON.MAGNIFY);
    addButton(dp.JICON.HAND);
    //
    jsp=new JSeparator(JSeparator.HORIZONTAL);
    jsp.setPreferredSize(new Dimension(50,2));
    this.add(jsp);
    //
    JSpoitTool spoit=(JSpoitTool)dp.getAction(dp.JICON.SPOIT).getDragger();
    JColorChanger cChanger=new JColorChanger(dp.getViewer());
    spoit.setColorChanger(cChanger);
    this.add(cChanger);
    //       jsp=new JSeparator(JSeparator.HORIZONTAL);
    //       jsp.setPreferredSize(new Dimension(50,2));
    //       this.add(jsp);
    JStrokeChanger stc=new JStrokeChanger();
    stc.setViewer(dp.getViewer());
    spoit.setStrokeChanger(stc);
    this.add(stc);
    //
    parentWindow.pack();
    }
     */

    private void addButton(ImageIcon img) {
        JToggle tgl = new JToggle();
        //tgl.setAction(dragPane.getAction(img));
        tgl.setAction(new EmptyAction(img));
        buttonGroup.add(tgl);
        buttons.add(tgl);
        this.add(tgl);
    }

    private void addSelectItem(JSelectToggle jt, ImageIcon img) {
        JDMenuItem jm = new JDMenuItem();
        //jm.setAction(dragPane.getAction(img));
        jm.setAction(new EmptyAction(img));
        buttons.add(jm);
        jt.add(jm);
    }

    protected class EmptyAction extends AbstractAction {

        public EmptyAction(ImageIcon ic) {
            putValue(LARGE_ICON_KEY, ic);
            putValue(SMALL_ICON, ic);
        }

        public void actionPerformed(ActionEvent e) {
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        buttonGroup = new javax.swing.ButtonGroup();

        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 1));

    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup;
    // End of variables declaration//GEN-END:variables
}