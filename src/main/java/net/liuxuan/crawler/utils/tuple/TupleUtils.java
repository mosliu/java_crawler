package net.liuxuan.crawler.utils.tuple;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description Tools for xx use
 * @date 2019-09-11
 **/
public class TupleUtils {
    public static <V1, V2> Tuple2<V1, V2> tuple(V1 v1, V2 v2) {
        return new Tuple2<V1, V2>(v1, v2);
    }

    public static <V1, V2, V3> Tuple3<V1, V2, V3> tuple(V1 v1, V2 v2, V3 v3) {
        return new Tuple3<V1, V2, V3>(v1, v2, v3);
    }

    public static <V1, V2, V3, V4> Tuple4<V1, V2, V3, V4> tuple(V1 v1, V2 v2, V3 v3, V4 v4) {
        return new Tuple4<V1, V2, V3, V4>(v1, v2, v3, v4);
    }


}
