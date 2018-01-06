package com.accolite.Xml_Parsing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import javax.xml.parsers.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class License_Line{
	public String License_Line = "-";
	public Date License_Line_Effective_date  = null;
	public Date License_Line_Expiry_date = null;
	public String License_Line_Status = "-";
}

class License{
	public String StateCode;
	public String licenseNumber;
	public String EffectiveDate;
	public String Resident_Indicator;
	public String License_Class;
	public String License_Expiry_date;
	public String License_Status	;

	boolean is_License_Line = false;
	List<License_Line> License_Line_arr = new ArrayList<License_Line>();

	License(String StateCode , String license_no , String date){
		this.StateCode = StateCode;
		this.licenseNumber = license_no;
		this.EffectiveDate = date;
	}
	@Override
	public String toString() {
		return " StateCode : " + StateCode + " " + 
				" License Number : " + licenseNumber + 
				" Effective Date : " + EffectiveDate;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((EffectiveDate == null) ? 0 : EffectiveDate.hashCode());
		result = prime * result + ((StateCode == null) ? 0 : StateCode.hashCode());
		result = prime * result + ((licenseNumber == null) ? 0 : licenseNumber.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		License other = (License) obj;
		if (EffectiveDate == null) {
			if (other.EffectiveDate != null)
				return false;
		} else if (!EffectiveDate.equals(other.EffectiveDate))
			return false;
		if (StateCode == null) {
			if (other.StateCode != null)
				return false;
		} else if (!StateCode.equals(other.StateCode))
			return false;
		if (licenseNumber == null) {
			if (other.licenseNumber != null)
				return false;
		} else if (!licenseNumber.equals(other.licenseNumber))
			return false;
		return true;
	}

}



public class Xml_Dom_parsing {

	HashMap<String, List<License>> Main_map = new HashMap<>();
	HashMap<String, List<License>> map = new HashMap<>();
	private static final String LICENSE_HEADER_ROW =
			"nipr,License ID,Jurisdiction,Resident,License Class,License Effective Date,License Expiry Date,License Status,License Line,License Line Effective Date,License Line Expiry Date,License Line Status";



	void parse(String path , HashMap<String, List<License>> map_temp , boolean is_License_Line) {
		File Xml_file = new File(path);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(Xml_file);
			doc.getDocumentElement().normalize();
			NodeList nodeList;
			//			System.out.println("Root element is : " + doc.getDocumentElement().getNodeName());
			//			nodeList = doc.getElementsByTagName("CSR_Report_Header");
			//			for(int i = 0;i < nodeList.getLength();i++) {
			//				Node node = nodeList.item(i);
			//				System.out.println("element is : " + node.getNodeName());	
			//				if(node.getNodeType() == Node.ELEMENT_NODE) {
			//					Element ele = (Element)node;
			//					System.out.println("Title		: " + ele.getAttribute("Title"));
			//					System.out.println("Report_Type : " + ele.getAttribute("Report_Type"));
			//					System.out.println("Time_Stamp  : " + ele.getAttribute("TimeStamp_Created"));
			//				}
			//				System.out.println("\n\n\n");
			//			}

			nodeList = doc.getElementsByTagName("CSR_Producer");
			for(int i = 0;i < nodeList.getLength();i++) {
				Node node = nodeList.item(i);
				System.out.println("element is : " + node.getNodeName());
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					Element ele = (Element)node;
					System.out.println("NIPR_Number	: " + ele.getAttribute("NIPR_Number"));
					System.out.println("Entity_Id   : " + ele.getAttribute("Entity_Id"));
					System.out.println("Secondary_ID: " + ele.getAttribute("Secondary_ID") + "\n");

					NodeList License_nodeList = ele.getElementsByTagName("License");
					List<License> arr = new ArrayList<License>();

					for(int j = 0;j < License_nodeList.getLength();j++) {
						Node temp_node = License_nodeList.item(j);
						System.out.println("element is : " + temp_node.getNodeName());
						if(temp_node.getNodeType() == Node.ELEMENT_NODE) {
							Element temp_ele = (Element)temp_node;
							load_arr(arr ,temp_ele , is_License_Line);
						}
					}

					map_temp.put(ele.getAttribute("NIPR_Number") , arr);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	void load_arr(List<License> arr , Element temp_ele , boolean is_License_Line) {

		System.out.println("State_Code		: " + temp_ele.getAttribute("State_Code"));
		System.out.println("License_Number  : " + temp_ele.getAttribute("License_Number"));
		System.out.println("Date_Status_Effective : " + temp_ele.getAttribute("Date_Status_Effective") + "\n");
		License obj = new License(temp_ele.getAttribute("State_Code") , temp_ele.getAttribute("License_Number") , 
				temp_ele.getAttribute("Date_Status_Effective"));
		obj.is_License_Line = is_License_Line;
		obj.Resident_Indicator = temp_ele.getAttribute("Resident_Indicator");
		obj.License_Class = temp_ele.getAttribute("License_Class");
		obj.License_Expiry_date = temp_ele.getAttribute("License_Expiration_Date");
		obj.License_Status = temp_ele.getAttribute("License_Status");
		if(is_License_Line) {
			NodeList nodeList = temp_ele.getElementsByTagName("LOA");
			for(int i = 0;i < nodeList.getLength();i++) {
				Node temp_node = nodeList.item(i);
				License_Line object = new License_Line();
				if(temp_node.getNodeType() == Node.ELEMENT_NODE) {
					Element ele = (Element)temp_node;
					try {

						object.License_Line_Effective_date = new SimpleDateFormat("MM/dd/yyyy").parse(ele.getAttribute("LOA_Issue_Date"));
						object.License_Line_Expiry_date = object.License_Line_Effective_date;
						object.License_Line_Expiry_date.setYear(object.License_Line_Effective_date.getYear() + 2);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						object.License_Line_Effective_date = null;
						object.License_Line_Expiry_date = new Date(0);
					}
					object.License_Line = ele.getAttribute("LOA_Name");
					object.License_Line_Status = ele.getAttribute("LOA_Status");
				}
				obj.License_Line_arr.add(object);
			}
		}

		arr.add(obj);
	}

	void Compare_and_Write_to_file() {
		boolean flag = true;
		for (Entry<String, List<License>> Main_Map_entry : Main_map.entrySet()) {
			List<License> Main_arr = Main_Map_entry.getValue();
			for (Entry<String, List<License>> Map_entry : map.entrySet()) {
				List<License> arr = Map_entry.getValue();

				// if both entries have same NIPR number then we go to next comparison else 
				// we directly write content to a file 
				if(Main_Map_entry.getKey().equals(Map_entry.getKey())) {


					for (License obj : Main_arr) {
						for(License temp_obj : arr) {
							if(obj.licenseNumber.equals(temp_obj.licenseNumber) &&
									(obj.StateCode.equals(temp_obj.StateCode)) &&
									(obj.EffectiveDate.equals(temp_obj.EffectiveDate))) {
								write_to_file(Map_entry.getKey() , Map_entry.getValue(),"Merged.csv");
								//((List<License_Line>) Main_Map_entry).remove(Main_Map_entry.getKey());
								//((List<License_Line>) Map_entry).remove(Map_entry.getKey());
								//Map_entry.setValue(null);
								//Main_Map_entry.setValue(null);
							}
						}
					}
					flag = false;
				} 
			}
			//if flag is true then it implies that there is no entry found with same NIPR number
			// in the second file so we write that content to file
			if(flag) {
				System.out.println(Main_Map_entry.getKey());
				write_to_file(Main_Map_entry.getKey() , Main_Map_entry.getValue(),"Invalid_Licenses.csv");
				//((List<License_Line>) Main_Map_entry).remove(Main_Map_entry.getKey());
				Main_Map_entry.setValue( null);
			}
		}
		for (Entry<String, List<License>> Map_entry : map.entrySet()) {
			write_to_file(Map_entry.getKey() , Map_entry.getValue(),"Invalid_License_Lines.csv");
		}
	}

	private void write_to_file(String NIPR,List<License> arr , String File_Name) {

		FileWriter fw = null;
		try {
			fw = new FileWriter(File_Name);
			fw.append(LICENSE_HEADER_ROW + "\n");
			System.out.println("length : " + arr.size());
			for(License obj :arr) {
				System.out.println("In Writing : " + obj.licenseNumber + " Length " + obj.License_Line_arr.size());
				if(obj.License_Line_arr.size() > 0) {
					for(int i = 0;i < obj.License_Line_arr.size();i++) {
						fw.append(NIPR + "," + obj.licenseNumber + ",");
						fw.append(obj.StateCode + "," + obj.Resident_Indicator + ",");
						fw.append(obj.License_Class + "," + obj.EffectiveDate + ",");
						fw.append(obj.License_Expiry_date + "," + obj.License_Status + ",");
						fw.append(obj.License_Line_arr.get(i).License_Line + ","); 
						fw.append(obj.License_Line_arr.get(i).License_Line_Effective_date + ",");
						fw.append(obj.License_Line_arr.get(i).License_Line_Expiry_date + "," );
						fw.append(obj.License_Line_arr.get(i).License_Line_Status + "\n");
					}
				}	
				else {
					fw.append(NIPR + "," + obj.licenseNumber + ",");
					fw.append(obj.StateCode + "," + obj.Resident_Indicator + ",");
					fw.append(obj.License_Class + "," + obj.EffectiveDate + ",");
					fw.append(obj.License_Expiry_date + "," + obj.License_Status + "\n");
					//					fw.append(obj.License_Line_arr.get(0).License_Line + ","); 
					//					fw.append(obj.License_Line_arr.get(0).License_Line_Effective_date + ",");
					//					fw.append(obj.License_Line_arr.get(0).License_Line_Expiry_date + "," );
					//					fw.append(obj.License_Line_arr.get(0).License_Line_Status + "\n");
				}
			}		
		}
		catch(Exception e) {
			System.out.println(e);;
		}
		try {
			fw.flush();
			fw.close();
		} catch (IOException e) {
			System.out.println(e);;
		}
	}

	public static void main(String[] args) {
		Xml_Dom_parsing obj = new Xml_Dom_parsing();
		obj.parse("src//Licenses.xml" , obj.Main_map , false);
		obj.parse("src//Line_Licenses.xml" , obj.map , true);		
		obj.Compare_and_Write_to_file();
	}
}
