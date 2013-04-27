package jui.color;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;

/**
 *
 * @author  i002060
 */
public class JDGradientPanel extends javax.swing.JPanel implements ChangeListener {

    Vector<ChangeListener> listeners;
    ChangeEvent changeEvent;
    MultipleGradientPaint paint;
    boolean desided;
    boolean closeWhenFocusLost = true;
    JDialog popup;

    /** Creates new form JDGradientPanel */
    public JDGradientPanel() {
        float w = Math.max(1f, this.getWidth());
        paint = new LinearGradientPaint(0, 0, w, 0, new float[]{0f, 1f}, new Color[]{Color.WHITE, Color.BLACK}, MultipleGradientPaint.CycleMethod.NO_CYCLE);
        initComponents();

        slider.setGradinet(paint);
        slider.addChangeListener(this);
        listeners = null;
        changeEvent = null;
        desided = false;
        popup = null;
       /*
        Dimension d = new Dimension(246, 92);
        this.setMinimumSize(d);
        this.setMaximumSize(d);
        this.setPreferredSize(d);
        this.setSize(d);
        */
        setPaint(paint);
        //
        popup = new JDialog();
        popup.setContentPane(this);
        popup.setUndecorated(true);
        Insets is = popup.getInsets();
        popup.setSize(this.getPreferredSize().width + is.left + is.right, this.getPreferredSize().height + is.top + is.bottom);
        popup.setModal(false);
        popup.setResizable(false);
        WindowFocusListener wadp = new WindowFocusListener() {

            public void windowGainedFocus(WindowEvent e) {
            }

            public void windowLostFocus(WindowEvent e) {
                focusLost(e);
            }
        };
        popup.addWindowFocusListener(wadp);
        popup.pack();
        popup.setVisible(true);
        popup.setVisible(false);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        requestFocusInWindow();
    }

    public void setPaint(MultipleGradientPaint p) {
        paint = p;
        slider.setGradinet(p);
        preview.setPaint(p);
        cancel.setPaint(p);
        if (p instanceof LinearGradientPaint) {
            combobox.setSelectedIndex(0);
        } else {
            combobox.setSelectedIndex(1);
        }
    }

    public MultipleGradientPaint getPaint() {
        return paint;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        int tp;
        LinearGradientPaint gd = slider.getGradient();
        if (combobox.getSelectedIndex() == 0) {
            paint = new LinearGradientPaint(gd.getStartPoint(), gd.getEndPoint(), gd.getFractions(), gd.getColors(), gd.getCycleMethod());
        } else {
            double dx = gd.getEndPoint().getX() - gd.getStartPoint().getX();
            double dy = gd.getEndPoint().getY() - gd.getStartPoint().getY();
            float dist = (float) (Math.sqrt(dx * dx + dy * dy));
            paint = new RadialGradientPaint(gd.getStartPoint(), dist,
                    gd.getFractions(), gd.getColors(), gd.getCycleMethod());
        }
        preview.setPaint(paint);
        fireChangeEvent();
    }

    public void addChangeListener(ChangeListener l) {
        if (listeners == null) {
            listeners = new Vector<ChangeListener>();
        }
        listeners.add(l);
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

    public boolean isPopupVisible() {
        if (popup == null) {
            return false;
        }
        return popup.isVisible();
    }

    public void setCloseWhenFocusLost(boolean b) {
        closeWhenFocusLost = b;
    }

    public boolean isCloseWhenFocusLost() {
        return closeWhenFocusLost;
    }

    public void showAsPopup(JComponent cmp) {

        Point p = new Point(cmp.getWidth() / 2, cmp.getHeight() / 2);
        SwingUtilities.convertPointToScreen(p, cmp);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(this.getGraphicsConfiguration());
        if (p.x + popup.getWidth() > d.width - insets.right) {
            p.x = d.width - insets.right - popup.getWidth();
        }
        if (p.x < insets.top) {
            p.x = insets.top;
        }
        if (p.y + popup.getHeight() > d.height - insets.bottom) {
            p.y = d.height - insets.bottom - popup.getHeight();
        }
        if (p.y < insets.top) {
            p.y = insets.top;
        }
        popup.setLocation(p.x, p.y);
        desided = false;
        popup.setVisible(true);
    }

    private void popupCanceled() {
        if (popup != null) {
            popup.dispose();
        }
        desided = false;
        fireChangeEvent();
    }

    private void focusLost(WindowEvent e) {
        if (closeWhenFocusLost) {
            fireChangeEvent();
            desided = true;
            fireChangeEvent();
            popup.dispose();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        combobox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        slider = new jui.color.JDGradientSlider();
        preview = new jui.color.JDGradientPreview();
        cancel = new jui.color.JDGradientPreview();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JDGradientPanel.this.keyPressed(evt);
            }
        });

        combobox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "線形", "放射状" }));
        combobox.setToolTipText("グラデーションの形式");
        combobox.setFocusable(false);
        combobox.setInheritsPopupMenu(true);
        combobox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                stateChanged(evt);
            }
        });

        jLabel1.setText("グラデーションの形状");

        okButton.setText("jButton1");

        cancelButton.setText("jButton1");

        preview.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        preview.setToolTipText("変更後(クリックして決定)");
        preview.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                previewMousePressed(evt);
            }
        });

        javax.swing.GroupLayout previewLayout = new javax.swing.GroupLayout(preview);
        preview.setLayout(previewLayout);
        previewLayout.setHorizontalGroup(
            previewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 28, Short.MAX_VALUE)
        );
        previewLayout.setVerticalGroup(
            previewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 28, Short.MAX_VALUE)
        );

        cancel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        cancel.setToolTipText("変更前(クリックして決定)");
        cancel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                cancelMousePressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(combobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(preview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(slider, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(combobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(preview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(slider, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cancelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelMousePressed
// TODO add your handling code here:
        fireChangeEvent();
        desided = true;
        fireChangeEvent();
        popup.dispose();
        evt.consume();
    }//GEN-LAST:event_cancelMousePressed

    private void previewMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_previewMousePressed
// TODO add your handling code here:
        desided = true;
        fireChangeEvent();
        popup.dispose();
        evt.consume();
    }//GEN-LAST:event_previewMousePressed

    private void stateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_stateChanged
// TODO add your handling code here:
        stateChanged(new ChangeEvent(evt.getSource()));
    }//GEN-LAST:event_stateChanged

private void keyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_keyPressed
// TODO add your handling code here:
    if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
        popupCanceled();
        return;
    }
    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
        fireChangeEvent();
        desided = true;
        fireChangeEvent();
        popup.dispose();
    }
}//GEN-LAST:event_keyPressed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private jui.color.JDGradientPreview cancel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JComboBox combobox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton okButton;
    private jui.color.JDGradientPreview preview;
    private jui.color.JDGradientSlider slider;
    // End of variables declaration//GEN-END:variables
}
