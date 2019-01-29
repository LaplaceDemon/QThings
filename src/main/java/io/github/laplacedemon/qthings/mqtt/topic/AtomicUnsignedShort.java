package io.github.laplacedemon.qthings.mqtt.topic;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicUnsignedShort {
	private AtomicInteger x;
	
	public AtomicUnsignedShort() {
		super();
		this.x = new AtomicInteger(0);
	}
	
	public AtomicUnsignedShort(int x) {
		super();
		this.x = new AtomicInteger(x);
	}
	
	public int incrementAndGet() {
		while(true) {
			int x0 = x.get();
			int x1 = x0 + 1;
			if(x1 >= 65536) {
				x1 = 0;
			}
			boolean compareAndSet = x.compareAndSet(x0, x1);
			if(compareAndSet) {
				return x1;
			}
		}
	}
	
	public int decrementAndGet() {
		while(true) {
			int x0 = x.get();
			int x1 = x0 - 1;
			if(x1 < 0) {
				x1 = 65535;
			}
			boolean compareAndSet = x.compareAndSet(x0, x1);
			if(compareAndSet) {
				return x1;
			}
		}
	}

	public int get() {
		return x.get();
	}
}
