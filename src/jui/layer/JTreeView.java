/*
 * JTreeView.java
 *
 * Created on 2008/05/10, 17:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jui.layer;

import jactions.JUndoRedoEvent;
import jactions.JUndoRedoListener;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentSkipListMap;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import jedit.layeredit.BringToIndexEdit;
import jedit.layeredit.SetLockEdit;
import jedit.layeredit.SetVisibleEdit;
import jobject.JImageObject;
import jobject.JLayer;
import jobject.JLeaf;
import jobject.JObject;
import jobject.JPage;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;
import jobject.JText;

/**
 *
 * @author takashi
 */
public class JTreeView extends JComponent implements JTreeModelListener, MouseListener, MouseMotionListener, ItemListener, JUndoRedoListener, Scrollable {

    private JPageTreeModel model = null;
    private static int ROWHEIGHT = 20;
    private static int ICONGAP = 4;
    private static int ARROWSIZE = 5;
    private static int TREEINDENT = 8;
    private static Color SELECTED_COLOR = new Color(0.2f, 0.4f, 1f);
    private static Color SELECTED_LAYER_COLOR = new Color(0.6f, 0.8f, 1f);
    private Vector<JLeaf> selectedLeafs = new Vector<JLeaf>();
    private boolean beDragging = false;
    //--------------------------------------------
    private DropTarget dropTarget = null;
    private Point startPoint = null;
    private Point currentPoint = null;
    private Rectangle dragPreviewRect = null;    //
    //
    private Dimension initialSize = new Dimension(200, ROWHEIGHT * 12);
    BasicArrowButton arrow = new BasicArrowButton(SwingConstants.NORTH, this.getBackground(), Color.GRAY, Color.DARK_GRAY, Color.WHITE);

    /** Creates a new instance of JTreeView */
    public JTreeView() {
        //this.setPreferredSize(initialSize);
        this.setSize(initialSize);
        this.setPreferredSize(initialSize);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

    }

    public void setTreeModel(JPageTreeModel model) {
        if (this.model != null) {
            this.model.removeTreeModelLsitener(this);
            this.model.getPage().getDocument().removeItemListener(this);
            this.model.getPage().getDocument().removeUndoRedoListener(this);
        }
        this.model = model;
        if (this.model != null) {
            this.model.addTreeModelListener(this);
            this.model.getPage().getDocument().addItemListener(this);
            this.model.getPage().getDocument().addUndoRedoListener(this);
        }
        repaint();
    }

    public void paintComponent(Graphics g) {
        if (model == null) {
            return;
        }
        JRequest req = model.getPage().getRequest();
        Graphics2D gc = (Graphics2D) g.create();
        for (int i = 1; i < model.getRowCount(); i++) {
            JLeaf jl = model.getJLeaForRow(i);
            int depth = model.depth(jl);
            boolean selected = selectedLeafs.contains(jl);
            paintRow(gc, jl, depth, req, selected);
            gc.translate(0, ROWHEIGHT);
        }
        gc.dispose();
        if (dragPreviewRect != null) {
            gc = (Graphics2D) g.create();
            AffineTransform tx = new AffineTransform();
            tx.setToTranslation(0, currentPoint.y - startPoint.y);
            Shape s = tx.createTransformedShape(dragPreviewRect);
            gc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
            gc.setColor(Color.LIGHT_GRAY);
            gc.setStroke(new BasicStroke(1f));
            gc.draw(s);
            if (dropTarget != null) {
                if (dropTarget.getIndex() != -1) {
                    int y = 0;
                    if (dropTarget.getParent().indexOf(dropTarget.draggedLeaf) == dropTarget.index) {
                        y = (model.indexOf(dropTarget.getDraggedLeaf())) * ROWHEIGHT;
                    } else {
                        y = (model.indexOf(dropTarget.getDraggedLeaf()) - 1) * ROWHEIGHT;
                    }
                    Shape sp = getInsertShape(y);
                    gc.setColor(Color.BLACK);
                    gc.fill(sp);
                } else {
                    int y = (model.indexOf(dropTarget.getParent()) - 1) * ROWHEIGHT;
                    Shape sp = dropIntoJObjectShape(y);
                    gc.setColor(Color.BLACK);
                    gc.fill(sp);
                }
            }
        }
    }

    private Shape dropIntoJObjectShape(int y) {
        GeneralPath ret = new GeneralPath();
        int h = ROWHEIGHT / 2;
        int w = getWidth();
        y = y + h;
        ret.moveTo(0, y - h);
        ret.lineTo(h, y);
        ret.lineTo(0, y + h);
        ret.closePath();
        ret.moveTo(w - h, y);
        ret.lineTo(w, y - h);
        ret.lineTo(w, y + h);
        ret.closePath();
        return ret;
    }

    private Shape getInsertShape(int y) {
        GeneralPath ret = new GeneralPath();
        ret.moveTo(0, y - 5);
        ret.lineTo(4, y - 1);
        ret.lineTo(getWidth() - 4, y - 1);
        ret.lineTo(getWidth(), y - 5);
        ret.lineTo(getWidth(), y + 5);
        ret.lineTo(getWidth() - 4, y + 1);
        ret.lineTo(4, y + 1);
        ret.lineTo(0, y + 5);
        ret.lineTo(0, y - 5);
        ret.closePath();
        return ret;
    }

    public int getRowAt(int x, int y) {
        int ry = y / ROWHEIGHT;
        Rectangle r = new Rectangle(0, ry * ROWHEIGHT, getWidth(), ROWHEIGHT);
        if (r.contains(x, y) && ry < model.getRowCount() - 1) {
            return ry + 1;
        }
        return -1;
    }

    private void paintRow(Graphics gg, JLeaf leaf, int depth, JRequest req, boolean selected) {
        Graphics2D g = (Graphics2D) gg;
        //visibleIcon
        Rectangle rect;
        if (selected) {
            rect = getDraggRect(0, depth);
            g.setColor(SELECTED_COLOR);
            g.fill(rect);
        } else if (leaf == model.getPage().getCurrentLayer()) {
            rect = getDraggRect(0, depth);
            g.setColor(SELECTED_LAYER_COLOR);
            g.fill(rect);
        }
        rect = getEyeRect(0);
        drawEtchedRect(g, rect.x, rect.y, rect.width, rect.height);
        Icon ic;
        if (leaf.isVisible()) {
            ic = JEnvironment.ICONS.EYE_ICON;
            if (!anscesterVisible(leaf)) {
                Composite com = g.getComposite();
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                ic.paintIcon(this, g, rect.x, rect.y);
                g.setComposite(com);
            } else {
                ic.paintIcon(this, g, rect.x, rect.y);
            }
        }
        //LockIcon
        rect = getLockRect(0);
        drawEtchedRect(g, rect.x, rect.y, rect.width, rect.height);
        ic = JEnvironment.ICONS.LOCK_ICON;
        if (leaf.isLocked()) {
            ic.paintIcon(this, g, rect.x, rect.y);
        } else if (anscesterLocked(leaf)) {
            Composite comp = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            ic.paintIcon(this, g, rect.x, rect.y);
            g.setComposite(comp);
        }
        //SelectionCheck;
        rect = getSelectorRect(0);
        drawEtchedRect(g, rect.x, rect.y, rect.width, rect.height);
        ic = JEnvironment.ICONS.CHECK_ICON;
        if (leaf instanceof JLayer) {
            Rectangle rc = new Rectangle(rect.x + 1, rect.y + 1, rect.width - 2, rect.width - 2);
            g.setColor(leaf.getPreviewColor());
            g.fill(rc);
            g.setColor(Color.BLACK);
            g.draw(rc);
        } else if (req.contains(leaf)) {
            ic.paintIcon(this, g, rect.x, rect.y);
        } else if (ancesterSelected(leaf, req)) {
            Composite cmp = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            ic.paintIcon(this, g, rect.x, rect.y);
            g.setComposite(cmp);
        }
        paintBorder(g, rect.x + rect.width + ICONGAP, 1, rect.height);
        //
        rect = getExpandButtonRect(0, depth);
        if (leaf instanceof JObject) {
            JObject o = (JObject) leaf;
            int direction = SwingConstants.EAST;
            if (model.isExpanded(o)) {
                direction = SwingConstants.SOUTH;
            }
            arrow.paintTriangle(g, rect.x, rect.y, ARROWSIZE, direction, true);
            ic = JEnvironment.ICONS.FOLDER_ICON;
        } else if (leaf instanceof JText) {
            ic = JEnvironment.ICONS.TEXT_OBJECT_ICON;
        } else if (leaf instanceof JImageObject) {
            ic = JEnvironment.ICONS.IMAGE_OBJECT_ICON;
        } else {
            ic = JEnvironment.ICONS.OBJECT_ICON;
        }
        rect = getIconRect(0, depth);
        ic.paintIcon(this, g, rect.x, rect.y);
        if (selected) {
            g.setColor(Color.WHITE);
        } else {
            g.setColor(Color.BLACK);
        }
        LineMetrics ln = g.getFont().getLineMetrics(leaf.toString(), g.getFontRenderContext());
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.drawString(leaf.toString(), rect.x + rect.width + ICONGAP, 10 + (ROWHEIGHT - ln.getHeight()) / 2);
        g.setColor(Color.GRAY);
        g.drawLine(0, ROWHEIGHT - 1, getWidth(), ROWHEIGHT - 1);
        g.setColor(Color.WHITE);
        g.drawLine(0, ROWHEIGHT, getWidth(), ROWHEIGHT);
    }

    private Rectangle getEyeRect(int row) {
        Icon icon = JEnvironment.ICONS.EYE_ICON;
        int offset = (ROWHEIGHT - icon.getIconHeight()) / 2;
        return new Rectangle(offset, row * ROWHEIGHT + offset, icon.getIconWidth(), icon.getIconHeight());
    }

    private Rectangle getLockRect(int row) {
        Icon icon = JEnvironment.ICONS.LOCK_ICON;
        int offset = (ROWHEIGHT - icon.getIconHeight()) / 2;
        Rectangle eRect = getEyeRect(row);
        return new Rectangle(eRect.x + eRect.width + ICONGAP, row * ROWHEIGHT + offset, icon.getIconWidth(), icon.getIconHeight());
    }

    private Rectangle getSelectorRect(int row) {
        Icon icon = JEnvironment.ICONS.CHECK_ICON;
        int offset = (ROWHEIGHT - icon.getIconHeight()) / 2;
        Rectangle lRect = getLockRect(row);
        return new Rectangle(lRect.x + lRect.width + ICONGAP, row * ROWHEIGHT + offset, icon.getIconWidth(), icon.getIconHeight());
    }

    private Rectangle getExpandButtonRect(int row, int depth) {
        int offset = (ROWHEIGHT - ARROWSIZE) / 2;
        Rectangle lRect = getSelectorRect(row);
        return new Rectangle(lRect.x + lRect.width + ICONGAP * 2 + TREEINDENT * depth, row * ROWHEIGHT + offset, ARROWSIZE, ARROWSIZE);
    }

    private Rectangle getIconRect(int row, int depth) {
        Icon icon = JEnvironment.ICONS.FOLDER_ICON;
        int offset = (ROWHEIGHT - icon.getIconHeight()) / 2;
        Rectangle eRect = getExpandButtonRect(row, depth);
        return new Rectangle(eRect.x + eRect.width + ICONGAP, row * ROWHEIGHT + offset, icon.getIconWidth(), icon.getIconHeight());
    }

    private Rectangle getDraggRect(int row, int depth) {
        Rectangle iRect = getExpandButtonRect(row, depth);
        int x = iRect.x + iRect.width + ICONGAP;
        int width = getWidth() - x;
        return new Rectangle(x, row * ROWHEIGHT, width, ROWHEIGHT);
    }

    private Rectangle getRowRect(int row) {
        return new Rectangle(0, ROWHEIGHT * row, getWidth(), ROWHEIGHT);
    }

    private boolean ancesterSelected(JLeaf jl, JRequest req) {
        JLeaf parent = jl.getParent();
        if (parent == null || parent instanceof JLayer) {
            return false;
        }
        if (req.contains(parent)) {
            return true;
        }
        return ancesterSelected(parent, req);
    }

    private boolean anscesterVisible(JLeaf jl) {
        JLeaf parent = jl.getParent();
        if (parent == null) {
            return true;
        }
        if (!parent.isVisible()) {
            return false;
        }
        return anscesterVisible(parent);
    }

    private boolean anscesterLocked(JLeaf jl) {
        JLeaf parent = jl.getParent();
        if (parent == null) {
            return false;
        }
        if (parent.isLocked()) {
            return true;
        }
        return anscesterLocked(parent);
    }

    private void drawEtchedRect(Graphics g, int x, int y, int width, int height) {
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(x, y, x + width - 1, y);
        g.drawLine(x, y, x, y + height - 1);
        g.setColor(Color.WHITE);
        g.drawLine(x + width - 1, y, x + width - 1, y + height - 1);
        g.drawLine(x, y + height - 1, x + width - 1, y + height - 1);
    }

    private void paintBorder(Graphics g, int x, int y, int height) {
        g.setColor(Color.GRAY);
        g.drawLine(x, y, x, y + height);
        g.setColor(Color.WHITE);
        g.drawLine(x + 1, y, x + 1, y + height);
    }

    private DropTarget getDropTargetAt(int x, int y) {
        int row = getRowAt(x, y);
        if (row < 0) {
            return null;
        }
        JLeaf jl = model.getJLeaForRow(row);
        JObject parent = null;
        int index = -1;
        int subY = y % ROWHEIGHT;
        if (jl instanceof JObject) {
            if (subY < ROWHEIGHT / 4) {
                parent = jl.getParent();
                index = parent.indexOf(jl) + 1;
            } else if (subY < ROWHEIGHT * 3 / 4) {
                parent = (JObject) jl;
                index = -1;
            } else {
                parent = jl.getParent();
                index = parent.indexOf(jl);
            }
        } else {
            if (subY < ROWHEIGHT / 2) {
                parent = jl.getParent();
                index = parent.indexOf(jl) + 1;
            } else {
                parent = jl.getParent();
                index = parent.indexOf(jl);
            }
        }
        if (selectedLeafs.contains(parent)) {
            return null;
        }
        if (parent instanceof JPage) {
            Iterator<JLeaf> it = selectedLeafs.iterator();
            while (it.hasNext()) {
                JLeaf ijl = it.next();
                if (!(ijl instanceof JLayer)) {
                    return null;
                }
            }
        }
        if (parent instanceof JLayer) {
            for (int i = 0; i < selectedLeafs.size(); i++) {
                if (selectedLeafs.get(i) instanceof JLayer) {
                    return null;
                }
            }
        }
        Iterator<JLeaf> it = selectedLeafs.iterator();
        while (it.hasNext()) {
            if (it.next().isDescender(parent)) {
                return null;
            }
        }
        return new DropTarget(parent, index, jl);
    }

    public void clearSelection() {
        selectedLeafs.clear();
        repaint();
    }

    public void addSelection(JLeaf jl) {
        if (selectedLeafs.contains(jl)) {
            return;
        }
        selectedLeafs.add(jl);
        repaint();
    }

    public void removeSelection(JLeaf jl) {
        selectedLeafs.remove(jl);
        repaint();
    }

    @Override
    public void treeChanged(JTreeEvent e) {
        int w = Math.max(getParent().getWidth(), 200);
        Dimension d = new Dimension(w, model.getRowCount() * ROWHEIGHT);
        this.setPreferredSize(d);
        this.setSize(d);
        repaint();
    }

    public void adjustSize() {
        int w = Math.max(getParent().getWidth(), 200);
        int h=ROWHEIGHT*12;
        if (model !=null)
            h=model.getRowCount()*ROWHEIGHT;
        Dimension d = new Dimension(w,h);
        this.setPreferredSize(d);
        this.setSize(d);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        repaint();
    }

    private Frame getRootFrame(Container c) {
        if (c instanceof Frame) {
            return (Frame) c;
        }
        return getRootFrame(c.getParent());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            return;
        }
        if (model == null) {
            return;
        }
        if (e.getClickCount() != 2) {
            return;
        }
        Point p = e.getPoint();
        int selectedRow = getRowAt(p.x, p.y);
        if (selectedRow == -1) {
            return;
        }
        JLeaf jl = model.getJLeaForRow(selectedRow);
        int depth = model.depth(jl);
        Rectangle bRect = getDraggRect(selectedRow - 1, depth);
        if (!bRect.contains(p)) {
            return;
        }
        Frame frame = getRootFrame(getParent());
        if (jl instanceof JLayer) {
            JLayerOption dlg = new JLayerOption(frame, true);
            int x = frame.getX() + (frame.getWidth() - dlg.getWidth()) / 2;
            int y = frame.getY() + (frame.getHeight() - dlg.getHeight()) / 2;
            dlg.setLocation(x, y);
            dlg.showDialog((JLayer) jl, model.getPage());
        } else if (jl instanceof JImageObject) {
            JImageObjectOption dlg = new JImageObjectOption(frame, true);
            int x = frame.getX() + (frame.getWidth() - dlg.getWidth()) / 2;
            int y = frame.getY() + (frame.getHeight() - dlg.getHeight()) / 2;
            dlg.setLocation(x, y);
            dlg.showDialog((JImageObject) jl, model.getPage());
        } else {
            JObjectOption dlg = new JObjectOption(frame, true);
            int x = frame.getX() + (frame.getWidth() - dlg.getWidth()) / 2;
            int y = frame.getY() + (frame.getHeight() - dlg.getHeight()) / 2;
            dlg.setLocation(x, y);
            dlg.showDialog(jl, model.getPage());
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (model == null) {
            return;
        }
        if (SwingUtilities.isRightMouseButton(e)) {
            return;
        }
        Point p = e.getPoint();
        int selectedRow = getRowAt(p.x, p.y);
        if (selectedRow == -1) {
            selectedLeafs.clear();
            repaint();
            return;
        }
        //atVisible ICon
        JLeaf jl = model.getJLeaForRow(selectedRow);
        JRequest req = model.getPage().getRequest();
        int depth = model.depth(jl);
        //visible
        if (getEyeRect(selectedRow - 1).contains(p)) {
            JLeaf target = model.getJLeaForRow(selectedRow);
            if (target.isLocked() || anscesterLocked(target)) {
                return;
            }
            UndoableEdit anEdit = new SetVisibleEdit(model.getPage().getDocument().getViewer(), target, !target.isVisible());
            model.getPage().getDocument().fireUndoEvent(anEdit);
            return;
        }
        //Lock
        if (getLockRect(selectedRow - 1).contains(p)) {
            JLeaf target = model.getJLeaForRow(selectedRow);
            if (anscesterLocked(target)) {
                return;
            }
            UndoableEdit anEdit = new SetLockEdit(model.getPage().getDocument().getViewer(), target, !target.isLocked());
            model.getPage().getDocument().fireUndoEvent(anEdit);
            return;
        }
        //selector
        if (getSelectorRect(selectedRow - 1).contains(p)) {

            if (jl instanceof JLayer) {
                return;
            }
            if (!e.isShiftDown()) {
                req.clear();
                req.add(jl);
            } else {
                if (req.contains(jl)) {
                    req.remove(jl);
                } else {
                    if (!req.contains(jl.getParent())) {
                        req.add(jl);
                        if (jl instanceof JObject) {
                            JObject jo = (JObject) jl;
                            for (int i = 0; i < jo.size(); i++) {
                                req.remove(jo.get(i));
                            }
                        }
                    }
                }
            }
            model.getPage().getDocument().getViewer().repaint();
            return;
        }
        //

        Rectangle bRect = getExpandButtonRect(selectedRow - 1, depth);
        bRect.x -= 2;
        bRect.y -= 2;
        bRect.width += 4;
        bRect.width += 4;

        if ((jl instanceof JObject) && bRect.contains(p)) {
            JObject jo = (JObject) jl;
            model.setExpanded(jo, !model.isExpanded(jo));
            return;
        }
        if (getDraggRect(selectedRow - 1, depth).contains(p)) {
            if (e.getClickCount() == 2) {
                return;
            }
            for (int i = 0; i < selectedLeafs.size(); i++) {
                Object o = selectedLeafs.get(i);
                if (!model.contains(o)) {
                    selectedLeafs.remove(o);
                }
            }
            if (jl instanceof JLayer) {
                JLayer jjl = (JLayer) jl;
                if (model.getPage().getCurrentLayer() != jjl) {
                    model.getPage().setCurrentLayer(jjl);
                }
            }
            normalizeSelection(jl, e.isShiftDown());
            startPoint = p;
            p.y = (p.y / ROWHEIGHT) * ROWHEIGHT + ROWHEIGHT / 2;
            repaint();

        }

    }

    private void normalizeSelection(JLeaf jl, boolean shiftDown) {
        int maxIndex = Integer.MIN_VALUE;
        int minIndex = Integer.MAX_VALUE;
        int depth = model.depth(jl);
        for (int i = 0; i < selectedLeafs.size(); i++) {
            Object o = selectedLeafs.get(i);
            if (o instanceof JLeaf) {
                JLeaf jlf = (JLeaf) o;
                int index;
                if (model.depth(jlf) == depth && (index = model.indexOf(jlf)) != -1 && jlf.getParent() == jl.getParent()) {
                    if (index < minIndex) {
                        minIndex = index;
                    }
                    if (index > maxIndex) {
                        maxIndex = index;
                    }
                }
            }
        }
        int index = model.indexOf(jl);
        if (maxIndex < minIndex) {
            selectedLeafs.clear();
            selectedLeafs.add(jl);
        } else if (!shiftDown) {
            if (selectedLeafs.contains(jl)) {
                for (int i = 0; i < selectedLeafs.size(); i++) {
                    JLeaf jlf = selectedLeafs.get(i);
                    if (depth != model.depth(jlf) || jlf.getParent() != jl.getParent() || model.indexOf(jlf) < 0) {
                        selectedLeafs.remove(i--);
                    }
                }
            } else {
                selectedLeafs.clear();
                selectedLeafs.add(jl);
            }
        } else {
            selectedLeafs.clear();
            for (int i = Math.min(index, minIndex); i <= Math.max(index, minIndex); i++) {
                JLeaf jlf = model.getJLeaForRow(i);
                if (depth == model.depth(jlf)) {
                    selectedLeafs.add(jlf);
                }
            }
        }
    }

    private void sortSelection() {
        if (selectedLeafs.isEmpty()) {
            return;
        }
        ConcurrentSkipListMap<Integer, JLeaf> sortedMap = new ConcurrentSkipListMap<Integer, JLeaf>();
        for (int i = 0; i < selectedLeafs.size(); i++) {
            JLeaf jl = selectedLeafs.get(i);
            sortedMap.put(jl.getParent().indexOf(jl), jl);
        }
        selectedLeafs.clear();
        Iterator<JLeaf> it = sortedMap.values().iterator();
        while (it.hasNext()) {
            selectedLeafs.add(it.next());
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            return;
        }
        if (model == null) {
            return;
        }
        if (dropTarget != null) {
            CompoundEdit cEdit = new CompoundEdit();
            JDocumentViewer viewer = model.getPage().getDocument().getViewer();
            int index = dropTarget.getIndex();
            sortSelection();
            JLeaf sjl = selectedLeafs.get(selectedLeafs.size() - 1);
            if (dropTarget.getIndex() < sjl.getParent().indexOf(sjl)) {
                for (int i = selectedLeafs.size() - 1; i >= 0; i--) {
                    JLeaf jl = selectedLeafs.get(i);
                    cEdit.addEdit(new BringToIndexEdit(viewer, jl, dropTarget.parent, index));
                }
            } else {
                for (int i = 0; i < selectedLeafs.size(); i++) {
                    JLeaf jl = selectedLeafs.get(i);
                    cEdit.addEdit(new BringToIndexEdit(viewer, jl, dropTarget.parent, index));
                }
            }
            cEdit.end();
            model.getPage().getDocument().fireUndoEvent(cEdit);
        }
        dropTarget = null;
        beDragging = false;
        startPoint = null;
        dragPreviewRect = null;
        currentPoint = null;
        this.setCursor(Cursor.getDefaultCursor());
        repaint();
        model.getPage().getDocument().getViewer().repaint();

    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            return;
        }
        if (startPoint != null) {
            if (dragPreviewRect == null) {
                int y = (model.indexOf(selectedLeafs.get(0)) - 1) * ROWHEIGHT;
                dragPreviewRect = new Rectangle(1, 1 + y, this.getWidth() - 2, ROWHEIGHT * selectedLeafs.size() - 2);
            }
            dropTarget = getDropTargetAt(e.getX(), e.getY());
            currentPoint = e.getPoint();
            if (this.getCursor() != JEnvironment.MOUSE_CURSOR.GRIP) {
                this.setCursor(JEnvironment.MOUSE_CURSOR.GRIP);
            }
            Point p = e.getPoint();
            Rectangle rv = new Rectangle(p.x, p.y, 1, 1);
            this.scrollRectToVisible(rv);
            repaint();
        }

    }

    public void mouseMoved(MouseEvent e) {
    }

    public void undoRedoEventHappened(JUndoRedoEvent e) {
        repaint();
    }

    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return ROWHEIGHT / 2;
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return ROWHEIGHT;
    }

    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    public class DropTarget {

        private JObject parent;
        private JLeaf draggedLeaf;
        private int index;

        public DropTarget(JObject parent, int index, JLeaf leaf) {
            this.parent = parent;
            this.index = index;
            draggedLeaf = leaf;
        }

        public JObject getParent() {
            return parent;
        }

        public int getIndex() {
            return index;
        }

        public String toString() {
            return "Parent=" + parent.toString() + ";Index=" + String.valueOf(index);
        }

        public JLeaf getDraggedLeaf() {
            return draggedLeaf;
        }
    }
}
