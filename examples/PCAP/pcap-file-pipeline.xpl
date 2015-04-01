<p:declare-step xmlns:p="http://www.w3.org/ns/xproc"
                xmlns:c="http://www.w3.org/ns/xproc-step"
                xmlns:dfdl="urn:daffodil:calabash"
                name="pcap-pipeline"
                version="1.0">
    <p:output port="result" primary="true"/>

    <p:import href="../../etc/daffodil-library.xpl"/>
    
    <dfdl:parse-file file="tcp.ecn.pcap" name="parse" schema="pcap.dfdl.xsd"  
        root="pcap:PCAP" xmlns:pcap="urn:pcap:2.4"/>
</p:declare-step>
