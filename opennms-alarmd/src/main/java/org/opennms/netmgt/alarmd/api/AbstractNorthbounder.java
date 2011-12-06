/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2009-2011 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2011 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.netmgt.alarmd.api;

import java.util.List;

import org.opennms.netmgt.alarmd.AlarmQueue;
import org.opennms.netmgt.alarmd.api.support.NorthbounderException;


/**
 * AbstractNorthBounder
 * 
 * The purpose of this class is manage the queue of alarms that need to be forward and receive queries to/from a Southbound Interface.
 * 
 * It passes Alarms on to the forwardAlarms method implemented by base classes in batches as they are 
 * added to the queue.  The forwardAlarms method does the actual work of sending them to the Southbound Interface.
 * 
 * preserve, accept and discard are called to add the Alarms to the queue as appropriate.  
 * 
 * @author <a mailto:david@opennms.org>David Hustace</a>
 */

public abstract class AbstractNorthbounder implements Northbounder, Runnable {
    
    private String m_name;

    /**
     * If you override this, be sure to call super.init()
     */
    @Override
    public void init() throws NorthbounderException {
        start();
    }
    
    protected AbstractNorthbounder(String name) {
        setName(name);
    }

    private Thread m_thread;
    private volatile boolean m_stopped = false;

    private AlarmQueue m_queue = new AlarmQueue();
    private long m_retryInterval = 1000;
    
    public void setNaglesDelay (long delay) {
        m_queue.setNaglesDelay(delay);
    }
    
    public void setRetryInterval(int retryInterval) {
        m_retryInterval = retryInterval;
    }
    
    public void setMaxBatchSize(int maxBatchSize) {
        m_queue.setMaxBatchSize(maxBatchSize);
    }
    
    public void setMaxPreservedAlarms(int maxPreservedAlarms) {
        m_queue.setMaxPreservedAlarms(maxPreservedAlarms);
    }
    
    @Override
    public void start() throws NorthbounderException {
        m_stopped = false;
        m_queue.init();
        m_thread = new Thread(this, getName()+"-Thread");
        m_thread.start();
    }
    
    @Override
    public final void onAlarm(Alarm alarm) throws NorthbounderException {
        if (accepts(alarm)) {
            m_queue.accept(alarm);
        }
    };
    
    
    protected abstract boolean accepts(Alarm alarm);

    protected void preserve(Alarm alarm) throws NorthbounderException {
        m_queue.preserve(alarm);
    }

    protected void discard(Alarm alarm) throws NorthbounderException {
        m_queue.discard(alarm);
    }
    
    public void stop() throws NorthbounderException {
        m_stopped = true;
    }
    
    public void run() {
        
        try {

            while(!m_stopped) {

                List<Alarm> alarmsToForward = m_queue.getAlarmsToForward();
                
                try {
                    forwardAlarms(alarmsToForward);
                    m_queue.forwardSuccessful(alarmsToForward);
                } catch (Exception e) {
                    m_queue.forwardFailed(alarmsToForward);
                    if (!m_stopped) {
                        // a failure occurred so sleep a moment and try again
                        Thread.sleep(m_retryInterval);
                    }
                }
            
            }
        
        } catch (InterruptedException e) {
            // thread interrupted so complete it
        }
        
    }
    
    public abstract void forwardAlarms(List<Alarm> alarms) throws NorthbounderException;

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

}
