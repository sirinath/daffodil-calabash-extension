<p:declare-step xmlns:p="http://www.w3.org/ns/xproc"
                xmlns:c="http://www.w3.org/ns/xproc-step"
                xmlns:dfdl="urn:daffodil:calabash"
                name="pcap-pipeline"
                version="1.0">
    <p:output port="result" primary="true"/>
    
    <p:option name="file" required="true"/>

    <p:import href="../etc/daffodil-library.xpl"/>
    
    <dfdl:parse-file name="parse" schema="PCAP/schemas/pcap.dfdl.xsd"  
        root="pcap:PCAP" xmlns:pcap="urn:pcap:2.4">
        <p:with-option name="file" select="$file"/>
    </dfdl:parse-file>
</p:declare-step>
