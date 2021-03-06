package com.tumanako.dash;

import android.os.Bundle;

/**
Tumanako - Electric Vehicle and Motor control software <p>

Copyright (C) 2014 Jeremy Cole-Baker <jeremy@rhtech.co.nz> <p>

This file is part of Tumanako Dashboard. <p>

Tumanako is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version. <p>

Tumanako is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details. <p>

You should have received a copy of the GNU Lesser General Public License
along with Tumanako.  If not, see <http://www.gnu.org/licenses/>. <p>

@author Jeremy Cole-Baker / Riverhead Technology

*/



public interface IDashMessages
  {
 
  /**** Message ID Constants: ***********************************************
   * These integer IDs are used as the 'value' of the DASHMESSAGE_MESSAGE
   * field in an intent. They idenitify the source of the message.
   * We define a global list here, and other classes may add their own by 
   * using one of these as a base and adding an integer value.     
   **************************************************************************/
  public static final int DASHMESSAGE_UNSPECIFIED  =    0;
  public static final int NMEA_GPS_SENSOR_ID       =  300;
  public static final int NMEA_PROCESSOR_ID        =  400;
  public static final int VEHICLE_DATA_ID          =  500;
  public static final int CHARGE_NODE_ID           = 1000;
  public static final int CHARGE_HTTPCON_ID        = 1100;

  
  public abstract void messageReceived(String action, Integer intData, Float floatData, String stringData, Bundle bundleData );
    // Classes which wish to use the DashMessages class should impliment the above
    // method, which will receive filtered intents. 
  
  }
