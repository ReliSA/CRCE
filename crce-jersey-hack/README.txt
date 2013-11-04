This module enforces [3.1,4.0) version dependency on asm for jersey-server.

Jersey Servlet 1.17 doesn't work with asm version 4 and higher (see http://stackoverflow.com/questions/12166382/java-lang-incompatibleclasschangeerror-implementing-class-deploying-to-app-engi)

This workaround found here: http://stackoverflow.com/questions/14948301/osgi-is-it-possible-to-override-a-bundles-import-package-version-using-a-frag
In particular link leading to this repository: https://github.com/ops4j/org.ops4j.pax.web/tree/master/pax-web-features/xbean-fragment