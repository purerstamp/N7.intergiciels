package linda.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import linda.Callback;
import linda.Tuple;

public class WrappedCallbackImpl extends UnicastRemoteObject implements WrappedCallback{

	private WrappedCallback cb;
	
	protected WrappedCallbackImpl(WrappedCallback cb) throws RemoteException {
		super();
		this.cb = cb;
	}
	
	/** Asynchronous call: the associated callback is concurrently run and this one immediately returns.
     * */
	@Override
    public void call(final Tuple t) throws RemoteException {
        new Thread() {
            public void run() {
                try {
					cb.call(t);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
            }
        }.start();
    }

}
