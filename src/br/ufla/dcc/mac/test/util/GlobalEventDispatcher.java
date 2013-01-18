package br.ufla.dcc.mac.test.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalEventDispatcher {

	private static GlobalEventDispatcher SINGLETON;

	private final Map<Class<?>, List<GlobalEventListener>> _senderClassToListeners;

	public static GlobalEventDispatcher getDispatcher() {
		if (SINGLETON == null) {
			synchronized (GlobalEventDispatcher.class) {
				if (SINGLETON == null) {
					SINGLETON = new GlobalEventDispatcher();
				}
			}
		}

		return SINGLETON;
	}

	private GlobalEventDispatcher() {
		_senderClassToListeners = new HashMap<Class<?>, List<GlobalEventListener>>();
	}

	public void registerForNotifications(Class<?> senderClass, GlobalEventListener listener) {
		List<GlobalEventListener> listenersList = _senderClassToListeners.get(senderClass);

		if (listenersList == null) {
			listenersList = new ArrayList<GlobalEventListener>();
			_senderClassToListeners.put(senderClass, listenersList);
		}

		listenersList.add(listener);
	}

	public void postNotification(Object sender, Object notification) {
		List<GlobalEventListener> listenersList = _senderClassToListeners.get(sender.getClass());

		if (listenersList != null) {
			for (GlobalEventListener globalEventListener : listenersList) {
				globalEventListener.didReceiveGlobalNotification(sender, notification);
			}
		}
	}
}
