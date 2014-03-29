<library xmlns="http://www.w3.org/ns/xproc"
           xmlns:dfdl="urn:daffodil:calabash"
           version="1.0">

   <declare-step type="dfdl:parse-file">
      <output port="result" />
      <option name="file" required="true" />   
      <option name="schema" required="true" />
      <option name="root" />          <!-- (QName) -->

      <!--
      <output port="warnings" />
      <option name="show-trace" />    (boolean - default: false)
      <option name="assert-valid" />  (boolean - default: false)
      <option name="validate-schema" />  (boolean - default: false)
      -->
   </declare-step>
   
   <declare-step type="dfdl:parse">
      <input port="source" />   <!-- must be base64-encoded version of input, as created by p:data -->
      <output port="result" />
      <option name="schema" required="true" />
      <option name="root" />          <!-- (QName) -->
   </declare-step>

</library>