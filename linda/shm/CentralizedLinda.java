package linda.shm;

import java.util.*;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

/** Shared memory implementation of Linda. */
public class CentralizedLinda implements Linda {
	
    public CentralizedLinda() {
    }

    ArrayList<Tuple> tuplespace = new ArrayList<Tuple>();

	@Override
	public void write(Tuple t) {
		synchronized (this) {
			tuplespace.add(t);
			this.notify();
		}
	}

	@Override
	public Tuple take(Tuple template) {
		
		Tuple t_temp = read(template);
		tuplespace.remove(t_temp);
		return t_temp;
	}

	@Override
	public Tuple read(Tuple template) {

		synchronized (this) {
			do {
				for (Tuple t : tuplespace) {
					if (t.matches(template)) {
						return t;
					}
				}
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} while (true);
		}
	}

	@Override
	public Tuple tryTake(Tuple template) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tuple tryRead(Tuple template) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Tuple> takeAll(Tuple template) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Tuple> readAll(Tuple template) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(String prefix) {
		System.out.println("DEBUG " + "id : " + prefix.toString());
		Iterator<Tuple> iterator = tuplespace.iterator();
		while (iterator.hasNext()) {
			Tuple t = iterator.next();
			System.out.println(t);
		}
	}
}
