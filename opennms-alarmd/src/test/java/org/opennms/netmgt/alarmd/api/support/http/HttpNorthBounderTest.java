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

package org.opennms.netmgt.alarmd.api.support.http;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennms.core.test.OpenNMSJUnit4ClassRunner;
import org.opennms.core.test.annotations.JUnitHttpServer;
import org.opennms.netmgt.alarmd.api.Alarm;
import org.opennms.netmgt.alarmd.api.support.NorthboundAlarm;
import org.opennms.netmgt.alarmd.api.support.http.HttpNorthbounderConfig.HttpMethod;
import org.opennms.netmgt.dao.db.JUnitConfigurationEnvironment;
import org.opennms.netmgt.model.OnmsAlarm;
import org.springframework.test.context.ContextConfiguration;

/**
 * Tests the HTTP North Bound Interface
 * FIXME: This is far from completed
 * 
 * @author <a mailto:brozow@opennms.org>Matt Brozowski</a>
 * @author <a mailto:david@opennms.org>David Hustace</a>
 */
@RunWith(OpenNMSJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:META-INF/opennms/emptyContext.xml")
@JUnitConfigurationEnvironment
@JUnitHttpServer(port=10342)
public class HttpNorthBounderTest {

    @Test
    public void testForwardAlarms() throws InterruptedException {
        
        HttpNorthbounder nb = new HttpNorthbounder();
        HttpNorthbounderConfig config = new HttpNorthbounderConfig("localhost");
        config.setMethod(HttpMethod.POST);
        config.setPath("/jms/post");
        config.setPort(Integer.valueOf(10342));
        
        nb.setConfig(config);
        
        OnmsAlarm alarm = new OnmsAlarm();
        alarm.setId(1);
        alarm.setUei("uei.opennms.org/test/httpNorthBounder");
        
        Alarm a = new NorthboundAlarm(alarm);
//        List<Alarm> alarms = Arrays.asList(a);
//        
//        nb.forwardAlarms(alarms);
        
        nb.onAlarm(a);
        
        Thread.sleep(10000);
        
    }

}
