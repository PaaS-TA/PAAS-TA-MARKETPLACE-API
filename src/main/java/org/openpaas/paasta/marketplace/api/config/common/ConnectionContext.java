package org.openpaas.paasta.marketplace.api.config.common;

import org.cloudfoundry.reactor.DefaultConnectionContext;

import java.util.Date;

public class ConnectionContext {

    DefaultConnectionContext connectionContext;
    Date create_time;

    public ConnectionContext(DefaultConnectionContext connectionContext, Date create_time){
        this.connectionContext = connectionContext;
        this.create_time = create_time;
    }

    public DefaultConnectionContext getConnectionContext() {
        return connectionContext;
    }

    public Date getCreate_time() {
        return create_time;
    }
}
