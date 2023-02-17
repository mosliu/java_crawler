package net.liuxuan.crawler.utils.tuple;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description 两个元素的元组，用于在一个方法里返回两种类型的值
 * @date 2019-09-11
 **/
@EqualsAndHashCode
@Data
public class Tuple2<V1, V2> {

    public V1 v1;

    public V2 v2;

    public Tuple2(V1 v1, V2 v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public static <V1, V2> Tuple2<V1, V2> of(V1 v1, V2 v2) {
        return new Tuple2<>(v1, v2);
    }

    @Override
    public String toString() {
        return "Tuple2(" + v1 + "," + v2 + ")";
    }

}
