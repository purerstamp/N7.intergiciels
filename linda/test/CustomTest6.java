package linda.test;

import java.util.ArrayList;

import linda.*;

/**
 * Test if takeAll returns several tuples (corresponding to a template)
 * and delete all those tuples in the tuplespace.
 *
 */
public class CustomTest6 {

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
				
				Tuple t1 = new Tuple(4, "foo");
                System.out.println("(1) write: " + t1);
                linda.write(t1);
                
                Tuple t2 = new Tuple(4, 10);
                System.out.println("(1) write: " + t2);
                linda.write(t2);
                
                Tuple t3 = new Tuple("foo", "bar");
                System.out.println("(1) write: " + t3);
                linda.write(t3);
                
                Tuple t4 = new Tuple(8, 42);
                System.out.println("(1) write: " + t4);
                linda.write(t4);
				
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
				Tuple motif = new Tuple(Integer.class, Integer.class);
				ArrayList<Tuple> res = new ArrayList<>(linda.takeAll(motif));
				System.out.println("(2) Result:" + res);
				
				linda.debug("(2)");
			}
		}.start();

	}

}
