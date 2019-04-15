package linda.test;

import linda.*;

/**
 * Test if take primitive is a blocking operation.
 *
 */
public class CustomTest1 {

	public static void main(String[] args) {

		final Linda linda = new linda.shm.CentralizedLinda();
        //final Linda linda = new linda.server.LindaClient("//localhost:4000/aaa");
		
		new Thread() {
			public void run() {
				try {
					Thread.sleep(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Tuple motif = new Tuple(Integer.class, Integer.class);
				Tuple res1 = linda.take(motif);
				System.out.println("(1) Result:" + res1);
				Tuple res2 = linda.take(motif);
				System.out.println("(1) Result:" + res2);
				linda.debug("(1)");
			}
		}.start();

		new Thread() {
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				Tuple t1 = new Tuple(4, 5);
                System.out.println("(2) write: " + t1);
                linda.write(t1);
				
				linda.debug("(2)");
			}
		}.start();
		
		new Thread() {
			public void run() {
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				Tuple t1 = new Tuple(4, 5);
                System.out.println("(3) write: " + t1);
                linda.write(t1);
				
				linda.debug("(3)");
			}
		}.start();
	}

}
