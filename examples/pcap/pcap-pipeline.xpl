<p:declare-step xmlns:p="http://www.w3.org/ns/xproc"
                xmlns:c="http://www.w3.org/ns/xproc-step"
                xmlns:dfdl="urn:daffodil:calabash"
                name="pcap-pipeline"
                version="1.0">
    <p:output port="result" primary="true">
        <p:pipe port="result" step="parse"/>
    </p:output>
    <p:option name="file" required="true"/>

    <p:import href="../../etc/daffodil-library.xpl"/>
    
    <!--
    <dfdl:parse name="parse" file="dns.cap" 
        schema="pcap.dfdl.xsd" 
        root="ex:pcap" xmlns:ex="http://example.com"/>
        -->
    <dfdl:parse-file name="parse" schema="pcap.dfdl.xsd"  
        root="ex:pcap" xmlns:ex="http://example.com">
        <p:with-option name="file" select="$file"/>
    </dfdl:parse-file>
</p:declare-step>
