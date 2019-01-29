package io.github.laplacedemon.qthings.mqtt.protocal.util;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;

public class BufferUtil {
	public static String readString(ByteBuf buffer, int length) {
		byte[] bs = new byte[length];
		buffer.readBytes(bs);
		return new String(bs, Charset.forName("UTF-8"));
	}

	public static byte[] readBytes(ByteBuf buffer, int length) {
		byte[] bs = new byte[length];
		buffer.readBytes(bs);
		return bs;
	}
}
