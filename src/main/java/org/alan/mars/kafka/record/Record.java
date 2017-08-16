package org.alan.mars.kafka.record;

/**
 * Created on 2017/5/24.
 *
 * @author Alan
 * @since 1.0
 * <p>
 * 记录数据结构
 */
public class Record {
    /**
     * 主题
     */
    public String topic;
    /**
     * 数据内容
     */
    public Object data;

    public Record(String topic, Object data) {
        this.topic = topic;
        this.data = data;
    }
}
