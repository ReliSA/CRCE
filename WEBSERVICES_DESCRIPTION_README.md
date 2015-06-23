# Branch-specific notes

### Installation Instructions
1. Pull this branch and make use your own specific configuration as described in step no. 3 at https://www.assembla.com/spaces/crce/wiki
2. In order to prevent unsolved "maven store BUG" in this branch you need to disable maven store. Easiest way to do this is to set `store.uri` property of __conf/cz.zcu.kiv.crce.repository.maven-local.cfg__ to some non-existing directory. If you did this right you should not see a repository selection box in Web UI (since there is only filebased store active).
3. Build the repository by running `mvn -Dmaven.test.skip=true -Dfindbugs.skip=true -Dpmd.failOnViolation=false install` from project root folder.
4. Start the application by launching by `mvn pax:run` in the modules/ subfolder.
5. Web UI should now be accessible at http://localhost:8080/crce
6. Now you are able to use Webservices description module at http://localhost:8080/crce/resource?link=webservices

### Examples of Webservice IDLs to index

  - JSON-WSP
   - http://ladonize.org/python-demos/AlbumService/jsonwsp/description
   - http://ladonize.org/python-demos/Calculator/jsonwsp/description
   - http://grom.ijs.si:8001/MUSE_services_V3/jsonwsp/description
  - WSDL
   - http://ladonize.org/python-demos/Calculator/soap/description (simple IDL)
   - http://ladonize.org/python-demos/AlbumService/soap/description (simple IDL)
   - http://ladonize.org/python-demos/Calculator/soap11/description (simple IDL with more namespaces)
   - http://ladonize.org/python-demos/AlbumService/soap11/description (simple IDL with more namespaces)
   - http://www.webservicex.net/geoipservice.asmx?WSDL (more complex IDL)
   - http://www.webservicex.com/globalweather.asmx?wsdl
   - https://raw.githubusercontent.com/jkinred/psphere/master/psphere/wsdl/vim.wsdl (IDL will not be parsed, because this WSDL does not define any service elements in its concrete section thus no webservice descriptions to process)
   - http://enterprise-demo.user.magentotrial.com/api?wsdl
  - WADL
   - ftp://ftp.bgbilling.ru/pub/bgbilling/activemq/win/apache-activemq-5.4.2/webapps/camel/WEB-INF/classes/application.wadl
   - http://spotlight.dbpedia.org/rest/application.wadl
   - https://www.fueleconomy.gov/ws/rest/application.wadl
   - http://api.dnbdirectapps.com/dev/DnBAPI-10/rest/application.wadl
   - https://api.staging.launchpad.net/1.0/ (HTTP header is needed in GET -- Accept: application/vd.sun.wadl+xml)
