/*
 * ParagraphOption.java
 *
 * Created on 2008/06/08, 8:02
 */

package jdraw.typemenus;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JFormattedTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

/**
 *
 * @author  takashi
 */
public class ParagraphOption extends javax.swing.JDialog implements FocusListener {
    private boolean canceled=true;
    public static float MaxTabWidth=1000/25.4f*72;
    /** Creates new form ParagraphOption */
    public ParagraphOption(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        tabSpacing.setModel(new SpinnerNumberModel(72f,12f,9999f,1f));
        /*
        firstIndent.addFocusListener(this);
        leftIndent.addFocusListener(this);
        rightIndent.addFocusListener(this);
        lineSpacing.addFocusListener(this);
         */
        setTitle("段落オプション");
    }
    private static Frame getFrame(Component c){
        if (c instanceof Frame) return (Frame)c;
        if (c==null) return null;
        return getFrame(c.getParent());
    }
    public static AttributeSet showAsDialog(Component c,AttributeSet attr){
        Frame f=getFrame(c);
        ParagraphOption dlg=new ParagraphOption(f,true);
        AttributeSet ret=attr;
        dlg.setAttributes(attr);
        int x=0,y=0;
        if (f==null){
            Insets iset=Toolkit.getDefaultToolkit().getScreenInsets(dlg.getGraphicsConfiguration());
            Dimension d=Toolkit.getDefaultToolkit().getScreenSize();
            x=(d.width-dlg.getWidth())/2;
            y=(d.height-dlg.getHeight())/2;
        }else{
            x=f.getX()+(f.getWidth()-dlg.getWidth())/2;
            y=f.getY()+(f.getHeight()-dlg.getHeight())/2;
        }
        dlg.setLocation(x,y);
        dlg.setVisible(true);
        if (dlg.isCanceled()){
            return ret;
        }else{
            return dlg.getAttributes();
        }
    }
    public void setAttributes(AttributeSet attr){
        int align=StyleConstants.getAlignment(attr);
        switch (align){
            case StyleConstants.ALIGN_LEFT:
                alignLeft.setSelected(true);
                break;
            case StyleConstants.ALIGN_CENTER:
                alignCenter.setSelected(true);
                break;
            case StyleConstants.ALIGN_RIGHT:
                alignRight.setSelected(true);
                break;
            default:
                alignLeft.setSelected(true);
                break;
        }
        firstIndent.setValue(new Float(StyleConstants.getFirstLineIndent(attr)));
        leftIndent.setValue(new Float(StyleConstants.getLeftIndent(attr)));
        rightIndent.setValue(new Float(StyleConstants.getRightIndent(attr)));
        lineSpacing.setValue(new Float(StyleConstants.getLineSpacing(attr)));
        //
        float tabspace=72f;
        TabSet tabs=StyleConstants.getTabSet(attr);
        if (tabs !=null ){
            tabspace=tabs.getTab(0).getPosition();
        }
        tabSpacing.setValue(tabspace);
        
        /*
        firstIndent.setFloatValue(StyleConstants.getFirstLineIndent(attr));
        leftIndent.setFloatValue(StyleConstants.getLeftIndent(attr));
        rightIndent.setFloatValue(StyleConstants.getRightIndent(attr));
        lineSpacing.setFloatValue(StyleConstants.getLineSpacing(attr));
         */
    }
    public AttributeSet getAttributes(){
        MutableAttributeSet attr=new SimpleAttributeSet();
        int align=StyleConstants.ALIGN_LEFT;
        if (alignCenter.isSelected())
            align=StyleConstants.ALIGN_CENTER;
        else if (alignRight.isSelected())
            align=StyleConstants.ALIGN_RIGHT;
        StyleConstants.setAlignment(attr,align);
        float f=((Number)firstIndent.getValue()).floatValue();
        StyleConstants.setFirstLineIndent(attr,f);
        f=((Number)leftIndent.getValue()).floatValue();
        StyleConstants.setLeftIndent(attr,f);
        f=((Number)rightIndent.getValue()).floatValue();
        StyleConstants.setRightIndent(attr,f);
        f=((Number)lineSpacing.getValue()).floatValue();
        StyleConstants.setLineSpacing(attr,f);
        f=((Number)tabSpacing.getValue()).floatValue();
        TabSet tabset=null;
        int cnt=(int)(MaxTabWidth/f);
        TabStop[] tabstop=new TabStop[cnt];
        for (int i=0;i<cnt;i++){
            tabstop[i]=new TabStop((i+1)*f);
        }
        tabset=new TabSet(tabstop);
        StyleConstants.setTabSet(attr,tabset);
        return attr;
    }
    public boolean isCanceled(){
        return canceled;
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        alignLeft = new javax.swing.JRadioButton();
        alignCenter = new javax.swing.JRadioButton();
        alignRight = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        firstIndent = new javax.swing.JSpinner();
        leftIndent = new javax.swing.JSpinner();
        rightIndent = new javax.swing.JSpinner();
        lineSpacing = new javax.swing.JSpinner();
        tabSpacing = new javax.swing.JSpinner();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder("行揃え")));

        buttonGroup.add(alignLeft);
        alignLeft.setText("左");
        alignLeft.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        alignLeft.setFocusable(false);
        alignLeft.setMargin(new java.awt.Insets(0, 0, 0, 0));

        buttonGroup.add(alignCenter);
        alignCenter.setText("中央");
        alignCenter.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        alignCenter.setFocusable(false);
        alignCenter.setMargin(new java.awt.Insets(0, 0, 0, 0));

        buttonGroup.add(alignRight);
        alignRight.setText("右");
        alignRight.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        alignRight.setFocusable(false);
        alignRight.setMargin(new java.awt.Insets(0, 0, 0, 0));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(alignLeft)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(alignCenter)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(alignRight)
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(alignLeft)
                .addComponent(alignRight)
                .addComponent(alignCenter))
        );

        jLabel1.setText("1行目インデント:");

        jLabel2.setText("左インデント:");

        jLabel3.setText("右インデント:");

        jLabel4.setText("行間隔:");

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("キャンセル");
        cancelButton.setDefaultCapable(false);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jLabel5.setText("pt");

        jLabel6.setText("pt");

        jLabel7.setText("pt");

        jLabel8.setText("pt");

        jLabel9.setText("pt");

        jLabel10.setText("タブスペース:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(18, 18, 18)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel2)
                                        .addComponent(jLabel3)
                                        .addComponent(jLabel4)))
                                .addComponent(jLabel1)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lineSpacing, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(rightIndent, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(leftIndent, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(firstIndent, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)
                            .addComponent(tabSpacing, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel5)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(okButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cancelButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(okButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel5)
                    .addComponent(firstIndent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(leftIndent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(rightIndent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(lineSpacing, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tabSpacing, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
// TODO add your handling code here:
        canceled=true;
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
// TODO add your handling code here:
        canceled=false;
        this.dispose();
    }//GEN-LAST:event_okButtonActionPerformed
    
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
// TODO add your handling code here:
        canceled=true;
        this.dispose();
    }//GEN-LAST:event_formWindowClosing
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ParagraphOption(new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }
    
    public void focusGained(FocusEvent e) {
        if ( e.getSource() instanceof JFormattedTextField){
            JFormattedTextField jf=(JFormattedTextField)e.getSource();
            jf.selectAll();
        }
    }
    
    public void focusLost(FocusEvent e) {
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton alignCenter;
    private javax.swing.JRadioButton alignLeft;
    private javax.swing.JRadioButton alignRight;
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JButton cancelButton;
    private javax.swing.JSpinner firstIndent;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSpinner leftIndent;
    private javax.swing.JSpinner lineSpacing;
    private javax.swing.JButton okButton;
    private javax.swing.JSpinner rightIndent;
    private javax.swing.JSpinner tabSpacing;
    // End of variables declaration//GEN-END:variables
    
}
