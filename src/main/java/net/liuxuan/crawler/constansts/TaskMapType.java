package net.liuxuan.crawler.constansts;

/**
 * @author Liuxuan
 * @version v1.0.0
 * @description net.liuxuan.crawler.constansts 包下xxxx工具
 * @date 2023/2/7
 **/
public enum TaskMapType {
    NormalTask(0, "普通任务"),
    TopHubTaskType(1, "TopHub任务");

    int typeId;
    String typeName;

    TaskMapType(int typeId, String typeName) {
        this.typeId = typeId;
        this.typeName = typeName;
    }

    public int id() {
        return typeId;
    }

    public String typeName() {
        return typeName;
    }
}
