/*
 * LineToolPanel.java
 *
 * Created on 2007/11/07, 14:19
 */

package jtools.jcontrol;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import jtools.JAbstractTool;
import jtools.JLineTool;
import jscreen.JEnvironment;

/**
 *
 * @author  i002060
 */
public class LineToolPanel extends javax.swing.JPanel  implements ChangeListener{
    //private  static final int WIDTH=238,HEIGHT=110;
    //
    private JDialog dialog;
    private JAbstractTool dragger=null;
    private boolean cancel=true;
    /**
     * Creates new form LineToolPanel
     */
    private static Window getRootWindow(Container c){
        if (c instanceof Window) return (Window)c;
        return getRootWindow(c.getParent());
    }
    public LineToolPanel(JAbstractTool dragger) {
        Window frame=getRootWindow(dragger.getViewer().getRootPane().getParent());
        this.dragger=dragger;
        dialog=new JDialog(frame);
        dialog.setContentPane(this);
        initComponents();
        JEnvironment env=dragger.getEnvironment();
        //this.setPreferredSize(new Dimension(WIDTH,HEIGHT));
        //Insets inset=dialog.getInsets();
        //dialog.setSize(new Dimension(WIDTH+inset.left+inset.right,HEIGHT+inset.top+inset.bottom));
        dialog.setLocationByPlatform(true);
        dialog.setResizable(false);
        dialog.getRootPane().setDefaultButton(okButton);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setModal(true);
        dialog.setTitle(dragger.presentationName() +"�I�v�V����");
        //
        angle.addChangeListener(this);
        angleSlider.addChangeListener(this);
        //
        length.setValue(JEnvironment.DEFAULT_RADIUS);
        angle.setValue(JEnvironment.DEFAULT_ANGLE);
        angleSlider.setValue(-JEnvironment.DEFAULT_ANGLE);
        dialog.pack();
        int centerX=frame.getX()+(frame.getWidth()-dialog.getWidth())/2;
        int centerY=frame.getY()+(frame.getHeight()-dialog.getHeight())/2;
        dialog.setLocation(centerX,centerY);
        okButton.requestFocus();
        dialog.setVisible(true);
    }
    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource()==angleSlider){
            angle.setValue(new Double(-angleSlider.getValue()));
        }else if (e.getSource()==angle){
            angleSlider.setValue(-((Double)angle.getValue()).floatValue());
        }
    }
    public boolean isCanceled(){
        return cancel;
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        length = new jtools.jcontrol.JDLengthTextField();
        angleSlider = new jtools.jcontrol.AngleSlider();
        angle = new jtools.jcontrol.JDAngleTextField();

        jLabel1.setText("����:");

        jLabel2.setText("�p�x:");

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("�L�����Z��");
        cancelButton.setDefaultCapable(false);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout angleSliderLayout = new javax.swing.GroupLayout(angleSlider);
        angleSlider.setLayout(angleSliderLayout);
        angleSliderLayout.setHorizontalGroup(
            angleSliderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 32, Short.MAX_VALUE)
        );
        angleSliderLayout.setVerticalGroup(
            angleSliderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 32, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(angleSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(angle, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(length, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(cancelButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(okButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(length, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(okButton))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(cancelButton))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(angleSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(angle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
// TODO add your handling code here:
        cancel=true;
        dialog.setVisible(false);
        dialog.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
// TODO add your handling code here:
        cancel=false;
        JEnvironment env=dragger.getEnvironment();
        env.DEFAULT_RADIUS=((Number)length.getValue()).doubleValue();
        env.DEFAULT_ANGLE=((Number)angle.getValue()).doubleValue();
        dialog.setVisible(false);
        dialog.dispose();
    }//GEN-LAST:event_okButtonActionPerformed
    
    
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private jtools.jcontrol.JDAngleTextField angle;
    private jtools.jcontrol.AngleSlider angleSlider;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private jtools.jcontrol.JDLengthTextField length;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables
    
}