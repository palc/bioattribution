BEGIN{
  findBioSample=1;
}
{
 for(i=1;i<=NF;i++) {
    if($i=="<BioSample") {
       findBioSample=2;
    }
    if(findBioSample==2) {
       if(substr($i,1,2)=="id") {
          split($i,res,"\"");
          id=res[2];
          testid = id;
          gsub(/[0-9]/,"",testid);
          if(testid==""){
             print id;
          } else{
             print NR":"$0 >> scriptpath"/deletedIDs.txt";
          }
          findBioSample=1;
       }
    }
      
 } 
}
