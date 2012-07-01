package br.ufla.dcc.mac.backbone;

import java.util.Random;

public class Schedule {

	private static int __id = 0;

	private final int _id;
	private final double _delay;

	private final double _creationTime;

	private final Random _random = new Random();

	public Schedule(double creationTime, double delayToSleep) {
		_id = __id++;
		_creationTime = creationTime;
		_delay = _random.nextDouble() * delayToSleep;
	}

	public double getCreationTime() {
		return _creationTime;
	}

	public double getDelay() {
		return _delay;
	}

	public int getId() {
		return _id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + _id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Schedule other = (Schedule) obj;
		if (_id != other._id) {
			return false;
		}
		return true;
	}

	public double getDelay(double currentTime) {
		double delayCorrection = currentTime - _creationTime;
		delayCorrection %= _delay;
		return _delay - delayCorrection;
	}

}
