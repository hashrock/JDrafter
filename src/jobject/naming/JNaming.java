/*
 * JNaming.java
 *
 * Created on 2008/05/05, 8:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jobject.naming;

import java.util.regex.Pattern;

/**
 *自動命名メカニズムによる順序付けを設定します.
 * @author takashi
 */
public class JNaming implements Comparable<JNaming>{
    private String prefixer;
    private int index;
    private static final String regex=".*\\D\\d{1,8}\\z";
    private static final String indexer="\\d{1,8}\\z"; 
    /**
     * 冠詞を指定し、JNamingクラスのインスタンスを構築します.
     * @param name 指定する冠詞. 
     */
    public JNaming(String name) {
        if (Pattern.matches(regex,name)){
            prefixer=name.replaceAll(indexer,"");
            index=Integer.parseInt(name.replace(prefixer,""));
        }else{
            prefixer=name;
            index=0;
        }        
    }
    /**
     * 冠詞及び、一連番号を指定し、JNaminクラスのインスタンスを構築します.
     * @param prefixer 指定する冠詞.
     * @param index 指定する一連番号.
     */
    public JNaming(String prefixer,int index){
        this.prefixer=prefixer;
        this.index=index;
    }
    /**
     * このJNamingで指定可能な名称のうち最小のインスタンスを返します.
     * @return
     */
    public JNaming minimumName(){
        return new JNaming(prefixer,-1);
    }
    /**
     * このJNamingで指定可能な名称のうち最大のインスタンスを返します。
     * @return
     */
    public JNaming maximumName(){
        return new JNaming(prefixer,Integer.MAX_VALUE);
    }
    /**
     * このJNamingの冠詞を返します.
     * @return
     */
    public String getPrefixer(){
        return prefixer;
    }
    /**
     * このJNamingの一連番号を返します.
     * @return
     */
    public int getIndex(){
        return index;
    }
    /**
     * 指定されたJNamingとこのJNaming自身の順序付けの比較を行います.
     * @param o 比較対象のJNaming
     * @return 順序付けの比較結果
     */
    @Override
    public int compareTo(JNaming o) {
        int result=prefixer.compareTo(o.prefixer);
        if (result !=0) return result;
        return (int)Math.signum(index-o.index); 
    }
    /**
     * このJNamingの名称を返します.
     * @return
     */
    @Override
    public String toString(){
        return prefixer+String.valueOf(index).trim();
    }
}
