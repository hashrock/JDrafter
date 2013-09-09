/*
 * JGridOption.java
 *
 * Created on 2008/05/23, 13:35
 */
package jdraw;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.SpinnerNumberModel;
import jobject.JGuidLayer;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;

/**
 *
 * @author  takashi
 */
public class JGridOption extends javax.swing.JDialog implements ActionListener {

    /** Creates new form JGridOption */
    private JEnvironment env;
    private boolean result = false;
    private int savedGauge;
    private JDocumentViewer viewer = null;

    public JGridOption(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public JGridOption(Frame parent, JDocumentViewer viewer) {
        this(parent, true);
        this.viewer = viewer;
        env = viewer.getEnvironment();
        savedGauge = env.getGuageUnit();
        interval.setMinValue(0.5f);
        division.setMinValue(1);
        glidColor.setBackground(env.GRID_COLOR);
        divColor.setBackground(env.DIVIDE_GRID_COLOR);
        if (env.getGuageUnit() == JEnvironment.METRIC_GAUGE) {
            unitCombo.setSelectedItem(java.util.ResourceBundle.getBundle("main").getString("grid_mm"));
        } else {
            unitCombo.setSelectedItem(java.util.ResourceBundle.getBundle("main").getString("grid_point"));
        }
        paintAntiareas.setSelected(JEnvironment.PAINT_ANTI_AREASING);
        previewAntiareas.setSelected(JEnvironment.PREVIEW_ANTI_AREASING);
        ancurSize.setModel(new SpinnerNumberModel(JEnvironment.PATH_SELECTOR_SIZE, 4, 12, 1));
        pathAreaSize.setModel(new SpinnerNumberModel(JEnvironment.SELECTION_STROKE_SIZE, 3, 8, 1));
        hilightRat.setModel(new SpinnerNumberModel(JEnvironment.HILIGHT_RATIO, 1.0, 2.0, 0.1));
        shiftAngle.setModel(new SpinnerNumberModel(env.getUnitAngle(), 5, 90, 5));
        gridForeground.setSelected(JEnvironment.GRID_FOREGROUND);
        //
        JGuidLayer gLayer = viewer.getCurrentPage().getGuidLayer();
        gColor.setBackground(gLayer.getGuidColor());
        gHilight.setBackground(gLayer.getPreviewColor());
        if (gLayer.isDotStyle()) {
            lineStyle.setSelectedIndex(1);
        } else {
            lineStyle.setSelectedIndex(0);
        }
        backToGuid.setSelected(!gLayer.isOnTop());
        stateChange();
        unitCombo.addActionListener(this);
        this.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                closing();
            }
        });
        jTabbedPane1.setTitleAt(0, java.util.ResourceBundle.getBundle("main").getString("grid_display"));
        jTabbedPane1.setTitleAt(1, java.util.ResourceBundle.getBundle("main").getString("grid_grid"));
        jTabbedPane1.setTitleAt(2, java.util.ResourceBundle.getBundle("main").getString("grid_guideline"));
        ok.requestFocus();
    }

    public void closing() {
        result = false;
        env.setGuageUnit(savedGauge);
        this.dispose();
    }

    public boolean showDialog() {
        result = false;
        Window owner = getOwner();
        Insets insets = owner.getInsets();
        int x = insets.left + (owner.getWidth() - this.getWidth()) / 2;
        int y = insets.top + (owner.getHeight() - this.getHeight()) / 2;
        this.setLocation(x, y);
        this.setVisible(true);
        return result;
    }

    private void stateChange() {
        if (env.getGuageUnit() == env.METRIC_GAUGE) {
            unit.setText(java.util.ResourceBundle.getBundle("main").getString("grid_mm"));
            interval.setValue(new Double(env.getGridSizeForMil()));
        } else {
            interval.setValue(new Double(env.getGridSize()));
            unit.setText(java.util.ResourceBundle.getBundle("main").getString("grid_point"));
        }
        division.setIntValue(env.getGridDivision());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (unitCombo.getSelectedItem().equals(java.util.ResourceBundle.getBundle("main").getString("grid_mm"))) {
            env.setGuageUnit(env.METRIC_GAUGE);
        } else {
            env.setGuageUnit(env.INCHI_GAUGE);
        }
        stateChange();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ok = new javax.swing.JButton();
        cancel = new javax.swing.JButton();
        resetDefault = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        paintAntiareas = new javax.swing.JCheckBox();
        previewAntiareas = new javax.swing.JCheckBox();
        ancurSize = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        pathAreaSize = new javax.swing.JSpinner();
        hilightRat = new javax.swing.JSpinner();
        jLabel8 = new javax.swing.JLabel();
        shiftAngle = new javax.swing.JSpinner();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        unitCombo = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        interval = new jtools.jcontrol.JDNumericTextField();
        unit = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        division = new jtools.jcontrol.JDIntegerTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        glidColor = new javax.swing.JLabel();
        divColor = new javax.swing.JLabel();
        gridForeground = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        gColor = new javax.swing.JLabel();
        gHilight = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        lineStyle = new javax.swing.JComboBox();
        backToGuid = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("main"); // NOI18N
        setTitle(bundle.getString("grid_display_option")); // NOI18N
        setLocationByPlatform(true);
        setResizable(false);

        ok.setText("OK");
        ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okActionPerformed(evt);
            }
        });

        cancel.setText("Cancel");
        cancel.setDefaultCapable(false);
        cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelActionPerformed(evt);
            }
        });

        resetDefault.setText(bundle.getString("grid_default")); // NOI18N
        resetDefault.setDefaultCapable(false);
        resetDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetDefaultActionPerformed(evt);
            }
        });

        jTabbedPane1.setFocusable(false);

        paintAntiareas.setText(bundle.getString("grid_object_antialias")); //NOI18N

        previewAntiareas.setText(bundle.getString("grid_preview_antialias")); // NOI18N

        jLabel6.setText(bundle.getString("grid_anchor_point_size")); // NOI18N

        jLabel7.setText(bundle.getString("grid_path_selection_tolerance")); // NOI18N

        jLabel8.setText(bundle.getString("grid_shift_key_angle_limit")); // NOI18N

        jLabel9.setText(bundle.getString("grid_pix")); // NOI18N

        jLabel10.setText(bundle.getString("grid_pix")); // NOI18N

        jLabel11.setText(bundle.getString("grid_degree")); // NOI18N

        jLabel12.setText(bundle.getString("grid_highlight_anchor_size_ratio")); // NOI18N

        jLabel13.setText(bundle.getString("grid_times")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(paintAntiareas)
                    .addComponent(previewAntiareas)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel6)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(hilightRat, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                            .addComponent(ancurSize, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                            .addComponent(pathAreaSize, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel13)
                                    .addComponent(jLabel10))
                                .addGap(36, 36, 36))))
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(shiftAngle, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(paintAntiareas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(previewAntiareas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ancurSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel7)
                    .addComponent(pathAreaSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(hilightRat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(shiftAngle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addContainerGap())
        );

        jTabbedPane1.addTab("tab2", jPanel2);

        jLabel1.setText(bundle.getString("grid_unit")); // NOI18N

        unitCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "mm", "point" }));

        jLabel2.setText(bundle.getString("grid_spacing")); // NOI18N

        unit.setText(bundle.getString("grid_mm")); // NOI18N

        jLabel3.setText(bundle.getString("grid_split_number")); // NOI18N

        jLabel4.setText(bundle.getString("grid_color")); // NOI18N

        jLabel5.setText(bundle.getString("grid_parting_line_color")); // NOI18N

        glidColor.setText("　　");
        glidColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        glidColor.setOpaque(true);
        glidColor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                glidColorMouseClicked(evt);
            }
        });

        divColor.setText("　　");
        divColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        divColor.setOpaque(true);
        divColor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                glidColorMouseClicked(evt);
            }
        });

        gridForeground.setText(bundle.getString("grid_show_top")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel1)
                        .addComponent(jLabel2))
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(unitCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(division, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(interval, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(glidColor))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(unit)
                .addGap(79, 79, 79))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(gridForeground)
                    .addComponent(divColor))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(unitCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(interval, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(unit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(division, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(glidColor))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(divColor))
                .addGap(18, 18, 18)
                .addComponent(gridForeground)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab1", jPanel1);

        jLabel14.setText(bundle.getString("grid_display_color:")); // NOI18N

        jLabel15.setText(bundle.getString("grid_highlight_color")); // NOI18N

        gColor.setFont(new java.awt.Font("MS UI Gothic", 0, 14)); // NOI18N
        gColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        gColor.setOpaque(true);
        gColor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                guidMouseClicked(evt);
            }
        });

        gHilight.setFont(new java.awt.Font("MS UI Gothic", 0, 14)); // NOI18N
        gHilight.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        gHilight.setOpaque(true);
        gHilight.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                guidMouseClicked(evt);
            }
        });

        jLabel18.setText(bundle.getString("grid_style:")); // NOI18N

        lineStyle.setModel(new javax.swing.DefaultComboBoxModel(new String[] { bundle.getString("grid_line_solid"), bundle.getString("grid_line_dotted") }));

        backToGuid.setText(bundle.getString("grid_show_guide_background")); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel15)
                    .addComponent(jLabel14)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(backToGuid)
                    .addComponent(gColor, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(gHilight, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lineStyle, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(36, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(gColor, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel15)
                    .addComponent(gHilight, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(lineStyle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addComponent(backToGuid)
                .addContainerGap(66, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab3", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cancel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ok, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(resetDefault, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(ok)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(resetDefault)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void glidColorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_glidColorMouseClicked
// TODO add your handling code here:
        JLabel source = (JLabel) evt.getSource();
        String title = java.util.ResourceBundle.getBundle("main").getString("grid_colorchooser_title");
        if (source == divColor) {
            title = java.util.ResourceBundle.getBundle("main").getString("grid_colorchooser_parting_line_color");
        }
        Color c = source.getBackground();
        Color nc = JColorChooser.showDialog(this, title, c);
        if (nc == null) {
            return;
        }
        if (!nc.equals(c)) {
            source.setBackground(nc);
        }

    }//GEN-LAST:event_glidColorMouseClicked

    private void resetDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetDefaultActionPerformed
// TODO add your handling code here
        unitCombo.setSelectedItem(java.util.ResourceBundle.getBundle("main").getString("grid_mm"));
        interval.setDoubleValue(JEnvironment.DEFAULT_GRIDSIZE_BYMIL);
        division.setIntValue(JEnvironment.DEFAULT_GRIDDIVISION_BYMIL);
        glidColor.setBackground(JEnvironment.DEFAULT_GRID_COLOR);
        divColor.setBackground(JEnvironment.DEFAULT_DIVIDE_GRID_COLOR);
        paintAntiareas.setSelected(JEnvironment.DEFAULT_PAINT_ANTI_AREASING);
        previewAntiareas.setSelected(JEnvironment.DEFAULT_PREVIEW_ANTI_AREASING);
        ancurSize.setValue(JEnvironment.DEFAULT_PATH_SELECTOR_SIZE);
        pathAreaSize.setValue(JEnvironment.DEFAULT_SELECTION_STROKE_SIZE);
        hilightRat.setValue(JEnvironment.DEFAULT_HILIGHT_RATIO);
        gridForeground.setSelected(false);
        shiftAngle.setValue(JEnvironment.DEFAULT_UNIT_ANGLE);
        //
        gColor.setBackground(JEnvironment.DEFAULT_GUID_COLOR);
        gHilight.setBackground(JEnvironment.DEFAULT_GUID_PREVIEW_COLOR);
        lineStyle.setSelectedIndex(0);
        backToGuid.setSelected(false);
    //

    }//GEN-LAST:event_resetDefaultActionPerformed

    private void cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionPerformed
// TODO add your handling code here:
        closing();
    }//GEN-LAST:event_cancelActionPerformed

    private void okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okActionPerformed
// TODO add your handling code here:
        int unit = 0;
        if (unitCombo.getSelectedItem().equals(java.util.ResourceBundle.getBundle("main").getString("grid_mm"))) {
            unit = env.METRIC_GAUGE;
        } else {
            unit = env.INCHI_GAUGE;
        }
        env.setGridSize(interval.getDoubleValue());
        env.setGridDivision(division.getIntValue());
        env.GRID_COLOR = glidColor.getBackground();
        env.DIVIDE_GRID_COLOR = divColor.getBackground();
        JEnvironment.PAINT_ANTI_AREASING = paintAntiareas.isSelected();
        JEnvironment.PREVIEW_ANTI_AREASING = previewAntiareas.isSelected();
        env.setUnitAngle(((Number) shiftAngle.getValue()).intValue());
        JEnvironment.PATH_SELECTOR_SIZE = ((Number) ancurSize.getValue()).floatValue();
        JEnvironment.SELECTION_STROKE_SIZE = ((Number) pathAreaSize.getValue()).floatValue();
        JEnvironment.HILIGHT_RATIO = ((Number) hilightRat.getValue()).floatValue();
        JEnvironment.SELECTION_STROKE = new BasicStroke(JEnvironment.SELECTION_STROKE_SIZE);
        JEnvironment.GRID_FOREGROUND = gridForeground.isSelected();
        if (viewer != null) {
            JGuidLayer gLayer = viewer.getCurrentPage().getGuidLayer();
            gLayer.setGuidColor(gColor.getBackground());
            gLayer.setPreviewColor(gHilight.getBackground());
            gLayer.setDotStyle(lineStyle.getSelectedIndex() == 1);
            gLayer.setOnTop(!backToGuid.isSelected());
        }

        result = true;
        this.dispose();
    }//GEN-LAST:event_okActionPerformed

private void guidMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_guidMouseClicked
// TODO add your handling code here:
    JLabel source = (JLabel) evt.getSource();
    String title = java.util.ResourceBundle.getBundle("main").getString("grid_guide_color");
    if (source == gHilight) {
        title = java.util.ResourceBundle.getBundle("main").getString("grid_guide_highlight_color");
    }
    Color c = source.getBackground();
    Color nc = JColorChooser.showDialog(this, title, c);
    if (nc == null) {
        return;
    }
    if (!nc.equals(c)) {
        source.setBackground(nc);
    }
}//GEN-LAST:event_guidMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner ancurSize;
    private javax.swing.JCheckBox backToGuid;
    private javax.swing.JButton cancel;
    private javax.swing.JLabel divColor;
    private jtools.jcontrol.JDIntegerTextField division;
    private javax.swing.JLabel gColor;
    private javax.swing.JLabel gHilight;
    private javax.swing.JLabel glidColor;
    private javax.swing.JCheckBox gridForeground;
    private javax.swing.JSpinner hilightRat;
    private jtools.jcontrol.JDNumericTextField interval;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JComboBox lineStyle;
    private javax.swing.JButton ok;
    private javax.swing.JCheckBox paintAntiareas;
    private javax.swing.JSpinner pathAreaSize;
    private javax.swing.JCheckBox previewAntiareas;
    private javax.swing.JButton resetDefault;
    private javax.swing.JSpinner shiftAngle;
    private javax.swing.JLabel unit;
    private javax.swing.JComboBox unitCombo;
    // End of variables declaration//GEN-END:variables
}
