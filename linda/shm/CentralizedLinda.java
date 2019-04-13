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
	
    public CentralizedLinda() {}

    ArrayList<Tuple> tuplespace = new ArrayList<Tuple>();
    HashMap<Tuple, ArrayList<Callback>> eventsCollectionRead = new HashMap<Tuple, ArrayList<Callback>>();
    HashMap<Tuple, ArrayList<Callback>> eventsCollectionTake = new HashMap<Tuple, ArrayList<Callback>>();
    final Lock lock = new ReentrantLock();
    final Condition notEmpty = lock.newCondition();
    

	@Override
	/**
	 * signalAll() is used instead of signal() for safety measures :
	 * in the case of a missed notification by one thread, 
	 * others will most likely receive theirs and compete for the lock,
	 * thus, continuity is almost guaranteed (?)
	 * ----
	 * callbacks have priority over waiting threads
	 */
	public void write(Tuple t) {
		lock.lock();
		tuplespace.add(t);
		if (eventsCollectionRead.containsKey(t)) {
			eventsCollectionRead.get(t).get(0).call(t);
			eventsCollectionRead.get(t).remove(0);
			if (eventsCollectionRead.get(t).isEmpty()) {
				eventsCollectionRead.remove(t);
			}
		} else {
			if (eventsCollectionTake.containsKey(t)) {
				eventsCollectionTake.get(t).get(0).call(t);
				eventsCollectionTake.get(t).remove(0);
				if (eventsCollectionTake.get(t).isEmpty()) {
					eventsCollectionTake.remove(t);
				}
			}
		}
		notEmpty.signalAll();
		lock.unlock();
	}

	@Override
	public Tuple take(Tuple template) {
		lock.lock();
		do {
			for (Tuple t : tuplespace) {
				if (t.matches(template)) {
					tuplespace.remove(t);
					lock.unlock();
					return t;
				}
			}
			try {
				notEmpty.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (true);
	}

	@Override
	public Tuple read(Tuple template) {
		lock.lock();
		do {
			for (Tuple t : tuplespace) {
				if (t.matches(template)) {
					lock.unlock();
					return t;
				}
			}
			try {
				notEmpty.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (true);
	}

	@Override
	public Tuple tryTake(Tuple template) {
		lock.lock();
		for (Tuple t : tuplespace) {
			if (t.matches(template)) {
				Tuple t_tmp = t;
				tuplespace.remove(t_tmp);
				lock.unlock();
				return t_tmp;
			}
		}
		lock.unlock();
		return null;
	}

	@Override
	public Tuple tryRead(Tuple template) {
		lock.lock();
		for (Tuple t : tuplespace) {
			if (t.matches(template)) {
				lock.unlock();
				return t;
			}
		}
		lock.unlock();
		return null;
	}

	@Override
	public Collection<Tuple> takeAll(Tuple template) {
		ArrayList<Tuple> tuples = new ArrayList<Tuple>() ;
		lock.lock();
		for (Tuple t : tuplespace) {
			if (t.matches(template)) {
				tuples.add(t);
				tuplespace.remove(t);
			}
		}
		lock.unlock();
		return tuples;
	}

	@Override
	public Collection<Tuple> readAll(Tuple template) {
		ArrayList<Tuple> tuples = new ArrayList<Tuple>() ;
		lock.lock();
		for (Tuple t : tuplespace) {
			if (t.matches(template)) {
				tuples.add(t);
			}
		}
		lock.unlock();
		return tuples;
	}

	@Override
	/**
	 * IMMEDIATE case : according to operation,
	 * 	-> success : callback.call
	 * 	-> failure : callback stored to hashmap, callbacks are grouped by template in arraylists
	 * 
	 * FUTURE case : according to operation,
	 * -> callback stored to hashmap (...)
	 * 
	 * 
	 * => WRITE operation : check for callbacks matching template, trigger if any, priority to read (?), no daisy-chaining (?)
	 */
	public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
		if (timing == eventTiming.IMMEDIATE) {
			switch(mode)
			{
			case READ : 
				Tuple t_tmpR = new Tuple();
				t_tmpR = this.tryRead(template);
				if (t_tmpR == null) {
					if (eventsCollectionRead.containsKey(template)) {
						eventsCollectionRead.get(template).add(callback);
					} else {
						eventsCollectionRead.put(template, new ArrayList<Callback>());
						eventsCollectionRead.get(template).add(callback);
					}
				} else {
					callback.call(template);
				}
				break;
			case TAKE :	
				Tuple t_tmpT = new Tuple();
				t_tmpT = this.tryTake(template);
				if (t_tmpT == null) {
					if (eventsCollectionTake.containsKey(template)) {
						eventsCollectionTake.get(template).add(callback);
					} else {
						eventsCollectionTake.put(template, new ArrayList<Callback>());
						eventsCollectionTake.get(template).add(callback);
					}
				} else {
					callback.call(template);
				}
				break;
			}
		} else {
			switch(mode)
			{
			case READ :
				if (eventsCollectionRead.containsKey(template)) {
					eventsCollectionRead.get(template).add(callback);
				} else {
					eventsCollectionRead.put(template, new ArrayList<Callback>());
					eventsCollectionRead.get(template).add(callback);
				}			
			case TAKE :
				if (eventsCollectionTake.containsKey(template)) {
					eventsCollectionTake.get(template).add(callback);
				} else {
					eventsCollectionTake.put(template, new ArrayList<Callback>());
					eventsCollectionTake.get(template).add(callback);
				}
			}
		}
		
	}

	@Override
	public void debug(String prefix) {
		lock.lock();
		System.out.println("DEBUG " + "id : " + prefix.toString());
		Iterator<Tuple> iterator = tuplespace.iterator();
		while (iterator.hasNext()) {
			Tuple t = iterator.next();
			System.out.println(t);
		}
		lock.unlock();
	}
}
