<p:declare-step xmlns:p="http://www.w3.org/ns/xproc"
                xmlns:dfdl="urn:daffodil:calabash"
                version="1.0">
    <p:output port="result"/>
    <p:import href="../../etc/daffodil-library.xpl"/>
    <dfdl:parse-file file="simpleCSV" schema="csv.dfdl.xsd" 
        root="ex:file" xmlns:ex="http://example.com"/>
</p:declare-step>
