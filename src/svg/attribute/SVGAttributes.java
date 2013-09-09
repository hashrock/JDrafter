/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package svg.attribute;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.HashMap;
import org.xml.sax.Attributes;
import svg.oject.SVGDocument;
import svg.oject.SVGMarker;
import svg.oject.SVGObject;


/**
 *
 * @author takashi
 */
public class SVGAttributes implements Cloneable{
    // <editor-fold defaultstate="collapsed" desc="カラー定義">
    public static final Color ALICEBLUE = new Color(240, 248, 255);
    public static final Color ANTIQUEWHITE = new Color(250, 235, 215);
    public static final Color AQUA = new Color(0, 255, 255);
    public static final Color AQUAMARINE = new Color(127, 255, 212);
    public static final Color AZURE = new Color(240, 255, 255);
    public static final Color BEIGE = new Color(245, 245, 220);
    public static final Color BISQUE = new Color(255, 228, 196);
    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color BLANCHEDALMOND = new Color(255, 235, 205);
    public static final Color BLUE = new Color(0, 0, 255);
    public static final Color BLUEVIOLET = new Color(138, 43, 226);
    public static final Color BROWN = new Color(165, 42, 42);
    public static final Color BURLYWOOD = new Color(222, 184, 135);
    public static final Color CADETBLUE = new Color(95, 158, 160);
    public static final Color CHARTREUSE = new Color(127, 255, 0);
    public static final Color CHOCOLATE = new Color(210, 105, 30);
    public static final Color CORAL = new Color(255, 127, 80);
    public static final Color CORNFLOWERBLUE = new Color(100, 149, 237);
    public static final Color CORNSILK = new Color(255, 248, 220);
    public static final Color CRIMSON = new Color(220, 20, 60);
    public static final Color CYAN = new Color(0, 255, 255);
    public static final Color DARKBLUE = new Color(0, 0, 139);
    public static final Color DARKCYAN = new Color(0, 139, 139);
    public static final Color DARKGOLDENROD = new Color(184, 134, 11);
    public static final Color DARKGRAY = new Color(169, 169, 169);
    public static final Color DARKGREEN = new Color(0, 100, 0);
    public static final Color DARKGREY = new Color(169, 169, 169);
    public static final Color DARKKHAKI = new Color(189, 183, 107);
    public static final Color DARKMAGENTA = new Color(139, 0, 139);
    public static final Color DARKOLIVEGREEN = new Color(85, 107, 47);
    public static final Color DARKORANGE = new Color(255, 140, 0);
    public static final Color DARKORCHID = new Color(153, 50, 204);
    public static final Color DARKRED = new Color(139, 0, 0);
    public static final Color DARKSALMON = new Color(233, 150, 122);
    public static final Color DARKSEAGREEN = new Color(143, 188, 143);
    public static final Color DARKSLATEBLUE = new Color(72, 61, 139);
    public static final Color DARKSLATEGRAY = new Color(47, 79, 79);
    public static final Color DARKSLATEGREY = new Color(47, 79, 79);
    public static final Color DARKTURQUOISE = new Color(0, 206, 209);
    public static final Color DARKVIOLET = new Color(148, 0, 211);
    public static final Color DEEPPINK = new Color(255, 20, 147);
    public static final Color DEEPSKYBLUE = new Color(0, 191, 255);
    public static final Color DIMGRAY = new Color(105, 105, 105);
    public static final Color DIMGREY = new Color(105, 105, 105);
    public static final Color DODGERBLUE = new Color(30, 144, 255);
    public static final Color FIREBRICK = new Color(178, 34, 34);
    public static final Color FLORALWHITE = new Color(255, 250, 240);
    public static final Color FORESTGREEN = new Color(34, 139, 34);
    public static final Color FUCHSIA = new Color(255, 0, 255);
    public static final Color GAINSBORO = new Color(220, 220, 220);
    public static final Color GHOSTWHITE = new Color(248, 248, 255);
    public static final Color GOLD = new Color(255, 215, 0);
    public static final Color GOLDENROD = new Color(218, 165, 32);
    public static final Color GRAY = new Color(128, 128, 128);
    public static final Color GREEN = new Color(0, 128, 0);
    public static final Color GREENYELLOW = new Color(173, 255, 47);
    public static final Color GREY = new Color(128, 128, 128);
    public static final Color HONEYDEW = new Color(240, 255, 240);
    public static final Color HOTPINK = new Color(255, 105, 180);
    public static final Color INDIANRED = new Color(205, 92, 92);
    public static final Color INDIGO = new Color(75, 0, 130);
    public static final Color IVORY = new Color(255, 255, 240);
    public static final Color KHAKI = new Color(240, 230, 140);
    public static final Color LAVENDER = new Color(230, 230, 250);
    public static final Color LAVENDERBLUSH = new Color(255, 240, 245);
    public static final Color LAWNGREEN = new Color(124, 252, 0);
    public static final Color LEMONCHIFFON = new Color(255, 250, 205);
    public static final Color LIGHTBLUE = new Color(173, 216, 230);
    public static final Color LIGHTCORAL = new Color(240, 128, 128);
    public static final Color LIGHTCYAN = new Color(224, 255, 255);
    public static final Color LIGHTGOLDENRODYELLOW = new Color(250, 250, 210);
    public static final Color LIGHTGRAY = new Color(211, 211, 211);
    public static final Color LIGHTGREEN = new Color(144, 238, 144);
    public static final Color LIGHTGREY = new Color(211, 211, 211);
    public static final Color LIGHTPINK = new Color(255, 182, 193);
    public static final Color LIGHTSALMON = new Color(255, 160, 122);
    public static final Color LIGHTSEAGREEN = new Color(32, 178, 170);
    public static final Color LIGHTSKYBLUE = new Color(135, 206, 250);
    public static final Color LIGHTSLATEGRAY = new Color(119, 136, 153);
    public static final Color LIGHTSLATEGREY = new Color(119, 136, 153);
    public static final Color LIGHTSTEELBLUE = new Color(176, 196, 222);
    public static final Color LIGHTYELLOW = new Color(255, 255, 224);
    public static final Color LIME = new Color(0, 255, 0);
    public static final Color LIMEGREEN = new Color(50, 205, 50);
    public static final Color LINEN = new Color(250, 240, 230);
    public static final Color MAGENTA = new Color(255, 0, 255);
    public static final Color MAROON = new Color(128, 0, 0);
    public static final Color MEDIUMAQUAMARINE = new Color(102, 205, 170);
    public static final Color MEDIUMBLUE = new Color(0, 0, 205);
    public static final Color MEDIUMORCHID = new Color(186, 85, 211);
    public static final Color MEDIUMPURPLE = new Color(147, 112, 219);
    public static final Color MEDIUMSEAGREEN = new Color(60, 179, 113);
    public static final Color MEDIUMSLATEBLUE = new Color(123, 104, 238);
    public static final Color MEDIUMSPRINGGREEN = new Color(0, 250, 154);
    public static final Color MEDIUMTURQUOISE = new Color(72, 209, 204);
    public static final Color MEDIUMVIOLETRED = new Color(199, 21, 133);
    public static final Color MIDNIGHTBLUE = new Color(25, 25, 112);
    public static final Color MINTCREAM = new Color(245, 255, 250);
    public static final Color MISTYROSE = new Color(255, 228, 225);
    public static final Color MOCCASIN = new Color(255, 228, 181);
    public static final Color NAVAJOWHITE = new Color(255, 222, 173);
    public static final Color NAVY = new Color(0, 0, 128);
    public static final Color OLDLACE = new Color(253, 245, 230);
    public static final Color OLIVE = new Color(128, 128, 0);
    public static final Color OLIVEDRAB = new Color(107, 142, 35);
    public static final Color ORANGE = new Color(255, 165, 0);
    public static final Color ORANGERED = new Color(255, 69, 0);
    public static final Color ORCHID = new Color(218, 112, 214);
    public static final Color PALEGOLDENROD = new Color(238, 232, 170);
    public static final Color PALEGREEN = new Color(152, 251, 152);
    public static final Color PALETURQUOISE = new Color(175, 238, 238);
    public static final Color PALEVIOLETRED = new Color(219, 112, 147);
    public static final Color PAPAYAWHIP = new Color(255, 239, 213);
    public static final Color PEACHPUFF = new Color(255, 218, 185);
    public static final Color PERU = new Color(205, 133, 63);
    public static final Color PINK = new Color(255, 192, 203);
    public static final Color PLUM = new Color(221, 160, 221);
    public static final Color POWDERBLUE = new Color(176, 224, 230);
    public static final Color PURPLE = new Color(128, 0, 128);
    public static final Color RED = new Color(255, 0, 0);
    public static final Color ROSYBROWN = new Color(188, 143, 143);
    public static final Color ROYALBLUE = new Color(65, 105, 225);
    public static final Color SADDLEBROWN = new Color(139, 69, 19);
    public static final Color SALMON = new Color(250, 128, 114);
    public static final Color SANDYBROWN = new Color(244, 164, 96);
    public static final Color SEAGREEN = new Color(46, 139, 87);
    public static final Color SEASHELL = new Color(255, 245, 238);
    public static final Color SIENNA = new Color(160, 82, 45);
    public static final Color SILVER = new Color(192, 192, 192);
    public static final Color SKYBLUE = new Color(135, 206, 235);
    public static final Color SLATEBLUE = new Color(106, 90, 205);
    public static final Color SLATEGRAY = new Color(112, 128, 144);
    public static final Color SLATEGREY = new Color(112, 128, 144);
    public static final Color SNOW = new Color(255, 250, 250);
    public static final Color SPRINGGREEN = new Color(0, 255, 127);
    public static final Color STEELBLUE = new Color(70, 130, 180);
    public static final Color TAN = new Color(210, 180, 140);
    public static final Color TEAL = new Color(0, 128, 128);
    public static final Color THISTLE = new Color(216, 191, 216);
    public static final Color TOMATO = new Color(255, 99, 71);
    public static final Color TURQUOISE = new Color(64, 224, 208);
    public static final Color VIOLET = new Color(238, 130, 238);
    public static final Color WHEAT = new Color(245, 222, 179);
    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color WHITESMOKE = new Color(245, 245, 245);
    public static final Color YELLOW = new Color(255, 255, 0);
    public static final Color YELLOWGREEN = new Color(154, 205, 50);
    public static final HashMap<String, Color> COLOR_MAP = createColorMap();
    
    private static HashMap<String, Color> createColorMap() {
        HashMap<String, Color> result = new HashMap<String, Color>();
        result.put("aliceblue", ALICEBLUE);
        result.put("antiquewhite", ANTIQUEWHITE);
        result.put("aqua", AQUA);
        result.put("aquamarine", AQUAMARINE);
        result.put("azure", AZURE);
        result.put("beige", BEIGE);
        result.put("bisque", BISQUE);
        result.put("black", BLACK);
        result.put("blanchedalmond", BLANCHEDALMOND);
        result.put("blue", BLUE);
        result.put("blueviolet", BLUEVIOLET);
        result.put("brown", BROWN);
        result.put("burlywood", BURLYWOOD);
        result.put("cadetblue", CADETBLUE);
        result.put("chartreuse", CHARTREUSE);
        result.put("chocolate", CHOCOLATE);
        result.put("coral", CORAL);
        result.put("cornflowerblue", CORNFLOWERBLUE);
        result.put("cornsilk", CORNSILK);
        result.put("crimson", CRIMSON);
        result.put("cyan", CYAN);
        result.put("darkblue", DARKBLUE);
        result.put("darkcyan", DARKCYAN);
        result.put("darkgoldenrod", DARKGOLDENROD);
        result.put("darkgray", DARKGRAY);
        result.put("darkgreen", DARKGREEN);
        result.put("darkgrey", DARKGREY);
        result.put("darkkhaki", DARKKHAKI);
        result.put("darkmagenta", DARKMAGENTA);
        result.put("darkolivegreen", DARKOLIVEGREEN);
        result.put("darkorange", DARKORANGE);
        result.put("darkorchid", DARKORCHID);
        result.put("darkred", DARKRED);
        result.put("darksalmon", DARKSALMON);
        result.put("darkseagreen", DARKSEAGREEN);
        result.put("darkslateblue", DARKSLATEBLUE);
        result.put("darkslategray", DARKSLATEGRAY);
        result.put("darkslategrey", DARKSLATEGREY);
        result.put("darkturquoise", DARKTURQUOISE);
        result.put("darkviolet", DARKVIOLET);
        result.put("deeppink", DEEPPINK);
        result.put("deepskyblue", DEEPSKYBLUE);
        result.put("dimgray", DIMGRAY);
        result.put("dimgrey", DIMGREY);
        result.put("dodgerblue", DODGERBLUE);
        result.put("firebrick", FIREBRICK);
        result.put("floralwhite", FLORALWHITE);
        result.put("forestgreen", FORESTGREEN);
        result.put("fuchsia", FUCHSIA);
        result.put("gainsboro", GAINSBORO);
        result.put("ghostwhite", GHOSTWHITE);
        result.put("gold", GOLD);
        result.put("goldenrod", GOLDENROD);
        result.put("gray", GRAY);
        result.put("green", GREEN);
        result.put("greenyellow", GREENYELLOW);
        result.put("grey", GREY);
        result.put("honeydew", HONEYDEW);
        result.put("hotpink", HOTPINK);
        result.put("indianred", INDIANRED);
        result.put("indigo", INDIGO);
        result.put("ivory", IVORY);
        result.put("khaki", KHAKI);
        result.put("lavender", LAVENDER);
        result.put("lavenderblush", LAVENDERBLUSH);
        result.put("lawngreen", LAWNGREEN);
        result.put("lemonchiffon", LEMONCHIFFON);
        result.put("lightblue", LIGHTBLUE);
        result.put("lightcoral", LIGHTCORAL);
        result.put("lightcyan", LIGHTCYAN);
        result.put("lightgoldenrodyellow", LIGHTGOLDENRODYELLOW);
        result.put("lightgray", LIGHTGRAY);
        result.put("lightgreen", LIGHTGREEN);
        result.put("lightgrey", LIGHTGREY);
        result.put("lightpink", LIGHTPINK);
        result.put("lightsalmon", LIGHTSALMON);
        result.put("lightseagreen", LIGHTSEAGREEN);
        result.put("lightskyblue", LIGHTSKYBLUE);
        result.put("lightslategray", LIGHTSLATEGRAY);
        result.put("lightslategrey", LIGHTSLATEGREY);
        result.put("lightsteelblue", LIGHTSTEELBLUE);
        result.put("lightyellow", LIGHTYELLOW);
        result.put("lime", LIME);
        result.put("limegreen", LIMEGREEN);
        result.put("linen", LINEN);
        result.put("magenta", MAGENTA);
        result.put("maroon", MAROON);
        result.put("mediumaquamarine", MEDIUMAQUAMARINE);
        result.put("mediumblue", MEDIUMBLUE);
        result.put("mediumorchid", MEDIUMORCHID);
        result.put("mediumpurple", MEDIUMPURPLE);
        result.put("mediumseagreen", MEDIUMSEAGREEN);
        result.put("mediumslateblue", MEDIUMSLATEBLUE);
        result.put("mediumspringgreen", MEDIUMSPRINGGREEN);
        result.put("mediumturquoise", MEDIUMTURQUOISE);
        result.put("mediumvioletred", MEDIUMVIOLETRED);
        result.put("midnightblue", MIDNIGHTBLUE);
        result.put("mintcream", MINTCREAM);
        result.put("mistyrose", MISTYROSE);
        result.put("moccasin", MOCCASIN);
        result.put("navajowhite", NAVAJOWHITE);
        result.put("navy", NAVY);
        result.put("oldlace", OLDLACE);
        result.put("olive", OLIVE);
        result.put("olivedrab", OLIVEDRAB);
        result.put("orange", ORANGE);
        result.put("orangered", ORANGERED);
        result.put("orchid", ORCHID);
        result.put("palegoldenrod", PALEGOLDENROD);
        result.put("palegreen", PALEGREEN);
        result.put("paleturquoise", PALETURQUOISE);
        result.put("palevioletred", PALEVIOLETRED);
        result.put("papayawhip", PAPAYAWHIP);
        result.put("peachpuff", PEACHPUFF);
        result.put("peru", PERU);
        result.put("pink", PINK);
        result.put("plum", PLUM);
        result.put("powderblue", POWDERBLUE);
        result.put("purple", PURPLE);
        result.put("red", RED);
        result.put("rosybrown", ROSYBROWN);
        result.put("royalblue", ROYALBLUE);
        result.put("saddlebrown", SADDLEBROWN);
        result.put("salmon", SALMON);
        result.put("sandybrown", SANDYBROWN);
        result.put("seagreen", SEAGREEN);
        result.put("seashell", SEASHELL);
        result.put("sienna", SIENNA);
        result.put("silver", SILVER);
        result.put("skyblue", SKYBLUE);
        result.put("slateblue", SLATEBLUE);
        result.put("slategray", SLATEGRAY);
        result.put("slategrey", SLATEGREY);
        result.put("snow", SNOW);
        result.put("springgreen", SPRINGGREEN);
        result.put("steelblue", STEELBLUE);
        result.put("tan", TAN);
        result.put("teal", TEAL);
        result.put("thistle", THISTLE);
        result.put("tomato", TOMATO);
        result.put("turquoise", TURQUOISE);
        result.put("violet", VIOLET);
        result.put("wheat", WHEAT);
        result.put("white", WHITE);
        result.put("whitesmoke", WHITESMOKE);
        result.put("yellow", YELLOW);
        result.put("yellowgreen", YELLOWGREEN);
        return result;
    }
    //</editor-fold>
    //transform 属性
    //属性key
    public static final String STROKE = "stroke";
    public static final String STROKE_WIDTH = "stroke-width";
    public static final String STROKE_LINECAP = "stroke-linecap";
    public static final String STROKE_LINEJOIN = "stroke-linejoin";
    public static final String STROKE_MITERLIMIT = "stroke-miterlimit";
    public static final String STROKE_DASHARRAY = "stroke-dasharray";
    public static final String STROKE_DASHOFFSET = "stroke-dashoffset";
    public static final String STROKE_OPACITY = "stroke-opacity";
    public static final String FILL = "fill";
    public static final String FILL_OPACITY = "fill-opacity";
    public static final String FILL_RULE = "fill-rule";
    public static final String OPACITY = "opacity";
    public static final String FONT_SIZE = "font-size";
    public static final String FONT_FAMILY = "font-family";
    public static final String FONT_WEIGHT = "font-weight";
    public static final String FONT_STRETCH = "font-stretch";
    public static final String FONT_STYLE = "font-style";
    public static final String TEXT_DECORATION = "text-decoration";
    public static final String TEXT_ANCHOR = "text-anchor";
    public static final String TRANSFORM = "transform";
    public static final String CLASS = "class";
    //
    public static final String MARKER_START="marker-start";
    public static final String MARKER_MIDDLE="marker-middle";
    public static final String MARKER_END="marker-end";
    public static final String MARKER="maker";
    
    //
    public static final String[] KEYS = new String[]{
        STROKE,
        STROKE_WIDTH,
        STROKE_LINECAP,
        STROKE_LINEJOIN,
        STROKE_MITERLIMIT,
        STROKE_DASHARRAY,
        STROKE_DASHOFFSET,
        STROKE_OPACITY,
        FILL,
        FILL_OPACITY,
        FILL_RULE,
        OPACITY,
        FONT_SIZE,
        FONT_FAMILY,
        FONT_WEIGHT,
        FONT_STYLE,
        FONT_STRETCH,
        TEXT_DECORATION,
        TEXT_ANCHOR,
        TRANSFORM,
        CLASS,
        MARKER_START,
        MARKER_MIDDLE,
        MARKER_END,
        MARKER
    };
    //Values
    public static final String NONE = "none";
    public static final String INHERIT = "inherit";
    public static final String CURRENT_COLOR = "current-color";
    public static final String CAP_BUTT = "butt";
    public static final String CAP_ROUND = "round";
    public static final String CAP_SQUARE = "square";
    public static final String JOIN_MITER = "miter";
    public static final String JOIN_ROUND = "round";
    public static final String JOIN_BEVEL = "bevel";
    public static final String NONZERO = "nonzero";
    public static final String EVENODD = "evenodd";
    public static final String NORMAL = "normal";
    public static final String ITALIC = "italic";
    public static final String OBLIQUE = "oblique";
    public static final String BOLD = "bold";
    public static final String BOLDER = "bolder";
    public static final String LIGHTER = "lighter";
    public static final String UNDERLINE = "underline";
    public static final String OVERLINE = "overline";
    public static final String LINETHROUGH = "line-through";
    public static final String WIDER = "wider";
    public static final String NARROWER = "narrower";
    public static final String ULTRA_CONDENSED = "ultra-condensed";
    public static final String EXTRA_CONDENSED = "extra-condensed";
    public static final String CONDENSED = "condensed";
    public static final String SEMI_CONDENSED = "semi-condensed";
    public static final String SEMI_EXPANDED = "semi-expanded";
    public static final String EXPANDED = "expanded";
    public static final String EXTRA_EXPANDED = "extra-expanded";
    public static final String ULTRA_EXPANDED = "ultra-expanded";
    public static final String ALIGN_START = "start";
    public static final String ALIGN_MIDDLE = "middle";
    public static final String ALIGN_END = "end";    //
    public static final String URL_PATTERN = "url\\s*\\(\\s*#.+\\)";
    //
    private static final String[] WEIGHT_VALUES=new String[]{
        "100",
        "200",
        "300",
        "400",
        NORMAL,
        "500",
        "600",
        "700",
        BOLD,
        "800",
        "900"
    };
    private static final String[] STRETCH_VALUES = new String[]{
        ULTRA_CONDENSED,
        EXTRA_CONDENSED,
        CONDENSED,
        SEMI_CONDENSED,
        NORMAL,
        SEMI_EXPANDED,
        EXPANDED,
        EXTRA_EXPANDED,
        ULTRA_EXPANDED
    };
    private static final String[] FONT_SIZE_NAME=new String[]{
        "xx-small",
        "x-small",
        "small",
        "medium",
        "large",
        "x-large",
        "xx-large",
        "xxx-large"
    };
    private static final String[] FONT_SIZE_H=new String[]{
        "h6",
        "nothing",
        "h5",
        "h4",
        "h3",
        "h2",
        "h1",
        "h0"
    };
    private static final String[] FONT_SIZE_=new String[]{
        "7.2pt",
        "9pt",
        "10pt",
        "12pt",
        "14.4pt",
        "18pt",
        "24pt",
        "36pt"
    };
    private HashMap<String, String> attrMap;
    
    private SVGAttributes() {
        attrMap = new HashMap<String, String>();
    }
    
    public SVGAttributes(SVGObject obj, Attributes attr) {
        this();
        setAttributes(obj, attr);
    }
    
    public void addStyleAttributes(SVGStyle style, SVGObject obj) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.putAll(style.getMap(obj.getName()));
        String s = obj.getId();
        if (s != null) {
            map.putAll(style.getMap("#" + s.trim()));
        }
        s = attrMap.get(CLASS);
        if (s != null) {
            map.putAll(style.getMap("." + s.trim()));
        }
        appendAttributes(map);
    }
    
    private void setAttributes(SVGObject obj, Attributes attr) {
        HashMap<String, String> map = new HashMap<String, String>();
        if (obj != null && obj.getRootDocument() != null) {
            SVGDocument doc = obj.getRootDocument();
            map.putAll(doc.getStyle().getMap(obj.getName()));
            String s = attr.getValue("id");
            if (s != null) {
                map.putAll(doc.getStyle().getMap("#" + s.trim()));
            }
            s = attr.getValue(CLASS);
            if (s != null) {
                map.putAll(doc.getStyle().getMap("." + s.trim()));
            }
        }
        map.putAll(getMapFromAttributes(attr));
        appendAttributes(map);
    }
    
    private void appendAttributes(HashMap<String, String> map) {
        for (int i = 0; i < KEYS.length; i++) {
            if (map.containsKey(KEYS[i])) {
                attrMap.put(KEYS[i], map.get(KEYS[i]));
            }
        }
    }
    
    public Paint getStroke(SVGObject owner,SVGObject node) {
        String s = attrMap.get(STROKE);
        if (s == null || s.equals(INHERIT) || s.equals(CURRENT_COLOR)) {
            if (owner != null && owner.getPropertyParent() != null) {
                return owner.getPropertyParent().getAttributes().getStroke(owner.getPropertyParent(),node);
            }
            return null;//default none;
        }
        if (s.matches(URL_PATTERN)) {
            String url = s.replaceAll("(url\\s*\\(\\s*#)|\\)", "");
            return ((SVGPaint) owner.getRootDocument().getLink(url)).getPaint(node);
        }
        if (s.equals(NONE)) {
            return null;
        }
        Color c = getColorFromAttribute(s, null);
        float op = getStrokeOpacity(owner);
        if (op != 1) {
            c = new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (op * 255));
        }
        return c;
    }
    
    public BasicStroke getBasicStroke(SVGObject owner) {
        return new BasicStroke(getStrokeWidth(owner), getLineCap(owner), getLineJoin(owner), getMiterLimit(owner), getDashArray(owner), getDashOffset(owner));
    }
    
    public float getStrokeOpacity(SVGObject owner) {
        String s = attrMap.get(STROKE_OPACITY);
        if (s == null || s.equals(INHERIT)) {
            if (owner != null && owner.getPropertyParent() != null) {
                return owner.getPropertyParent().getAttributes().getStrokeOpacity(owner.getPropertyParent());
            }
            s = "1";
        }
        return Float.valueOf(s);
    }
    
    public float getStrokeWidth(SVGObject owner) {
        String s = attrMap.get(STROKE_WIDTH);
        if (s == null || s.equals(INHERIT)) {
            if (owner != null && owner.getPropertyParent() != null) {
                return owner.getPropertyParent().getAttributes().getStrokeWidth(owner.getPropertyParent());
            }
            s = "1";
        }
        return SVGObject.toPixel(owner, s, SVGObject.BOTH, SVGObject.LENGTH);
    }
    
    public int getLineCap(SVGObject owner) {
        String s = attrMap.get(STROKE_LINECAP);
        if (s == null || s.equals(INHERIT)) {
            if (owner != null && owner.getPropertyParent() != null) {
                return owner.getPropertyParent().getAttributes().getLineCap(owner.getPropertyParent());
            }
            s = CAP_BUTT;
        }
        if (s.equals(CAP_ROUND)) {
            return BasicStroke.CAP_ROUND;
        } else if (s.equals(CAP_SQUARE)) {
            return BasicStroke.CAP_SQUARE;
        }
        return BasicStroke.CAP_BUTT;
    }
    
    public int getLineJoin(SVGObject owner) {
        String s = attrMap.get(STROKE_LINEJOIN);
        if (s == null || s.equals(INHERIT)) {
            if (owner != null && owner.getPropertyParent() != null) {
                return owner.getPropertyParent().getAttributes().getLineJoin(owner.getPropertyParent());
            }
            s = JOIN_MITER;
        }
        if (s.equals(JOIN_ROUND)) {
            return BasicStroke.JOIN_ROUND;
        } else if (s.equals(JOIN_BEVEL)) {
            return BasicStroke.JOIN_BEVEL;
        }
        return BasicStroke.JOIN_MITER;
    }
    
    public float getMiterLimit(SVGObject owner) {
        String s = attrMap.get(STROKE_MITERLIMIT);
        if (s == null || s.equals(INHERIT)) {
            if (owner != null && owner.getPropertyParent() != null) {
                return owner.getPropertyParent().getAttributes().getMiterLimit(owner.getPropertyParent());
            }
            s = "8";
        }
        return Float.valueOf(s);
    }
    
    public float[] getDashArray(SVGObject owner) {
        String s = attrMap.get(STROKE_DASHARRAY);
        if (s == null || s.equals(INHERIT)) {
            if (owner != null && owner.getPropertyParent() != null) {
                return owner.getPropertyParent().getAttributes().getDashArray(owner.getPropertyParent());
            }
            s = NONE;
        }
        if (s.equals(NONE)) {
            return null;
        }
        String[] params = s.trim().split("(\\s*\\,\\s*)|(\\s+)");
        float[] result = new float[params.length];
        for (int i = 0; i < params.length; i++) {
            result[i] = SVGObject.toPixel(owner, params[i], SVGObject.HORIZONTAL, SVGObject.LENGTH);
        }
        return result;
        
    }
    
    public float getDashOffset(SVGObject owner) {
        String s = attrMap.get(STROKE_DASHOFFSET);
        if (s == null || s.equals(INHERIT)) {
            if (owner != null && owner.getPropertyParent() != null) {
                return owner.getPropertyParent().getAttributes().getDashOffset(owner.getPropertyParent());
            }
            s = "0";
        }
        return SVGObject.toPixel(owner, s, SVGObject.HORIZONTAL, SVGObject.LENGTH);
    }
    
    public Paint getFill(SVGObject owner,SVGObject node) {
        String s = attrMap.get(FILL);
        if (s == null || s.equals(INHERIT) || s.equals(CURRENT_COLOR)) {
            if (owner != null && owner.getPropertyParent() != null) {
                return owner.getPropertyParent().getAttributes().getFill(owner.getPropertyParent(),node);
            }
            return Color.BLACK;
        }
        if (s.equals(NONE)) {
            return null;
        }
        if (s.matches(URL_PATTERN)) {
            String url = s.replaceAll("(url\\s*\\(\\s*#)|\\)", "");
            return ((SVGPaint) owner.getRootDocument().getLink(url)).getPaint(node);
        }
        Color c = getColorFromAttribute(s, null);
        float op = getFillOpacity(owner);
        if (op != 1) {
            c = new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (op * 255));
        }
        return c;
    }
    
    public float getFillOpacity(SVGObject owner) {
        String s = attrMap.get(FILL_OPACITY);
        if (s == null || s.equals(INHERIT)) {
            if (owner != null && owner.getPropertyParent() != null) {
                return owner.getPropertyParent().getAttributes().getFillOpacity(owner.getPropertyParent());
            }
            s = "1";
        }
        return Float.valueOf(s);
    }
    
    public int getFillRule(SVGObject owner) {
        String s = attrMap.get(FILL_RULE);
        if (s == null || s.equals(INHERIT)) {
            if (owner != null && owner.getPropertyParent() != null) {
                return owner.getPropertyParent().getAttributes().getFillRule(owner.getPropertyParent());
            }
            s = NONZERO;
        }
        if (s.equals(EVENODD)) {
            return PathIterator.WIND_EVEN_ODD;
        }
        return PathIterator.WIND_NON_ZERO;
    }
    
    public float getOpacity(SVGObject owner) {
        String s = attrMap.get(OPACITY);
        if (s == null || s.equals(INHERIT)) {
            if (owner != null && owner.getPropertyParent() != null) {
                return owner.getPropertyParent().getAttributes().getOpacity(owner.getPropertyParent());
            }
            s = "1";
        }
        float f = 1;
        if (owner != null && owner.getPropertyParent() != null) {
            f = owner.getPropertyParent().getAttributes().getOpacity(owner.getPropertyParent());
        }
        return f * Float.valueOf(s);
    }
    
    public float getFontSize(SVGObject owner) {
        String s = attrMap.get(FONT_SIZE);
        if (s == null || s.equals(INHERIT)) {
            if (owner != null && owner.getPropertyParent() != null) {
                return owner.getPropertyParent().getAttributes().getFontSize(owner.getPropertyParent());
            }
            s = "12pt";
            
        }
        String a=getFontSizeByName(s);
        if (a !=null)
            s=a;
        return SVGObject.toPixel(owner, s, SVGObject.BOTH, SVGObject.LENGTH);
    }
    
    public String getFontFamily(SVGObject owner) {
        String s = attrMap.get(FONT_FAMILY);
        if (s == null || s.equals(INHERIT)) {
            if (owner != null && owner.getPropertyParent() != null) {
                return owner.getPropertyParent().getAttributes().getFontFamily(owner.getPropertyParent());
            }
            s = Font.MONOSPACED;
        }
        return s.trim();
    }
    
    public float getFontStyle(SVGObject owner) {
        String s = attrMap.get(FONT_STYLE);
        if (s == null || s.equals(INHERIT)) {
            if (owner != null && owner.getPropertyParent() != null) {
                return owner.getPropertyParent().getAttributes().getFontStyle(owner.getPropertyParent());
            }
            s = NORMAL;
        }
        if (s.equals(ITALIC) || s.equals(OBLIQUE)) {
            return TextAttribute.POSTURE_OBLIQUE;
        }
        return TextAttribute.POSTURE_REGULAR;
    }
    
    protected int getFontWidthIndex(SVGObject owner) {
        String s = attrMap.get(FONT_STRETCH);
        int result = widthIndex(NORMAL);
        if (s == null || s.equals(INHERIT)) {
            if (owner != null && owner.getPropertyParent() != null) {
                return owner.getPropertyParent().getAttributes().getFontWidthIndex(owner.getPropertyParent());
            }
            return result;
        }
        if (s.equals(NARROWER) || s.equals(WIDER)) {
            if (owner != null && owner.getPropertyParent() != null) {
                result = owner.getPropertyParent().getAttributes().getFontWidthIndex(owner.getPropertyParent());
            }
            if (s.equals(NARROWER) && result > 0) {
                result--;
            }
            if (s.equals(WIDER) && result < STRETCH_VALUES.length - 1) {
                result++;
            }
            return result;
        }
        return widthIndex(s);
    }
    
    public float getFontWidth(SVGObject owner) {
        int i = getFontWidthIndex(owner);
        switch (i) {
            case 0:
            case 1:
            case 2:
                return TextAttribute.WIDTH_CONDENSED;
            case 3:
                return TextAttribute.WIDTH_SEMI_CONDENSED;
            case 4:
                return TextAttribute.WIDTH_REGULAR;
            case 5:
                return TextAttribute.WIDTH_SEMI_EXTENDED;
            case 6:
            case 7:
            case 8:
                return TextAttribute.WIDTH_EXTENDED;
        }
        return TextAttribute.WIDTH_REGULAR;
    }
    
    public Font getFont(SVGObject owner){
        HashMap< TextAttribute,Object> map=new HashMap<TextAttribute,Object>();
        map.put(TextAttribute.SIZE,getFontSize(owner));
        map.put(TextAttribute.FAMILY, getFontFamily(owner));
        map.put(TextAttribute.WEIGHT, getFontWeight(owner));
        map.put(TextAttribute.WIDTH,getFontWidth(owner));
        map.put(TextAttribute.POSTURE, getFontStyle(owner));
        map.put(TextAttribute.STRIKETHROUGH, getStrikeThrough(owner));
        map.put(TextAttribute.UNDERLINE, getTextUnderLine(owner));
        return new Font(map);
    }
    
    public SVGMarker getMarker(SVGObject owner){
        String s=attrMap.get(MARKER);
        if (s==null){
            return null;
        }
        if (s.equals(INHERIT)){
            if (owner !=null && owner.getPropertyParent() !=null){
                return owner.getPropertyParent().getAttributes().getMarker(owner.getPropertyParent());
            }
            return null;
        }
        if (s.equals(NONE)){
            return null;
        }
        s=s.replaceAll("(url)|[#\\(\\)]","").trim();
        return ((SVGMarker)owner.getRootDocument().getLink(s)).getWritableInstance(owner);
    }
    public SVGMarker getStartMarker(SVGObject owner){
        String s=attrMap.get(MARKER_START);
        if (s==null){
            return getMarker(owner);
        }
        if (s.equals(INHERIT)){
            if (owner !=null && owner.getPropertyParent() !=null){
                return owner.getPropertyParent().getAttributes().getStartMarker(owner.getPropertyParent());
            }
            return null;
        }
        if (s.equals(NONE)){
            return null;
        }
        s=s.replaceAll("(url)|[#\\(\\)]","").trim();
        return ((SVGMarker)owner.getRootDocument().getLink(s)).getWritableInstance(owner);
    }
    public SVGMarker getMiddleMarker(SVGObject owner){
        String s=attrMap.get(MARKER_MIDDLE);
        if (s==null){
            return getMarker(owner);
        }
        if (s.equals(INHERIT)){
            if (owner !=null && owner.getPropertyParent() !=null){
                return owner.getPropertyParent().getAttributes().getMiddleMarker(owner.getPropertyParent());
            }
            return null;
        }
        if (s.equals(NONE)){
            return null;
        }
        s=s.replaceAll("(url)|[#\\(\\)]","").trim();
        return ((SVGMarker)owner.getRootDocument().getLink(s)).getWritableInstance(owner);
    }
    
    public SVGMarker getEndMarker(SVGObject owner){
        String s=attrMap.get(MARKER_END);
        if (s==null){
            return getMarker(owner);
        }
        if (s.equals(INHERIT)){
            if (owner !=null && owner.getPropertyParent() !=null){
                return owner.getPropertyParent().getAttributes().getEndMarker(owner.getPropertyParent());
            }
            return null;
        }
        if (s.equals(NONE)){
            return null;
        }
        s=s.replaceAll("(url)|[#\\(\\)]","").trim();
       return ((SVGMarker)owner.getRootDocument().getLink(s)).getWritableInstance(owner);
    }
    
    
    private int widthIndex(String value) {
        for (int i = 0; i < STRETCH_VALUES.length; i++) {
            if (STRETCH_VALUES[i].equals(value)) {
                return i;
            }
        }
        return widthIndex(NORMAL);
    }
    protected int getFontWeightIndex(SVGObject owner){
        String s=attrMap.get(FONT_WEIGHT);
        int  result=weightIndex(NORMAL);
        if (s==null || s.equals(INHERIT)){
            if (owner !=null && owner.getPropertyParent()!=null){
                result=owner.getPropertyParent().getAttributes().getFontWeightIndex(owner.getPropertyParent());
            }
            return result;
        }
        if (s.equals(LIGHTER)||s.equals(BOLDER)){
            if (owner !=null && owner.getPropertyParent() !=null){
                result=owner.getPropertyParent().getAttributes().getFontWeightIndex(owner.getPropertyParent());
            }
            if (s.equals(LIGHTER) && result>0)
                result--;
            else if (s.equals(BOLDER) && result <WEIGHT_VALUES.length-1)
                result++;
            return result;
        }
        return weightIndex(s);
    }
    public float getFontWeight(SVGObject owner) {
        switch(getFontWeightIndex(owner)){
            case 0:return TextAttribute.WEIGHT_EXTRA_LIGHT;
            case 1:return TextAttribute.WEIGHT_LIGHT;
            case 2:return TextAttribute.WEIGHT_DEMILIGHT;
            case 3:
            case 4:return TextAttribute.WEIGHT_REGULAR;
            case 5:return TextAttribute.WEIGHT_MEDIUM;
            case 6:return TextAttribute.WEIGHT_DEMIBOLD;
            case 7:
            case 8:return TextAttribute.WEIGHT_BOLD;
            case 9:return TextAttribute.WEIGHT_HEAVY;
            case 10:return TextAttribute.WEIGHT_ULTRABOLD;
        }
        return TextAttribute.WEIGHT_REGULAR;
    }
    private int weightIndex(String value){
        for (int i=0;i<WEIGHT_VALUES.length;i++){
            if (WEIGHT_VALUES[i].equals(value))
                return i;
        }
        return weightIndex(NORMAL);
    }
    public String getTextDecoration(SVGObject owner) {
        String s = attrMap.get(TEXT_DECORATION);
        if (s == null || s.equals(INHERIT)) {
            if (owner != null && owner.getPropertyParent() != null) {
                return owner.getPropertyParent().getAttributes().getTextDecoration(owner.getPropertyParent());
            }
            s = NONE;
        }
        return s;
    }
    
    public int getTextUnderLine(SVGObject owner) {
        String s = getTextDecoration(owner);
        if (s.equals(UNDERLINE)) {
            return TextAttribute.UNDERLINE_ON;
        }
        return -1;
    }
    
    public boolean getStrikeThrough(SVGObject owner) {
        String s = getTextDecoration(owner);
        return s.equals(LINETHROUGH);
    }
    
    public String getTextAncur(SVGObject owner) {
        String s = attrMap.get(TEXT_ANCHOR);
        if (s == null || s.equals(INHERIT)) {
            if (owner != null && owner.getPropertyParent() != null) {
                return owner.getPropertyParent().getAttributes().getTextAncur(owner.getPropertyParent());
            }
            s = ALIGN_START;
        }
        return s;
    }
    
    public AffineTransform getTransform(SVGObject owner) {
        String s = attrMap.get(TRANSFORM);
        if (s == null) {
            return null;
        }
        return getTransformFromAttribute(s, owner);
    }
    
    public static HashMap<String, String> getMapFromAttributes(Attributes attr) {
        HashMap<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < attr.getLength(); i++) {
            if (!attr.getQName(i).equals("style")) {
                map.put(attr.getQName(i), attr.getValue(i));
            }
        }
        String s = attr.getValue("style");
        if (s != null) {
            s = s.replaceAll("((/\\*)([^*/])+\\*/)", "").trim();
            s = s.replaceAll("[\\t\\n]", "").trim();
            String[] sp1 = s.split("\\s*;\\s*");
            for (int i = 0; i < sp1.length; i++) {
                String[] sp2 = sp1[i].trim().split(":");
                map.put(sp2[0].trim(), sp2[1].trim());
            }
            
        }
        return map;
    }
    
    public static Color getColorFromAttribute(String s, String op) {
        String reg1 = "#[0-9a-fA-F]+";
        String reg2 = "rgb\\s*\\((\\s*\\d+\\s*)(\\,\\s*\\d+\\s*){2}\\)";
        String reg3 = "rgb\\s*\\((\\s*\\d+)(\\s+\\d+){2}\\)";
        s = s.trim();
        Color c = null;
        if (COLOR_MAP.containsKey(s)) {
            c = COLOR_MAP.get(s);
        } else if (s.matches(reg1)) {
            s = s.replace("#", "").trim();
            s=(s+"000000").substring(0,6);
            int ci = Integer.parseInt(s, 16);
            c = new Color(ci);
        } else if (s.matches(reg2)) {
            s = s.replaceAll("(rgb)|[\\(\\)]", "");
            String[] sp = s.split("\\,");
            c = new Color(Integer.valueOf(sp[0].trim()), Integer.valueOf(sp[1].trim()), Integer.valueOf(sp[2].trim()));
        } else if (s.matches(reg3)) {
            s = s.replaceAll("(rgb)|[\\(\\)]", "");
            String[] sp = s.split("\\s+");
            c = new Color(Integer.valueOf(sp[0].trim()), Integer.valueOf(sp[1].trim()), Integer.valueOf(sp[2].trim()));
        }
        if (c != null) {
            if (op != null) {
                int fop = (int) (Float.valueOf(op) * 255);
                c = new Color(c.getRed(), c.getGreen(), c.getBlue(), fop);
            }
        }
        return c;
    }
    
    public static AffineTransform getTransformFromAttribute(String s, SVGObject obj) {
        String fa = "(\\s*(\\-?)((\\d+(\\.\\d+)?)|(\\.\\d+))";
        String matrix = "(matrix\\s*\\(.+\\))";
        String translate = "(translate\\s*\\(.+\\))";
        String scale = "(scale\\s*\\(.+\\))";
        String rotate = "(rotate\\s*\\(.+\\))";
        String skewX = "(skewX\\s*\\(.+\\))";
        String skewY = "(skewY\\s*\\(.+\\))";
        //
        if (s == null) {
            return null;
        }
        //String sMatch = "("+matrix + "|" + translate + "|" + scale + "|" + rotate + "|" + skewX + "|" + skewY+"){1}?";
        //Pattern pattern = Pattern.compile(sMatch);
        // Matcher match = pattern.matcher(s);
        AffineTransform result = null;
        String[] mss = s.trim().split("\\)\\s*");
        for (int i = 0; i < mss.length; i++) {
            //while (match.find()) {
            
            String sm = mss[i].trim() + ")";
            if (sm.matches(matrix)) {
                sm = sm.replaceAll("(matrix)|(\\()|(\\))", "").trim();
                String[] ps = sm.split("(\\s*\\,\\s*)|(\\s+)");
                float[] para = new float[6];
                para[0] = Float.valueOf(ps[0].trim());
                para[1] = Float.valueOf(ps[1].trim());
                para[2] = Float.valueOf(ps[2].trim());
                para[3] = Float.valueOf(ps[3].trim());
                para[4] = SVGObject.toPixel(obj, ps[4], SVGObject.HORIZONTAL, SVGObject.LENGTH);
                para[5] = SVGObject.toPixel(obj, ps[5], SVGObject.VERTICAL, SVGObject.LENGTH);
                if (result == null) {
                    result = new AffineTransform(para);
                } else {
                    result.concatenate(new AffineTransform(para));
                }
            } else if (sm.matches(translate)) {
                sm = sm.replaceAll("(translate)|(\\()|(\\))", "").trim();
                
                String[] ps = sm.split("(\\s*\\,\\s*)|(\\s+)");
                float sx=0, sy=0;
                sx = SVGObject.toPixel(obj, ps[0].trim(), SVGObject.HORIZONTAL, SVGObject.LENGTH);
                if (ps.length > 1) {
                    sy = SVGObject.toPixel(obj, ps[1].trim(), SVGObject.VERTICAL, SVGObject.LENGTH);
                }
                if (result == null) {
                    result = AffineTransform.getTranslateInstance(sx, sy);
                } else {
                    result.translate(sx, sy);
                }
            } else if (sm.matches(scale)) {
                sm = sm.replaceAll("(scale)|[\\(\\)]", "").trim();
                String[] ps = sm.split("(\\s+)|\\s*\\,\\s*");
                System.out.println(sm);
                float sx, sy;
                sx = sy = Float.valueOf(ps[0]);
                if (ps.length > 1) {
                    sy = Float.valueOf(ps[1]);
                }
                if (result == null) {
                    result = AffineTransform.getScaleInstance(sx, sy);
                } else {
                    result.scale(sx, sy);
                }
            } else if (sm.matches(rotate)) {
                sm = sm.replaceAll("(rotate)|[\\(\\)]", "");
                String[] ps = sm.split("(\\s+)|\\s*\\,\\s*");
                float r = 0, x = 0, y = 0;
                r = (float) (Float.valueOf(ps[0]) * Math.PI / 180);
                if (ps.length > 1) {
                    x = SVGObject.toPixel(obj, ps[1], SVGObject.HORIZONTAL, SVGObject.COORDS);
                    y = SVGObject.toPixel(obj, ps[2], SVGObject.VERTICAL, SVGObject.COORDS);
                }
                if (result == null) {
                    result = AffineTransform.getRotateInstance(r, x, y);
                } else {
                    result.rotate(r, x, y);
                }
            } else if (sm.matches(skewX)) {
                sm = sm.replaceAll("(skewX)|[\\(\\)]", "");
                float sx = (float) (Float.valueOf(sm) * Math.PI / 180);
                if (result == null) {
                    result = AffineTransform.getShearInstance(sx, 0);
                } else {
                    result.shear(sx, 0);
                }
            } else if (sm.matches(skewY)) {
                sm = sm.replaceAll("(skewY)|[\\(\\)]", "");
                float sy = (float) (Float.valueOf(sm) * Math.PI / 180);
                if (result == null) {
                    result = AffineTransform.getShearInstance(0, sy);
                } else {
                    result.shear(0, sy);
                }
            }
        }
        return result;
    }
    public static String getFontSizeByName(String s){
        if (s==null) return null;
        for (int i=0;i<FONT_SIZE_.length;i++){
            if (s.equals(FONT_SIZE_NAME[i]) || s.equals(FONT_SIZE_H[i])){
                return FONT_SIZE_[i];
            }
        }
        return null;
    }
    public String get(String key) {
        return attrMap.get(key);
    }
    public void put(String key,String value){
        attrMap.put(key,value);
    }
    public String getCurrentAttributs(String key,SVGObject owner){
        String s=attrMap.get(key);
        if (s==null){
            if (owner !=null && owner.getPropertyParent()!=null){
                return owner.getPropertyParent().getAttributes().getCurrentAttributs(key,owner.getPropertyParent());
            }
            return null;
        }
        return s;
    }
    @Override
    public SVGAttributes clone(){
        SVGAttributes result=new SVGAttributes();
        result.attrMap.putAll(attrMap);
        return result;
    }
}
