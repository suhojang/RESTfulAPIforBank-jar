package com.kwic.util;

import java.awt.print.PrinterJob;

import javax.print.PrintService;
import javax.print.attribute.PrintServiceAttributeSet;

public class PrinterService {
	private static PrinterService instance;
	
	private PrinterService(){
		
	}
	
	public static PrinterService getInstance(){
		synchronized(PrinterService.class){
			if(instance==null){
				instance	= new PrinterService();
			}
		}
		return instance;
	}
	
	public String[] getPrinterNameList(){
		PrintService services[] = PrinterJob.lookupPrintServices();
		String[] printNameArr	= new String[services.length];
		
		for(int i=0;i<services.length;i++){
		    PrintServiceAttributeSet attrSet = services[i].getAttributes();

		    //System.out.println(" >> "+attrSet.get(javax.print.attribute.standard.PrinterName.class).toString());
		    printNameArr[i]	= attrSet.get(javax.print.attribute.standard.PrinterName.class).toString();
		    System.out.println(attrSet.get(javax.print.attribute.standard.ColorSupported.class).toString());
		    /*
		    Attribute[] attrs = attrSet.toArray();
		    for (int j = 0; j < attrs.length; j++) {
		    	System.out.println(attrs[j].getCategory() + ":" + attrs[j].getName() + ":" + attrs[j].toString());
		    }
		    DocFlavor[] flavors = services[i].getSupportedDocFlavors();
		    for (int j = 0; j < flavors.length; j++) System.out.println(flavors[j].toString());

		    System.out.println("====================");

		    Class[] category = services[i].getSupportedAttributeCategories();
		    for (int j = 0; j < category.length; j++) {
		        Attribute attr = (Attribute)services[i].getDefaultAttributeValue(category[j]);

		        if (attr != null) {
		            String attrName = attr.getName();
		            String attrValue = attr.toString();

		            System.out.println("---------------------");
		            System.out.println(attrName + ":" + attrValue);

		            Object o = services[i].getSupportedAttributeValues(attr.getCategory(), null, null);
		            if (o.getClass().isArray()) {
		                for (int k = 0; k < Array.getLength(o); k++) {
		                    Object o2 = Array.get(o, k);
		                    System.out.println(o2.toString());
		                }
		            }
		        }
		    }
		    */
		}
		return printNameArr;
	}
	
	public boolean isColorSupported(String printerName){
		String supported	= "";
		PrintService services[] = PrinterJob.lookupPrintServices();
		for(int i=0;i<services.length;i++){
		    PrintServiceAttributeSet attrSet = services[i].getAttributes();
			if(attrSet.get(javax.print.attribute.standard.PrinterName.class).toString().equals(printerName)){
				supported	= attrSet.get(javax.print.attribute.standard.ColorSupported.class).toString();
				break;
			}
		}
		return "supported".equals(supported);
	}
	
	public static void main(String[] args){
	}
	
}


