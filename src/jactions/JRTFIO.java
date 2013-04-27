/*
 * JRTFIO.java
 *
 * Created on 2007/10/19, 10:52
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jactions;

/**
 *
 * @author i002060
 */
import java.io.*;
import java.util.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;

/**
 * RTF の入出力に関するクラス
 */
public class JRTFIO {
    
    static CFontSet myFonts = new CFontSet();
    
    /** RTFフォント情報 **/
    private static class CFontSet {
        ArrayList fontNum,fontEnc;
        final String[] rtfFontset = new String[1500];
        
        public CFontSet() {
            fontNum = new ArrayList();
            fontEnc = new ArrayList();
            for(int i=0;i<1500;i++) rtfFontset[i] = "";
            
            rtfFontset[0] = "iso-8859-1";//0: ANSI
            rtfFontset[1] = "SJIS"; //1: Default
            rtfFontset[2] = "MacSymbol"; //2: Symbol
            rtfFontset[3] = ""; //3: Invalid
            
            rtfFontset[77] = "MacRoman"; //77: Mac
            rtfFontset[78] = "SJIS"; //Japanese
            rtfFontset[79] = "Cp950";
            rtfFontset[80] = "EUC_KR";
            
            rtfFontset[102] = "MS936";
            rtfFontset[128] = "SJIS"; //128: Shift Jis
            rtfFontset[129] = "MS949"; //129: Hangul
            rtfFontset[130] = "x-Johab"; //130: Johab
            rtfFontset[134] = "MS936"; //134: GB2312
            rtfFontset[136] = "Big5"; //136: Big5
            rtfFontset[161] = "Cp1253"; //161: Greek
            rtfFontset[162] = "Cp1254"; //162: Turkish
            rtfFontset[163] = "Cp1258"; //163: Vietnamese
            rtfFontset[177] = "Cp1255"; //177: Hebrew
            rtfFontset[178] = "Cp1256"; //178: Arabic
            rtfFontset[179] = "Cp1256"; //179: Arabic Traditional
            rtfFontset[180] = "Cp864"; //180: Arabic user
            rtfFontset[181] = "Cp862"; //181: Hebrew user
            rtfFontset[186] = "Cp775"; //186: Baltic
            
            rtfFontset[204] = "Cp866"; //204: Russian
            rtfFontset[238] = "Cp1250"; //238: Eastern European
            rtfFontset[222] = "Cp874"; //222: Thai
            rtfFontset[254] = "Cp437"; //254: PC 437
            rtfFontset[255] = "SJIS"; //255: OEM
            rtfFontset[256] = "MacRoman"; //256:
            
            rtfFontset[437] = "Cp437"; //437: United States IBM
            
            rtfFontset[708] = "Cp1256"; //708: Arabic (ASMO 708)
            rtfFontset[709] = "Cp1256"; //709: Arabic (ASMO 449+, BCON V4)
            rtfFontset[710] = "Cp1256"; //710: Arabic (transparent Arabic)
            rtfFontset[711] = "Cp1256"; //711: Arabic (Nafitha Enhanced)
            rtfFontset[720] = "Cp1256"; //720: Arabic (transparent ASMO)
            rtfFontset[819] = "Cp1250"; //819: Windows 3.1 (United States and Western Europe)
            rtfFontset[850] = "Cp850"; //850: IBM multilingual
            rtfFontset[852] = "Cp852"; //852: Eastern European
            rtfFontset[860] = "Cp860"; //860: Portuguese
            rtfFontset[862] = "Cp862"; //862: Hebrew
            rtfFontset[863] = "Cp863"; //863: French Canadian
            rtfFontset[864] = "Cp864"; //864: Arabic
            rtfFontset[865] = "Cp865"; //865: Norwegian
            rtfFontset[866] = "Cp866"; //866: Soviet Union
            rtfFontset[874] = "MS874"; //874: Thai
            rtfFontset[932] = "MS932"; //932: Japanese
            rtfFontset[936] = "MS936"; //936: Simplified Chinese
            rtfFontset[949] = "MS949"; //949: Korean
            rtfFontset[950] = "MS950"; //950: Traditional Chinese
            rtfFontset[1250] = "Cp1250"; //1250: Windows 3.1 (Eastern European)
            rtfFontset[1251] = "Cp1251"; //1251: Windows 3.1 (Cyrillic)
            rtfFontset[1252] = "Cp1252"; //1252: Western European
            rtfFontset[1253] = "Cp1253"; //1253: Greek
            rtfFontset[1254] = "Cp1254"; //1254: Turkish
            rtfFontset[1255] = "Cp1255"; //1255: Hebrew
            rtfFontset[1256] = "Cp1256"; //1256: Arabic
            rtfFontset[1257] = "Cp1257"; //1257: Baltic
            rtfFontset[1258] = "Cp1258"; //1258: Vietnamese
            rtfFontset[1361] = "x-Johab"; //1361: Johab
        }
        
        public String fontname(int n) {
            return rtfFontset[n];
        }
        
        public void add(int n_font,int n_fcharset) {
            for(int i=0;i<fontNum.size();i++){
                if(n_font == ((Integer)fontNum.get(i)).intValue()){
                    fontEnc.set(i,new Integer(n_fcharset));
                    return;
                }
            }
            fontNum.add(new Integer(n_font));
            fontEnc.add(new Integer(n_fcharset));
        }
        public String get(int n_font) {
            for(int i=0;i<fontNum.size();i++){
                if(n_font == ((Integer)fontNum.get(i)).intValue()){
                    int cset = ((Integer)fontEnc.get(i)).intValue();
                    if(cset >= 0 && cset < 1500){
                        return rtfFontset[cset];
                    }
                    return "";
                }
            }
            return "";
        }
    }
    
    /** ファイル内容をテキストとして取得 **/
    private static StringBuffer readString(InputStream stream) {
        StringBuffer inb = new StringBuffer();
        try {
            String str = "";
            InputStreamReader fis = new InputStreamReader(stream);
            BufferedReader br = new BufferedReader(fis);
            while((str = br.readLine())!=null) {
                inb.append(str); inb.append("\n");
            }
            br.close(); fis.close();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return inb;
    }
    
    /** フォント情報の読み取り **/
    private static void getRTFfontset(StringBuffer inb) {
        boolean ffont = false;
        int n_font = -1;
        for(int i=0;i<inb.length();i++){
            if(ffont && inb.charAt(i) == ';'){
                ffont = false; n_font = -1; continue;
            }
// get font set
            if(!ffont && i < inb.length()-4 && inb.substring(i,i+2).equals("\\f")){
                if(Character.isDigit(inb.substring(i+2,i+3).charAt(0))){
// get font number
                    for(int fi=3;fi<6;fi++){
                        if(!Character.isDigit(inb.substring(i+fi,i+fi+1).charAt(0))){
                            try {
                                n_font = Integer.valueOf(inb.substring(i+2,i+fi)).intValue();
                                break;
                            } catch(java.lang.NumberFormatException e) {
                                n_font = -1;
                                break;
                            }
                        }
                    }
                    if(n_font >= 0){
                        ffont = true;
                    }
                }
            }
// get 'fcharset' of language type
            if(ffont && i < inb.length()-9 && inb.substring(i,i+9).equals("\\fcharset")){
                int n_fcharset = -1;
// get font number
                for(int fi=9;fi<13;fi++){
                    if(!Character.isDigit(inb.substring(i+fi,i+fi+1).charAt(0))){
                        try {
                            n_fcharset = Integer.valueOf(inb.substring(i+9,i+fi)).intValue();
                            //System.out.print("["+n_font+"] fcharset is "+n_fcharset+": ");
                            if(n_fcharset >= 0){
                                myFonts.add(n_font,n_fcharset);
                                //System.out.println(myFonts.get(n_font));
                            } else {
                                //System.out.println();
                            }
                            break;
                        } catch(java.lang.NumberFormatException e) {
                            n_font = -1;
                            break;
                        }
                    }
                }
            }
        }
    }
    
    /** RTF文字列文字の変換(SJIS to UTF8) **/
    private static InputStream ConvertJapanese(InputStream stream) {
        ByteArrayOutputStream oStream=new ByteArrayOutputStream();
        try {
            byte b1[] = new byte[1];
            byte b2[] = new byte[2];
            int skip = 0,n_font = -1,n_unicode = -1;
            String cset = "";
            boolean breadrule = false;
            boolean bsymbol = false;
            boolean bbackslash = false;
            boolean breadfont = false;
            
            StringBuffer inb = readString(stream);
            
// get fontset information
            getRTFfontset(inb);
            
// read strings
            for(int i=0;i<inb.length();i++,skip=0){
// get font set
                if(i < inb.length()-4 && inb.substring(i,i+2).equals("\\f")){
                    if(Character.isDigit(inb.substring(i+2,i+3).charAt(0))){
// get font number
                        for(int fi=3;fi<6;fi++){
                            if(!Character.isDigit(inb.substring(i+fi,i+fi+1).charAt(0))){
                                try {
                                    n_font = Integer.valueOf(inb.substring(i+2,i+fi)).intValue();
                                    cset = myFonts.get(n_font);
                                    if(cset.equals("MacSymbol")){
                                        bsymbol = true;
                                    } else {
                                        bsymbol = false;
                                    }
                                    break;
                                } catch(java.lang.NumberFormatException e) {
                                    break;
                                }
                            }
                        }
                    }
                }
// get 'fcharset' of language type
                if(i < inb.length()-9 && inb.substring(i,i+9).equals("\\fcharset")){
                    breadfont = true;
                }
// get 'fchar' and 'lchar' rule
                if(i < inb.length()-9 && inb.substring(i,i+9).equals("\\*\\fchars")){
                    breadrule = true;
                }
                if(i < inb.length()-9 && inb.substring(i,i+9).equals("\\*\\lchars")){
                    breadrule = true;
                }
// get unicode character
                if(i < inb.length()-4 && inb.substring(i,i+2).equals("\\u")){
                    char c = inb.substring(i+2,i+3).charAt(0);
                    if(Character.isDigit(c) || c == '-'){
// get unicode number
                        for(int fi=3;fi<9;fi++){
                            if(!Character.isDigit(inb.substring(i+fi,i+fi+1).charAt(0))){
                                try {
                                    if(c == '-'){
                                        n_unicode = -Integer.valueOf(inb.substring(i+3,i+fi)).intValue();
                                    } else {
                                        n_unicode = Integer.valueOf(inb.substring(i+2,i+fi)).intValue();
                                    }
                                    break;
                                } catch(java.lang.NumberFormatException e) {
                                    break;
                                }
                            }
                        }
                    }
                }
// convert japanese
                if(!breadfont && i < inb.length()-8 && inb.substring(i,i+2).equals("\\'")){
                    byte v1,v2;
                    
                    v1 = (byte)Character.digit((char)inb.substring(i+2,i+4).getBytes("iso-8859-1")[0],16);
                    v2 = (byte)Character.digit((char)inb.substring(i+3,i+4).getBytes("iso-8859-1")[0],16);
                    String code = "";
                    if(!bsymbol && !breadrule){
                        b2[0] = (byte)(v1 * 16 + v2);
                        if(inb.substring(i+4,i+6).equals("\\'")) {
                            v1 = (byte)Character.digit((char)inb.substring(i+6,i+7).getBytes("iso-8859-1")[0],16);
                            v2 = (byte)Character.digit((char)inb.substring(i+7,i+8).getBytes("iso-8859-1")[0],16);
                            b2[1] = (byte)(v1 * 16 + v2);
                            if(cset.equals("")) code = new String(b2,"SJIS");
                            else code = new String(b2,cset);
                            skip += 7;
                        } else {
                            v1 = v2 = 0;
                            if(isRtfChar(inb.substring(i+4,i+5).charAt(0))){
                                b2[1] = inb.substring(i+4,i+5).getBytes("iso-8859-1")[0];
                                if(cset.equals("")) code = new String(b2,"SJIS");
                                else code = new String(b2,cset);
                                skip += 4;
                            } else {
                                b1[0] = b2[0];
                                if(cset.equals("")) code = new String(b1,"iso-8859-1");
                                else code = new String(b1,cset);
                                skip += 3;
                            }
                        }
                    } else {
                        b1[0] = (byte)(v1 * 16 + v2);
                        if(!breadrule){
                            code = new String(b1,cset);
                            if(cset.equals("")) code = new String(b1,"iso-8859-1");
                            else code = new String(b1,cset);
                        } else code = new String(b1,"iso-8859-1");
                        skip += 3;
                    }
                    String ascii = escapeString(code,n_unicode);
                    if(ascii.indexOf("\\u65533") > 0){
                        //System.out.print(inb.substring(i+0,i+skip+1)+" : ");
                        //System.out.print(code+": "+cset+" : "+ascii+" : ");
                        for(int ci=0;ci<code.length();ci++){
                            int v = code.charAt(ci);
                            //System.out.print(code.charAt(ci)+"["+v+"]"+", ");
                        }
                        //System.out.println();
                    }
                    oStream.write(ascii.getBytes("iso-8859-1"));
                    i += skip;
                    bbackslash = false;
                    n_unicode = -1;
                } else {
                    char ch = inb.charAt(i);
                    if(ch == '\\') bbackslash = true;
                    else if(!isRtfChar(ch)) bbackslash = false;
                    if(breadfont || bbackslash || !bsymbol || !isRtfChar(ch)) {
                        oStream.write(ch);
                    } else {
                        byte[] sb = new byte[1];
                        sb[0] = inb.substring(i,i+1).getBytes()[0];
                        String ascii = escapeString(new String(sb,"MacSymbol"),-1);
                        oStream.write(ascii.getBytes("iso-8859-1"));
                    }
                }
                if(breadfont){
                    if(inb.substring(i,i+1).equals(";")){
                        breadfont = false;
                    }
                }
                if(breadrule){
                    if(inb.substring(i,i+1).equals("}")){
                        breadrule = false;
                    }
                }
            }
            oStream.close();
        } catch(UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch(IOException ex) {
            ex.printStackTrace();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        ByteArrayInputStream rt=new ByteArrayInputStream(oStream.toByteArray());
        return rt;
    }
    
    /** RTF unicode 文字列かどうか **/
    private static boolean isRtfChar(char c) {
        if(c == '\r' || c == '\n') return false;
        if(c == '\\' || c == '{' || c == '}' || c == ' ') return false;
                return true;
    }
    
    private static String escapeString(String str,int n_unicode) throws IOException {
        StringBuffer sb = new StringBuffer();
        if (str == null) {
            return "";
        }
        int sz;
        sz = str.length();
        for(int i = 0; i < sz; i++) {
            char ch = str.charAt(i);
            int v = ch;
// handle unicode
            if(ch > 0xfff) {
                if(n_unicode > 0) {
                    if(v != n_unicode){
                        sb.append("{\\u"); sb.append(v); sb.append("}");
                    }
                } else {
                    if(v != 0x10000 + n_unicode){
                        sb.append("{\\u"); sb.append(v); sb.append("}");
                    }
                }
            } else if(ch > 0xff) {
                sb.append("\\u"); sb.append(v); sb.append("? ");
            } else if(ch > 0x7f) {
                sb.append("\\u"); sb.append(v);
                sb.append("? ");
            } else if(ch < 32) {
                switch(ch) {
                    case '\b':
                        sb.append('\\'); sb.append('b'); break;
                    case '\n':
                        sb.append('\\'); sb.append('n'); break;
                    case '\t':
                        sb.append('\\'); sb.append('t'); break;
                    case '\f':
                        sb.append('\\'); sb.append('f'); break;
                    case '\r':
                        sb.append('\\'); sb.append('r'); break;
                    default:
                        if(ch > 0xf) sb.append("\\u"+v);
                        else sb.append("\\u"+v);
                        break;
                }
            } else {
                switch(ch) {
                    case '\'': case '"': case '\\': case '{': case '}':
                            sb.append('\\'); sb.append(ch);
                                    break;
                    default:
                        sb.append(ch);
                        break;
                }
            }
        }
        return new String(sb);
    }
    
    /** 文字列から１行ずつ抽出 **/
    private static ArrayList splitLine(String s) {
        String body = s;
        body = body.replaceAll("\\Q\r\n","\n");
        body = body.replaceAll("\\Q\r" ,"\n");
        String[] ss = body.split("\\Q\n");
        ArrayList v = new ArrayList();
        for(int i=0;i<ss.length;i++) v.add(ss[i]);
        return v;
    }
    
    /** RTFファイルから１行ずつ抽出 **/
    public static DefaultStyledDocument readRTF(InputStream stream) {
        StringBuffer sb = new StringBuffer();
        
        InputStream instream = ConvertJapanese(stream);
        RTFEditorKit rtf = new RTFEditorKit();
        DefaultStyledDocument doc = new DefaultStyledDocument();
        try {
             InputStreamReader isr = new InputStreamReader(instream,"UTF-8");//UTF-8          
             rtf.read(isr,doc,0);
//            rtf.read(instream,doc,0);
        } catch(Exception ex) {
            System.err.println("RTF read error: "+ex);
        }
        return doc;
    }
    
    
}