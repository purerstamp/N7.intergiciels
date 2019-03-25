package linda.shm;

import java.util.ArrayList;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

/** Shared memory implementation of Linda. */
public class CentralizedLinda implements Linda {
	
    public CentralizedLinda() {
    }

    ArrayList<Tuple> tuplespace;

}
