package jdraw;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.util.LinkedHashMap;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
/*
import jdraw.JDrawApplication.ViewMenus.DocumentSelector;
import jdraw.JDrawApplication.ViewMenus.GridForeAction;
import jdraw.JDrawApplication.ViewMenus.GridOptionAction;
import jdraw.JDrawApplication.ViewMenus.GridVisibleAction;
import jdraw.JDrawApplication.ViewMenus.InchGridAction;
import jdraw.JDrawApplication.ViewMenus.LayerVisibleAction;
import jdraw.JDrawApplication.ViewMenus.MilGridAction;
import jdraw.JDrawApplication.ViewMenus.PageVisibleAction;
import jdraw.JDrawApplication.ViewMenus.SnapGridAction;
 */
import javax.swing.undo.CompoundEdit;
import jedit.JDeleteObjectEdit;
import jedit.JInsertObjectEdit;
import jobject.JGuidLayer;
import jobject.JLeaf;
import jobject.JPage;
import jscreen.JEnvironment;
import jscreen.JRequest;

class ViewMenus extends ComponentAdapter implements ChangeListener, ItemListener, ContainerListener, InternalFrameListener {

    public JCheckBoxMenuItem layerVisible;
    public JCheckBoxMenuItem pageVisible;
    public JCheckBoxMenuItem gridVisible;
    public GridForeAction gridForeAction;
    public SendGuidAction sendGuidAction;
    public ReleaseGuidAction releaseGuidAction;
    public LockGuidAction lockGuidAction;
    public ShowGuidAction showGuidAction;
    public ClearGuidAction clearGuidAction;
    public SnapAncurMenu snapAncurMenu;
    public JMenu gridGauge;
    private JRadioButtonMenuItem milGrid;
    private JRadioButtonMenuItem inchGrid;
    public JCheckBoxMenuItem snapGrid;
    public JMenuItem gridOption;
    private LinkedHashMap<JDocumentFrame, DocumentSelector> docMap;
    private ButtonGroup bgroup = new ButtonGroup();
    JDrawApplication outer;

    public ViewMenus(JDrawApplication outer) {
        super();
        this.outer = outer;
        outer.layerBrowser.addComponentListener(this);
        outer.pageNavigator.addComponentListener(this);
        layerVisible = new JCheckBoxMenuItem();
        layerVisible.setAction(new LayerVisibleAction());
        pageVisible = new JCheckBoxMenuItem();
        pageVisible.setAction(new PageVisibleAction());
        gridVisible = new JCheckBoxMenuItem();
        gridVisible.setAction(new GridVisibleAction());
        gridForeAction = new GridForeAction();
        sendGuidAction = new SendGuidAction();
        releaseGuidAction = new ReleaseGuidAction();
        lockGuidAction = new LockGuidAction();
        showGuidAction = new ShowGuidAction();
        clearGuidAction=new ClearGuidAction();
        snapAncurMenu=new SnapAncurMenu();
        gridGauge = new JMenu("グリッドの単位(U)");
        gridGauge.setMnemonic(KeyEvent.VK_U);
        milGrid = new JRadioButtonMenuItem(new MilGridAction());
        inchGrid = new JRadioButtonMenuItem(new InchGridAction());
        ButtonGroup buttong = new ButtonGroup();
        buttong.add(milGrid);
        buttong.add(inchGrid);
        gridGauge.add(milGrid);
        gridGauge.add(inchGrid);
        snapGrid = new JCheckBoxMenuItem(new SnapGridAction());
        gridOption = new JMenuItem(new GridOptionAction());
        docMap = new LinkedHashMap<JDocumentFrame, DocumentSelector>();
        changeStetes();
    }

    public DocumentSelector getDcomSelector(JDocumentFrame frame) {
        return docMap.get(frame);
    }

    public void changeStetes() {
        layerVisible.setSelected(outer.layerBrowser.isVisible());
        pageVisible.setSelected(outer.pageNavigator.isVisible());
        JDocumentFrame frame = (JDocumentFrame) outer.jDesktopPane1.getSelectedFrame();
        gridForeAction.changeStates();
        sendGuidAction.stateChanged(frame);
        lockGuidAction.stateChanged(frame);
        showGuidAction.stateChanged(frame);
        releaseGuidAction.stateChanged(frame);
        clearGuidAction.stateChanged(frame);
        if (frame == null) {
            gridVisible.setEnabled(false);
            gridGauge.setEnabled(false);
            snapGrid.setEnabled(false);
            gridOption.setEnabled(false);
            gridForeAction.setEnabled(false);
            outer.jPagePanel1.changeStates(null);
            outer.magCombo.changeStates(null);
            snapAncurMenu.setEnabled(false);
        } else {
            gridVisible.setEnabled(true);
            gridVisible.setSelected(frame.getViewer().getEnvironment().isGridVisible());
            gridGauge.setEnabled(true);
            snapGrid.setEnabled(true);
            gridForeAction.setEnabled(true);
            gridOption.setEnabled(true);
            snapAncurMenu.setEnabled(true);
            if (frame.getViewer().getEnvironment().getGuageUnit() == JEnvironment.METRIC_GAUGE) {
                milGrid.setSelected(true);
            } else {
                inchGrid.setSelected(true);
            }
            snapGrid.setSelected(frame.getViewer().getEnvironment().isSnapGrid());
            int pst = (int) (frame.getViewer().getEnvironment().getMagnification() * 100);
            outer.jPagePanel1.changeStates(frame.getViewer());
            outer.magCombo.changeStates(frame.getViewer());
        }
    }

    @Override
    public void componentShown(ComponentEvent e) {
        changeStetes();
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        changeStetes();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        changeStetes();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        changeStetes();
    }

    @Override
    public void componentAdded(ContainerEvent e) {
        outer.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (e.getChild() instanceof JDocumentFrame) {
            JDocumentFrame frame = (JDocumentFrame) e.getChild();
            frame.addInternalFrameListener(this);
            DocumentSelector dsel = new DocumentSelector(frame);
            docMap.put(frame, dsel);
            bgroup.add(dsel);
            outer.viewMenu.add(dsel);
        }

        outer.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void componentRemoved(ContainerEvent e) {
        if (e.getChild() instanceof JDocumentFrame) {
            JDocumentFrame frame = (JDocumentFrame) e.getChild();
            frame.removeInternalFrameListener(this);
            DocumentSelector dsel = docMap.get(frame);
            bgroup.remove(dsel);
            outer.viewMenu.remove(dsel);
        }
    }

    @Override
    public void internalFrameOpened(InternalFrameEvent e) {
    }

    @Override
    public void internalFrameClosing(InternalFrameEvent e) {
    }

    @Override
    public void internalFrameClosed(InternalFrameEvent e) {
    }

    @Override
    public void internalFrameIconified(InternalFrameEvent e) {
    }

    @Override
    public void internalFrameDeiconified(InternalFrameEvent e) {
    }

    @Override
    public void internalFrameActivated(InternalFrameEvent e) {
        docMap.get(e.getInternalFrame()).setSelected(true);
        outer.typePanel.fontFamily.setEnabled(true);
        outer.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        outer.typePanel.fontFamily.showPopup();
        outer.typePanel.fontFamily.hidePopup();
        outer.typePanel.fontFamily.setEnabled(false);
        outer.setCursor(Cursor.getDefaultCursor());
    }

    @Override
    public void internalFrameDeactivated(InternalFrameEvent e) {
    }

    private class LayerVisibleAction extends AbstractAction {

        private LayerVisibleAction() {
            super();
            putValue(NAME, "レイヤーブラウザ(L)");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK));
            putValue(MNEMONIC_KEY, KeyEvent.VK_L);

            setEnabled(true);
            putValue(SELECTED_KEY, outer.layerBrowser.isVisible());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            outer.layerBrowser.setVisible(!outer.layerBrowser.isVisible());
        }
    }

    private class PageVisibleAction extends AbstractAction {

        private PageVisibleAction() {
            super();
            putValue(NAME, "ページブラウザ(B)");
            putValue(MNEMONIC_KEY, KeyEvent.VK_B);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK));
            setEnabled(true);
            putValue(SELECTED_KEY, outer.pageNavigator.isVisible());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            outer.pageNavigator.setVisible(!outer.pageNavigator.isVisible());
        }
    }

    private class GridVisibleAction extends AbstractAction {

        public GridVisibleAction() {
            super();
            putValue(NAME, "グリッド表示(G)");
            putValue(MNEMONIC_KEY, KeyEvent.VK_G);
            //putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_G,ActionEvent.CTRL_MASK|ActionEvent.ALT_MASK));
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JDocumentFrame frame = (JDocumentFrame) outer.jDesktopPane1.getSelectedFrame();
            if (frame == null) {
                return;
            }
            frame.getViewer().getEnvironment().setGridVisible(!frame.getViewer().getEnvironment().isGridVisible());
            frame.getViewer().isDraftMode = false;
            frame.getViewer().repaint();
        }
    }

    private class MilGridAction extends AbstractAction {

        public MilGridAction() {
            super();
            putValue(NAME, "ミリメートル(M)");
            putValue(MNEMONIC_KEY, KeyEvent.VK_M);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK));

            setEnabled(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JDocumentFrame frame = (JDocumentFrame) outer.jDesktopPane1.getSelectedFrame();
            if (frame == null) {
                return;
            }
            boolean isSelected = milGrid.isSelected();
            if (isSelected) {
                frame.getViewer().getEnvironment().setGuageUnit(JEnvironment.METRIC_GAUGE);
            }
            frame.getViewer().isDraftMode = false;
            frame.getViewer().getScroller().repaint();
        }
    }

    private class InchGridAction extends AbstractAction {

        public InchGridAction() {
            super();
            putValue(NAME, "ポイント(P)");
            putValue(MNEMONIC_KEY, KeyEvent.VK_P);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK));
            setEnabled(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JDocumentFrame frame = (JDocumentFrame) outer.jDesktopPane1.getSelectedFrame();
            if (frame == null) {
                return;
            }
            boolean isSelected = inchGrid.isSelected();
            if (isSelected) {
                frame.getViewer().getEnvironment().setGuageUnit(JEnvironment.INCHI_GAUGE);
            }
            frame.getViewer().isDraftMode = false;
            frame.getViewer().getScroller().repaint();
        }
    }
    private class SnapAncurMenu extends JCheckBoxMenuItem implements ActionListener{
        public SnapAncurMenu(){
            this.setText("アンカーに吸着(A)");
            this.setMnemonic(KeyEvent.VK_A);
            this.setSelected(JEnvironment.SNAP_TO_ANCUR);
            this.setEnabled(false);
            this.addActionListener(this);
        }
        @Override
        public void actionPerformed(ActionEvent e){
            JEnvironment.SNAP_TO_ANCUR=!JEnvironment.SNAP_TO_ANCUR;
        }
    }
    private class SnapGridAction extends AbstractAction {

        public SnapGridAction() {
            super();
            putValue(NAME, "グリッド吸着（S)");
            putValue(MNEMONIC_KEY, KeyEvent.VK_S);
            //putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.CTRL_MASK|ActionEvent.ALT_MASK));
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JDocumentFrame frame = (JDocumentFrame) outer.jDesktopPane1.getSelectedFrame();
            if (frame == null) {
                return;
            }
            frame.getViewer().getEnvironment().setSnapGrid(!frame.getViewer().getEnvironment().isSnapGrid());
        }
    }

    private class GridForeAction extends AbstractAction {

        private String gridFore = "グリッドを前面に(F)";
        private String gridBack = "グリッドを背面に(F)";

        public GridForeAction() {
            super();
            changeStates();
            putValue(MNEMONIC_KEY, KeyEvent.VK_F);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK));
            setEnabled(false);
        }

        public void changeStates() {
            if (JEnvironment.GRID_FOREGROUND) {
                putValue(NAME, gridBack);
            } else {
                putValue(NAME, gridFore);
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JEnvironment.GRID_FOREGROUND = !(JEnvironment.GRID_FOREGROUND);
            changeStates();
            JDocumentFrame frame = (JDocumentFrame) outer.jDesktopPane1.getSelectedFrame();
            if (frame != null) {
                frame.getViewer().isDraftMode = false;
                frame.getViewer().getScroller().repaint();
            }
        }
    }

    private class GridOptionAction extends AbstractAction {

        public GridOptionAction() {
            super();
            putValue(NAME, "オプション(O)");
            putValue(MNEMONIC_KEY, KeyEvent.VK_O);
            //putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_O,ActionEvent.CTRL_MASK|ActionEvent.ALT_MASK));
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JDocumentFrame frame = (JDocumentFrame) outer.jDesktopPane1.getSelectedFrame();
            if (frame == null) {
                return;
            }
            JGridOption dlg = new JGridOption(outer, frame.getViewer());
            if (dlg.showDialog()) {
                frame.getViewer().isDraftMode = false;
                frame.getViewer().getScroller().repaint();
            }
        }
    }

    private class SendGuidAction extends AbstractAction {

        public SendGuidAction() {
            putValue(NAME, "ガイドライン作成(M)");
            putValue(MNEMONIC_KEY, KeyEvent.VK_M);
            //putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK));
            stateChanged((JDocumentFrame) outer.jDesktopPane1.getSelectedFrame());
        }

        public void stateChanged(JDocumentFrame frame) {
            if (frame == null) {
                setEnabled(false);
            } else {
                JRequest req = frame.getViewer().getCurrentRequest();
                boolean b = false;
                for (int i = 0; i < req.size(); i++) {
                    if (req.get(i) instanceof JLeaf) {
                        JLeaf jl = (JLeaf) req.get(i);
                        if (jl.canBeGuide()) {
                            b = true;
                            break;
                        }
                    }
                }
                setEnabled(b);
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            NamableCompoundEdit cEdit = null;
            JDocumentFrame frame = (JDocumentFrame) outer.jDesktopPane1.getSelectedFrame();
            if (frame != null) {
                JRequest req = frame.getViewer().getCurrentRequest();
                Vector<JLeaf> leafs = new Vector<JLeaf>();
                for (int i = 0; i < req.size(); i++) {
                    if (req.get(i) instanceof JLeaf) {
                        JLeaf jl = (JLeaf) req.get(i);
                        if (jl.canBeGuide()) {
                            leafs.add(jl);
                        }
                    }
                }
                for (int i = 0; i < leafs.size(); i++) {
                    JLeaf jl = leafs.get(i);
                    if (cEdit == null) {
                        cEdit = new NamableCompoundEdit("ガイドライン作成");
                    }
                    cEdit.addEdit(new JDeleteObjectEdit(frame.getViewer(), jl, "ガイドライン作成"));
                    cEdit.addEdit(new JInsertObjectEdit(frame.getViewer(), jl, frame.getViewer().getCurrentPage().getGuidLayer(), "ガイドライン作成"));
                }
                if (cEdit != null) {
                    cEdit.end();
                    frame.getDocument().fireUndoEvent(cEdit);
                }
            }
        }
    }

    private class ReleaseGuidAction extends AbstractAction {

        public ReleaseGuidAction() {
            putValue(NAME, "ガイドライン解除(R)");
            putValue(MNEMONIC_KEY, KeyEvent.VK_R);
            stateChanged((JDocumentFrame) outer.jDesktopPane1.getSelectedFrame());
        }

        public void stateChanged(JDocumentFrame frame) {
            if (frame == null) {
                this.setEnabled(false);
                return;
            }
            JRequest req = frame.getViewer().getCurrentRequest();
            if (req != null) {
                boolean en = false;
                for (int i = 0; i < req.size(); i++) {
                    if (req.get(i) instanceof JLeaf) {
                        JLeaf jl = (JLeaf) req.get(i);
                        if (jl.getLayer() instanceof JGuidLayer) {
                            en = true;
                            break;
                        }
                    }
                }
                this.setEnabled(en);
            } else {
                this.setEnabled(false);
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JDocumentFrame frame = (JDocumentFrame) outer.jDesktopPane1.getSelectedFrame();
            NamableCompoundEdit cEdit=null;
            if (frame != null) {
                JRequest req = frame.getViewer().getCurrentRequest();
                Vector<JLeaf> leafs = new Vector<JLeaf>();
                for (int i = 0; i < req.size(); i++) {
                    if (req.get(i) instanceof JLeaf) {
                        JLeaf jl = (JLeaf) req.get(i);
                        if (jl.canBeGuide()) {
                            leafs.add(jl);
                        }
                    }
                }
                for (int i = 0; i < leafs.size(); i++) {
                    JLeaf jl = leafs.get(i);
                    if (cEdit == null) {
                        cEdit = new NamableCompoundEdit("ガイドライン解除");
                    }
                    cEdit.addEdit(new JDeleteObjectEdit(frame.getViewer(), jl, "ガイドライン解除"));
                    cEdit.addEdit(new JInsertObjectEdit(frame.getViewer(), jl, frame.getViewer().getCurrentPage().getCurrentLayer(), "ガイドライン解除"));
                }
                if (cEdit != null) {
                    cEdit.end();
                    frame.getDocument().fireUndoEvent(cEdit);
                }
                for(int i=0;i<leafs.size();i++){
                    req.add(leafs.get(i));
                }
            }
        }
    }

    private class LockGuidAction extends AbstractAction {

        public LockGuidAction() {
            putValue(NAME, "ガイドラインのロック解除(N)");
            putValue(MNEMONIC_KEY, KeyEvent.VK_N);
            stateChanged((JDocumentFrame) outer.jDesktopPane1.getSelectedFrame());
        }

        public void stateChanged(JDocumentFrame frame) {
            if (frame == null) {
                this.setEnabled(false);
                return;
            }
            this.setEnabled(true);
            JPage pg = frame.getViewer().getCurrentPage();
            if (!pg.getGuidLayer().isLocked()) {
                putValue(NAME, "ガイドラインをロック(O)");
                putValue(MNEMONIC_KEY, KeyEvent.VK_O);
            } else {
                putValue(NAME, "ガイドラインのロック解除(N)");
                putValue(MNEMONIC_KEY, KeyEvent.VK_N);
            }

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JDocumentFrame frame = (JDocumentFrame) outer.jDesktopPane1.getSelectedFrame();
            if (frame != null) {
                JPage page = frame.getViewer().getCurrentPage();
                if (!page.getGuidLayer().isLocked()) {
                    JRequest req = frame.getViewer().getCurrentRequest();

                    for (int i = 0; i < req.size(); i++) {
                        Object o = req.get(i);
                        if (o instanceof JLeaf) {
                            JLeaf jl = (JLeaf) o;
                            if (jl.getLayer() instanceof JGuidLayer) {
                                req.remove(i--);
                            }
                        } else {
                            req.remove(i--);
                        }
                    }
                }
                page.getGuidLayer().setLocked(!(page.getGuidLayer().isLocked()));
                stateChanged(frame);
                frame.getViewer().isDraftMode = false;
                frame.getViewer().repaint();
            }
        }
    }

    private class ShowGuidAction extends AbstractAction {

        public ShowGuidAction() {
            putValue(NAME, "ガイドラインを隠す(H)");
            putValue(MNEMONIC_KEY, KeyEvent.VK_H);
        }

        public void stateChanged(JDocumentFrame frame) {
            if (frame == null) {
                this.setEnabled(false);
                return;
            }
            this.setEnabled(true);
            JPage pg = frame.getViewer().getCurrentPage();
            if (pg.getGuidLayer().isVisible()) {
                putValue(NAME, "ガイドラインを隠す(H)");
                putValue(MNEMONIC_KEY, KeyEvent.VK_H);
            } else {
                putValue(NAME, "ガイドラインを表示(V)");
                putValue(MNEMONIC_KEY, KeyEvent.VK_V);
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JDocumentFrame frame = (JDocumentFrame) outer.jDesktopPane1.getSelectedFrame();
            if (frame != null) {
                JPage page = frame.getViewer().getCurrentPage();
                page.getGuidLayer().setVisible(!(page.getGuidLayer().isVisible()));
                JRequest req=frame.getViewer().getCurrentRequest();
                for (int i=0;i<req.size();i++){
                    if (req.get(i) instanceof JLeaf){
                        JLeaf jl=(JLeaf)req.get(i);
                        if (jl.getLayer() instanceof JGuidLayer){
                            req.remove(i--);
                        }
                    }
                }
                stateChanged(frame);
                frame.getViewer().isDraftMode = false;
                frame.getViewer().repaint();
            }
        }
    }
    private class ClearGuidAction extends AbstractAction{
        public ClearGuidAction(){
            putValue(NAME,"ガイドラインをクリア(C)");
            putValue(MNEMONIC_KEY,KeyEvent.VK_C);
            stateChanged((JDocumentFrame) outer.jDesktopPane1.getSelectedFrame());
        }
        public void stateChanged(JDocumentFrame frame){
            if (frame==null){
                this.setEnabled(false);
            }else{
                JPage page=frame.getViewer().getCurrentPage();
                setEnabled(!page.getGuidLayer().isEmpty());
            }
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            JDocumentFrame frame=(JDocumentFrame)outer.jDesktopPane1.getSelectedFrame();
            if (frame !=null){
                JGuidLayer layer=frame.getViewer().getCurrentPage().getGuidLayer();
                NamableCompoundEdit cEdit=null;
                for (int i=0;i<layer.size();i++){
                    JLeaf jl=layer.get(i--);
                    if (cEdit==null){
                        cEdit=new NamableCompoundEdit("ガイドラインをクリア");
                    }
                    cEdit.addEdit(new JDeleteObjectEdit(frame.getViewer(),jl,"ガイドラインをクリア"));
                }
                if (cEdit !=null){
                    cEdit.end();
                    frame.getDocument().fireUndoEvent(cEdit);
                }
            }
            stateChanged(frame);
        }
        
    }
    private class NamableCompoundEdit extends CompoundEdit {

        private String presentationName = null;

        public NamableCompoundEdit(String name) {
            presentationName = name;
        }

        @Override
        public String getPresentationName() {
            return presentationName;
        }
    }

    public class DocumentSelector extends JCheckBoxMenuItem implements ActionListener {

        private JDocumentFrame frame;

        public DocumentSelector(JDocumentFrame f) {
            super();
            frame = f;
            setText(frame.getDocument().getName());
            addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                frame.setSelected(true);
            } catch (PropertyVetoException ex) {
                ex.printStackTrace();
            }
        }
    }
}
