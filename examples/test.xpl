<p:declare-step xmlns:p="http://www.w3.org/ns/xproc"
                xmlns:c="http://www.w3.org/ns/xproc-step"
                xmlns:dfdl="urn:daffodil:calabash"
                name="pipeline"
                version="1.0">
    <p:output port="result" primary="true">
        <p:pipe port="result" step="parse"/>
    </p:output>

    <p:import href="../etc/daffodil-library.xpl"/>
    
    <dfdl:parse name="parse" file="../examples/csv/simpleCSV" 
        schema="../examples/csv/csv.dfdl.xsd" />
    <!-- <dfdl:parse name="parse" file="../examples/pcap/dns.cap" 
        schema="../examples/pcap/pcap.dfdl.xsd" /> -->
 
</p:declare-step>
