BEGIN {
  count=0;
#  filePrefix="/path/to/BioSampleXML/scripts/testsplit/test";
#  openingTag="<BioSampleSet>";
#  closingTag="</BioSampleSet>";
#  beginTag1="<BioSample>";
#  beginTag2="<BioSample ";
#  endTag="</BioSample>";
#  maxRecords="5000";

  print openingTag > filePrefix count".xml";
  line=0;
  currentLength=0;
  foundClosed=0;
}
{
  endIndex=index($0,endTag);
  beginIndex1=index($0,beginTag1);
  beginIndex2=index($0,beginTag2);

  if((beginIndex1 > 0) || (beginIndex2 > 0)) {
    state=1;
  } 
  if(state == 1) {
    currentLength = currentLength + length($0);
    if(currentLength > 10000000) {
      state=2;
      for(i=0;i<line;i++) {
        print savedLines[i] >> filePrefix ".long";
      }
      print NR":"$0 >> filePrefix ".long";
      line=0;
    } else {
      gsub(/R\&D/,"R&amp;D",$0);
      gsub(/\& /,"&amp;",$0);
      gsub(/< /,"&lt; ",$0);
      gsub(/<15.0 cm/,"&lt;15.0 cm",$0);
      gsub(/<2 Y/,"&lt;2 Y",$0);
      savedLines[line]=$0;
      line++;
    }
  }

  if((endIndex > 0)) {
    if(state == 1) {
      for(i=0;i<line;i++) {
        print savedLines[i] >> filePrefix count".xml";
      }
      linecount++;
      if(linecount>=maxRecords) {
        print closingTag >> filePrefix count".xml";
        count++;
        print openingTag > filePrefix count".xml";
        linecount=0;
      }
    }
    state=0;
    currentLength=0;
    line=0;
  }
}
END {
  print closingTag >> filePrefix count ".xml";
}
