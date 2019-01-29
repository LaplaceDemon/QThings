package io.github.laplacedemon.qthings.mqtt.handler;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutException;

public class ReadTimeoutHandler extends IdleStateHandler {

	public ReadTimeoutHandler(long readerIdleTime, TimeUnit unit) {
		super(readerIdleTime, 0, 0, unit);
	}

	@Override
	protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
		if(evt.state() == IdleState.READER_IDLE) {
			ctx.fireExceptionCaught(ReadTimeoutException.INSTANCE);
		}
	}

	@Override
	protected IdleStateEvent newIdleStateEvent(IdleState state, boolean first) {
		return super.newIdleStateEvent(state, first);
	}
	
}
