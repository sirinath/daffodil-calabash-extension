<p:declare-step xmlns:p="http://www.w3.org/ns/xproc"
                xmlns:dfdl="urn:daffodil:calabash"
                name="pcap-pipeline"
                version="1.0">
    <p:input port="source" primary="true"/>
    <p:output port="result" primary="true"/>

    <p:import href="../../etc/daffodil-library.xpl"/>
    
    <dfdl:parse schema="pcap.dfdl.xsd"  
        root="ex:pcap" xmlns:ex="http://example.com"/>
</p:declare-step>
