/**
 * Copyright 2018 Mike Hummel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.osgi.services.heartbeat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MTimerTask;
import de.mhus.lib.core.base.service.TimerFactory;
import de.mhus.lib.core.base.service.TimerIfc;
import de.mhus.lib.karaf.MOsgi.Service;
import de.mhus.lib.karaf.jms.JmsDataSource;
import de.mhus.lib.karaf.jms.JmsManagerService;
import de.mhus.lib.karaf.jms.JmsUtil;

@Component(provide=HeartbeatAdmin.class,immediate=true,name="de.mhus.lib.karaf.jms.heartbeat.HeartbeatAdmin")
public class HeartbeatAdminImpl extends MLog implements HeartbeatAdmin {

	private TimerIfc timer;
	private HashMap<String, HeartbeatService> services = new HashMap<>();
	private boolean enabled = true;
	private MTimerTask timerTask;

	@Activate
	public void doActivate(ComponentContext ctx) {
	}

	@Reference(service=TimerFactory.class)
	public void setTimerFactory(TimerFactory factory) {
		log().i("create timer");
		timer = factory.getTimer();
		timerTask = new MTimerTask() {
			
			@Override
			public void doit() throws Exception {
				if (!enabled) return;
				sendHeartbeat(null);
			}
		};
		timer.schedule(timerTask, 10000, 60000 * 5);
	}
	
	@Deactivate
	public void doDeactivate(ComponentContext ctx) {
		if (timer != null)
			timer.cancel();
	}

	@Override
	public void sendHeartbeat(String cmd) {
		synchronized (services) {
			JmsManagerService jmsService = JmsUtil.getService();
			if (jmsService == null) return;
			
			List<Service<JmsDataSource>> conList = jmsService.getDataSources();
			HashSet<String> existList = new HashSet<>();
			existList.addAll(services.keySet());
			// work over all existing connections
			for (Service<JmsDataSource> src : conList) {
				String conName = src.getService().getName();
				try {
					HeartbeatService service = services.get(conName);
					if (service == null) {
						log().i("create",conName);
						service = new HeartbeatService();
						service.setName(service.getName() + ":" + conName);
						service.setConnectionName(conName);
						jmsService.addChannel(service);
						services.put(conName, service);
						service.doTimerTask(cmd);
					} else {
						if (!service.getChannel().isClosed())
							service.doTimerTask(cmd);
					}
				} catch (Throwable t) {
					log().d(conName,t);
				}
				existList.remove(conName);
			}
			// remove overlapping
			for (String conName : existList) {
				log().i("remove",conName);
				HeartbeatService service = services.get(conName);
				jmsService.removeChannel(service.getName());
			}
						
		}
	}

	@Override
	public void setEnabled(boolean enable) {
		this.enabled = enable;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public List<HeartbeatService> getServices() {
		return new LinkedList<>(services.values());
	}
}
