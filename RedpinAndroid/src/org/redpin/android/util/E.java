/*
Copyright (c) 2009 nullwire aps

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

Contributors: 
Mads Kristiansen, mads.kristiansen@nullwire.com
Glen Humphrey
Evan Charlton
Peter Hewitt
*/

package org.redpin.android.util;

public class E {
    // This must be set by the application - it used to automatically
    // transmit exceptions to the trace server
    public static String FILES_PATH                                 = null;
    public static String APP_VERSION                                = "unknown";
    public static String APP_PACKAGE                                = "unknown";
public static String PHONE_MODEL                            = "unknown";
public static String ANDROID_VERSION            = "unknown";
// Where are the stack traces posted?
    public static String URL                                                = "http://redpin.org/android/st.php";
    public static String TraceVersion                               = "0.3.0";
}
