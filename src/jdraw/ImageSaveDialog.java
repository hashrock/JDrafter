/*
 * ImageSaveDialog.java
 *
 * Created on 2008/07/12, 16:03
 */
package jdraw;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.io.File;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.RepaintManager;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import jtools.JCursor;
import jobject.JObject;
import jobject.JPage;
import jscreen.JRequest;

/**
 *
 * @author  takashi
 */
public class ImageSaveDialog extends javax.swing.JDialog implements ActionListener, ChangeListener {

    private JPage page = null;
    private Rectangle dragRect = null;
    public boolean isDraftMode = false;
    private InnerDragPane dragPane = null;

    /** Creates new form ImageSaveDialog */
    public ImageSaveDialog(java.awt.Frame parent, boolean modal, JPage p) {
        super(parent, modal);
        page = p;
        initComponents();
        jPanel2.setLayout(new BorderLayout());
        dragPane = new InnerDragPane();
        jPanel2.add(dragPane, BorderLayout.CENTER);
        setCombos();
    }

    public static void showAsDialog(JDrawApplication app, JPage cPage) {
        ImageSaveDialog dialog = new ImageSaveDialog(app, true, cPage);
        if (app != null) {
            int x = app.getX() + (app.getWidth() - dialog.getWidth()) / 2;
            int y = app.getY() + (app.getHeight() - dialog.getHeight()) / 2;
            dialog.setLocation(x, y);
        }
        dialog.setVisible(true);
    }

    private void setCombos() {
        writeArea.addItem(java.util.ResourceBundle.getBundle("main").getString("isd_whole_document"));
        writeArea.addItem(java.util.ResourceBundle.getBundle("main").getString("isd_selected_object"));
        String[] formats = ImageIO.getWriterFileSuffixes();
        for (int i = 0; i < formats.length; i++) {
            format.addItem(formats[i]);
        }
        dpi.setModel(new SpinnerNumberModel(72, 36, 600, 12));
        quality.setModel(new SpinnerNumberModel(100, 0, 100, 10));
        writeArea.addActionListener(this);
        format.addActionListener(this);
        dpi.addChangeListener(this);
        quality.addChangeListener(this);
        inputValueChanged();
        transparent.setEnabled(false);
        transparent.setSelected(false);
        transparent.addChangeListener(this);
        jButton1.requestFocus();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == writeArea || e.getSource() == transparent || e.getSource() == format) {
            isDraftMode = false;
            if (e.getSource() == writeArea) {
                dragRect = null;
            }
            jPanel2.repaint();
        }
        inputValueChanged();
    }

    @Override
    public void stateChanged(ChangeEvent e) {

        if (e.getSource() == writeArea || e.getSource() == transparent || e.getSource() == format) {
            isDraftMode = false;
            if (e.getSource() == writeArea) {
                dragRect = null;
            }
            jPanel2.repaint();
        }
        inputValueChanged();
    }

    private void inputValueChanged() {
        if (page == null) {
            return;
        }
        if (writeArea.getSelectedIndex() == 0) {
            PageFormat p = page.getPageFormat();
            int dpiValue = ((Number) dpi.getValue()).intValue();
            double imageX = p.getWidth();
            double imageY = p.getHeight();
            if (dragRect != null && !dragRect.isEmpty()) {
                Rectangle2D r = getPreviewBounds();
                double ratio = p.getWidth() / r.getWidth();
                imageX = dragRect.getWidth() * ratio;
                imageY = dragRect.getHeight() * ratio;
            }
            width.setIntValue((int) (imageX * dpiValue / 72f));
            height.setIntValue((int) (imageY * dpiValue / 72f));
        } else {
            JRequest req = page.getDocument().getViewer().getCurrentRequest();
            Rectangle2D r = getSelectionBounds(req);
            if (r != null) {
                double imageX = r.getWidth();
                double imageY = r.getHeight();
                if (dragRect != null && !dragRect.isEmpty()) {
                    Rectangle2D br = getPreviewBounds();
                    double ratio = r.getWidth() / br.getWidth();
                    imageX = dragRect.getWidth() * ratio;
                    imageY = dragRect.getHeight() * ratio;
                }
                int dpiValue = ((Number) dpi.getValue()).intValue();
                width.setIntValue((int) (imageX * dpiValue / 72f));
                height.setIntValue((int) (imageY * dpiValue / 72f));
            } else {
                width.setIntValue(0);
                height.setIntValue(0);
            }
        }
        quality.setEnabled(format.getSelectedItem().equals("jpg") || format.getSelectedItem().equals("jpeg")); //NOI18N
        boolean isPng = format.getSelectedItem().equals("png") || format.getSelectedItem().equals("PNG"); //NOI18N
        transparent.setEnabled(isPng);

        jPanel2.repaint();
    }

    private void paintSelection(JObject o, Rectangle2D r, Graphics2D g, JRequest req) {
        for (int i = 0; i < req.size(); i++) {
            if (req.get(i) instanceof jobject.JLeaf) {
                jobject.JLeaf jl = (jobject.JLeaf) req.get(i);
                jl.paint(r, g);
            }
        }
    }

    private Rectangle2D getSelectionBounds(JRequest req) {
        Rectangle2D r = null;
        for (int i = 0; i < req.size(); i++) {
            Object o = req.get(i);
            if (!(o instanceof jobject.JLeaf)) {
                continue;
            }
            jobject.JLeaf jl = (jobject.JLeaf) o;
            if (r == null) {
                r = jl.getBounds();
            } else {
                r.add(jl.getBounds());
            }
        }
        return r;
    }

    private AffineTransform getToScreenTransform(Rectangle2D objectBounds, Rectangle2D componentBounds) {
        AffineTransform result = null;
        Rectangle2D wr = getPreviewBounds(objectBounds, componentBounds);
        if (wr != null) {
            double ratX = componentBounds.getWidth() / objectBounds.getWidth();
            double ratY = componentBounds.getHeight() / objectBounds.getHeight();
            double rat = Math.min(ratX, ratY);
            result = new AffineTransform();
            result.setToTranslation(wr.getX(), wr.getY());
            result.scale(rat, rat);
            result.translate(-objectBounds.getX(), -objectBounds.getY());
        }
        return result;
    }

    private AffineTransform getToScreenTransform() {
        if (page == null) {
            return null;
        }
        Rectangle2D r;
        JRequest req = page.getDocument().getViewer().getCurrentRequest();

        if (writeArea.getSelectedIndex() == 0) {
            r = new Rectangle2D.Double(0, 0, (int) page.getPageFormat().getWidth(), (int) page.getPageFormat().getHeight());
        } else {
            r = getSelectionBounds(req);
        }
        Insets inset = jPanel2.getInsets();
        Rectangle cBounds = new Rectangle(inset.left, inset.top, jPanel2.getWidth() - inset.left - inset.right - 1, jPanel2.getHeight() - inset.top - inset.bottom - 1);
        return getToScreenTransform(r, cBounds);
    }

    private Rectangle2D getPreviewBounds(Rectangle2D objectBounds, Rectangle2D componentBounds) {
        Rectangle2D result = null;
        if (objectBounds != null && !objectBounds.isEmpty() &&
                componentBounds != null && !componentBounds.isEmpty()) {
            double ratX = componentBounds.getWidth() / objectBounds.getWidth();
            double ratY = componentBounds.getHeight() / objectBounds.getHeight();
            double rat = Math.min(ratX, ratY);
            Rectangle2D.Double wr = new Rectangle2D.Double(0, 0, objectBounds.getWidth() * rat, objectBounds.getHeight() * rat);
            wr.x = componentBounds.getX() + (componentBounds.getWidth() - wr.getWidth()) / 2;
            wr.y = componentBounds.getY() + (componentBounds.getHeight() - wr.getHeight()) / 2;
            result = wr;
        }
        return result;
    }

    private Rectangle2D getPreviewBounds() {
        if (page == null) {
            return null;
        }
        Rectangle2D r;
        JRequest req = page.getDocument().getViewer().getCurrentRequest();

        if (writeArea.getSelectedIndex() == 0) {
            r = new Rectangle2D.Double(0, 0, (int) page.getPageFormat().getWidth(), (int) page.getPageFormat().getHeight());
        } else {
            r = getSelectionBounds(req);
        }
        Insets inset = jPanel2.getInsets();
        Rectangle cBounds = new Rectangle(inset.left, inset.top, jPanel2.getWidth() - inset.left - inset.right - 1, jPanel2.getHeight() - inset.top - inset.bottom - 1);
        return getPreviewBounds(r, cBounds);
    }

    private class InnerPanel extends JPanel {

        private Paint cTexture;
        private BufferedImage buffer = null;

        public InnerPanel() {
            BufferedImage bImage = new BufferedImage(64, 64, BufferedImage.TYPE_INT_BGR);
            Graphics2D g2 = bImage.createGraphics();
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, 64, 64);
            g2.setColor(new Color(220, 220, 220));
            g2.fillRect(0, 0, 32, 32);
            g2.fillRect(32, 32, 32, 32);
            cTexture = new TexturePaint(bImage, new Rectangle(0, 0, 64, 64));
        }

        @Override
        public void paintComponent(Graphics ga) {
            Paint p;
            if (!isDraftMode || buffer == null) {
                buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                if (transparent.isEnabled() && transparent.isSelected()) {
                    p = cTexture;
                } else {
                    p = Color.WHITE;
                }
                Graphics2D g2 = buffer.createGraphics();
                g2.setPaint(p);
                g2.fill(new Rectangle(0, 0, getWidth(), getHeight()));

                if (width.getIntValue() != 0 && height.getIntValue() != 0 && page != null) {
                    JRequest req = page.getDocument().getViewer().getCurrentRequest();
                    Rectangle2D r;
                    if (writeArea.getSelectedIndex() == 0) {
                        r = new Rectangle2D.Double(0, 0, (int) page.getPageFormat().getWidth(), (int) page.getPageFormat().getHeight());
                    } else {
                        r = getSelectionBounds(req);
                    }
                    Insets inset = this.getInsets();
                    Rectangle cBounds = new Rectangle(inset.left, inset.top, getWidth() - inset.left - inset.right - 1, getHeight() - inset.top - inset.bottom - 1);
                    Rectangle2D wr = getPreviewBounds();
                    AffineTransform tx = getToScreenTransform(r, cBounds);
                    wr = getPreviewBounds();
                    g2.setColor(Color.BLACK);
                    g2.draw(wr);
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setTransform(tx);
                    if (writeArea.getSelectedIndex() == 0) {
                        boolean vb = page.getGuidLayer().isVisible();
                        page.getGuidLayer().setVisible(false);
                        page.paint(r, g2);
                        page.getGuidLayer().setVisible(vb);
                    } else {
                        tx.setToTranslation(wr.getX(), wr.getY());
                        paintSelection(page, r, g2, req);
                    }
                }
                g2.dispose();
            }
            ga.drawImage(buffer, 0, 0, this);
            isDraftMode = true;
        }
    }

    private ImageSaveDialog getDialog() {
        return this;
    }

    private class InnerDragPane extends JComponent implements Runnable {

        private Thread thread = null;
        private boolean isRunning = false;
        private int isEven = 0;
        private Point startPoint = null,  endPoint = null;
        private BasicStroke b1 = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10f,
                new float[]{2f, 2f}, 0f);
        private BasicStroke b2 = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10f,
                new float[]{2f, 2f}, 1f);
        private BasicStroke b3 = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10f,
                new float[]{2f, 2f}, 2f);

        public InnerDragPane() {
            MouseAdapter adp = new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e) {
                    mPressed(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    mReleased(e);
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    mDragged(e);
                }
            };
            addMouseListener(adp);
            addMouseMotionListener(adp);
            this.setCursor(JCursor.CROSSHAIR);
        }

        private void mPressed(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                return;
            }
            startPoint = e.getPoint();
            endPoint = null;
            dragRect = null;
            dragRect = null;
            repaint();
        }

        private void mReleased(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                return;
            }
            if (endPoint == null) {
                dragRect = null;
            } else {
                if (dragRect == null) {
                    dragRect = new Rectangle();
                }
                dragRect.setFrameFromDiagonal(startPoint, endPoint);
                Rectangle2D pRect = getPreviewBounds();
                if (pRect != null) {
                    Rectangle.intersect(dragRect, pRect, dragRect);
                } else {
                    dragRect = null;
                }
                if (dragRect != null && dragRect.isEmpty()) {
                    dragRect = null;
                }
            }

            startPoint = endPoint = null;
            if (dragRect == null) {
                stop();
            } else if (!isRunning()) {
                start();
            }
            inputValueChanged();
            repaint();
        }

        private void mDragged(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                return;
            }
            endPoint = e.getPoint();
            if (dragRect == null) {
                dragRect = new Rectangle();
            }
            dragRect.setFrameFromDiagonal(startPoint, endPoint);
            if (!isRunning()) {
                start();
            }
            RepaintManager rp = RepaintManager.currentManager(this);
            rp.addDirtyRegion(this, 0, 0, getWidth(), getHeight());
            rp.paintDirtyRegions();
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            if (dragRect == null) {
                return;
            }

            g2.setStroke(new BasicStroke(1f));
            g2.setColor(Color.WHITE);
            g2.draw(dragRect);
            g2.setColor(Color.BLACK);
            switch (isEven++) {
                case 0:
                    g2.setStroke(b1);
                    break;
                case 1:
                    g2.setStroke(b2);
                    break;
                default:
                    g2.setStroke(b3);
                    break;
            }
            g2.draw(dragRect);
            if (isEven > 2) {
                isEven = 0;
            }
        }

        @Override
        public void run() {
            while (isRunning() && dragRect != null && thread != null && getDialog().isVisible()) {
                repaint();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                }
            }
            isRunning = false;
            dragRect = null;
            thread = null;
        }

        public void start() {
            if (isRunning()) {
                return;
            }
            thread = new Thread(this);
            isRunning = true;
            thread.start();
        }

        public void stop() {
            isRunning = false;
            thread = null;
        }

        private boolean isRunning() {
            return isRunning;
        }
    }

    private void saveImage(File f, String sufix) {
        Rectangle2D r;
        JRequest req = page.getRequest();
        float rat = ((Number) dpi.getValue()).floatValue() / 72f;
        if (writeArea.getSelectedIndex() == 0) {
            PageFormat pf = page.getPageFormat();
            r = new Rectangle2D.Float(0, 0, (float) pf.getWidth(), (float) pf.getHeight());
        } else {
            r = getSelectionBounds(req);
        }
        int imageType = BufferedImage.TYPE_INT_BGR;
        if ((sufix.equals("png") || sufix.equals("PNG")) && transparent.isSelected()) { //NOI18N
            imageType = BufferedImage.TYPE_INT_ARGB;
        }
        double imageWidth = r.getWidth();
        double imageHeight = r.getHeight();
        if (dragRect != null && !dragRect.isEmpty()) {
            Rectangle2D ra = getPreviewBounds();
            double ratio = r.getWidth() / ra.getWidth();
            imageWidth = dragRect.getWidth() * ratio;
            imageHeight = dragRect.getHeight() * ratio;
            AffineTransform af = getToScreenTransform();
            if (af != null) {
                try {
                    af = af.createInverse();
                } catch (NoninvertibleTransformException ex) {
                    Logger.getLogger(ImageSaveDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
                r = af.createTransformedShape(dragRect).getBounds2D();
            }
        }
        BufferedImage bImage = null;
        try {
            bImage = new BufferedImage((int) (imageWidth * rat), (int) (imageHeight * rat), imageType);
        } catch (Exception ex) {
            JOptionPane.showConfirmDialog(this, ex.getLocalizedMessage());
            return;
        }

        Graphics2D g = (Graphics2D) bImage.getGraphics();
        AffineTransform tx = new AffineTransform();
        tx.setToScale(rat, rat);
        tx.translate(-r.getX(), -r.getY());
        if ((sufix.equals("png") || sufix.equals("PNG")) && transparent.isSelected()) { //NOI18N
            g.setColor(new Color(255, 255, 255, 0));
        } else {
            g.setColor(Color.WHITE);
        }
        g.fillRect(0, 0, bImage.getWidth(), bImage.getHeight());
        g.transform(tx);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (writeArea.getSelectedIndex() == 0) {
            boolean vb = page.getGuidLayer().isVisible();
            page.getGuidLayer().setVisible(false);
            page.paint(r, g);
            page.getGuidLayer().setVisible(vb);
        } else {
            paintSelection(page, r, g, req);
        }
        try {
            Iterator wfiles = ImageIO.getImageWritersBySuffix(format.getSelectedItem().toString());
            if (wfiles.hasNext()) {
                ImageWriter iWriter = (ImageWriter) wfiles.next();
                ImageOutputStream stream = ImageIO.createImageOutputStream(f);
                iWriter.setOutput(stream);
                ImageWriteParam param = iWriter.getDefaultWriteParam();
                float q = ((Number) quality.getValue()).floatValue() / 100f;
                if (quality.isEnabled()) {
                    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    param.setCompressionQuality(q);
                }
                iWriter.write(null, new IIOImage(bImage, null, null), param);
                stream.close();
            }
        } catch (Exception ex) {
            JOptionPane.showConfirmDialog(this, ex.getLocalizedMessage());
            ex.printStackTrace();
        }

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        writeArea = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        format = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        dpi = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        quality = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        transparent = new javax.swing.JCheckBox();
        jLabel7 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        width = new jtools.jcontrol.JDIntegerTextField();
        height = new jtools.jcontrol.JDIntegerTextField();
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new InnerPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("main_ja"); // NOI18N
        setTitle(bundle.getString("isd_write_as_image")); // NOI18N
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        writeArea.setFocusable(false);

        java.util.ResourceBundle bundle1 = java.util.ResourceBundle.getBundle("main"); // NOI18N
        jLabel2.setText(bundle1.getString("isd_output_range")); // NOI18N

        format.setFocusable(false);

        jLabel5.setText(bundle1.getString("isd_format")); // NOI18N

        jLabel4.setText(bundle1.getString("isd_resolution")); // NOI18N

        jLabel1.setText(bundle1.getString("isd_dpi")); // NOI18N

        jLabel8.setText(bundle1.getString("isd_size_x")); // NOI18N

        jLabel10.setText(bundle1.getString("pixel")); // NOI18N

        jLabel9.setText(bundle1.getString("pixel")); // NOI18N

        jLabel11.setText(bundle1.getString("isd_size_y")); // NOI18N

        jLabel6.setText(bundle1.getString("isd_quality")); // NOI18N

        transparent.setText(bundle1.getString("isd_transparent_background")); // NOI18N

        jLabel7.setText(bundle1.getString("percent")); // NOI18N

        jButton1.setText(bundle.getString("isd_write_image")); // NOI18N
        jButton1.setLabel(bundle1.getString("isd_execute_write_as_image")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText(bundle.getString("isd_exit")); // NOI18N
        jButton2.setDefaultCapable(false);
        jButton2.setLabel(bundle1.getString("isd_exit")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        width.setEditable(false);

        height.setEditable(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel2)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4)
                            .addComponent(jLabel11)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(transparent)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(dpi, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(format, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(writeArea, javax.swing.GroupLayout.Alignment.LEADING, 0, 137, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(width, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(height, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel10)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(quality, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(129, 129, 129)
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(129, 129, 129)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(writeArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(format, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dpi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel10)
                    .addComponent(width, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel11)
                    .addComponent(height, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(quality, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addGap(18, 18, 18)
                .addComponent(transparent)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap())
        );

        jLabel4.getAccessibleContext().setAccessibleName("null");
        jLabel1.getAccessibleContext().setAccessibleName("null");

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 467, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 452, Short.MAX_VALUE)
        );

        jPanel3.add(jPanel2, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
// TODO add your handling code here:
    dragPane.stop();
    dispose();
}//GEN-LAST:event_jButton2ActionPerformed

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
// TODO add your handling code here:
    JRequest req = page.getRequest();
    if (req.isEmpty() && writeArea.getSelectedIndex() == 1) {
        JOptionPane.showConfirmDialog(this, java.util.ResourceBundle.getBundle("main").getString("isd_there_is_nothing_to_write"),
                java.util.ResourceBundle.getBundle("main").getString("isd_write_image"), JOptionPane.NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return;
    }
    JFileChooser fdlg = new JFileChooser();
    fdlg.setDialogType(JFileChooser.SAVE_DIALOG);
    String fs = format.getSelectedItem().toString();
    fdlg.setFileFilter(new FileNameExtensionFilter(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("main").getString("isd_filter_file"), new Object[] {fs}), fs));
    fdlg.setDialogTitle(java.util.ResourceBundle.getBundle("main").getString("isd_write_as_image_title"));
    if (fdlg.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
        File f = fdlg.getSelectedFile();
        String nm = f.getName();
        if (!nm.contains("." + fs)) {
            nm += "." + fs; //NOI18N //NOI18N //NOI18N //NOI18N //NOI18N //NOI18N
            f = new File(f.getParent() + File.separator + nm);
        }
        if (f.exists()) {
            if (JOptionPane.showConfirmDialog(this, f.getName() + java.util.ResourceBundle.getBundle("main").getString("asd_is_already_exists_confirm_overwrite"), java.util.ResourceBundle.getBundle("main").getString("isd_confirm_overwrite"),
                    JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) != JOptionPane.YES_OPTION) {
                return;
            }
        }
        saveImage(f, fs);
    }
}//GEN-LAST:event_jButton1ActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
// TODO add your handling code here:
    dragPane.stop();
    dispose();
}//GEN-LAST:event_formWindowClosing
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner dpi;
    private javax.swing.JComboBox format;
    private jtools.jcontrol.JDIntegerTextField height;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSpinner quality;
    private javax.swing.JCheckBox transparent;
    private jtools.jcontrol.JDIntegerTextField width;
    private javax.swing.JComboBox writeArea;
    // End of variables declaration//GEN-END:variables
}
