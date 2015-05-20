Vendormac
=========

 This is simple utility for generating Vendor Mac Map such as Propery file, xml file and apple enum elements.

Precondition
------------

 1. Source file from http://standards-oui.ieee.org/oui.txt.
 2. Save as oui.txt(Default source file name)

> NOTE: Periodically Need to update http://standards-oui.ieee.org/oui.txt 


Usage
-----
```
cmd> java -jar vendormac.jar
cmd> java -jar vendormac.jar <source file>
```

*Example 1. Use URL internally. - http://standards-oui.ieee.org/oui.txt.*
```
cmd> java -jar vendormac.jar
```

*Example 2. Use database file oui.txt.*
```
cmd> java -jar vendormac.jar oui.txt
```


Output files
------------

* apple-enum-generated.txt
	- enum elements for Apple Mac
* vendorMacs-generated.prop
	- '=' Seperated file format
* vendorMacs-generated.xml
	- xml file format

###applee-enum-generated.txt

*File contents*
```
$000393("000393", "Apple"),
$000502("000502", "Apple"),
$000A27("000A27", "Apple"),
$000A95("000A95", "Apple"),
$000D93("000D93", "Apple"),
$0010FA("0010FA", "Apple"),
$001124("001124", "Apple"),
$001451("001451", "Apple"),
$0016CB("0016CB", "Apple"),
$0017F2("0017F2", "Apple"),
(snip)...
```


*Usage example*
```java
public enum AppleMacs {
    // Here, Copy and Paste from applee-enum-generated.txt
    $000393("000393", "Apple"),
    $000502("000502", "Apple"),
    $000A27("000A27", "Apple"),
    (snip)...
     ;
    
    private static final Map<String, AppleMacs> kMap;
    static {
        kMap = new HashMap<String, AppleMacs>();
        
        for (AppleMacs k : AppleMacs.values()) {
            kMap.put(k.prefix, k);
        }
    }
    
    public static AppleMacs getEnum(String prefix) {
        return kMap.get(prefix);
    }
    
    public final String prefix;
    public final String vendor;
    AppleMacs(String prefix, String vendor) {
        this.prefix = prefix;
        this.vendor = vendor;
    }
    
    @Override
    public String toString() {
        return "prefix(" + prefix + ") , vendor(" + vendor + ")";
    }
}
```

###vendorMacs-generated.prop
'=' Seperated file.

*File contents*
```
000000=XEROX CORPORATION
000001=XEROX CORPORATION
000002=XEROX CORPORATION
000003=XEROX CORPORATION
000004=XEROX CORPORATION
000005=XEROX CORPORATION
000006=XEROX CORPORATION 
(snip)...
```

*Usage example*
```java
    Properties macs = new Properties();

   try {
        FileInputStream vendorMacs = new FileInputStream("vendorMacs-generated.prop");
        if (vendorMacs != null) {
            try {
                macs.load(vendorMacs);
            } catch (IOException e) {
                e.printStackTrace();
            }

            vendorMacs.close();
            vendorMacs = null;
        }
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    } 
```

###vendorMacs-generated.xml
xml file format.

*File content*
```
<?xml version='1.0' standalone='yes' ?>
<!--癤? Generated: Tue, 07 Apr 2015 05:00:04 -0400-->
<MacAddressVendorMappings xmlns="http://standards-oui.ieee.org/oui.txt">
  <VendorMapping mac_prefix="000000" vendor_name="XEROX CORPORATION" />
  <VendorMapping mac_prefix="000001" vendor_name="XEROX CORPORATION" />
  <VendorMapping mac_prefix="000002" vendor_name="XEROX CORPORATION" />
  <VendorMapping mac_prefix="000003" vendor_name="XEROX CORPORATION" />
  (snip)...
  <VendorMapping mac_prefix="FCFFAA" vendor_name="IEEE REGISTRATION AUTHORITY  - Please see MAL public listing for more information." />
</MacAddressVendorMappings>  
```

*Usage example*

```java
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class VendorMacsMap {    
    private static final String DEFAULT_PATH = "/system/etc/vendorMacs-generated.xml";
    private static final Map<String, String> kVedorMacsMaps = new HashMap<String, String>();
    
    private static final String PATH;
    
    static {
        PATH = DEFAULT_PATH;
        
        try {
            int eventType = 0;
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(PATH)));

            xpp.setInput(br);
            while ((eventType = xpp.next()) != XmlPullParser.END_DOCUMENT) {
                
                switch (eventType) {
                case XmlPullParser.START_TAG:
                    if ("VendorMapping".equals(xpp.getName())) {
                        String mac = xpp.getAttributeValue(null, "mac_prefix");
                        String vendor = xpp.getAttributeValue(null, "vendor_name");
                        kVedorMacsMaps.put(mac, vendor);
                    }
                    break;
                }
            }
        } catch (XmlPullParserException e) {
            ALog.e(TAG, "XmlPullParserException exception!");
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            ALog.e(TAG, "File Not Found: " + PATH);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    static public String getVendorName(String mac) {
        
        // start of 6 chars in address
        String mac_prefix = (mac.contains(":") == true) ? mac.substring(0, 8)
                : mac.substring(0, 6);
        StringTokenizer st = new StringTokenizer(mac_prefix, ":");
        String key = "";
        
        while(st.hasMoreTokens())
            key += st.nextToken();
        
        return kVedorMacsMaps.get(key);
    }
}
```