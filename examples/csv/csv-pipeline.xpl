<p:declare-step xmlns:p="http://www.w3.org/ns/xproc"
                xmlns:c="http://www.w3.org/ns/xproc-step"
                xmlns:dfdl="urn:daffodil:calabash"
                name="csv-pipeline"
                version="1.0">
    <p:input port="source"/>
    <p:output port="result"/>

    <p:import href="../../etc/daffodil-library.xpl"/>
    
    <dfdl:parse schema="csv.dfdl.xsd" 
        root="ex:file" xmlns:ex="http://example.com"/>

</p:declare-step>
