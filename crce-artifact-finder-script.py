# This script will walk directory tree of crce project and
# prints artifact ids of all poms it will find in following format:
# filepath: parentArtifactId -> currentArtifactId for artifacts with parent
# filepath: currentArtifactId for artifacts without parent
# Implemented method is compatible with Python 2.2 - 3.4

import fnmatch
import os
import xml.etree.ElementTree as ET

# format for printing artifacts with parent
# filepath: parentArtifactId -> currentArtifactId
artifactWithParentFormat = '%s: %s -> %s'

# format for printing artifacts without parent
# filepath: currentArtifactId
artifactFormat = '%s: %s'

# base path to crce
pathToCrce = 'c:\\users\\valesz\\documents\\crce'

# pom namespace
pomNs = '{http://maven.apache.org/POM/4.0.0}'

# this array will contain only folders where the pom is stored
# e.g. c:\tmp instead of c:\tmp\pom.xml
matches = []
for root, dirnames, filenames in os.walk(pathToCrce):
    for filename in fnmatch.filter(filenames, '*pom.xml'):
		matches.append(root)
		
# parse poms and print results		
for pathToPom in matches:
	tree = ET.parse(pathToPom+'//pom.xml')
	root = tree.getroot()
	aIdElem = root.find(pomNs+'artifactId')
	currentArtifactId = aIdElem.text
	
	# check if the current pom has parent defined
	parentElem = root.find(pomNs+'parent')
	if parentElem is None:
		print artifactFormat % (pathToPom, currentArtifactId)
	else:
		parentAId = parentElem.find(pomNs+'artifactId').text
		print artifactWithParentFormat % (pathToPom, parentAId, currentArtifactId)