package io.github.laplacedemon.qthings.mqtt.topic;

import org.junit.Assert;
import org.junit.Test;

public class TestAtomicUnsignedShort {
	
	@Test
	public void testAdd() throws InterruptedException {
		AtomicUnsignedShort s = new AtomicUnsignedShort();
		Thread t0 = new Thread(()-> {
			for(int i = 0;i < 20000; i++) {
				s.incrementAndGet();
			}
		});
		t0.start();
		
		Thread t1 = new Thread(()-> {
			for(int i = 0;i < 20000; i++) {
				s.incrementAndGet();
			}
		});
		t1.start();
		
		Thread t2 = new Thread(()-> {
			for(int i = 0;i < 20000; i++) {
				s.incrementAndGet();
			}
		});
		t2.start();
		
		Thread t3 = new Thread(()-> {
			for(int i = 0;i < 20000; i++) {
				s.incrementAndGet();
			}
		});
		t3.start();
		
		
		t0.join();
		t1.join();
		t2.join();
		t3.join();
		
		int value = s.get();
		Assert.assertEquals(value, 80000 - 65536);
	}
	
	@Test
	public void testSub() throws InterruptedException {
		AtomicUnsignedShort s = new AtomicUnsignedShort();
		
		Thread t0 = new Thread(()-> {
			for(int i = 0;i < 20000; i++) {
				s.incrementAndGet();
			}
		});
		t0.start();
		
		Thread t1 = new Thread(()-> {
			for(int i = 0;i < 20000; i++) {
				s.incrementAndGet();
			}
		});
		t1.start();
		
		Thread t2 = new Thread(()-> {
			for(int i = 0;i < 20000; i++) {
				s.incrementAndGet();
			}
		});
		t2.start();
		
		Thread t3 = new Thread(()-> {
			for(int i = 0;i < 20000; i++) {
				s.decrementAndGet();
			}
		});
		t3.start();
		
		Thread t4 = new Thread(()-> {
			for(int i = 0;i < 20000; i++) {
				s.decrementAndGet();
			}
		});
		t4.start();
		
		Thread t5 = new Thread(()-> {
			for(int i = 0;i < 20000; i++) {
				s.decrementAndGet();
			}
		});
		t5.start();
		
		
		t0.join();
		t1.join();
		t2.join();
		t3.join();
		t4.join();
		t5.join();
		
		int value = s.get();
		Assert.assertEquals(value, 0);
	}
}
