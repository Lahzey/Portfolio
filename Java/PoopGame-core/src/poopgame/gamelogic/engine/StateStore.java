package poopgame.gamelogic.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateStore {

	private final long storageDuration;

	private final List<State> states = new ArrayList<>();
	private final Map<State, Long> stateTimes = new HashMap<>();

	public StateStore(long storageDuration) {
		this.storageDuration = storageDuration;
	}

	public void store(State state, long currentTime) {
		synchronized (states) {
			states.add(state);
			stateTimes.put(state, currentTime);
		}
		clearExpiredStates(currentTime);
	}

	public State getState(long maxStateTime) {
		if (states.size() == 0) return null;
		
		State result = null;

		State oldestState = states.get(0);
		long oldestStateTime = stateTimes.get(oldestState);
		if (oldestStateTime <= maxStateTime) {
			State newestState = states.get(states.size() - 1);
			long newestStateTime = stateTimes.get(newestState);

			// determine the faster search direction
			long oldestDif = maxStateTime - oldestStateTime;
			long newestDif = newestStateTime - maxStateTime;

			if (oldestDif < newestDif) {
				// scan from old to new
				long currentResultTime = Long.MIN_VALUE;
				for (int i = 0; i < states.size(); i++) {
					State state = states.get(i);
					long stateTime = stateTimes.get(state);

					if (stateTime <= maxStateTime) {
						if (stateTime > currentResultTime) {
							result = state;
							currentResultTime = stateTime;
						}
					} else {
						break; // stateTime is after maxStateTime, can abort now
					}
				}
			} else {
				// scan from new to old
				for (int i = states.size() - 1; i >= 0; i--) {
					State state = states.get(i);
					long stateTime = stateTimes.get(state);

					if (stateTime <= maxStateTime) {
						result = state;
						break; // first result is automatically the most recent due to direction
					}
				}
			}
		}

		return result;
	}

	public Long getStateTime(State state) {
		return stateTimes.get(state);
	}

	private void clearExpiredStates(long currentTime) {
		synchronized (states) {
			while (!states.isEmpty()) {
				State state = states.get(0);
				long stateTime = stateTimes.get(state);
				if (stateTime < currentTime - storageDuration) {
					states.remove(0);
					stateTimes.remove(state);
				} else {
					break;
				}
			}
		}
	}

	public void deleteAfter(long time) {
		synchronized (states) {
			int i = 0;
			while (i < states.size()) {
				State state = states.get(i);
				long stateTime = stateTimes.get(state);
				if (stateTime > time) {
					states.remove(i);
					stateTimes.remove(state);
				} else {
					i++;
				}
			}
		}
	}

}
