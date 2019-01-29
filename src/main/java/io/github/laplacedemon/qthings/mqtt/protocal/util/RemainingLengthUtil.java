package io.github.laplacedemon.qthings.mqtt.protocal.util;

import io.github.laplacedemon.qthings.mqtt.protocal.exception.MQTTDecodeException;
import io.github.laplacedemon.qthings.mqtt.protocal.exception.MQTTEncodeException;

public class RemainingLengthUtil {
	
	public final static int MIN_VALUE = 0;
	public final static int MAX_VALUE = 268435455;


	public static int decode(byte... bs) {
		if(bs.length == 1) {
			return bs[0];
		} else if(bs.length == 2) {
			return (int)(bs[1]) * 128 + (int)(bs[0] & 0x7F);
		} else if(bs.length == 3) {
			return  (int)(bs[2]) * 128 * 128 + (int)(bs[1] & 0x7F) * 128 + (int)(bs[0] & 0x7F);
		} else if(bs.length == 4) {
			return  (int)(bs[3]) * 128 * 128 * 128 + (int)(bs[2] & 0x7F) * 128 * 128 + (int)(bs[1] & 0x7F) * 128 + (int)(bs[0]  & 0x7F);
		}
		
		throw new MQTTDecodeException();
	}
	
	public static byte[] encode(int length) {
		if(length >= 0 && length <= 127) {
			byte[] bs = new byte[1];
			bs[0] = (byte)length;
			return bs;
		} else if (length >= 128 && length <= 16383) {
			byte[] bs = new byte[2];
			bs[1] = (byte)(length / 128);
			bs[0] = (byte)(length - bs[1] * 128 - 128);
			return bs;
		} else if (length >= 16384 && length <= 2097151) {
			byte[] bs = new byte[3];
			bs[2] = (byte)(length / (128*128));
			int v0 = length % (128*128);
			bs[1] = (byte) (v0 / 128 - 128);
			int v1 = v0 % 128;
			bs[0] = (byte)(v1 - 128);
			return bs;
		} else if (length >= 2097152 && length <= 268435455) {
			byte[] bs = new byte[4];
			bs[3] = (byte)(length / (128*128*128));
			int v0 = length % (128*128*128);
			
			bs[2] = (byte) (v0 / (128*128) - 128);
			int v1 = v0 % (128*128);
			
			bs[1] = (byte)(v1 / 128 - 128);
			int v2 = v0 % 128;
			
			bs[0] = (byte)(v2 - 128);
			return bs;
		}
		
		throw new MQTTEncodeException();
	}
	
}
