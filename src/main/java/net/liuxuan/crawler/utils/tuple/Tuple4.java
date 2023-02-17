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
public class Tuple4<V1, V2, V3, V4> extends Tuple3<V1, V2, V3> {
    public V4 v4;

    public Tuple4(V1 v1, V2 v2, V3 v3, V4 v4) {
        super(v1, v2, v3);
        this.v4 = v4;
    }

    public static <V1, V2, V3, V4> Tuple4<V1, V2, V3, V4> of(V1 v1, V2 v2, V3 v3, V4 v4) {
        return new Tuple4<>(v1, v2, v3, v4);
    }

    @Override
    public String toString() {
        return "Tuple4(" + v1 + "," + v2 + "," + v3 + "," + v4 + ")";
    }
}
