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

import com.tumanako.dash.RingBuffer;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.view.View;


/*************************************************************************************
 * 
 * Scrolling Chart:
 * 
 * Creates a chart which scrolls horizontally as new data are added! 
 * 
 * This is far from complete! 
 *
 * To Use: Place something like this in the XML layout file, preferably inside a
 * horizontal LinearLayout: 
 * 
       <com.tumanako.ui.ScrollChart android:id="@+id/demoScrollChart"
                 android:layout_width="fill_parent"
                 android:layout_height="wrap_content"
                 android:layout_weight="0" />

 *
 * To access from code, use something like this: 
 * 
 *    private ScrollChart demoChart;
 *    demoChart = (ScrollChart) findViewById(R.id.demoScrollChart);
 *    demoChart.AddPoint( 10.5, 12.5 );
 *    
 * @author Jeremy Cole-Baker / Riverhead Technology
 *
 ************************************************************************************/



public class ScrollChart extends View
  {

  private ShapeDrawable chartBorder;
  private ShapeDrawable chartLine;
  private ShapeDrawable chartFill;
  private ShapeDrawable chartAvgLine;

  private int drawingWidth = 0;
  private int drawingHeight = 0;
  private int drawingTop = 0;
  private int drawingLeft = 0;
  
  private float dataAverage = 0;
  
  private static final int MARGIN = 0;
  private static final int NUMBER_POINTS = 60;
  private static final float MAX_SCALE = 20;
  
  private RingBuffer dataBuffer = new RingBuffer(NUMBER_POINTS+1, 1, false);
  
  private float[] xCoord = new float[NUMBER_POINTS];
  private float[] yCoord = new float[NUMBER_POINTS];  
  
  // ********** Constructor: ***************************
  public ScrollChart(Context context, AttributeSet atttibutes)
    {
    super(context, atttibutes);
    // Set up the data buffer (initially filled with 1):
    dataBuffer.PreFill(new float[] {10f});
    }
  
  
  
  // ***** Add a new point to the chart. This causes the chart to scroll along. *****
  public void AddPoint(float thisValue, float thisAverage)
    {
    // Note: The value we will store in the internal buffer is converted here to a
    // fraction of the distance down from the top of the chart. 
    // This makes it much faster later when we have to compute the coordinates 
    // of points on the chart. 
    // The dataAverage is also converted to a chart fraction, and stored. 
    dataAverage = 1f - (thisAverage/MAX_SCALE);
    dataBuffer.AddPoint(new float[] {  (1f - (thisValue/MAX_SCALE))  } );
    invalidate();  // Signal the OS that we need to be redrawn!
    }
  
  
 public void reset()
   {  
   // Resets the scroll chart... 
   // Currently, this has no effect!
   }
  
  
  
  private Path MakeChartFill()
    {
    // Convert the data in the data buffer to coordinates for the chart:
    int n;
    int lastPointIndex = NUMBER_POINTS - 1;
    for (n=0; n<NUMBER_POINTS; n++)
         yCoord[n] = (float)drawingTop + ( (Float)dataBuffer.GetPoint(lastPointIndex-n)[0] * (float)drawingHeight );
    // Generate a filled path object to represent the path: 
    Path thisPath = new Path();  // Create a blank path.
    thisPath.setFillType(Path.FillType.EVEN_ODD);
    thisPath.moveTo(xCoord[0], yCoord[0]);                            // ...First point!
    for (n=1; n<NUMBER_POINTS; n++)  thisPath.lineTo(xCoord[n], yCoord[n]);      //    Loop through points and add each. 
    thisPath.lineTo((float)drawingWidth, (float)drawingHeight);       //  ]
    thisPath.lineTo(0, (float)drawingHeight);                         //  ]  Finish the filled shape.
    thisPath.lineTo(xCoord[0], yCoord[0]);                            //  ]
    return thisPath;
    }


  private Path MakeChartLine()
    {
    // NOTE: Must call MakeChartFill (Above) FIRST to set up yCoord array!!
    int n;
    // Generate a path object to represent the line for the chart: 
    Path thisPath = new Path();  // Create a blank path.
    thisPath.setFillType(Path.FillType.EVEN_ODD);
    thisPath.moveTo(xCoord[0], yCoord[0]);                                       // ...First point!
    for (n=1; n<NUMBER_POINTS; n++)  thisPath.lineTo(xCoord[n], yCoord[n]);      //    Loop through points and add each. 
    return thisPath;
    }
  
  
  
  private Path MakeAvgLine()
    {
    // Draws an 'Average' line across the chart:
    float xStart = (float)drawingLeft;
    float xEnd = xStart + (float)drawingWidth; 
    float y = (float)drawingTop + ( dataAverage * (float)drawingHeight ); 
    Path thisPath = new Path();  // Create a blank path.
    thisPath.setFillType(Path.FillType.EVEN_ODD);
    thisPath.moveTo(xStart, y);             // ...First point!
    thisPath.lineTo(xEnd,   y);             // ...Second point!                
    return thisPath;
    }
  
  
  
  
  
  // ********************** onMeasure: ************************************
  // We're being told how big we should be. Compute our dimensions: 
  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
    {
    // The widthMeasureSpec and heightMeasureSpec contain width and height specifications packed into an integer. 
    // We need to carefully check and decode these. We'll use a couple of helper functions to do this: 
    setMeasuredDimension(measureThis(widthMeasureSpec),  measureThis(heightMeasureSpec));
    }

  
  
  // **** Check a supplied width or hight spec, and decide whether to override it: 
  private int measureThis(int measureSpec) 
    {
    int result = 0;
    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec);
    if (specMode == MeasureSpec.EXACTLY) 
      {
      // We were told how big to be, so we'll go with that!
      result = specSize;
      }
    else 
      {
      // We might have been given an indication of maximum size: 
      result = 320;   // We'll try 320 pixels. 
      if (specMode == MeasureSpec.AT_MOST) result = specSize;
         // Respect AT_MOST value if that was what is called for by measureSpec
      }
    return result;
    }


  
  
  
  
  @Override 
  protected void onDraw(Canvas canvas)
    {
    super.onDraw(canvas);
    Paint paintBorder;
   
    // Determine the chart dimensions, if we haven't already done so: 
    if (drawingWidth == 0)
      {
      // **** Get actual layout parameters: ****
      drawingWidth = this.getWidth() - MARGIN - MARGIN;
      drawingHeight = this.getHeight() - MARGIN - MARGIN;
      drawingTop = this.getTop();
      drawingLeft = this.getLeft();
      // **** Set up chart border: **** 
      chartBorder = new ShapeDrawable(new RectShape());
      paintBorder = chartBorder.getPaint();
      paintBorder.setStyle(Paint.Style.STROKE);
      paintBorder.setStrokeWidth(5);
      paintBorder.setColor(0xFFF0F0F0);
      chartBorder.setBounds(MARGIN,MARGIN,drawingWidth,drawingHeight);
      // *** Compute X coordinates for the chart points (these don't change): *** 
      for (int n=0; n<NUMBER_POINTS; n++)  xCoord[n] = (float)drawingLeft + ( ((float)n/(NUMBER_POINTS-1)) * (float)drawingWidth);
      }

    // *** Set up the chart: ****
    Path chartFillPath = MakeChartFill();
    Path chartLinePath = MakeChartLine();    
    Path chartAvgPath = MakeAvgLine();

    Paint paintChartFill;
    Paint paintChartLine;
    Paint paintAvgLine;

    chartFill = new ShapeDrawable(new PathShape(chartFillPath,(float)drawingWidth,(float)drawingHeight) );
    chartFill.setBounds(MARGIN,MARGIN,drawingWidth,drawingHeight);
    paintChartFill = chartFill.getPaint();
    paintChartFill.setStyle(Paint.Style.FILL_AND_STROKE);
    paintChartFill.setStrokeWidth(1);
    paintChartFill.setColor(0xFF2CA6F0);
    
    chartLine = new ShapeDrawable(new PathShape(chartLinePath,(float)drawingWidth,(float)drawingHeight) );
    chartLine.setBounds(MARGIN,MARGIN,drawingWidth,drawingHeight);
    paintChartLine = chartLine.getPaint();
    paintChartLine.setStyle(Paint.Style.STROKE);
    paintChartLine.setStrokeWidth(6);
    paintChartLine.setColor(0xFFF0F0F0);

    chartAvgLine = new ShapeDrawable(new PathShape(chartAvgPath,(float)drawingWidth,(float)drawingHeight) );
    chartAvgLine.setBounds(MARGIN,MARGIN,drawingWidth,drawingHeight);
    paintAvgLine = chartAvgLine.getPaint();
    paintAvgLine.setStyle(Paint.Style.STROKE);
    paintAvgLine.setStrokeWidth(8);
    paintAvgLine.setColor(0xA0FF0000);

    chartFill.draw(canvas);
    chartLine.draw(canvas);
    chartAvgLine.draw(canvas);
    chartBorder.draw(canvas);
    
    //     invalidate();
    }
  
  }  // [Class]
  

