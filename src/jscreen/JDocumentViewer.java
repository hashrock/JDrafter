/*
 * JDocumentViewer.java
 *
 * Created on 2007/08/19, 8:58
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jscreen;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.VolatileImage;
import java.awt.print.PageFormat;
import javax.swing.JLayeredPane;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import jactions.JTransferHandler;
import java.awt.image.BufferedImage;
import jobject.JDocument;
import jobject.JLayer;
import jobject.JPage;
import jpaint.JBlurPaint;
import jprinter.JPageFormat;
import jobject.text.InlineTextPane;

/**
 *ドキュメントを表示するためのコンポーネントです。
 * @author TI
 */
public class JDocumentViewer extends JLayeredPane implements Scrollable {

    private static final Color BACKCOLOR = Color.LIGHT_GRAY;
    private static final Color IMAGEAREA_COLOR = new Color(0.6f, 0.6f, 0.8f);
    private JDocument document = null;
    private JScroller scroller = null;
    private JDragPane dragPane = null;
    private JTransferHandler transferHandler = null;
    //private VolatileImage backBuffer = null;
    private BufferedImage backBuffer=null;
    private Rectangle oldView = null;
    private Point oldOffset = null;
    private InlineTextPane textPane = null;
    public boolean isDraftMode = false;

    /** 
     * JDocumentViewerのインスタンスを構築します.
     */
    public JDocumentViewer() {
        //backBuffer = this.createVolatileImage(800, 600);
        backBuffer=(BufferedImage)this.createImage(800,600);
        setDocument(new JDocument(new JPage()));
        dragPane = new JDragPane(this);
        textPane = new InlineTextPane(this);
        dragPane.setLayout(new BorderLayout());
        dragPane.add(textPane, BorderLayout.CENTER);
        textPane.setVisible(false);
        oldView = null;
        transferHandler = new JTransferHandler(this);
        document.addItemListener(transferHandler);
    }

    /**テキストオブジェクトの編集するInlineTextPaneを返します.*/
    public InlineTextPane getTextPane() {
        return textPane;
    }

    /**
     * このJDocumentViewerが表示対象とするJDocumentを設定します.
     * @param bk このJDocmentViewerが表示対象とするJDocument
     */
    public void setDocument(JDocument bk) {
        if (this.document != null) {
            document.setViewer(null);
        }
        this.document = bk;
        bk.setViewer(this);
        if (dragPane != null) {
            dragPane.documentChagned();
        // dragPane=new JDragPane(this);
        }
    }

    /**
     * イベントの取得及びイベント状態を描画するJDragPaneを取得します.
     * @return イベントの取得及びイベント状態を描画するJDragPane
     */
    public JDragPane getDragPane() {
        return dragPane;
    }

    /**
     * このJDocumentViewerの編集機能を実装するJTransferHandlerを返します.
     * @return このJDocumentVierの編集機能を実装するJTransferHandler
     */
    public JTransferHandler getJTransferHandler() {
        return transferHandler;
    }

    /**
     * ダイアログを指定し、現在のページの用紙情報を更新します.
     */
    public void pageSetup() {
        JPage cpage = getCurrentPage();
        if (cpage != null) {
            PageFormat p = document.getPrinterJob().pageDialog(cpage.getPageFormat());
            if (p != null) {
                cpage.setPageFormat(new JPageFormat(p));
                isDraftMode = false;
                repaint();
            }
        }
    }

    /**
     * 現在有効なJDocumentを返します.
     * @return 現在有効なJDocument
     */
    public JDocument getDocument() {
        return document;
    }

    /**
     * このJDocumentViewerを所有するJScrollerを設定します.
     * @param sc  設定するJScroller
     */
    public void setScroller(JScroller sc) {
        scroller = sc;
    }

    /**
     * このJDocumentViewerを所有するJScrollerを返します。
     * @return このJDocumentViewerを所有するJScroller
     */
    public JScroller getScroller() {
        return scroller;
    }

    /**
     * 用紙のサイズ、表示倍率が変更されるなど、このJDocumentViewerのサイズの変更が必要な場合に
     * パラメータを更新し、最適な表示サイズを設定します.
     */
    public void adjustSize() {
        JEnvironment env = document.getEnvironment();
        double ratio = env.getToScreenRatio();
        double sw = scroller.getViewport().getWidth() / ratio;
        double sh = scroller.getViewport().getHeight() / ratio;
        double w = env.getPaperRect().getWidth() * 1.1;
        double h = env.getPaperRect().getHeight() * 1.1;
        if (w < sw) {
            w = sw;
        }
        if (h < sh) {
            h = sh;
        }
        Dimension dm = new Dimension((int) (w * ratio), (int) (h * ratio));
        Point p = culcOffset();

        this.setPreferredSize(dm);
        this.setSize(dm);
        env.setOffsetByScreen(p.x, p.y);
        scroller.adjustSize();
    }

    /**
     * カレントのJPageを返します.
     * @return カレントのJPage
     */
    public JPage getCurrentPage() {
        return document.getCurrentPage();
    }

    /**
     * 編集の優先順位がもっとも高いJLayerを返します.
     * @return 編集の優先順位がもっとも高いJLayer
     */
    public JLayer getAvailableLayer() {
        return getCurrentPage().getAvilableLayer();
    }

    /**
     * 描画オブジェクトの選択状態を示す有効なJRequestを返します.
     * @return 描画オブジェクトの選択状態を示すJRequest
     */
    public JRequest getCurrentRequest() {
        return dragPane.getCurrentRequest();
    }

    /**
     * 現在の表示環境を保持するJEnvironmentを返します.
     * @return
     */
    public JEnvironment getEnvironment() {
        return document.getCurrentPage().getEnvironment();
    }

    /**
     * 前後のViewからペーパー左上端の移動量をスクリーン座標単位で算出します.
     *@return ペーパー左上端からの移動量
     */
    private Point culcOffset() {
        JEnvironment env = getEnvironment();
        Rectangle view = scroller.getViewport().getViewRect();
        double ratio = env.getToScreenRatio();
        double pw = env.getPaperRect().getWidth() * ratio;
        double ph = env.getPaperRect().getHeight() * ratio;
        double w = pw * 1.1;
        double h = ph * 1.1;
        if (w < view.width) {
            w = view.width;
        }
        if (h < view.height) {
            h = view.height;
        }
        return new Point((int) ((w - pw) / 2), (int) ((h - ph) / 2));
    }

    /**
     * このコンポーネントの再描画が必要な部分を描画します。
     * @param g グラフィックスコンテキスト
     */
    @Override
    public synchronized void paintComponent(Graphics g) {
        Rectangle view = scroller.getViewport().getViewRect();
        //<editor-fold defaultstate="collapsed" desc="最初の描画">
        //if (backBuffer == null || oldView == null || backBuffer.contentsLost()) {
        if (backBuffer == null || oldView == null ) {
            int w = 800;
            int h = 600;
            if (w < view.width) {
                w = view.width;
            }
            if (h < view.height) {
                h = view.height;
            }
            if (backBuffer != null) {
                backBuffer.flush();
            }
            //backBuffer = this.createVolatileImage(w, h);
            backBuffer=(BufferedImage)this.createImage(w,h);
            Graphics2D gb = backBuffer.createGraphics();
            gb.translate(-view.x, -view.y);
            Rectangle clp = new Rectangle(0, 0, backBuffer.getWidth(), backBuffer.getHeight());
            gb.setClip(clp);
            getEnvironment().addDrawClip(clp);
            paintAll(gb);
            oldView = view;
            oldOffset = culcOffset();
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="リサイズ">
        if (oldView.width != view.width || oldView.height != view.height) {
            int w = backBuffer.getWidth();
            int h = backBuffer.getHeight();
            if (w < view.width || h < view.height) {
                if (w < view.width) {
                    w = view.width;
                }
                if (h < view.height) {
                    h = view.height;
                }
                //VolatileImage newBuffer = this.createVolatileImage(w, h);
                BufferedImage newBuffer=(BufferedImage)this.createImage(w,h);
                Graphics2D gb = newBuffer.createGraphics();
                gb.drawImage(backBuffer, 0, 0, this);
                backBuffer.flush();
                backBuffer = newBuffer;
                gb.dispose();
            }
            Point ofs = culcOffset();
            int ofsX = view.x - oldView.x - (int) (ofs.x - oldOffset.x);
            int ofsY = view.y - oldView.y - (int) (ofs.y - oldOffset.y);
            if (ofsX != 0 || ofsY != 0) {
                getEnvironment().setOffsetByScreen(ofs.x, ofs.y);
                Graphics2D gb = backBuffer.createGraphics();
                gb.copyArea(0, 0, backBuffer.getWidth(), backBuffer.getHeight(), -ofsX, -ofsY);
                gb.translate(-view.x, -view.y);
                Rectangle r = new Rectangle();
                if (ofsX != 0) {
                    if (ofsX > 0) {
                        r.setFrame(view.x + view.width - ofsX, view.y, ofsX, view.height);
                    } else {
                        r.setFrame(view.x, view.y, -ofsX, view.height);
                    }
                    getEnvironment().addDrawClip(r);
                    gb.setClip(r);
                    paintAll(gb);
                    getEnvironment().flushClip();
                }
                if (ofsY != 0) {
                    if (ofsY > 0) {
                        r.setFrame(view.x, view.y + view.height - ofsY, view.width, ofsY);
                    } else {
                        r.setFrame(view.x, view.y, view.width, -ofsY);
                    }
                    getEnvironment().addDrawClip(r);
                    gb.setClip(r);
                    paintAll(gb);
                    getEnvironment().flushClip();

                }
                gb.dispose();
            }
            int dw = view.width - oldView.width;
            int dh = view.height - oldView.height;
            Graphics2D gb = backBuffer.createGraphics();
            gb.translate(-view.x, -view.y);
            Rectangle r = new Rectangle();
            if (dw > 0) {
                r.setFrame(view.x + view.width - dw, view.y, dw, view.height);
                getEnvironment().addDrawClip(r);
                gb.setClip(r);
                paintAll(gb);
                getEnvironment().flushClip();
            }
            if (dh > 0) {
                r.setFrame(view.x, view.y + view.height - dh, view.width, dh);
                getEnvironment().addDrawClip(r);
                gb.setClip(r);
                paintAll(gb);
                getEnvironment().flushClip();
            }
            gb.dispose();
        } else {
            //</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="スクロール">
            if (view.y != oldView.y || view.x != oldView.x) {
                int dx = view.x - oldView.x;
                int dy = view.y - oldView.y;
                Graphics2D gb = backBuffer.createGraphics();
                gb.copyArea(0, 0, backBuffer.getWidth(), backBuffer.getHeight(), -dx, -dy);
                gb.translate(-view.x, -view.y);
                Rectangle r = new Rectangle();
                if (dx != 0) {
                    if (dx > 0) {
                        r.setFrame(view.x + view.width - dx, view.y, dx, view.height);
                    } else {
                        r.setFrame(view.x, view.y, -dx, view.height);
                    }
                    getEnvironment().addDrawClip(r);
                    gb.setClip(getEnvironment().getScreenClip());
                    paintAll(gb);
                    getEnvironment().flushClip();
                }
                if (dy != 0) {
                    if (dy > 0) {
                        r.setFrame(view.x, view.y + view.height - dy, view.width, dy);
                    } else {
                        r.setFrame(view.x, view.y, view.width, -dy);
                    }
                    getEnvironment().addDrawClip(r);
                    gb.setClip(getEnvironment().getScreenClip());
                    paintAll(gb);
                    getEnvironment().flushClip();

                }
                gb.dispose();
            }
        }
        //</editor-fold>

        if (!isDraftMode) {
            Graphics2D gb = backBuffer.createGraphics();
            gb.translate(-view.x, -view.y);
            //Rectangle clp=(Rectangle)view.clone();
            Rectangle clp = new Rectangle(0, 0, getWidth(), getHeight());
            gb.setClip(clp);
            getEnvironment().addDrawClip(clp);
            paintAll(gb);
            getEnvironment().flushClip();
            gb.dispose();
        } else if (getEnvironment().getClip() != null) {
            Graphics2D gb = backBuffer.createGraphics();
            gb.translate(-view.x, -view.y);
            gb.setClip(getEnvironment().getScreenClip());
            paintAll(gb);
            getEnvironment().flushClip();
            gb.dispose();
        }
        g.drawImage(backBuffer, view.x, view.y, this);
        getEnvironment().flushClip();
        oldOffset = culcOffset();
        oldView = view;
        isDraftMode = true;
    }

    /**
     * このコンポーネント全ての要素を描画します.
     * @param g グラフィックスコンテキスト
     */
    private void paintAll(Graphics2D g) {
        Rectangle paintHere = g.getClipBounds();
        if (paintHere == null) {
            return;
        }
        JEnvironment env = getEnvironment();
        AffineTransform af = env.getToScreenTransform();
        BasicStroke str = new BasicStroke(1f);
        Rectangle paper = af.createTransformedShape(env.getPaperRect()).getBounds();
        Shape paperOut = str.createStrokedShape(paper);
        Rectangle img = af.createTransformedShape(env.getImageRect()).getBounds();
        Shape imgOut = str.createStrokedShape(img);
        //背景
        g.setColor(BACKCOLOR);
        g.fill(paintHere);
        //影
        Rectangle shadowRect = new Rectangle(paper.x + 4, paper.y + 4, paper.width, paper.height);
        JBlurPaint bp = new JBlurPaint(shadowRect, 4f, Color.BLACK, 0.8f);
        Rectangle insec = new Rectangle(paper.x + paper.width, paper.y, 8, paper.height + 8);
        insec = insec.intersection(paintHere);
        if (!insec.isEmpty()) {
            g.setPaint(bp);
            g.fill(insec);
        }
        insec.setFrame(paper.x, paper.y + paper.height, paper.width, 8);
        insec = insec.intersection(paintHere);
        if (!insec.isEmpty()) {
            g.setPaint(bp);
            g.fill(insec);
        }
        //紙
        insec = paper.intersection(paintHere);
        if (!insec.isEmpty()) {
            g.setColor(Color.WHITE);
            g.fill(insec);
        }
        if (!env.GRID_FOREGROUND) {
            paintGrid(g);

            if (paperOut.intersects(paintHere)) {
                g.setColor(Color.BLACK);
                g.draw(paper);
            }
            //イメージエリア
            if (imgOut.intersects(paintHere)) {
                g.setColor(IMAGEAREA_COLOR);
                g.draw(img);
            }
        }

        JPage crPage = getDocument().getCurrentPage();
        Graphics2D gs = (Graphics2D) g.create();
        gs.transform(af);
        if (JEnvironment.PAINT_ANTI_AREASING) {
            gs.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            gs.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_OFF);
        }
        crPage.paint(crPage.getEnvironment().getClip(), gs);
        gs.dispose();
        if (env.GRID_FOREGROUND) {
            paintGrid(g);

            if (paperOut.intersects(paintHere)) {
                g.setColor(Color.BLACK);
                g.draw(paper);
            }
            //イメージエリア
            if (imgOut.intersects(paintHere)) {
                g.setColor(IMAGEAREA_COLOR);
                g.draw(img);
            }
        }
    }

    /**
     * グリッドを描画します.
     * @param g グラフィックスコンテキスト.
     */
    private void paintGrid(Graphics2D g) {
        JEnvironment env = getEnvironment();
        if (!env.isGridVisible()) {
            return;
        }
        AffineTransform toSecreen = env.getToScreenTransform();
        Rectangle paper = toSecreen.createTransformedShape(env.getPaperRect()).getBounds();
        Rectangle clp = g.getClipBounds();
        double mg = env.getMagnification();
        clp.x -= (int) (4 * mg);
        clp.y -= (int) (4 * mg);
        clp.width += (int) (8 * mg);
        clp.height += (int) (8 * mg);
        Rectangle paintHere = clp.intersection(paper);
        if (paintHere.isEmpty()) {
            return;
        //
        }
        double ratio = env.getToScreenRatio();
        //ゲージ原点のスクリーン座標
        double offsX = env.getOffsetByScreen().x + env.getGaugeOffset().getX() * ratio;
        double offsY = env.getOffsetByScreen().y + env.getGaugeOffset().getY() * ratio;
        //小ゲージ間の間隔（スクリーン座標)
        double sGridIval = ratio * env.getGridSize() / env.getGridDivision();
        //小ゲージを表示する間隔(間隔3ピクセル以内なら飛ばす)
        int sIval = 1;
        while (sGridIval < 4) {
            sIval *= 2;
            sGridIval *= 2;
        }
        //開始位置のゲージカウント
        int start = (int) Math.round((paintHere.x - offsX) / sGridIval);
        int end = (int) Math.round((paintHere.x + paintHere.width - offsX) / sGridIval);
        g.setStroke(JEnvironment.GUAGE_STROKE);
        g.setColor(env.DIVIDE_GRID_COLOR);
        int lp;
        for (int i = start; i < end; i++) {
            lp = (int) (i * sGridIval + offsX);
            if (i % env.getGridDivision() == 0) {
                g.setColor(env.GRID_COLOR);
            } else {
                g.setColor(env.DIVIDE_GRID_COLOR);
            }
            g.drawLine(lp, paintHere.y, lp, paintHere.y + paintHere.height);
        }
        start = (int) Math.round((paintHere.y - offsY) / sGridIval);
        end = (int) Math.round((paintHere.y + paintHere.height - offsY) / sGridIval);
        for (int i = start; i < end; i++) {
            lp = (int) (i * sGridIval + offsY);
            if (i % env.getGridDivision() == 0) {
                g.setColor(env.GRID_COLOR);
            } else {
                g.setColor(env.DIVIDE_GRID_COLOR);
            }
            g.drawLine(paintHere.x, lp, paintHere.x + paintHere.width, lp);
        }

    }
// <editor-fold defaultstate="collapsed" desc="Scrollableの実装メソッド">
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return new Dimension(1000, 200);
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        int rtio = (int) (8 * getEnvironment().getMagnification());
        if (rtio < 1) {
            rtio = 1;
        }
        return rtio;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        int rtio;
        if (orientation == SwingConstants.HORIZONTAL) {
            rtio = (int) (visibleRect.width / 2);
        } else {
            rtio = (int) (visibleRect.height / 2);
        }
        return rtio;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        JEnvironment env = document.getEnvironment();
        double ratio = env.getToScreenRatio();
        double sw = scroller.getViewport().getWidth() / ratio;
        double w = env.getPaperRect().getWidth() * 1.1;
        if (w < sw) {
            adjustSize();
            return true;
        }
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        JEnvironment env = document.getEnvironment();
        double ratio = env.getToScreenRatio();
        double sh = scroller.getViewport().getHeight() / ratio;
        double h = env.getPaperRect().getHeight() * 1.1;
        if (h < sh) {
            adjustSize();
            return true;
        }
        return false;
    }
    //</editor-fold>
}
