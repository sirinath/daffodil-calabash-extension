<p:declare-step xmlns:p="http://www.w3.org/ns/xproc"
                xmlns:dfdl="urn:daffodil:calabash"
                name="pcap-pipeline"
                version="1.0">
    <p:input port="source" primary="true"/>
    <p:output port="result" primary="true"/>

    <p:import href="../etc/daffodil-library.xpl"/>
    
    <dfdl:parse schema="PCAP/schemas/pcap.dfdl.xsd"  
        root="pcap:PCAP" xmlns:pcap="urn:pcap:2.4"/>
</p:declare-step>
