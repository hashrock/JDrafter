/*
 * JCAGCulcActions.java
 *
 * Created on 2007/12/19, 10:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jactions;

import jui.color.BluerPanel;
import jui.color.DropShadowPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import jedit.effect.JCAGAddEdit;
import jedit.effect.JCAGMultEdit;
import jedit.effect.JCAGSubEdit;
import jedit.effect.JCAGXorEdit;
import jedit.effect.JCompoundPathEdit;
import jedit.effect.JJoinPathEdit;
import jedit.effect.JReleaceCompoundPathEdit;
import jedit.effect.JSetEffectEdit;
import jedit.effect.JStrokeOutlineEdit;
import jedit.effect.JTextOutlineEdit;
import jedit.effect.JUniformJoinEdit;
import jedit.pathedit.JReversePathEdit;
import jgeom.JSegment;
import jgeom.JSimplePath;
import jobject.JLeaf;
import jobject.JObject;
import jobject.JPathObject;
import jobject.effector.JBlurEffector;
import jobject.effector.JDefaultEffector;
import jobject.effector.JDropShadowEffector;
import jui.color.JColorChanger;
import jscreen.JDocumentViewer;
import jscreen.JRequest;
import jobject.JText;
import jobject.effector.JArrowEffect;
import jui.color.arrow.ArrowPanel;

/**
 *
 * @author i002060
 */
public class JCAGCulcActions implements ItemListener {

    private JDocumentViewer viewer = null;
    public static AddAction addAction = null;
    public static XorAction xorAction = null;
    public static MultAction multAction = null;
    public static SubAction subAction = null;
    public static CompoundPathAction compoundPathAction = null;
    public static ReleaseCompoundPathAction releaseCompoundPathAction = null;
    public static JoinPathAction joinPathAction = null;
    public static UniformJoinPathAction uniformJoinAction = null;
    public static ReversePathAction reversePathAction = null;
    public static ReleaseEffectAction releaseEffectAction = null;
    public static MakeBlurAction makeBlurAction = null;
    public static DropShadowAction dropShadowAction = null;
    public static MakeArrowAction makeArrowAction = null;
    public static OutlineTextAction outlineTextAction = null;
    public static OutlineStrokeAction strokeOutlineAction = null;

    /** Creates a new instance of JCAGCulcActions */
    public JCAGCulcActions() {
        this(null);
    }

    public JCAGCulcActions(JDocumentViewer v) {
        setViewer(v);
        addAction = new AddAction();
        xorAction = new XorAction();
        multAction = new MultAction();
        subAction = new SubAction();
        compoundPathAction = new CompoundPathAction();
        releaseCompoundPathAction = new ReleaseCompoundPathAction();
        joinPathAction = new JoinPathAction();
        uniformJoinAction = new UniformJoinPathAction();
        reversePathAction = new ReversePathAction();
        releaseEffectAction = new ReleaseEffectAction();
        makeBlurAction = new MakeBlurAction();
        dropShadowAction = new DropShadowAction();
        outlineTextAction = new OutlineTextAction();
        makeArrowAction = new MakeArrowAction();
        strokeOutlineAction = new OutlineStrokeAction();
    }

    public void setViewer(JDocumentViewer view) {
        if (viewer != null) {
            viewer.getDocument().removeItemListener(this);
        }
        viewer = view;
        if (viewer != null) {
            viewer.getDocument().addItemListener(this);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        setUpActions();
    }

    private void setUpActions() {
        Vector<JPathObject> target = getSelectedPathObjects();
        Vector<JLeaf> leafs = getSelectedLeafs();
        if (target.size() < 2) {
            addAction.setEnabled(false);
            xorAction.setEnabled(false);
            multAction.setEnabled(false);
            subAction.setEnabled(false);
            compoundPathAction.setEnabled(false);
        } else {
            addAction.setEnabled(true);
            xorAction.setEnabled(true);
            multAction.setEnabled(true);
            subAction.setEnabled(true);
            compoundPathAction.setEnabled(true);
        }
        if (target.size() < 1 || viewer.getCurrentRequest().getSelectionMode() != JRequest.DIRECT_MODE) {
            joinPathAction.setEnabled(false);
            uniformJoinAction.setEnabled(false);
        } else {
            joinPathAction.setEnabled(true);
            uniformJoinAction.setEnabled(true);
        }
        if (target.isEmpty()) {
            reversePathAction.setEnabled(false);
        } else {
            reversePathAction.setEnabled(true);
        }
        if (leafs.isEmpty()) {
            releaseEffectAction.setEnabled(false);
            makeBlurAction.setEnabled(false);
            dropShadowAction.setEnabled(false);
            makeArrowAction.setEnabled(false);
        } else {
            makeBlurAction.setEnabled(true);
            dropShadowAction.setEnabled(true);
            makeArrowAction.setEnabled(true);
        }
        boolean rs = false;
        boolean tx = false;
        for (int i = 0; i < leafs.size(); i++) {
            if (!(leafs.get(i).getEffector() instanceof JDefaultEffector)) {
                rs = true;
            }
            if (leafs.get(i) instanceof JText) {
                tx = true;
            }
        }
        outlineTextAction.setEnabled(tx);
        releaseEffectAction.setEnabled(rs);
        boolean cp = false;
        boolean to = false;
        for (int i = 0; i < target.size(); i++) {
            if (target.get(i).getPath().size() > 1) {
                cp = true;
            }
            if (!(target.get(i) instanceof JText) && (target.get(i).getStrokePaint() != null)) {
                to = true;
            }
        }
        strokeOutlineAction.setEnabled(to);
        releaseCompoundPathAction.setEnabled(cp);
    }

    private Vector<JPathObject> getSelectedPathObjects() {
        Vector<JPathObject> ret = new Vector<JPathObject>();
        JRequest req = viewer.getCurrentRequest();
        for (int i = 0; i < req.size(); i++) {
            if (req.get(i) instanceof JPathObject) {
                ret.add((JPathObject) req.get(i));
            }
        }
        return ret;
    }

    private Vector<JLeaf> getSelectedLeafs() {
        Vector<JLeaf> leafs = new Vector<JLeaf>();
        JRequest req = viewer.getCurrentRequest();
        for (int i = 0; i < req.size(); i++) {
            Object o = req.get(i);
            if (o instanceof JObject) {
                JColorChanger.getLeafObjects(leafs, (JObject) o);
            } else if (o instanceof JLeaf) {
                leafs.add((JLeaf) o);
            }
        }
        return leafs;
    }

    private boolean getSelectedSegments(Vector<JPathObject> tObj, Vector<JSimplePath> tPath, Vector<JSegment> tSeg) {
        JRequest req = viewer.getCurrentRequest();
        for (int i = 0; i < req.size(); i++) {
            Object o = req.get(i);
            if (o instanceof JPathObject) {
                tObj.add((JPathObject) o);
            } else if (o instanceof JSimplePath) {
                tPath.add((JSimplePath) o);
            } else if (o instanceof JSegment) {
                tSeg.add((JSegment) o);
            }
        }
        if (tSeg.size() != 2) {
            tObj.clear();
            tPath.clear();
            tSeg.clear();
            return false;
        }
        JPathObject o1 = null, o2 = null;
        JSimplePath p1 = null, p2 = null;
        JSegment s1 = tSeg.get(0), s2 = tSeg.get(1);
        for (int i = 0; i < tPath.size(); i++) {
            if (p1 == null && tPath.get(i).contains(s1)) {
                p1 = tPath.get(i);
            }
            if (p2 == null && tPath.get(i).contains(s2)) {
                p2 = tPath.get(i);
            }
            if (p1 != null && p2 != null) {
                break;
            }
        }
        if (p1 == null || p2 == null) {
            return false;
        }
        for (int i = 0; i < tObj.size(); i++) {
            if (o1 == null && tObj.get(i).getPath().contains(p1)) {
                o1 = tObj.get(i);
            }
            if (o2 == null && tObj.get(i).getPath().contains(p2)) {
                o2 = tObj.get(i);
            }
        }
        tObj.clear();
        tPath.clear();
        tSeg.clear();
        if (o1 == null || o2 == null) {
            return false;
        }
        if (p1.isLooped() || p2.isLooped() || p1.indexOf(s1) != 0 && p1.indexOf(s1) != p1.size() - 1 ||
                p2.indexOf(s2) != 0 && p2.indexOf(s2) != p2.size() - 1) {
            return false;
        }
        if ((o1.getParent() == o2.getParent() && o1.getParent().indexOf(o1) > o2.getParent().indexOf(o2)) ||
                o1.getParent().getParent().indexOf(o1.getParent()) > o1.getParent().getParent().indexOf(o1.getParent())) {
            tObj.add(o2);
            tObj.add(o1);
            tPath.add(p2);
            tPath.add(p1);
            tSeg.add(s2);
            tSeg.add(s1);
        } else {
            tObj.add(o1);
            tObj.add(o2);
            tPath.add(p1);
            tPath.add(p2);
            tSeg.add(s1);
            tSeg.add(s2);
        }
        return true;
    }

    public class AddAction extends AbstractAction {

        public AddAction() {
            putValue(NAME, "和(A)");
            putValue(MNEMONIC_KEY, KeyEvent.VK_A);
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Vector<JPathObject> targets = getSelectedPathObjects();
            if (targets == null || targets.isEmpty()) {
                return;
            }
            UndoableEdit anEdit = new JCAGAddEdit(viewer, targets);
            viewer.getDocument().fireUndoEvent(anEdit);
            viewer.repaint();

        }
    }

    public class SubAction extends AbstractAction {

        public SubAction() {
            putValue(NAME, "差(S)");
            putValue(MNEMONIC_KEY, KeyEvent.VK_S);
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Vector<JPathObject> targets = getSelectedPathObjects();
            if (targets == null || targets.isEmpty()) {
                return;
            }
            UndoableEdit anEdit = new JCAGSubEdit(viewer, targets);
            viewer.getDocument().fireUndoEvent(anEdit);
            viewer.repaint();

        }
    }

    public class XorAction extends AbstractAction {

        public XorAction() {
            putValue(NAME, "排他的論理和(X)");
            putValue(MNEMONIC_KEY, KeyEvent.VK_X);
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Vector<JPathObject> targets = getSelectedPathObjects();
            if (targets == null || targets.isEmpty()) {
                return;
            }
            UndoableEdit anEdit = new JCAGXorEdit(viewer, targets);
            viewer.getDocument().fireUndoEvent(anEdit);
            viewer.repaint();

        }
    }

    public class MultAction extends AbstractAction {

        public MultAction() {
            putValue(NAME, "積(M)");
            putValue(MNEMONIC_KEY, KeyEvent.VK_M);
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Vector<JPathObject> targets = getSelectedPathObjects();
            if (targets == null || targets.isEmpty()) {
                return;
            }
            UndoableEdit anEdit = new JCAGMultEdit(viewer, targets);
            viewer.getDocument().fireUndoEvent(anEdit);
            viewer.repaint();

        }
    }

    public class ReleaseCompoundPathAction extends AbstractAction {

        public ReleaseCompoundPathAction() {
            putValue(NAME, "複合パス解除(R)");
            putValue(MNEMONIC_KEY, KeyEvent.VK_R);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Vector<JPathObject> targets = getSelectedPathObjects();
            if (targets == null || targets.isEmpty()) {
                return;
            }
            for (int i = 0; i < targets.size(); i++) {
                if (targets.get(i).getPath().size() <= 1) {
                    targets.remove(i--);
                }
            }
            if (targets.isEmpty()) {
                return;
            }
            CompoundEdit cEdit = new CompoundEdit();
            for (int i = 0; i < targets.size(); i++) {
                cEdit.addEdit(new JReleaceCompoundPathEdit(viewer, targets.get(i)));
            }
            cEdit.end();
            viewer.getDocument().fireUndoEvent(cEdit);
            viewer.repaint();
        }
    }

    public class CompoundPathAction extends AbstractAction {

        public CompoundPathAction() {
            putValue(NAME, "複合パス作成(P)");
            putValue(MNEMONIC_KEY, KeyEvent.VK_P);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.CTRL_MASK));
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Vector<JPathObject> targets = getSelectedPathObjects();
            if (targets == null || targets.isEmpty()) {
                return;
            }
            if (targets.size() < 2) {
                return;
            }
            UndoableEdit anEdit = new JCompoundPathEdit(viewer, targets);
            viewer.getDocument().fireUndoEvent(anEdit);
            viewer.repaint();
        }
    }

    public class JoinPathAction extends AbstractAction {

        public JoinPathAction() {
            putValue(NAME, "パスの連結(J)");
            putValue(MNEMONIC_KEY, KeyEvent.VK_J);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_J, ActionEvent.CTRL_MASK));
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Vector<JPathObject> tObj = new Vector<JPathObject>();
            Vector<JSimplePath> tPath = new Vector<JSimplePath>();
            Vector<JSegment> tSeg = new Vector<JSegment>();
            if (!getSelectedSegments(tObj, tPath, tSeg)) {
                JOptionPane pane = new JOptionPane();
                JOptionPane.showMessageDialog(viewer.getScroller(), "パスを連結するためには、連結する二つの端点を選択してください.", "パスの連結", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            UndoableEdit anEdit = new JJoinPathEdit(viewer, tObj.get(0), tSeg.get(0), tObj.get(1), tSeg.get(1));
            viewer.getDocument().fireUndoEvent(anEdit);
            viewer.repaint();
        }
    }

    public class UniformJoinPathAction extends AbstractAction {

        public UniformJoinPathAction() {
            putValue(NAME, "パスを平均化して連結(A)");
            putValue(MNEMONIC_KEY, KeyEvent.VK_A);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_J, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Vector<JPathObject> tObj = new Vector<JPathObject>();
            Vector<JSimplePath> tPath = new Vector<JSimplePath>();
            Vector<JSegment> tSeg = new Vector<JSegment>();
            if (!getSelectedSegments(tObj, tPath, tSeg)) {
                JOptionPane pane = new JOptionPane();
                JOptionPane.showMessageDialog(viewer.getScroller(), "パスを連結するためには、連結する二つの端点を選択してください.", "パスの連結", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            UndoableEdit anEdit = new JUniformJoinEdit(viewer, tObj.get(0), tSeg.get(0), tObj.get(1), tSeg.get(1));
            viewer.getDocument().fireUndoEvent(anEdit);
            viewer.repaint();
        }
    }

    public class ReversePathAction extends AbstractAction {

        public ReversePathAction() {
            putValue(NAME, "パスの逆転(R)");
            putValue(MNEMONIC_KEY, KeyEvent.VK_R);
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Vector<JPathObject> jp = getSelectedPathObjects();
            if (jp.isEmpty()) {
                return;
            }
            JRequest req = viewer.getCurrentRequest();
            SelectedPaths sPath = new SelectedPaths(jp);
            if (req.getSelectionMode() == JRequest.GROUP_MODE) {
                sPath.addAll();
            } else {
                for (int i = 0; i < req.size(); i++) {
                    if (req.get(i) instanceof JSimplePath) {
                        sPath.add((JSimplePath) req.get(i));
                    }
                }
            }
            if (sPath.pathCount() == 0) {
                return;
            }
            UndoableEdit anedit = sPath.getEdit();
            if (anedit != null) {
                viewer.getDocument().fireUndoEvent(anedit);
                viewer.repaint();
            }

        }

        public class SelectedPaths {

            Vector<SelectedPath> paths;

            public SelectedPaths(Vector<JPathObject> targets) {
                paths = new Vector<SelectedPath>();
                for (int i = 0; i < targets.size(); i++) {
                    paths.add(new SelectedPath(targets.get(i)));
                }
            }

            public void addAll() {
                for (int i = 0; i < paths.size(); i++) {
                    paths.get(i).addAll();
                }
            }

            public void add(JSimplePath sp) {
                for (int i = 0; i < paths.size(); i++) {
                    if (paths.get(i).add(sp)) {
                        break;
                    }
                }
            }

            public int pathCount() {
                int ret = 0;
                for (int i = 0; i < paths.size(); i++) {
                    ret += paths.get(i).paths.size();
                }
                return ret;
            }

            public UndoableEdit getEdit() {
                CompoundEdit cEdit = null;
                for (int i = 0; i < paths.size(); i++) {
                    UndoableEdit anedit = paths.get(i).getEdit();
                    if (anedit != null) {
                        if (cEdit == null) {
                            cEdit = new CompoundEdit();
                        }
                        cEdit.addEdit(anedit);
                    }
                }
                if (cEdit != null) {
                    cEdit.end();
                }
                return cEdit;
            }
        }

        public class SelectedPath {

            public JPathObject jp;
            public Vector<JSimplePath> paths;

            public SelectedPath(JPathObject jpo) {
                jp = jpo;
                paths = new Vector<JSimplePath>();
            }

            public void addAll() {
                paths.clear();
                for (int i = 0; i < jp.getPath().size(); i++) {
                    if (!paths.contains(jp.getPath().get(i))) {
                        paths.add(jp.getPath().get(i));
                    }
                }
            }

            public boolean add(JSimplePath p) {
                if (jp.getPath().contains(p) && !paths.contains(p)) {
                    paths.add(p);
                    return true;
                }
                return false;
            }

            public UndoableEdit getEdit() {
                CompoundEdit cEdit = null;
                for (int i = 0; i < paths.size(); i++) {
                    if (cEdit == null) {
                        cEdit = new CompoundEdit();
                    }
                    cEdit.addEdit(new JReversePathEdit(viewer, jp, paths.get(i)));
                }
                if (cEdit != null) {
                    cEdit.end();
                }
                return cEdit;
            }
        }
    }

    public class ReleaseEffectAction extends AbstractAction {

        public ReleaseEffectAction() {
            putValue(NAME, "エフェクト解除(R)");
            putValue(MNEMONIC_KEY, KeyEvent.VK_R);
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Vector<JLeaf> leafs = getSelectedLeafs();
            if (leafs.isEmpty()) {
                return;
            }
            CompoundEdit cEdit = null;
            JDefaultEffector jfe = new JDefaultEffector();
            for (int i = 0; i < leafs.size(); i++) {
                JLeaf lf = leafs.get(i);
                if (!(lf.getEffector() instanceof JDefaultEffector)) {
                    if (cEdit == null) {
                        cEdit = new CompoundEdit();
                    }
                    cEdit.addEdit(new JSetEffectEdit(viewer, lf, jfe, "エフェクト解除"));
                }
            }
            if (cEdit != null) {
                cEdit.end();
                viewer.getDocument().fireUndoEvent(cEdit);
                viewer.repaint();
            }
            setUpActions();
        }
    }

    public class MakeBlurAction extends AbstractAction {

        public MakeBlurAction() {
            putValue(NAME, "ぼかし(B)");
            putValue(MNEMONIC_KEY, KeyEvent.VK_B);
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            BluerPanel panel = new BluerPanel(viewer.getScroller(), true);
            if (panel.isDesided()) {
                Vector<JLeaf> leafs = getSelectedLeafs();
                if (leafs.isEmpty()) {
                    return;
                }
                CompoundEdit cEdit = new CompoundEdit();
                for (int i = 0; i < leafs.size(); i++) {
                    JBlurEffector effect = new JBlurEffector(panel.getValue());
                    cEdit.addEdit(new JSetEffectEdit(viewer, leafs.get(i), effect, "ぼかし"));
                }
                cEdit.end();
                viewer.getDocument().fireUndoEvent(cEdit);
                viewer.repaint();
            }
            setUpActions();
        }
    }

    public class DropShadowAction extends AbstractAction {

        public DropShadowAction() {
            putValue(NAME, "ドロップシャドウ(D)");
            putValue(MNEMONIC_KEY, KeyEvent.VK_D);
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            DropShadowPanel panel = new DropShadowPanel(viewer.getScroller(), true);
            if (panel.isDesided()) {
                Vector<JLeaf> leafs = getSelectedLeafs();
                if (leafs.isEmpty()) {
                    return;
                }
                CompoundEdit cEdit = new CompoundEdit();
                for (int i = 0; i < leafs.size(); i++) {
                    JDropShadowEffector effect = panel.getEffector();
                    cEdit.addEdit(new JSetEffectEdit(viewer, leafs.get(i), effect, "ドロップシャドウ"));
                }
                cEdit.end();
                viewer.getDocument().fireUndoEvent(cEdit);
                viewer.repaint();
            }
            setUpActions();

        }
    }

    public class MakeArrowAction extends AbstractAction {

        public MakeArrowAction() {
            putValue(NAME, "矢印を作成(A)");
            putValue(MNEMONIC_KEY, KeyEvent.VK_A);
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Vector<JLeaf> leafs = getSelectedLeafs();
            JArrowEffect effect = null;
            for (JLeaf jl : leafs) {
                if (jl.getEffector() instanceof JArrowEffect) {
                    effect = (JArrowEffect) jl.getEffector();
                    break;
                }
            }
            JArrowEffect newEffect = ArrowPanel.showAsDialog(viewer.getScroller(), effect);
            if (newEffect != effect) {
                CompoundEdit cEdit = null;
                for (JLeaf jl : leafs) {

                    if (newEffect != null) {
                        if (cEdit == null) {
                            cEdit = new CompoundEdit();
                        }
                        cEdit.addEdit(new JSetEffectEdit(viewer, jl, newEffect, "矢印にする"));
                    } else if (jl.getEffector() instanceof JArrowEffect) {
                        if (cEdit == null) {
                            cEdit = new CompoundEdit();
                        }
                        cEdit.addEdit(new JSetEffectEdit(viewer,jl,new JDefaultEffector(),"エフェクト解除"));
                    }
                }
                if (cEdit != null) {
                    cEdit.end();
                    viewer.getDocument().fireUndoEvent(cEdit);
                    viewer.repaint();
                }
            }
            setUpActions();
        }
    }

    public class OutlineTextAction extends AbstractAction {

        public OutlineTextAction() {
            putValue(NAME, "テキストアウトライン(T)");
            putValue(MNEMONIC_KEY, KeyEvent.VK_T);
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Vector<JLeaf> leafs = getSelectedLeafs();
            if (leafs.isEmpty()) {
                return;
            }
            Vector<JText> jtx = new Vector<JText>();
            for (int i = 0; i < leafs.size(); i++) {
                if (leafs.get(i) instanceof JText) {
                    jtx.add((JText) leafs.get(i));
                }
            }
            if (jtx.isEmpty()) {
                return;
            }
            CompoundEdit cEdit = new CompoundEdit();
            for (int i = 0; i < jtx.size(); i++) {
                cEdit.addEdit(new JTextOutlineEdit(viewer, jtx.get(i)));
            }
            cEdit.end();
            viewer.getDocument().fireUndoEvent(cEdit);
            viewer.repaint();
        }
    }

    public class OutlineStrokeAction extends AbstractAction {

        public OutlineStrokeAction() {
            putValue(NAME, "ストロークアウトライン(S)");
            putValue(MNEMONIC_KEY, KeyEvent.VK_S);
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Vector<JPathObject> jp = getSelectedPathObjects();
            if (jp.isEmpty()) {
                return;
            }
            for (int i = 0; i < jp.size(); i++) {
                if (jp.get(i) instanceof JText || jp.get(i).getStrokePaint() == null) {
                    jp.remove(i--);
                }
            }
            if (jp.isEmpty()) {
                return;
            }
            CompoundEdit cEdit = new CompoundEdit();
            for (int i = 0; i < jp.size(); i++) {
                cEdit.addEdit(new JStrokeOutlineEdit(viewer, jp.get(i)));
            }
            cEdit.end();
            viewer.getDocument().fireUndoEvent(cEdit);
            viewer.repaint();
        }
    }
}
