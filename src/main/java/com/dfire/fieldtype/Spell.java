package com.dfire.fieldtype;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.IndexableField;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.schema.TrieIntField;


/**
 * @author 粟谷 sugu@2dfire.com
 * @date 2018年03月19日 下午2:01:51
 */
public class Spell extends TrieIntField{
    private static final HanyuPinyinOutputFormat outputFormat;
    /**用int的32位，分为5段，分别记录5个值，每个值分配6个bit*/
    public static final int BitsPerValue = 6;
    public static final int NumOfValue = 5;
    private static final Log logger = LogFactory.getLog(FirstCharacter.class);

    static {
        outputFormat = new HanyuPinyinOutputFormat();
        outputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        logger.info("successful load pinyin4j");
    }
    @Override
    public IndexableField createField(SchemaField field, Object value, float boost){
        String externalVal = (String) value;
        externalVal = externalVal.trim();
        if ((externalVal == null) || ("".equals(externalVal.trim())))
            throw new IllegalStateException("name can not be null");
        int maxNum = externalVal.length() > NumOfValue ? NumOfValue:externalVal.length();
        int result = 0;
        for(int i=0;i<maxNum;i++){
            char fc = externalVal.charAt(i);
            try {
                if(FieldHelper.isChinese(fc)){
                    String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(fc,outputFormat);
                    /**多音字暂定取第一个*/
                    fc = pinyin[0].charAt(0);
                }else if(FieldHelper.isCharOrNum(fc)){
                    fc = Character.toLowerCase(fc);
                }else{
                    /**特殊字符默认排在最后,数字无具体意义*/
                    fc = 118;
                }
                result = FieldHelper.processInt(result,fc,i,BitsPerValue);
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }

        return super.createField(field, result, boost);
    }


    public static void main(String[] args) {
        String externalVal = "二维火";
        externalVal = externalVal.trim();
        int result = 0;
        int maxNum = externalVal.length() > NumOfValue ? NumOfValue:externalVal.length();
        for(int i=0;i<maxNum;i++){
            char fc = externalVal.charAt(i);
            try {
                if(FieldHelper.isChinese(fc)){
                    String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(fc,outputFormat);
                    /**多音字暂定取第一个*/
                    fc = pinyin[0].charAt(0);
                }else if(FieldHelper.isCharOrNum(fc)){
                    fc = Character.toLowerCase(fc);
                }else{
                    /**特殊字符默认排在最后,数字无具体意义*/
                    fc = 138;
                }
                result = FieldHelper.processInt(result,fc,i,BitsPerValue);
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
        System.out.println(result);
    }
}
