/*
 * JDColorPanel.java
 *
 * Created on 2007/02/08, 16:22
 */
package jui.color;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.*;

/**
 *カラーパネルです.
 * @author  i002060
 */
public class JDColorPanel extends javax.swing.JPanel implements ChangeListener {

    /**
     * Creates new form JDColorPanel
     */
    private Color prevColor;
    private Color currentColor;
    private int alpha;
    private javax.swing.JDialog dialog;
    private Vector<ChangeListener> changeListeners;
    private ChangeEvent changeEvent;
    private boolean desided;
    private boolean allowNull;
    private boolean closeWhenFocusLost = true;

    public JDColorPanel(JComponent c) {
        initComponents();
        changeListeners = null;
        changeEvent = null;
        dialog = null;
        prevColor = null;
        currentColor = null;
        desided = false;
        allowNull = true;
        alpha = 255;
        jTabbedPane1.setTitleAt(0, "Picker");
        jTabbedPane1.setTitleAt(1, "HSV");
        jTabbedPane1.setTitleAt(2, "RGB");
        setupText();
        setupSlider();
        setupAlpha();
       /* Dimension d = new Dimension(299, 120);
        this.setSize(d);
        this.setPreferredSize(d);
        this.setMaximumSize(d);
        this.setMinimumSize(d);
        */
        colorChooser.addChangeListener(this);
        hSlider.addChangeListener(this);
        sSlider.addChangeListener(this);
        vSlider.addChangeListener(this);
        aSlider.addChangeListener(this);
        rSlider.addChangeListener(this);
        gSlider.addChangeListener(this);
        bSlider.addChangeListener(this);
        hText.addChangeListener(this);
        sText.addChangeListener(this);
        vText.addChangeListener(this);
        rText.addChangeListener(this);
        gText.addChangeListener(this);
        bText.addChangeListener(this);
        aText.addChangeListener(this);
        //
        this.dialog = new JDialog(getFrame(c));
        dialog.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                fireChangeEvent();
            }
        });
        dialog.setContentPane(this);
        dialog.setUndecorated(true);
        dialog.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                focusLost(e);
            }
        });
        MouseAdapter adp=new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                previewMouseClicked(e);
            }
        };
        preview1.addMouseListener(adp);
        preview2.addMouseListener(adp);
        preview3.addMouseListener(adp);
    }
    public JDialog getDialog(){
        return dialog;
    }
    @Override
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();
        if (source == aText) {
            alpha = aText.getIntValue();
            setupAlpha();
            setupPreview();
            fireChangeEvent();
            return;
        }
        if (source == aSlider) {
            alpha = aSlider.getValue();
            setupAlpha();
            setupPreview();
            fireChangeEvent();
            return;
        }
        if (source == colorChooser) {
            currentColor = colorChooser.getColor();
            setupPreview();
            setupSlider();
            setupText();
            fireChangeEvent();
            return;
        }
        if (source == rSlider || source == gSlider || source == bSlider) {
            currentColor = new Color(rSlider.getValue(), gSlider.getValue(), bSlider.getValue());
            setupPreview();
            setupSlider();
            setupText();
            fireChangeEvent();
            return;
        }
        if (source == rText || source == gText || source == bText) {
            currentColor = new Color(rText.getIntValue(), gText.getIntValue(), bText.getIntValue());
            setupPreview();
            setupSlider();
            setupText();
            fireChangeEvent();
            return;
        }
        float[] hsv = new float[3];
        if (source instanceof JDSlider) {
            hsv[0] = hSlider.getValue() / 360f;
            hsv[1] = sSlider.getValue() / 100f;
            hsv[2] = vSlider.getValue() / 100f;
            hText.setValue(new Double(hSlider.getValue()));
            sText.setValue(new Double(sSlider.getValue()));
            vText.setValue(new Double(vSlider.getValue()));
        } else {
            hsv[0] = ((Number) hText.getValue()).floatValue() / 360f;
            hsv[1] = ((Number) sText.getValue()).floatValue() / 100f;
            hsv[2] = ((Number) vText.getValue()).floatValue() / 100f;
            hText.setValue((int) (hsv[0] * 360));
            sText.setValue((int) (hsv[1] * 100));
            vText.setValue((int) (hsv[1] * 100));
        }
        currentColor = Color.getHSBColor(hsv[0], hsv[1], hsv[2]);
        setupPreview();
        rSlider.setColor(currentColor);
        gSlider.setColor(currentColor);
        bSlider.setColor(currentColor);
        hSlider.setColor(hsv);
        sSlider.setColor(hsv);
        vSlider.setColor(hsv);
        rSlider.setValue(currentColor.getRed());
        rText.setIntValue(currentColor.getRed());
        gSlider.setValue(currentColor.getGreen());
        rText.setIntValue(currentColor.getGreen());
        bSlider.setValue(currentColor.getRed());
        bText.setIntValue(currentColor.getRed());
        fireChangeEvent();
    }

    private void setupSlider() {
        if (currentColor == null) {
            float[] f = new float[3];
            f[0] = f[1] = 0;
            f[2] = 0;
            hSlider.setValue(0);
            hSlider.setColor(f);
            sSlider.setValue(0);
            sSlider.setColor(f);
            vSlider.setValue(0);
            vSlider.setColor(f);
            rSlider.setValue(0);
            rSlider.setColor(Color.BLACK);

            gSlider.setValue(0);
            gSlider.setColor(Color.BLACK);
            bSlider.setValue(0);
            bSlider.setColor(Color.BLACK);
            return;
        }
        float[] f = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), null);
        hSlider.setValue((int) (f[0] * 360));
        hSlider.setColor(f);
        sSlider.setValue((int) (f[1] * 100));
        sSlider.setColor(f);
        vSlider.setValue((int) (f[2] * 100));
        vSlider.setColor(f);
        rSlider.setColor(currentColor);
        gSlider.setColor(currentColor);
        bSlider.setColor(currentColor);
        rSlider.setValue(currentColor.getRed());
        gSlider.setValue(currentColor.getGreen());
        bSlider.setValue(currentColor.getBlue());
    }

    private void setupText() {
        if (currentColor == null) {
            hText.setDoubleValue(0);
            sText.setDoubleValue(0);
            vText.setDoubleValue(0);
            rText.setIntValue(0);
            gText.setIntValue(0);
            bText.setIntValue(0);
            return;
        }
        float[] f = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), null);
        hText.setValue(new Double((double) f[0] * 360));
        sText.setValue(new Double((double) f[1] * 100));
        vText.setValue(new Double((double) f[2] * 100));
        rText.setIntValue(currentColor.getRed());
        gText.setIntValue(currentColor.getGreen());
        bText.setIntValue(currentColor.getBlue());
    }

    private void setupAlpha() {
        aText.setValue(alpha);
        aSlider.setValue(alpha);
    }

    public void setPrevColor(Color c) {
        prevColor = c;
        preview2.setColor(c);
        setCurrentColor(c);

    }

    public void setCurrentColor(Color c) {
        if (c != null) {
            currentColor = new Color(c.getRed(), c.getGreen(), c.getBlue());
            alpha = c.getAlpha();
        } else {
            currentColor = null;
            alpha = 255;
        }
        setupSlider();
        setupText();
        setupAlpha();
        setupPreview();
    }

    private void setupPreview() {
        if (currentColor != null) {
            preview1.setColor(new Color(currentColor.getRed(), currentColor.getGreen(),
                    currentColor.getBlue(), alpha));
        } else {
            preview1.setColor(currentColor);
        }
        preview2.setColor(prevColor);
    }

    public Color getDisidedColor() {
        return preview1.getColor();
    }

    public boolean isDesided() {
        return desided;
    }

    private static Frame getFrame(Component c) {
        if (c == null) {
            return null;
        }
        if (c instanceof Frame) {
            return (Frame) c;
        }
        return getFrame(c.getParent());
    }

    public void setCloseWhenFocusLost(boolean b) {
        closeWhenFocusLost = b;
    }

    public boolean isCloseWhenFocusLost() {
        return closeWhenFocusLost;
    }

    public  void showAsPopup(Component c) {     
        desided = false;
        Frame w = getFrame(c);
        desided = false;
        //dialog.setModal(false);
        Dimension rct = this.getPreferredSize();
        dialog.setPreferredSize(rct);
        dialog.setSize(rct);
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
        dialog.setVisible(true);
    }

    private void focusLost(WindowEvent e) {
        if (closeWhenFocusLost) {
            desided = true;
            fireChangeEvent();
            dialog.dispose();
        }
    }

    public boolean isDialogVisible() {
        if (dialog == null) {
            return false;
        }
        return dialog.isVisible();
    }
    //
    public void addChangeListener(ChangeListener ls) {
        if (changeListeners == null) {
            changeListeners = new Vector<ChangeListener>();
        }
        if (!changeListeners.contains(ls)) {
            changeListeners.add(ls);
        }
    }

    public void removeChangeListener(ChangeListener ls) {
        if (changeListeners == null) {
            return;
        }
        changeListeners.remove(ls);
    }

    public void fireChangeEvent() {
        if (changeListeners == null) {
            return;
        }
        if (changeEvent == null) {
            changeEvent = new ChangeEvent(this);
        }
        for (int i = 0; i < changeListeners.size(); i++) {
            changeListeners.get(i).stateChanged(changeEvent);
        }
    }

    public boolean isAllowNull() {
        return allowNull;
    }

    public void setAllowNull(boolean b) {
        allowNull = b;
        preview3.setVisible(b);
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (closeWhenFocusLost)
        this.requestFocus();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        colorChooser = new jui.color.JDColorChooser();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        hSlider = new jui.color.JDSlider();
        hText = new jtools.jcontrol.JDNumericTextField();
        sSlider = new jui.color.JDSlider();
        sText = new jtools.jcontrol.JDNumericTextField();
        vSlider = new jui.color.JDSlider();
        vText = new jtools.jcontrol.JDNumericTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        rSlider = new jui.color.JDRGBSlider();
        gSlider = new jui.color.JDRGBSlider();
        bSlider = new jui.color.JDRGBSlider();
        rText = new jtools.jcontrol.JDIntegerTextField();
        gText = new jtools.jcontrol.JDIntegerTextField();
        bText = new jtools.jcontrol.JDIntegerTextField();
        aSlider = new jui.color.JDSlider();
        aText = new jtools.jcontrol.JDIntegerTextField();
        preview1 = new jui.color.JDPaintPreview();
        preview3 = new jui.color.JDPaintPreview();
        preview2 = new jui.color.JDPaintPreview();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JDColorPanel.this.keyPressed(evt);
            }
        });

        jLabel4.setText("A");

        jTabbedPane1.setFocusable(false);

        jPanel3.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout colorChooserLayout = new javax.swing.GroupLayout(colorChooser);
        colorChooser.setLayout(colorChooserLayout);
        colorChooserLayout.setHorizontalGroup(
            colorChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 240, Short.MAX_VALUE)
        );
        colorChooserLayout.setVerticalGroup(
            colorChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 72, Short.MAX_VALUE)
        );

        jPanel3.add(colorChooser, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("tab3", jPanel3);

        jPanel1.setLayout(null);

        jLabel6.setText("%");
        jPanel1.add(jLabel6);
        jLabel6.setBounds(216, 30, 10, 13);

        jLabel1.setText("H");
        jPanel1.add(jLabel1);
        jLabel1.setBounds(10, 10, 8, 13);

        jLabel2.setText("S");
        jPanel1.add(jLabel2);
        jLabel2.setBounds(10, 30, 7, 13);
        jLabel2.getAccessibleContext().setAccessibleName("");

        jLabel3.setText("Ｖ");
        jPanel1.add(jLabel3);
        jLabel3.setBounds(10, 50, 8, 13);

        jLabel5.setText("°");
        jPanel1.add(jLabel5);
        jLabel5.setBounds(220, 10, 12, 20);

        jLabel7.setText("%");
        jPanel1.add(jLabel7);
        jLabel7.setBounds(216, 50, 10, 13);

        hSlider.setMaxValue(360);
        hSlider.setMode(0);
        jPanel1.add(hSlider);
        hSlider.setBounds(20, 10, 150, 20);

        hText.setFormat("%.1f");
        jPanel1.add(hText);
        hText.setBounds(170, 10, 40, 18);

        sSlider.setMaxValue(100);
        sSlider.setMode(1);
        jPanel1.add(sSlider);
        sSlider.setBounds(20, 30, 150, 20);

        sText.setFormat("%.1f");
        jPanel1.add(sText);
        sText.setBounds(170, 30, 40, 18);

        vSlider.setMaxValue(100);
        vSlider.setMode(2);
        jPanel1.add(vSlider);
        vSlider.setBounds(20, 50, 150, 20);

        vText.setFormat("%.1f");
        jPanel1.add(vText);
        vText.setBounds(170, 50, 40, 18);

        jTabbedPane1.addTab("tab1", jPanel1);

        jPanel2.setLayout(null);

        jLabel9.setText("R");
        jPanel2.add(jLabel9);
        jLabel9.setBounds(10, 10, 8, 13);

        jLabel10.setText("G");
        jPanel2.add(jLabel10);
        jLabel10.setBounds(10, 30, 8, 13);

        jLabel11.setText("B");
        jPanel2.add(jLabel11);
        jLabel11.setBounds(10, 50, 8, 13);

        rSlider.setMode(0);
        jPanel2.add(rSlider);
        rSlider.setBounds(20, 10, 150, 20);

        gSlider.setMode(1);
        jPanel2.add(gSlider);
        gSlider.setBounds(20, 30, 150, 20);

        bSlider.setMode(2);
        jPanel2.add(bSlider);
        bSlider.setBounds(20, 50, 150, 20);
        jPanel2.add(rText);
        rText.setBounds(180, 10, 40, 18);
        jPanel2.add(gText);
        gText.setBounds(180, 30, 40, 18);
        jPanel2.add(bText);
        bText.setBounds(180, 50, 40, 18);

        jTabbedPane1.addTab("tab2", jPanel2);

        preview1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        preview1.setToolTipText("変更後(クリックして決定)");

        javax.swing.GroupLayout preview1Layout = new javax.swing.GroupLayout(preview1);
        preview1.setLayout(preview1Layout);
        preview1Layout.setHorizontalGroup(
            preview1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 28, Short.MAX_VALUE)
        );
        preview1Layout.setVerticalGroup(
            preview1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 28, Short.MAX_VALUE)
        );

        preview3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        preview3.setToolTipText("塗りなし");

        javax.swing.GroupLayout preview3Layout = new javax.swing.GroupLayout(preview3);
        preview3.setLayout(preview3Layout);
        preview3Layout.setHorizontalGroup(
            preview3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 18, Short.MAX_VALUE)
        );
        preview3Layout.setVerticalGroup(
            preview3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 18, Short.MAX_VALUE)
        );

        preview2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        preview2.setToolTipText("変更前(クリックして決定)");

        javax.swing.GroupLayout preview2Layout = new javax.swing.GroupLayout(preview2);
        preview2.setLayout(preview2Layout);
        preview2Layout.setHorizontalGroup(
            preview2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 28, Short.MAX_VALUE)
        );
        preview2Layout.setVerticalGroup(
            preview2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 28, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(aSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(aText, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(preview1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(preview2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(preview3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(preview1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(preview2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(preview3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(aText, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(aSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("");
    }// </editor-fold>//GEN-END:initComponents

private void keyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_keyPressed
// TODO add your handling code here:
    if (evt.getKeyCode() == KeyEvent.VK_ENTER || evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
        desided = (evt.getKeyCode() == KeyEvent.VK_ENTER);
        if (!desided) {
            setPrevColor(prevColor);
        }
        fireChangeEvent();
        dialog.dispose();
    }
}//GEN-LAST:event_keyPressed

    private void previewMouseClicked(java.awt.event.MouseEvent evt){
        Object source=evt.getSource();
        if (source==preview3){
            currentColor=null;
            alpha=255;
            setupText();
            setupSlider();
            setupAlpha();
            desided=true;
            setupPreview();
        }else if (source==preview2){
            setPrevColor(prevColor);
            setupSlider();
            setupAlpha();
            setupPreview();
            desided=false;
        }else{
            desided=true;
        }
        dialog.dispose();
        fireChangeEvent();
    }

    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private jui.color.JDSlider aSlider;
    private jtools.jcontrol.JDIntegerTextField aText;
    private jui.color.JDRGBSlider bSlider;
    private jtools.jcontrol.JDIntegerTextField bText;
    private jui.color.JDColorChooser colorChooser;
    private jui.color.JDRGBSlider gSlider;
    private jtools.jcontrol.JDIntegerTextField gText;
    private jui.color.JDSlider hSlider;
    private jtools.jcontrol.JDNumericTextField hText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private jui.color.JDPaintPreview preview1;
    private jui.color.JDPaintPreview preview2;
    private jui.color.JDPaintPreview preview3;
    private jui.color.JDRGBSlider rSlider;
    private jtools.jcontrol.JDIntegerTextField rText;
    private jui.color.JDSlider sSlider;
    private jtools.jcontrol.JDNumericTextField sText;
    private jui.color.JDSlider vSlider;
    private jtools.jcontrol.JDNumericTextField vText;
    // End of variables declaration//GEN-END:variables



       
}
