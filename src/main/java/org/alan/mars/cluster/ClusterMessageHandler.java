/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package org.alan.mars.cluster;

import org.alan.mars.net.Inbox;

/**
 * Created on 2017/4/6.
 *
 * @author Alan
 * @since 1.0
 */
public interface ClusterMessageHandler<T> extends Inbox<T> {
    int getMessageType();
}
