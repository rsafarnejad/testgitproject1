package gov.eeoc.complainantportal.util;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFFooter;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HeaderFooter;
import org.apache.poi.hssf.util.HSSFColor;

public class WebUtil {

	public static final String SECURITY_TOKEN = "TOKEN";
	public final static String CHARGE_SEQ = "CHARGE_SEQ";
	
	private WebUtil() {
	}
    
	public static String getServerContext(FacesContext fc) {
		String server = fc.getExternalContext().getRequestServerName();
		int port = fc.getExternalContext().getRequestServerPort();
		String protocol = fc.getExternalContext().getRequestScheme();
		String contextname = fc.getExternalContext().getContextName();
		StringBuffer buf = new StringBuffer();
		
		//buf.append("<img src='");
		buf.append(protocol);
		buf.append("://");
		buf.append(server);
		buf.append(":");
		// buf.append();
		buf.append(port);
		buf.append("/");
		buf.append(contextname);
		
		return buf.toString();
	}
	
	public static String getParameterValue(String paramName) {
		if (paramName == null)
			return null;

		FacesContext facesContext = FacesContext.getCurrentInstance();
		String parameter_value = (String) facesContext.getExternalContext()
				.getRequestParameterMap().get(paramName);
		return parameter_value;
	}

	public static void setParameterValue(String key, String value) {
		String[] val = new String[1];
		val[0] = value;
		FacesContext facesContext = FacesContext.getCurrentInstance();
		System.out.println("&&&&&&&&&&&&& = "
				+ facesContext.getExternalContext()
						.getRequestParameterValuesMap().size());
		facesContext.getExternalContext().getRequestParameterValuesMap()
				.put(key, val);
		System.out.println("xxxxxxxxxxxxxxxxx = "
				+ facesContext.getExternalContext()
						.getRequestParameterValuesMap().size());
	}

	public static void redirectToPage(String toUrl) {
		try {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ExternalContext extContext = ctx.getExternalContext();
			String url = extContext.encodeActionURL(ctx.getApplication()
					.getViewHandler().getActionURL(ctx, toUrl));
			extContext.redirect(url);
		} catch (IOException e) {
			throw new FacesException(e);
		}
	}

	public static boolean toBoolean(String str) {
		if (str == null || str.isEmpty())
			return false;
		if (str.equalsIgnoreCase("Y"))
			return true;
		return false;
	}

	public static String fromBoolean(boolean flag) {
		if (flag == true)
			return "Y";
		return null;
	}

	public static void SetSessionVariable(String key, Object value) {
		FacesContext context = FacesContext.getCurrentInstance();
		if(context == null){
			throw new RuntimeException("FacesContext for current instance is null");
		}
		HttpServletRequest request = (HttpServletRequest) context
				.getExternalContext().getRequest();
		HttpSession httpSession = request.getSession(false);
		httpSession.setAttribute(key, value);
	}

	public static void RemoveSessionVariable(String key) {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context
				.getExternalContext().getRequest();
		HttpSession httpSession = request.getSession(false);
		httpSession.removeAttribute(key);
	}

	public static Object getSessionVariable(String key) {
		FacesContext context = FacesContext.getCurrentInstance();
		if(context == null){
			throw new RuntimeException("FacesContext for current instance is null");
		}
		HttpServletRequest request = (HttpServletRequest) context
				.getExternalContext().getRequest();
		HttpSession httpSession = request.getSession(false);
		if(httpSession == null){
			return null;
		}
		return httpSession.getAttribute(key);
	}

	public static void processXLS(Object document, String pHeader) {
	    HSSFWorkbook wb = (HSSFWorkbook) document;  
	    HSSFSheet sheet = wb.getSheetAt(0);  
	    System.out.println("The number of rows are = "+sheet.getLastRowNum());
	    HSSFFont boldFont = wb.createFont();
	    boldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	    HSSFCellStyle cellStyle = wb.createCellStyle();    
	    cellStyle.setFillForegroundColor(HSSFColor.GREEN.index);  
	    cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	    cellStyle.setFont(boldFont);
	    cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	    HSSFHeader pageHeader = sheet.getHeader();
	    pageHeader.setCenter(pHeader+" "+ new Date().toString());
	    HSSFRow header = sheet.getRow(0);
	    for(int i=0; i < header.getPhysicalNumberOfCells();i++) {  
	        HSSFCell cell = header.getCell(i);  
	        cell.setCellStyle(cellStyle);
	        sheet.autoSizeColumn(i);
	    }
	    for(int j=1; j <= sheet.getLastRowNum();j++){
	    	HSSFRow recRow = sheet.getRow(j);
		    for(int k=0; k < recRow.getPhysicalNumberOfCells();k++) {  
		        HSSFCell cell = recRow.getCell(k); 
		        System.out.println("The cell "+k+"value is "+cell.getStringCellValue());
		        String val = StringUtils.substringBetween(cell.getStringCellValue(), ">", "<");
		        if(val != null){
		        	cell.setCellValue(val);
		        }else{
		        	cell.setCellValue(cell.getStringCellValue());
		        }
		        sheet.autoSizeColumn(k);
		    }
	    }
	    HSSFFooter footer = sheet.getFooter();
	    footer.setRight( "Page " + HeaderFooter.page() + " of " + HeaderFooter.numPages() );
		
	}
	
	public static void redirectToExpirePage(){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext ec = facesContext.getExternalContext();
		try {
			ec.redirect("SessionExpire.jsp");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean getSessionVariableAsBoolean(String key){
		 Object obj = WebUtil.getSessionVariable(key);
		 if(obj != null){
			 return (Boolean)obj;
		 }
		 return false;
	}
	
	public static String getRemoteServerHost(){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext ec = facesContext.getExternalContext();
		HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
		return request.getRemoteHost();
	}

	public static List<String> getChargeSeqs(){
		List<String> chargeSeqList = (List<String>) WebUtil.getSessionVariable(WebUtil.CHARGE_SEQ);
		if(chargeSeqList == null){
			return Collections.emptyList();
		}
		return chargeSeqList;
	}

}

