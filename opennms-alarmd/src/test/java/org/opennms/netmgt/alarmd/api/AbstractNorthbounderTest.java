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

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.opennms.netmgt.alarmd.api.AbstractNorthbounder;
import org.opennms.netmgt.alarmd.api.Alarm;
import org.opennms.netmgt.alarmd.api.support.NorthboundAlarm;
import org.opennms.netmgt.alarmd.api.support.NorthbounderException;
import org.opennms.netmgt.model.OnmsAlarm;

/**
 * Tests NBI Supporting abstract class
 * 
 * @author <a mailto:brozow@opennms.org>Matt Brozowski</a>
 * @author <a mailto:david@opennms.org>David Hustace</a>
 */
public class AbstractNorthbounderTest {
    
    public static class TestNorthbounder extends AbstractNorthbounder {

        private List<Alarm> m_alarms;
        private boolean m_accepting;
        
        private CountDownLatch m_forwardAlarmsCalled = new CountDownLatch(1);
        
        private CountDownLatch m_acceptsCalled = new CountDownLatch(1);

        protected TestNorthbounder() {
            super("TestNorthbounder");
        }

        @Override
        public void fetch(String query) throws NorthbounderException {
        }

        @Override
        public void sync(Alarm alarm) throws NorthbounderException {
        }

        @Override
        public void syncAll() throws NorthbounderException {
        }

        @Override
        protected boolean accepts(Alarm alarm) {
            m_acceptsCalled.countDown();
            return m_accepting;
        }

        @Override
        public void forwardAlarms(List<Alarm> alarms)
                throws NorthbounderException {
            
            m_alarms = alarms;
            m_forwardAlarmsCalled.countDown();
            
        }
        
        public void waitForForwardToBeCalled(long waitTime) throws InterruptedException {
            m_forwardAlarmsCalled.await(waitTime, TimeUnit.MILLISECONDS);
        }

        public void waitForAcceptsToBeCalled(long waitTime) throws InterruptedException {
            m_acceptsCalled.await(waitTime, TimeUnit.MILLISECONDS);
        }

        
        public List<Alarm> getAlarms() {
            return m_alarms;
        }

        public boolean isAccepting() {
            return m_accepting;
        }

        public void setAccepting(boolean accepting) {
            m_accepting = accepting;
        }
        
    }

    @Test
    public void testAlarmForwarding() throws InterruptedException {
        
        TestNorthbounder tnb = new TestNorthbounder();
        tnb.setAccepting(true);
        
        tnb.init();
        
        Alarm a = createNorthboundAlarm(1);
        
        tnb.onAlarm(a);

        tnb.waitForAcceptsToBeCalled(2000);
        tnb.waitForForwardToBeCalled(2000);

        assertNotNull(tnb.getAlarms());
        assertTrue(tnb.getAlarms().contains(a));

    }
    
    @Test
    public void testAlarmNotAccepted() throws InterruptedException {
        
        TestNorthbounder tnb = new TestNorthbounder();
        tnb.setAccepting(false);
        
        tnb.init();
        
        tnb.onAlarm(createNorthboundAlarm(1));

        tnb.waitForAcceptsToBeCalled(2000);
        
        Thread.sleep(100);

        assertNull(tnb.getAlarms());

    }

    @Test
    public void testAlarmForwardingWithNagles() throws InterruptedException {
        
        TestNorthbounder tnb = new TestNorthbounder();
        tnb.setAccepting(true);
        
        tnb.setNaglesDelay(500);
        tnb.init();
        
        Alarm a1 = createNorthboundAlarm(1);
        Alarm a2 = createNorthboundAlarm(2);
        Alarm a3 = createNorthboundAlarm(3);
        
        tnb.onAlarm(a1);
        Thread.sleep(100);
        tnb.onAlarm(a2);
        Thread.sleep(100);
        tnb.onAlarm(a3);

        tnb.waitForAcceptsToBeCalled(2000);
        tnb.waitForForwardToBeCalled(2000);

        assertNotNull(tnb.getAlarms());
        
        assertEquals(3, tnb.getAlarms().size());
        
        assertTrue(tnb.getAlarms().contains(a1));
        assertTrue(tnb.getAlarms().contains(a2));
        assertTrue(tnb.getAlarms().contains(a3));

    }
    
    private Alarm createNorthboundAlarm(int alarmid) {
        OnmsAlarm alarm = new OnmsAlarm();
        alarm.setId(alarmid);
        alarm.setUei("uei.opennms.org/test/httpNorthBounder");
        
        Alarm a = new NorthboundAlarm(alarm);
        return a;
    }
    


}
