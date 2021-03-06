package com.tumanako.ui;

/************************************************************************************
Tumanako - Electric Vehicle and Motor control software

Copyright (C) 2012 Jeremy Cole-Baker <jeremy@rhtech.co.nz>

This file is part of Tumanako Dashboard.

Tumanako is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Tumanako is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Tumanako.  If not, see <http://www.gnu.org/licenses/>.

*************************************************************************************/

import com.tumanako.dash.DashMessages;
import com.tumanako.dash.IDashMessages;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.ImageView;



/*************************************************************************************
 * 
 * Status Lamp:
 * 
 *  This class provides a simple 'Lamp' UI element. The lamp has two states 
 *  defined as 'On' and 'Off', each of which is displayed with a bitmap image in the 
 *  UI. 
 *  
 *  The states don't have to be literally On or Off - it could be any binary indicator,
 *  e.g. 'Connected' / 'Disconnected', 'On Fire' / 'Not On Fire', etc. 
 *
 * To use, suitable XML should be added to the layout file, e.g.:
 * 
      <com.tumanako.ui.StatusLamp  
               android:id="@+id/demoStatusLamp"  
               android:layout_width="32dp"  
               android:layout_height="32dp"  
               app:on_bitmap="@drawable/lamp_on"
               app:off_bitmap="@drawable/lamp_off"
               app:initial_status="true"         />
 
 * In the above example, the lamp is a 32x32 bitmap. Bitmaps called 
 * 'lamp_on.png' and 'lamp_off.png' should be located in the appropriate res\drawable 
 * folder.
 * 
 * Note that there must also be a values\attrs.xml file which defines the custom 
 * attributes:
 *   on_bitmap      - Bitmap resource to display when lamp is 'On'
 *   off_bitmap     - Bitmap resource to display when lamp is 'Off'
 *   initial_status - Should the lamp be on or off initially? ("on" or "off").  
 *   default_status - What status should the lamp revert to on reset?
 *   It should look like this: 
 *
    <?xml version="1.0" encoding="utf-8"?>
    
     <resources>
       <declare-styleable name="App">  
         <attr name="update_action"  format="string" />
       </declare-styleable>


       <declare-styleable name="StatusLamp">
          <attr name="on_bitmap" format="string" />
          <attr name="off_bitmap" format="string" />
          <attr name="initial_status" format="string" />
          <attr name="default_status" format="string" />
      </declare-styleable>    
    </resources>  

 *
 * To access the lamp from code and turn it on or off, use something like this:
 *  
 *   private StatusLamp demoStatusLamp;
 *   demoStatusLamp = (StatusLamp)   findViewById(R.id.demoStatusLamp);
 *   demoStatusLamp.turnOn();
 *   
 * Easy as that!
 * 
 * Other public methods: 
 *   turnOff()
 *   getStatus()
 *
 * @author Jeremy Cole-Baker / Riverhead Technology
 *
 ************************************************************************************/



public class StatusLamp extends ImageView implements IDashMessages
  {
  private String updateAction;
  private DashMessages dashMessages;

  private boolean lampState = false;  // true = Lamp On; false = Lamp Off. 

  private boolean defaultState = false; // Will be set to this when the UI is reset. 
  
  // Bitmaps to represent lamp state (on / off): 
  private Bitmap bitmapLampOn;
  private Bitmap bitmapLampOff;
  
  
  
  /**** Constructor: ***************************************
   * Called when this view is created, probably from inflating
   * an XML layout file.  Context and attributes are passed on
   * to super class constructor for basic creation of the view,
   * then custom components are added. 
   *  
   * @param context
   * @param attrs
   * 
   *********************************************************/
  public StatusLamp(Context context, AttributeSet attrs)
    {
    // Call the super class constructor to create a basic ImageView:
    super(context, attrs);

    // Get attributes from XML file:
    // This method also loads the 'On' and 'Off' bitmaps for the lamp.  
    getCustomAttributes(attrs);

    // Set up a DashMessages class to recieve intents:
    String [] messageFilters = { updateAction, UIActivity.UI_RESET };
    dashMessages = new DashMessages( context, this, messageFilters );
    
    // Set Lamp State:
    if (lampState) turnOn();
    else           turnOff();
    
    }

  
  
  /*********** Extract custom attributes: **************************
   * Given a set of attributes from the XML layout file, extract
   * the custom attributes specific to the StatusLamp: 
   * @param attrs - Attributes passed in from the XML parser 
   *****************************************************************/
  private void getCustomAttributes(AttributeSet attrs)
    {
    TypedArray a = getContext().obtainStyledAttributes( attrs, R.styleable.StatusLamp );
    // Look up the resource ID of the bitmaps named for 'off' and 'on' in the XML. 
    // If they can't be found, a default is used. (probably means we made a typo in the XML...)
    int idBitmapLampOn  = a.getResourceId(R.styleable.StatusLamp_on_bitmap, R.drawable.greenglobe_on);
    int idBitmapLampOff = a.getResourceId(R.styleable.StatusLamp_off_bitmap, R.drawable.greenglobe_off);
    String initialStatus = a.getString(R.styleable.StatusLamp_initial_status);
    String defaultStatus = a.getString(R.styleable.StatusLamp_default_status);
    // Recycle the TypedArray: 
    a.recycle();

    // Load the bitmaps for the lamp: 
    bitmapLampOn   = BitmapFactory.decodeResource( getResources(), idBitmapLampOn  );
    bitmapLampOff  = BitmapFactory.decodeResource( getResources(), idBitmapLampOff );

    // Set initial status: (note that default is Off):  
    if (initialStatus != null) 
      if (initialStatus.equals("on")) lampState = true;  // Turn on the lamp if initial_status is "on"!

    if (defaultStatus != null)
      if (defaultStatus.equals("on")) defaultState = true;  

    // Get the "update" action: 
    a = getContext().obtainStyledAttributes( attrs, R.styleable.App);
    updateAction = a.getString(R.styleable.App_update_action);
    a.recycle();    // Recycle the TypedArray.
    if (updateAction == null) updateAction = UIActivity.UI_NOTHING;  
    // ...Default UI update action if no action specified. This indicates that no update action should be taken.
    //    UIActivity.UI_NOTHING should not be used in any actual intent. 

    }

  
  
  
  /****** Turn lamp ON: ***********/
  public void turnOn()
    {
    setImageBitmap(bitmapLampOn);
    lampState = true;
    }

  
  
  /****** Turn lamp OFF: ***********/
  public void turnOff()
    {
    setImageBitmap(bitmapLampOff);    
    lampState = false;
    }
  
  
  /****** Reset to default state: ***********/
  public void reset()
    {
    if (defaultState) turnOn();
    else              turnOff();
    }
  
  
  
  /****** Return current lamp state: ***********
   * @return true = Lamp is on; false = lamp is off.
   *********************************************/
  public boolean getState()
    {  return lampState;  }



  public void messageReceived(String action, Integer intData, Float floatData, String stringData, Bundle bundleData)
    {
    if (action.equals(UIActivity.UI_RESET))
      {
      // RESET intent has been received: 
      reset();
      }
    else
      {
      // UPDATE intent: Set guage value to the float contained in the floatData parameter:
      if (null != floatData)
        {
        if (floatData == 1f) turnOn();
        else                 turnOff();
        }
      }
    }
  
  
  
 }  // Class
