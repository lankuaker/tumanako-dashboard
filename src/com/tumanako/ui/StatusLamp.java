package com.tumanako.ui;

import com.tumanako.dash.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
               app:off_bitmap="@drawable/lamp_off"  />
 
 * In the above example, the lamp is a 32x32 bitmap. Bitmaps called 
 * 'lamp_on.png' and 'lamp_off.png' should be located in the appropriate res\drawable 
 * folder.
 * 
 * Note that there must also be a values\attrs.xml file which defines the custom 
 * attributes on_bitmap and off_bitmap. It should look like this: 
 *
    <?xml version="1.0" encoding="utf-8"?>
     <resources>
       <declare-styleable name="StatusLamp">
          <attr name="on_bitmap" format="string" />
          <attr name="off_bitmap" format="string" />
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



public class StatusLamp extends ImageView
  {

  private boolean lampState = false;  // true = Lamp On; false = Lamp Off. 

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

    // Default to OFF: 
    turnOff();
    
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
    // If they can't be found, a default is used. (probably means we made a type in the XML...)
    int idBitmapLampOn  = a.getResourceId(R.styleable.StatusLamp_on_bitmap, R.drawable.greenglobe_on);
    int idBitmapLampOff = a.getResourceId(R.styleable.StatusLamp_off_bitmap, R.drawable.greenglobe_off);

    // Recycle the TypedArray: 
    a.recycle();
    
    // Load the bitmaps for the lamp: 
    bitmapLampOn   = BitmapFactory.decodeResource( getResources(), idBitmapLampOn  );
    bitmapLampOff  = BitmapFactory.decodeResource( getResources(), idBitmapLampOff );
    
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
  
  
  
  
  
  /****** Return current lamp state: ***********
   * @return true = Lamp is on; false = lamp is off.
   *********************************************/
  public boolean getState()
    {  return lampState;  }

  
  
  
  }
