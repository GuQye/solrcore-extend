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
public class FirstCharacter extends TrieIntField{
    private static final HanyuPinyinOutputFormat outputFormat;

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
        char fc = externalVal.charAt(0);
        try {
            if(isChinese(fc)){
                String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(externalVal.charAt(0),outputFormat);
                /**多音字暂定取第一个*/
                fc = pinyin[0].charAt(0);
            }else if(isCharOrNum(fc)){
                fc = Character.toLowerCase(fc);
            }else{
                /**特殊字符默认排在最后*/
                fc = 126;
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        int first_character = (int) fc;
        return super.createField(field, first_character, boost);
    }

    private boolean isCharOrNum(char a) {
        int v = a;
        boolean isNum = (v >= 48) && (v <= 57);
        boolean isHighCase = (v >= 65) && (v <= 90);
        boolean isLowCase = (v >= 97) && (v <= 122);
        return isNum || isHighCase || isLowCase;
    }

    private  boolean isChinese(char a) {
        int v = a;
        return (v >= 19968) && (v <= 171941);
    }

    public static void main(String[] args) throws BadHanyuPinyinOutputFormatCombination {
        String chinese = "     长二维火";
        chinese = chinese.trim();
        //System.out.println(isCharOrNum('?'));


    }
}
