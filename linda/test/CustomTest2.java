package linda.test;

import linda.*;

/**
 * Test if read primitive is a blocking operation.
 *
 */
public class CustomTest2 {

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
				Tuple motif1 = new Tuple(Integer.class, Integer.class);
				Tuple motif2 = new Tuple(Integer.class, String.class);
				Tuple res1 = linda.read(motif1);
				System.out.println("(1) Result:" + res1);
				Tuple res2 = linda.read(motif2);
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
				
				Tuple t1 = new Tuple(4, "foo");
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