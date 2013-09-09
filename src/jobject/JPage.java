/*
 * JPage.java
 *
 * Created on 2007/08/27, 10:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jobject;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.ItemEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import jobject.naming.JNameComparator;
import jobject.naming.JNaming;
import jprinter.JPageFormat;
import jscreen.JEnvironment;
import jscreen.JRequest;
import jui.layer.JPageTreeModel;

/**
 *
 *JDocument上のページ。
 * @author i002060
 */
public class JPage extends JObject<JDocument, JLayer> implements Printable {

    private JPageFormat pageFormat;
    private JEnvironment pageEnv;
    private JLayer currentLayer = null;
    //
    private JGuidLayer guidLayer =null;
    private transient UndoManager undoManager;
    private transient JRequest request;
    private transient SortedMap<String, JLeaf> nameTable = null;
    private transient JPageTreeModel treeModel = null;    //
    private static boolean IS_REGISTERED = false;
    //
    static final long serialVersionUID = 110l;

    /**
     * Creates a new instance of JPage
     */
    public JPage() {
        this(new JLayer());

    }

    public JPage(JLayer layer) {
        treeModel = new JPageTreeModel(this);
        pageFormat = new JPageFormat(PrinterJob.getPrinterJob().defaultPage());
        pageEnv = new JEnvironment();
        pageEnv.setPaper(pageFormat);
        add(layer);
        undoManager = new UndoManager();
        request = new JRequest(this);
        IS_REGISTERED = isRegisterd();
        guidLayer=new JGuidLayer();
        guidLayer.setParent(this);

    }

    public UndoManager getUndoManager() {
        return undoManager;
    }

    public JRequest getRequest() {
        return request;
    }

    public void fireChangeEvent(Object o, int stateChange) {
        getDocument().fireItemEvent(o, stateChange);
    }

    public JLayer getCurrentLayer() {
        if (currentLayer == null || !contains(currentLayer)) {
            currentLayer = get(0);
        }
        return currentLayer;
    }

    public boolean setCurrentLayer(JLayer layer) {
        if (!contains(layer)) {
            return true;
        }
        if (layer != currentLayer) {
            currentLayer = layer;
            getDocument().fireItemEvent(this, ItemEvent.SELECTED);
        }
        return true;
    }

    public JLayer getAvilableLayer() {
        if (getCurrentLayer().isEnabled() && getCurrentLayer().isVisible()) {
            return getCurrentLayer();
        }
        for (int i = size() - 1; i >= 0; i--) {
            if (get(i).isVisible() && get(i).isEnabled()) {
                return get(i);
            }
        }
        return null;
    }

    public JEnvironment getEnvironment() {
        return pageEnv;
    }

    public JPageFormat getPageFormat() {
        return pageFormat;
    }

    public void setPageFormat(JPageFormat page) {
        pageFormat = page;
        pageEnv.setPaper(page);
    }

    public JGuidLayer getGuidLayer() {
        return guidLayer;
    }

    @Override
    public void transform(AffineTransform tr, JRequest req, Point p) {
        //Do Nothing
    }
    @Override
    public void transform(AffineTransform tr){
        //Do Nothing
    }
    @Override
    public int hitByPoint(JEnvironment env, JRequest req, Point2D point) {
        if (isLocked() || !isVisible()) {
            return JRequest.HIT_NON;
        //Send to Children;
        }
        int ret = JRequest.HIT_NON;
        for (int i = size() - 1; i >= 0; i--) {
            if ((ret = get(i).hitByPoint(env, req, point)) != JRequest.HIT_NON) {
                break;
            }
        }
        if (ret == JRequest.HIT_NON) {
            ret = getGuidLayer().hitByPoint(env, req, point);
        }
        return ret;
    }

    @Override
    public void hitByRect(JEnvironment env, JRequest req, Rectangle2D rect) {
        if (isLocked() || !isVisible()) {
            return;
        //Send to Children;
        }
        for (int i = 0; i < size(); i++) {
            get(i).hitByRect(env, req, rect);
        }
        getGuidLayer().hitByRect(env, req, rect);
    }

    @Override
    public UndoableEdit updateTransform(JEnvironment env) {
        //Do Nothing
        return null;
    }

    @Override
    public UndoableEdit updateRotate(JEnvironment env, double rotation) {
        return null;
    }

    @Override
    public void paint(Rectangle2D clip, Graphics2D g) {
        if (getGuidLayer().isOnTop()) {
            super.paint(clip, g);
            getGuidLayer().paint(clip, g);
        } else {
            getGuidLayer().paint(clip, g);
            super.paint(clip, g);
        }
        /*
        if (!IS_REGISTERED) {
            g = (Graphics2D) g.create();
            g.setColor(Color.BLACK);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
            Font f = new Font(Font.SANS_SERIF, Font.PLAIN, 6);
            g.setFont(f);
            String waterMark = "Designed With JDrafter(http://jdrafter.com)";
            FontMetrics fm = g.getFontMetrics(f);
            PageFormat pf = getPageFormat();
            Rectangle2D r = fm.getStringBounds(waterMark, g);
            g.drawString(waterMark, (float) (pf.getImageableX() + pf.getImageableWidth() - r.getWidth()),
                    (float) (pf.getImageableY() + pf.getImageableHeight() - r.getHeight() + fm.getAscent()));
            g.dispose();
        }
         */
    }

    @Override
    public void paintThis(Rectangle2D clip, Graphics2D g) {
        //Do Nothing
    }

    @Override
    public void paintPreview(JEnvironment env, JRequest req, Graphics2D g) {
        //Do Nothing
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        JPage ret = new JPage();
        ret.clear();
        ret.pageFormat = pageFormat.clone();
        ret.pageEnv = pageEnv.clone();
        ret.guidLayer=(JGuidLayer)guidLayer.clone();
        ret.guidLayer.setParent(ret);
        for (int i = 0; i < size(); i++) {
            ret.add((JLayer) get(i).clone());
        }
        ret.currentLayer = ret.get(indexOf(currentLayer));
        return ret;
    }

    @Override
    public Rectangle2D getSelectionBounds() {
        return null;
    }

    @Override
    public Rectangle2D getOriginalSelectionBounds(double x, double y) {
        return null;
    }

    @Override
    public Rectangle2D getBounds() {
        Rectangle2D ret = null;
        for (int i = 0; i < size(); i++) {
            if (get(i).getBounds() == null) {
                continue;
            }
            if (ret == null) {
                ret = get(i).getBounds();
            } else {
                ret.add(get(i).getBounds());
            }
        }
        return ret;
    }

    @Override
    public Shape getShape() {
        return null;
    }

    @Override
    public String getPrefixer() {
        return "Page";
    }

    @Override
    public String getName() {
        if (getParent() == null) {
            return getPrefixer();
        } else {
            return getPrefixer() + String.valueOf(getParent().indexOf(this) + 1);
        }
    }

    @Override
    public JPage getPage() {
        return this;
    }
    //Naming Mechanism
    public void sendPacket() {
        if (nameTable == null) {
            nameTable = new ConcurrentSkipListMap<String, JLeaf>(JNameComparator.getInstance());
        }
        for (int i = 0; i < objectPacket.size(); i++) {
            JLeaf jl = objectPacket.get(i);
            int type = packetType.get(i);
            String name = jl.getName();
            switch (type) {
                case ADDING:
                    namingObjects(jl, nameTable);
                    break;
                case REMOVING:
                    removeJObjects(jl, nameTable);
                    break;
            }
        }
        treeModel.reload();
        objectPacket.clear();
        packetType.clear();
    }

    private void namingObjects(JLeaf jl, SortedMap<String, JLeaf> map) {
        if (jl instanceof JObject) {
            JObject jo = (JObject) jl;
            for (int i = 0; i < jo.size(); i++) {
                namingObjects(jo.get(i), map);
            }
        }
        if (jl.getPage() == null) {
            return;
        }
        String name = jl.getName();
        if (name.trim().equals("") || isDuplicates(jl)) {
            name = getProperName(jl.getPrefixer());
            jl.setName(name);
        }
        map.put(name, jl);
    }

    private void removeJObjects(JLeaf jl, SortedMap<String, JLeaf> map) {
        if (jl instanceof JObject) {
            JObject jo = (JObject) jl;
            for (int i = 0; i < jo.size(); i++) {
                removeJObjects(jo.get(i), map);
            }
        }
        map.remove(jl.getName());
    }

    public JLeaf get(String name) {
        if (!objectPacket.isEmpty()) {
            sendPacket();
        }
        return nameTable.get(name);
    }

    public SortedMap<String, JLeaf> getNameTable() {
        return nameTable;
    }

    public JPageTreeModel getTreeModel() {
        return treeModel;
    }

    private boolean isDuplicates(JLeaf jl) {
        if (!nameTable.containsKey(jl.getName())) {
            return false;
        }
        return (nameTable.get(jl.getName()) != jl);
    }

    public String getProperName(String prefixer) {
        String min = prefixer + "1";
        String max = prefixer + String.valueOf(Integer.MAX_VALUE).trim();
        SortedMap<String, JLeaf> map = nameTable.subMap(min, max);
        if (map.isEmpty()) {
            return min;
        }
        JNaming nm = new JNaming(map.lastKey());
        return (prefixer + String.valueOf(nm.getIndex() + 1).trim());
    }

    private void makeNameTable() {
        if (nameTable == null) {
            nameTable = new ConcurrentSkipListMap<String, JLeaf>(JNameComparator.getInstance());
        }
        nameTable.clear();
        for (int i = 0; i < size(); i++) {
            JLeaf jl = get(i);
            if (jl instanceof JObject) {
                setNames(nameTable, (JObject) jl);
            } else {
                nameTable.put(jl.getName(), jl);
            }
        }
        treeModel.reload();
    }

    private void setNames(SortedMap<String, JLeaf> map, JObject o) {
        for (int i = 0; i < o.size(); i++) {
            JLeaf jl = o.get(i);
            if (jl instanceof JObject) {
                setNames(map, (JObject) jl);
            } else {
                map.put(jl.getName(), jl);
            }
        }
        map.put(o.getName(), o);
    }

    public static final boolean isRegisterd() {
        String s = getRegisterKey();
        if (s == null) {
            return false;
        }
        return isVaridRegister(s);
    }

    public static final String getRegisterKey() {
        File f = new File(System.getProperty("user.dir") + File.separator + "regkey.txt");
        if (!f.exists()) {
            return null;
        }
        StringBuffer stb = null;
        try {
            FileReader fs = new FileReader(f);
            CharBuffer buffer = CharBuffer.allocate(100);
            fs.read(buffer);
            fs.close();
            buffer.position(0);
            stb = new StringBuffer();
            for (int i = 0; i < buffer.length(); i++) {
                char c = buffer.get(i);
                if (c == '\n') {
                    break;
                }
                if (c != 0) {
                    stb.append(c);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        if (stb == null || stb.toString().equals("")) {
            return null;
        }
        return stb.toString();
    }

    public static final boolean isVaridRegister(String s) {
        if (s == null) {
            return false;
        }
        int cn = 0;
        for (int i = 0; i < s.length(); i++) {
            cn += s.charAt(i);
        }
        return (cn == 1147);
    }

    public static final void putLocalRegister(String s) {
        if (IS_REGISTERED) {
            return;
        }
        IS_REGISTERED = isVaridRegister(s);
    }

    public static final void putRegsiter(String s) {
        File f = new File(System.getProperty("user.dir") + File.separator + "regkey.txt");
        try {
            FileWriter fw = new FileWriter(f);
            fw.write(s);
            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        IS_REGISTERED = isRegisterd();
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        for (int i = 0; i < size(); i++) {
            get(i).setParent(this);
        }
        treeModel = new JPageTreeModel(this);
        pageEnv = new JEnvironment();
        pageEnv.setPaper(pageFormat);
        undoManager = new UndoManager();
        request = new JRequest(this);
        makeNameTable();
        treeModel.reload();
        IS_REGISTERED = isRegisterd();
        if (guidLayer==null){
            guidLayer=new JGuidLayer();
        }
        guidLayer.setParent(this);
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        int index = getParent().indexOf(this);
        if (index != pageIndex) {
            return NO_SUCH_PAGE;
        }
        boolean vb=getGuidLayer().isVisible();
        getGuidLayer().setVisible(false);
        this.paint(this.getBounds(), (Graphics2D) graphics);
        getGuidLayer().setVisible(vb);
        return PAGE_EXISTS;
    }
}
