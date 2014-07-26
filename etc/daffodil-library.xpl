<library xmlns="http://www.w3.org/ns/xproc"
           xmlns:dfdl="urn:daffodil:calabash"
           version="1.0">

   <declare-step type="dfdl:parse-file">
      <output port="result" />
      <option name="file" required="true" />   
      <option name="schema" required="true" />
      <option name="root" />          <!-- (QName) -->

      <!-- 
	   TODO
	   <option name="assert-valid" />  (boolean - default: false)
	   <option name="validate-schema" />  (boolean - default: false)
      -->
   </declare-step>
   
   <declare-step type="dfdl:parse">
      <!-- 
         The text content of the root element is used as the input to the 
         parser.
         
         If the root element has an 'encoding' attribute with a value of 
         'base64', then the input is base64-decoded before being passed
         to Daffodil.
         
         Otherwise, if there is a 'charset' attribute or a charset parameter
         in a 'content-type' attribute, then the text is encoded using that
         character set before being passed to parser.
         
         Otherwise, if no valid character set is specified, the text is encoded
         to UTF-8 before being passed to the parser.
         
         Note that p:data produces exactly the right kind of XML to be
         processed by this step.  See http://www.w3.org/TR/xproc/#p.data
         for more details.
      -->
      <input port="source" />   
      
      <output port="result" />
      <option name="schema" required="true" />
      <option name="root" />          <!-- (QName) -->
   </declare-step>

</library>
