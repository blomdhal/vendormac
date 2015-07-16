package com.sdc.shjung;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.StringTokenizer;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

public class VendorMac {

    private static final boolean D = true;
    
    private static final String DEFAULT_SOURCE_FILE = "oui.txt";
    private static final String DEFAULT_DEST_PATH = "vendorMacs-generated.prop";
    private static final String APPLE_ENUM_PATH = "apple-enum-generated.txt";
    private static final String APPLE_MACS_CLASS = "AppleMacs.java";
    private static final String VENDOR_MACS_PATH = "vendorMacs-generated.xml";
    
    private static final String DELIM = "(base 16)";
    private static final String EQUAL = "=";
    
    private static final int MODE_URL = 0;
    private static final int MODE_FILE = 1;
    
    private static int MODE = MODE_URL;
    
    private static void printUsage() {
        System.out.println("Usage: ");
        System.out.println("1. url mode: java -jar vendormac.jar");
        System.out.println("2. file mode: java -jar vendormac.jar <source file>");
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
    
    static private final String APPLE_ENUM_PREFIX = 
            "package com.yourpackage.aha.constants;\n"
           + "\n"
           + "import java.util.HashMap;\n"
           + "import java.util.Map;\n"
           + "\n"
           + "import android.annotation.SuppressLint;\n"
           + "\n"
           + "/**\n"
           + " * Updated " + new Date().toString() + " , \n"
           + " *  Reference from: http://standards-oui.ieee.org/oui.txt\n"
           + " * \n"
           + " *  Using utils/vendormac.jar, Generate enum elements from http://standards-oui.ieee.org/oui.txt\n"
           + " *  \n"
           + " *  OUI stands for Organizationally Unique Identifier.\n"
           + " *  \n"
           + " * NOTE: Need to Update periodically.\n"
           + " * \n"
           + " * @author blomdhal\n"
           + " * \n"
           + " */\n"
           + "@SuppressLint(\"UseSparseArrays\")\n"
           + "public enum AppleMacs {\n"
           ;


    static private final String APPLE_ENUM_SUFFIX = 
            "    ;\n"
            + "    \n"
            + "    private static final Map<String, AppleMacs> kMap;\n"
            + "    static {\n"
            + "        kMap = new HashMap<String, AppleMacs>();\n"
            + "        \n"
            + "        for (AppleMacs k : AppleMacs.values()) {\n"
            + "            kMap.put(k.prefix, k);\n"
            + "        }\n"
            + "    }\n"
            + "    \n"
            + "    public static AppleMacs getEnum(String prefix) {\n"
            + "        return kMap.get(prefix);\n"
            + "    }\n"
            + "    \n"
            + "    public final String prefix;\n"
            + "    public final String vendor;\n"
            + "    AppleMacs(String prefix, String vendor) {\n"
            + "        this.prefix = prefix;\n"
            + "        this.vendor = vendor;\n"
            + "    }\n"
            + "    \n"
            + "    @Override\n"
            + "    public String toString() {\n"
            + "        return new StringBuilder()\n"
            + "                .append(\"prefix : \").append(prefix)\n"
            + "                .append(\", vendor : \").append(vendor).toString();\n"
            + "    }\n"
            + "} // AppleMacs\n"
            ;
    
    private static void _loadAppleEnumElementMakeAppleEnumClass() throws IOException {
        
        BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(APPLE_ENUM_PATH)));
        BufferedWriter bfw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(APPLE_MACS_CLASS)));
        String tmp = null;
        
        bfw.append(APPLE_ENUM_PREFIX).append("\n");
        
        while ( (tmp = bfr.readLine()) != null) {
            
            if (!tmp.startsWith("$"))
                continue;
            
            // Append enum member
            bfw.append("    ").append(tmp).append("\n");
        }
        
        bfw.append(APPLE_ENUM_SUFFIX).append("\n");
        
        bfr.close();
        bfw.close();
        
        System.out.println("Done!!!, File generated : " + APPLE_MACS_CLASS);
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
        System.out.println("You can Copy and Paste to com.lge.aha.constants.AppleMacs.java");
        
        _loadAppleEnumElementMakeAppleEnumClass();
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
    
    private static void downloadouitxt() throws IOException {

        System.out.println("Downloading oui file... from http://standards-oui.ieee.org/oui.txt");
        
        int size = 0;

        URL url = new URL("http://standards-oui.ieee.org/oui.txt");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("HEAD");
        size = conn.getContentLength();

        if (D) {
            System.out.println("url = " + url);
            System.out.println("protocol = " + url.getProtocol());
            System.out.println("host = " + url.getHost());
            System.out.println("content = " + url.getContent());
            System.out.println("size = " + size + "bytes");
            System.out.println("");
        }
        
        BufferedInputStream in = new BufferedInputStream(url.openStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        byte[] buff = new byte[2048];
        int read = 0;
        int progress = -1;
        int total = 0;
        
        long begin = System.currentTimeMillis();
        
        while((read = in.read(buff)) != -1) {
            
            total += read;
            int cur = (total * 100) / size;
            
            if (cur != progress) {
                progress = cur;
                int elasped = (int) ((System.currentTimeMillis() - begin) / 1000);
                System.out.println(String.format("%7d bytes / %7d bytes (%3d percent), %3ds elasped.", total, size, progress, elasped));
            }
            out.write(buff, 0, read);
        }
        
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(DEFAULT_SOURCE_FILE));
        bos.write(out.toByteArray());
        bos.close();
        in.close();
        conn.disconnect();
        

//        BufferedReader bfr = new BufferedReader(new InputStreamReader(
//                url.openStream()));
//        BufferedWriter bfw = new BufferedWriter(new FileWriter(DEFAULT_SOURCE_FILE));
//
//        while ((buf = bfr.readLine()) != null) {
//            bfw.append(buf).append("\n");
//        }
//
//        bfr.close();
//        bfw.close();
        
        System.out.println("download done!!!, total elasped: " + ((System.currentTimeMillis()-begin) / 1000) + "s.");
    }
    
    /**
     * @param args
     * @throws IOException 
     * @throws XmlPullParserException 
     */
    public static void main(String[] args) throws XmlPullParserException {
        
        String ouiFile = null;
        
        System.out.println("Generate vendor mac file from RAW IEEE-oui files(http://standards-oui.ieee.org/oui.txt)");
        System.out.println("");
        
        if (args.length > 1) {
            printUsage();
            return;
        }
        
        if(args.length == 0) MODE = MODE_URL;
        else MODE = MODE_FILE;
        
        do {
            // web mode, download file
            if(MODE == MODE_URL) {
                try {
                    ouiFile = DEFAULT_SOURCE_FILE;
                    downloadouitxt();
                    break;
                } catch (IOException e) {
                    System.out.println("Check your internet connection...");
                    e.printStackTrace();
                }
            }
            
            if(MODE == MODE_FILE) {
                if ( new File(args[0]).isFile() == true)
                    ouiFile = args[0];
                else {
                    System.out.println(args[0] + " does not exist. use default source file: " + DEFAULT_SOURCE_FILE);
                    ouiFile = DEFAULT_SOURCE_FILE;
                }
            }
            
        } while(false);
        
        System.out.println("");
        
        try {
            loadOUIAndWrite(ouiFile);
            loadOUIAndmakeAppleMacEnumElements(ouiFile);
            loadOUIAndmakeXml(ouiFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
