package linda.shm;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

/** Shared memory implementation of Linda. */
public class CentralizedLinda implements Linda {
	
    public CentralizedLinda() {
    }

    ArrayList<Tuple> tuplespace = new ArrayList<Tuple>();
    final Lock lock = new ReentrantLock();
    final Condition empty = lock.newCondition();
    

	@Override
	public void write(Tuple t) {
		synchronized (this) {
			tuplespace.add(t);
			this.notifyAll();
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
		
		synchronized (this) {
			for (Tuple t : tuplespace) {
				if (t.matches(template)) {
					Tuple t_temp = t;
					tuplespace.remove(t_temp);
					return t_temp;
				}
			}
		}
		return null;
	}

	@Override
	public Tuple tryRead(Tuple template) {

		synchronized (this) {
			for (Tuple t : tuplespace) {
				if (t.matches(template)) {
					return t;
				}
			}
		}
		return null;
	}

	@Override
	public Collection<Tuple> takeAll(Tuple template) {
		ArrayList<Tuple> tuples = new ArrayList<Tuple>() ;
		synchronized (this) {
			for (Tuple t : tuplespace) {
				if (t.matches(template)) {
					tuples.add(t);
					tuplespace.remove(t);
				}
			}
		}
		return tuples;
	}

	@Override
	public Collection<Tuple> readAll(Tuple template) {
		ArrayList<Tuple> tuples = new ArrayList<Tuple>() ;
		synchronized (this) {
			for (Tuple t : tuplespace) {
				if (t.matches(template)) {
					tuples.add(t);
				}
			}
		}
		return tuples;
	}

	@Override
	public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
		if (timing == eventTiming.IMMEDIATE) {
			switch(mode)
			{
			case READ : callback.call(template);
						this.read(template);
						break;
			case TAKE :	callback.call(template);
						this.take(template);
						break;
			}
		} else {
			switch(mode)
			{
			case READ :
				
			case TAKE :
				
			}
		}
		
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
