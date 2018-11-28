import module namespace aiaebmvalidationfn = "http://xmlns.oracle.com/communications/ordermanagement/pip/aiaebmvalidationfn" at "http://xmlns.oracle.com/communications/ordermanagement/pip/aiaebmvalidationfn/AIAEBMResponse_ValidationModule.xquery";

declare namespace salesord="http://xmlns.oracle.com/EnterpriseObjects/Core/EBO/SalesOrder/V2";


(: SalesOrder EBM validation :)

let $ebm := ../salesord:ProcessSalesOrderFulfillmentEBM 
return
    aiaebmvalidationfn:validateProcessSalesOrderFulfillmentEBM($ebm)

