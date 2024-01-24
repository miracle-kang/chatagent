package com.miraclekang.chatgpt.common.access;

/**
 * 操作类型(动作)
 */
public enum EnumOperationAction {

    CREATE("新增"),
    UPDATE("修改"),
    DELETE("删除"),
    READ("查询");

    private final String desc;

    EnumOperationAction(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public static EnumOperationAction forName(String name) {
        for (EnumOperationAction value : EnumOperationAction.values()) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }
}
