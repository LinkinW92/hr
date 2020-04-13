package my.linkin;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.IntsRefBuilder;
import org.apache.lucene.util.fst.Builder;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.fst.PositiveIntOutputs;
import org.apache.lucene.util.fst.Util;

public class IndexDemo {

    public static void main(String[] args) {
        try {
            String inputValues[] = {"cat", "deep", "do", "dog", "dogs"};
            long outputValues[] = {5, 7, 17, 18, 21};
            PositiveIntOutputs outputs = PositiveIntOutputs.getSingleton();
            Builder<Long> builder = new Builder<Long>(FST.INPUT_TYPE.BYTE1, outputs);
            BytesRef scratchBytes = new BytesRef();

            IntsRef scratchInts = new IntsRef();
            for (int i = 0; i < inputValues.length; i++) {
                scratchBytes = new BytesRef(inputValues[i].getBytes());
                IntsRefBuilder intsRefBuilder = new IntsRefBuilder();
                intsRefBuilder.copyInts(scratchInts);
                builder.add(Util.toIntsRef(scratchBytes, intsRefBuilder), outputValues[i]);
            }
            FST<Long> fst = builder.finish();
            Long value = Util.get(fst, new BytesRef("dogs"));
            System.out.println(value); // 18
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
