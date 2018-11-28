declare namespace oms="urn:com:metasolv:oms:xmlapi:1";
declare namespace automator = "java:oracle.communications.ordermanagement.automation.plugin.ScriptReceiverContextInvocation";
declare namespace context = "java:com.mslv.oms.automation.TaskContext";
declare namespace log = "java:org.apache.commons.logging.Log";
declare namespace java="http://xml.apache.org/xslt/java";
declare namespace outboundMessage="java:javax.jms.TextMessage";
declare namespace provord="http://xmlns.oracle.com/EnterpriseObjects/Core/provord/ProvisioningOrder/V1";
declare namespace fn="http://www.w3.org/2005/02/xpath-functions";
declare namespace soapenv="http://schemas.xmlsoap.org/soap/envelope/";
declare namespace ord="http://xmlns.oracle.com/communications/ordermanagement";

declare variable $automator external;
declare variable $context external;
declare variable $log external;
declare variable $outboundMessage external;

let $orderXML := /oms:GetOrder.Response
let $selfOrderID :=$orderXML/oms:OrderID/text()

return
(
	log:info($log,concat('Setting JMSProperty and JMSCorrelationID for Order : ', $selfOrderID)),
	outboundMessage:setStringProperty($outboundMessage, '_wls_mimehdrContent_Type', 'text/xml; charset=&quot;utf-8&quot;'),
	outboundMessage:setStringProperty($outboundMessage, 'URI', '/osm/wsapi'),
	outboundMessage:setStringProperty($outboundMessage, 'Ora_OSM_COM_OrderId', $selfOrderID),
	outboundMessage:setStringProperty($outboundMessage, 'Event', 'OrderCreationEvent'),
	outboundMessage:setJMSCorrelationID($outboundMessage, concat($selfOrderID,'-','OrderCreationEvent')),
	log:info($log,concat('Sending XML for Order : ', $selfOrderID)),
	$orderXML
	
)