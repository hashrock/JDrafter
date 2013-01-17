/*
 * JColorChanger.java
 *
 * Created on 2007/11/14, 16:09
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jui.color;

import jactions.JUndoRedoEvent;
import jactions.JUndoRedoListener;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import jedit.paintedit.JChangeFillPaintEdit;
import jedit.paintedit.JChangeFillPaintsEdit;
import jedit.paintedit.JChangeStrokeEdit;
import jedit.paintedit.JChangeStrokePaintEdit;
import jedit.paintedit.JChangeStrokePaintsEdit;
import jobject.JColorable;
import jobject.JLeaf;
import jobject.JObject;
import jpaint.JPaint;
import jpaint.JStroke;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;
import jui.JIcons;
import jui.JStrokeChanger;
import jobject.JText;

/**
 *オブジェクトの塗り、ストロークを変更するためのコントロールです.
 *
 * @author i002060
 */
public class JColorChanger extends JPanel implements ItemListener, ChangeListener, JUndoRedoListener {

    /**塗りを設定するモードです。DrawObjectの塗りを更新します. */
    public static final int FILLMODE = 0;
    /**ストロークの色を設定するモードです*/
    public static final int STROKEMODE = 1;
    private int mode = FILLMODE;
    /**v*/
    private JDocumentViewer viewer = null;
    private boolean hasCommonFillPaint = true;
    private boolean hasCommonStrokePaint = true;
    private JPaint currentFillPaint = null;
    private JPaint currentStrokePaint = null;
    private Paint cTexture = null;
    private static GeneralPath outline = null;
    private static GeneralPath shade = null;
    private static Rectangle fillRect = null;
    private static Rectangle strokeRect = null;
    private static Rectangle innerRect = null;
    private static Rectangle defaultRect = null;
    private JDColorPanel colorPanel = null;
    private JDGradientPanel gradPanel = null;
    //
    private colorControl cctl;
    private gradControl gctl;
    private textureControl tctl;
    private nullControl nctl;

    /** Creates a new instance of JColorChanger */
    public JColorChanger(JDocumentViewer viewer) {
        Dimension dm = new Dimension(42, 60);
        colorPanel = new JDColorPanel(this);
        gradPanel = new JDGradientPanel();

        this.setPreferredSize(dm);
        this.setMaximumSize(dm);
        this.setMinimumSize(dm);
        if (JDPaintPreview.cTexture == null) {
            JDPaintPreview.createCImage();
        }
        cTexture = new TexturePaint(JDPaintPreview.cImage, new Rectangle(0, 0, 16, 16));
        outline = new GeneralPath();
        outline.moveTo(28, 0);
        outline.lineTo(28, 14);
        outline.moveTo(42, 14);
        outline.lineTo(42, 42);
        outline.lineTo(14, 42);
        outline.moveTo(15, 28);
        outline.lineTo(0, 28);
        shade = new GeneralPath();
        shade.moveTo(0, 28);
        shade.lineTo(0, 0);
        shade.lineTo(28, 0);
        shade.moveTo(28, 14);
        shade.lineTo(42, 14);
        shade.moveTo(14, 42);
        shade.lineTo(14, 28);
        outline.closePath();
        fillRect = new Rectangle(1, 1, 26, 26);
        strokeRect = new Rectangle(15, 15, 26, 26);
        innerRect = new Rectangle(23, 23, 10, 10);
        defaultRect = new Rectangle(2, 30, 10, 10);
        this.viewer = viewer;
        if (viewer != null) {
            viewer.getDocument().addItemListener(this);
            setupPaints();
        }
        MouseAdapter madp = new MouseAdapter() {

            public void mouseMoved(MouseEvent e) {
                mMoved(e);
            }

            public void mouseClicked(MouseEvent e) {
                mClicked(e);
            }
        };
        this.addMouseListener(madp);
        this.addMouseMotionListener(madp);
        colorPanel.addChangeListener(this);
       gradPanel.addChangeListener(this);
        gctl = new gradControl();
        cctl = new colorControl();
        tctl = new textureControl();
        nctl = new nullControl();
        this.setLayout(null);
        this.add(cctl);
        cctl.setBounds(0, 44, 12, 12);
        this.add(gctl);
        gctl.setBounds(14, 44, 12, 12);
        this.add(tctl);
        tctl.setBounds(28, 44, 12, 12);
        this.add(nctl);
        //nctl.setBounds(28,44,12,12);
        nctl.setBounds(32, 0, 12, 12);


    }

    public JColorChanger() {
        this(null);
    }

    public void setViewer(JDocumentViewer view) {
        if (viewer == view) {
            return;
        }
        if (viewer != null) {
            viewer.getDocument().removeItemListener(this);
            viewer.getDocument().removeUndoRedoListener(this);
        }
        viewer = view;
        if (viewer != null) {
            viewer.getDocument().addItemListener(this);
            viewer.getDocument().addUndoRedoListener(this);
        }
    }

    /**選択オブジェクトから共通の塗りを抽出し、カレントの塗りを設定します.*/
    public void setupPaints() {
        if (viewer == null) {
            return;
        }
        JRequest req = viewer.getCurrentRequest();
        JEnvironment env = viewer.getEnvironment();
        Vector<JLeaf> vec = new Vector<JLeaf>();
        if (JEnvironment.SAVED_PATTERN == null || mode ==STROKEMODE) {
            tctl.setEnabled(false);
        } else {
            tctl.setEnabled(true);
        }
        for (int i = 0; i < req.size(); i++) {
            Object o = req.get(i);
            if (o instanceof JObject) {
                getLeafObjects(vec, (JObject) o);
            } else if ((o instanceof JLeaf) && (o instanceof JColorable)) {
                vec.add((JLeaf) o);
            }
        }
        if (vec.isEmpty()) {
            currentFillPaint = JEnvironment.currentFill;
            currentStrokePaint = JEnvironment.currentBorder;
            hasCommonFillPaint = true;
            hasCommonStrokePaint = true;
        } else {
            boolean fl = true;
            hasCommonFillPaint = true;
            hasCommonStrokePaint = true;
            currentFillPaint = null;
            currentStrokePaint = null;
            for (int i = 0; i < vec.size(); i++) {
                Object o = vec.get(i);
                if (o instanceof JLeaf && o instanceof JColorable) {
                    JLeaf jl = (JLeaf) o;
                    if (fl) {
                        hasCommonFillPaint = hasCommonStrokePaint = true;
                        currentFillPaint = jl.getFillPaint();
                        currentStrokePaint = jl.getStrokePaint();
                        fl = false;
                    } else {
                        if (hasCommonFillPaint) {
                            if (currentFillPaint == null) {
                                hasCommonFillPaint = (jl.getFillPaint() == null);
                            } else {
                                hasCommonFillPaint = currentFillPaint.equals(jl.getFillPaint());
                            }
                        }
                        if (hasCommonStrokePaint) {
                            if (currentStrokePaint == null) {
                                hasCommonStrokePaint = (jl.getStrokePaint() == null);
                            } else {
                                hasCommonStrokePaint = (currentStrokePaint.equals(jl.getStrokePaint()));
                            }
                        }
                    }
                }
            }
        }
        adjustGradient();
        repaint();
    }

    private void adjustGradient() {
        if (currentFillPaint == null || currentFillPaint.getPaintMode() == JPaint.COLOR_MODE || currentFillPaint.getPaintMode() == JPaint.PATTERN_MODE) {
            return;
        }
        JPaint cp = currentFillPaint;
        float x1, y1, x2, y2;
        if (cp.getPaintMode() == JPaint.LINEAR_GRADIENT_MODE) {
            x1 = fillRect.x;
            y1 = y2 = fillRect.y + fillRect.height / 2;
            x2 = fillRect.x + fillRect.width;
        } else {
            x1 = fillRect.x + fillRect.width / 2;
            y2 = y1 = fillRect.y + fillRect.height / 2;
            x2 = fillRect.x + fillRect.width;
        }
        try {
            currentFillPaint = new JPaint(cp.getPaintMode(), x1, y1, x2, y2, cp.getFracs(), cp.getColors());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        ImageIcon img = JIcons.DEFAULT_COLOR;
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(0f, 0f, 0f, 0.4f));
        g2.draw(shade);
        g2.setColor(new Color(1f, 1f, 1f, 0.8f));
        g2.draw(outline);
        if (mode == FILLMODE) {
            paintStroke(g2);
            paintFill(g2);
        } else {
            paintFill(g2);
            paintStroke(g2);
        }
        img.paintIcon(this, g, 2, 30);
    }

    private void paintFill(Graphics2D g) {
        g.setPaint(cTexture);
        g.fill(fillRect);
        g.setStroke(new BasicStroke(0f));
        if (hasCommonFillPaint) {
            if (currentFillPaint != null) {
                g.setPaint(currentFillPaint);
                if (currentFillPaint.getPaintMode() == JPaint.PATTERN_MODE) {
                    Graphics2D gg = (Graphics2D) g.create();
                    gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Rectangle2D r = (Rectangle2D)currentFillPaint.getClip().clone();
                    double sx = Math.min( fillRect.width/r.getWidth() , fillRect.getHeight()/r.getHeight() );
                    gg.scale(sx, sx);
                    gg.translate(fillRect.x,fillRect.y);
                    gg.fill(r);
                    gg.dispose();
                } else {
                    g.fill(fillRect);
                }

            } else {
                g.setColor(Color.WHITE);
                g.fill(fillRect);
                g.setColor(Color.RED);
                g.setStroke(new BasicStroke(2f));
                g.drawLine(fillRect.x + fillRect.width - 1, fillRect.y + 1, fillRect.x + 1, fillRect.y + fillRect.height - 1);
                g.setStroke(new BasicStroke(0f));
            }
            g.setColor(Color.BLACK);
            g.draw(fillRect);
            g.setColor(Color.WHITE);
            g.drawRect(fillRect.x + 1, fillRect.y + 1, fillRect.width - 2, fillRect.height - 2);
        } else {
            g.setColor(this.getBackground());
            g.fill(fillRect);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.BLACK);
            g.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
            g.drawString("?", fillRect.x + 9, fillRect.y + 18);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
            g.draw(fillRect);

        }
    }

    private void paintStroke(Graphics2D g) {
        g.setStroke(new BasicStroke(0f));
        g.setPaint(cTexture);
        g.fill(strokeRect);
        if (hasCommonStrokePaint) {
            if (currentStrokePaint != null) {
                g.setPaint(currentStrokePaint);
                g.fill(strokeRect);
            } else {
                g.setColor(Color.WHITE);
                g.fill(strokeRect);
                g.setStroke(new BasicStroke(2f));
                g.setColor(Color.RED);
                g.drawLine(strokeRect.x + strokeRect.width - 1, strokeRect.y + 1, strokeRect.x + 1, strokeRect.y + strokeRect.height - 1);
                g.setStroke(new BasicStroke(0f));
            }
        } else {
            g.setColor(this.getBackground());
            g.fill(strokeRect);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.BLACK);
            g.setFont(new Font(Font.DIALOG, Font.BOLD, 8));
            g.drawString("?", strokeRect.x + 2, strokeRect.y + 8);
            g.drawString("?", strokeRect.x + strokeRect.width - 6, strokeRect.y + 8);
            g.drawString("?", strokeRect.x + strokeRect.width - 6, strokeRect.y + strokeRect.height - 2);
            g.drawString("?", strokeRect.x + 2, strokeRect.y + strokeRect.height - 2);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
        }
        g.setColor(this.getBackground());
        g.fill(innerRect);
        g.setColor(Color.BLACK);
        g.draw(innerRect);
        g.draw(strokeRect);
        if (currentStrokePaint != null) {
            g.setColor(Color.WHITE);
            g.drawRect(strokeRect.x + 1, strokeRect.y + 1, strokeRect.width - 2, strokeRect.height - 2);
            g.drawRect(innerRect.x - 1, innerRect.y - 1, innerRect.width + 2, innerRect.height + 2);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        setupPaints();
    }

    @Override
    public void undoRedoEventHappened(JUndoRedoEvent e) {
        setupPaints();
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
        repaint();
    }

    public void setFillPaint(JPaint p) {
        currentFillPaint = p;
        if (p != null && p.getPaintMode() != JPaint.COLOR_MODE) {
            adjustGradient();
        }
        hasCommonFillPaint = true;

        repaint();
    }

    public JPaint getFillPaint() {
        return currentFillPaint;
    }

    public void setStrokePaint(JPaint p) {
        currentStrokePaint = p;
        hasCommonStrokePaint = true;
        repaint();
    }

    public JPaint getStrokePaint() {
        return currentStrokePaint;
    }

    public boolean hasCommonFillPaint() {
        return hasCommonFillPaint;
    }

    public boolean hasCommonStrokePaint() {
        return hasCommonStrokePaint;
    }

    public void setCommonFillPaint(boolean b) {
        hasCommonFillPaint = b;
        repaint();
    }

    public void setCommonStrokePaint(boolean b) {
        hasCommonStrokePaint = b;
        repaint();
    }

    private void mMoved(MouseEvent e) {
        Point p = e.getPoint();
        String cm = null;
        if (defaultRect.contains(p)) {
            cm = "デフォルトの塗りと線";
        }
        if (mode == FILLMODE) {
            if (fillRect.contains(p)) {
                cm = "塗り(クリックして編集)";
            } else if (strokeRect.contains(p)) {
                cm = "線の色(クリックしてアクティブ)";
            }
        } else {
            if (strokeRect.contains(p)) {
                cm = "線の色(クリックして編集)";
            } else if (fillRect.contains(p)) {
                cm = "塗り(クリックしてアクティブ)";
            }
        }
        if (cm != null) {
            if (!cm.equals(getToolTipText())) {
                setToolTipText(cm);
            }
        } else {
            if (getToolTipText() != null) {
                setToolTipText(null);
            }
        }
    }

    private void mClicked(MouseEvent e) {
        Point p = e.getPoint();
        if (defaultRect.contains(p)) {
            if (viewer == null) {
                return;
            }
            Vector<JLeaf> leafs = JStrokeChanger.getSelectedLeafs(viewer.getCurrentRequest());
            CompoundEdit cEdit = null;
            UndoableEdit edt = null;
            for (int i = 0; i < leafs.size(); i++) {
                JLeaf jl = leafs.get(i);
                JPaint c = JEnvironment.DEFAULT_FILL;
                if (jl instanceof JText) {
                    c = JEnvironment.DEFAULT_TEXT_FILL;
                }
                edt = null;
                if ((c == null && jl.getFillPaint() != null) || (c != null && !c.equals(jl.getFillPaint()))) {
                    edt = new JChangeFillPaintEdit(viewer, jl, c);
                }
                if (edt != null) {
                    if (cEdit == null) {
                        cEdit = new CompoundEdit();
                    }
                    cEdit.addEdit(edt);
                }
                c = JEnvironment.DEFAULT_BORDER;
                if (jl instanceof JText) {
                    c = JEnvironment.DEFAULT_TEXT_BORDER;
                }
                edt = null;
                if ((c == null && jl.getStrokePaint() != null) || (c != null && !c.equals(jl.getStrokePaint()))) {
                    edt = new JChangeStrokePaintEdit(viewer, jl, c);
                }
                if (edt != null) {
                    if (cEdit == null) {
                        cEdit = new CompoundEdit();
                    }
                    cEdit.addEdit(edt);
                }
                JStroke s = JEnvironment.DEFAULT_STROKE;
                edt = null;
                if (!s.equals(jl.getStroke())) {
                    edt = new JChangeStrokeEdit(viewer, jl, s);
                }
                if (edt != null) {
                    if (cEdit == null) {
                        cEdit = new CompoundEdit();
                    }
                    cEdit.addEdit(edt);
                }
            }
            currentFillPaint = JEnvironment.currentBorder = JEnvironment.DEFAULT_BORDER;
            currentStrokePaint = JEnvironment.currentFill = JEnvironment.DEFAULT_FILL;
            JEnvironment.currentStroke = JEnvironment.DEFAULT_STROKE;
            if (cEdit != null) {
                cEdit.end();
                viewer.getDocument().fireUndoEvent(cEdit);
                viewer.repaint();
            }
            viewer.getCurrentRequest().fireChangeEvent(null, ItemEvent.DESELECTED);
            repaint();
            return;
        }
        boolean fContain = fillRect.contains(p);
        boolean sContain = strokeRect.contains(p);
        if (mode == FILLMODE) {
            if (fContain) {
                
                if (currentFillPaint == null || currentFillPaint.getPaintMode() == JPaint.COLOR_MODE) {
                    if (currentFillPaint != null) {
                        colorPanel.setCurrentColor(currentFillPaint.getColor());
                        colorPanel.setPrevColor(currentFillPaint.getColor());
                    } else {
                        colorPanel.setCurrentColor(null);
                        colorPanel.setPrevColor(null);
                    }
                    colorPanel.setAllowNull(false);
                    colorPanel.showAsPopup(this);
                } else if (currentFillPaint.getPaintMode() != JPaint.PATTERN_MODE) {
                    gradPanel.setPaint(currentFillPaint.getGradient());
                    gradPanel.showAsPopup(this);
                }
            } else if (sContain) {
                mode = STROKEMODE;
                gctl.setEnabled(false);
                tctl.setEnabled(false);
                repaint();
                return;
            }
        } else {
            if (sContain) {
                if (currentStrokePaint != null) {
                    colorPanel.setCurrentColor(currentStrokePaint.getColor());
                    colorPanel.setPrevColor(currentStrokePaint.getColor());
                } else {
                    colorPanel.setCurrentColor(null);
                    colorPanel.setPrevColor(null);
                }
                colorPanel.setAllowNull(false);
                colorPanel.showAsPopup(this);
            } else if (fContain) {
                mode = FILLMODE;
                gctl.setEnabled(true);
                if (JEnvironment.SAVED_PATTERN !=null){
                    tctl.setEnabled(true);
                }
                repaint();
                return;
            }
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JPaint jp = null;
        if (e.getSource() == colorPanel && colorPanel.isDesided()) {
            if (colorPanel.getDisidedColor() != null) {
                jp = new JPaint(colorPanel.getDisidedColor());
            }
            UndoableEdit edt = updatePaint(jp, mode);
            if (edt != null && viewer != null) {
                viewer.getDocument().fireUndoEvent(edt);
            }
            repaint();
        } else if (e.getSource() == gradPanel && gradPanel.isDesided()) {
            try {
                jp = new JPaint(gradPanel.getPaint());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            UndoableEdit edt = updatePaint(jp, mode);
            if (edt != null && viewer != null) {
                viewer.getDocument().fireUndoEvent(edt);
            }
            adjustGradient();
            repaint();
        }
    }

    public UndoableEdit updatePaint(JPaint paint, int pmode) {
        if (viewer == null) {
            return null;
        }
        Vector<JLeaf> vec = new Vector<JLeaf>();
        UndoableEdit ret = null;
        JRequest req = viewer.getCurrentRequest();
        for (int i = 0; i < req.size(); i++) {
            Object o = req.get(i);
            if (o instanceof JObject) {
                getLeafObjects(vec, (JObject) o);
            } else if ((o instanceof JLeaf) && (o instanceof JColorable)) {
                vec.add((JLeaf) o);
            }
        }
        if (pmode == FILLMODE) {
            JEnvironment.currentFill = currentFillPaint = paint;
            hasCommonFillPaint = true;
            if (!vec.isEmpty()) {
                ret = new JChangeFillPaintsEdit(viewer, vec, currentFillPaint);
            }
        } else {
            hasCommonStrokePaint = true;
            JEnvironment.currentBorder = currentStrokePaint = paint;
            if (!vec.isEmpty()) {
                ret = new JChangeStrokePaintsEdit(viewer, vec, currentStrokePaint);
            }
        }
        viewer.repaint();
        return ret;
    }

    public static void getLeafObjects(Vector<JLeaf> vec, JObject o) {
        for (int i = 0; i < o.size(); i++) {
            Object obj = o.get(i);
            if (obj instanceof JObject) {
                getLeafObjects(vec, (JObject) obj);
            } else if (obj instanceof JColorable) {
                vec.add((JLeaf) obj);
            }
        }
    }

    private JColorChanger getThis() {
        return this;
    }

    protected class gradControl extends JComponent implements ChangeListener {

        private boolean pushed = false;
        private boolean hovered = false;
        private GeneralPath shine = new GeneralPath(),  shade = new GeneralPath();
        protected Rectangle bounds = new Rectangle(2, 2, 7, 7);
        private Color lColor = new Color(1f, 1f, 1f, 0.8f);
        private Color sColor = new Color(0f, 0f, 0f, 0.5f);
        protected JPaint paint;

        public gradControl() {
            Dimension d = new Dimension(12, 12);
            this.setPreferredSize(d);
            this.setMaximumSize(d);
            this.setMinimumSize(d);
            this.setToolTipText("グラデーション");
            shine.moveTo(0, 11);
            shine.lineTo(0, 0);
            shine.lineTo(11, 0);
            shade.moveTo(11, 0);
            shade.lineTo(11, 11);
            shade.lineTo(0, 11);
            this.setEnabled(true);
            MouseAdapter madp = new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    mEntered(e);
                }

                public void mouseExited(MouseEvent e) {
                    mExited(e);
                }

                public void mouseDragged(MouseEvent e) {
                    mDragged(e);
                }

                public void mousePressed(MouseEvent e) {
                    mPressed(e);
                }

                public void mouseReleased(MouseEvent e) {
                    mReleased(e);

                }
            };
            this.addMouseListener(madp);
            this.addMouseMotionListener(madp);
            try {
                paint = adjustGradation(new JPaint(gradPanel.getPaint()), bounds);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            gradPanel.addChangeListener(this);
            colorPanel.addChangeListener(this);
        }

        protected void paintThis(Graphics2D g) {
            g.setPaint(paint);
            g.fill(bounds);
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(this.getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());
            if (!isEnabled()) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            }
            g2.setStroke(new BasicStroke(0f));
            if (pushed) {
                g2.setColor(sColor);
                g2.draw(shine);
                g2.setColor(lColor);
                g2.draw(shade);
            } else if (hovered) {
                g2.setColor(lColor);
                g2.draw(shine);
                g2.setColor(sColor);
                g2.draw(shade);
            }
            paintThis(g2);
            g2.setColor(Color.BLACK);
            g2.draw(bounds);
        }

        private void mEntered(MouseEvent e) {
            if (!isEnabled()) {
                return;
            }
            hovered = true;
            repaint();

        }

        private void mExited(MouseEvent e) {
            if (!isEnabled()) {
                return;
            }
            hovered = false;
            repaint();

        }

        private void mPressed(MouseEvent e) {
            if (!isEnabled()) {
                return;
            }
            pushed = true;
            repaint();
        }

        private void mDragged(MouseEvent e) {
            if (!isEnabled()) {
                return;
            }
            Point p = e.getPoint();
            Rectangle br = new Rectangle(0, 0, getWidth(), getHeight());
            pushed = br.contains(p);
            repaint();
        }

        private void mReleased(MouseEvent e) {
            if (!isEnabled()) {
                return;
            }
            if (pushed) {
                pushed = false;
                performed();
                repaint();
            }

        }

        private JPaint adjustGradation(JPaint p, Rectangle2D r) {
            if (p == null || p.getPaintMode() == JPaint.COLOR_MODE || p.getPaintMode() == JPaint.PATTERN_MODE) {
                try {
                    p = new JPaint(JPaint.LINEAR_GRADIENT_MODE, (float) r.getX(), (float) r.getY(),
                            (float) (r.getX() + r.getWidth()), (float) r.getY(), new float[]{0f, 1f}, new Color[]{Color.BLACK, Color.WHITE});
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }





            float sx, sy, ex, ey;
            if (p.getPaintMode() == JPaint.LINEAR_GRADIENT_MODE) {
                sx = (float) r.getX();
                sy = ey = (float) (r.getY() + r.getHeight() / 2);
                ex = sx + (float) r.getWidth();
            } else {
                sx = (float) r.getCenterX();
                sy = ey = (float) r.getCenterY();
                ex = sx + (float) (r.getWidth() / 2);
            }
            JPaint ret = null;
            try {
                ret = new JPaint(p.getPaintMode(), sx, sy, ex, ey, p.getFracs(), p.getColors());
            } catch (Exception es) {
                es.printStackTrace();
            }
            return ret;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            if (e.getSource() == gradPanel && gradPanel.desided) {
                try {
                    this.paint = adjustGradation(new JPaint(gradPanel.getPaint()), bounds);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }

        protected void performed() {
            if (!paint.equals(currentFillPaint) && mode == FILLMODE && viewer != null) {
                UndoableEdit edt = updatePaint(paint, FILLMODE);
                if (edt != null) {
                    viewer.getDocument().fireUndoEvent(edt);
                }
                adjustGradient();
                getThis().repaint();
            }
        }
    }

    protected class colorControl extends gradControl {

        private JPaint fillPaint = JEnvironment.DEFAULT_FILL;
        private JPaint strokePaint = JEnvironment.DEFAULT_BORDER;

        public colorControl() {
            this.setToolTipText("カラー");
        }

        public void stateChanged(ChangeEvent e) {
            if (e.getSource() == colorPanel && colorPanel.isDesided()) {
                if (colorPanel.getDisidedColor() == null) {
                    return;
                }
                JPaint p = new JPaint(colorPanel.getDisidedColor());
                if (mode == FILLMODE) {
                    fillPaint = p;
                } else {
                    strokePaint = p;
                }
                repaint();
            }
        }

        protected void performed() {
            UndoableEdit edt = null;
            if (mode == FILLMODE) {
                if (!fillPaint.equals(currentFillPaint) || !hasCommonFillPaint) {
                    edt = updatePaint(fillPaint, FILLMODE);
                }

            } else if (!strokePaint.equals(currentStrokePaint)) {
                edt = updatePaint(strokePaint, STROKEMODE);
            }
            if (edt != null) {
                viewer.getDocument().fireUndoEvent(edt);
            }
            getThis().repaint();

        }

        protected void paintThis(Graphics2D g) {
            if (mode == FILLMODE) {
                g.setPaint(fillPaint);
            } else {
                g.setPaint(strokePaint);
            }
            g.fill(bounds);
            g.setPaint(Color.BLACK);
            g.draw(bounds);
        }
    }

    protected class nullControl extends gradControl {

        private JPaint fillPaint = JEnvironment.DEFAULT_FILL;
        private JPaint strokePaint = JEnvironment.DEFAULT_BORDER;

        public nullControl() {
            this.setToolTipText("塗り/線色なし");
        }

        public void stateChanged(ChangeEvent e) {
        }

        protected void performed() {
            if (mode == FILLMODE) {
                if (currentFillPaint != null) {
                    UndoableEdit edt = updatePaint(null, FILLMODE);
                    if (edt != null && viewer != null) {
                        viewer.getDocument().fireUndoEvent(edt);
                    }
                }
            } else {
                if (currentStrokePaint != null) {
                    UndoableEdit edt = updatePaint(null, STROKEMODE);
                    if (edt != null && viewer != null) {
                        viewer.getDocument().fireUndoEvent(edt);
                    }
                }
            }
            getThis().repaint();

        }

        protected void paintThis(Graphics2D g) {
            g.setColor(Color.WHITE);
            g.fill(bounds);
            g.setColor(Color.RED);
            g.drawLine(bounds.x + bounds.width - 1, bounds.y + 1, bounds.x + 1, bounds.y + bounds.height - 1);
            g.setColor(Color.BLACK);
            g.draw(bounds);
        }
    }

    protected class textureControl extends gradControl {

        JPaint tPaint = null;

        public textureControl() {
            this.setToolTipText("パターン");
            setEnabled(false);
        }

        @Override
        public void stateChanged(ChangeEvent e) {
        }

        @Override
        public void performed() {
            if (mode == FILLMODE && viewer != null) {
                UndoableEdit edt = updatePaint(JEnvironment.SAVED_PATTERN, FILLMODE);
                if (edt != null) {
                    viewer.getDocument().fireUndoEvent(edt);
                }
                getThis().repaint();
            }
        }

        protected void paintThis(Graphics2D g) {
            g.setColor(Color.darkGray);
            g.fillRect(2, 2, 4, 4);
            g.fillRect(6, 6, 4, 4);
        }
    }
}
