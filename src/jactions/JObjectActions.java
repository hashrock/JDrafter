/*
 * JObjectActions.java
 *
 * Created on 2007/09/17, 14:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jactions;

import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import jtools.jcontrol.ReflectToolPanel;
import jtools.jcontrol.RotateToolPanel;
import jtools.jcontrol.ScaleToolPanel;
import jtools.jcontrol.SheerToolPanel;
import jtools.jcontrol.TranslateToolPanel;
import jedit.JDuplicateObjectEdit;
import jedit.effect.JMakeTrimingEdit;
import jedit.effect.JReleaseTrimingEdit;
import jedit.objectmenuedit.BringObjectEdit;
import jedit.objectmenuedit.BringObjectsEdit;
import jedit.objectmenuedit.JDoGroupEdit;
import jedit.objectmenuedit.JUnGroupEdit;
import jedit.paintedit.JMakePatternEdit;
import jgeom.JSegment;
import jobject.JClippedImageObject;
import jobject.JGroupObject;
import jobject.JImageObject;
import jobject.JLayer;
import jobject.JLeaf;
import jobject.JObject;
import jobject.JPage;
import jobject.JPathObject;
import jpaint.JPaint;
import jscreen.JDocumentViewer;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author TI
 */
public class JObjectActions implements ItemListener, JUndoRedoListener {

    public static final int HOLIZONTAL = 0;
    public static final int VERTICAL = 1;
    private JDocumentViewer viewer = null;
    //static Actions
    public static DoGroup doGroup = null;
    public static UnGroup unGroup = null;
    public static BringToTop bringToTop = null;
    public static BringFront bringFront = null;
    public static SendToBottom sendToBottom = null;
    public static SendBack sendBack = null;
    //
    public static ReshapeAgain reshapeAgain = null;
    public static TranslateAction translateAction = null;
    public static RotateAction rotateAction = null;
    public static ScaleAction scaleAction = null;
    public static ReflectAction reflectAction = null;
    public static ShearAction shearAction = null;    //
    public static AlignHCenter alignHCenter = null;
    public static AlignTop alignTop = null;
    public static AlignBottom alignBottom = null;
    public static AlignVCenter alignVCenter = null;
    public static AlignLeft alignLeft = null;
    public static AlignRight alignRight = null;
    public static HJustify hJustify = null;
    public static VJustify vJustify = null;
    //
    public static MakePattern makePattern = null;
    //
    public static MakeTriming makeTriming=null;
    public static ReleaseTriming releaseTriming=null;
    //public static MakeClipedPattern makeClipedPattern = null;

    /**
     * Creates a new instance of JObjectActions
     */
    public JObjectActions() {
        this(null);
    }

    public JObjectActions(JDocumentViewer v) {
        setViewer(v);
        doGroup = new DoGroup();
        unGroup = new UnGroup();
        //
        bringToTop = new BringToTop();
        bringFront = new BringFront();
        //
        sendToBottom = new SendToBottom();
        sendBack = new SendBack();
        //
        reshapeAgain = new ReshapeAgain();
        translateAction = new TranslateAction();
        scaleAction = new ScaleAction();
        rotateAction = new RotateAction();
        reflectAction = new ReflectAction();
        shearAction = new ShearAction();
        //
        alignHCenter = new AlignHCenter();
        alignTop = new AlignTop();
        alignBottom = new AlignBottom();
        alignVCenter = new AlignVCenter();
        alignLeft = new AlignLeft();
        alignRight = new AlignRight();
        hJustify = new HJustify();
        vJustify = new VJustify();
        //
        makePattern = new MakePattern();
        //
        makeTriming=new MakeTriming();
        releaseTriming=new ReleaseTriming();
        //makeClipedPattern = new MakeClipedPattern();
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

    @Override
    public void itemStateChanged(ItemEvent e) {
        setupActions();
    }

    private void setupActions() {
        JRequest req = viewer.getCurrentRequest();
        Vector<JLeaf> objs = new Vector();
        //<editor-fold defaultstate="collapsed" desc=" For Group,UnGroup">
        for (int i = 0; i < req.size(); i++) {
            if (req.get(i) instanceof JLeaf) {
                objs.add((JLeaf) req.get(i));
            }
        }
        if (objs.size() < 2) {
            doGroup.setEnabled(false);
        } else {
            doGroup.setEnabled(true);
        }
        if (objs.size() == 1 && req.get(0) instanceof JGroupObject) {
            unGroup.setEnabled(true);
        } else {
            unGroup.setEnabled(false);
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc=" For Arrange">
        if (objs.isEmpty()) {
            bringToTop.setEnabled(false);
            bringFront.setEnabled(false);
            sendToBottom.setEnabled(false);
            sendBack.setEnabled(false);
        } else if (objs.size() == 1) {
            JLeaf leaf = objs.get(0);
            int idx = leaf.getParent().indexOf(leaf);
            sendToBottom.setEnabled(false);
            sendBack.setEnabled(false);
            bringToTop.setEnabled(false);
            bringFront.setEnabled(false);
            if (idx > 0) {
                sendToBottom.setEnabled(true);
                sendBack.setEnabled(true);
            }
            if (idx < leaf.getParent().size() - 1) {
                bringToTop.setEnabled(true);
                bringFront.setEnabled(true);
            }
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc=" For Transform">
        reshapeAgain.setEnabled((viewer.getEnvironment().LAST_TRANSFORM != null) && !objs.isEmpty());
        if (objs.isEmpty()) {
            translateAction.setEnabled(false);
            scaleAction.setEnabled(false);
            rotateAction.setEnabled(false);
            reflectAction.setEnabled(false);
            shearAction.setEnabled(false);
        } else {
            translateAction.setEnabled(true);
            scaleAction.setEnabled(true);
            rotateAction.setEnabled(true);
            reflectAction.setEnabled(true);
            shearAction.setEnabled(true);
        }
        //</editor-fold>
        if (objs.size() < 2) {
            alignHCenter.setEnabled(false);
            alignTop.setEnabled(false);
            alignBottom.setEnabled(false);
            alignVCenter.setEnabled(false);
            alignLeft.setEnabled(false);
            alignRight.setEnabled(false);
        } else {
            alignHCenter.setEnabled(true);
            alignTop.setEnabled(true);
            alignBottom.setEnabled(true);
            alignVCenter.setEnabled(true);
            alignLeft.setEnabled(true);
            alignRight.setEnabled(true);
        }
        if (objs.size() < 3) {
            hJustify.setEnabled(false);
            vJustify.setEnabled(false);
        } else {
            hJustify.setEnabled(true);
            vJustify.setEnabled(true);
        }
        makePattern.setEnabled(!objs.isEmpty());

        makeTriming.setEnabled(objs.size()==2);
        releaseTriming.setEnabled(false);
        for (JLeaf jlf:objs){
            if (jlf instanceof JClippedImageObject){
                releaseTriming.setEnabled(true);
                break;
            }
        }
        //makeClipedPattern.setEnabled(!objs.isEmpty());

    }

    private Vector<JLeaf> getSelectedLeafs() {
        Vector<JLeaf> ret = new Vector<JLeaf>();
        JRequest req = viewer.getCurrentRequest();
        for (int i = 0; i < req.size(); i++) {
            if (req.get(i) instanceof JLeaf) {
                ret.add((JLeaf) req.get(i));
            }
        }
        return ret;
    }

    private Vector<Rectangle2D> sort(Vector<JLeaf> objects, int dr) {
        Vector<Rectangle2D> pos = new Vector<Rectangle2D>();
        for (int i = 0; i < objects.size(); i++) {
            pos.add(objects.get(i).getSelectionBounds());
        }
        for (int i = 0; i < objects.size() - 1; i++) {
            for (int j = i + 1; j < objects.size(); j++) {
                if (dr == HOLIZONTAL && pos.get(i).getX() > pos.get(j).getX()) {
                    swap(pos, i, j);
                    swap(objects, i, j);
                } else if (dr == VERTICAL && pos.get(i).getY() > pos.get(j).getY()) {
                    swap(pos, i, j);
                    swap(objects, i, j);
                }
            }
        }
        return pos;
    }

    private void swap(Vector leafs, int i, int j) {
        if (i > j) {
            int p = i;
            i = j;
            j = p;
        }
        Object jj = leafs.remove(j);
        Object ji = leafs.remove(i);
        leafs.add(i, jj);
        leafs.add(j, ji);
    }

    private CompoundEdit transformObjects(Vector<JLeaf> targets, AffineTransform tx, double rotation, boolean copy) {
        CompoundEdit ret = null;
        if (targets.isEmpty()) {
            return ret;
        }
        if (tx.isIdentity()) {
            return ret;
        }
        ret = new CompoundEdit();
        Point p = new Point(0, 0);
        JEnvironment env = viewer.getEnvironment();
        JRequest req = viewer.getCurrentRequest();
        //

        req.hitObjects.clear();
        req.hitResult = req.HIT_NON;
        if (req.getSelectionMode() == JRequest.DIRECT_MODE) {
            for (Object obj : req.getSelectedVector()) {
                if (obj instanceof JSegment) {
                    req.hitResult = JRequest.HIT_ANCUR;
                }
            }
        }
        req.isAltDown = false;
        for (int i = 0; i < targets.size(); i++) {
            if (copy) {
                ret.addEdit(new JDuplicateObjectEdit(viewer, targets.get(i)));
            }
            targets.get(i).transform(tx, req, p);
            if (rotation != 0) {
                ret.addEdit(targets.get(i).updateRotate(env, rotation));
            } else {
                ret.addEdit(targets.get(i).updateTransform(env));
            }
        }
        ret.end();
        return ret;
    }

    private Rectangle2D getBoundingBox(Vector<JLeaf> targets) {
        Rectangle2D ret = null;
        for (int i = 0; i < targets.size(); i++) {
            Rectangle2D r = targets.get(i).getSelectionBounds();
            if (r != null && !r.isEmpty()) {
                if (ret == null) {
                    ret = r;
                } else {
                    ret.add(r);
                }
            }
        }
        return ret;
    }

    @Override
    public void undoRedoEventHappened(JUndoRedoEvent e) {
        setupActions();
    }

    public class DoGroup extends AbstractAction {

        public DoGroup() {
            putValue(NAME, java.util.ResourceBundle.getBundle("main").getString("item_group"));
            putValue(MNEMONIC_KEY, KeyEvent.VK_G);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            JPage p = viewer.getCurrentPage();
            JRequest req = viewer.getCurrentRequest();
            Vector<JLeaf> objects = new Vector<JLeaf>();
            for (int i = 0; i < p.size(); i++) {
                JLayer jl = p.get(i);
                for (int j = 0; j < jl.size(); j++) {
                    JLeaf o = jl.get(j);
                    if (req.contains(o)) {
                        objects.add(o);
                    }
                }
            }
            if (!objects.isEmpty()) {
                viewer.getDocument().fireUndoEvent(new JDoGroupEdit(viewer, objects));
                viewer.repaint();
            }
            setupActions();
        }
    }

    public class UnGroup extends AbstractAction {

        public UnGroup() {
            putValue(NAME, java.util.ResourceBundle.getBundle("main").getString("item_ungroup"));
            putValue(MNEMONIC_KEY, KeyEvent.VK_U);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            JRequest req = viewer.getCurrentRequest();
            if (req.size() == 1 && req.get(0) instanceof JGroupObject) {
                viewer.getDocument().fireUndoEvent(new JUnGroupEdit(viewer, (JGroupObject) req.get(0)));
                viewer.repaint();
                setupActions();
            }
        }
    }

    public class BringToTop extends AbstractAction {

        public BringToTop() {
            putValue(NAME, java.util.ResourceBundle.getBundle("main").getString("item_move_top"));
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_8, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
            putValue(MNEMONIC_KEY, KeyEvent.VK_F);
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            Vector<JLeaf> leafs = getSelectedLeafs();
            if (leafs.isEmpty()) {
                return;
            }
            UndoableEdit edt = new BringObjectsEdit(viewer, leafs, BringObjectEdit.BRING_TO_TOP);
            viewer.getDocument().fireUndoEvent(edt);
            viewer.repaint();
            setupActions();
        }
    }

    public class BringFront extends AbstractAction {

        public BringFront() {
            putValue(NAME, java.util.ResourceBundle.getBundle("main").getString("item_move_forward"));
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_8, ActionEvent.CTRL_MASK));
            putValue(MNEMONIC_KEY, KeyEvent.VK_B);
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            Vector<JLeaf> leafs = getSelectedLeafs();
            if (leafs.isEmpty()) {
                return;
            }
            UndoableEdit edt = new BringObjectsEdit(viewer, leafs, BringObjectEdit.BRING_FRONT);
            viewer.getDocument().fireUndoEvent(edt);
            viewer.repaint();
            setupActions();
        }
    }

    public class SendToBottom extends AbstractAction {

        public SendToBottom() {
            putValue(NAME, java.util.ResourceBundle.getBundle("main").getString("item_move_bottom"));
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_9, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
            putValue(MNEMONIC_KEY, KeyEvent.VK_K);
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            Vector<JLeaf> leafs = getSelectedLeafs();
            if (leafs.isEmpty()) {
                return;
            }
            UndoableEdit edt = new BringObjectsEdit(viewer, leafs, BringObjectEdit.SEND_TO_BOTTOM);
            viewer.getDocument().fireUndoEvent(edt);
            viewer.repaint();
            setupActions();
        }
    }

    public class SendBack extends AbstractAction {

        public SendBack() {
            putValue(NAME, java.util.ResourceBundle.getBundle("main").getString("item_move_back"));
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_9, ActionEvent.CTRL_MASK));
            putValue(MNEMONIC_KEY, KeyEvent.VK_S);
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            Vector<JLeaf> leafs = getSelectedLeafs();
            if (leafs.isEmpty()) {
                return;
            }
            UndoableEdit edt = new BringObjectsEdit(viewer, leafs, BringObjectEdit.SEND_BACK);
            viewer.getDocument().fireUndoEvent(edt);
            viewer.repaint();
            setupActions();
        }
    }

    private JFrame getRootWindow(Container c) {
        if (c instanceof JFrame) {
            return (JFrame) c;
        }
        return getRootWindow(c.getParent());
    }
    //移動の繰り返し

    public class ReshapeAgain extends AbstractAction {

        public ReshapeAgain() {
            putValue(NAME, java.util.ResourceBundle.getBundle("main").getString("item_repeat_transform"));
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
            putValue(MNEMONIC_KEY, KeyEvent.VK_R);
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            JEnvironment env = viewer.getEnvironment();
            if (env.LAST_TRANSFORM == null) {
                return;
            }
            JRequest req = viewer.getCurrentRequest();
            Vector<JLeaf> targets = getSelectedLeafs();
            CompoundEdit cEdit = transformObjects(targets, env.LAST_TRANSFORM, env.LAST_ROTATION, env.LAST_COPY);
            if (cEdit != null) {
                viewer.getDocument().fireUndoEvent(cEdit);
                viewer.repaint();
                setupActions();
            }
        }
    }
    //変形

    public class TranslateAction extends AbstractAction {

        public TranslateAction() {
            putValue(NAME, java.util.ResourceBundle.getBundle("main").getString("item_move_horizontally"));
            //putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_D,ActionEvent.CTRL_MASK));
            //putValue(MNEMONIC_KEY,KeyEvent.VK_D);
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            TranslateToolPanel panel = new TranslateToolPanel(getRootWindow(viewer.getParent()));
            if (!panel.isCanceled()) {
                Vector<JLeaf> targets = getSelectedLeafs();
                JEnvironment env = viewer.getEnvironment();
                AffineTransform tx = new AffineTransform();
                tx.setToTranslation(env.DEFAULT_TRANSLATE_X, env.DEFAULT_TRANSLATE_Y);
                CompoundEdit cEdit = transformObjects(targets, tx, 0, panel.isCopyed());
                if (cEdit != null) {
                    env.LAST_TRANSFORM = tx;
                    env.LAST_ROTATION = 0;
                    env.LAST_COPY = panel.isCopyed();
                    viewer.getDocument().fireUndoEvent(cEdit);
                    viewer.repaint();
                    setupActions();
                }
            }
        }
    }

    public class ScaleAction extends AbstractAction {

        public ScaleAction() {
            putValue(NAME, java.util.ResourceBundle.getBundle("main").getString("item_scale"));
            //putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_D,ActionEvent.CTRL_MASK));
            //putValue(MNEMONIC_KEY,KeyEvent.VK_D);
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            ScaleToolPanel panel = new ScaleToolPanel(getRootWindow(viewer.getParent()));
            if (!panel.isCanceled()) {
                Vector<JLeaf> targets = getSelectedLeafs();
                JEnvironment env = viewer.getEnvironment();
                AffineTransform tx = new AffineTransform();
                Rectangle2D rc = null;
                for (int i = 0; i < targets.size(); i++) {
                    Rectangle2D bd = targets.get(i).getSelectionBounds();
                    if (bd != null && !bd.isEmpty()) {
                        if (rc == null) {
                            rc = bd;
                        } else {
                            rc.add(bd);
                        }
                    }
                }
                tx.setToTranslation(rc.getCenterX(), rc.getCenterY());
                tx.scale(env.DEFAULT_SCALE_X, env.DEFAULT_SCALE_Y);
                tx.translate(-rc.getCenterX(), -rc.getCenterY());
                CompoundEdit cEdit = transformObjects(targets, tx, 0, panel.isCopy());
                if (cEdit != null) {
                    env.LAST_TRANSFORM = tx;
                    env.LAST_ROTATION = 0;
                    env.LAST_COPY = panel.isCopy();
                    viewer.getDocument().fireUndoEvent(cEdit);
                    viewer.repaint();
                    setupActions();
                }
            }
        }
    }

    public class RotateAction extends AbstractAction {

        public RotateAction() {
            putValue(NAME, java.util.ResourceBundle.getBundle("main").getString("item_rotate"));
            //putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_D,ActionEvent.CTRL_MASK));
            //putValue(MNEMONIC_KEY,KeyEvent.VK_D);
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            RotateToolPanel panel = new RotateToolPanel(getRootWindow(viewer.getParent()));
            if (!panel.isCanceled()) {
                Vector<JLeaf> targets = getSelectedLeafs();
                if (targets.isEmpty()) {
                    return;
                }
                Rectangle2D bounds = null;
                for (int i = 0; i < targets.size(); i++) {
                    Rectangle2D b = targets.get(i).getSelectionBounds();
                    if (b == null) {
                        continue;
                    }
                    if (bounds == null) {
                        bounds = b;
                    } else {
                        bounds.add(b);
                    }
                }
                if (bounds == null || bounds.isEmpty()) {
                    return;
                }
                JEnvironment env = viewer.getEnvironment();
                AffineTransform tx = new AffineTransform();
                tx.setToRotation(env.DEFAULT_THETA, bounds.getCenterX(), bounds.getCenterY());
                CompoundEdit cEdit = transformObjects(targets, tx, env.DEFAULT_THETA, panel.isCopy());
                if (cEdit != null) {
                    env.LAST_TRANSFORM = tx;
                    env.LAST_ROTATION = env.DEFAULT_THETA;
                    env.LAST_COPY = panel.isCopy();
                    viewer.getDocument().fireUndoEvent(cEdit);
                    viewer.repaint();
                    setupActions();
                }
            }
        }
    }

    public class ReflectAction extends AbstractAction {

        public ReflectAction() {
            putValue(NAME, java.util.ResourceBundle.getBundle("main").getString("item_move_mirror"));
            //putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_D,ActionEvent.CTRL_MASK));
            //putValue(MNEMONIC_KEY,KeyEvent.VK_D);
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            ReflectToolPanel panel = new ReflectToolPanel(getRootWindow(viewer.getParent()));
            if (!panel.isCanceled()) {
                Vector<JLeaf> targets = getSelectedLeafs();
                if (targets.isEmpty()) {
                    return;
                }
                Rectangle2D bounds = null;
                for (int i = 0; i < targets.size(); i++) {
                    Rectangle2D b = targets.get(i).getSelectionBounds();
                    if (b == null) {
                        continue;
                    }
                    if (bounds == null) {
                        bounds = b;
                    } else {
                        bounds.add(b);
                    }
                }
                if (bounds == null || bounds.isEmpty()) {
                    return;
                }
                JEnvironment env = viewer.getEnvironment();
                AffineTransform tx = new AffineTransform();
                tx.setToTranslation(bounds.getCenterX(), bounds.getCenterY());
                tx.rotate(env.DEFAULT_REFLECT_AXIS);
                tx.scale(1, -1);
                tx.rotate(-env.DEFAULT_REFLECT_AXIS);
                tx.translate(-bounds.getCenterX(), -bounds.getCenterY());
                CompoundEdit cEdit = transformObjects(targets, tx, 0, panel.isCopy());
                if (cEdit != null) {
                    env.LAST_TRANSFORM = tx;
                    env.LAST_ROTATION = env.DEFAULT_THETA;
                    env.LAST_COPY = panel.isCopy();
                    viewer.getDocument().fireUndoEvent(cEdit);
                    viewer.repaint();
                    setupActions();
                }
            }
        }
    }

    public class ShearAction extends AbstractAction {

        public ShearAction() {
            putValue(NAME, java.util.ResourceBundle.getBundle("main").getString("item_shear"));
            //putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_D,ActionEvent.CTRL_MASK));
            //putValue(MNEMONIC_KEY,KeyEvent.VK_D);
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            SheerToolPanel panel = new SheerToolPanel(getRootWindow(viewer.getParent()));
            if (!panel.isCanceled()) {
                Vector<JLeaf> targets = getSelectedLeafs();
                JEnvironment env = viewer.getEnvironment();
                Rectangle2D rc = null;
                for (int i = 0; i < targets.size(); i++) {
                    Rectangle2D bd = targets.get(i).getSelectionBounds();
                    if (bd != null && !bd.isEmpty()) {
                        if (rc == null) {
                            rc = bd;
                        } else {
                            rc.add(bd);
                        }
                    }
                }
                AffineTransform tx = new AffineTransform();
                tx.setToTranslation(rc.getCenterX(), rc.getCenterY());
                tx.shear(env.DEFAULT_SHEER_X, env.DEFAULT_SHEER_Y);
                tx.translate(-rc.getCenterX(), -rc.getCenterY());
                CompoundEdit cEdit = transformObjects(targets, tx, 0, panel.isCopy());
                if (cEdit != null) {
                    env.LAST_TRANSFORM = tx;
                    env.LAST_ROTATION = 0;
                    env.LAST_COPY = panel.isCopy();
                    viewer.getDocument().fireUndoEvent(cEdit);
                    viewer.repaint();
                    setupActions();
                }
            }
        }
    }
    //整列

    public class AlignHCenter extends AbstractAction {

        public AlignHCenter() {
            putValue(NAME, java.util.ResourceBundle.getBundle("main").getString("item_align_vertical_center"));
            putValue(MNEMONIC_KEY, KeyEvent.VK_H);
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            Vector<JLeaf> targets = getSelectedLeafs();
            Rectangle2D r = getBoundingBox(targets);
            if (r == null || r.isEmpty()) {
                return;
            }
            double alignY = r.getCenterY();
            AffineTransform tx = new AffineTransform();
            CompoundEdit cEdit = new CompoundEdit();
            Point p = new Point(0, 0);
            JRequest req = viewer.getCurrentRequest();
            JEnvironment env = viewer.getEnvironment();
            req.hitObjects.clear();
            req.hitResult = req.HIT_NON;
            for (int i = 0; i < targets.size(); i++) {
                double deltaY = alignY - targets.get(i).getSelectionBounds().getCenterY();
                tx.setToTranslation(0, deltaY);
                targets.get(i).transform(tx, req, p);
                UndoableEdit anEdit = targets.get(i).updateTransform(env);
                if (anEdit != null) {
                    cEdit.addEdit(anEdit);
                }
            }
            cEdit.end();
            viewer.getDocument().fireUndoEvent(cEdit);
            viewer.repaint();
        }
    }

    public class AlignTop extends AbstractAction {

        public AlignTop() {
            putValue(NAME, java.util.ResourceBundle.getBundle("main").getString("item_align_top"));
            putValue(MNEMONIC_KEY, KeyEvent.VK_T);
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            Vector<JLeaf> targets = getSelectedLeafs();
            Rectangle2D r = getBoundingBox(targets);
            if (r == null || r.isEmpty()) {
                return;
            }
            double alignY = r.getY();
            AffineTransform tx = new AffineTransform();
            CompoundEdit cEdit = new CompoundEdit();
            Point p = new Point(0, 0);
            JRequest req = viewer.getCurrentRequest();
            JEnvironment env = viewer.getEnvironment();
            req.hitObjects.clear();
            req.hitResult = req.HIT_NON;
            for (int i = 0; i < targets.size(); i++) {
                double deltaY = alignY - targets.get(i).getSelectionBounds().getY();
                tx.setToTranslation(0, deltaY);
                targets.get(i).transform(tx, req, p);
                UndoableEdit anEdit = targets.get(i).updateTransform(env);
                if (anEdit != null) {
                    cEdit.addEdit(anEdit);
                }
            }
            cEdit.end();
            viewer.getDocument().fireUndoEvent(cEdit);
            viewer.repaint();
        }
    }

    public class AlignBottom extends AbstractAction {

        public AlignBottom() {
            putValue(NAME, java.util.ResourceBundle.getBundle("main").getString("item_align_bottom"));
            putValue(MNEMONIC_KEY, KeyEvent.VK_B);
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            Vector<JLeaf> targets = getSelectedLeafs();
            Rectangle2D r = getBoundingBox(targets);
            if (r == null || r.isEmpty()) {
                return;
            }
            double alignY = r.getY() + r.getHeight();
            AffineTransform tx = new AffineTransform();
            CompoundEdit cEdit = new CompoundEdit();
            Point p = new Point(0, 0);
            JRequest req = viewer.getCurrentRequest();
            JEnvironment env = viewer.getEnvironment();
            req.hitObjects.clear();
            req.hitResult = req.HIT_NON;
            for (int i = 0; i < targets.size(); i++) {
                Rectangle2D or = targets.get(i).getSelectionBounds();
                double deltaY = alignY - (or.getY() + or.getHeight());
                tx.setToTranslation(0, deltaY);
                targets.get(i).transform(tx, req, p);
                UndoableEdit anEdit = targets.get(i).updateTransform(env);
                if (anEdit != null) {
                    cEdit.addEdit(anEdit);
                }
            }
            cEdit.end();
            viewer.getDocument().fireUndoEvent(cEdit);
            viewer.repaint();
        }
    }

    public class AlignVCenter extends AbstractAction {

        public AlignVCenter() {
            putValue(NAME, java.util.ResourceBundle.getBundle("main").getString("item_align_horizontal_center"));
            putValue(MNEMONIC_KEY, KeyEvent.VK_V);
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            Vector<JLeaf> targets = getSelectedLeafs();
            Rectangle2D r = getBoundingBox(targets);
            if (r == null || r.isEmpty()) {
                return;
            }
            double alignX = r.getCenterX();
            AffineTransform tx = new AffineTransform();
            CompoundEdit cEdit = new CompoundEdit();
            Point p = new Point(0, 0);
            JRequest req = viewer.getCurrentRequest();
            JEnvironment env = viewer.getEnvironment();
            req.hitObjects.clear();
            req.hitResult = req.HIT_NON;
            for (int i = 0; i < targets.size(); i++) {
                double deltaX = alignX - targets.get(i).getSelectionBounds().getCenterX();
                tx.setToTranslation(deltaX, 0);
                targets.get(i).transform(tx, req, p);
                UndoableEdit anEdit = targets.get(i).updateTransform(env);
                if (anEdit != null) {
                    cEdit.addEdit(anEdit);
                }
            }
            cEdit.end();
            viewer.getDocument().fireUndoEvent(cEdit);
            viewer.repaint();
        }
    }

    public class AlignLeft extends AbstractAction {

        public AlignLeft() {
            putValue(NAME, java.util.ResourceBundle.getBundle("main").getString("item_align_left"));
            putValue(MNEMONIC_KEY, KeyEvent.VK_L);
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            Vector<JLeaf> targets = getSelectedLeafs();
            Rectangle2D r = getBoundingBox(targets);
            if (r == null || r.isEmpty()) {
                return;
            }
            double alignX = r.getX();
            AffineTransform tx = new AffineTransform();
            CompoundEdit cEdit = new CompoundEdit();
            Point p = new Point(0, 0);
            JRequest req = viewer.getCurrentRequest();
            JEnvironment env = viewer.getEnvironment();
            req.hitObjects.clear();
            req.hitResult = req.HIT_NON;
            for (int i = 0; i < targets.size(); i++) {
                double deltaX = alignX - targets.get(i).getSelectionBounds().getX();
                tx.setToTranslation(deltaX, 0);
                targets.get(i).transform(tx, req, p);
                UndoableEdit anEdit = targets.get(i).updateTransform(env);
                if (anEdit != null) {
                    cEdit.addEdit(anEdit);
                }
            }
            cEdit.end();
            viewer.getDocument().fireUndoEvent(cEdit);
            viewer.repaint();
        }
    }

    public class AlignRight extends AbstractAction {

        public AlignRight() {
            putValue(NAME, java.util.ResourceBundle.getBundle("main").getString("item_align_right"));
            putValue(MNEMONIC_KEY, KeyEvent.VK_R);
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            Vector<JLeaf> targets = getSelectedLeafs();
            Rectangle2D r = getBoundingBox(targets);
            if (r == null || r.isEmpty()) {
                return;
            }
            double alignX = r.getX() + r.getWidth();
            AffineTransform tx = new AffineTransform();
            CompoundEdit cEdit = new CompoundEdit();
            Point p = new Point(0, 0);
            JRequest req = viewer.getCurrentRequest();
            JEnvironment env = viewer.getEnvironment();
            req.hitObjects.clear();
            req.hitResult = req.HIT_NON;
            for (int i = 0; i < targets.size(); i++) {
                Rectangle2D or = targets.get(i).getSelectionBounds();
                double deltaX = alignX - (or.getX() + or.getWidth());
                tx.setToTranslation(deltaX, 0);
                targets.get(i).transform(tx, req, p);
                UndoableEdit anEdit = targets.get(i).updateTransform(env);
                if (anEdit != null) {
                    cEdit.addEdit(anEdit);
                }
            }
            cEdit.end();
            viewer.getDocument().fireUndoEvent(cEdit);
            viewer.repaint();
        }
    }

    public class HJustify extends AbstractAction {

        public HJustify() {
            putValue(NAME, java.util.ResourceBundle.getBundle("main").getString("item_align_divide_horizontal"));
            putValue(MNEMONIC_KEY, KeyEvent.VK_W);
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            Vector<JLeaf> leafs = getSelectedLeafs();
            if (leafs == null || leafs.isEmpty()) {
                return;
            }
            Vector<Rectangle2D> bounds = sort(leafs, HOLIZONTAL);
            Rectangle2D outBound = getBoundingBox(leafs);
            double totalWidth = 0;
            for (int i = 0; i < bounds.size(); i++) {
                totalWidth += bounds.get(i).getWidth();
            }
            double inval = (outBound.getWidth() - totalWidth) / (leafs.size() - 1);
            JRequest req = viewer.getCurrentRequest();
            JEnvironment env = viewer.getEnvironment();
            req.hitObjects.clear();
            req.hitResult = req.HIT_NON;
            req.isAltDown = false;
            double x = bounds.get(0).getX() + bounds.get(0).getWidth() + inval;
            AffineTransform tx = new AffineTransform();
            Point p = new Point(0, 0);
            CompoundEdit cEdit = null;
            for (int i = 1; i < leafs.size(); i++) {
                tx.setToTranslation(x - bounds.get(i).getX(), 0);
                leafs.get(i).transform(tx, req, p);
                UndoableEdit anEdit = leafs.get(i).updateTransform(env);
                if (anEdit != null) {
                    if (cEdit == null) {
                        cEdit = new CompoundEdit();
                    }
                    cEdit.addEdit(anEdit);
                }
                x += bounds.get(i).getWidth() + inval;
            }
            if (cEdit != null) {
                cEdit.end();
                viewer.getDocument().fireUndoEvent(cEdit);
                viewer.repaint();
            }
        }
    }

    public class VJustify extends AbstractAction {

        public VJustify() {
            putValue(NAME, java.util.ResourceBundle.getBundle("main").getString("item_align_divide_vertical"));
            putValue(MNEMONIC_KEY, KeyEvent.VK_D);
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            Vector<JLeaf> leafs = getSelectedLeafs();
            if (leafs == null || leafs.isEmpty()) {
                return;
            }
            Vector<Rectangle2D> bounds = sort(leafs, VERTICAL);
            Rectangle2D outBound = getBoundingBox(leafs);
            double totalHeight = 0;
            for (int i = 0; i < bounds.size(); i++) {
                totalHeight += bounds.get(i).getHeight();
            }
            double inval = (outBound.getHeight() - totalHeight) / (leafs.size() - 1);
            JRequest req = viewer.getCurrentRequest();
            JEnvironment env = viewer.getEnvironment();
            req.hitObjects.clear();
            req.hitResult = req.HIT_NON;
            req.isAltDown = false;
            double y = bounds.get(0).getY() + bounds.get(0).getHeight() + inval;
            AffineTransform tx = new AffineTransform();
            Point p = new Point(0, 0);
            CompoundEdit cEdit = null;
            for (int i = 1; i < leafs.size(); i++) {
                tx.setToTranslation(0, y - bounds.get(i).getY());
                leafs.get(i).transform(tx, req, p);
                UndoableEdit anEdit = leafs.get(i).updateTransform(env);
                if (anEdit != null) {
                    if (cEdit == null) {
                        cEdit = new CompoundEdit();
                    }
                    cEdit.addEdit(anEdit);
                }
                y += bounds.get(i).getHeight() + inval;
            }
            if (cEdit != null) {
                cEdit.end();
                viewer.getDocument().fireUndoEvent(cEdit);
                viewer.repaint();
            }
        }
    }

    public class MakePattern extends AbstractAction {

        public MakePattern() {
            putValue(NAME, java.util.ResourceBundle.getBundle("main").getString("item_restore_object_as_pattern"));
            putValue(MNEMONIC_KEY, KeyEvent.VK_M);
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JRequest req = viewer.getCurrentRequest();
            if (req.isEmpty()) {
                return;
            }
            Vector<JLeaf> leafs = new Vector<JLeaf>();
            for (int i = 0; i < req.size(); i++) {
                if (req.get(i) instanceof JLeaf) {
                    addNode((JLeaf) req.get(i), leafs);
                }
            }
            if (leafs.isEmpty()) {
                return;
            }
            Rectangle2D clip = null;
            for (JLeaf l : leafs) {
                Rectangle2D r = l.getShape().getBounds2D();
                if (r != null) {
                    if (clip == null) {
                        clip = r;
                    } else {
                        clip.add(r);
                    }
                }
            }
            if (clip == null) {
                return;
            }
            Vector<JLeaf> cLeaf = new Vector<JLeaf>();
            try {
                for (JLeaf l : leafs) {
                    cLeaf.add((JLeaf) l.clone());
                }
            } catch (Exception ex) {
            }
            JPaint tp = new JPaint(clip, cLeaf);
            UndoableEdit anEdit = new JMakePatternEdit(viewer, tp);
            viewer.getDocument().fireUndoEvent(anEdit);
        }

        private void addNode(JLeaf jl, Vector<JLeaf> vjl) {
            if (jl instanceof JObject) {
                JObject jo = (JObject) jl;
                for (int i = 0; i < jo.size(); i++) {
                    addNode(jo.get(i), vjl);
                }
            } else {
                if (!vjl.contains(jl)) {
                    vjl.add(jl);
                }
            }
        }
    }
    //トリミング

    public class MakeTriming extends AbstractAction {

        public MakeTriming() {
            putValue(NAME, java.util.ResourceBundle.getBundle("main").getString("item_create_trimming"));
            putValue(MNEMONIC_KEY, KeyEvent.VK_M);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Vector<JLeaf> objs=getSelectedLeafs();
            JPathObject jp = null;
            JImageObject ji = null;
            if (objs.size() == 2) {
                for (JLeaf obj:objs) {
                    if (obj instanceof JPathObject) {
                        jp = (JPathObject) obj;
                    } else if (obj instanceof JImageObject && !(obj instanceof JClippedImageObject)) {
                        ji = (JImageObject) obj;
                    }
                }
                if (jp != null && ji != null) {
                    UndoableEdit ce = new JMakeTrimingEdit(viewer, ji, jp);
                    viewer.getDocument().fireUndoEvent(ce);
                } else {
                    showErrorMsg();
                }
            } else {
                showErrorMsg();
            }
        }
        private void showErrorMsg() {
            JOptionPane.showMessageDialog(viewer, java.util.ResourceBundle.getBundle("main").getString("msg_to_create_trimming_trim"),
                    "JDrafter", JOptionPane.WARNING_MESSAGE);
        }
    }

    public class ReleaseTriming extends AbstractAction {

        public ReleaseTriming() {
            putValue(NAME, java.util.ResourceBundle.getBundle("main").getString("item_remove_trimming"));
            putValue(MNEMONIC_KEY, KeyEvent.VK_R);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Vector<JLeaf> objs=getSelectedLeafs();
            CompoundEdit edt = null;
            for (JLeaf o:objs) {
                if (o instanceof JClippedImageObject) {
                    JClippedImageObject jc = (JClippedImageObject) o;
                    if (edt == null) {
                        edt = new CompoundEdit();
                    }
                    edt.addEdit(new JReleaseTrimingEdit(viewer, jc));
                }
            }
            if (edt != null) {
                edt.end();
                viewer.getDocument().fireUndoEvent(edt);
            }
        }
    }
    /*
    public class MakeClipedPattern extends AbstractAction {

    public MakeClipedPattern() {
    putValue(NAME, "最背面オブジェクトでクリップ(C)");
    putValue(MNEMONIC_KEY, KeyEvent.VK_C);
    setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    JRequest req = viewer.getCurrentRequest();
    if (req.isEmpty()) {
    return;
    }
    Vector<JLeaf> leafs = new Vector<JLeaf>();
    for (int i = 0; i < req.size(); i++) {
    if (req.get(i) instanceof JLeaf) {
    addNode((JLeaf) req.get(i), leafs);
    }
    }
    if (leafs.isEmpty()) {
    return;
    }
    Rectangle2D clip = leafs.get(0).getShape().getBounds2D();
    if (clip == null || clip.isEmpty()) {
    return;
    }
    Vector<JLeaf> cLeaf = new Vector<JLeaf>();
    try {
    for (JLeaf l : leafs) {
    cLeaf.add((JLeaf) l.clone());
    }
    } catch (Exception ex) {
    }
    JPaint tp = new JPaint(clip, cLeaf,true);
    UndoableEdit anEdit = new JMakePatternEdit(viewer, tp);
    viewer.getDocument().fireUndoEvent(anEdit);
    }

    private void addNode(JLeaf jl, Vector<JLeaf> vjl) {
    if (jl instanceof JObject) {
    JObject jo = (JObject) jl;
    for (int i = 0; i < jo.size(); i++) {
    addNode(jo.get(i), vjl);
    }
    } else {
    if (!vjl.contains(jl)) {
    vjl.add(jl);
    }
    }
    }
    }
     */
}
