<p:declare-step xmlns:p="http://www.w3.org/ns/xproc"
                xmlns:c="http://www.w3.org/ns/xproc-step"
                xmlns:dfdl="urn:daffodil:calabash"
                name="csv-pipeline"
                version="1.0">
    <p:output port="result" primary="true">
        <p:pipe port="result" step="parse"/>
    </p:output>

    <p:import href="../../etc/daffodil-library.xpl"/>
    

    <dfdl:parse name="parse" file="simpleCSV" 
        schema="csv.dfdl.xsd" 
        root="ex:file" xmlns:ex="http://example.com"/>
<!--
    <dfdl:parse name="parse" file="../examples/pcap/dns.cap" 
        schema="../examples/pcap/pcap.dfdl.xsd" 
        root="ex:pcap" xmlns:ex="http://example.com"/> 
 -->
</p:declare-step>
