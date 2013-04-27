/*
 * JPageSelecter.java
 *
 * Created on 2007/12/07, 15:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jui;

import jactions.JUndoRedoEvent;
import jactions.JUndoRedoListener;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.RepaintManager;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import jobject.JDocument;
import jobject.JPage;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;

/**
 *
 * @author i002060
 */
public class JPageSelecter extends JComponent implements JUndoRedoListener, Scrollable, ChangeListener {

    private JDocument doc = null;
    private HashMap<JPage, JPagePreviewer> map;
    private int xInterval = 8;
    private int yInterval = 8;
    private int previewWidth = 120,  previewHeight = 120;
    private int columnCount = 1;
    private boolean deactivateEvent = false;
    private Point startPoint = null;
    private Point currentPoint = null;
    private JPage draggingPage = null;

    /** Creates a new instance of JPageSelecter */
    public JPageSelecter(JDocument doc) {
        this.doc = doc;
        setDocument(doc);

        MouseAdapter madp = new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                mPressed(e);
            }

            public void mouseDragged(MouseEvent e) {
                mDragged(e);
            }

            public void mouseReleased(MouseEvent e) {
                mReleased(e);
            }
        };
        this.addMouseListener(madp);
        this.addMouseMotionListener(madp);
    }

    public JPageSelecter() {
        this(null);
    }

    public void setDocument(JDocument dc) {
        map = new HashMap<JPage, JPagePreviewer>();
        if (doc != null) {
            doc.getViewer().getScroller().getViewport().removeChangeListener(this);
            doc.removeUndoRedoListener(this);
        }
        doc = dc;
        if (doc != null) {
            doc.getViewer().getScroller().getViewport().addChangeListener(this);
            doc.addUndoRedoListener(this);
            for (int i = 0; i < doc.size(); i++) {
                JPagePreviewer preview = new JPagePreviewer(doc.get(i), this);
                preview.setWidth(previewWidth);
                preview.setHeight(previewHeight);
                map.put(doc.get(i), preview);
            }
        }
        adjustSize();
    }

    private Dimension adjustSize() {
        int w = Math.max(previewWidth + xInterval * 2, getWidth());
        int h = Math.max(previewHeight + yInterval * 2, getVisibleRect().height);
        columnCount = (w - xInterval) / (previewWidth + xInterval);
        if (columnCount > map.size()) {
            columnCount = map.size();
        }
        columnCount = Math.max(1, columnCount);
        int rowCount = map.size() / columnCount;
        if ((map.size() % columnCount) != 0) {
            rowCount++;
        }
        h = yInterval + (previewHeight + yInterval) * rowCount;
        if (doc != null) {
            int ix = 0, iy = 0;
            for (int i = 0; i < doc.size(); i++) {
                JPagePreviewer jp = map.get(doc.get(i));
                jp.setX(ix * (previewWidth + xInterval) + xInterval);
                jp.setY(iy * (previewHeight + yInterval) + yInterval);
                ix++;
                if (ix >= columnCount) {
                    ix = 0;
                    iy++;
                }
            }
        }
        JViewport vp = (JViewport) getParent();
        if (vp != null) {
            if (h < vp.getViewRect().height) {
                h = vp.getViewRect().height;
            }
        }
        if (w < 120) {
            w = 120;
        }
        if (h < 240) {
            h = 240;
        }
        Dimension d = new Dimension(w, h);
        this.setPreferredSize(d);
        this.setSize(d);
        return d;
    }

    private void setPage(JPage page) {
        if (doc.getCurrentPage() != page) {
            doc.setCurrentPage(page);
        }
    }

    private void mPressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            return;
        }
        Point p = e.getPoint();
        JPage jp = null;
        Iterator<JPagePreviewer> it = map.values().iterator();
        while (it.hasNext()) {
            JPagePreviewer pr = it.next();
            if (pr.getBounds().contains(p)) {
                jp = pr.getPage();
                break;
            }
        }
        if (jp != null) {
            setPage(jp);
            JDocumentViewer viewer = doc.getViewer();
            viewer.adjustSize();
            viewer.getScroller().adjustSize();
            viewer.isDraftMode = false;
            RepaintManager.currentManager(viewer).markCompletelyDirty(viewer);
            RepaintManager.currentManager(viewer.getScroller()).markCompletelyDirty(viewer.getScroller());
            RepaintManager.currentManager(viewer).paintDirtyRegions();
            JPagePreviewer prev = map.get(jp);
            p.x -= prev.getX();
            p.y -= prev.getY();
            try {
                AffineTransform tx = prev.getTransform().createInverse();
                tx.transform(p, p);
                viewer.getEnvironment().getToScreenTransform().transform(p, p);
                Rectangle vr = viewer.getVisibleRect();
                int vx = Math.max(p.x - vr.width / 2, 0);
                int vy = Math.max(p.y - vr.height / 2, 0);
                if (vx > viewer.getWidth() - vr.width) {
                    vx = viewer.getWidth() - vr.width;
                }
                if (vy > viewer.getHeight() - vr.height) {
                    vy = viewer.getHeight() - vr.height;
                }
                viewer.getScroller().getViewport().setViewPosition(new Point(vx, vy));
                viewer.getEnvironment().addClip(viewer.getEnvironment().getPaperRect());
                viewer.isDraftMode = false;
                RepaintManager.currentManager(viewer).markCompletelyDirty(viewer);
                RepaintManager.currentManager(viewer.getScroller()).markCompletelyDirty(viewer.getScroller());
                RepaintManager.currentManager(viewer).paintDirtyRegions();
            } catch (NoninvertibleTransformException ex) {
                ex.printStackTrace();
            }
            startPoint = e.getPoint();
            draggingPage = jp;
            repaint();

        }
    }

    private void mDragged(MouseEvent e) {
        if (startPoint != null) {
            currentPoint = e.getPoint();
        } else {
            draggingPage = null;
            currentPoint = null;
        }
        if (this.getCursor() != JEnvironment.MOUSE_CURSOR.GRIP) {
            this.setCursor(JEnvironment.MOUSE_CURSOR.GRIP);
        }
        Point p = e.getPoint();
        Rectangle rv = new Rectangle(p.x, p.y, 1, 1);
        this.scrollRectToVisible(rv);
        repaint();
    }

    private void mReleased(MouseEvent e) {
        if (draggingPage != null && currentPoint != null) {
            int idx = insertIndex(draggingPage, currentPoint);
            if (idx != -1) {
                int cIdx = doc.indexOf(draggingPage);
                if (idx > cIdx) {
                    idx--;
                }
                doc.remove(draggingPage);
                doc.add(idx, draggingPage);
            }
        }
        startPoint = null;
        currentPoint = null;
        draggingPage = null;
        this.setCursor(Cursor.getDefaultCursor());
        repaint();

    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        Graphics2D g2 = (Graphics2D) g;
        if (doc == null) {
            return;
        }
        Graphics2D gc = (Graphics2D) g2.create();
        AffineTransform af = (AffineTransform) gc.getTransform().clone();
        JPagePreviewer jp = null;
        for (int i = 0; i < doc.size(); i++) {
            jp = map.get(doc.get(i));
            gc.translate(jp.getX(), jp.getY());
            jp.paint(gc);
            gc.setTransform(af);
        }
        gc.dispose();
        if (draggingPage != null && currentPoint != null) {
            jp = map.get(draggingPage);
            Image img = jp.getImage();
            int x = jp.getX() + (currentPoint.x - startPoint.x);
            int y = jp.getY() + (currentPoint.y - startPoint.y);
            Shape hit = hitRect(draggingPage, currentPoint);
            if (hit != null) {
                g2.setColor(Color.BLACK);
                g2.fill(hit);
            }
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
            g2.drawImage(img, x, y, this);
            g2.setColor(Color.ORANGE);
            g2.drawRect(x, y, img.getWidth(this), img.getHeight(this));


        }
    }

    public int insertIndex(JPage dragPage, Point p) {
        int ret = -1;
        Rectangle r = new Rectangle();
        int idx = doc.indexOf(dragPage) + 1;
        for (int i = 0; i < doc.size(); i++) {
            JPage pg = doc.get(i);
            if (pg == dragPage) {
                continue;
            }
            JPagePreviewer jp = map.get(pg);
            r.setFrame(jp.getX() - xInterval, jp.getY(), xInterval, jp.getHeight());
            if (r.contains(p) && i != idx) {
                ret = i;
                break;
            }
            r.setFrame(jp.getX(), jp.getY() - yInterval, jp.getWidth(), yInterval);
            if (r.contains(p) && i != idx) {
                ret = i;
                break;
            }
            r.setFrame(jp.getX() + jp.getWidth(), jp.getY(), xInterval, jp.getHeight());
            if (r.contains(p) && i + 1 != idx) {
                ret = i + 1;
                break;
            }
            r.setFrame(jp.getX(), jp.getY() + jp.getHeight(), jp.getWidth(), yInterval);
            if (r.contains(p) && i + 1 != idx) {
                ret = i + 1;
                break;

            }
        }
        return ret;
    }

    private Shape hitRect(JPage dragPage, Point p) {
        Shape ret = null;
        Rectangle r = new Rectangle();
        int idx = doc.indexOf(dragPage);
        for (int i = 0; i < doc.size(); i++) {
            JPage pg = doc.get(i);
            if (pg == dragPage) {
                continue;
            }
            JPagePreviewer jp = map.get(pg);
            r.setFrame(jp.getX() - xInterval, jp.getY(), xInterval, jp.getHeight());
            if (r.contains(p) && i != idx && i != idx + 1) {
                ret = getSelectionShape(r);
                break;
            }
            r.setFrame(jp.getX(), jp.getY() - yInterval, jp.getWidth(), yInterval);
            if (r.contains(p) && i != idx && i != idx + 1) {
                ret = getSelectionShape(r);
                break;
            }
            r.setFrame(jp.getX() + jp.getWidth(), jp.getY(), xInterval, jp.getHeight());
            if (r.contains(p) && i + 1 != idx && i != idx) {
                ret = getSelectionShape(r);
                break;
            }
            r.setFrame(jp.getX(), jp.getY() + jp.getHeight(), jp.getWidth(), yInterval);
            if (r.contains(p) && i + 1 != idx && i != idx) {
                ret = getSelectionShape(r);
                break;

            }
        }
        return ret;
    }

    private Shape getSelectionShape(Rectangle r) {
        GeneralPath ret = new GeneralPath();
        int rs = (Math.min(r.width, r.height) - 2) / 2;
        if (r.width > r.height) {
            r.x -= rs;
            r.width += rs * 2;
            ret.moveTo(r.x, r.y);
            ret.lineTo(r.x + rs, r.y + rs);
            ret.lineTo(r.x + r.width - rs, r.y + rs);
            ret.lineTo(r.x + r.width, r.y);
            ret.lineTo(r.x + r.width, r.y + r.height);
            ret.lineTo(r.x + r.width - rs, r.y + r.height - rs);
            ret.lineTo(r.x + rs, r.y + r.height - rs);
            ret.lineTo(r.x, r.y + r.height);
            ret.lineTo(r.x, r.y);
            ret.closePath();
        } else {
            r.y -= rs;
            r.height += rs * 2;
            ret.moveTo(r.x, r.y);
            ret.lineTo(r.x + r.width, r.y);
            ret.lineTo(r.x + r.width - rs, r.y + rs);
            ret.lineTo(r.x + r.width - rs, r.y + r.height - rs);
            ret.lineTo(r.x + r.width, r.y + r.height);
            ret.lineTo(r.x, r.y + r.height);
            ret.lineTo(r.x + rs, r.y + r.height - rs);
            ret.lineTo(r.x + rs, r.y + rs);
            ret.lineTo(r.x, r.y);
            ret.closePath();
        }
        return ret;

    }

    public void undoRedoEventHappened(JUndoRedoEvent e) {
        if (map.size() != doc.size()) {
            map.clear();
            for (int i = 0; i < doc.size(); i++) {
                JPagePreviewer preview = new JPagePreviewer(doc.get(i), this);
                preview.setWidth(previewWidth);
                preview.setHeight(previewHeight);
                map.put(doc.get(i), preview);
            }
            adjustSize();
            doc.getViewer().adjustSize();
            doc.getViewer().isDraftMode = false;
            doc.getViewer().repaint();
        }
        map.get(doc.getCurrentPage()).isDraftMode = false;
        if (doc != null) {
            JPagePreviewer pr = map.get(doc.getCurrentPage());
            scrollRectToVisible(pr.getBounds());
        }
        repaint();
    //RepaintManager.currentManager(this).markCompletelyDirty(this);
    //RepaintManager.currentManager(this).paintDirtyRegions();
    }

    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 8;
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 8;
    }

    public boolean getScrollableTracksViewportWidth() {
        adjustSize();
        return true;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    public void stateChanged(ChangeEvent e) {
        if (doc != null) {
            JPagePreviewer pr = map.get(doc.getCurrentPage());
            scrollRectToVisible(pr.getBounds());
        }
        repaint();
    }
}
