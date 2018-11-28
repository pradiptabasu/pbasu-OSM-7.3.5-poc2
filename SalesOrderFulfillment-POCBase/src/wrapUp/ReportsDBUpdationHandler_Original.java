package wrapUp;


/*
 * This Class implementation intended for replicating OSM Order Data into Reports DB
 * It will come into play once the creation milestone event is fired inside the OSM
 * For all the Orders which are created into the OSM regardless of current OrderState,Certain Agreed
 * data will be sent to Reports DB which will further Used for generation of reports to the end-user. 
 
 
 @author Simhachalam Kolli
 */

import java.awt.GraphicsEnvironment;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mslv.oms.automation.AutomationContext;
import com.mslv.oms.automation.AutomationException;
import com.mslv.oms.automation.OrderContext;
import com.mslv.oms.automation.OrderNotificationContext;
import com.mslv.oms.automation.OrderUpdateException;
import com.mslv.oms.automation.TaskContext;
import com.mslv.oms.automation.plugin.AbstractAutomator;


public class ReportsDBUpdationHandler_Original extends AbstractAutomator  {
	
	private static final long serialVersionUID = -410941090053971111L;
	
	public static final Log logger = LogFactory.getLog(ReportsDBUpdationHandler_Original.class);
	public static final String WEBLOGIC_JNDI_INITIAL_CONTEXT = "weblogic.jndi.WLInitialContextFactory";
	public static final String DATASOURCE_NAME = "tdc.osm.reports.datasource";
	public static final String xsltFile_OrderCategory="/xsltFilesForJava/UpdateOrderCategory.xsl";
	public static final String xsltFile_Reports="/xsltFilesForJava/UpdatingReportDBOnOrderCreation.xsl";
	private static final String AWT_HeadLess = "java.awt.headless";
	private static final String BASE_FILENAME = "Wrap-up-page";
	@Override
	public void run(String inputXML, AutomationContext orderNotificationContext) throws AutomationException 
	{
		String updateOrderData=null;
		String insertQueryData=null;
		Connection conn = null;
		Context ctx = null;
		Statement stmt=null;
		try {
			
			  
			OrderContext orderContext=(OrderContext)orderNotificationContext;
			
			/*
			  * Updating OrderCategory upon the creation milestone event   
			  * for segregation between Private/Soho/Business customers  
			 */
			logger.info("GetOrder Response :"+inputXML);
			//updateOrderData=performTransformation(inputXML, xsltFile_OrderCategory);
			//logger.info("Transformation Result (Update Order Data) :"+updateOrderData);
			
			//OrderContext orderContext=(OrderNotificationContext)orderNotificationContext;
			//orderContext.updateOrderData(updateOrderData);
			logger.info("<Order ID>"+orderContext.getOrderId()+"Updated");
			/*
			   * Attaching PDF to the Order 
			 */
			
			ArrayList arrayList=(ArrayList)orderContext.getAllAttachmentFileNames();
			Iterator iterator=arrayList.iterator();
			 boolean wrapup_check=true; 
				while(iterator.hasNext()){
					String fileName=(String)iterator.next();
					logger.info("Attachment File Name :"+fileName);
					if(fileName.indexOf("Wrap-up-page") != -1 ){
						wrapup_check=false;
					}
				}
				logger.info("WrapUpCheck to be done"+wrapup_check);
				if(wrapup_check){
					pdfGenerator(inputXML,orderContext);
				}
				
			/*
			 * Logic for Updating ReportsDB.
			 * Here,Performing XSLT transformation for generating the Database Queries.
			 */
			//insertQueryData=performTransformation(inputXML, xsltFile_Reports);
			/*
			 * Obtaining the Reports Database connection through the DataSource
			 */
			//ctx = new InitialContext();
			//DataSource ds = (DataSource) ctx.lookup(DATASOURCE_NAME);
			//conn = ds.getConnection();
			/*
			 * Parsing the Result Data to get the individual Queries
			 */
			/*
				DocumentBuilderFactory docBuilderFactory =DocumentBuilderFactory.newInstance();
				DocumentBuilder parser = docBuilderFactory.newDocumentBuilder();
				Document doc = parser.parse(new ByteArrayInputStream(insertQueryData.getBytes()));
				Element root = doc.getDocumentElement();
				NodeList nodeList=root.getElementsByTagName("query");
				stmt = conn.createStatement();
				for(int counter=0;counter<nodeList.getLength();counter++){
					Node node=nodeList.item(counter);
					stmt.addBatch(node.getTextContent());
				}
				stmt.executeBatch();
				stmt.close();
			*/
		} /*catch (OrderUpdateException e) {
			// TODO Auto-generated catch block
			logger.error("Exception Rasied:"+e);
		}catch (NamingException e) {
			// TODO Auto-generated catch block
			logger.error("Exception Rasied:"+e);
		} catch (SQLException e) {
			// TODO Auto-generated catch blockR
			logger.error("Exception Rasied:"+e);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			logger.error("Exception Rasied:"+e);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			logger.error("Exception Rasied:"+e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception Rasied:"+e);
		}*/
		 finally {
				try {
					if (ctx != null)
						ctx.close();
					if (conn != null)
						conn.close();
				} catch (SQLException sqle) {
					sqle.printStackTrace();
					logger.error("Exception Rasied:"+sqle);
				} catch (NamingException ne) {
					ne.printStackTrace();
					logger.error("Exception Rasied:"+ne);
				}
		 }		
	}
	public  String performTransformation(String inputXML,String xsltPath)
	{
		TransformerFactory tFactory=null;
		Transformer transformer=null;
		Writer writer=null;
		StreamResult streamResult=null;
		StringBuffer stringBuffer=null;
		StringWriter stringWriter=null;
		try {
			  writer=new StringWriter();
			  stringBuffer=new StringBuffer();
		      streamResult=new StreamResult(writer);
			  tFactory = TransformerFactory.newInstance();
			  /*
			   * Performing conversion of one form XML to another by using XSLT
			   */
			  transformer = tFactory.newTransformer(new StreamSource(this.getClass().getResourceAsStream(xsltPath)));
			  transformer.transform(new StreamSource(new StringReader(inputXML)),streamResult);
			  /*
			   * Sending the Contents of StreamResult into StringBuffer and return the result 
			   */
			  stringWriter= (StringWriter) streamResult.getWriter(); 
		      stringBuffer = stringWriter.getBuffer();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			logger.error("Exception Rasied:"+e);
		}  catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			logger.error("Exception Rasied:"+e);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			logger.error("Exception Rasied:"+e);
		} 
		finally 
		{
			try 
			{
				if (writer != null)
					writer.close();
				if(stringWriter != null)
					stringWriter.close();
			}catch(IOException e)  {
			logger.info("Exception Raised :"+e);
			}
	   }		
		return stringBuffer.toString();
	  }
	
	
	public void pdfGenerator(String xml, OrderContext context) throws AutomationException {
		long orderId = context.getOrderId();
		logger.info("####  Running PDF Attacher for order " + orderId + " ####");
		logger.debug(AWT_HeadLess + " = " + System.getProperty(AWT_HeadLess));
		if (!GraphicsEnvironment.isHeadless()) {
			System.setProperty(AWT_HeadLess, "true");
			logger.debug(AWT_HeadLess + " set to true");
		}
		
		logger.info("####  Default View ID is :" + context.getViewId());
		byte[] content = null;
		try {
			/* For adding xml necessary for debugging */
			//context.addAttachment(xml.getBytes(), BASE_FILENAME + ".xml");
			logger.info("XML for debugging wrap-up page PDF-attachment added to order with ID: " + orderId);
		
			FopFactory fopFactory = FopFactory.newInstance();
			fopFactory.setStrictValidation(false); 
			FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);
			TransformerFactory factory = TransformerFactory.newInstance();
			//StreamSource ins = new StreamSource(this.getClass().getResourceAsStream("/xsltFilesForJava/pdf-template.xsl"));
			StreamSource ins = new StreamSource(this.getClass().getResourceAsStream("pdf-template_com.xsl"),"./pdf-template_com.xsl");
			Transformer transformer = factory.newTransformer(ins);
			transformer.setParameter("version", "1.0");
			logger.debug("Order [" + orderId + "] XSLT transformer created");
			
			//Removing empty product parameters
			//Matches the named elements including all attributes.
			xml = xml.replaceAll("<ProductParameter( [^>]*)?></ProductParameter>", "");
			xml = xml.replaceAll("<ParameterValue( [^>]*)?></ParameterValue>", "");
			xml = xml.replaceAll("<ParameterName( [^>]*)?></ParameterName>", "");
			
			
			Source src = new StreamSource(new StringReader(xml));
			Result res = new SAXResult(fop.getDefaultHandler());
			logger.debug("SAX-parser for Order-XML initalised.\nBeginning XSLT-transformation to PDF-file");
			transformer.transform(src, res);
			logger.debug("XML transformation completed!");
			content = out.toByteArray();
			out.close();		

		} catch (Exception e) {
			logger.fatal("Order [" + orderId + "] Exception causes wrap-up page generation to be terminated!", e);
			logger.fatal(xml);
			return;
		}
		if (content != null) {
			try {
				context.addAttachment(content, BASE_FILENAME + ".pdf");
				logger.info(BASE_FILENAME + ".pdf attached to order with id: " + orderId);
			} catch (Exception e) {
				logger.error("Order[" + orderId + "] Wrap-up page not attached!", e);
			}
		}	
		logger.trace("Order[" + orderId + "] Finished execution");

	}

}
