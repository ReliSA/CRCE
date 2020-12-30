#!/bin/bash

# This script will go through bundles in ${bundleDir} and removes capabilities
# which cause bundles to require old runtime environment (osgi.ee capability).
# If you can't see ${bundleDir}, run mvn pax:directory first

# lines to be removed from manifests
requireCapString="Require-Capability: osgi.ee;"
reqEnvString="Bundle-RequiredExecutionEnvironment: J2SE-"

# log file, just for checking / debugging
logFile="out.log"

# directory with bundles to be processed
bundleDir="target/pax-runner-dir/bundles"

# path to manifest relative to the jar root
manifestPath="META-INF/MANIFEST.MF"

tmpFolder="bundle-tmp"

# This function processes one bundle.
function process_bundle {
    # unzip jar to tmp folder
    unzip -q $1 -d $tmpFolder
    absManifestPath="$tmpFolder/${manifestPath}"

    # remove capabilities/requirements from MANIFEST.MF if needed and pack it back to jar
    if grep -q "$requireCapString" "$absManifestPath"; then
        sed -i "/${requireCapString}/d" "$absManifestPath"
        echo "$requireCapString: $1" >> ${logFile}
        zip -u $1 "${absManifestPath}"
    fi
    if grep -q "$reqEnvString" "$absManifestPath"; then
        sed -i "/${reqEnvString}/d" "$absManifestPath"
        echo "$reqEnvString: $1" >> ${logFile}
        zip -u $1 "${absManifestPath}"
    fi

    # remove temp folder
    rm -rf $tmpFolder
    mkdir $tmpFolder
}


# Main function
function main {
    cd "${bundleDir}"
    
    mkdir $tmpFolder

    for bundle in *.jar; do
        process_bundle ${bundle}
    done

    rm ${logFile}
}

#
# Script body
#
main

