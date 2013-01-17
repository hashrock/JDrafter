/*
 * JDStrokePanel.java
 *
 * Created on 2007/02/20, 9:56
 */
package jui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import jtools.jcontrol.JDNumericTextField;
import java.util.Vector;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author  i002060
 */
public class JDStrokePanel extends javax.swing.JPanel implements ChangeListener {

    BasicStroke stroke;
    JDNumericTextField[] dashes;
    boolean disableEvents;
    JDialog dialog;
    boolean desided;
    private Vector<ChangeListener> listeners;
    private ChangeEvent changeEvent = null;

    /** Creates new form JDStrokePanel */
    public JDStrokePanel() {
        initComponents();
        desided = false;
        dialog = null;
        listeners = null;
        disableEvents = false;
        lineWidth.setMinValue(new Double(0));
        lineWidth.setMaxValue(new Double(100d));
        lineWidth.addChangeListener(this);
        slider.addChangeListener(this);
        dashes = new JDNumericTextField[]{dash1, dash2, dash3, dash4, dash5, dash6, dash7, dash8};
        for (int i = 0; i < 8; i++) {
            dashes[i].setMinValue(new Double(0));
            dashes[i].setMaxValue(new Double(100d));
            dashes[i].setDoubleValue(0d);
            dashes[i].addChangeListener(this);
        }
        SpinnerNumberModel sp = new SpinnerNumberModel(10d, 1d, 100d, 1d);
        miterLimit.setModel(sp);
        sp.addChangeListener(this);
        setStroke(new BasicStroke(1f));
    /**  Dimension dm=new Dimension(380,175);
    this.setMinimumSize(dm);
    this.setMaximumSize(dm);
    this.setPreferredSize(dm);
    this.setSize(dm);
     */
    }

    public void setStroke(Stroke s) {
        stroke = (BasicStroke) s;
        strokePreview.setStroke(stroke);
        setupControls();
    }

    private void setupControls() {
        disableEvents = true;
        lineWidth.setFloatValue(stroke.getLineWidth());
        slider.setValue((int) stroke.getLineWidth() * 4);
        switch (stroke.getEndCap()) {
            case BasicStroke.CAP_BUTT:
                cap.setSelectedIndex(0);
                break;
            case BasicStroke.CAP_ROUND:
                cap.setSelectedIndex(1);
                break;
            case BasicStroke.CAP_SQUARE:
                cap.setSelectedIndex(2);
                break;
        }
        switch (stroke.getLineJoin()) {
            case BasicStroke.JOIN_BEVEL:
                join.setSelectedIndex(0);
                miterLimit.setEnabled(false);
                break;
            case BasicStroke.JOIN_MITER:
                join.setSelectedIndex(1);
                miterLimit.setEnabled(true);
                miterLimit.setValue(new Float(stroke.getMiterLimit()));
                break;
            case BasicStroke.JOIN_ROUND:
                join.setSelectedIndex(2);
                miterLimit.setEnabled(false);
                break;
        }
        float[] darray;
        if ((darray = stroke.getDashArray()) != null) {
            jRadioButton1.setSelected(true);
            for (int i = 0; i < 8; i++) {
                if (darray.length > i) {
                    dashes[i].setFloatValue(darray[i]);
                } else {
                    dashes[i].setFloatValue(0f);
                }
                dashes[i].setEnabled(true);
            }
        } else {
            jRadioButton1.setSelected(false);
            for (int i = 0; i < 8; i++) {
                dashes[i].setFloatValue(0f);
                dashes[i].setEnabled(false);
            }
        }
        repaint();
        disableEvents = false;
    }

    public BasicStroke getStroke() {
        return stroke;
    }

    public void showAsPopup(JComponent c) {
        if (dialog == null) {
            Window w = (Window) (c.getRootPane().getParent());
            dialog = new JDialog(w);
            dialog.setContentPane(this);
            dialog.setResizable(false);
            dialog.setUndecorated(true);
            dialog.setModal(false);
            dialog.pack();
            dialog.addWindowFocusListener(new WindowFocusListener() {

                public void windowGainedFocus(WindowEvent e) {
                }

                public void windowLostFocus(WindowEvent e) {
                    fLost(e);
                }
            });
        }
        Insets ins = dialog.getInsets();
        int w = ins.left + ins.right + this.getPreferredSize().width;
        int h = ins.top + ins.bottom + this.getPreferredSize().height;
        dialog.setSize(w, h);
        Point p = new Point(c.getWidth() / 2, c.getHeight() / 2);
        SwingUtilities.convertPointToScreen(p, c);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(this.getGraphicsConfiguration());
        if (p.x + dialog.getWidth() > d.width - insets.right) {
            p.x = d.width - insets.right - dialog.getWidth();
        }
        if (p.x < insets.top) {
            p.x = insets.top;
        }
        if (p.y + dialog.getHeight() > d.height - insets.bottom) {
            p.y = d.height - insets.bottom - dialog.getHeight();
        }
        if (p.y < insets.top) {
            p.y = insets.top;
        }
        dialog.setLocation(p);
        desided = false;
        dialog.setVisible(true);
        fireChangeEvent();
    }

    private void fLost(WindowEvent e) {
        if (dialog.isVisible()) {
            desided = true;
            dialog.setVisible(false);
            fireChangeEvent();
        }
    }

    public void addChangeListener(ChangeListener l) {
        if (listeners == null) {
            listeners = new Vector<ChangeListener>();
        }
        if (!listeners.contains(l)) {
            listeners.add(l);
        }
    }

    public void removeChangeListener(ChangeListener l) {
        if (listeners == null) {
            return;
        }
        listeners.remove(l);
    }

    public void fireChangeEvent() {
        if (listeners == null) {
            return;
        }
        if (changeEvent == null) {
            changeEvent = new ChangeEvent(this);
        }
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).stateChanged(changeEvent);
        }
    }

    public boolean isDesided() {
        return desided;
    }

    public boolean isDialogShown() {
        if (dialog == null) {
            return false;
        }
        return dialog.isVisible();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        requestFocus();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cap = new javax.swing.JComboBox();
        join = new javax.swing.JComboBox();
        miterLimit = new javax.swing.JSpinner();
        jPanel1 = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        dash1 = new jtools.jcontrol.JDNumericTextField();
        dash2 = new jtools.jcontrol.JDNumericTextField();
        dash3 = new jtools.jcontrol.JDNumericTextField();
        dash4 = new jtools.jcontrol.JDNumericTextField();
        dash5 = new jtools.jcontrol.JDNumericTextField();
        dash6 = new jtools.jcontrol.JDNumericTextField();
        dash7 = new jtools.jcontrol.JDNumericTextField();
        dash8 = new jtools.jcontrol.JDNumericTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lineWidth = new jtools.jcontrol.JDNumericTextField();
        slider = new jui.color.JDSlider();
        strokePreview = new jui.JDStrokePreview();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JDStrokePanel.this.keyPressed(evt);
            }
        });

        cap.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Butt", "Round", "Squre" }));
        cap.setFocusable(false);
        cap.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                JDStrokePanel.this.itemStateChanged(evt);
            }
        });

        join.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Bebel", "Miter", "Round" }));
        join.setFocusable(false);
        join.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                JDStrokePanel.this.itemStateChanged(evt);
            }
        });

        miterLimit.setFocusable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jRadioButton1.setText("破線間隔");
        jRadioButton1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton1.setFocusable(false);
        jRadioButton1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jRadioButton1MouseClicked(evt);
            }
        });
        jRadioButton1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jRadioButton1MouseDragged(evt);
            }
        });

        dash1.setFormat("%.1f");

        dash2.setFormat("%.1f");

        dash3.setFormat("%.1f");

        dash4.setFormat("%.1f");

        dash5.setFormat("%.1f");

        dash6.setFormat("%.1f");

        dash7.setFormat("%.1f");

        dash8.setFormat("%.1f");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(dash1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dash2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dash3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dash4, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dash5, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dash6, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dash7, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dash8, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jRadioButton1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dash1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dash2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dash3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dash4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dash5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dash6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dash7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dash8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jLabel1.setText("端の形状:");

        jLabel2.setText("角の形状:");

        jLabel3.setText("線幅:");

        jLabel4.setText("トリミング:");

        jLabel5.setText("pt");

        lineWidth.setFormat("%.2f");

        strokePreview.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        strokePreview.setToolTipText("線のプレビュークリックして決定");
        strokePreview.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                strokePreviewMousePressed(evt);
            }
        });

        javax.swing.GroupLayout strokePreviewLayout = new javax.swing.GroupLayout(strokePreview);
        strokePreview.setLayout(strokePreviewLayout);
        strokePreviewLayout.setHorizontalGroup(
            strokePreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 337, Short.MAX_VALUE)
        );
        strokePreviewLayout.setVerticalGroup(
            strokePreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 66, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lineWidth, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(slider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(join, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(miterLimit, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(strokePreview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(lineWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5))
                    .addComponent(slider, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(join, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(miterLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(strokePreview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel5.getAccessibleContext().setAccessibleName("");
    }// </editor-fold>//GEN-END:initComponents

    private void dash3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dash3ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_dash3ActionPerformed

    private void jRadioButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jRadioButton1MouseClicked
// TODO add your handling code here:
        if (jRadioButton1.isSelected()) {
            cap.setSelectedIndex(0);
        }
        cap.setEnabled(!jRadioButton1.isSelected());
        for (int i = 0; i < 8; i++) {
            dashes[i].setEnabled(jRadioButton1.isSelected());
        }
        stateChanged(new ChangeEvent(evt.getSource()));
    }//GEN-LAST:event_jRadioButton1MouseClicked

    private void jRadioButton1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jRadioButton1MouseDragged
// TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton1MouseDragged

    private void itemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_itemStateChanged
// TODO add your handling code here:
        if (evt.getStateChange() != evt.DESELECTED) {
            return;
        }
        if (disableEvents) {
            return;
        }
        if (evt.getSource() == join) {
            if (getJoin() == BasicStroke.JOIN_MITER) {
                miterLimit.setEnabled(true);
            } else {
                miterLimit.setEnabled(false);
            }
        }
        stateChanged(new ChangeEvent(evt.getSource()));
    }//GEN-LAST:event_itemStateChanged

private void keyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_keyPressed
// TODO add your handling code here:
    if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
        desided = false;
        dialog.dispose();
        return;
    }
    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
        desided = true;
        fireChangeEvent();
        dialog.dispose();
    }
}//GEN-LAST:event_keyPressed

private void strokePreviewMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_strokePreviewMousePressed
// TODO add your handling code here:
    desided = true;
    fireChangeEvent();
    dialog.dispose();
}//GEN-LAST:event_strokePreviewMousePressed

    @Override
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();
        if (disableEvents) {
            return;
        }
        if (source == lineWidth) {
            slider.setValue((int) Math.round(lineWidth.getDoubleValue()) * 4);
        }
        if (source == slider) {
            lineWidth.setDoubleValue(((double) slider.getValue()) / 4);
        }
        stroke = new BasicStroke(lineWidth.getFloatValue(), getCap(), getJoin(),
                getMiterLimit(), getDash(), 0f);
        strokePreview.setStroke(stroke);
        repaint();
        fireChangeEvent();
    }

    private int getCap() {
        if (cap.getSelectedIndex() == 0) {
            return BasicStroke.CAP_BUTT;
        }
        if (cap.getSelectedIndex() == 1) {
            return BasicStroke.CAP_ROUND;
        }
        return BasicStroke.CAP_SQUARE;
    }

    private int getJoin() {
        if (join.getSelectedIndex() == 0) {
            return BasicStroke.JOIN_BEVEL;
        }
        if (join.getSelectedIndex() == 1) {
            return BasicStroke.JOIN_MITER;
        }
        return BasicStroke.JOIN_ROUND;
    }

    private float getMiterLimit() {
        if (getJoin() != BasicStroke.JOIN_MITER) {
            return 0f;
        }
        Number ns = (Number) miterLimit.getValue();
        return ns.floatValue();
    }

    private float[] getDash() {
        if (!jRadioButton1.isSelected()) {
            return null;
        }
        float[] rt = new float[8];
        int maxindex = 7;
        while (maxindex >= 0 && dashes[maxindex].getFloatValue() == 0) {
            maxindex--;
        }
        if (maxindex < 0) {
            return null;
        }
        for (int i = 0; i <= maxindex; i++) {
            if (dashes[i].getFloatValue() != 0) {
                rt[i] = dashes[i].getFloatValue();
            }
        }
        return rt;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cap;
    private jtools.jcontrol.JDNumericTextField dash1;
    private jtools.jcontrol.JDNumericTextField dash2;
    private jtools.jcontrol.JDNumericTextField dash3;
    private jtools.jcontrol.JDNumericTextField dash4;
    private jtools.jcontrol.JDNumericTextField dash5;
    private jtools.jcontrol.JDNumericTextField dash6;
    private jtools.jcontrol.JDNumericTextField dash7;
    private jtools.jcontrol.JDNumericTextField dash8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JComboBox join;
    private jtools.jcontrol.JDNumericTextField lineWidth;
    private javax.swing.JSpinner miterLimit;
    private jui.color.JDSlider slider;
    private jui.JDStrokePreview strokePreview;
    // End of variables declaration//GEN-END:variables
}
