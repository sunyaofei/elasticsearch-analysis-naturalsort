package org.xbib.elasticsearch.index.analysis.naturalsort;

import org.apache.lucene.analysis.tokenattributes.CharTermAttributeImpl;
import org.apache.lucene.util.BytesRef;

import java.text.Collator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NaturalSortKeyAttributeImpl extends CharTermAttributeImpl {

    private final static Pattern numberPattern = Pattern.compile("(\\+|\\-)?([0-9]+)");

//    static class IgnoreCaseComp implements Comparator<String> {
//        Collator col;
//        IgnoreCaseComp() {
//            // Get a Collator for this locale.
//            col = Collator.getInstance(); //工厂方法
//            // Have it consider only primary differences.
//            col.setStrength(Collator.PRIMARY);
//        }
//        // Uses Collator's compare() method to compare strings.
//        public int compare(String strA, String strB) {
//            return col.compare(strA, strB);//采用Collator的compare方法xi zao/ xin zang/ xi nan
//        }
//    }
//
//    public static void main(String[] args) {
//
//        List<String> chinese = Lists.newArrayList("啊啊", "饿了么", "洗澡", "心脏", "西南",  "波浪");
//        Collections.sort(chinese, new IgnoreCaseComp());
//        System.out.println(chinese);
//
//    }

    private final Collator collator;

    private final int digits;

    private final int maxTokens;

    public NaturalSortKeyAttributeImpl(Collator collator, int digits, int maxTokens) {
        this.collator = collator;
        this.digits = digits;
        this.maxTokens = maxTokens;
    }

    @Override
    public BytesRef getBytesRef() {
        byte[] collationKey = collator.getCollationKey(natural(toString())).toByteArray();
        final BytesRef ref = this.builder.get();
        ref.bytes = collationKey;
        ref.offset = 0;
        ref.length = collationKey.length;
        return ref;
    }

    private String natural(String s) {
        StringBuffer sb = new StringBuffer();
        Matcher m = numberPattern.matcher(s);
        int foundTokens = 0;
        while (m.find()) {
            int len = m.group(2).length();
            String repl = String.format("%0" + digits + "d", len) + m.group();
            m.appendReplacement(sb, repl);

            foundTokens++;
            if (foundTokens >= maxTokens){
                break;
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
