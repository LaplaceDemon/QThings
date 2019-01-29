package io.github.laplacedemon.qthings.mqtt.protocal.packet;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import io.github.laplacedemon.qthings.mqtt.protocal.util.RemainingLengthUtil;


public class TestRemainingLength {
	
	@Test
	public void hasNext() {
		for(byte b = 0; b <= Byte.MAX_VALUE && b >= 0;b++) {
			byte x = (byte) ((b >> 7) & 0x01);
			Assert.assertEquals(x, (byte)0);
		}
		
		for(byte b = Byte.MIN_VALUE; b < 0 ;b++) {
			byte x = (byte) ((b >> 7) & 0x01);
			Assert.assertEquals(x, (byte)1);
		}
	}
	
	@Test
	public void decode0() {
		byte[] bs = {(byte)0x80,(byte)0x01};
		int len = RemainingLengthUtil.decode(bs);
		Assert.assertEquals(len, 128);
	}
	
	@Test
	public void decode1() {
		byte[] bs = {(byte)0xFF,(byte)0x7F};
		int len = RemainingLengthUtil.decode(bs);
		Assert.assertEquals(len, 16383);
	}
	
	@Test
	public void decode2() {
		byte[] bs = {(byte)0x80,(byte)0x80,(byte)0x01};
		int len = RemainingLengthUtil.decode(bs);
		Assert.assertEquals(len, 16384);
	}
	
	@Test
	public void decode3() {
		byte[] bs = {(byte)0xff,(byte)0xff,(byte)0x7f};
		int len = RemainingLengthUtil.decode(bs);
		Assert.assertEquals(len, 2097151);
	}
	
	@Test
	public void decode4() {
		byte[] bs = {(byte)0x80,(byte)0x80,(byte)0x80,(byte)0x01};
		int len = RemainingLengthUtil.decode(bs);
		Assert.assertEquals(len, 2097152);
	}
	
	@Test
	public void decode5() {
		byte[] bs = {(byte)0xff, (byte)0xff,(byte)0xff,(byte)0x7f};
		int len = RemainingLengthUtil.decode(bs);
		Assert.assertEquals(len, 268435455);
	}
	
	@Test
	public void encode0() {
		byte[] encode = RemainingLengthUtil.encode(128);
		System.out.println(Arrays.toString(encode));
	}
	
	@Test
	public void encode1() {
		byte[] encode = RemainingLengthUtil.encode(16383);
		System.out.println(Arrays.toString(encode));
	}
	
	@Test
	public void encode2() {
		byte[] encode = RemainingLengthUtil.encode(16384);
		System.out.println(Arrays.toString(encode));
	}
	
	@Test
	public void encode3() {
		byte[] encode = RemainingLengthUtil.encode(2097151);
		System.out.println(Arrays.toString(encode));
	}
	
	@Test
	public void encode4() {
		byte[] encode = RemainingLengthUtil.encode(2097152);
		System.out.println(Arrays.toString(encode));
	}
	
	@Test
	public void encode5() {
		byte[] encode = RemainingLengthUtil.encode(268435455);
		System.out.println(Arrays.toString(encode));
	}

}
