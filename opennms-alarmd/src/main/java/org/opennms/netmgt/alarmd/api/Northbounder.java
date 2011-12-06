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

import org.opennms.netmgt.alarmd.api.support.NorthbounderException;


/**
 * North bound Interface API
 * 
 * FIXME: these fetch, sync, and sycncAll API calls need some helper functions in the abstraction
 *
 * @author <a href="mailto:david@opennms.org">David Hustace</a>
 * @version $Id: $
 */

public interface Northbounder {

    void init() throws NorthbounderException;
    
    public void start() throws NorthbounderException;
    
    public void onAlarm(Alarm alarm) throws NorthbounderException;
    
    public void stop() throws NorthbounderException;
    
    public void fetch(String query) throws NorthbounderException;
    
    public void sync(Alarm alarm) throws NorthbounderException; 
    
    public void syncAll() throws NorthbounderException;
        
}
