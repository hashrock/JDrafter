/*
 * JEnvironment.java
 *
 * Created on 2007/08/18, 18:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jscreen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.io.IOException;
import java.io.Serializable;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import jgeom.JIntersect;
import jgeom.JSegment;
import jgeom.JSimplePath;
import jobject.JGuidLayer;
import jobject.JPage;
import jtools.JCursor;
import jpaint.JPaint;
import jpaint.JPatternPaint;
import jpaint.JStroke;
import jtools.JAbstractTool;
import jui.JIcons;

/**
 *スクリーンとオブジェクトの描画に必要な情報を提供するためのインターフェースを提供します.
 * @author TI
 */
public class JEnvironment implements Serializable, Cloneable {
    //
    private static final long serialVersionUID = 110l;
    /**マウスカーソルです*/
    public static final JCursor MOUSE_CURSOR = new JCursor();
    /**アイコンです*/
    public static final JIcons ICONS = new JIcons();
    /** mm(ミリメートル）の表示単位を表します.*/
    public static final int METRIC_GAUGE = 0;
    /** Point(1/72インチ)の表示単位を表します.*/
    public static final int INCHI_GAUGE = 1;
    /**Pointをmmに換算するための係数です.*/
    public static final double MIL_PER_POINT = 25.4d / 72d;
    /**mmをポイントに換算するための係数です.*/
    public static final double MIL_PER_INCH = 25.4d;
    /**パスを示す正方形の一辺の長さです。*/
    public static final float DEFAULT_PATH_SELECTOR_SIZE = 4;
    public static float PATH_SELECTOR_SIZE = DEFAULT_PATH_SELECTOR_SIZE;
    /**ハイライトアンカーの表示比率*/
    public static final float DEFAULT_HILIGHT_RATIO = 1.5f;
    public static float HILIGHT_RATIO = DEFAULT_HILIGHT_RATIO;
    /*フリーフォームセレクタのコントロールの正方形の一辺の長さです.*/
    //public static  float OBJECT_SELECTOR_SIZE=4;
    //
    /** デフォルトのガイドの色です*/
    public static final Color DEFAULT_GUID_COLOR = Color.BLUE;
    /**デフォルトのガイドのプレビュー色です.*/
    public static final Color DEFAULT_GUID_PREVIEW_COLOR = Color.CYAN;
    /**ペイント時のアンチエリアフラグ*/
    public static final boolean DEFAULT_PAINT_ANTI_AREASING = true;
    public static boolean PAINT_ANTI_AREASING = DEFAULT_PAINT_ANTI_AREASING;
    /**プレビュー時のアンチエリアフラグ*/
    public static final boolean DEFAULT_PREVIEW_ANTI_AREASING = false;
    public static boolean PREVIEW_ANTI_AREASING = DEFAULT_PREVIEW_ANTI_AREASING;
    /**線を選択するための許容誤差です。*/
    public static final float DEFAULT_SELECTION_STROKE_SIZE = 3f;
    public static float SELECTION_STROKE_SIZE = DEFAULT_SELECTION_STROKE_SIZE;
    /**線を選択するためのStrokeです。*/
    public static Stroke SELECTION_STROKE = new BasicStroke(SELECTION_STROKE_SIZE);
    /**ポイントスナップフラグ*/
    public static boolean SNAP_TO_ANCUR = true;
    /**デフォルトの塗りです。*/
    public static final JPaint DEFAULT_FILL = new JPaint(Color.WHITE);
    /**デフォルトの線色です。*/
    public static final JPaint DEFAULT_BORDER = new JPaint(Color.BLACK);
    /**デフォルトの線種です*/
    public static final JStroke DEFAULT_STROKE = new JStroke(new BasicStroke(1f));
    /**デフォルトのテキスト色です.*/
    public static final JPaint DEFAULT_TEXT_FILL = new JPaint(Color.BLACK);
    /**デフォルトのテキストアウトライン色です*/
    public static final JPaint DEFAULT_TEXT_BORDER = null;
    /**デフォルトのテキスト線種です*/
    public static final JStroke DEFAULT_TEXT_STROKE = new JStroke(new BasicStroke(1f));
    /**カレントの塗りです。*/
    public static JPaint currentFill = DEFAULT_FILL;
    /**カレントの線の塗りです.*/
    public static JPaint currentBorder = DEFAULT_BORDER;
    /**カレントの線です.*/
    public static JStroke currentStroke = DEFAULT_STROKE;
    /**テキストオブジェクトのカレントの塗りです.*/
    public static JPaint currentTextFill = DEFAULT_TEXT_FILL;
    /**テキストオブジェクトのカレントの線種です。*/
    public static JStroke currentTextStroke = DEFAULT_TEXT_STROKE;
    /**テキストオブジェクトのカレントの線色です。*/
    public static JPaint currentTextBorder = DEFAULT_TEXT_BORDER;
    /**プレビューの色です。*/
    public static Color PREVIEW_COLOR = new Color(0f, 0.8f, 0.8f);
    /**デフォルトのプレビュー色を示します*/
    public static final Color[] PREVIEW_COLORS = new Color[]{
        new Color(50, 100, 255),
        new Color(255, 0, 0),
        new Color(0, 255, 0),
        new Color(0, 0, 255),
        new Color(255, 255, 0),
        new Color(255, 0, 255),
        new Color(0, 255, 255),
        new Color(153, 153, 153),
        new Color(0, 0, 0),
        new Color(255, 102, 0),
        new Color(0, 153, 0),
        new Color(0, 204, 204),
        new Color(204, 153, 0),
        new Color(204, 0, 0),
        new Color(153, 0, 255),
        new Color(255, 204, 0),
        new Color(0, 0, 153),
        new Color(255, 153, 255),
        new Color(153, 153, 255),
        new Color(102, 0, 0),
        new Color(51, 102, 0),
        new Color(255, 153, 153),
        new Color(153, 153, 0),
        new Color(204, 255, 0),
        new Color(204, 204, 255),
        new Color(204, 102, 255),
        new Color(204, 204, 204),
        new Color(0, 153, 153),
        new Color(255, 255, 204),
        new Color(204, 204, 255),
        new Color(204, 255, 204),
        new Color(204, 204, 0)
    };
    /**ドラッグ時のセレクションレクトのプレビュー色です。*/
    public static Color DRAG_AREA_COLOR = Color.BLACK;
    /**ドラッグ時のセレクションプレビューの線です.*/
    public static Stroke DRAG_AREA_STROKE = new BasicStroke(0f, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER, 10f, new float[]{1f, 1f}, 0f);
    /**グリッド線の線種を示します.*/
    public static Stroke GUAGE_STROKE = new BasicStroke(0f);
    /**グリッド線の色を示します.*/
    public static final Color DEFAULT_GRID_COLOR = new Color(0.85f, 0.85f, 0.85f);
    public Color GRID_COLOR = DEFAULT_GRID_COLOR;
    /**分割グリッド線の色を示します.*/
    public static final Color DEFAULT_DIVIDE_GRID_COLOR = new Color(0.95f, 0.95f, 0.95f);
    public Color DIVIDE_GRID_COLOR = DEFAULT_DIVIDE_GRID_COLOR;
    /**グリッドの前面表示フラグです。*/
    public static boolean GRID_FOREGROUND = false;
    /**スケーリング時の最小スケーリング係数です。*/
    public static final double MINIMUM_SCALE_RATIO = 0.0001;
    /**セレクションレクトの最小値を示します.*/
    public static final double MINIMUM_SELECT_SIZE = 18d;
    /**オブジェクトのデフォルトの半径を示します*/
    public static double DEFAULT_RADIUS = 72d;
    /**オブジェクトのでデフォルトの幅を示します.*/
    public static double DEFAULT_WIDTH = 72d;
    /**オブジェクトのデフォルトの高さを示します.*/
    public static double DEFAULT_HEIGHT = 72d;
    /**角度ツールのデフォルトの角度を示します*/
    public static double DEFAULT_ANGLE = Math.PI / 2;
    /**デフォルトの角の丸め半径を示します.*/
    public static double DEFAULT_ROUNDRECT_RADIUS = 16d;
    /**デフォルトのべベルの半径を示します.*/
    public static double DEFAULT_BEVEL_RADIUS = 12d;
    /**デフォルトの多角形の頂点数を示します.*/
    public static int DEFAULT_POLYGON_VERTEX = 6;
    /**デフォルトの星型図形の凸頂点数を示します.*/
    public static int DEFAULT_STAR_VERTEX = 5;
    /**星型図形の凸頂点と凹頂点の半径の比率を示します.*/
    public static double DEFAULT_STAR_RADIUS_RATIO = 0.381966011;
    /**デフォルトのX方向のスケーリング係数を示します。*/
    public static double DEFAULT_SCALE_X = 1d;
    /**デフォルトのY方向のスケーリング係数を示します.*/
    public static double DEFAULT_SCALE_Y = 1d;
    /**デフォルトのX軸方向のシアリング係数を示します.*/
    public static double DEFAULT_SHEER_X = 0d;
    /**デフォルトのY軸方向のシアリング係数を示します.*/
    public static double DEFAULT_SHEER_Y = 1d;
    /**デフォルトの回転角を示します.*/
    public static double DEFAULT_THETA = 0d;
    /**対称移動軸の角度を示します*/
    public static double DEFAULT_REFLECT_AXIS = 0d;
    /**角丸め・切り落し操作時のデフォルトの半径を示します.*/
    public static double DEFAULT_CUTCORNER_RADIUS = 12d;
    /**X座標の平行移動距離を示します.*/
    public static double DEFAULT_TRANSLATE_X = 0d;
    /**Y座標の平行移動距離を示します>*/
    public static double DEFAULT_TRANSLATE_Y = 0d;
    /**直前の移動を示すAffineTransformです.*/
    public static AffineTransform LAST_TRANSFORM = null;
    /**直前の回転角を示します.*/
    public static double LAST_ROTATION = 0;
    /**直前の移動のコピー動作の有無を示します.*/
    public static boolean LAST_COPY = false;
    /**表示倍率を示します.*/
    private double magnification = 1.0d;//表示倍率
    /**座標単位を示します.*/
    public static int guageUnit = METRIC_GAUGE;//座標単
    /**ペーパーの左上隅から、目盛りの原点までのXオフセットを示します.*/
    private double gaugeX = 0;
    /**ペーパー左上隅から、目盛りの原点までのYオフセットを示します.*/
    private double gaugeY = 0;
    /**グリッド表示の有無を指定するフラグです.*/
    private boolean isGridVisible = true;
    /**グリッド吸着の有無を指定するフラグです.*/
    private boolean isSnapGrid = false;
    /**ミリ単位表示の場合のデフォルトのグリッド間隔です.*/
    public static final double DEFAULT_GRIDSIZE_BYMIL = 10;
    private double gridSizeByMil = 10;
    /**ミリ単位表示の場合のデフォルトのグリッド分割数です.*/
    public static final int DEFAULT_GRIDDIVISION_BYMIL = 10;
    private int gridDivisionByMil = 10;
    /**ポイント単位表示の場合のデフォルトのグリッド間隔です.*/
    public static final double DEFAULT_GRIDSIZE_BYPOINT = 36;
    private double gridSizeByPoint = 36;
    /**ポイント単位表示の場合のデフォルトのグリッド分割数です.*/
    public static final int DEFAULT_GRIDDIVISION_BYPOINT = 6;
    /**作成されたパターンを一時的にセーブします.
     */
    public static JPaint SAVED_PATTERN=null;
    private int gridDivisionByPoint = 6;
    /**シフト押下げ時の移動制限角です.*/
    public static final double DEFAULT_UNIT_ANGLE = 45d;
    private double unitAngle = DEFAULT_UNIT_ANGLE;
    private Point paperOffset = new Point();
    private Rectangle2D.Double paperRectangle = new Rectangle2D.Double();
    private Rectangle2D.Double imageRect = new Rectangle2D.Double();
    /**
     * 現在の表示デバイスの解像度です。
     */
    public static final double screenDPI = Toolkit.getDefaultToolkit().getScreenResolution();
    private transient Rectangle2D clip = null;
    private transient Vector<ChangeListener> listeners = null;

    /** インスタンスを構築します。*/
    public JEnvironment() {
        currentFill = new JPaint(Color.WHITE);
        currentBorder = new JPaint(Color.BLACK);
        currentStroke = new JStroke(new BasicStroke(1.0f));
    }
    /**チェンジリスナーを追加します*/
    ;

    public void addChangeListener(ChangeListener ls) {
        if (listeners == null) {
            listeners = new Vector<ChangeListener>();
        }
        if (!listeners.contains(ls)) {
            listeners.add(ls);
        }

    }

    /**チェンジリスナーを削除します.*/
    public void removeChangeListener(ChangeListener ls) {
        if (listeners == null) {
            return;
        }
        listeners.remove(ls);
    }

    private void fireChangeEvent() {
        if (listeners == null) {
            return;
        }
        ChangeEvent e = new ChangeEvent(this);
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).stateChanged(e);
        }
    }

    /**表示画面上の1ピクセルの長さ(Point)を返します.*/
    public double pixelPerPoint() {
        return 72d / screenDPI;
    }

    /**表示画面の解像度を返します.*/
    public double getScreenDPI() {
        return screenDPI;
    }

    /**表示倍率を設定します*/
    public void setMagnification(double magnification) {
        this.magnification = magnification;
        fireChangeEvent();
    }

    /**表示倍率を取得します*/
    public double getMagnification() {
        return magnification;
    }

    /**コンポーネント原点からDocument原点までのoffsetをスクリーン座標で設定します。*/
    public void setOffsetByScreen(int x, int y) {
        paperOffset.x = x;
        paperOffset.y = y;
        fireChangeEvent();
    }

    /**コンポーネント原点からDocument原点までのoffsetを絶対座標で設定します.*/
    public void setOffset(double x, double y) {
        paperOffset.x = (int) (x * getToScreenRatio());
        paperOffset.y = (int) (y * getToScreenRatio());
        fireChangeEvent();
    }

    /**コンポーネント原点からDocument原点までのoffsetを絶対座標で取得します.*/
    public Point2D getOffset() {
        return new Point2D.Double(paperOffset.x / getToScreenRatio(), paperOffset.y / getToScreenRatio());
    }

    /**コンポーネント原点からDocument原点までのoffsetを絶対座標で取得します.*/
    public Point getOffsetByScreen() {
        return paperOffset;
    }

    /**Documentの原点からメモリ座標軸原点までの相対距離を設定します*/
    public void setGaugeOffset(double x, double y) {
        gaugeX = x;
        gaugeY = y;
        fireChangeEvent();
    }

    /**Documentの原点から目盛座標軸原点までの水平方向のオフセットを設定します.*/
    public void setGaugeOffsetX(double x) {
        this.gaugeX = x;
        fireChangeEvent();
    }

    /**Documentの原点から目盛座標軸原点までの垂直方向のオフセットを設定します.*/
    public void setGaougeOffsetY(double y) {
        this.gaugeY = y;
        fireChangeEvent();
    }

    /**グリッド吸着の有無を取得します.*/
    public boolean isSnapGrid() {
        return isSnapGrid;
    }

    /**グリッドの吸着を設定します.*/
    public void setSnapGrid(boolean b) {
        isSnapGrid = b;
        fireChangeEvent();
    }

    /**グリッド表示の有無を設定します.*/
    public void setGridVisible(boolean b) {
        isGridVisible = b;
        fireChangeEvent();
    }

    /**グリッド表示の有無を取得します.*/
    public boolean isGridVisible() {
        return isGridVisible;
    }

    /**グリッドサイズを設定します*/
    public void setGridSize(double grd) {
        if (guageUnit == METRIC_GAUGE) {
            gridSizeByMil = grd;
        } else {
            gridSizeByPoint = grd;
        }
        fireChangeEvent();
    }

    /**グリッドサイズをpoint単位で取得します.*/
    public double getGridSize() {
        if (guageUnit == METRIC_GAUGE) {
            return gridSizeByMil * 72 / 25.4;
        } else {
            return gridSizeByPoint;
        }
    }

    /**グリッドサイズをmm単位で取得します.*/
    public double getGridSizeForMil() {
        if (guageUnit == METRIC_GAUGE) {
            return gridSizeByMil;
        } else {
            return gridSizeByPoint / 72 * 25.4;
        }
    }

    /**グリッド間隔を設定します.*/
    public void setGridDivision(int dv) {
        if (guageUnit == METRIC_GAUGE) {
            gridDivisionByMil = dv;
        } else {
            gridDivisionByPoint = dv;
        }
        fireChangeEvent();
    }

    /**グリッド間隔を取得します.*/
    public int getGridDivision() {
        if (guageUnit == METRIC_GAUGE) {
            return gridDivisionByMil;
        } else {
            return gridDivisionByPoint;
        }
    }

    /**移動制限角を設定します.*/
    public void setUnitAngle(int ua) {
        unitAngle = ua;
        fireChangeEvent();
    }

    /**移動制限角を取得します.*/
    public double getUnitAngle() {
        return unitAngle;
    }

    /**Documentoの原点からメモリ座標軸原点までの相対距離を取得します*/
    public Point2D getGaugeOffset() {
        return new Point2D.Double(gaugeX, gaugeY);
    }

    /**画面表示のためのScaleを取得します*/
    public double getToScreenRatio() {
        return magnification * screenDPI / 72;
    }

    /**絶対座標からスクリーン座標に変換するための表示倍率を含むAffineTransformを構築します.*/
    public AffineTransform getToScreenTransform() {
        AffineTransform ret = new AffineTransform();
        Point2D p = getOffset();
        double ratio = getToScreenRatio();
        ret.setToScale(ratio, ratio);
        ret.translate(p.getX(), p.getY());
        return ret;
    }

    /**スクリーン座標系から絶対座標系に変換するためのAffineTransformを構築します.*/
    public AffineTransform getToAbsoluteTransform() {
        AffineTransform ret = new AffineTransform();
        double ratio = 1 / getToScreenRatio();
        Point2D p = getOffset();
        ret.setToTranslation(-p.getX(), -p.getY());
        ret.scale(ratio, ratio);
        return ret;
    }

    /**用紙領域のRectangleを返します*/
    public Rectangle2D getPaperRect() {
        return paperRectangle;
    }

    /**印刷可能領域のRectangleを返します*/
    public Rectangle2D getImageRect() {
        return imageRect;
    }

    /**PageFormatからパラメータを設定します*/
    public void setPaper(PageFormat p) {
        paperRectangle.setFrame(0, 0, p.getWidth(), p.getHeight());
        imageRect.setFrame(p.getImageableX(), p.getImageableY(), p.getImageableWidth(), p.getImageableHeight());
    }

    /**スナップグリッドフラグを考慮し描画座標系のマウスポイントを絶対座標に変換します.*/
    public Point2D getAbsoluteMousePoint(Point2D p, JPage page) {
        Point2D.Double ret = new Point2D.Double();
        AffineTransform af = getToAbsoluteTransform();
        af.transform(p, ret);
        //
        if (page != null) {
            JGuidLayer jgl = page.getGuidLayer();
            if (!jgl.isEmpty()) {
                JRequest req = new JRequest(page);
                req.setSelectionMode(JRequest.DIRECT_MODE);
                int result = jgl.hitByPoint(this, req, ret);
                if (result == JRequest.HIT_ANCUR || result == JRequest.HIT_PATH) {
                    Point2D point = null;
                    JSimplePath path = null;
                    for (Object o : req.hitObjects) {
                        if (o instanceof JSegment) {
                            point = ((JSegment) o).getAncur();
                            if (path != null) {
                                break;
                            }
                        }
                        if (o instanceof JSimplePath) {
                            path = ((JSimplePath) o);
                            if (result == JRequest.HIT_PATH) {
                                point = getInterSection((JSimplePath) o, ret);
                                break;
                            } else if (point != null) {
                                break;
                            }
                        }
                    }
                    JAbstractTool tool = page.getDocument().getViewer().getDragPane().getDragger();
                    tool.setSnapPlace(result, point, path);
                    return point;
                }
            }
        }
        if (SNAP_TO_ANCUR) {
            JRequest req = new JRequest(page);
            req.hitResult = JRequest.HIT_NON;
            req.setSelectionMode(JRequest.DIRECT_MODE);
            int result = page.hitByPoint(this, req, ret);
            if (result == JRequest.HIT_ANCUR) {
                JSimplePath path = null;
                Point2D point = null;
                for (Object o : req.hitObjects) {
                    if (o instanceof JSegment) {
                        point = ((JSegment) o).getAncur();
                        if (path != null) {
                            break;
                        }
                    } else if (o instanceof JSimplePath) {
                        path = (JSimplePath) o;
                        if (point != null) {
                            break;
                        }
                    }
                }
                JAbstractTool tool = page.getDocument().getViewer().getDragPane().getDragger();
                tool.setSnapPlace(result, point, path);
                return point;
            }
        }
        if (isSnapGrid()) {
            double ival = getGridSize() / getGridDivision();
            double offsX = getGaugeOffset().getX();
            double offsY = getGaugeOffset().getY();
            ret.x = ival * Math.round((ret.x - offsX) / ival) + offsX;
            ret.y = ival * Math.round((ret.y - offsY) / ival) + offsY;
        }
        return ret;
    }

    /**パスとポイントの交点を返します.
     * 
     */
    private Point2D getInterSection(JSimplePath pth, Point2D p) {
        JSimplePath addedPath = pth.clone();
        Point2D p0 = new Point2D.Double(),p1  = new Point2D.Double();
        double radius = PATH_SELECTOR_SIZE * HILIGHT_RATIO / getToScreenRatio() / 2;
        p0.setLocation(p.getX() - radius, p.getY() - radius);
        p1.setLocation(p.getX() + radius, p.getY() + radius);
        int k = JIntersect.addPath(p0, p1, addedPath);
        if (k == -1) {
            p0.setLocation(p.getX() - radius, p.getY() + radius);
            p1.setLocation(p.getX() + radius, p.getY() - radius);
            k = JIntersect.addPath(p0, p1, addedPath);
        }
        if (k != -1) {
            return addedPath.get(k).getAncur();
        }
        return null;
    }

    /**シフト移動の場合のポイント位置を取得します
     *@param source 基準点
     *@param current 現在点
     *@return 変換後の点
     */
    public Point2D getShiftedMovePoint(Point2D source, Point2D current) {
        double unitTheta = unitAngle * Math.PI / 180d;
        double dx = current.getX() - source.getX();
        double dy = current.getY() - source.getY();

        double theta = unitTheta * Math.round(Math.atan2(dy, dx) / unitTheta);
        double length = Math.sqrt(dx * dx + dy * dy);
        return new Point2D.Double(length * Math.cos(theta) + source.getX(), length * Math.sin(theta) + source.getY());
    }

    /**表示の単位系を設定します
     *@param g 表示の単位メートル単位系:METRIC_GUAGE,インチ単位系:INCHI_GUAGE;*/
    public void setGuageUnit(int g) {
        if (g != METRIC_GAUGE && g != INCHI_GAUGE) {
            return;
        }
        JEnvironment.guageUnit = g;
    }

    /**現在の表示単位系を返します.
     *@return  表示の単位メートル単位系:METRIC_GUAGE,インチ単位系:INCHI_GUAGE*/
    public int getGuageUnit() {
        return JEnvironment.guageUnit;
    }

    /**データ読み込み*/
    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject(); 
    //screenDPI=Toolkit.getDefaultToolkit().getScreenResolution();

    }

    /**クリップ領域に指定したShapeを加えます.*/
    public void addClip(Shape s) {
        if (clip == null) {
            clip = s.getBounds2D();
        }
        clip.add(s.getBounds2D());
    }

    /**描画座標系のShapeをクリップに加えます.*/
    public void addDrawClip(Shape s) {
        addClip(getToAbsoluteTransform().createTransformedShape(s));
    }

    /**クリップ領域を取得します.*/
    public Rectangle2D getClip() {
        return clip;
    }

    /**クリップ領域を描画座標系で取得します*/
    public Rectangle getScreenClip() {
        return getToScreenTransform().createTransformedShape(clip).getBounds();
    }

    /**クリッピング領域をクリアします.*/
    public void flushClip() {
        clip = null;
    }
    /**
     * このJEnvironmentの複製を返します.
     * @return
     */
    @Override
    @SuppressWarnings("static-access")
    public JEnvironment clone() {
        JEnvironment ret = new JEnvironment();
        ret.magnification = this.magnification;
        ret.gaugeX = this.gaugeX;
        ret.gaugeY = this.gaugeY;
        ret.guageUnit = JEnvironment.guageUnit;
        ret.imageRect = new Rectangle2D.Double(imageRect.x, imageRect.y, imageRect.width, imageRect.height);
        ret.paperOffset = new Point(paperOffset.x, paperOffset.y);
        ret.paperRectangle = new Rectangle2D.Double(paperRectangle.x, paperRectangle.y, paperRectangle.width, paperRectangle.height);
        return ret;
    }
}


