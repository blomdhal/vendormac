package com.sdc.shjung;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.StringTokenizer;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

public class VendorMac {

    private static final String DEFAULT_SOURCE_PATH = "IEEE-oui.txt";
    private static final String DEFAULT_DEST_PATH = "vendorMacs-generated.prop";
    private static final String APPLE_ENUM_PATH = "applee-enum-generated.txt";
    private static final String VENDOR_MACS_PATH = "vendorMacs-generated.xml";
    
    private static final String DELIM = "(base 16)";
    private static final String EQUAL = "=";
    
    private static void printUsage() {
        System.out.println("Usage: java -jar vendormac.jar <source file>");
    }
    
    // '=' serated file
    private static void loadOUIAndWrite(final String source) throws IOException {
        
        BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(source)));
        BufferedWriter bfw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(DEFAULT_DEST_PATH)));
        String tmp = null;
        
        bfw.append("* Reference from: http://standards-oui.ieee.org/oui.txt").append("\n");
        bfw.append("*" + bfr.readLine()).append("\n\n");
        
        
        while ( (tmp = bfr.readLine()) != null) {
            if(tmp.contains(DELIM) == false)
                continue;
            
            StringTokenizer stk = new StringTokenizer(tmp.replace(DELIM, "="), EQUAL);
            String prefix = null;
            String vendor = null;
            
            if (stk.hasMoreTokens())
                prefix = stk.nextToken().trim();
            
            if (stk.hasMoreTokens())
                vendor = stk.nextToken().trim();
            
            // Make '=' separated
            bfw.append(prefix).append("=").append(vendor).append("\n");
        }
        
        bfr.close();
        bfw.close();
        
        System.out.println("Done!!!, File generated : " + DEFAULT_DEST_PATH);
    }
    
    private static void loadOUIAndmakeAppleMacEnumElements(final String source) throws IOException {
        
        BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(source)));
        BufferedWriter bfw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(APPLE_ENUM_PATH)));
        String tmp = null;
        
        bfw.append("* Reference from: http://standards-oui.ieee.org/oui.txt").append("\n");
        bfw.append("* " + bfr.readLine()).append("\n\n");
        
        
        while ( (tmp = bfr.readLine()) != null) {
            if (tmp.contains(DELIM) == false)
                continue;
            
            if (tmp.contains("Apple") == false)
                continue;
            
            StringTokenizer stk = new StringTokenizer(tmp.replace(DELIM, "="), EQUAL);
            String prefix = null;
            String vendor = null;
            
            if (stk.hasMoreTokens())
                prefix = stk.nextToken().trim();
            
            if (stk.hasMoreTokens())
                vendor = stk.nextToken().trim();
            
            // Make enum member
            bfw.append("$").append(prefix).append("(\"").append(prefix).append("\", ");
            bfw.append("\"").append(vendor).append("\"),").append("\n");
        }
        
        bfr.close();
        bfw.close();
        
        System.out.println("Done!!!, File generated : " + APPLE_ENUM_PATH);
        System.out.println("You can Copy and Paste to AppleMacs.java");
    }    

    private static void loadOUIAndmakeXml(final String source) throws IOException, XmlPullParserException {
        
        final String NAMESPACE = "http://standards-oui.ieee.org/oui.txt";
        
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance(
                System.getProperty(XmlPullParserFactory.PROPERTY_NAME), null);
        XmlSerializer serializer = factory.newSerializer();
        
        BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(source)));
        BufferedWriter bfw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(VENDOR_MACS_PATH)));
        String tmp = null;
        
        // xml
        serializer.setOutput(bfw);
        serializer.startDocument(null, Boolean.valueOf(true));
        serializer.ignorableWhitespace("\n");
        serializer.comment(bfr.readLine());
        serializer.ignorableWhitespace("\n");
        
        serializer.setPrefix("", NAMESPACE);
        serializer.startTag(NAMESPACE, "MacAddressVendorMappings");
        //serializer.text("\n");
        
        while ( (tmp = bfr.readLine()) != null) {
            if (tmp.contains(DELIM) == false)
                continue;
            
            StringTokenizer stk = new StringTokenizer(tmp.replace(DELIM, "="), EQUAL);
            String prefix = null;
            String vendor = null;
            
            if (stk.hasMoreTokens())
                prefix = stk.nextToken().trim();
            
            if (stk.hasMoreTokens())
                vendor = stk.nextToken().trim();
            
            // xml attribute
            writeLine(serializer, prefix, vendor);
        }
        
        serializer.endTag(NAMESPACE, "MacAddressVendorMappings");
        serializer.endDocument();
        
        //write xml data into the FileOutputStream
        serializer.flush();
        
        bfr.close();
        bfw.close();
        
        System.out.println("Done!!!, File generated : " + VENDOR_MACS_PATH);
    }
    
    private static void writeLine(XmlSerializer serializer, String prefix, String vendor)
            throws IOException {
        
        // set indentation option
        serializer.setFeature(
                "http://xmlpull.org/v1/doc/features.html#indent-output", true);
        
        serializer.startTag(null, "VendorMapping");
        serializer.attribute(null, "mac_prefix", prefix);
        serializer.attribute(null, "vendor_name", vendor.replaceAll("&", ""));
        serializer.endTag(null, "VendorMapping");
//        serializer.text("\n");
    }
    
    /**
     * @param args
     * @throws IOException 
     * @throws XmlPullParserException 
     */
    public static void main(String[] args) throws IOException, XmlPullParserException {
        
        String src = null;
        
        System.out.println("Generate vendor mac file from RAW IEEE-oui files(http://standards-oui.ieee.org/oui.txt)");
        System.out.println("");
        
        if (args.length != 1) {
            printUsage();
            return;
        }
        
        if ( new File(args[0]).isFile() == true)
            src = args[0];
        else {
            System.out.println(args[0] + " does not exist. use default source file: " + DEFAULT_SOURCE_PATH);
            src = DEFAULT_SOURCE_PATH;
        }
        
        System.out.println("");
        
        loadOUIAndWrite(src);
        loadOUIAndmakeAppleMacEnumElements(src);
        loadOUIAndmakeXml(src);
    }

}
