declare namespace ord="http://xmlns.oracle.com/communications/ordermanagement";

declare function local:orderLineItem($orderLine)
{
             for $x in $orderLine
                return
                <OrderLine>
      				
			<ProductClass>{$x/ord:ProductClass/text()}</ProductClass>
			<ProductName>{$x/ord:ProductName/text()}</ProductName>
			<ProductCategory>{$x/ord:ProductCategory/text()}</ProductCategory>
			<OrderLineAction>{$x/ord:OrderLineAction/text()}</OrderLineAction>
      		</OrderLine>
};

let $order := fn:root(.)//ord:Order
let $orderLine := $order/ord:OrderLine
let $orderHeader := $order/ord:OrderHeader
let $fulfillmentModeCode := $orderHeader/ord:Mode/text()
let $orderType := $orderHeader/ord:ExternalOrderType/text()
let $salesOrderNumber := $orderHeader/ord:SalesOrderNumber/text()
let $salesChannel := $orderHeader/ord:SalesChannel
let $customerHeader := $order/ord:CustomerHeader
let $customerInfo := $customerHeader/ord:CustomerInfo
let $OrderLine := $order/ord:OrderLine
let $ProductClass := $OrderLine/ord:ProductClass
let $ProductName := $OrderLine/ord:ProductName
let $ProductCategory := $OrderLine/ord:ProductCategory
let $OrderLineAction := $OrderLine/ord:OrderLineAction

return
    <_root>
        <Order>
        	<OrderHeader>
        		<SalesOrderNumber>
        			{$salesOrderNumber}
        		</SalesOrderNumber>
        		<SalesChannel>
        			<ID>
        				{$salesChannel/ord:ID/text()}
        			</ID>
        			<Name>
        				{$salesChannel/ord:Name/text()}
        			</Name>
        		</SalesChannel>
        		<ExternalOrderType>
					{$orderType}   		
        		</ExternalOrderType>
        		<OrderCreationDate>
        			{data($salesChannel/ord:OrderCreationDate)}
        		</OrderCreationDate>
        		{
	        		if (fn:exists($salesChannel/ord:RequestedDeliveryDate)) then
	        			<RequestedDeliveryDate>
	        				{data($salesChannel/ord:RequestedDeliveryDate)}
	        			</RequestedDeliveryDate>
	        		else ()
        		}
        		<Mode>
        			{$fulfillmentModeCode}
        		</Mode>
        	</OrderHeader>
        	<CustomerHeader>
        		<CustomerType>
        			{$customerHeader/ord:CustomerType}
        		</CustomerType>
        		<CustomerName>
        			{$customerHeader/ord:CustomerName}
        		</CustomerName>
        		<CustomerInfo>
        			<CompanyInfo>
        				<CVRNo>
        				</CVRNo>
        				<CompanyName>
        				</CompanyName>
        				<Department>
        				</Department>
        				<Email>
        				</Email>
        				<Phone>
        				</Phone>
        				<ContactPerson>
        				</ContactPerson>
        				<SecondaryLn>
        				</SecondaryLn>
        				<CostCenter>
        				</CostCenter>
        			</CompanyInfo>
        		</CustomerInfo>
        	</CustomerHeader>
        	<AccountDetails>
        	</AccountDetails>
        	<OrderDetails>
        	</OrderDetails>
                
      	        {local:orderLineItem($orderLine)}
       
        </Order>
	    
    </_root>
