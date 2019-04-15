package linda.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;

import linda.Callback;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;
import linda.Tuple;
import linda.shm.CentralizedLinda;

public class LindaObjectImpl extends UnicastRemoteObject implements LindaObject {

	/**
	 * Auto-generated serial version UID
	 */
	private static final long serialVersionUID = 3136775299313441896L;
	
	public CentralizedLinda centralizedLinda = new CentralizedLinda();
	
	public LindaObjectImpl() throws RemoteException {
		super();
	}

	@Override
	public void write(Tuple t) {
		centralizedLinda.write(t);
	}

	@Override
	public Tuple take(Tuple template) {
		Tuple t = new Tuple();
		t = centralizedLinda.take(template);
		return t;
	}

	@Override
	public Tuple read(Tuple template) {
		Tuple t = new Tuple();
		t = centralizedLinda.read(template);
		return t;
	}

	@Override
	public Tuple tryTake(Tuple template) {
		Tuple t = new Tuple();
		t = centralizedLinda.tryTake(template);
		return t;
	}

	@Override
	public Tuple tryRead(Tuple template) {
		Tuple t = new Tuple();
		t = centralizedLinda.tryRead(template);
		return t;
	}

	@Override
	public Collection<Tuple> takeAll(Tuple template) {
		ArrayList<Tuple> tuples = new ArrayList<Tuple>();
		tuples = (ArrayList<Tuple>) centralizedLinda.takeAll(template);
		return tuples;
	}

	@Override
	public Collection<Tuple> readAll(Tuple template) {
		ArrayList<Tuple> tuples = new ArrayList<Tuple>();
		tuples = (ArrayList<Tuple>) centralizedLinda.readAll(template);
		return tuples;
	}

	@Override
	public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
		centralizedLinda.eventRegister(mode, timing, template, callback);
		// should callbacks be linked to client ?
		// -> callbackServer extends callback :
		// attribute client ?
	}

	@Override
	public void debug(String prefix) {
		centralizedLinda.debug(prefix);
	}

}
