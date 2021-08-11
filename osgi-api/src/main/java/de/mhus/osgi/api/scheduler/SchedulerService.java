/**
 * Copyright (C) 2018 Mike Hummel (mh@mhus.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.osgi.api.scheduler;

import de.mhus.lib.core.ITimerTask;
import de.mhus.lib.core.schedule.SchedulerJob;
import de.mhus.lib.core.schedule.TimerTaskInterceptor;

/**
 * Create this interface as a component and the timer will schedule the observer as a timer task.
 *
 * <p>Deprecated:
 *
 * <p>Use the parameter 'interval' to define the default interval as time or cron job definition.
 * interval=15min - every 15 minutes from start (do not insert spaces!) interval=1min,1h - first
 * execution after 1 minute then every hour (do not insert spaces!) interval=1,15,30,45 * * * * *
 * Every 15 minutes exact every quarter hour
 *
 * <p>Use annotation '@Interval' and use SchedulerServiceAdapter to set a interval.
 *
 * @author mikehummel
 */
public interface SchedulerService extends ITimerTask {

    /**
     * Overwrite interval defined in the component parameters. Return null if you don't need to
     * define a customized interval.
     *
     * @return Interval
     */
    String getInterval();

    SchedulerJob getWrappedJob();

    /**
     * Return a intercepter to handle this
     *
     * @return intercepter
     */
    TimerTaskInterceptor getInterceptor();

    /**
     * Return the user name or null for guest to execute with this users privileges.
     *
     * @return The user name to run with.
     */
    String getUsername();
}
