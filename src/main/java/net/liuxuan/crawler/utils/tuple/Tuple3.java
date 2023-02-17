package net.liuxuan.crawler.utils.tuple;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description 两三个元素的元组，用于在一个方法里返回三种类型的值
 * @date 2019-09-11
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class Tuple3<V1, V2, V3> extends Tuple2<V1, V2> {
    public V3 v3;

    public Tuple3(V1 v1, V2 v2, V3 v3) {
        super(v1, v2);
        this.v3 = v3;
    }

    public static <V1, V2, V3> Tuple3<V1, V2, V3> of(V1 v1, V2 v2, V3 v3) {
        return new Tuple3<>(v1, v2, v3);
    }
    @Override
    public String toString() {
        return "Tuple3(" + v1 + "," + v2 +","+ v3 + ")";
    }
}
